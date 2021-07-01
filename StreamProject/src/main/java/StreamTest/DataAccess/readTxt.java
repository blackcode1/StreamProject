package StreamTest.DataAccess;

import java.io.*;
import java.net.Socket;


public class readTxt {

    public static void readTxtFile(String filePath){
        try {
            Socket socket = new Socket("192.168.3.32", 1049);
            OutputStream out = socket.getOutputStream();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String encoding="GBK";
            File file1=new File(filePath);
            File file2=new File("C:\\Users\\12905\\Desktop\\TCPData\\H411握手报文.txt");
            if(file1.isFile() && file1.exists() && file2.isFile() && file2.exists()) { //判断文件是否存在
                InputStreamReader read1 = new InputStreamReader(
                        new FileInputStream(file1),encoding);//考虑到编码格式
                InputStreamReader read2 = new InputStreamReader(
                        new FileInputStream(file2),encoding);//考虑到编码格式
                BufferedReader bufferedReader1 = new BufferedReader(read1);
                BufferedReader bufferedReader2 = new BufferedReader(read2);
                String lineTxt1 = null;
                String lineTxt2 = null;
                int lenth=0;
                long total = 0;
                while((lineTxt1 = bufferedReader1.readLine()) != null && (lineTxt2 = bufferedReader2.readLine()) != null){
                    System.out.println(System.currentTimeMillis()+"a:"+lineTxt1);
                    System.out.println(System.currentTimeMillis()+"b:"+ lineTxt2);
                    byte[] r1 = byteTool.hexToByteArray(lineTxt1);
                    byte[] r2 = byteTool.hexToByteArray(lineTxt2);
                    out.write(r2);
                    out.write(r1);
                    //先发r2 再发r1
                    lenth ++ ;
                    long t0 = System.currentTimeMillis();
                    Thread.sleep(lenth * 1000 - total);
                    total += System.currentTimeMillis() - t0;
                }
                System.out.println(lenth);
                read1.close();
                read2.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

    }

    public static void main(String argv[]){
        String filePath = "C:\\Users\\12905\\Desktop\\TCPData\\H411数据.txt";
//      "res/";
        readTxtFile(filePath);
    }



}