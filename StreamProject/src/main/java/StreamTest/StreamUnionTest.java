package StreamTest;

import StreamDataSource.QyKafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import StreamDataSource.*;

public class StreamUnionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> s1 = see.addSource(new OnTimeTestSource(10));
        DataStream<String> s2 = null;
        s2 = s1;
        s2 = s2.union(s1);
        s2.print();

        see.execute();

    }
}
