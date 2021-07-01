package StreamTest;

import StreamDataPacket.SubClassDataType.JsonList;
import StreamDataPacket.DataType;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class ReadByDataset {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        String ip = "172.16.244.51";
        String port = "9092";
        String gropID = "qz0703";
        String topicName = "FlinkTestJsonSource2";
        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", ip+":"+port);
        kafkaConfig.setProperty("group.id", gropID);
        kafkaConfig.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<String>(topicName, new SimpleStringSchema(), kafkaConfig);
        kafkaConsumer.setStartFromEarliest();
        DataStream<String> k = see.addSource(kafkaConsumer);
        DataStream<DataType> kafkaSource = k.map(new MapFunction<String, DataType>() {
            @Override
            public DataType map(String s) throws Exception {
                System.out.println("s:"+s);
                JSONObject jo = JSONObject.parseObject(s);
                System.out.println("jo:"+jo);
                DataType jl = (DataType) new JsonList(jo);
                System.out.println("jl:"+jl);
                return jl;
            }
        });
        //k.print();
        kafkaSource.print();

        see.execute();

    }
}
