package StreamTest.StorageTest;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.DBStore.DBCondition;
import StreamDataPacket.SubClassDataType.DBStore.DBSQL;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import StreamSink.IotDBBatchSink;
import StreamSink.RelationDBSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class IOTDBSinkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        Long time = 1600307387410L;
        DBStore store = new DBStore("root.QD2000.Q200.Head", new ArrayList<>());
        DBSQL dbsql = new DBSQL("Carriage12", time, new ArrayList<>());
        dbsql.addtConditon(new DBCondition("U16AS2Pressure", "Double", "517"));
        store.addSql(dbsql);
        /*
        DBStore store = new DBStore("root.QD2000.Q200.Head", new ArrayList<>(), true);
        Long time = 1600307387410L;
        for(int i = 0; i < 5; i++){
            DBCondition condition2 = new DBCondition("U16CvPressure", "Double", String.valueOf(50+i));
            DBSQL dbsql = new DBSQL("Carriage12", time+i, new ArrayList<>());
            dbsql.addtConditon(condition2);
            if(i % 2 == 0){
                DBCondition condition = new DBCondition("U16AS2Pressure", "Double", String.valueOf(40+i));
                dbsql.addtConditon(condition);
            }
            store.addSql(dbsql);
        }*/
        List<DataType> list = new ArrayList<>();
        list.add((DataType) store);

        DataStream<DataType> dataStream = see.fromCollection(list);

        dataStream.addSink(new IotDBBatchSink("192.168.3.31:6667","root", "root"));
        dataStream.print();
        see.execute();
    }
}
