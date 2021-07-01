package StreamTest;

import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketSerializationSchema;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import ty.pub.TransPacket;

import java.util.HashMap;
import java.util.Map;

public class CreatLSNTrans {


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> rdataStream = see.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                sourceContext.collect("0");
            }

            @Override
            public void cancel() {

            }
        });

        DataStream<TransPacket> resStream = rdataStream.flatMap(new RichFlatMapFunction<String, TransPacket>() {

            private  transient  Integer count;

            public void open(Configuration parameters) throws Exception {
                count = 0;
                //this.producer = new FlinkKafkaInternalProducer(this.producerConfig);
            }
            @Override
            public void flatMap(String s, Collector<TransPacket> collector) throws Exception {
                Integer deviceID = 1001265107;


                TransPacket res = new TransPacket();
                    //Json:{String dataID, Long timestamp, Map<String gk, Map<Long key, String value>> workStatusMap}

                res.setTimestamp(System.currentTimeMillis());
                    //res.put("timeStamp", System.currentTimeMillis());
                Map<String, Map<Long, String>> workStatusMap = new HashMap<String, Map<Long, String>>();

                res.setDeviceId(String.valueOf(deviceID));
                Map<Long, String> gkMap = new HashMap<Long, String>();
                gkMap.put(0L, s);
                workStatusMap.put("engineState", gkMap);

                System.out.println(res.getDeviceId()+workStatusMap);
                res.setWorkStatusMap(workStatusMap);
                collector.collect(res);

            }
        });

        rdataStream.print();
        resStream.addSink(new FlinkKafkaProducer<TransPacket>
                ("192.168.10.178:9092", "FlinkTest1", new TransPacketSerializationSchema()));

        see.execute();
    }
}
