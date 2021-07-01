package StreamSink;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.DBStore.DBCondition;
import StreamDataPacket.SubClassDataType.DBStore.DBSQL;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import io.leopard.javahost.JavaHost;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.*;

public class HBaseSink extends RichSinkFunction<DataType> {
    String ip;
    String virtualDns;
    String virtualDnsName;
    Connection conn;

    public HBaseSink(String ip, String virtualDns, String virtualDnsName) {
        this.ip = ip;
        this.virtualDns = virtualDns;
        this.virtualDnsName = virtualDnsName;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        Properties props = new Properties();
        props.put(this.virtualDnsName, this.virtualDns);
        JavaHost.updateVirtualDns(props);

        org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", this.ip.split(":")[0]);
        config.set("hbase.zookeeper.property.clientPort", this.ip.split(":")[1]);
        HBaseAdmin.available(config);
        this.conn = ConnectionFactory.createConnection(config);

    }

    @Override
    public void close() throws Exception {
        super.close();
        if (conn != null) {
            conn.close();
        }
    }

    @Override
    public void invoke(DataType value, Context context) throws Exception {
        if (value.streamDataType.equals("DBStore")) {
            DBStore dbStore = (DBStore) value;
            Table table = this.conn.getTable(TableName.valueOf(dbStore.getTableName()));
            for (DBSQL dbsql : dbStore.getSqls()) {
                Put put1 = new Put(Bytes.toBytes(dbsql.getRowKey()), dbsql.getTimeStamp());
                for (DBCondition condition : dbsql.getConditons()) {
                    String colname = condition.getColName();
                    String val = condition.getValue();
                    put1.addColumn(Bytes.toBytes(colname.split(":")[0]), Bytes.toBytes(colname.split(":")[1]), Bytes.toBytes(val));
                }
                table.put(put1);
            }
            table.close();
        }
    }

}