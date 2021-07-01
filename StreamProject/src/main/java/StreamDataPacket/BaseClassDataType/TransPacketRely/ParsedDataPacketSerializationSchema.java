package StreamDataPacket.BaseClassDataType.TransPacketRely;

import edu.thss.entity.ParsedDataPacket;
import org.apache.flink.api.common.serialization.SerializationSchema;
import ty.pub.BeanUtil;
import ty.pub.TransPacket;

public class ParsedDataPacketSerializationSchema implements SerializationSchema<ParsedDataPacket> {
    @Override
    public byte[] serialize(ParsedDataPacket parsedDataPacket) {
        return BeanUtil.toByteArray(parsedDataPacket);
    }
}
