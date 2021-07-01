package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import StreamDataPacket.SubClassDataType.TaskInfoPacket;
import StreamProjectInit.StreamLog;
import cn.edu.thss.rcsdk.RealTimeAlg;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.queryablestate.client.QueryableStateClient;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LoadcacheTest {
    public static void copytest(){
        LoadingCache<String, TaskState> loadingCache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(1, TimeUnit.SECONDS)
                .build(new CacheLoader<String, TaskState>() {
                    @Override
                    public TaskState load(String s) throws Exception {
                        TaskState taskState = new TaskState(s);
                        taskState.addListState("0");
                        return taskState;
                    }
                });
        Map<String, TaskState> map = loadingCache.asMap();
        loadingCache.put("test", new TaskState("t0"));
        System.out.println(loadingCache.asMap());
        LoadingCache<String, TaskState> loadingCache2 = CacheBuilder
                .newBuilder()
                .expireAfterAccess(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, TaskState>() {
                    @Override
                    public TaskState load(String s) throws Exception {
                        TaskState taskState = new TaskState(s);
                        taskState.addListState("0");
                        return taskState;
                    }
                });
        System.out.println(loadingCache2.asMap());
        loadingCache2.putAll(loadingCache.asMap());
        System.out.println(loadingCache2.asMap());
    }

    public static void typeTest(){
        TypeInformation<TaskState> x = TypeInformation.of(new TypeHint<TaskState>() {});
        TypeSerializer<TaskState> y = x.createSerializer(new ExecutionConfig());
        System.out.println(y);
    }

    public static void getState() throws Exception {
        JobID jobId = JobID.fromHexString("a611731302106fe8c0aade540821cf98");
        String key = "7";
        QueryableStateClient client = new QueryableStateClient("192.168.10.176", 9069);
        MapStateDescriptor<String, LoadingCache<String, TaskState>> stateDescriptor =
                new MapStateDescriptor<String, LoadingCache<String, TaskState>>(
                        "allTaskState",
                        new StringSerializer(),
                        new LoadcacheSerializer<String, TaskState>()
                );
        CompletableFuture<MapState<String, LoadingCache<String, TaskState>>> resultFuture =
                client.getKvState(jobId, "allTaskStateName", key, BasicTypeInfo.STRING_TYPE_INFO, stateDescriptor);

        MapState<String, LoadingCache<String, TaskState>> res = resultFuture.join();
        for(Map.Entry<String, LoadingCache<String, TaskState>> entry: res.entries()){
            System.out.println(entry.getKey()+entry.getValue().asMap());
        }
    }

    public static void getTaskInfo() throws Exception {
        JobID jobId = JobID.fromHexString("48a9916975b4459dc105415bb848c37e");
        String key = "7";
        QueryableStateClient client = new QueryableStateClient("192.168.10.176", 9069);
        ValueStateDescriptor<TaskInfoPacket> taskInfoDescriptor =
                new ValueStateDescriptor<TaskInfoPacket>(
                        "taskInfo",
                        TypeInformation.of(new TypeHint<TaskInfoPacket>() {}).createSerializer(new ExecutionConfig())
                );
        CompletableFuture<ValueState<TaskInfoPacket>> future = client.getKvState(jobId, "taskInfoName", key, BasicTypeInfo.STRING_TYPE_INFO, taskInfoDescriptor);
        ValueState<TaskInfoPacket> res = future.get();
        System.out.println(res.value());
    }

    public static void getAlg() throws Exception {
        JobID jobId = JobID.fromHexString("5bd48902b262e2f9ca758c549528d7f6");
        String key = "7";
        QueryableStateClient client = new QueryableStateClient("192.168.10.176", 9069);
        MapStateDescriptor<String, RealTimeAlg> liveAlgDescriptor =
                new MapStateDescriptor<String, RealTimeAlg>(
                        "liveAlg",
                        new StringSerializer(),
                        TypeInformation.of(new TypeHint<RealTimeAlg>() {}).createSerializer(new ExecutionConfig())
                );
        CompletableFuture<MapState<String, RealTimeAlg>> future = client.getKvState(jobId, "liveAlgName", key, BasicTypeInfo.STRING_TYPE_INFO, liveAlgDescriptor);
        MapState<String, RealTimeAlg> res = future.get();
        for(Map.Entry<String, RealTimeAlg> entry: res.entries()){
            System.out.println(entry.getKey()+entry.getValue());
        }

    }

    public static void sleepTest() throws Exception{
        LoadingCache<String, TaskState> loadingCache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(1, TimeUnit.SECONDS)
                .build(new CacheLoader<String, TaskState>() {
                    @Override
                    public TaskState load(String s) throws Exception {
                        TaskState taskState = new TaskState(s);
                        taskState.addListState("0");
                        return taskState;
                    }
                });
        Map<String, TaskState> map = loadingCache.asMap();
        System.out.println(loadingCache.asMap());
        System.out.println(loadingCache.asMap().size());
        System.out.println(loadingCache.asMap().isEmpty());
        loadingCache.put("test", new TaskState("t0"));
        loadingCache.put("test2", new TaskState("t0"));
        map = loadingCache.asMap();
        System.out.println(loadingCache.asMap());
        System.out.println(loadingCache.asMap().size());
        System.out.println(loadingCache.get("test"));
        Thread.sleep(2000L);
        loadingCache.put("test3", new TaskState("t0"));
        loadingCache.cleanUp();
        map = new ConcurrentHashMap<String, TaskState>();
        map.putAll(loadingCache.asMap());
        System.out.println(loadingCache.asMap());
        System.out.println(loadingCache.asMap().size());
        System.out.println(loadingCache.asMap().keySet());
        System.out.println(loadingCache.asMap().keySet().size());
        System.out.println(map.size());
    }

    public static void main(String[] args) throws Exception {
        //copytest();
        //typeTest();
        //getState();
        //getTaskInfo();
        //getAlg();
        //sleepTest();
        String s = StreamLog.createLog("", "", "", "");
        System.out.println(s);
    }
}
