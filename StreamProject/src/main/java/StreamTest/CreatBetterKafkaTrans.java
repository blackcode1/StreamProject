package StreamTest;

import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketSerializationSchema;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import ty.pub.TransPacket;

import java.util.HashMap;
import java.util.Map;

public class CreatBetterKafkaTrans {

    public static int getRandInt(Integer max){
        return (int) (Math.random()*max);
    }

    public static String getRandStr(Integer max){
        return String.valueOf(getRandInt(max));
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> rdataStream = see.readTextFile("C:\\Users\\12905\\Desktop\\Flink通用0708-0714\\mtntest.csv");
        see.setParallelism(3);

        DataStream<TransPacket> resStream = rdataStream.flatMap(new FlatMapFunction<String, TransPacket>() {
            @Override
            public void flatMap(String s, Collector<TransPacket> collector) throws Exception {

                String[] deviceInfo = s.split(",");
                String deviceID = deviceInfo[0];
                Integer spaceKM = Integer.valueOf(deviceInfo[6]);
                Integer floatKM = Integer.valueOf(deviceInfo[7]);
                //s-f-10;s-10;s+10;s+f+10
                for(Integer i = 0; i < 4; i++){
                    TransPacket res = new TransPacket();
                    //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}
                    res.setDeviceId(deviceID);
                    res.setTimestamp(System.currentTimeMillis());
                    //res.put("timeStamp", System.currentTimeMillis());
                    Map<String, Map<Long, String>> workStatusMap = new HashMap<String, Map<Long, String>>();
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
                        Map<Long, String> gkMap = new HashMap<Long, String>();
                        gkMap.put(0L, String.valueOf(currentKM));
                        workStatusMap.put(String.valueOf(j), gkMap);
                    }
                    System.out.println(workStatusMap);
                    res.setWorkStatusMap(workStatusMap);
                    collector.collect(res);
                }
            }
        });

        //rdataStream.print();
        resStream.addSink(new FlinkKafkaProducer<TransPacket>
                ("172.16.245.12:9092", "FlinkTestTransSource2", new TransPacketSerializationSchema()));

        see.execute();
    }
}
