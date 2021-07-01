package StreamProjectInit;

import StreamCal.StreamCal;
import StreamDataPacket.BaseClassDataType.RCProject;
import StreamDataPacket.BaseClassDataType.StreamDataset;
import StreamDataPacket.DataType;
import StreamInput.GetBroadStream.GetBroadcastStream;
import StreamInput.GetInputStream.GetInputStream;
import StreamInput.StreamUnion;
import StreamSink.StreamSink;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.OutputTag;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

public class RunProject {
    private static String engineUrl;
    private static StreamDataset localTestDataSet;
    private static StreamDataset logDataSet;
    public static String logStr;

    private static Boolean init() throws Exception{
        String engine_path = System.getProperty("user.dir");
        SAXReader saxReader = new SAXReader();

        Document document = null;
        Element rootElement = null;
        try {
            document = saxReader.read(new File(engine_path + "/Engine.xml"));
            rootElement = document.getRootElement();
        } catch (DocumentException e) {
            String msg = "配置文件Engine.xml读取失败";
            logStr = StreamLog.createExLog(e, "ERROR", msg);
            return false;
        }

        try {
            engineUrl = rootElement.element("engineurl").getTextTrim();
        } catch (Exception e) {
            String msg = "引擎路径engineurl读取失败";
            logStr = StreamLog.createExLog(e, "ERROR", msg);
            return false;
        }

        try {
            List<Element> dss = rootElement.element("datasets").elements("dataset");
            for(Element ds:dss){
                if("test".equals(ds.attributeValue("use"))){
                    localTestDataSet = new StreamDataset(ds);
                }
            }
            if(localTestDataSet == null){
                throw new Exception("未找到本地测试输入数据集");
            }
        } catch (Exception e) {
            String msg = "本地测试输入数据集读取失败";
            logStr = StreamLog.createExLog(e, "ERROR", msg);
            return false;
        }

        try {
            List<Element> dss = rootElement.element("datasets").elements("dataset");
            for(Element ds:dss){
                if("log".equals(ds.attributeValue("use"))){
                    logDataSet = new StreamDataset(ds);
                }
            }
            if(logDataSet == null){
                throw new Exception("未找到日志数据集");
            }
        } catch (Exception e) {
            String msg = "日志数据集读取失败";
            logStr = StreamLog.createExLog(e, "ERROR", msg);
            return false;
        }

        return true;
    }

    public static void main(String[] args) throws Exception {
        if(!init()){
            System.out.println(logStr);
            return;
        }
        RCProject rcProject = GetProjectInfo.getProjectInfo(args);
        if(logStr != null){
            System.out.println(logStr);
            return;
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        SetStreamEnv.setParaCheckPoint(env, rcProject);
        if(logStr != null){
            System.out.println(logStr);
            return;
        }

        ParameterTool parameterTool = ParameterTool.fromMap(rcProject.context);
        env.getConfig().setGlobalJobParameters(parameterTool);

        DataStream<DataType> t1 = GetInputStream.getInputStream(env, rcProject);
        DataStream<DataType> broadcastStream = GetBroadcastStream.getBroadCastStrem(env, rcProject, engineUrl);
        KeyedStream<DataType, String> calStream = StreamUnion.streamUnion(t1, broadcastStream, rcProject);
        DataStream<DataType> resultStream = StreamCal.streamCalTask(calStream);
        resultStream.print();
        StreamSink.resultSink(resultStream, rcProject.outputDataSet);

        SingleOutputStreamOperator<DataType> s = (SingleOutputStreamOperator<DataType>) resultStream;
        final OutputTag<String> outputTag = new OutputTag<String>("LocalTestInput"){};
        DataStream<String> localTestInputStream = s.getSideOutput(outputTag);
        //localTestInputStream.print();
        StreamSink.simpleSink(localTestInputStream, localTestDataSet);

        final OutputTag<String> logTag = new OutputTag<String>("Log"){};
        DataStream<String> runTimeLogStream = s.getSideOutput(logTag);
        //runTimeLogStream.print();
        StreamSink.simpleSink(runTimeLogStream, logDataSet);

        try{
            env.execute(rcProject.projectID);
        } catch (Exception e) {
            String logStr = StreamLog.createExLog(e, "ERROR", "流数据集连接失败");
            System.out.println(logStr);
            return;
        }
    }
}
