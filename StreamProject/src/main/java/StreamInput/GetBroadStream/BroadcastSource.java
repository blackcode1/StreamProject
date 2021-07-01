package StreamInput.GetBroadStream;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.LogList;
import StreamDataPacket.SubClassDataType.TaskInfoPacket;
import StreamDataPacket.SubClassDataType.TaskVarPacket;
import StreamProjectInit.StreamLog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.sql.SQLException;
import java.util.*;

import static java.lang.Math.max;

public class BroadcastSource extends RichSourceFunction<DataType> {
    private long spaceTime ;//单位为分钟
    private Boolean isRunning;
    private String projectID;
    private String engineUrl;

    public BroadcastSource(long spaceTime, String projectID, String engineUrl) {
        this.spaceTime = spaceTime*1000*60;
        //this.spaceTime = 1L;
        this.isRunning = true;
        this.projectID = projectID;
        this.engineUrl = engineUrl;
    }

    public DataType collectTaskInfo(JSONObject taskInfo){
        DataType taskInfoData = null;
        try {
            taskInfoData = (DataType) new TaskInfoPacket(taskInfo);
        } catch (Exception e) {
            String logStr = StreamLog.createLocalLog(e, "ERROR", "任务信息解析异常", this.projectID);
            taskInfoData = (DataType) new LogList(logStr);
        }
        return taskInfoData;
    }

    public List<DataType> collectTaskVar(JSONObject taskInfo) throws SQLException {

        Map<String, List<Map<String, String>>> bigPacket = new HashMap<String, List<Map<String, String>>>();
        Map<String, Map<String, List<Map<String, String>>>> filterSamllPacket = new HashMap<String, Map<String, List<Map<String, String>>>>();
        List<DataType> list = new ArrayList<DataType>();
        try {
            JSONArray inputDatasetList = taskInfo.getJSONArray("InputDataSetList");
            for(int i = 0; i < inputDatasetList.size(); i++){
                JSONObject inputDataset = inputDatasetList.getJSONObject(i);

                List<Map<String, String>> onetable = GetTaskVar.getTaskVarByJson(inputDataset);

                if(!inputDataset.containsKey("Filter") || inputDataset.getString("Filter").equals("")){
                    bigPacket.put(inputDataset.getString("DataSetID"), onetable);
                }
                else {
                    String filter = inputDataset.getString("Filter");
                    String datasetID = inputDataset.getString("DataSetID");
                    for(int j = 0; j < onetable.size(); j++){
                        String vclID = onetable.get(j).get(filter);
                        if(filterSamllPacket.containsKey(vclID)){
                            Map<String, List<Map<String, String>>> oneVclInfo = filterSamllPacket.get(vclID);
                            if(oneVclInfo.containsKey(datasetID)){
                                oneVclInfo.get(datasetID).add(onetable.get(j));
                            }
                            else {
                                List<Map<String, String>> oneRow = new LinkedList<Map<String, String>>();
                                oneRow.add(onetable.get(j));
                                oneVclInfo.put(datasetID, oneRow);
                            }
                        }
                        else {
                            Map<String, List<Map<String, String>>> oneVclInfo = new HashMap<String, List<Map<String, String>>>();
                            List<Map<String, String>> oneRow = new LinkedList<Map<String, String>>();
                            oneRow.add(onetable.get(j));
                            oneVclInfo.put(datasetID, oneRow);
                            filterSamllPacket.put(vclID, oneVclInfo);
                        }
                    }
                }
            }
            if(!bigPacket.isEmpty()){
                DataType taskVarDataBig = (DataType) new TaskVarPacket(bigPacket);
                list.add(taskVarDataBig);
            }
            for(Map.Entry<String, Map<String, List<Map<String, String>>>> entry: filterSamllPacket.entrySet()){
                DataType taskVarDataSmall = (DataType) new TaskVarPacket(entry.getValue(), entry.getKey());
                list.add(taskVarDataSmall);
            }
        } catch (Exception e) {
            String logStr = StreamLog.createLocalLog(e, "ERROR", "任务配置数据解析异常", this.projectID);
            DataType dataType = (DataType) new LogList(logStr);
            list.add(dataType);
        }

        return list;
    }

    @Override
    public void run(SourceContext<DataType> sourceContext) throws Exception {
        int count = 0;
        long total = 0;
        long spaceTime = this.spaceTime;
        Integer taskstate = 0;
        while (this.isRunning){
            JSONObject taskInfo = null;
            try {
                taskInfo = GetTaskInfo.getTaskInfo(this.projectID, this.engineUrl);
            } catch (Exception e) {
                String logStr = StreamLog.createLocalLog(e, "ERROR", "从引擎获取任务信息异常", projectID);
                DataType logData = (DataType) new LogList(logStr);
                sourceContext.collect(logData);
            }
            if(taskInfo != null){
                sourceContext.collect(this.collectTaskInfo(taskInfo));
                List<DataType> list = this.collectTaskVar(taskInfo);
                for(int i = 0; i < list.size(); i++){
                    DataType x = list.get(i);
                    sourceContext.collect(x);
                }
            }
            count = count + 1;
            long t0 = System.currentTimeMillis();
            Thread.sleep(max(count * spaceTime - total, 0));
            total += System.currentTimeMillis() - t0;
        }
    }

    @Override
    public void cancel() {
        this.isRunning = false;
    }
}
