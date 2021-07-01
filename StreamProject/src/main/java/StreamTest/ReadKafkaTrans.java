package StreamTest;

import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketDecoder;
import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketDeserializationSchema;
import com.alibaba.fastjson.JSONObject;
import edu.thss.entity.TransPacket;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class ReadKafkaTrans {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", "192.168.30.22:9092");
        kafkaConfig.setProperty("group.id", "test_319");
        kafkaConfig.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.setProperty("value.deserializer", TransPacketDecoder.class.getName());

        FlinkKafkaConsumer<TransPacket> kafkaConsumer = new
                FlinkKafkaConsumer<TransPacket>("TYP_KTP_CTY_Decode", new TransPacketDeserializationSchema(), kafkaConfig);
        kafkaConsumer.setStartFromEarliest();
        DataStream<TransPacket> kafkaSource = see.addSource(kafkaConsumer);
        DataStream<String> dataStream = kafkaSource.map(new MapFunction<TransPacket, String>() {
            @Override
            public String map(TransPacket transPacket) throws Exception {
                String s = transPacket.getDeviceId();
                if(s.equals("1001179196")){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("DeviceId", s);
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.putAll(transPacket.getBaseInfoMap());
                    jsonObject.put("BaseInfoMap", jsonObject1);
                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.putAll(transPacket.getWorkStatusMap());
                    jsonObject.put("WorkStatusMap", jsonObject2);
                    System.out.println(jsonObject);
                }

                return null;
            }
        });


        /*String pid = "test";
        DataStream<DataType> inputStream = kafkaSource.map(new MapTP2DataType(pid));

        KeyedStream<DataType, String> keyedStream = inputStream.map(new MapFunction<DataType, DataType>() {
            @Override
            public DataType map(DataType dataType) throws Exception {
                if(StringUtils.isNumeric(dataType.dataID)){
                    return dataType;
                }
                else {
                    String logStr = StreamLog.createLocalLog(null, "WARN",
                            "设备ID异常, 设备ID为"+dataType.dataID+"，数据包为"+dataType, pid);
                    return (DataType) new LogList(logStr);
                }
            }
        }).keyBy(new KeySelector<DataType, String>() {
            @Override
            public String getKey(DataType dataType) throws Exception {
                Integer idKey = Integer.valueOf(dataType.dataID) % 100;
                return idKey.toString();
            }
        });

        keyedStream.print();*/
        see.execute();

    }
}
