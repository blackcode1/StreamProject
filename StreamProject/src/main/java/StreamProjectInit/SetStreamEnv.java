package StreamProjectInit;

import StreamDataPacket.BaseClassDataType.RCProject;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Map;

public class SetStreamEnv {
    public static String logStr;

    public static void setParaCheckPoint(StreamExecutionEnvironment env, RCProject rcProject) throws Exception {
        try {
            Integer para = rcProject.parallelism;
            env.setParallelism(para);

            env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 1000L));

            Boolean isCheckPoint = rcProject.isCheckpoint;
            if (isCheckPoint) {
                Long cptime = rcProject.checkpointTime * 60 * 1000L;
                env.enableCheckpointing(cptime);
                CheckpointConfig checkpointConfig = env.getCheckpointConfig();
                checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
                checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            }
        } catch (Exception e) {
            logStr = StreamLog.createExLog(e, "ERROR", "项目并行度与检查点设置异常");
        }

    }
}
