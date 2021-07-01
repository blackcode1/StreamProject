package StreamCal;

import StreamDataPacket.BaseClassDataType.StreamTask;
import StreamDataPacket.BaseClassDataType.TaskState;
import StreamDataPacket.SubClassDataType.TaskInfoPacket;
import cn.edu.thss.rcsdk.RealTimeAlg;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.ValueState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ModifyMemory {
    public static void modifyTaskInfo
            (ValueState<TaskInfoPacket> taskInfoPacket, TaskInfoPacket dataType) throws IOException {
        taskInfoPacket.update(dataType);
    }

    public static void modifyJarInfo
            (Map<String, RealTimeAlg> userAlg, MapState<String, Map<String, Boolean>> userAlgInit, TaskInfoPacket taskInfo) throws Exception {
        for(StreamTask entry: taskInfo.taskList) {
            if(entry.taskState == 0 && userAlg.containsKey(entry.jarID)){
                userAlg.remove(entry.jarID);
            }
            if(entry.taskState == 0 && userAlgInit.contains(entry.taskID)){
                userAlgInit.remove(entry.taskID);
            }
        }
    }

    public static void modifyState
            (MapState<String, LoadingCache<String, TaskState>> allTaskState, TaskInfoPacket taskInfo) throws Exception{

        for(StreamTask entry: taskInfo.taskList) {
            if (!allTaskState.contains(entry.taskID) && entry.taskState != 0) {
                Long statePeriod = entry.statePeriod;
                if (statePeriod == -1L) {
                    statePeriod = 3 * 365L;
                }
                LoadingCache<String, TaskState> loadingCache = CacheBuilder
                        .newBuilder()
                        .expireAfterAccess(statePeriod, TimeUnit.DAYS)
                        .build(new CacheLoader<String, TaskState>() {
                            @Override
                            public TaskState load(String s) throws Exception {
                                TaskState taskState = new TaskState(s);
                                return taskState;
                            }
                        });
                loadingCache.put("Time", new TaskState(statePeriod.toString()));
                allTaskState.put(entry.taskID, loadingCache);
            }
            else if(allTaskState.contains(entry.taskID) && entry.taskState == 0){
                allTaskState.remove(entry.taskID);
            }
        }

    }

    public static List<String> getCondition(List<String> allGK, List<String> condGK) throws Exception{

        List<String> condition = new ArrayList<String>();
        for(int i = 0; i < allGK.size(); i++){
            String gkID = allGK.get(i);
            if(condGK.contains(gkID)){
                condition.add(gkID);
            }
        }
        return condition;
    }

    public static Map<String, List<Map<String, String>>> getTaskvar
            (MapState<String, Map<String, List<Map<String, String>>>> allTaskVar, String deviceID) throws Exception{

        Map<String, List<Map<String, String>>> taskvar = new HashMap<String, List<Map<String, String>>>();
        if(allTaskVar.contains(deviceID)){
            for(Map.Entry<String, List<Map<String, String>>> entry: allTaskVar.get(deviceID).entrySet()){
                List<Map<String, String>> mapList2 = entry.getValue();
                List<Map<String, String>> mapList3 = new ArrayList<Map<String, String>>();
                for(int i = 0; i < mapList2.size(); i++){
                    mapList3.add(new HashMap<String, String>(mapList2.get(i)));
                }
                taskvar.put(entry.getKey(), mapList3);
            }
        }
        if(allTaskVar.contains("*")){
            for(Map.Entry<String, List<Map<String, String>>> entry: allTaskVar.get("*").entrySet()){
                List<Map<String, String>> mapList2 = entry.getValue();
                List<Map<String, String>> mapList3 = new ArrayList<Map<String, String>>();
                for(int i = 0; i < mapList2.size(); i++){
                    mapList3.add(new HashMap<String, String>(mapList2.get(i)));
                }
                taskvar.put(entry.getKey(), mapList3);
            }
        }

        return taskvar;
    }

    public static List<TaskState> getPublicState
            (MapState<String, LoadingCache<String, TaskState>> allTaskState, StreamTask streamTask, String deviceID, TaskInfoPacket taskInfo)
            throws Exception{

        List<TaskState> publicStateList = new ArrayList<TaskState>();
        List<String> publicStateID = streamTask.publicStateIDList;
        for(int i = 0; i < publicStateID.size(); i++){
            String stateID = publicStateID.get(i);
            String stateTaskID = taskInfo.getStateTaskID(stateID);
            if(taskInfo.stateInfoList.containsKey(stateID) && allTaskState.contains(stateTaskID)){
                TaskState publicState = allTaskState.get(stateTaskID).get(deviceID).clone();
                publicStateList.add(publicState);
            }
            else {
                publicStateList.add(null);
            }
        }

        return publicStateList;
    }

    public static TaskState getPrivateState
            (MapState<String, LoadingCache<String, TaskState>> allTaskState, StreamTask streamTask, String deviceID)
            throws Exception{
        TaskState privateState = null;
        String taskID = null;
        taskID = streamTask.taskID;
        if(!allTaskState.contains(taskID)){
            Long statePeriod = streamTask.statePeriod;
            if(statePeriod == -1L){
                statePeriod = 3*365L;
            }
            LoadingCache<String, TaskState> loadingCache = CacheBuilder
                    .newBuilder()
                    .expireAfterAccess(statePeriod, TimeUnit.DAYS)
                    .build(new CacheLoader<String, TaskState>() {
                        @Override
                        public TaskState load(String s) throws Exception {
                            TaskState taskState = new TaskState(s);
                            return taskState;
                        }
                    });
            loadingCache.put("Time", new TaskState(statePeriod.toString()));
            allTaskState.put(taskID, loadingCache);
        }
        privateState = allTaskState.get(taskID).get(deviceID);
        return privateState;
    }
}
