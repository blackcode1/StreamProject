package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class stateSource {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(60*1000);
        //env.getConfig().enableForceAvro();
        env.setStateBackend(new FsStateBackend("file:///home/qianzhou/jar"));
        //env.setStateBackend(new FsStateBackend("file:///C://Users//12905//Desktop"));
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
                long spaceTime = 6*1000L;
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

                })
                .map(new RichMapFunction<Integer, Integer>() {
                    private transient MapState<String, LoadingCache<String, TaskState>> allTaskState;//task_id, device_id, state

                    @Override
                    public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
                        MapStateDescriptor<String, LoadingCache<String, TaskState>> stateDescriptor =
                                new MapStateDescriptor<String, LoadingCache<String, TaskState>>(
                                        "allTaskState",
                                        new StringSerializer(),
                                        new LoadcacheSerializer<String, TaskState>()
                                );
                        stateDescriptor.setQueryable("allTaskStateName");
                        allTaskState = getRuntimeContext().getMapState(stateDescriptor);
                    }

                    @Override
                    public Integer map(Integer integer) throws Exception {
                        if(!allTaskState.contains("task_id")){
                            LoadingCache<String, TaskState> loadingCache = CacheBuilder
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
                            allTaskState.put("task_id", loadingCache);
                        }
                        TaskState taskState = allTaskState.get("task_id").get("device_id");

                        Integer currentSum = Integer.valueOf(taskState.getListState().get(0));
                        currentSum = currentSum + 6;
                        taskState.setListState(new ArrayList<String>(Collections.singleton(String.valueOf(currentSum))));
                        return currentSum;
                    }
                });
        outStream.print();
        env.execute();
    }


}
