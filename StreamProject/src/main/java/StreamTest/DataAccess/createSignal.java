package StreamTest.DataAccess;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.ArrayList;
import java.util.List;

public class createSignal {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);

        List<String> s = new ArrayList<>();

        for(Integer i = 0; i < 100; i++){
            JSONObject res2 = new JSONObject();
            res2.put("deviceID", i.toString());
            res2.put("timeStamp", System.currentTimeMillis());
            res2.put("workStatusMap", null);
            res2.put("rawData", null);
            res2.put("signal", true);
            //res2.put("proID", "ADC7F432DDA37943A48100AB32114567");//8890280F78ADFF488D5D466262764AD7
            //res2.put("filePath", "/home/ubuntu/Exportold.xml");
            res2.put("protID", "11C0256D09E8D5448C00D60687C63529");//8890280F78ADFF488D5D466262764AD7
            res2.put("filePath", "/home/ubuntu/engine/decode/11C0256D09E8D5448C00D60687C63529.xml");

            s.add(res2.toString());
        }

        //s.add(res.toString());
        DataStream<String> rdataStream = see.fromCollection(s);

        rdataStream.print();
        rdataStream.addSink(new FlinkKafkaProducer<String>("192.168.3.32:9092", "decodeSignal", new SimpleStringSchema()));
        //rdataStream.addSink(new FlinkKafkaProducer<String>("192.168.71.18:12092", "Signal", new SimpleStringSchema()));
        see.execute();
    }
}
