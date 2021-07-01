package StreamDataPacket.DataTypeChange;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.JsonList;
import StreamDataPacket.SubClassDataType.TransPacketList;
import edu.thss.entity.TransPacket;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class MapDatatype2TP implements FlatMapFunction<DataType, TransPacket> {

    @Override
    public void flatMap(DataType dataType, Collector<TransPacket> collector) throws Exception {
        if(dataType.streamDataType.equals("TransPacket")){
            TransPacketList transListRes = (TransPacketList) dataType;
            TransPacket resultTrans = transListRes.streamData;
            collector.collect(resultTrans);
        }
    }
}
