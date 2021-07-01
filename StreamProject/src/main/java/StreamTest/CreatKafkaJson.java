package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;


import java.util.HashMap;
import java.util.Map;

import StreamDataSource.*;
import org.apache.flink.util.Collector;

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
        see.setParallelism(3);
        //rdataStream.print();
        rdataStream = rdataStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                for(Integer i = 0; i < 10; i++){
                    JSONObject res = new JSONObject();
                    //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}
                    res.put("deviceID", "device_"+getRandStr(10));
                    res.put("timeStamp", System.currentTimeMillis());
                    Map<String, Map<String, String>> workStatusMap = new HashMap<String, Map<String, String>>();
                    Integer gkNum = getRandInt(10);
                    for(Integer j = 0; j < gkNum; j++){
                        Map<String, String> gkMap = new HashMap<String, String>();
                        gkMap.put("0", getRandStr(107));
                        workStatusMap.put("test_gk_"+getRandStr(10), gkMap);
                    }
                    res.put("workStatusMap", workStatusMap);
                    collector.collect(res.toString());
                }
            }
        });
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.10.178:9092", "Flink1", new SimpleStringSchema()));

        see.execute();
    }
}
