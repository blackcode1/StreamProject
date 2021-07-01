package StreamTest;

import StreamCal.GetUserAlg;
import cn.edu.thss.rcsdk.RealTimeAlg;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;

public class ReadRemoteJarTest {
    public static RealTimeAlg loadjar(String jarPath, String jarClass){
        ClassLoader cl;
        RealTimeAlg rti = null;
        try {
            cl = new URLClassLoader(
                    new URL[]{new URL(jarPath)},
                    Thread.currentThread().getContextClassLoader());
            Class<?> myclass = cl.loadClass(jarClass);
            rti = (RealTimeAlg) myclass.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return rti;
    }

    public static void readJar(){
        String jarPath = "http://192.168.10.176:8081/home/qianzhou/engine/jar/testcase7-1.0-SNAPSHOT-jar-with-dependencies.jar";
        String jarClass = "rtcf.test.TestCase1";
        RealTimeAlg realTimeAlg = loadjar(jarPath, jarClass);
        System.out.println(realTimeAlg);
        //System.out.println(realTimeAlg.init(null, null, null, null,null, null,null));
    }

    public static void readXML()throws Exception{
        ChannelSftp sftp = null;
        Session session = null;
        try {
            session = SFTPUtil.connect("192.168.10.176", 22, "qianzhou", "dsa743xh170752x");
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            SFTPUtil.download( "/home/qianzhou/engine/xml/testcaseuuid1.xml", "C:/Users/12905/Desktop",sftp);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(sftp != null)sftp.disconnect();
            if(session != null)session.disconnect();
        }
        //String jarPath = "http://192.168.10.176:22/home/qianzhou/engine/xml/testcaseuuid1.xml";
        String jarPath = "file:///C:/Users/12905/Desktop/testcaseuuid1.xml";
        try {
            URL url = new URL(jarPath);
            URLConnection urlcon = url.openConnection();
            urlcon.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
            String s;
            while ((s = reader.readLine()) != null) {
                System.out.println(s);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void upJar(){
        ChannelSftp sftp = null;
        Session session = null;
        try {
            session = SFTPUtil.connect("192.168.10.176", 22, "qianzhou", "dsa743xh170752x");
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            SFTPUtil.upload( "/home/qianzhou/jar", "D:\\FlinkProject\\StreamProject\\target\\StreamProject-1.0-SNAPSHOT.jar",sftp);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(sftp != null)sftp.disconnect();
            if(session != null)session.disconnect();
        }
    }

    public static void main(String[] args) throws Exception{
       // RealTimeAlg rtl = loadjar("file:/C:/Users/12905/Desktop/CTY_DCEC_Service_Flink.jar", "tycmsc.dcec.service.DCECService");
       // Boolean b = rtl.init(null, null, null, null, null, null, null);
       // System.out.println(b);
        upJar();
    }
}
