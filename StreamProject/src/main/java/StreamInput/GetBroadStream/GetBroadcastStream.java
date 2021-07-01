package StreamInput.GetBroadStream;

import StreamDataPacket.BaseClassDataType.RCProject;
import StreamDataPacket.DataType;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import javax.xml.crypto.Data;
import java.util.Map;

public class GetBroadcastStream {

    public static DataStream<DataType> getBroadCastStrem(StreamExecutionEnvironment env, RCProject rcProject, String engineUrl) throws Exception{
        String projectID = rcProject.projectID;
        DataStream<DataType> broadcastStream = env.addSource(new BroadcastSource(1L, projectID, engineUrl));
        DataStream<DataType> onTimeStream = env.addSource(new OnTimeSource(1000L, projectID));
        return broadcastStream.union(onTimeStream);
    }


}
