package StreamInput.GetInputStream;

import StreamDataPacket.BaseClassDataType.RCProject;
import StreamDataPacket.BaseClassDataType.StreamDataset;
import StreamDataPacket.DataType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;
import java.util.Map;

public class GetInputStream {
    public static DataStream<DataType> getInputStream(StreamExecutionEnvironment env, RCProject rcProject){

        List<StreamDataset> inputDatasetList = rcProject.inputDataSetList;
        String pid = rcProject.projectID;

        DataStream<DataType> inputStream = null;

        for(Integer i = 0; i < inputDatasetList.size(); i++){
            DataStream<DataType> oneInputStream = InputSource.getOneIputStream(env, inputDatasetList.get(i), pid);
            if(i == 0){
                inputStream = oneInputStream;
            }
            else {
                inputStream = inputStream.union(oneInputStream);
            }
        }

        return inputStream;
    }
}
