package StreamTest;

import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketSerializationSchema;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import ty.pub.TransPacket;

import java.util.*;

import static StreamInput.GetBroadStream.GetTaskInfo.sendGet;

/*

1001150509 0
1001149042 1
1001149043 0 1
1001149044 0 -1 1
1001165261 0 -1*3 1
1001165262 0 -1*7 1
1001165759 随机
1001148921
1001165668
1001165670
 */

public class SceneLSNSource {

    public static TransPacket createOnePacket(String deviceID, String s){
        TransPacket res = new TransPacket();
        //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}

        res.setTimestamp(System.currentTimeMillis());
        Map<String, Map<Long, String>> workStatusMap = new HashMap<String, Map<Long, String>>();

        res.setDeviceId(deviceID);
        Map<Long, String> gkMap = new HashMap<Long, String>();
        gkMap.put(0L, s);
        workStatusMap.put("engineState", gkMap);
        res.setWorkStatusMap(workStatusMap);
        return res;
    }

    public static void setOneData(Map<String, List<Integer>> res, String ID, Integer stop, Integer start){
        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < 20; i++){
            list.add(-1);
        }
        if(stop != -1){
            list.set(stop, 0);
        }
        if(start != -1){
            list.set(start, 1);
        }
        if(stop == -1 && start == -1){
            for(int i = 0; i < 20; i++) {
                Integer r = ((int) (10 * Math.random())) % 2;
                list.set(i, r);
            }
        }
        res.put(ID, list);
    }

    public static Map<String, List<Integer>> creatData(){
        Map<String, List<Integer>> res = new HashMap<String, List<Integer>>();
        setOneData(res, "1001150509",1, -1);
        setOneData(res, "1001149042",-1, 1);
        setOneData(res, "1001149043",1, 2);
        setOneData(res, "1001149044",1, 3);
        setOneData(res, "1001165261",1, 5);
        setOneData(res, "1001165262",1, 9);
        setOneData(res, "1001165759",-1, -1);
        setOneData(res, "1001148921",-1, -1);
        setOneData(res, "1001165668",-1, -1);
        setOneData(res, "1001165670",-1, -1);
        return res;
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);
        DataStream<String> countStream = see.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                int count = 0;
                long total = 0;
                long spaceTime = 29000L;

                while (count < 20){
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
        });
        DataStream<TransPacket> rdataStream = countStream.flatMap(new FlatMapFunction<String, TransPacket>() {
            @Override
            public void flatMap(String s, Collector<TransPacket> collector) throws Exception {
                Map<String, List<Integer>> res = creatData();
                Integer count = Integer.valueOf(s);
                for(Map.Entry<String, List<Integer>> entry: res.entrySet()){
                    List<Integer> list = entry.getValue();
                    if(list.get(count) != -1){
                        collector.collect(createOnePacket(entry.getKey(), list.get(count).toString()));
                    }
                }
            }
        });
        DataStream<String> showStream = rdataStream.flatMap(new FlatMapFunction<TransPacket, String>() {
            @Override
            public void flatMap(TransPacket transPacket, Collector<String> collector) throws Exception {
                collector.collect(transPacket.getDeviceId()+transPacket.getWorkStatusMap());
            }
        });
        showStream.print();
        showStream.writeAsText("C:\\Users\\12905\\Desktop\\lsn_source.txt", FileSystem.WriteMode.OVERWRITE);
        rdataStream.addSink(new FlinkKafkaProducer<TransPacket>
                ("192.168.10.178:9092", "Flink7", new TransPacketSerializationSchema()));

        DataStream<String> stateStream = countStream.flatMap(new FlatMapFunction<String, String>() {

            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                Thread.sleep(1000L);
                String taskInfoStr = sendGet("http://192.168.10.176:8080/states?id=testcaseuuid14");
                collector.collect(taskInfoStr);
            }
        });
        stateStream.print();
        stateStream.writeAsText("C:\\Users\\12905\\Desktop\\lsn_state.txt", FileSystem.WriteMode.OVERWRITE);

        see.execute();
    }
}
