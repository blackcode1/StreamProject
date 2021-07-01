package StreamCal;

import StreamDataPacket.SubClassDataType.*;
import StreamProjectInit.StreamLog;
import StreamTest.LoadcacheSerializer;
import cn.edu.thss.rcsdk.RealTimeAlg;
import StreamDataPacket.BaseClassDataType.StreamTask;
import StreamDataPacket.BaseClassDataType.TaskState;
import StreamDataPacket.DataType;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.LoadingCache;
import edu.thss.entity.RawDataPacket;
import edu.thss.entity.TransPacket;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamCal {
    public static DataStream<DataType> streamCalTask(KeyedStream<DataType, String> keyedStream) throws Exception{
        final OutputTag<String> outputTag = new OutputTag<String>("LocalTestInput"){};
        final OutputTag<String> logTag = new OutputTag<String>("Log"){};

        SingleOutputStreamOperator<DataType> resultStream = keyedStream.process(new KeyedProcessFunction<String, DataType, DataType>() {
            private transient MapState<String, LoadingCache<String, TaskState>> allTaskState;//task_id, device_id, state
            private transient ValueState<TaskInfoPacket> taskInfoPacket;
            private transient MapState<String, Map<String, List<Map<String, String>>>> allTaskVar;//device_id, dataset_id, row, key, value
            private transient MapState<String, Boolean> liveTask;
            private transient Map<String, RealTimeAlg> userAlg;
            private transient String projectDataType;
            private transient String projectID;
            private transient MapState<String, Map<String, Boolean>> userAlgInit;

            public void open(Configuration parameters) throws Exception {
                ExecutionConfig config = getRuntimeContext().getExecutionConfig();
                MapStateDescriptor<String, LoadingCache<String, TaskState>> stateDescriptor =
                        new MapStateDescriptor<String, LoadingCache<String, TaskState>>(
                                "allTaskState",
                                new StringSerializer(),
                                new LoadcacheSerializer<String, TaskState>(config)
                        );
                stateDescriptor.setQueryable("allTaskStateName");
                allTaskState = getRuntimeContext().getMapState(stateDescriptor);

                ValueStateDescriptor<TaskInfoPacket> taskInfoDescriptor =
                        new ValueStateDescriptor<TaskInfoPacket>(
                                "taskInfo",
                                TypeInformation.of(new TypeHint<TaskInfoPacket>() {}).createSerializer(config)
                        );
                taskInfoPacket = getRuntimeContext().getState(taskInfoDescriptor);

                MapStateDescriptor<String, Map<String, List<Map<String, String>>>> taskVarSamllDescriptor =
                        new MapStateDescriptor<String, Map<String, List<Map<String, String>>>>(
                                "taskVar",
                                BasicTypeInfo.STRING_TYPE_INFO,
                                TypeInformation.of(new TypeHint<Map<String, List<Map<String, String>>>>() {})
                        );
                allTaskVar = getRuntimeContext().getMapState(taskVarSamllDescriptor);

                MapStateDescriptor<String, Boolean> liveTaskDescriptor =
                        new MapStateDescriptor<String, Boolean>(
                                "liveTask",
                                BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.BOOLEAN_TYPE_INFO
                        );
                liveTaskDescriptor.setQueryable("liveTaskName");
                liveTask = getRuntimeContext().getMapState(liveTaskDescriptor);

                userAlg = new HashMap<String, RealTimeAlg>();

                ParameterTool pt = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
                projectDataType = pt.get("StreamDataType");
                projectID = pt.get("StreamProjectID");

                MapStateDescriptor<String, Map<String, Boolean>> userAlgInitDescriptor =
                        new MapStateDescriptor<String, Map<String, Boolean>>(
                                "userAlgInit",
                                BasicTypeInfo.STRING_TYPE_INFO,
                                TypeInformation.of(new TypeHint<Map<String, Boolean>>() {})
                        );
                userAlgInitDescriptor.setQueryable("userAlgInitName");
                userAlgInit = getRuntimeContext().getMapState(userAlgInitDescriptor);
            }

            public void cal(StreamTask streamTask, String deviceID, List<String> gkIDList,
                            RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput,
                            Collector<DataType> collector, Context context) throws Exception{
                List<String> condition = ModifyMemory.getCondition(gkIDList, streamTask.condition);
                Map<String, List<Map<String, String>>> taskvar = ModifyMemory.getTaskvar(allTaskVar, deviceID);
                List<TaskState> publicStateList = ModifyMemory.getPublicState(allTaskState, streamTask, deviceID, taskInfoPacket.value());
                TaskState privateState = ModifyMemory.getPrivateState(allTaskState, streamTask, deviceID);
                Boolean isLiveTask = true;
                RealTimeAlg rti = null;
                if(streamTask.taskState == 1){
                    rti = GetUserAlg.getUserAlg(userAlg, taskInfoPacket.value().jarInfoMap, streamTask.jarID);
                }

                String outputType = taskInfoPacket.value().jarInfoMap.get(streamTask.jarID).outputType;

                if(streamTask.taskState == 1 && (!userAlgInit.contains(streamTask.taskID)
                        || !userAlgInit.get(streamTask.taskID).containsKey(deviceID)
                        || !userAlgInit.get(streamTask.taskID).get(deviceID))){
                    isLiveTask = rti.init(rawInput, transInput, jsonInput,
                            condition, taskvar, publicStateList, privateState);
                }
                else if(streamTask.taskState == -1){
                    isLiveTask = false;
                }
                if(userAlgInit.contains(streamTask.taskID)){
                    Map<String, Boolean> map = userAlgInit.get(streamTask.taskID);
                    map.put(deviceID, isLiveTask);
                    userAlgInit.put(streamTask.taskID, map);
                }
                else {
                    Map<String, Boolean> map = new HashMap<String, Boolean>();
                    map.put(deviceID, isLiveTask);
                    userAlgInit.put(streamTask.taskID, map);
                }
                liveTask.put(streamTask.taskID, isLiveTask);
                if(isLiveTask && streamTask.taskState == 1){
                    List<DataType> res = PacketCalResult.packetCalResult(projectID, outputType, rti, streamTask.taskID, streamTask.timeout,
                            rawInput, transInput, jsonInput, condition, taskvar, publicStateList, privateState, streamTask.outputTopic);
                    if(res != null){
                        for(int i = 0; i < res.size(); i++){
                            if(res.get(i).streamDataType.equals("Log")){
                                LogList logList = (LogList) res.get(i);
                                context.output(logTag, logList.logStr);
                            }else {
                                collector.collect(res.get(i));
                            }
                        }
                    }
                }
                else if(streamTask.taskState == -1){
                    JSONObject loacalTestInput = new JSONObject();
                    loacalTestInput.put("rawInput", rawInput);
                    loacalTestInput.put("transInput", transInput);
                    loacalTestInput.put("jsonInput", jsonInput);
                    loacalTestInput.put("condition", condition);
                    loacalTestInput.put("taskvar", taskvar);
                    loacalTestInput.put("publicStateList", publicStateList);
                    loacalTestInput.put("deviceID", deviceID);
                    loacalTestInput.put("taskID", streamTask.taskID);
                    context.output(outputTag, loacalTestInput.toJSONString());
                }
                LoadingCache<String, TaskState> loadingCache = allTaskState.get(streamTask.taskID);
                loadingCache.put(deviceID, privateState);
                allTaskState.put(streamTask.taskID, loadingCache);
            }


            @Override
            public void processElement(DataType dataType, Context context, Collector<DataType> collector) throws Exception {
                try {
                    if(taskInfoPacket.value() == null){
                        taskInfoPacket.update(new TaskInfoPacket());
                    }
                    if(dataType.streamDataType.equals("taskInfo")){
                        try {
                            ModifyMemory.modifyTaskInfo(taskInfoPacket, (TaskInfoPacket)dataType);
                            ModifyMemory.modifyState(allTaskState, (TaskInfoPacket)dataType);
                            ModifyMemory.modifyJarInfo(userAlg, userAlgInit, (TaskInfoPacket)dataType);
                        } catch (Exception e) {
                            String logStr = StreamLog.createLocalLog(e, "ERROR", "算法容器加载任务信息异常", projectID);
                            context.output(logTag, logStr);
                        }
                    }
                    else if(dataType.streamDataType.equals("taskVarSmall")){
                        try {
                            TaskVarPacket taskVarPacket = (TaskVarPacket) dataType;
                            allTaskVar.put(dataType.dataID, taskVarPacket.taskvar);
                        }catch (Exception e) {
                            String logStr = StreamLog.createLocalLog(e, "ERROR", "算法容器加载任务配置数据异常", projectID);
                            context.output(logTag, logStr);
                        }
                    }
                    else if(dataType.streamDataType.equals("taskVarBig")){
                        try {
                            TaskVarPacket taskVarPacket = (TaskVarPacket) dataType;
                            allTaskVar.put("*", taskVarPacket.taskvar);
                        }catch (Exception e) {
                            String logStr = StreamLog.createLocalLog(e, "ERROR", "算法容器加载任务配置数据异常", projectID);
                            context.output(logTag, logStr);
                        }
                    }
                    else if(dataType.streamDataType.equals(projectDataType)){
                        String datasetID = dataType.source;
                        String deviceID = AnalyInputPacket.getDeviceID(dataType);
                        List<String> gkIDList = AnalyInputPacket.getGKIDList(dataType);
                        RawDataPacket rawInput = AnalyInputPacket.getRawpacket(dataType);
                        TransPacket transInput = AnalyInputPacket.getTranspacket(dataType);
                        JSONObject jsonInput = AnalyInputPacket.getJsonInput(dataType);

                        List<StreamTask> taskMap = taskInfoPacket.value().taskList;
                        for(StreamTask streamTask: taskMap){
                            if(dataType.isSignal && IsActiveTask.isActiveSignal(datasetID, taskInfoPacket.value(), streamTask)){
                                cal(streamTask, deviceID, gkIDList, rawInput, transInput, jsonInput, collector, context);
                            }
                            else if(IsActiveTask.isActive(datasetID, deviceID, gkIDList, taskInfoPacket.value(), streamTask)){
                                cal(streamTask, deviceID, gkIDList, rawInput, transInput, jsonInput, collector, context);
                            }
                            else if(!IsActiveTask.isLive(taskInfoPacket.value(), streamTask)){
                                liveTask.put(streamTask.taskID, false);
                                userAlgInit.put(streamTask.taskID, new HashMap<String, Boolean>());
                            }
                        }
                    }
                    else if(dataType.streamDataType.equals("onTime")) {
                        List<StreamTask> taskMap = taskInfoPacket.value().taskList;
                        for (StreamTask streamTask : taskMap) {
                            if (streamTask.useOnTimeSource && IsActiveTask.isLive(taskInfoPacket.value(), streamTask)) {
                                List<String> deviceList = streamTask.deviceList;
                                if(deviceList.size() != 0 && deviceList.get(0).equals("*") && userAlgInit.contains(streamTask.taskID)){
                                    for(Map.Entry<String, Boolean> entry1: userAlgInit.get(streamTask.taskID).entrySet()){
                                        if(entry1.getValue() || streamTask.taskState == -1){
                                            String deviceID = entry1.getKey();
                                            cal(streamTask, deviceID, new ArrayList<String>(), null, null, null, collector, context);
                                        }
                                    }
                                }
                                else if(deviceList.size() != 0 && !deviceList.get(0).equals("*")){
                                    for(int i = 0; i < deviceList.size(); i++){
                                        String deviceID = deviceList.get(i);
                                        Integer current_key = Integer.valueOf(context.getCurrentKey());
                                        Integer device_key = Integer.valueOf(deviceID) % 100;
                                        if(current_key == device_key){
                                            cal(streamTask, deviceID, new ArrayList<String>(), null, null, null, collector, context);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(dataType.streamDataType.equals("Log")){
                        LogList logList = (LogList) dataType;
                        context.output(logTag, logList.logStr);
                    }
                } catch (Exception e) {
                    String logStr = StreamLog.createLocalLog(e, "ERROR", "算法容器异常", projectID);
                    context.output(logTag, logStr);
                }
            }
        }).uid("cal-id");

        return resultStream;
    }
}
