package StreamTest;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.runtime.checkpoint.savepoint.Savepoint;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamContextEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.transformations.SinkTransformation;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;


import java.util.Properties;

public class SinkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(3);
        DataStream<String> s1 = env.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                int count = 0;
                long total = 0;
                long spaceTime = 1000L;
                while (true){
                    sourceContext.collect(String.valueOf(count));
                    count = count + 1;
                    long t0 = System.currentTimeMillis();
                    Thread.sleep(count * spaceTime - total);
                    total += System.currentTimeMillis() - t0;
                }
            }

            @Override
            public void cancel() {

            }
        }).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "N2-"+s;
            }
        });
        s1.print();


        //s1.addSink(new SinkTestFunction());
        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", "172.16.245.12:9092");
        kafkaConfig.setProperty("group.id", "qz0722");
        kafkaConfig.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<String>("FlinkTestStr", new SimpleStringSchema(), kafkaConfig);
        kafkaConsumer.setStartFromEarliest();
        DataStream<String> read = env.addSource(kafkaConsumer);

        read.print();


        env.execute("y");

    }


}
