package StreamTest;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.ArrayList;
import java.util.List;

public class OutputStreamTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(10);
        List<String> list = new ArrayList<>();
        list.add("x");
        DataStream<String> dataStream = see.fromCollection(list);
        final OutputTag<String> outputTag = new OutputTag<String>("LocalTestInput"){};
        final OutputTag<String> logTag = new OutputTag<String>("Log"){};
        SingleOutputStreamOperator<String> operator = dataStream.process(new ProcessFunction<String, String>() {
            @Override
            public void processElement(String s, Context context, Collector<String> collector) throws Exception {
                collector.collect(s);
                context.output(outputTag, "output");
                context.output(logTag, "hello");
            }
        });
        DataStream<String> localTestInputStream = operator.getSideOutput(outputTag);

        DataStream<String> runTimeLogStream = operator.getSideOutput(logTag);
       // operator.print();
        localTestInputStream.print();
        runTimeLogStream.print();

        see.execute();
    }
}
