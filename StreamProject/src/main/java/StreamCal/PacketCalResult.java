package StreamCal;

import StreamDataPacket.BaseClassDataType.TaskState;
import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.DBStore.DBStore;
import StreamDataPacket.SubClassDataType.JsonList;
import StreamDataPacket.SubClassDataType.LogList;
import StreamDataPacket.SubClassDataType.ParsedDataPacketList;
import StreamDataPacket.SubClassDataType.TransPacketList;
import StreamProjectInit.StreamLog;
import cn.edu.thss.rcsdk.RealTimeAlg;
import cn.edu.thss.rcsdk.StreamAlg;
import com.alibaba.fastjson.JSONObject;
import edu.thss.entity.ParsedDataPacket;
import ty.pub.RawDataPacket;
import ty.pub.TransPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class PacketCalResult {
    public static List<DataType> packetCalResult(String pid, String outputType, StreamAlg rti, String taskID, String deviceID,
                                                 Integer timeout, RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput,
                                                 List<String> condition, Map<String, List<Map<String, String>>> taskvar, Long time,
                                                 List<TaskState> publicStateList, TaskState privateState, String topic)throws Exception{

        if(outputType.equals("JSONObject")){
            return packetJsonResult(pid, rti, taskID, timeout, rawInput, transInput, jsonInput, condition, taskvar, publicStateList, privateState, topic, deviceID, time);
        }
        else if(outputType.equals("TransPacket")){
            return packetTransResult(pid, rti, taskID, timeout, rawInput, transInput, jsonInput, condition, taskvar, publicStateList, privateState, topic, deviceID, time);
        }
        else if(outputType.equals("DBStore") || outputType.equals("Table")){
            return packetDBStoreResult(pid, rti, taskID, timeout, rawInput, transInput, jsonInput, condition, taskvar, publicStateList, privateState, topic, deviceID, time);
        }
        else if(outputType.equals("ParsedDataPacket")){
            return packetDBStoreResult(pid, rti, taskID, timeout, rawInput, transInput, jsonInput, condition, taskvar, publicStateList, privateState, topic, deviceID, time);
        }
        return null;
    }

    public static List<DataType> packetJsonResult(String projectID, StreamAlg rti, String taskID, Integer timeout,
                                    RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput,
                                    List<String> condition, Map<String, List<Map<String, String>>> taskvar,
                                    List<TaskState> publicStateList, TaskState privateState, String topic,
                                                  String deviceID, Long time) throws Exception{
        List<DataType> res = new ArrayList<DataType>();

        final ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<List<JSONObject>> call = new Callable< List<JSONObject>>(){
            public List<JSONObject> call() throws Exception{
                List<JSONObject> resJson =
                        rti.callAlg(rawInput, transInput, jsonInput, condition, taskvar, deviceID, publicStateList, privateState, time);
                return resJson;
            }
        };
        List<JSONObject> resJson = new ArrayList<>();
        try{
            Future< List<JSONObject>> future = exec.submit(call);
            resJson = future.get(timeout, TimeUnit.MILLISECONDS);
            if(resJson != null){
                for(int i = 0; i < resJson.size(); i++){
                    JSONObject jsonObject = resJson.get(i);
                    if(jsonObject.containsKey("RCOutputTopic")){
                        DataType resDataType = (DataType) new JsonList(jsonObject.getJSONObject("RCRes"), taskID, jsonObject.getString("RCOutputTopic"));
                        res.add(resDataType);
                    }
                    else{
                        DataType resDataType = (DataType) new JsonList(resJson.get(i), taskID, topic);
                        res.add(resDataType);
                    }
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

    public static List<DataType> packetTransResult(String projectID, StreamAlg rti, String taskID, Integer timeout,
                                    RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput,
                                    List<String> condition, Map<String, List<Map<String, String>>> taskvar,
                                    List<TaskState> publicStateList, TaskState privateState, String topic,
                                                   String deviceID, Long time)throws Exception{
        List<DataType> res = new ArrayList<DataType>();

        final ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<List<TransPacket>> call = new Callable< List<TransPacket>>(){
            public List<TransPacket> call() throws Exception{
                List<TransPacket> resTrans = rti.callAlg(rawInput, transInput, jsonInput, condition, taskvar, deviceID, publicStateList, privateState, time);
                return resTrans;
            }
        };
        List<TransPacket> resTrans = new ArrayList<>();
        try{
            Future< List<TransPacket>> future = exec.submit(call);
            resTrans = future.get(timeout, TimeUnit.MILLISECONDS);
            if(resTrans != null){
                for(int i = 0; i < resTrans.size(); i++){
                    TransPacket transPacket = resTrans.get(i);
                    if(transPacket.getBaseInfoMap().containsKey("RCOutputTopic")){
                        DataType resDataType = (DataType) new TransPacketList(resTrans.get(i), taskID, transPacket.getBaseInfoMap().get("RCOutputTopic"));
                        res.add(resDataType);
                    }
                    else{
                        DataType resDataType = (DataType) new TransPacketList(resTrans.get(i), taskID, topic);
                        res.add(resDataType);
                    }

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

    public static List<DataType> packetDBStoreResult(String projectID, StreamAlg rti, String taskID, Integer timeout,
                                                   RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput,
                                                   List<String> condition, Map<String, List<Map<String, String>>> taskvar,
                                                   List<TaskState> publicStateList, TaskState privateState, String topic,
                                                     String deviceID, Long time)throws Exception{
        List<DataType> res = new ArrayList<DataType>();

        final ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<List<DBStore>> call = new Callable< List<DBStore>>(){
            public List<DBStore> call() throws Exception{
                List<DBStore> resTrans = rti.callAlg(rawInput, transInput, jsonInput, condition, taskvar, deviceID, publicStateList, privateState, time);
                return resTrans;
            }
        };
        List<DBStore> resTrans = new ArrayList<>();
        try{
            Future< List<DBStore>> future = exec.submit(call);
            resTrans = future.get(timeout, TimeUnit.MILLISECONDS);
            if(resTrans != null){
                for(int i = 0; i < resTrans.size(); i++){
                    DataType resDataType = (DataType) resTrans.get(i);
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

    public static List<DataType> packetPDPResult(String projectID, StreamAlg rti, String taskID, Integer timeout,
                                                     RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput,
                                                     List<String> condition, Map<String, List<Map<String, String>>> taskvar,
                                                     List<TaskState> publicStateList, TaskState privateState, String topic,
                                                 String deviceID, Long time)throws Exception{
        List<DataType> res = new ArrayList<DataType>();

        final ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<List<ParsedDataPacket>> call = new Callable< List<ParsedDataPacket>>(){
            public List<ParsedDataPacket> call() throws Exception{
                List<ParsedDataPacket> resTrans = rti.callAlg(rawInput, transInput, jsonInput, condition, taskvar, deviceID, publicStateList, privateState, time);
                return resTrans;
            }
        };
        List<ParsedDataPacket> resTrans = new ArrayList<>();
        try{
            Future< List<ParsedDataPacket>> future = exec.submit(call);
            resTrans = future.get(timeout, TimeUnit.MILLISECONDS);
            if(resTrans != null){
                for(int i = 0; i < resTrans.size(); i++){
                    ParsedDataPacket pdp = resTrans.get(i);
                    if(pdp.getBaseInfoMap().containsKey("RCOutputTopic")){
                        DataType resDataType = (DataType) new ParsedDataPacketList(resTrans.get(i), taskID, pdp.getBaseInfoMap().get("RCOutputTopic"));;
                        res.add(resDataType);
                    }
                    else{
                        DataType resDataType = (DataType) new ParsedDataPacketList(resTrans.get(i), taskID, topic);;
                        res.add(resDataType);
                    }

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
}
