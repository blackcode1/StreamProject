package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Date;
import java.util.Properties;

public class KafakTimeTest2 {

    public static void produceData() throws Exception{
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> rdataStream = see.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                sourceContext.collect("0");
            }

            @Override
            public void cancel() {

            }
        });

        DataStream<String> resStream = rdataStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("time", new Date().getTime());
                collector.collect(jsonObject.toString());
            }
        });

        //rdataStream.print();
        resStream.addSink(new FlinkKafkaProducer<String>("192.168.10.178:9092", "TimeTest", new SimpleStringSchema()));

        see.execute();
    }

    public static void getData(){
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "192.168.71.18:12092");
        props.put("group.id", "tsx");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("EngineHearts"));
        System.out.println(consumer.partitionsFor("EngineHearts"));
        try {
            while (true) {//???????????????????????????????????????????????????????????????Kafka???????????????????????????????????????consumer.wakeup()??????????????????
                //???100ms?????????Kafka???broker????????????.??????????????????poll????????????????????????????????????????????????????????????????????????
                ConsumerRecords<String, String> records = consumer.poll(100);

                for (ConsumerRecord<String, String> record : records) {

                    System.out.println(record.value());
                }
            }
        } finally {
            //???????????????????????????close???????????????????????????????????????socket???????????????????????????????????????????????????
            consumer.close();
        }
    }


    public static void getData2() throws Exception{
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", "192.168.10.178:9092");
        kafkaConfig.setProperty("group.id", "qz0703");
        kafkaConfig.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer<String> kafkaConsumer = new
                FlinkKafkaConsumer<String>("TimeTest", new SimpleStringSchema(), kafkaConfig);

        kafkaConsumer.setStartFromEarliest();
        DataStream<String> kafkaSource = see.addSource(kafkaConsumer);

        kafkaSource.print();


        see.execute();
    }

    public static void main(String[] args) throws Exception {
        getData();
    }
}
