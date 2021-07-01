package cn.edu.thss.rcinterface;

import StreamDataPacket.BaseClassDataType.TaskState;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import java.util.Map;
import ty.pub.RawDataPacket;
import ty.pub.TransPacket;

public interface RealTimeInterface<T> {
    Boolean init(RawDataPacket var1, TransPacket var2, JSONObject var3, List<String> var4, Map<String, List<Map<String, String>>> var5, List<TaskState> var6, TaskState var7) throws Exception;

    List<T> callAlg(RawDataPacket var1, TransPacket var2, JSONObject var3, List<String> var4, Map<String, List<Map<String, String>>> var5, List<TaskState> var6, TaskState var7) throws Exception;
}

