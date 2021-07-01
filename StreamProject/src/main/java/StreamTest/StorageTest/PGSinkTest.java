package StreamTest.StorageTest;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.DBStore.DBCondition;
import StreamDataPacket.SubClassDataType.DBStore.DBSQL;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import StreamSink.RelationDBSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

public class PGSinkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DBCondition condition = new DBCondition("failureinfo", "String", "Charging valve failing");
        DBCondition condition2 = new DBCondition("id", "String", "0");
        DBSQL dbsql = new DBSQL("id", null, new ArrayList<>());
        dbsql.addtConditon(condition);
        dbsql.addtConditon(condition2);
        DBStore store = new DBStore("FailureModel_SF", new ArrayList<>());
        store.addSql(dbsql);
        List<DataType> list = new ArrayList<>();
        list.add((DataType) store);

        DataStream<DataType> dataStream = see.fromCollection(list);

        dataStream.addSink(new RelationDBSink("192.168.3.33", "postgres","postgres", "123456", "POSTGRESQL"));
        dataStream.print();
        see.execute();
    }
}
