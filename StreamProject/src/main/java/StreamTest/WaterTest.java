package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import StreamDataSource.OnTimeTestSource;
import akka.stream.actor.WatermarkRequestStrategy;
import com.google.common.cache.LoadingCache;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.state.KeyedStateStore;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.*;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import javax.annotation.Nullable;
import java.util.Map;

public class WaterTest {
    //流1：w1，w2，w3
    //流2：x1，x2，x3；y1，y2，y3
    //w，x按key分组，y放到所有流中
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(10);
        see.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);


        DataStream<String> s1 = see.addSource(new OnTimeTestSource(1)).assignTimestampsAndWatermarks(
                new AssignerWithPeriodicWatermarks<String>() {
            Long maxs = 0L;
            @Nullable
            @Override
            public Watermark getCurrentWatermark() {
                return new Watermark(maxs);
            }

            @Override
            public long extractTimestamp(String s, long l) {
                maxs = Long.parseLong(s)*2000-1;
                return Long.parseLong(s)*2000;
            }
        });
        DataStream<String> s2 = see.addSource(new OnTimeTestSource(1));
        s1.join(s2).where(new KeySelector<String, String>() {
            @Override
            public String getKey(String s) throws Exception {
                Integer i = Integer.valueOf(s) % 2;
                return String.valueOf(i);
            }
        }).equalTo(new KeySelector<String, String>() {
            @Override
            public String getKey(String s) throws Exception {
                return "0";
            }
        }).window(TumblingProcessingTimeWindows.of(Time.seconds(5L))).apply(new JoinFunction<String, String, String>() {
            @Override
            public String join(String s, String s2) throws Exception {
                return s + " " + s2;
            }
        }).print();
        see.execute();

    }
}
