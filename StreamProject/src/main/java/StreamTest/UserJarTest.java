package StreamTest;

import StreamDataPacket.BaseClassDataType.TaskState;
import cn.edu.thss.rcsdk.RealTimeAlg;
import com.alibaba.fastjson.JSONObject;
import edu.thss.entity.RawDataPacket;
import edu.thss.entity.TransPacket;


import java.util.*;

public class UserJarTest extends RealTimeAlg<JSONObject> {
    public Boolean init(RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput,
                        List<String> condition,
                        Map<String, List<Map<String, String>>> config,
                        List<TaskState> publicState,
                        TaskState privateState)throws Exception{
        if(condition.get(0).equals("1")){

            return true;
        }
        else if(condition.get(0).equals("2")){
            return false;
        }
        else if(condition.get(0).equals("3")){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
        else if(condition.get(0).equals("4")){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }
        else if(condition.get(0).equals("5")){
            String x = condition.get(2);
            return false;
        }
        if(config == null || !config.containsKey("task_dset_1")){
            return false;
        }
        List<Map<String, String>> mtnInfo = config.get("task_dset_1");
        for(int i = 0; i < mtnInfo.size(); i++){
            String mtnType = mtnInfo.get(i).get("mtnas_type");
            String tpID = mtnInfo.get(i).get("mtnas_tpid");
            String pgID = mtnInfo.get(i).get("mtnpg_id");
            String stateKey = tpID+pgID;
            privateState.addMapState(stateKey, mtnType);
        }
        return true;
    }

    protected  List<JSONObject> calc(RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput,
                           List<String> condition,
                           Map<String, List<Map<String,String>>>config,
                           List<TaskState> publicState,
                           TaskState privateState)throws Exception{
        if(condition.get(0).equals("1")){

            return null;
        }
        List<JSONObject> res = new ArrayList<JSONObject>();
        String deviceID = jsonInput.getString("deviceID");
        List<Map<String, String>> mtnInfo = config.get("task_dset_1");
        if(mtnInfo == null){
            return res;
        }
        for(int i = 0; i < mtnInfo.size(); i++){
            String vclID = mtnInfo.get(i).get("mtnas_vclid");
            if(!vclID.equals("1001146044")){
                //continue;
            }
            Double floatKM = Double.valueOf(mtnInfo.get(i).get("mtntp_floatkm"));
            Double spaceKM = Double.valueOf(mtnInfo.get(i).get("mtntp_spacekm"));
            Integer kmIDInt = Integer.valueOf(mtnInfo.get(i).get("vehiclerumkmid")) -1;
            String kmID = String.valueOf(kmIDInt);
            String tpID = mtnInfo.get(i).get("mtnas_tpid");
            String pgID = mtnInfo.get(i).get("mtnpg_id");
            Double lastKM = Double.valueOf(mtnInfo.get(i).get("mtnas_lastkm"));
            Double currentKM = -1.0;
            if(condition.contains(kmID)){
                Map<String, Map<String, String>> workStatusMap =
                        (Map<String, Map<String, String>>) jsonInput.get("workStatusMap");
                currentKM = Double.valueOf(workStatusMap.get(kmID).get("0"));
            }
            if(currentKM == -1.0){
                continue;
            }
            String stateKey = tpID+pgID;
            String mtnType = mtnInfo.get(i).get("mtnas_type");
            if(privateState.getMapState().containsKey(stateKey)){
                mtnType = privateState.getMapState().get(stateKey).get(0);
            }

            String newType = getNewType(currentKM, lastKM, spaceKM, floatKM);
            if(!newType.equals(mtnType)){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("timeStamp", new Date().getTime());
                jsonObject.put("deviceID", deviceID);
                Map<String, Map<String, String>> resMap = new HashMap<String, Map<String, String>>();
                Map<String, String> r = new HashMap<String, String>();
                r.put("floatKM", floatKM.toString());
                r.put("spaceKM", spaceKM.toString());
                r.put("mtnTypeOld", mtnType);
                r.put("mtnTypeNew", newType);
                r.put("currentKM", currentKM.toString());
                r.put("lastKM", lastKM.toString());
                resMap.put("result", r);
                jsonObject.put("workStatusMap", resMap);
                res.add(jsonObject);
                privateState.updateMapState(stateKey, new ArrayList<String>(Collections.singleton(newType)));
            }
            //System.out.println("r"+rawInput);
            //System.out.println("t"+transInput);
            //System.out.println("json:"+jsonInput);
            //System.out.println("cond"+condition);
            //System.out.println("conf:"+config);
            //System.out.println("public"+publicState);
            //System.out.println("private:"+privateState);
        }
        return res;
    }

    public static String getNewType(Double current, Double last, Double sapce, Double flo){
        Double start = last + sapce - flo;
        Double mid = last + sapce;
        Double end = last + sapce + flo;
        String res = "";
        if(current < start){
            res = "0";
        }
        else if(current >= start && current < mid){
            res = "1";
        }
        else if(current >= mid && current < end){
            res= "2";
        }
        else {
            res = "3";
        }
        return res;
    }
}
