package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.*;

public class CreateSimpleKafkaJson {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        List<String> list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timeStamp", new Date());
        Map<String, Map<String, String>> workStatusMap = new HashMap<>();
        Map<String, String> gkmap = new HashMap<>();
        gkmap.put("0", "2019/9/23 11:44:34");
        workStatusMap.put("time", gkmap);
        Map<String, String> gkmap2 = new HashMap<>();
        gkmap2.put("0", "75.078");
        workStatusMap.put("value", gkmap2);
        jsonObject.put("workStatusMap", workStatusMap);
        jsonObject.put("deviceID", "dqty");
        jsonObject.put("taskID", "dqty");
        list.add(jsonObject.toString());

        DataStream<String> rdataStream = see.fromCollection(list);
        see.setParallelism(1);
        rdataStream.print();
        rdataStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.3.32:9092", "FlinkSource0", new SimpleStringSchema()));

        see.execute();
    }
}
