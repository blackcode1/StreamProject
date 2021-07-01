package StreamTest.FailurediagTest;


import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static StreamTest.CreateParsedJson.hexString2binaryString;

public class CreateInput {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);

        DataStream<String> rdataStream = see.readTextFile("C:\\Users\\12905\\Desktop\\a.txt").map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return JSONObject.parseObject(s).toString();
            }
        });

        rdataStream.print();
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.3.32:9092", "streamInput2", new SimpleStringSchema()));

        see.execute();
    }
}
