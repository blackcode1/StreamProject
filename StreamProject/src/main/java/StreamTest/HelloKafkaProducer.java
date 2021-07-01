package StreamTest;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class HelloKafkaProducer {

    public static void main(String[] args) {
        //TODO 生产者三个属性必须指定(broker地址清单、key和value的序列化器)
        Properties properties = new Properties();
        properties.put("bootstrap.servers","192.168.10.178:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties);
        try {
            ProducerRecord<String,String> record;
            try {
                //TODO 发送4条消息
                BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\12905\\Desktop\\data.csv"));
                String line = bufferedReader.readLine();
                int index = 0;
                while ((line=bufferedReader.readLine())!=null){
                    Thread.sleep(100);
                    JSONObject data = new JSONObject();
                    String[] item = line.split(",");
                    Map<String, Map<String, String>> workStatusMap = new HashMap<String, Map<String, String>>();
                    Map<String,String> time = new HashMap<>(); time.put("0",item[0]);
                    Map<String,String> value = new HashMap<>(); value.put("0",item[1]);
                    workStatusMap.put("time",time);
                    workStatusMap.put("value",value);
//                    String[] item = line.split(",");
//                    jsonInput.put("time",item[0]);
//                    jsonInput.put("value",item[1]);
                    data.put("workStatusMap",workStatusMap);
                    data.put("deviceID","dqty");
                    data.put("timeStamp", System.currentTimeMillis());
                    //data.put("taskvar",config);
                    record = new ProducerRecord<String,String>("FlinkTest1", null,data.toJSONString());
                    producer.send(record);//发送并发忘记（重试会有）
                    System.out.println(data);
                    System.out.println(++index +"，message is sent");
                }
//                Map<String, List<Map<String, String>>> config = new HashMap<>();
//                Map<String, String> windowParameter = new HashMap<>();
//                windowParameter.put("interval","10");
//                windowParameter.put("triggerTime","20");
//                windowParameter.put("timeIndex","0");
//                List<Map<String, String>> windowList = new ArrayList<>();
//                windowList.add(windowParameter);
//                config.put("mywindow",windowList);
//                Map<String,String> scheduleParameter = new HashMap<>();
//                scheduleParameter.put("transformation","interval-origin");
//                scheduleParameter.put("detect","sigma:null-sigma:null");
//                scheduleParameter.put("statistics","count,mean,std-count,mean,std");
//                scheduleParameter.put("scheduleInterval","5000");
//                List<Map<String, String>> scheduleList = new ArrayList<>();
//                scheduleList.add(scheduleParameter);
//                config.put("myschedule",scheduleList);
//                while ((line=bufferedReader.readLine())!=null){
//                    Thread.sleep(100);
//                    JSONObject data = new JSONObject(),jsonInput = new JSONObject();
//                    String[] item = line.split(",");
//                    jsonInput.put("time",item[0]);
//                    jsonInput.put("value",item[1]);
//                    data.put("jsonInput",jsonInput);
//                    data.put("deviceID","p1");
//                    data.put("taskID","t21");
//                    data.put("taskvar",config);
//                    record = new ProducerRecord<String,String>(BusiConst.HELLO_TOPIC, null,data.toJSONString());
//                    producer.send(record);//发送并发忘记（重试会有）
//                    System.out.println(++index +"，message is sent");
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            producer.close();
        }
    }


}
