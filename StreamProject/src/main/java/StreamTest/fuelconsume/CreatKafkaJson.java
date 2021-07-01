package StreamTest.fuelconsume;

import StreamDataSource.OnTimeTestSource;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

public class CreatKafkaJson {

    public static int getRandInt(Integer max){
        return (int) (Math.random()*max);
    }

    public static String getRandStr(Integer max){
        return String.valueOf(getRandInt(max));
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> rdataStream = see.addSource(new OnTimeTestSource(1L));
        see.setParallelism(1);
        //rdataStream.print();
        rdataStream = rdataStream.flatMap(new FlatMapFunction<String, String>() {
            int start = 0;
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                for(Integer i = 0; i < 1; i++){
                    JSONObject res = new JSONObject();
                    //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}
                    //res.put("deviceID", "device_"+getRandStr(10));
                    res.put("deviceID", "device_12");
                    res.put("timestamp", System.currentTimeMillis());
                    Map<String, Map<String, String>> workStatusMap = new HashMap<String, Map<String, String>>();
                    Integer gkNum = 1;
                    for(Integer j = 0; j < gkNum; j++){
                        Map<String, String> gkMap = new HashMap<String, String>();
                        if(start == 0){
                            start = 1;
                            gkMap.put("0", "-1");
                        }else {
                            gkMap.put("0", getRandStr(107));
                        }


                        workStatusMap.put("fuel", gkMap);
                    }
                    res.put("workStatusMap", workStatusMap);
                    collector.collect(res.toString());
                }
            }
        });
        rdataStream.print();
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.3.32:9092", "fuelstream", new SimpleStringSchema()));

        see.execute();
    }
}
