package StreamInput;

import StreamDataPacket.BaseClassDataType.JarInfo;
import StreamDataPacket.BaseClassDataType.RCProject;
import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.LogList;
import StreamProjectInit.RunProject;
import StreamProjectInit.StreamLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;


public class StreamUnion {
    public static KeyedStream<DataType, String> streamUnion
            (DataStream<DataType> inputStream, DataStream<DataType> broadcastStream, RCProject project) throws Exception{
        final String pid = project.projectID;
        DataStream<DataType> bcStream = broadcastStream.process(new ProcessFunction<DataType, DataType>() {
            //任务信息，任务配置数据（taskVarSmall， taskVar），广播，错误日志
            @Override
            public void processElement(DataType dataType, Context context, Collector<DataType> collector) throws Exception {
                if(dataType.streamDataType.equals("taskVarSmall") || dataType.streamDataType.equals("Log")){
                    collector.collect(dataType);
                }
                else {
                    for(int i = 0; i < 100; i++){
                        DataType newData = (DataType) dataType.clone();
                        newData.dataID = String.valueOf(i);
                        collector.collect(newData);
                    }
                }
            }
        });
        KeyedStream<DataType, String> keyedStream = inputStream.union(bcStream).keyBy(new KeySelector<DataType, String>() {
            @Override
            public String getKey(DataType dataType) throws Exception {
                Integer idKey = dataType.dataID.hashCode() % 100;
                if(StringUtils.isNumeric(dataType.dataID)) {
                    idKey = Math.toIntExact(Long.valueOf(dataType.dataID) % 100);
                }

                return idKey.toString();
            }
        });

        return keyedStream;
    }
}
