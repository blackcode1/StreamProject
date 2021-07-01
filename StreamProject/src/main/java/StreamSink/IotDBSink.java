package StreamSink;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.*;
import StreamDataPacket.SubClassDataType.DBStore.DBCondition;
import StreamDataPacket.SubClassDataType.DBStore.DBSQL;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IotDBSink extends RichSinkFunction<DataType> {
    String ip;
    String user;
    String password;
    Connection conn;
    Statement statement;

    public IotDBSink(String ip, String user, String password) {
        this.ip = ip;
        this.user = user;
        this.password = password;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        Class.forName("org.apache.iotdb.jdbc.IoTDBDriver");
        conn = DriverManager.getConnection("jdbc:iotdb://"+ip+"/", user, password);
        statement = conn.createStatement();
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
        if(value.streamDataType.equals("DBStore")){
            DBStore dbStore = (DBStore) value;
            String sql = "insert into "+dbStore.getTableName();
            for(DBSQL dbsql : dbStore.getSqls()){
                String sql1 = sql;
                if(dbsql.getRowKey() != null){
                    sql1 = sql1 + "." + dbsql.getRowKey();
                }
                String sql2 = "(timestamp";
                String sql3 = ") values(" + dbsql.getTimeStamp();
                for(DBCondition condition :dbsql.getConditons()){
                    String colname = condition.getColName();
                    String type = condition.getDataType();
                    String val = condition.getValue();
                    if(type.equals("String")){
                        val = "\"" + val + "\"";
                    }
                    sql2 = sql2 + ", " + colname;
                    sql3 = sql3 + ", " + val;
                }
                String s = sql1 + sql2 + sql3 + ")";
                statement.execute(s);
            }
        }
    }
}
