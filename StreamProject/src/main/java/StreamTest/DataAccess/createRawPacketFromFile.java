package StreamTest.DataAccess;

import com.alibaba.fastjson.JSONObject;
import edu.thss.entity.ParsedDataPacket;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class createRawPacketFromFile {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);

        String filepath = "";//文件地址"C:\\Users\\12905\\Desktop\\data.txt"
        DataStream<String> rdataStream = see.readTextFile(filepath).flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                s.split(" ");//文件的一行
                ParsedDataPacket pdp = new ParsedDataPacket();
                pdp.setLine("");
                pdp.setCar("");
                pdp.addBaseInfo("", "");//线路车辆信息值
                pdp.addWorkStatus("", 0L, "");//工况值
                collector.collect(pdp.toJson().toString());
            }
        });



        rdataStream.print();
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.3.32:9092", "streamOutput1", new SimpleStringSchema()));

        see.execute();
    }
}
