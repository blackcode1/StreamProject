package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SpeedTestSource {

    public static int getRandInt(Integer max){
        return (int) (Math.random()*max);
    }

    public static String getRandStr(Integer max){
        return String.valueOf(getRandInt(max));
    }

    public static JSONObject createOnePacket(String deviceID, Integer currentKM){
        JSONObject res = new JSONObject();
        //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}
        res.put("deviceID", deviceID);
        res.put("timeStamp", System.currentTimeMillis());
        Map<String, Map<String, String>> workStatusMap = new HashMap<String, Map<String, String>>();
        for(Integer j = 0; j < 5; j++){
            Map<String, String> gkMap = new HashMap<String, String>();
            gkMap.put("0", String.valueOf(currentKM));
            workStatusMap.put(String.valueOf(j), gkMap);
        }
        res.put("workStatusMap", workStatusMap);
        return res;
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> rdataStream = see.readTextFile("C:\\Users\\12905\\Desktop\\Flink\\mtntest.csv");
        see.setParallelism(3);

        rdataStream = rdataStream.flatMap(new RichFlatMapFunction<String, String>() {

            private  transient  Integer count;
            private transient Set<String> deviceIDs;

            public void open(Configuration parameters) throws Exception {
                count = 0;
                deviceIDs = new HashSet<String>();
            }
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {

                String[] deviceInfo = s.split(",");
                String deviceID = deviceInfo[0];
                if(!deviceIDs.contains(deviceID)){
                    deviceIDs.add(deviceID);
                    count = count + 1;
                    for(int i = 0; i < 10000; i++){
                        JSONObject jsonObject = createOnePacket(deviceID, i);
                        collector.collect(jsonObject.toString());
                    }
                    System.out.println(count);
                }
            }
        }).setParallelism(1);

        //rdataStream.addSink(new FlinkKafkaProducer<String>
          //      ("192.168.10.178:9092", "SpeedSource", new SimpleStringSchema()));
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.10.178:9092", "LatencySource", new SimpleStringSchema()));
        see.execute();
    }
}
