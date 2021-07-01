package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.queryablestate.client.QueryableStateClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class readState {

    public static void getUserAlgInit()throws Exception{
        JobID jobId = JobID.fromHexString("075ec857e59f953a2215719565d2b16d");
        QueryableStateClient client = new QueryableStateClient("192.168.3.34", 9069);

        MapStateDescriptor<String, Map<String, Boolean>> userAlgInitDescriptor =
                new MapStateDescriptor<String, Map<String, Boolean>>(
                        "userAlgInit",
                        BasicTypeInfo.STRING_TYPE_INFO,
                        TypeInformation.of(new TypeHint<Map<String, Boolean>>() {})
                );
        Map<String, Boolean> resMap = new HashMap<>();
        for(int i = 29; i < 30; i++){
            String key = String.valueOf(i);
            CompletableFuture<MapState<String, Map<String, Boolean>>> resultFuture =
                    client.getKvState(jobId, "userAlgInitName", key, BasicTypeInfo.STRING_TYPE_INFO, userAlgInitDescriptor);
            MapState<String, Map<String, Boolean>> res = null;
            try {
                res = resultFuture.join();
            } catch (Exception e) {
            }
            if(res == null){
                resMap = null;
                break;
            }
            for(Map.Entry<String, Boolean> entry: res.get("0FAE65B3636F77439F687865366B694A").entrySet()){
                resMap.put(entry.getKey(), entry.getValue());
            }
        }
        System.out.println(resMap);
    }

    public static void getState()throws Exception{
        JobID jobId = JobID.fromHexString("075ec857e59f953a2215719565d2b16d");
        QueryableStateClient client = new QueryableStateClient("192.168.3.34", 9069);

        MapStateDescriptor<String, LoadingCache<String, TaskState>> stateDescriptor =
                new MapStateDescriptor<>(
                        "allTaskState",
                        new StringSerializer(),
                        new LoadcacheSerializer<String, TaskState>()
                );
        Map<String, TaskState> resMap = new HashMap<String, TaskState>();
        for(int i = 0; i < 100; i++){
            String key = String.valueOf(i);
            CompletableFuture<MapState<String, LoadingCache<String, TaskState>>> resultFuture =
                    client.getKvState(jobId, "allTaskStateName", key, BasicTypeInfo.STRING_TYPE_INFO, stateDescriptor);
            MapState<String, LoadingCache<String, TaskState>> res = null;
            try {
                res = resultFuture.join();
            } catch (Exception e) {
            }
            if(res == null){
                resMap = null;
                break;
            }
            for(Map.Entry<String, TaskState> entry: res.get("F4E41E5B91335E4096FE3DCAAB8A47FB").asMap().entrySet()){
                resMap.put(entry.getKey(), entry.getValue());
            }
        }
        System.out.println(resMap);

    }

    public static void main(String[] args) throws Exception {
        getState();
    }


}
