package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import scala.collection.parallel.mutable.ParArray;

import java.util.HashMap;
import java.util.Map;

public class CreateParsedJson {

    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> rdataStream = see.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                sourceContext.collect("0");
            }

            @Override
            public void cancel() {

            }
        });
        see.setParallelism(1);

        rdataStream = rdataStream.flatMap(new RichFlatMapFunction<String, String>() {

            public void open(Configuration parameters) throws Exception {

            }

            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                JSONObject res = new JSONObject();
                //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}
                String hex = "FF0107DC03D2004E6F2E323232473131314865616400175E0EA9D4010D000000007101020304FFFFFF11000000008105060708090AFFFF000000011205060708090AFFFF000000012205060708090AFFFF";
                Map<String, Map<String, String>> wsmap = new HashMap<>();
                res.put("deviceID", "0");
                res.put("timeStamp", System.currentTimeMillis());
                res.put("workStatusMap", wsmap);
                res.put("rawData", hexString2binaryString(hex));
                collector.collect(res.toString());
            }
        }).setParallelism(1);

        rdataStream.print();
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.3.32:9092", "streamInput2", new SimpleStringSchema()));

        see.execute();
    }
}
