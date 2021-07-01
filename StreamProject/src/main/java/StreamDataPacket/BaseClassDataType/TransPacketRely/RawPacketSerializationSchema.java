package StreamDataPacket.BaseClassDataType.TransPacketRely;

import edu.thss.entity.RawDataPacket;
import org.apache.flink.api.common.serialization.SerializationSchema;


public class RawPacketSerializationSchema implements SerializationSchema<RawDataPacket> {

    @Override
    public byte[] serialize(RawDataPacket rawDataPacket) {
        return BeanUtil.getBytesForRaw(rawDataPacket);
    }
}
