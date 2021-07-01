package StreamSink;

import StreamDataPacket.BaseClassDataType.TransPacketRely.ParsedDataPacketSerializationSchema;
import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketSerializationSchema;
import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.JsonList;
import StreamDataPacket.SubClassDataType.ParsedDataPacketList;
import StreamDataPacket.SubClassDataType.TransPacketList;
import edu.thss.entity.ParsedDataPacket;
import edu.thss.entity.TransPacket;
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchema;

import static StreamDataPacket.pressStr.compress;

public class KafkaOverrideSerialization implements KeyedSerializationSchema<DataType> {

    @Override
    public byte[] serializeKey(DataType dataType) {
        return null;
    }

    @Override
    public byte[] serializeValue(DataType dataType) {
        if(dataType.streamDataType.equals("JSONObject")){
            JsonList jsonListRes = (JsonList)dataType;
            String resultStr =jsonListRes.streamData.toJSONString();
            return resultStr.getBytes();
        }
        else if(dataType.streamDataType.equals("TransPacket")){
            TransPacketList transListRes = (TransPacketList) dataType;
            TransPacket resultTrans = transListRes.streamData;
            return new TransPacketSerializationSchema().serialize(resultTrans);
        }
        else if(dataType.streamDataType.equals("ParsedDataPacket")){
            ParsedDataPacketList transListRes = (ParsedDataPacketList) dataType;
            ParsedDataPacket resultTrans = transListRes.streamData;
            return new ParsedDataPacketSerializationSchema().serialize(resultTrans);
        }
        return new byte[0];
    }

    @Override
    public String getTargetTopic(DataType dataType) {
        if(dataType.outputTopic == null || dataType.outputTopic.length() < 1){
            return null;
        }
        return dataType.outputTopic;
    }
}
