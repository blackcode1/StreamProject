package StreamDataPacket.SubClassDataType;

import StreamDataPacket.DataType;
import edu.thss.entity.RawDataPacket;

public class RawPacketList extends DataType {
    public RawDataPacket streamData;

    public RawPacketList(RawDataPacket streamData) {
        super("RawDataPacket");
        this.streamData = streamData;
        this.dataID = streamData.getRawDataId();
    }

    @Override
    public String toString() {
        return "RawPacketList{" +
                "streamData=" + streamData +
                ", streamDataType='" + streamDataType + '\'' +
                ", dataID='" + dataID + '\'' +
                ", outputTopic='" + outputTopic + '\'' +
                '}';
    }
}
