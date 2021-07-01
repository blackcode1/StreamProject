package StreamTest;

import StreamDataPacket.DataType;
import StreamDataSource.OnTimeTestSource;
import StreamInput.GetBroadStream.OnTimeSource;
import StreamTest.FailurediagTest.TimeWindow;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.util.Collector;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TimeWindowTest {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        List<String> list = new ArrayList<>();
        list.add("1:100"+ " "+ System.currentTimeMillis());
        list.add("2:101");
        list.add("3:102");
        list.add("4:103");

        DataStream<String> rdataStream = env.addSource(new OnTimeTestSource(1)).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                s = s + " "+ System.currentTimeMillis();
                return s;
            }
        });

        rdataStream = rdataStream.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks<String>() {
            @Nullable
            @Override
            public Watermark getCurrentWatermark() {
                return new Watermark(System.currentTimeMillis());
            }

            @Override
            public long extractTimestamp(String s, long l) {
                return System.currentTimeMillis();
            }
        });

        rdataStream.keyBy(new KeySelector<String, String>() {
            @Override
            public String getKey(String s) throws Exception {
                int i = Integer.valueOf(s.split(" ")[0]) % 3;
                return String.valueOf(i);
            }
        }).process(new KeyedProcessFunction<String, String, String>() {
            TimeWindow timeWindow = new TimeWindow(5000L, 2000L);
            @Override
            public void processElement(String s, Context context, Collector<String> collector) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", s);
                List<JSONObject> res = timeWindow.timeWindow(jsonObject);
                System.out.println(timeWindow.windowPackets);
                collector.collect(res.toString());
            }
        }).print();
       // resStream.print();
         env.execute();
    }
}
