package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.JsonList;
import StreamDataPacket.SubClassDataType.LogList;
import StreamProjectInit.StreamLog;
import cn.edu.thss.rcsdk.RealTimeAlg;
import com.alibaba.fastjson.JSONObject;
import edu.thss.entity.RawDataPacket;
import edu.thss.entity.TransPacket;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class UserAlgTimeControl {

    public static List<DataType> packetJsonResult(String projectID, RealTimeAlg rti, String taskID, Integer timeout,
                                                  RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput,
                                                  List<String> condition, Map<String, List<Map<String, String>>> taskvar,
                                                  List<TaskState> publicStateList, TaskState privateState, String topic) throws Exception{
        List<DataType> res = new ArrayList<DataType>();

        final ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<List<JSONObject>> call = new Callable< List<JSONObject>>(){
            public List<JSONObject> call() throws Exception{
                List<JSONObject> resJson = rti.callAlg(rawInput, transInput, jsonInput, condition, taskvar, publicStateList, privateState);
                return resJson;
            }
        };
        List<JSONObject> resJson = new ArrayList<>();
        try{
            Future< List<JSONObject>> future = exec.submit(call);
            resJson = future.get(timeout, TimeUnit.MILLISECONDS);
            if(resJson != null){
                for(int i = 0; i < resJson.size(); i++){
                    DataType resDataType = (DataType) new JsonList(resJson.get(i), taskID, topic);
                    res.add(resDataType);
                }
            }
        }catch(TimeoutException ex){
            String logStr = StreamLog.createLocalLog(ex, "ERROR", "用户算法运行超时，任务ID："+taskID, projectID);
            DataType logData = (DataType) new LogList(logStr);
            res.add(logData);
        }catch(Exception e){
            String logStr = StreamLog.createLocalLog(e, "ERROR", "用户算法运行异常，任务ID："+taskID, projectID);
            DataType logData = (DataType) new LogList(logStr);
            res.add(logData);
        }
        exec.shutdown();

        return res;
    }

    public static void overtimeTset()throws Exception{
        StreamExecutionEnvironment see = StreamExecutionEnvironment.getExecutionEnvironment();
        see.setParallelism(3);
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        DataStream<Integer> stream = see.fromCollection(list);

        DataStream<String> stream1 = stream.map(new RichMapFunction<Integer, String>() {
            private transient RealTimeAlg alg;
            @Override
            public String map(Integer integer) throws Exception {
                alg = new UserJarTest();

                Boolean bdStatus = false;
                final ExecutorService exec = Executors.newFixedThreadPool(1);
                Callable<String> call = new Callable<String>(){
                    public String call() throws Exception{
                        List<String> list = new ArrayList<>();
                        list.add(String.valueOf(integer));

                        Boolean x = alg.init(null, null, null, list, null, null, null);
                        return x.toString();
                        //return null;
                    }
                };
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                try{
                    Future<String> future = exec.submit(call);
                    String obj = future.get(1000, TimeUnit.MILLISECONDS);
                    bdStatus = Boolean.parseBoolean(obj);
                    System.out.println("the return value from call is :" + obj);

                }catch(TimeoutException ex){
                    System.out.println("====================task time out===============");
                    //ex.printStackTrace();
                    bdStatus = false;
                }catch(Exception e){

                    e.printStackTrace(pw);
                    System.out.println("sw: "+sw);
                    bdStatus = false;
                }finally {
                    pw.close();
                }
                exec.shutdown();

                return bdStatus.toString();
            }
        }).setParallelism(1);
        stream1.print();


        see.execute();
    }

    public static void main(String[] args) throws Exception {
        RealTimeAlg alg = new UserJarTest();
        List<DataType> list = packetJsonResult(null, alg, null, 1000, null, null, null, new ArrayList<>(), null, null, null, null);
        System.out.println(list);
    }
}
