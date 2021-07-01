package StreamProjectInit;

import StreamDataPacket.BaseClassDataType.RCProject;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class GetProjectInfo {


    public static RCProject getProjectInfo(String[] args) throws Exception {
        RCProject rcProject = null;

        try {
            JSONObject projectJson = JSONObject.parseObject(args[0]);
            rcProject = new RCProject(projectJson);
        } catch (Exception e) {
            RunProject.logStr = StreamLog.createExLog(e, "ERROR", "项目信息解析异常");
        }

        return rcProject;
    }
}
