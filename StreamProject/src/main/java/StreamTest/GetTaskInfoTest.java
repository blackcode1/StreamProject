package StreamTest;

import StreamInput.GetBroadStream.GetTaskInfo;
import StreamInput.GetBroadStream.GetTaskVar;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


public class GetTaskInfoTest {
    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);

            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            /*
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
             */
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String s = GetTaskInfo.sendGet("http://192.168.71.19:2080/activatedtasks?id=3D4FC28E3560B44182C3A5DCC95CF9DB");
        JSONObject taskInfo = JSONObject.parseObject(s);
        JSONArray inputDatasetList = taskInfo.getJSONArray("InputDataSetList");
        for(int i = 0; i < inputDatasetList.size(); i++) {
            JSONObject inputDataset = inputDatasetList.getJSONObject(i);

            List<Map<String, String>> onetable = GetTaskVar.getTaskVarByJson(inputDataset);
            System.out.println(onetable);
        }
        //System.out.println(taskInfo);
        System.out.println(s);

    }

}
