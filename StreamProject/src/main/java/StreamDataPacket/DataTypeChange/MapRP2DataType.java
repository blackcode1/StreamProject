package StreamDataPacket.DataTypeChange;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.RawPacketList;
import StreamDataPacket.SubClassDataType.TransPacketList;
import org.apache.flink.api.common.functions.MapFunction;
import ty.pub.RawDataPacket;
import ty.pub.TransPacket;

public class MapRP2DataType implements MapFunction<RawDataPacket, DataType>{

    @Override
    public DataType map(RawDataPacket rawDataPacket) throws Exception {
        DataType res = (DataType) new RawPacketList(rawDataPacket);
        return res;
    }
}
