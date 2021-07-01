package StreamTest;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class ProcessExecTest {

    public static void main(String[] args) throws Exception {
        String exec_com = "java -jar D:\\FlinkProject\\StreamProject\\target\\StreamProject-1.0-SNAPSHOT.jar StreamTest.EndlessJar";


        try {
            Process pro = Runtime.getRuntime().exec(exec_com);
            InputStream inputStream = pro.getInputStream();
            String b = new String(readStream(inputStream));
            System.out.println("a"+b);
            String b2 = new String(readStream(inputStream));
            System.out.println("b"+b2);

        } catch (Exception e) {
           e.printStackTrace();
        }

    }
    public static byte[] readStream(InputStream inStream) throws Exception {
        int count = 0;
        Long t1 = new Date().getTime();
        while (count == 0) {
            Long t2 = new Date().getTime();
            if(t2 - t1 > 3000){
                break;
            }
            count = inStream.available();
        }
        byte[] b = new byte[count];
        inStream.read(b);
        return b;
    }
}
