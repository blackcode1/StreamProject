package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.*;

public class SpeedTestLatencyTarget {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);
        DataStream<List<String>> sourceStream = see.addSource(new SourceFunction<List<String>>() {
            @Override
            public void run(SourceContext<List<String>> sourceContext) throws Exception {
                //1571213119411
                Properties props = new Properties();
                props.put("bootstrap.servers", "192.168.10.178:9092");
                props.put("group.id", "tsx");
                props.put("enable.auto.commit", "false");
                props.put("auto.offset.reset", "earliest");
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Collections.singletonList("LatencySource"));
                try {
                    while (true) {//消费者是一个长期运行的程序，通过持续轮询向Kafka请求数据。在其他线程中调用consumer.wakeup()可以退出循环
                        //在100ms内等待Kafka的broker返回数据.超市参数指定poll在多久之后可以返回，不管有没有可用的数据都要返回
                        ConsumerRecords<String, String> records = consumer.poll(100);
                        for (ConsumerRecord<String, String> record : records) {
                            List<String> list = new ArrayList<String>();
                            JSONObject jsonObject = JSONObject.parseObject(record.value());
                            Map<String, Map<String, String>> workStatusMap =
                                    (Map<String, Map<String, String>>) jsonObject.get("workStatusMap");
                            String k1 = jsonObject.getString("deviceID");
                            String k2 =  workStatusMap.get("0").get("0");
                            String v =  String.valueOf(record.timestamp());
                            list.add(k1);
                            list.add(k2);
                            list.add(v);
                            sourceContext.collect(list);
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
        DataStream<List<String>> targetStream = see.addSource(new SourceFunction<List<String>>() {
            @Override
            public void run(SourceContext<List<String>> sourceContext) throws Exception {
                Properties props = new Properties();
                props.put("bootstrap.servers", "192.168.10.178:9092");
                props.put("group.id", "tsx");
                props.put("enable.auto.commit", "false");
                props.put("auto.offset.reset", "earliest");
                props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
                consumer.subscribe(Collections.singletonList("LatencyTarget"));
                try {
                    while (true) {//消费者是一个长期运行的程序，通过持续轮询向Kafka请求数据。在其他线程中调用consumer.wakeup()可以退出循环
                        //在100ms内等待Kafka的broker返回数据.超市参数指定poll在多久之后可以返回，不管有没有可用的数据都要返回
                        ConsumerRecords<String, String> records = consumer.poll(100);
                        for (ConsumerRecord<String, String> record : records) {
                            List<String> list = new ArrayList<String>();
                            JSONObject jsonObject = JSONObject.parseObject(record.value());
                            Map<String, Map<String, String>> workStatusMap =
                                    (Map<String, Map<String, String>>) jsonObject.get("workStatusMap");
                            String k1 = workStatusMap.get("result").get("deviceID");
                            String k2 =  workStatusMap.get("result").get("currentKM");
                            int km =  Double.valueOf(k2).intValue();
                            k2 = String.valueOf(km);
                            String v =  String.valueOf(record.timestamp());
                            list.add(k1);
                            list.add(k2);
                            list.add(v);
                            sourceContext.collect(list);
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

        DataStream<String> countStream = sourceStream.union(targetStream).flatMap(new RichFlatMapFunction<List<String>, String>() {

            Map<String, Map<String, Long>> t;

            public void open(Configuration parameters) throws Exception {
                t = new HashMap<String, Map<String, Long>>();
            }

            @Override
            public void flatMap(List<String> list, Collector<String> collector) throws Exception {
                String k1 = list.get(0);
                String k2 = list.get(1);
                Long v = Long.parseLong(list.get(2));
                if(!t.containsKey(k1)){
                    Map<String, Long> map = new HashMap<String, Long>();
                    map.put(k2, v);
                    t.put(k1, map);
                }
                else if(!t.get(k1).containsKey(k2)){
                    t.get(k1).put(k2, v);
                }
                else {
                    Long latency = Math.abs(t.get(k1).get(k2) - v);
                    collector.collect(k1+" "+k2+" "+latency.toString());
                }
            }
        });

        //sourceStream.print();
        //targetStream.print();
        countStream.print();
        countStream.writeAsText("C:\\Users\\12905\\Desktop\\latency_result.txt", FileSystem.WriteMode.OVERWRITE);
        see.execute();
    }
}
