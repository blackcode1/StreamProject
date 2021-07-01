package StreamTest.FailurediagTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Properties;

public class ReadKafkaJson3 {

    public static FlinkKafkaConsumer<String> init(String gropID, String topicName) {
        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", "192.168.71.19:2093,192.168.71.19:3093，192.168.71.19:4093，192.168.71.19:5093");
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
        FlinkKafkaConsumer<String> kafkaConsumer = init("qz0701", "EngineHeart");//EngineHeart
        kafkaConsumer.setStartFromLatest();
        //kafkaConsumer.setStartFromTimestamp(System.currentTimeMillis() - 3*3600*1000);
        DataStream<String> kafkaSource = see.addSource(kafkaConsumer).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "EngineHeart:"+s;
            }
        });
        kafkaSource.print();


        FlinkKafkaConsumer<String> kafkaConsumer2 = init("qz070", "decodeStore");//FailureDiag_SF，FailureInfo_SF decodeStore
        //kafkaConsumer2.setStartFromEarliest();//从最早数据（7天前）开始读 streamOutput1 QYData teststore decodeInputAll
        kafkaConsumer2.setStartFromLatest();//从最新数据开始读

        kafkaConsumer2.setStartFromTimestamp(1610604552000L);
        DataStream<String> kafkaSource2 = see.addSource(kafkaConsumer2).flatMap(new FlatMapFunction<String, String>() {
            int count = 0;
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {

                JSONObject jsonObject = JSONObject.parseObject(s);
                Long t = jsonObject.getLong("timestamp");
                if(t >= 1610604600 * 1000000L && t <= 1610605200 * 1000000L){
                    count = count + 1;
                    collector.collect(jsonObject.getString("timestamp") + "-" + count);
                }
            }
        });
        kafkaSource2.print();

       /* FlinkKafkaConsumer<String> kafkaConsumer3 = init("qz011", "QYData");//FailureDiag_SF，FailureInfo_SF decodeStore
        //kafkaConsumer3.setStartFromEarliest();//从最早数据（7天前）开始读 streamOutput1 QYData testdecode streamInput2
        kafkaConsumer3.setStartFromLatest();//从最新数据开始读
        //kafkaConsumer3.setStartFromTimestamp(System.currentTimeMillis() - 1*3600*1000);
        DataStream<String> kafkaSource3 = see.addSource(kafkaConsumer3).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "QYData:"+s;
            }
        });
        kafkaSource3.print();*/

        /*FlinkKafkaConsumer<String> kafkaConsumer4 = init("qz1229", "decodeInputAll");//FailureDiag_SF，FailureInfo_SF decodeStore
        //kafkaConsumer3.setStartFromEarliest();//从最早数据（7天前）开始读 streamOutput1 QYData testdecode streamInput2
        kafkaConsumer4.setStartFromLatest();//从最新数据开始读
        //kafkaConsumer4.setStartFromTimestamp(System.currentTimeMillis() - 12*3600*1000);
        DataStream<String> kafkaSource4 = see.addSource(kafkaConsumer4).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "decodeInputAll:"+s;
            }
        });
        kafkaSource4.print();*/

        see.execute();

    }
}
