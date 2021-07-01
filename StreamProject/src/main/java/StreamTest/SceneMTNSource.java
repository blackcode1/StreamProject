package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static StreamInput.GetBroadStream.GetTaskInfo.sendGet;

/*
VclId               KmID    LastKM  SpaceKM FloatKM 里程取值范围
1001150509	40	10	2	3	105528	50000	500     150000-210000
1001150509	41	10	2	3	105528	100000	1000
1001149042	40	10	2	3	0	50000	500       40000-110000
1001149042	41	10	2	3	0	100000	1000
1001149043	40	10	2	3	0	50000	500       40000-60000
1001149043	41	10	2	3	0	100000	1000
1001149044	40	10	2	3	0	50000	500       90000-110000
1001149044	41	10	2	3	0	100000	1000
1001165261	57	17	1	0	20554	20000	500     40000-50000
1001165262	57	17	1	0	19325	20000	500     30000-40000
1001165759	57	17	5	0	9971	20000	500     25000-35000
1001148921	31	9	5	0	181854	75000	500     90000-100000
1001165668	57	17	3	0	11094	20000	500     30000-40000
1001165670	57	17	3	0	12182	20000	500     30000-40000
 */

public class SceneMTNSource {

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

    public static void setOneKMData(Map<String, List<Integer>> res, String kmID, Integer s, Integer e){
        List<Integer> list = new ArrayList<Integer>();
        list.add(s);
        list.add(e);
        res.put(kmID, list);
    }

    public static Map<String, List<Integer>> creatKMData(){
        Map<String, List<Integer>> res = new HashMap<String, List<Integer>>();
        setOneKMData(res, "1001150509",150000, 210000);
        setOneKMData(res, "1001149042",40000, 110000);
        setOneKMData(res, "1001149043",40000, 60000);
        setOneKMData(res, "1001149044",90000, 110000);
        setOneKMData(res, "1001165261",40000, 50000);
        setOneKMData(res, "1001165262",30000, 40000);
        setOneKMData(res, "1001165759",25000, 35000);
        setOneKMData(res, "1001148921",90000, 100000);
        setOneKMData(res, "1001165668",30000, 40000);
        setOneKMData(res, "1001165670",30000, 40000);
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
                long spaceTime = 2000L;

                while (count < 100){
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
        DataStream<String> rdataStream = countStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                Map<String, List<Integer>> res = creatKMData();
                Integer count = Integer.valueOf(s);
                for(Map.Entry<String, List<Integer>> entry: res.entrySet()){
                    List<Integer> list = entry.getValue();
                    Integer km = (list.get(1)-list.get(0)) * count / 100 + list.get(0);
                    JSONObject jsonObject = createOnePacket(entry.getKey(), km);
                    collector.collect(jsonObject.toString());
                }
            }
        });
        rdataStream.print();
        rdataStream.writeAsText("C:\\Users\\12905\\Desktop\\mtn_source.txt", FileSystem.WriteMode.OVERWRITE);
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.10.178:9092", "FlinkTest1", new SimpleStringSchema()));

        DataStream<String> stateStream = countStream.flatMap(new FlatMapFunction<String, String>() {

            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                Thread.sleep(1000L);
                String taskInfoStr = sendGet("http://192.168.10.176:8080/states?id=testcaseuuid1");
                collector.collect(taskInfoStr);
            }
        });
        stateStream.print();
        stateStream.writeAsText("C:\\Users\\12905\\Desktop\\mtn_state.txt", FileSystem.WriteMode.OVERWRITE);

        see.execute();
    }
}
