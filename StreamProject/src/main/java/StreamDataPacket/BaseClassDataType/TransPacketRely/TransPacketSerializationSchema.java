package StreamDataPacket.BaseClassDataType.TransPacketRely;

import org.apache.flink.api.common.serialization.SerializationSchema;
import ty.pub.BeanUtil;
import ty.pub.TransPacket;

import java.io.IOException;

public class TransPacketSerializationSchema implements SerializationSchema<TransPacket> {
    @Override
    public byte[] serialize(TransPacket transPacket) {
        return BeanUtil.toByteArray(transPacket);
    }
}
