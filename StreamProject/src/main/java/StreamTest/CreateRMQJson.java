package StreamTest;

import StreamSink.RichRMQSink;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRMQJson {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 1000L));

        JSONObject res = new JSONObject();
        res.put("deviceID", "1001265107");
        res.put("timeStamp", System.currentTimeMillis());
        res.put("workStatusMap",  new HashMap<String, Map<String, String>>());

        //res.put("exchange", "amp.test");
        //res.put("routeKey", "mytest");
        //res.put("resStr", res.toString());
        //res.put("delay", 1000);


        List<String> list = new ArrayList<>();
        list.add(res.toString());

        DataStream<String> rdataStream = env.fromCollection(list);

        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost("192.168.3.32")
                .setPort(Integer.valueOf("5672"))
                .setUserName("qh")
                .setPassword("qh")
                .setVirtualHost("/")
                .build();

        rdataStream.addSink(new RichRMQSink<String>(connectionConfig, "input1", new SimpleStringSchema()));

        rdataStream.print();

        env.execute();
    }
}
