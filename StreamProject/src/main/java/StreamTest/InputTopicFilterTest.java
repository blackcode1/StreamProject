package StreamTest;

import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketSerializationSchema;
import StreamDataSource.OnTimeTestSource;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputTopicFilterTest {

    public static int getRandInt(Integer max){
        return (int) (Math.random()*max);
    }

    public static String getRandStr(Integer max){
        return String.valueOf(getRandInt(max));
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        List<String> list = new ArrayList<>();
        list.add("1001265107,test1");
        DataStream<String> rdataStream = see.fromCollection(list);
        see.setParallelism(1);

        DataStream<String> rStream = rdataStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {

                String[] deviceInfo = s.split(",");
                String deviceID = deviceInfo[0];
                JSONObject res = new JSONObject();
                //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}
                res.put("deviceID", deviceID);
                res.put("timeStamp", System.currentTimeMillis());
                Map<String, Map<String, String>> workStatusMap = new HashMap<String, Map<String, String>>();
                Map<String, String> gkMap = new HashMap<String, String>();
                gkMap.put("0", deviceInfo[1]);
                gkMap.put("1", "FlinkTest1");
                workStatusMap.put("gk1", gkMap);
                res.put("workStatusMap", workStatusMap);
                collector.collect(res.toString());
            }
        }).setParallelism(1);

        rStream.print();
        rStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.3.32:9092", "test2", new SimpleStringSchema()));

        DataStream<String> rStream2 = rdataStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {

                String[] deviceInfo = s.split(",");
                String deviceID = deviceInfo[0];
                JSONObject res = new JSONObject();
                //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}
                res.put("deviceID", deviceID);
                res.put("timeStamp", System.currentTimeMillis());
                Map<String, Map<String, String>> workStatusMap = new HashMap<String, Map<String, String>>();
                Map<String, String> gkMap = new HashMap<String, String>();
                gkMap.put("0", deviceInfo[1]);
                gkMap.put("1", "Flink1");
                workStatusMap.put("gk2", gkMap);
                res.put("workStatusMap", workStatusMap);
                collector.collect(res.toString());
            }
        }).setParallelism(1);

        rStream2.print();
       // rStream2.addSink(new FlinkKafkaProducer<String>
        //        ("192.168.10.178:9092", "Flink1", new SimpleStringSchema()));

        see.execute();
    }
}
