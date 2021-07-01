package StreamTest.StorageTest;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.DBStore.DBCondition;
import StreamDataPacket.SubClassDataType.DBStore.DBSQL;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import StreamDataPacket.SubClassDataType.JsonList;
import StreamDataPacket.SubClassDataType.ParsedDataPacketList;
import StreamSink.HBaseSink;
import StreamSink.KafkaOverrideSerialization;

import com.alibaba.fastjson.JSONObject;
import edu.thss.entity.ParsedDataPacket;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class PDPIOTest {

    public static void pdptest() throws Exception{
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        ParsedDataPacket pdp = new ParsedDataPacket();
        pdp.addWorkStatus("test", new Date().getTime(), "xxx");
        ParsedDataPacketList pdpl = new ParsedDataPacketList(pdp, "d1");
        List<DataType> list = new ArrayList<>();
        list.add((DataType) pdpl);

        DataStream<DataType> dataStream = see.fromCollection(list);

        String brokerList = "192.168.3.32:9092";
        String topicId = "PDPdata1";
        Properties kafkaConfig = new Properties();
        kafkaConfig.put("bootstrap.servers", brokerList);
        kafkaConfig.put("max.block.ms", 5000);
        dataStream.addSink(new FlinkKafkaProducer<DataType>(topicId, new KafkaOverrideSerialization(), kafkaConfig));
        dataStream.print();
        see.execute();
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", "test");
        JsonList jsonList = new JsonList(jsonObject);
        List<DataType> list = new ArrayList<>();
        list.add((DataType) jsonList);

        DataStream<DataType> dataStream = see.fromCollection(list);

        String brokerList = "192.168.3.32:9092";
        String topicId = "ptest";
        Properties kafkaConfig = new Properties();
        kafkaConfig.put("bootstrap.servers", brokerList);
        kafkaConfig.put("max.block.ms", 5000);
        dataStream.addSink(new FlinkKafkaProducer<DataType>(topicId, new KafkaOverrideSerialization(), kafkaConfig));
        dataStream.print();
        see.execute();
    }
}
