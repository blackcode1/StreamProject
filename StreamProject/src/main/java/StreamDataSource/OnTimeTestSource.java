package StreamDataSource;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.util.HashMap;
import java.util.Map;

public class OnTimeTestSource extends RichSourceFunction<String> {
    private long spaceTime ;
    private Boolean isRunning;

    public OnTimeTestSource(long spaceTime) {
        this.spaceTime = spaceTime;
        this.isRunning = true;
    }

    public long getSpaceTime() {
        return spaceTime;
    }

    public void setSpaceTime(long spaceTime) {
        this.spaceTime = spaceTime;
    }

    public void run(SourceContext<String> sourceContext) throws Exception {
        System.out.println("normal source start, space time:"+this.spaceTime+"s");
        int count = 0;
        long total = 0;
        long spaceTime = this.spaceTime*1000L;
        while (this.isRunning){
            sourceContext.collect(String.valueOf(count));
            count = count + 1;
            long t0 = System.currentTimeMillis();
            Thread.sleep(count * spaceTime - total);
            total += System.currentTimeMillis() - t0;
        }
    }

    public void cancel() {
        this.isRunning = false;
    }
}
