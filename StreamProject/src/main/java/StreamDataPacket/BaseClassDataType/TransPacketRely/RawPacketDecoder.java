package StreamDataPacket.BaseClassDataType.TransPacketRely;

import edu.thss.entity.RawDataPacket;
import edu.thss.util.BeanUtil;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class RawPacketDecoder implements Deserializer<RawDataPacket> {
    public RawPacketDecoder() {
    }

    public void close() {
    }

    public void configure(Map<String, ?> arg0, boolean arg1) {
    }

    public RawDataPacket deserialize(String arg0, byte[] bytes) {
        return BeanUtil.split(bytes);
    }
}
