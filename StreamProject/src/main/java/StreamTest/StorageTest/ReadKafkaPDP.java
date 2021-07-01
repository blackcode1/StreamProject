package StreamTest.StorageTest;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.ParsedDataPacketList;
import StreamInput.GetInputStream.KafkaSource;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

public class ReadKafkaPDP {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);

        DataStream<DataType> kafkaSource = KafkaSource.getKafkaStream(see, "ParsedDataPacket",
                "192.168.3.32", "9092", "qz", "PDPdata1", "x", "y", 0);

        DataStream<String> resS = kafkaSource.map(new MapFunction<DataType, String>() {
            @Override
            public String map(DataType dataType) throws Exception {
                ParsedDataPacketList pdpl = (ParsedDataPacketList) dataType;
                return pdpl.streamData.toJson().toString();
            }
        });
        kafkaSource.print();
        resS.print();



        see.execute();

    }
}
