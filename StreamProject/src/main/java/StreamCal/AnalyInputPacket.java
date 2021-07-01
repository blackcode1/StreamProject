package StreamCal;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.JsonList;
import StreamDataPacket.SubClassDataType.RawPacketList;
import StreamDataPacket.SubClassDataType.TransPacketList;
import com.alibaba.fastjson.JSONObject;
import ty.pub.RawDataPacket;
import ty.pub.TransPacket;

import java.util.ArrayList;
import java.util.List;

public class AnalyInputPacket {

    public static String getDeviceID(DataType dataType)throws Exception{
        if(dataType.streamDataType.equals("onTime")){
            return "onTimeData";
        }
        return dataType.dataID;
    }

    public static List<String> getGKIDList(DataType dataType)throws Exception{
        if(dataType.streamDataType.equals("JSONObject")){
            return ((JsonList) dataType).allGKID();
        }
        else if(dataType.streamDataType.equals("TransPacket")){
            return ((TransPacketList) dataType).allGKID();
        }
        else {
            return new ArrayList<String>();
        }
    }

    public static JSONObject getJsonInput(DataType dataType)throws Exception{
        if(dataType.streamDataType.equals("JSONObject")){
            return ((JsonList) dataType).streamData;
        }
        else {
            return null;
        }
    }

    public static TransPacket getTranspacket(DataType dataType)throws Exception{
        if(dataType.streamDataType.equals("TransPacket")){
            return ((TransPacketList) dataType).streamData;
        }
        else {
            return null;
        }
    }

    public static RawDataPacket getRawpacket(DataType dataType)throws Exception{
        if(dataType.streamDataType.equals("RawDataPacket")){
            return ((RawPacketList) dataType).streamData;
        }
        else {
            return null;
        }
    }

    public static Long getTime(DataType dataType)throws Exception{
        if(dataType.streamDataType.equals("JSONObject")){
            return ((JsonList) dataType).streamData.getLong("timestamp");
        }
        else if(dataType.streamDataType.equals("TransPacket")){
            return ((TransPacketList) dataType).streamData.getTimestamp();
        }
        else {
            return System.currentTimeMillis();
        }
    }
}
