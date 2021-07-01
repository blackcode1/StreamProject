package StreamTest.StorageTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

public class CreateIotDBInput {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> rdataStream = see.readTextFile("C:\\Users\\12905\\Desktop\\a.txt");

        DataStream<String> resStream = rdataStream.map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                JSONObject jsonObject = JSONObject.parseObject(s);
                return jsonObject.toString();
            }
        });
        resStream.print();
        resStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.3.32:9092", "decodeStore", new SimpleStringSchema()));
        see.execute();
    }
}
