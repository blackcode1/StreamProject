package StreamDataPacket.BaseClassDataType.TransPacketRely;



import edu.thss.entity.TransPacket;
import org.apache.flink.api.common.serialization.SerializationSchema;

import java.io.IOException;

public class TransPacketSerializationSchema implements SerializationSchema<TransPacket> {
    @Override
    public byte[] serialize(TransPacket transPacket) {
        return BeanUtil.toByteArray(transPacket);
    }
}
