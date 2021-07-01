package StreamTest.StorageTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

public class CreateSignal {

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
                res.put("workStatusMap", null);
                res.put("rawData", null);
                res.put("signal", true);
                res.put("filePath", "/home/ubuntu/engine/decode/2068005E0096B943B3E3AAF6E7E86791.xml");
                //res.put("filePath", "C:/Users/12905/Desktop/Export.xml");
                collector.collect(res.toString());
            }
        }).setParallelism(1);

        rdataStream.print();
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.3.32:9092", "decodeSignal", new SimpleStringSchema()));

        see.execute();
    }
}
