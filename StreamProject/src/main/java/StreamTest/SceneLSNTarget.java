package StreamTest;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimerService;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.util.Properties;

public class SceneLSNTarget {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);

        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", "192.168.10.178:9092");
        kafkaConfig.setProperty("group.id", "qz0703");
        kafkaConfig.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer<String> kafkaConsumer = new
                FlinkKafkaConsumer<String>("FlinkTarget7", new SimpleStringSchema(), kafkaConfig);

        //kafkaConsumer.setStartFromEarliest();
        DataStream<String> kafkaSource = see.addSource(kafkaConsumer);
        kafkaSource.print();
        kafkaSource.writeAsText("C:\\Users\\12905\\Desktop\\lsn_result.txt", FileSystem.WriteMode.OVERWRITE);
        see.execute();

    }
}
