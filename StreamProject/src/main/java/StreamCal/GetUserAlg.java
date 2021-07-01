package StreamCal;

import StreamDataPacket.BaseClassDataType.JarInfo;
import cn.edu.thss.rcsdk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public class GetUserAlg {
    public static StreamAlg getUserAlg(Map<String, StreamAlg> userAlg, Map<String, JarInfo> jarInfoMap, String jarID) throws Exception {
        //return new DCECService();
        if(userAlg.containsKey(jarID)){
            return userAlg.get(jarID);
        }
        else {
            StreamAlg realTimeAlg = null;
            String jarPath = jarInfoMap.get(jarID).jarPath;
            String jarClass = jarInfoMap.get(jarID).jarClass;
            String type = jarInfoMap.get(jarID).type;
            Long time = jarInfoMap.get(jarID).windowSize;
            realTimeAlg = loadjar(jarPath, jarClass, type, time);
            userAlg.put(jarID, realTimeAlg);
            return realTimeAlg;
        }
    }

    public static StreamAlg loadjar(String jarPath, String jarClass, String type, Long t)throws Exception{
        ClassLoader cl;
        StreamAlg rti = null;
        cl = new URLClassLoader(
                new URL[]{new URL(jarPath)},
                Thread.currentThread().getContextClassLoader());
        Class<?> myclass = cl.loadClass(jarClass);
        if(type.equals("stream")){
            rti = (StreamAlg) myclass.newInstance();
        }
        else if(type.equals("window")){
            StreamAlg streamAlg = (StreamAlg) myclass.newInstance();
            rti = new WindowAlg(5000L, 1000L, streamAlg);
        }
        else{
            BatchAlg batchAlg = (BatchAlg) myclass.newInstance();
            rti = new SAlgFromBA(batchAlg, t);
        }
        return rti;
    }
}
