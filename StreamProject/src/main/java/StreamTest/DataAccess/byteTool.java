package StreamTest.DataAccess;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class byteTool {
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }
    public static int byteToInt(byte[] bytes){
        int number = 0;
        for(int i = 0; i < 4 ; i++){
            number += bytes[i] << i*8;
        }
        return number;
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 整数转换成字节数组 关键技术：ByteArrayOutputStream和DataOutputStream
     *
     * @param n
     * 需要转换整数
     * @return 字节数组
     */
    public static byte[] intToByteArray(int n) {
        byte[] byteArray = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(byteOut);
            dataOut.writeInt(n);
            byteArray = byteOut.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
    /**
     * 16进制转换成为string类型字符串
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte
     */
    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return  转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            inHex="0"+inHex;
        }
        result = new byte[(hexlen/2)];
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    /**
     *
     * @param bytes 长度为6的byte数组
     * @return 返回一个 "yyyy-MM-ddTHH:mm:ss.SSS" 格式的时间字符串
     */
    public static String bytesToDate(byte[] bytes){
        int year=2000+bytes[0];
        return String.format("%s-%s-%sT%s:%s:%s.000", Integer.toString(year),
                Byte.toString(bytes[1]).length()<2?'0'+Byte.toString(bytes[1]):Byte.toString(bytes[1]),
                Byte.toString(bytes[2]).length()<2?'0'+Byte.toString(bytes[2]):Byte.toString(bytes[2]),
                Byte.toString(bytes[3]).length()<2?'0'+Byte.toString(bytes[3]):Byte.toString(bytes[3]),
                Byte.toString(bytes[4]).length()<2?'0'+Byte.toString(bytes[4]):Byte.toString(bytes[4]),
                Byte.toString(bytes[5]).length()<2?'0'+Byte.toString(bytes[5]):Byte.toString(bytes[5]));
    }

    /**
     * 4字节的比特数组转换成int
     * @param bytes 需要4字节的长度。由高位到低位
     * @return int 类型
     */
    public static int bytesToInt(byte[] bytes){
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
    }
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }
    /**
     * 合并多个byte数组为一个数组
     * @param values 多个数组
     * @return 一个数组
     */
    public static byte[] byteMergerAll(byte[]... values) {
        int length_byte = 0;
        for (byte[] value : values) {
            length_byte += value.length;
        }
        byte[] all_byte = new byte[length_byte];
        int countLength = 0;
        for (byte[] b : values) {
            System.arraycopy(b, 0, all_byte, countLength, b.length);
            countLength += b.length;
        }
        return all_byte;
    }

    public static String getValueFromApplication(String keyWord){
        Properties prop = new Properties();
        String value = null;
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
            assert inputStream != null;
            prop.load(inputStream);
            value = prop.getProperty(keyWord);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
    public static byte[] getCurrentData(){
        Calendar nowTime = Calendar.getInstance();
        nowTime.setTime(new Date());
        byte[] nowTimeBytes = new byte[6];
        nowTimeBytes[0]= (byte) (nowTime.get(Calendar.YEAR)-2000);
        nowTimeBytes[1]= (byte) nowTime.get(Calendar.MONTH);
        nowTimeBytes[2]= (byte) nowTime.get(Calendar.DAY_OF_MONTH);
        nowTimeBytes[3]= (byte) nowTime.get(Calendar.HOUR_OF_DAY);
        nowTimeBytes[4]= (byte) nowTime.get(Calendar.MINUTE);
        nowTimeBytes[5]= (byte) nowTime.get(Calendar.SECOND);
        return nowTimeBytes;
    }

    public static String bytesToHex(Byte[] result) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : result) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    //十六进制字符串转二进制字符串
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

//    public static void main(String[] args) {
//        byte [] test = {0x34,0x00};
//        System.out.println(byteToShort(test));
//    }
}
