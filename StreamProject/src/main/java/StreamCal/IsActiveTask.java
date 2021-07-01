package StreamCal;

import StreamDataPacket.BaseClassDataType.StreamTask;
import StreamDataPacket.SubClassDataType.TaskInfoPacket;

import java.util.ArrayList;
import java.util.List;

public class IsActiveTask {

    public static Boolean isActive(String datasetID,
                                   String deviceID,
                                   List<String> allGK,
                                   TaskInfoPacket taskInfoPacket,
                                   StreamTask streamTask) throws Exception{

        //任务不运行
        if(!isLive(taskInfoPacket, streamTask)){
            return false;
        }
        //当前工况的设备ID不在任务的设备ID列表中
        List<String> inputTopicList = streamTask.inputTopicTest;
        if(!inputTopicList.get(0).equals("*") && !inputTopicList.contains(datasetID)){
            return false;
        }
        //当前工况的设备ID不在任务的设备ID列表中
        List<String> deviceList = streamTask.deviceList;
        if(!deviceList.get(0).equals("*") && !deviceList.contains(deviceID)){
            return false;
        }
        //当前工况不在任务的触发工况ID列表中
        List<String> condGKList = streamTask.condition;
        if(condGKList.size() > 0 && condGKList.get(0).equals("*")){
            return true;
        }
        List<String> condition = ModifyMemory.getCondition(allGK, condGKList);
        if(allGK.size() != 0 && condition.size() == 0){
            return false;
        }
        return true;
    }

    public static Boolean isActiveSignal(String datasetID,
                                   TaskInfoPacket taskInfoPacket,
                                   StreamTask streamTask) throws Exception{

        //任务不运行
        if(!isLive(taskInfoPacket, streamTask)){
            return false;
        }
        //当前工况的主题不在任务的流输入过滤数据集列表中
        List<String> inputTopicList = streamTask.inputTopicTest;
        if(!inputTopicList.get(0).equals("*") && !inputTopicList.contains(datasetID)){
            return false;
        }
        return true;
    }



    public static Boolean isLive(TaskInfoPacket taskInfoPacket, StreamTask streamTask) throws Exception{

        //任务被停止
        if(streamTask.taskState == 0){
            return false;
        }
        //算法jar包不存在
        if(!taskInfoPacket.jarInfoMap.containsKey(streamTask.jarID)){
            return false;
        }
        return true;
    }
}
