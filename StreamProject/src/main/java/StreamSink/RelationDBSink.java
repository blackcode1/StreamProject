package StreamSink;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.DBStore.DBCondition;
import StreamDataPacket.SubClassDataType.DBStore.DBSQL;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class RelationDBSink extends RichSinkFunction<DataType> {
    String ip;
    String dbName;
    String user;
    String password;
    String type;
    Connection conn;
    Statement statement;

    public RelationDBSink(String ip, String dbName, String user, String password, String type) {
        this.ip = ip;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        this.type = type;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        String driverName=null;
        String dbURL=null;
        if(type.equals("POSTGRESQL")){
            driverName = "org.postgresql.Driver";
            dbURL = "jdbc:postgresql://" + ip + "/" + dbName;
        }
        else if(type.equals("SQLSERVER")){
            driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            dbURL = "jdbc:sqlserver://" + ip + ";DatabaseName=" + dbName;
        }
        else if(type.equals("MYSQL")){
            driverName = "com.mysql.jdbc.Driver";
            dbURL = "jdbc:mysql://" + ip + "/" + dbName;
        }
        Class.forName(driverName);
        conn = DriverManager.getConnection(dbURL, user, password);
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
            String sql4 = "update " + dbStore.getTableName() + " set ";
            for(DBSQL dbsql : dbStore.getSqls()){
                String mainkey = dbsql.getRowKey();
                String miankeyV = null;
                String sql1 = sql;
                String sql2 = " (";
                String sql3 = ") values(" ;
                int count = 0;
                for(DBCondition condition :dbsql.getConditons()){
                    String colname = condition.getColName();
                    String type = condition.getDataType();
                    String val = condition.getValue();
                    if(type.equals("String")){
                        val = "'" + val + "'";
                    }
                    if(colname == mainkey){
                        miankeyV = val;
                    }
                    if(count == 0){
                        sql2 = sql2 + colname;
                        sql3 = sql3 + val;
                        sql4 = sql4 + colname + " = " + val;
                    }
                    else{
                        sql2 = sql2 + ", " + colname;
                        sql3 = sql3 + ", " + val;
                        sql4 = sql4 + "," + colname + " = " + val;
                    }
                    count = count + 1;
                }
                String s = sql1 + sql2 + sql3 + ")";
                sql4 = sql4 + " where " + mainkey + " = " + miankeyV;

                try {
                    statement.execute(s);
                }catch (Exception e){
                    statement.execute(sql4);
                }
            }
        }
    }
}
