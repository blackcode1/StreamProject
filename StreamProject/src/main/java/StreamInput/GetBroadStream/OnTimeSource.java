package StreamInput.GetBroadStream;

import StreamDataPacket.DataType;
import StreamDataPacket.SubClassDataType.LogList;
import StreamDataPacket.SubClassDataType.TaskInfoPacket;
import StreamDataPacket.SubClassDataType.TaskVarPacket;
import StreamProjectInit.StreamLog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.sql.SQLException;
import java.util.*;

import static java.lang.Math.max;

public class OnTimeSource extends RichSourceFunction<DataType> {
    private long spaceTime ;//单位为毫秒
    private Boolean isRunning;
    private String pid;

    public OnTimeSource(long spaceTime) {
        this.spaceTime = spaceTime;
        this.isRunning = true;
    }

    public OnTimeSource(long spaceTime, String pid) {
        this.spaceTime = spaceTime;
        this.isRunning = true;
        this.pid = pid;
    }

    @Override
    public void run(SourceContext<DataType> sourceContext) throws Exception {
        int count = 0;
        long total = 0;
        long spaceTime = this.spaceTime;
        while (this.isRunning){
            sourceContext.collect(new DataType("onTime"));
            count = count + 1;
            long t0 = System.currentTimeMillis();
            Thread.sleep(max(count * spaceTime - total, 0));
            total += System.currentTimeMillis() - t0;
        }
    }

    @Override
    public void cancel() {
        this.isRunning = false;
    }
}
