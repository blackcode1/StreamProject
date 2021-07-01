package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public class SpeedTestTarget {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);
        DataStream<String> countStream = see.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                Properties props = new Properties();
                props.put("bootstrap.servers", "192.168.10.178:9092");
                props.put("group.id", "tsx");
                props.put("enable.auto.commit", "false");
                props.put("auto.offset.reset", "earliest");
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Collections.singletonList("SpeedTarget"));

                try {
                    while (true) {//消费者是一个长期运行的程序，通过持续轮询向Kafka请求数据。在其他线程中调用consumer.wakeup()可以退出循环
                        //在100ms内等待Kafka的broker返回数据.超市参数指定poll在多久之后可以返回，不管有没有可用的数据都要返回
                        ConsumerRecords<String, String> records = consumer.poll(100);
                        for (ConsumerRecord<String, String> record : records) {
                            JSONObject jsonObject = JSONObject.parseObject(record.value());
                            Map<String, Map<String, String>> workStatusMap =
                                    (Map<String, Map<String, String>>) jsonObject.get("workStatusMap");
                            sourceContext.collect(record.timestamp()+" "+jsonObject.getString("timeStamp")+" "+
                                    workStatusMap.get("result").get("currentKM")+" "+workStatusMap.get("result").get("deviceID"));
                        }
                    }
                } finally {
                    //退出应用程序前使用close方法关闭消费者，网络连接和socket也会随之关闭，并立即触发一次再均衡
                    consumer.close();
                }
            }

            @Override
            public void cancel() {

            }
        });
        countStream.print();
        countStream.writeAsText("C:\\Users\\12905\\Desktop\\speed_result.txt", FileSystem.WriteMode.OVERWRITE);
        see.execute();
    }
}
