package StreamTest;

import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketSerializationSchema;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import ty.pub.TransPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatADDTrans {

    public static int getRandInt(Integer max){
        return (int) (Math.random()*max);
    }

    public static String getRandStr(Integer max){
        return String.valueOf(getRandInt(max));
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        List<String> list = new ArrayList<>();
        list.add("1001265107");
        DataStream<String> rdataStream = see.fromCollection(list);
        see.setParallelism(1);

        DataStream<TransPacket> resStream = rdataStream.flatMap(new RichFlatMapFunction<String, TransPacket>() {

            private  transient  Integer count;

            public void open(Configuration parameters) throws Exception {
                count = 0;
                //this.producer = new FlinkKafkaInternalProducer(this.producerConfig);
            }
            @Override
            public void flatMap(String s, Collector<TransPacket> collector) throws Exception {

                String[] deviceInfo = s.split(",");
                Integer deviceID = 1001265107;

                for(Integer i = 0; i < 4; i++){
                    TransPacket res = new TransPacket();
                    //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}

                    res.setTimestamp(System.currentTimeMillis());
                    //res.put("timeStamp", System.currentTimeMillis());
                    Map<String, Map<Long, String>> workStatusMap = new HashMap<String, Map<Long, String>>();

                    res.setDeviceId(String.valueOf(deviceID+i));
                    Map<Long, String> gkMap = new HashMap<Long, String>();
                    gkMap.put(0L, String.valueOf(count));
                    workStatusMap.put("0", gkMap);

                    System.out.println(res.getDeviceId()+workStatusMap);
                    res.setWorkStatusMap(workStatusMap);
                    collector.collect(res);
                }
                count = count + 1;
            }
        });

        rdataStream.print();
        resStream.addSink(new FlinkKafkaProducer<TransPacket>
                ("192.168.10.178:9092", "Flink1", new TransPacketSerializationSchema()));

        see.execute();
    }
}
