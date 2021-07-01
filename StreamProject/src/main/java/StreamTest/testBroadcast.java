package StreamTest;

import StreamDataPacket.BaseClassDataType.RCProject;
import StreamDataSource.OnTimeTestSource;
import StreamProjectInit.GetProjectInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Map;

public class testBroadcast {
    public static void testBroadCast(StreamExecutionEnvironment env, Map<String, String> context){
        String dsetListr = context.get("StreamDatasetList");
        JSONArray dsetList = JSONArray.parseArray(dsetListr);
        JSONObject dset = new JSONObject();
        for(Integer i = 0; i < dsetList.size(); i++){
            dset = dsetList.getJSONObject(i);
        }
        DataStream<String> t1 = env.addSource(new OnTimeTestSource(dset.getInteger("DataSetOffset")));
        DataStream<String> t2 = t1.flatMap(new RichFlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                collector.collect(s);
                ParameterTool parameters = (ParameterTool) getRuntimeContext().getExecutionConfig().getGlobalJobParameters();
                collector.collect(parameters.get("StreamDataType"));
            }
        });
        t2.print();
    }

    public static void main(String[] args) throws Exception {

        //StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String s = "88001421125";
        Integer idKey = Math.toIntExact(Long.valueOf(s) % 100);
        System.out.println(StringUtils.isNumeric(s));
        System.out.println(idKey);
        //RCProject rcProject = GetProjectInfo.getProjectInfo(args);
        //testBroadCast(env, rcProject.context);
       // env.execute(rcProject.projectName);
    }
}
