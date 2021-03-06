package StreamTest.FailurediagTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.Map;
import java.util.Properties;

public class ReadKafkaJson2 {

    public static FlinkKafkaConsumer<String> init(String gropID, String topicName) {
        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", "192.168.3.32:9092");
        kafkaConfig.setProperty("group.id", gropID);
        kafkaConfig.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer<String> kafkaConsumer = new
                FlinkKafkaConsumer<String>(topicName, new SimpleStringSchema(), kafkaConfig);
        //kafkaConsumer.setStartFromEarliest();
        return kafkaConsumer;
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        see.setParallelism(1);
        FlinkKafkaConsumer<String> kafkaConsumer = init("qz07", "EngineHeart");//EngineHeart
        kafkaConsumer.setStartFromLatest();
        //kafkaConsumer.setStartFromTimestamp(System.currentTimeMillis() - 3*3600*1000);
        DataStream<String> kafkaSource = see.addSource(kafkaConsumer).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "EngineHeart:"+s;
            }
        });
        kafkaSource.print();


        FlinkKafkaConsumer<String> kafkaConsumer2 = init("qz070", "decodeInputAll");//FailureDiag_SF???FailureInfo_SF decodeStore
        //kafkaConsumer2.setStartFromEarliest();//??????????????????7?????????????????? streamOutput1 QYData teststore decodeInputAll
        kafkaConsumer2.setStartFromLatest();//????????????????????????
        kafkaConsumer2.setStartFromTimestamp(System.currentTimeMillis() - 1*3600*1000);
        DataStream<String> kafkaSource2 = see.addSource(kafkaConsumer2).flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                JSONObject jsonObject = JSONObject.parseObject(s);
                if(jsonObject.getString("outputtopic").equals("GHData")){
                    collector.collect(s);
                }
            }

        });
        kafkaSource2.print();

        FlinkKafkaConsumer<String> kafkaConsumer3 = init("qz011", "GHData");//FailureDiag_SF???FailureInfo_SF decodeStore
        //kafkaConsumer3.setStartFromEarliest();//??????????????????7?????????????????? streamOutput1 QYData testdecode streamInput2
        kafkaConsumer3.setStartFromLatest();//????????????????????????
        kafkaConsumer3.setStartFromTimestamp(System.currentTimeMillis() - 1*3600*1000);
        DataStream<String> kafkaSource3 = see.addSource(kafkaConsumer3).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "QYData:"+s;
            }
        });
        kafkaSource3.print();

        see.execute();

    }
}
