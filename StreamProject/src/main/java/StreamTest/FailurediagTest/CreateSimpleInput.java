package StreamTest.FailurediagTest;

import edu.thss.entity.ParsedDataPacket;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateSimpleInput {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(1);

        List<String> list = new ArrayList<>();
        list.add("0");
        list.add("10000");//等待10s，或者直接发送一个10s后的数据包
        DataStream<String> rdataStream = see.fromCollection(list);

        DataStream<String> resStream = rdataStream.flatMap(new FlatMapFunction<String, String>() {

            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                Long ctime = new Date().getTime();
                ctime = ctime + Long.valueOf(s);
                ParsedDataPacket pdp = new ParsedDataPacket();
                pdp.setLine("BJ8T00");
                //pdp.setDeviceID("test4");
                pdp.setCar("H411");
                pdp.addBaseInfo("TP_LineNumber", "BJ8T00");
                pdp.addBaseInfo("TrainrNumber", "H411");
                pdp.addBaseInfo("TP_HeadOrTail", "Head");
                pdp.addBaseInfo("TP_Timestamp", ctime.toString());
                for(int i = 0; i < 10; i++){
                    Long time = Long.valueOf((int) (i * 102.4));
                    pdp.addWorkStatus("1.BI11Value_IB02A", time, "1");//紧急制动信号
                    pdp.addWorkStatus("1.CvPressureSensorLBtx", time, "0");//预控压力信号
                    pdp.addWorkStatus("1.BC_PressureBtx", time, "0");//制动缸压力信号
                    pdp.addWorkStatus("1.BC_P_TargetBtx", time, "17");//制动缸压力目标值
                }

                collector.collect(pdp.toJson().toString());
            }
        });
        resStream.print();
        resStream.addSink(new FlinkKafkaProducer<String>
                ("192.168.3.32:9092", "teststoreb", new SimpleStringSchema()));
        see.execute();
    }
}
