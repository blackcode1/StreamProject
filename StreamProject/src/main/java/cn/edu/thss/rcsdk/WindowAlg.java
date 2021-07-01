package cn.edu.thss.rcsdk;

import StreamDataPacket.BaseClassDataType.TaskState;
import com.alibaba.fastjson.JSONObject;
import ty.pub.RawDataPacket;
import ty.pub.TransPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WindowAlg<T> extends StreamAlg<T> {

    SimpleTimeWindow window;
    StreamAlg streamAlg;

    public WindowAlg(Long size, Long slide, StreamAlg streamAlg) {
        this.window = new SimpleTimeWindow(5000L, 1000L);
        this.streamAlg = streamAlg;
    }

    @Override
    public Boolean init(RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput, List<String> condition, Map<String, List<Map<String, String>>> config, String deviceID, List<TaskState> publicState, TaskState privateState, Long curtime) throws Exception {
        return streamAlg.init(rawInput, transInput, jsonInput, condition, config, deviceID, publicState, privateState, curtime);
    }

    @Override
    public List<T> calc(RawDataPacket rawInput, TransPacket transInput, JSONObject jsonInput, List<String> condition, Map<String, List<Map<String, String>>> config, String deviceID, List<TaskState> publicState, TaskState privateState, Long curtime) throws Exception {
        List<T> res = new ArrayList<>();
        List<JSONObject> jsonInputs = window.myTimeWindow(jsonInput);
        for(JSONObject input : jsonInputs){
            res.addAll(streamAlg.calc(rawInput, transInput, input, condition, config, deviceID, publicState, privateState, curtime));
        }
        return res;
    }
}
