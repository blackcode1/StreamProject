package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import StreamDataSource.OnTimeTestSource;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.string.StringEncoder;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class KeybyTest {
    //流1：w1，w2，w3
    //流2：x1，x2，x3；y1，y2，y3
    //w，x按key分组，y放到所有流中
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(10);

        DataStream<String> s1 = see.addSource(new OnTimeTestSource(1));
        DataStream<String> s2 = see.addSource(new OnTimeTestSource(5));
        DataStream<String>sw = s1.map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "w"+s;
            }
        });
        s2 = s2.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                collector.collect("x"+s);
                collector.collect("y"+s);
            }
        });
        OutputTag<String> outputTag = new OutputTag<String>("samllPacketTaskVar"){};
        SingleOutputStreamOperator<String> sx = s2.process(new ProcessFunction<String, String>() {
            @Override
            public void processElement(String s, Context context, Collector<String> collector) throws Exception {
                if(s.startsWith("x")){
                    collector.collect(s);
                }
                else {
                    context.output(outputTag, s);
                }
            }
        });
        DataStream<String> sy = sx.getSideOutput(outputTag);

        KeyedStream<String, String> k1 = sw.union(s2).keyBy(new KeySelector<String, String>() {
            @Override
            public String getKey(String s) throws Exception {
                Integer key = (Integer.valueOf(s.substring(1))%3);
                return key.toString();
            }
        });

        DataStream<String> tmp = k1.process(new KeyedProcessFunction<String, String, String>() {
            private transient MapState<String, Map<String, TaskState>> state2;
            private transient MapState<String, LoadingCache<String, TaskState>> newState;

            public void open(org.apache.flink.configuration.Configuration parameters) throws Exception {
                StateTtlConfig ttlConfig = StateTtlConfig
                        .newBuilder(Time.seconds(3))
                        .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                        .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                        .build();
                //descriptor.enableTimeToLive(ttlConfig);

                MapStateDescriptor<String, Map<String, TaskState>> descriptor1 =
                        new MapStateDescriptor<String, Map<String, TaskState>>(
                                "2",
                                BasicTypeInfo.STRING_TYPE_INFO,
                                TypeInformation.of(new TypeHint<Map<String, TaskState>>() {}));
                state2 = getRuntimeContext().getMapState(descriptor1);

                MapStateDescriptor<String, LoadingCache<String, TaskState>> descriptor =
                        new MapStateDescriptor<String, LoadingCache<String, TaskState>>(
                                "x",
                                BasicTypeInfo.STRING_TYPE_INFO,
                                TypeInformation.of(new TypeHint<LoadingCache<String, TaskState>>() {}));
                newState = getRuntimeContext().getMapState(descriptor);
            }

            @Override
            public void processElement(String s, Context context, Collector<String> collector) throws Exception {
                /*if(!newState.contains("xy")){
                    LoadingCache<String, TaskState> loadingCache = CacheBuilder
                            .newBuilder()
                            .expireAfterAccess(25L, TimeUnit.SECONDS)
                            .build(new CacheLoader<String, TaskState>() {
                                @Override
                                public TaskState load(String s) throws Exception {
                                    TaskState taskState = new TaskState(s);
                                    taskState.listState.add(null);
                                    List<String> ls = new ArrayList<String>();
                                    ls.add(null);
                                    taskState.mapState.put("y", ls);
                                    return taskState;
                                }
                            });
                    newState.put("xy", loadingCache);
                }
                if(s.startsWith("w")){
                    collector.collect(s+
                            newState.get("xy").get("0").listState.get(0)+
                            newState.get("xy").get("0").mapState.get("y").get(0));
                }
                else if(s.startsWith("x")){
                    LoadingCache<String, TaskState> loadingCache = newState.get("xy");
                    TaskState taskState = loadingCache.get("0");
                    taskState.listState.set(0, s);
                    //loadingCache.put("0", taskState);
                    //newState.get("xy").get("0").listState.set(0, s);
                }
                else if(s.startsWith("y")){
                    newState.get("xy").get("0").mapState.get("y").set(0, s);
                }
                if(!state2.contains("xy")){
                    TaskState taskState = new TaskState("List");
                    taskState.listState.add(null);
                    List<String> ls = new ArrayList<String>();
                    ls.add(null);
                    taskState.mapState.put("y", ls);
                    Map<String, TaskState> taskStateMap = new HashMap<String, TaskState>();
                    taskStateMap.put("0", taskState);
                    state2.put("xy", taskStateMap);
                }
                if(s.startsWith("w")){
                   collector.collect(s
                            +state2.get("xy").get("0").listState.get(0)
                            +state2.get("xy").get("0").mapState.get("y").get(0)
                            +state2.get("xy").get("0").stateType);
                }
                else if(s.startsWith("x")){
                    TaskState taskState = state2.get("xy").get("0").clone();
                    taskState.stateType = "x";
                    taskState.listState.set(0, s);
                }
                else if(s.startsWith("y")){
                    TaskState taskState = state2.get("xy").get("0").clone();
                    taskState.mapState.get("y").set(0, s);
                }*/
            }
        });

        //.process(new StateTest());
        //k1.print();
        tmp.print();

        //System.out.println(see.getExecutionPlan());
        see.execute();

    }
}
