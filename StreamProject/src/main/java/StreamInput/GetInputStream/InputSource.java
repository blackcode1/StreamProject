package StreamInput.GetInputStream;

import StreamDataPacket.BaseClassDataType.StreamDataset;
import StreamDataPacket.DataType;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class InputSource {
    public static DataStream<DataType> getOneIputStream(StreamExecutionEnvironment env, StreamDataset streamDataset, String pid){
        String dataType = streamDataset.dataType;
        String ip = streamDataset.dataSourceIp;
        String port = streamDataset.dataSourcePort;
        String topic = streamDataset.dataSetTopic;
        String groupID = streamDataset.dataSetGroupID;
        String datasetID = streamDataset.datasetID;
        String user = streamDataset.dataSourceUser;
        String pw = streamDataset.dataSourcePassword;
        Integer offset = streamDataset.dataSetOffset;
        Long startt = streamDataset.startTime;
        if(streamDataset.dataSourceType.equals("KAFKA")){
            //return KafkaSource.getKafkaStream(env, dataType, ip, port, groupID, topic, pid, datasetID, offset);
            return KafkaSource.getKafkaStream(env, dataType, ip, port, groupID, topic, pid, datasetID, startt);
        }
        else if(streamDataset.dataSourceType.equals("RMQ")){
            return RabbitMQSource.getRabbitMQStream(env, dataType, ip, port, groupID, topic, pid, datasetID, user, pw);
        }
        return null;
    }

}

