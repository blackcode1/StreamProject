package StreamTest.DataAccess;

import StreamDataSource.KafkaSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;


public class ReadKafkaJsonOld {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);
        FlinkKafkaConsumer<String> kafkaConsumer = KafkaSource.init("qz07", "EngineHeart");//EngineHeart
        //kafkaConsumer.setStartFromLatest();
        DataStream<String> kafkaSource = see.addSource(kafkaConsumer).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "EngineHeart:"+s;
            }
        });
        kafkaSource.print();



        FlinkKafkaConsumer<String> kafkaConsumer2 = KafkaSource.init("qz0721", "streamInput2");//streamInput2
        kafkaConsumer2.setStartFromLatest();
        DataStream<String> kafkaSource2 = see.addSource(kafkaConsumer2).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "streamInput2:"+s;
            }
        });
        kafkaSource2.print();


        FlinkKafkaConsumer<String> kafkaConsumer3 = KafkaSource.init("qz0721", "streamOutput1");//FailureInfo_SF
        //kafkaConsumer3.setStartFromLatest();
        DataStream<String> kafkaSource3 = see.addSource(kafkaConsumer3).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "streamOutput1:"+s;
            }
        });
        kafkaSource3.print();

        FlinkKafkaConsumer<String> kafkaConsumer4 = KafkaSource.init("qz070", "ZDData");//FailureInfo_SF
        //kafkaConsumer2.setStartFromEarliest();
        DataStream<String> kafkaSource4 = see.addSource(kafkaConsumer4).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "ZDData:"+s;
            }
        });
        kafkaSource4.print();

        FlinkKafkaConsumer<String> kafkaConsumer5 = KafkaSource.init("qz070", "decodeStore");//FailureInfo_SF
        //kafkaConsumer2.setStartFromEarliest();
        DataStream<String> kafkaSource5 = see.addSource(kafkaConsumer5).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "decodeStore:"+s;
            }
        });
        kafkaSource5.print();
        see.execute();

    }
}
