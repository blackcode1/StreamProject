package StreamTest.StorageTest;

import io.leopard.javahost.JavaHost;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Properties;

public class ReadHBase {

    private static Connection connection = null;

    public static void getResult(TableName tableName, String rowKey) throws Exception{
        Table table = connection.getTable(tableName);
        //获得一行
        Get get = new Get(Bytes.toBytes(rowKey));
        Result set = table.get(get);
        Cell[] cells = set.rawCells();
        for (Cell cell: cells){
            System.out.println(Bytes.toString(cell.getQualifierArray()));
            System.out.println(Bytes.toString(cell.getFamilyArray()));

            System.out.println(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()) + "::" +
                    Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
        }
        table.close();
    }

    public static void main(String args[]){

        try {
            System.out.println("yes 1");
            Properties props = new Properties();
            props.put("ubuntu-kafka", "192.168.10.178");
            JavaHost.updateVirtualDns(props);
            Configuration config = HBaseConfiguration.create();
            config.set("hbase.zookeeper.quorum", "192.168.10.178");
            config.set("hbase.zookeeper.property.clientPort", "2182");
            HBaseAdmin.available(config);
            connection = ConnectionFactory.createConnection(config);
            System.out.println("after connection " + connection);// + " " + connection.getConfiguration());
            getResult(TableName.valueOf("test"), "1");
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
