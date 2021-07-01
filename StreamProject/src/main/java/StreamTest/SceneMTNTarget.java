package StreamTest;

import StreamDataSource.KafkaSource;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/*
VclId               KmID    LastKM  SpaceKM FloatKM
1001150509	40	10	2	3	105528	50000	500
1001150509	41	10	2	3	105528	100000	1000
1001149042	40	10	2	3	0	50000	500
1001149042	41	10	2	3	0	100000	1000
1001149043	40	10	2	3	0	50000	500
1001149043	41	10	2	3	0	100000	1000
1001149044	40	10	2	3	0	50000	500
1001149044	41	10	2	3	0	100000	1000
1001165261	57	17	1	0	20554	20000	500
1001165262	57	17	1	0	19325	20000	500
1001165759	57	17	5	0	9971	20000	500
1001148921	31	9	5	0	181854	75000	500
1001165668	57	17	3	0	11094	20000	500
1001165670	57	17	3	0	12182	20000	500
 */

public class SceneMTNTarget {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);

        Properties kafkaConfig = new Properties();
        kafkaConfig.setProperty("bootstrap.servers", "192.168.10.178:9092");
        kafkaConfig.setProperty("group.id", "qz0703");
        kafkaConfig.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConfig.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        FlinkKafkaConsumer<String> kafkaConsumer = new
                FlinkKafkaConsumer<String>("FlinkTarget1", new SimpleStringSchema(), kafkaConfig);
        //kafkaConsumer.setStartFromEarliest();
        DataStream<String> kafkaSource = see.addSource(kafkaConsumer);
        kafkaSource.print();
        kafkaSource.writeAsText("C:\\Users\\12905\\Desktop\\mtn_result.txt", FileSystem.WriteMode.OVERWRITE);
        see.execute();

    }
}
