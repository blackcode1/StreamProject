package StreamTest;

import StreamSink.RichRMQSink;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RMQSinkTest {
    public static String queueNameGet="input1";

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 1000L));

        JSONObject res = new JSONObject();
        res.put("deviceID", "1001265107");
        res.put("timeStamp", System.currentTimeMillis());
        res.put("workStatusMap",  new HashMap<String, Map<String, String>>());

        JSONObject res2 = new JSONObject();
        //res2.put("deviceID", "1111265107");
        //res2.put("timeStamp", System.currentTimeMillis());
        //res2.put("workStatusMap",  new HashMap<String, Map<String, String>>());
        res2.put("exchange", "amp.test");
        res2.put("routeKey", "mytest");
        res2.put("delay", 1000);
        res2.put("resStr", "hhh"+res.toString());

        List<String> list = new ArrayList<>();
        list.add(res.toString());
        list.add(res2.toString());

        DataStream<String> rdataStream = env.fromCollection(list);

        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost("192.168.3.32")
                .setPort(Integer.valueOf("5672"))
                .setUserName("qh")
                .setPassword("qh")
                .setVirtualHost("/")
                .build();

        Map<String, Object> delayHeaders = new HashMap<String, Object>();
        delayHeaders.put("x-delay", 10000);
        AMQP.BasicProperties.Builder delayProps = new AMQP.BasicProperties.Builder().headers(delayHeaders);
        MyRMQSinkOpts<String> myRMQSinkOpts = new MyRMQSinkOpts<>("mytest2", "", "JSONObject", null);
        rdataStream.addSink(new RichRMQSink<String>(connectionConfig, new SimpleStringSchema(), myRMQSinkOpts));
        //rdataStream.addSink(new RichRMQSink<String>(connectionConfig, "input1", new SimpleStringSchema()));
        //rdataStream.addSink(new RMQSink1());

        rdataStream.print();

        env.execute();
    }
}
