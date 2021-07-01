package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

public class CreatBetterKafkaJson {

    public static int getRandInt(Integer max){
        return (int) (Math.random()*max);
    }

    public static String getRandStr(Integer max){
        return String.valueOf(getRandInt(max));
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> rdataStream = see.readTextFile("C:\\Users\\12905\\Desktop\\Flink\\mtntest.csv");
        see.setParallelism(1);

        rdataStream = rdataStream.flatMap(new RichFlatMapFunction<String, String>() {

            private  transient  Integer kkk;

            public void open(Configuration parameters) throws Exception {
                kkk = 0;
                //this.producer = new FlinkKafkaInternalProducer(this.producerConfig);
            }
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {

                String[] deviceInfo = s.split(",");
                String deviceID = deviceInfo[0];
                Integer spaceKM = Integer.valueOf(deviceInfo[6]);
                Integer floatKM = Integer.valueOf(deviceInfo[7]);
                //s-f-10;s-10;s+10;s+f+10
                for(Integer i = 0; i < 4; i++){
                    JSONObject res = new JSONObject();
                    //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}
                    res.put("deviceID", deviceID);
                    res.put("timeStamp", System.currentTimeMillis());
                    Map<String, Map<String, String>> workStatusMap = new HashMap<String, Map<String, String>>();
                    Integer currentKM = spaceKM;
                    if(i == 0){
                        currentKM = spaceKM - floatKM - 10;
                    }
                    else if(i == 1){
                        currentKM = spaceKM - 10;
                    }
                    else if(i == 2){
                        currentKM = spaceKM + 10;
                    }
                    else if(i == 3){
                        currentKM = spaceKM + floatKM + 10;
                    }
                    for(Integer j = 0; j < 5; j++){
                        Map<String, String> gkMap = new HashMap<String, String>();
                        gkMap.put("0", String.valueOf(currentKM));
                        workStatusMap.put(String.valueOf(j), gkMap);
                    }
                    res.put("workStatusMap", workStatusMap);
                    if(deviceID.equals("1001146044") && kkk < 1){
                        kkk = kkk + 1;
                        collector.collect(res.toString());
                    }
                }
            }
        }).setParallelism(1);

        rdataStream.print();
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.10.178:9092", "FlinkTest1", new SimpleStringSchema()));

        see.execute();
    }
}
