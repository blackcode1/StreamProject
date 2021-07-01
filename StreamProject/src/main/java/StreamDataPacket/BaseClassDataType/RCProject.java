package StreamDataPacket.BaseClassDataType;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

public class RCProject {

    public String projectID;
    public String projectName;
    public Boolean isCheckpoint;
    public Integer checkpointTime;//分钟

    public Integer parallelism;
    public String inputDataType;
    public List<StreamDataset> inputDataSetList;
    public StreamDataset outputDataSet;
    public Map<String, String> context;

    public RCProject(JSONObject jsonObject) throws Exception{
        this.projectID = jsonObject.getString("StreamProjectID");
        this.projectName = jsonObject.getString("StreamProjectName");
        this.isCheckpoint = jsonObject.getBoolean("IsCheckpoint");
        this.checkpointTime = jsonObject.getInteger("CheckpointTime");
        this.parallelism = jsonObject.getInteger("Para");
        this.inputDataType = jsonObject.getString("StreamDataType");
        this.inputDataSetList = new ArrayList<StreamDataset>();
        JSONArray datasetList = jsonObject.getJSONArray("StreamDataSetList");
        for(int i = 0; i < datasetList.size(); i++){
            JSONObject dataset = datasetList.getJSONObject(i);
            StreamDataset streamDataset = new StreamDataset(dataset);
            this.inputDataSetList.add(streamDataset);
        }
        this.outputDataSet = new StreamDataset(jsonObject.getJSONObject("OutputDataSet"));
        this.context = new HashMap<String, String>();
        for(String key: jsonObject.keySet()){
            this.context.put(key, jsonObject.getString(key));
        }
    }

    @Override
    public String toString() {
        return "RCProject{" +
                "projectID='" + projectID + '\'' +
                ", projectName='" + projectName + '\'' +
                ", isCheckpoint=" + isCheckpoint +
                ", checkpointTime=" + checkpointTime +
                ", parallelism=" + parallelism +
                ", inputDataType='" + inputDataType + '\'' +
                ", inputDataSetList=" + inputDataSetList +
                ", outputDataSet=" + outputDataSet +
                ", context=" + context +
                '}';
    }
}
