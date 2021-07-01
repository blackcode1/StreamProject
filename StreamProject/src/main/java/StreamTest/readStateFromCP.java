package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.aggregation.Aggregations;
import org.apache.flink.configuration.Configuration;

import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.state.api.ExistingSavepoint;
import org.apache.flink.state.api.Savepoint;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;


import java.util.HashMap;
import java.util.Map;


public class readStateFromCP {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment bEnv = ExecutionEnvironment.getExecutionEnvironment();
        //ExistingSavepoint savepoint = Savepoint.load(bEnv, "file:///home/qianzhou/save/savepoint-ab2ba7-d008df870580", new FsStateBackend("file:///home/qianzhou/save"));
        ExistingSavepoint savepoint = Savepoint.load(bEnv, "file:///C:/Users/12905/Desktop/savepoint-df8a37-31bfe7a76e71", new FsStateBackend("file:///C:/Users/12905/Desktop/save"));

        DataSet<Map<String, Map<String, TaskState>>> data = savepoint.readKeyedState("cal-id", new KeyedStateReaderFunction<String, Map<String, Map<String, TaskState>>>() {
            private transient MapState<String, LoadingCache<String, TaskState>> allTaskState;
            @Override
            public void open(Configuration configuration) throws Exception {
                ExecutionConfig config = getRuntimeContext().getExecutionConfig();
                MapStateDescriptor<String, LoadingCache<String, TaskState>> stateDescriptor =
                        new MapStateDescriptor<String, LoadingCache<String, TaskState>>(
                                "allTaskState",
                                new StringSerializer(),
                                new LoadcacheSerializer<String, TaskState>(config)
                        );
                stateDescriptor.setQueryable("allTaskStateName");
                allTaskState = getRuntimeContext().getMapState(stateDescriptor);
            }

            @Override
            public void readKey(String s, Context context, Collector<Map<String, Map<String, TaskState>>> collector) throws Exception {
                Map<String, Map<String, TaskState>> map = new HashMap<>();
                for(Map.Entry<String, LoadingCache<String, TaskState>> entry: allTaskState.entries()){
                    Map<String, TaskState> stateMap = new HashMap<>();
                    stateMap.putAll(entry.getValue().asMap());
                    map.put(entry.getKey(), stateMap);
                }
                collector.collect(map);
            }
        }).mapPartition(new MapPartitionFunction<Map<String, Map<String, TaskState>>, Map<String, Map<String, TaskState>>>() {
            @Override
            public void mapPartition(Iterable<Map<String, Map<String, TaskState>>> iterable, Collector<Map<String, Map<String, TaskState>>> collector) throws Exception {
                Map<String, Map<String, TaskState>> map = new HashMap<>();
                Map<String, TaskState> stateMap = new HashMap<>();
                for(Map<String, Map<String, TaskState>> mapMap: iterable){
                    Map<String, TaskState> taskStateMap = mapMap.get("testcaseuuid1");
                    stateMap.putAll(taskStateMap);
                }
                map.put("testcaseuuid1", stateMap);
                collector.collect(map);
            }
        }).setParallelism(1);
        System.out.println(data.collect().get(0));
        data.print();

    }
}
