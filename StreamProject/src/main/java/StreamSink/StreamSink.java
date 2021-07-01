package StreamSink;

import StreamDataPacket.BaseClassDataType.StreamDataset;
import StreamDataPacket.BaseClassDataType.TransPacketRely.ParsedDataPacketSerializationSchema;
import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketSerializationSchema;
import StreamDataPacket.DataType;
import StreamDataPacket.DataTypeChange.MapDatatype2JS;
import StreamDataPacket.DataTypeChange.MapDatatype2PDP;
import StreamDataPacket.DataTypeChange.MapDatatype2TP;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import StreamDataPacket.SubClassDataType.JsonList;
import StreamTest.MyRMQSinkOpts;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import edu.thss.entity.ParsedDataPacket;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import ty.pub.TransPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class StreamSink {
    public static void simpleSink(DataStream<String> resultStream, StreamDataset dataset)throws Exception{
        String brokerList = dataset.dataSourceIp + ":" + dataset.dataSourcePort;
        String topicId = dataset.dataSetTopic;

        Properties kafkaConfig = new Properties();
        kafkaConfig.put("bootstrap.servers", brokerList);
        kafkaConfig.put("max.block.ms", 5000);

        FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<String>(topicId, new SimpleStringSchema(), kafkaConfig);
        resultStream.addSink(producer);
    }


    public static void resultSink(DataStream<DataType> resultStream, StreamDataset streamDataset){

        if(streamDataset.dataSourceType.equals("KAFKA")){
            String brokerList = streamDataset.dataSourceIp + ":" + streamDataset.dataSourcePort;
            String topicId = streamDataset.dataSetTopic;
            Properties kafkaConfig = new Properties();
            kafkaConfig.put("bootstrap.servers", brokerList);
            kafkaConfig.put("max.block.ms", 5000);
            resultStream.addSink(new FlinkKafkaProducer<DataType>(topicId, new KafkaOverrideSerialization(), kafkaConfig));
        }
        else if(streamDataset.dataSourceType.equals("RMQ")){
            RMQConnectionConfig rmqConfig = new RMQConnectionConfig.Builder()
                    .setHost(streamDataset.dataSourceIp)
                    .setPort(Integer.valueOf(streamDataset.dataSourcePort))
                    .setUserName(streamDataset.dataSourceUser)
                    .setPassword(streamDataset.dataSourcePassword)
                    .setVirtualHost("/")
                    .build();
            String queueName = streamDataset.dataSetTopic;
            String exchange = streamDataset.rmqExchange;
            Integer delay = streamDataset.rmqDelay;

            if(streamDataset.dataType.equals("JSONObject")){
                MyRMQSinkOpts<String> opts = new MyRMQSinkOpts<>(queueName, "", streamDataset.dataType, null);
                if(exchange != null && delay == null){
                   opts = new MyRMQSinkOpts<>(queueName, exchange, streamDataset.dataType, MessageProperties.TEXT_PLAIN);
                }
                else if(exchange != null && delay != null){
                    Map<String, Object> delayHeaders = new HashMap<String, Object>();
                    delayHeaders.put("x-delay", delay);
                    AMQP.BasicProperties.Builder delayProps = new AMQP.BasicProperties.Builder().headers(delayHeaders);
                    opts = new MyRMQSinkOpts<>(queueName, exchange, streamDataset.dataType, delayProps.build());
                }
                RichRMQSink<String> richRMQSink = new RichRMQSink<String>(rmqConfig, new SimpleStringSchema(), opts);
                resultStream
                        .flatMap(new MapDatatype2JS())
                        .addSink(richRMQSink)
                        .setParallelism(1);
            }
            else if(streamDataset.dataType.equals("TransPacket")){
                MyRMQSinkOpts<TransPacket> opts = new MyRMQSinkOpts<>(queueName, "", streamDataset.dataType, null);
                if(exchange != null && delay == null){
                    opts = new MyRMQSinkOpts<>(queueName, exchange, streamDataset.dataType, MessageProperties.TEXT_PLAIN);
                }
                else if(exchange != null && delay != null){
                    Map<String, Object> delayHeaders = new HashMap<String, Object>();
                    delayHeaders.put("x-delay", delay);
                    AMQP.BasicProperties.Builder delayProps = new AMQP.BasicProperties.Builder().headers(delayHeaders);
                    opts = new MyRMQSinkOpts<>(queueName, exchange, streamDataset.dataType, delayProps.build());

                }
                RichRMQSink<TransPacket> richRMQSink = new RichRMQSink<TransPacket>(rmqConfig, new TransPacketSerializationSchema(), opts);
                resultStream
                        .flatMap(new MapDatatype2TP())
                        .addSink(richRMQSink)
                        .setParallelism(1);
            }
            else if(streamDataset.dataType.equals("ParsedDataPacket")){
                MyRMQSinkOpts<ParsedDataPacket> opts = new MyRMQSinkOpts<>(queueName, "", streamDataset.dataType, null);
                if(exchange != null && delay == null){
                    opts = new MyRMQSinkOpts<>(queueName, exchange, streamDataset.dataType, MessageProperties.TEXT_PLAIN);
                }
                else if(exchange != null && delay != null){
                    Map<String, Object> delayHeaders = new HashMap<String, Object>();
                    delayHeaders.put("x-delay", delay);
                    AMQP.BasicProperties.Builder delayProps = new AMQP.BasicProperties.Builder().headers(delayHeaders);
                    opts = new MyRMQSinkOpts<>(queueName, exchange, streamDataset.dataType, delayProps.build());

                }
                RichRMQSink<ParsedDataPacket> richRMQSink = new RichRMQSink<ParsedDataPacket>(rmqConfig, new ParsedDataPacketSerializationSchema(), opts);
                resultStream
                        .flatMap(new MapDatatype2PDP())
                        .addSink(richRMQSink)
                        .setParallelism(1);
            }
        }
        else if(streamDataset.dataSourceType.equals("IOTDB")){
            resultStream.keyBy(new KeySelector<DataType, String>() {
                @Override
                public String getKey(DataType dataType) throws Exception {
                    DBStore dbStore = (DBStore) dataType;

                    return dbStore.getTableName();
                }
            }).addSink(new IotDBBatchSink(streamDataset.dataSourceIp, streamDataset.dataSourceUser, streamDataset.dataSourcePassword));
        }
        else if(streamDataset.dataSourceType.equals("HBase")){
            resultStream.addSink(new HBaseSink(streamDataset.dataSourceIp, streamDataset.dataSourceUser, streamDataset.dataSourcePassword));
        }
        else {
            resultStream.addSink(new RelationDBSink(streamDataset.dataSourceIp, streamDataset.dataBase,
                    streamDataset.dataSourceUser, streamDataset.dataSourcePassword, streamDataset.dataSourceType));
        }
    }
}
