package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import cn.edu.thss.rcsdk.RealTimeAlg;
import com.alibaba.fastjson.JSONObject;
//import rtcf.test.failurediag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class readIotdb {

    public static void main(String[] args) throws Exception {
        Class.forName("org.apache.iotdb.jdbc.IoTDBDriver");
        Connection conn = DriverManager.getConnection("jdbc:iotdb://192.168.3.31:6667/", "root", "root");

        String sql = "select U16AS1Pressure,U16AS2Pressure,U16MRorPBPressure from root.QD2000.Q100.Head.Carriage22 where time = 1596525088";

        PreparedStatement statement = (PreparedStatement) conn.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();

        /*ResultSetMetaData metaData = rs.getMetaData();
        int col = metaData.getColumnCount();
        for (int i = 1; i <= col; ++i) {
            String columnName = metaData.getColumnName(i);
            System.out.println(columnName);
        }*/

        List<List<String>> list = new ArrayList<>();
        while (rs.next()) {
            List<String> line = new ArrayList<>();
            line.add(rs.getString(1));
            line.add(rs.getString(2));
            line.add(rs.getString(3));
            line.add(rs.getString(4));
            list.add(line);
            System.out.println(rs.getString(1) + " " + rs.getString(2)+ " " + rs.getString(3)+ " " + rs.getString(4));
        }

        TaskState taskState = new TaskState();
        /*RealTimeAlg failureDaig = new failurediag();
        failureDaig.init(null, null, null, null, null, null, taskState);
        JSONObject jsonInput = new JSONObject();
        Long starttime = 1596525088L;
        for(List<String> line : list){
            Long time = Long.parseLong(line.get(0));
            if(time - starttime > 30*1000L){
                List<JSONObject> resJson = failureDaig.callAlg(null, null, jsonInput, null, null, null, taskState);
                System.out.println(resJson);
                jsonInput = new JSONObject();
                starttime = starttime + 30*1000L;
                Thread.sleep(30*1000L);
            }
            else {
                //jsonInput.put("workstatusmap", Map);
            }
        }*/
    }
}
