package StreamDataSource;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class QyKafkaSource {
    public static FlinkKafkaConsumer<String> init(String gropID, String topicName) {
        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", "172.16.244.51:9092,172.16.244.54:9092,172.16.244.52:9092");
        kafkaConfig.setProperty("group.id", gropID);
        kafkaConfig.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer<String> kafkaConsumer = new
                FlinkKafkaConsumer<String>(topicName, new SimpleStringSchema(), kafkaConfig);
        kafkaConsumer.setStartFromEarliest();
        return kafkaConsumer;
    }
}
