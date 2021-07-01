package StreamTest.StorageTest;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.DBStore.DBCondition;
import StreamDataPacket.SubClassDataType.DBStore.DBSQL;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import StreamSink.HBaseSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HBaseSinkTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();

        DBCondition condition = new DBCondition("cf :b", "String", "sinkb0702x");
        DBCondition condition2 = new DBCondition("cf :a", "String", "sinka0702x");
        DBSQL dbsql = new DBSQL("1", new Date().getTime(), new ArrayList<>());
        dbsql.addtConditon(condition);
        dbsql.addtConditon(condition2);
        DBStore store = new DBStore("test", new ArrayList<>());
        store.addSql(dbsql);
        List<DataType> list = new ArrayList<>();
        list.add((DataType) store);

        DataStream<DataType> dataStream = see.fromCollection(list);

        dataStream.addSink(new HBaseSink("192.168.10.178:2182", "192.168.10.178", "ubuntu-kafka"));
        dataStream.print();
        see.execute();
    }
}
