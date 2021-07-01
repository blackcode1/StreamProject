package StreamTest;

import StreamDataPacket.BaseClassDataType.TransPacketRely.TransPacketSerializationSchema;
import StreamProjectInit.StreamLog;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class readXMLTest {


    public static void main(String[] args) throws Exception {

        SAXReader saxReader = new SAXReader();

        Document document = null;
        Element rootElement = null;
        try {
            document = saxReader.read(new File("C:\\Users\\12905\\Desktop\\mqflinkTaskUID.xml"));
            rootElement = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        System.out.println(rootElement.element("job").element("uuid").getTextTrim());

    }
}
