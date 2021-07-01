package StreamTest;

import StreamSink.RichRMQSink;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;

public class ReadRMQ {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        //env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 1000L));

        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost("192.168.3.32")
                .setPort(Integer.valueOf("5672"))
                .setUserName("qh")
                .setPassword("qh")
                .setVirtualHost("/")
                .build();
        RMQSource<String> rmqSource = new RMQSource<String>(connectionConfig, "mytest", new SimpleStringSchema());

        DataStream<String> rdataStream = env.addSource(rmqSource).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return s;
            }
        });

        rdataStream.print();

        RMQSource<String> rmqSource2 = new RMQSource<String>(connectionConfig, "mytest2", new SimpleStringSchema());

        DataStream<String> rdataStream2 = env.addSource(rmqSource2).map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "mt2:"+s;
            }
        });

        rdataStream2.print();


        rdataStream.addSink(new RichRMQSink<String>(connectionConfig, "mytest", new SimpleStringSchema()));

        env.execute();
    }
}
