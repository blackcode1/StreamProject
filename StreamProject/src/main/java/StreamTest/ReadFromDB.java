package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadFromDB {
    private static String driverName="org.postgresql.Driver";
    private static final String dbURL="jdbc:postgresql://192.168.3.33/postgres";
    private static final String userName="postgres";
    private static final String userPwd="123456";
    private static final String query="select * from failuremodel_sf";//1001179196

    public static List<Map<String, String>> getTaskVar(String driverName,String dbURL,String userName,String userPwd,String query) throws SQLException {
        Connection dbConn=null;
        try {
            Class.forName(driverName);
            System.out.println("加载驱动成功！");
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("加载驱动失败！");
        }
        try{
            dbConn=DriverManager.getConnection(dbURL,userName,userPwd);
            System.out.println("连接数据库成功！");
        }catch(Exception e) {
            e.printStackTrace();
            System.out.print("数据库连接失败！");
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = dbConn.createStatement();
            //stmt.execute("update failuremodel_sf set ebro = 't' where id = '0'");
            rs = stmt.executeQuery(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Map<String, String>> resList = new ArrayList<Map<String, String>>();
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        while(true){
            try {
                if (!rs.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //将获取的数据存入vclinfos中
            Map<String, String> rowData = new HashMap<String, String>();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getString(i));
            }
            resList.add(rowData);
        }
        if(rs != null){   // 关闭记录集
            try{ rs.close() ; }catch(SQLException e){ e.printStackTrace() ; }
        }
        if(stmt != null){   // 关闭声明
            try{ stmt.close() ; }catch(SQLException e){ e.printStackTrace() ; }
        }
        if(dbConn != null){  // 关闭连接对象
            try{ dbConn.close() ; }catch(SQLException e){ e.printStackTrace() ; }
        }
        return resList;
    }


    public static void main(String[] args) throws Exception {
        List<Map<String, String>> dbData = getTaskVar(driverName, dbURL, userName, userPwd, query);
        for(Map<String, String> map : dbData){
            //Boolean s = Boolean.valueOf(map.get("ebro"));
           // Long t = Long.parseLong(s);
            //System.out.println(s);
        }
        System.out.println(dbData);
    }
}
