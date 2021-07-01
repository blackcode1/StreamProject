package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import StreamDataPacket.SubClassDataType.TaskInfoPacket;
import cn.edu.thss.rcsdk.RealTimeAlg;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;

import java.util.*;

public class stateSource2 {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String x = "";

        Long cptime =1 * 10 * 1000L;
        if (env.getStateBackend() == null) {
            String checkPointPath = "file:///" + System.getProperty("user.dir");
            System.out.println("checkpoint path: " + checkPointPath);
            env.setStateBackend(new FsStateBackend(checkPointPath));
        }
        env.enableCheckpointing(cptime);
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.setParallelism(1);
        DataStream<Integer> staSource = env.addSource(new SourceFunction<Integer>() {
            @Override
            public void run(SourceContext<Integer> sourceContext) throws Exception {
                int count = 0;
                long total = 0;
                //long spaceTime = 60 * 1000L;
                long spaceTime = 1000L;
                while (true) {
                    sourceContext.collect(count);
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
        DataStream<Integer> outStream = staSource
                .keyBy(new KeySelector<Integer, String>() {
                    @Override
                    public String getKey(Integer integer) throws Exception {
                        return "1";
                    }

                }).process(new KeyedProcessFunction<String, Integer, Integer>() {
                    private transient MapState<String, LoadingCache<String, TaskState>> allTaskState;//task_id, device_id, state
                    private transient ValueState<TaskInfoPacket> taskInfoPacket;
                    private transient MapState<String, Map<String, List<Map<String, String>>>> allTaskVar;//device_id, dataset_id, row, key, value
                    private transient MapState<String, Boolean> liveTask;
                    private transient MapState<String, RealTimeAlg> userAlg;
                    private transient MapState<String, Map<String, Boolean>> userAlgInit;

                    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
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
                                        TypeInformation.of(new TypeHint<TaskInfoPacket>() {})
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

                        MapStateDescriptor<String, RealTimeAlg> liveAlgDescriptor =
                                new MapStateDescriptor<String, RealTimeAlg>(
                                        "liveAlg",
                                        BasicTypeInfo.STRING_TYPE_INFO,
                                        TypeInformation.of(new TypeHint<RealTimeAlg>() {})
                                );
                        userAlg = getRuntimeContext().getMapState(liveAlgDescriptor);


                        MapStateDescriptor<String, Map<String, Boolean>> userAlgInitDescriptor =
                                new MapStateDescriptor<String, Map<String, Boolean>>(
                                        "userAlgInit",
                                        BasicTypeInfo.STRING_TYPE_INFO,
                                        TypeInformation.of(new TypeHint<Map<String, Boolean>>() {})
                                );
                        userAlgInit = getRuntimeContext().getMapState(userAlgInitDescriptor);
                    }
                    @Override
                    public void processElement(Integer integer, Context context, Collector<Integer> collector) throws Exception {
                        allTaskState.put("test", null);
                        taskInfoPacket.update(null);
                        allTaskState.put("test", null);
                        allTaskVar.put("test", null);
                        liveTask.put("test", false);
                        userAlg.put("test", null);
                        userAlgInit.put("test", null);
                        collector.collect(integer);
                    }
                } );
        outStream.print();
        env.execute();
    }


}
