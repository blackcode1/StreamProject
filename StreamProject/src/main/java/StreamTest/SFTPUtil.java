package StreamTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * SFTP帮助类
 * @author wangbailin
 *
 */
public class SFTPUtil {


    /**
     * 连接sftp服务器
     * @param host 远程主机ip地址
     * @param port sftp连接端口，null 时为默认端口
     * @param user 用户名
     * @param password 密码
     * @return
     * @throws JSchException
     */
    public static Session connect(String host, Integer port, String user, String password) throws JSchException{
        Session session = null;
        try {
            JSch jsch = new JSch();
            if(port != null){
                session = jsch.getSession(user, host, port.intValue());
            }else{
                session = jsch.getSession(user, host);
            }
            session.setPassword(password);
            //设置第一次登陆的时候提示，可选值:(ask | yes | no)
            session.setConfig("StrictHostKeyChecking", "no");
            //30秒连接超时
            session.connect(30000);
        } catch (JSchException e) {
            e.printStackTrace();
            System.out.println("SFTPUitl 获取连接发生错误");
            throw e;
        }
        return session;
    }

    /**
     * sftp上传文件(夹)
     * @param directory
     * @param uploadFile
     * @param sftp
     * @throws Exception
     */
    public static void upload(String directory, String uploadFile, ChannelSftp sftp) throws Exception{
        System.out.println("sftp upload file [directory] : "+directory);
        System.out.println("sftp upload file [uploadFile] : "+ uploadFile);
        File file = new File(uploadFile);
        if(file.exists()){
            //这里有点投机取巧，因为ChannelSftp无法去判读远程linux主机的文件路径,无奈之举
            try {
                Vector content = sftp.ls(directory);
                if(content == null){
                    sftp.mkdir(directory);
                }
            } catch (SftpException e) {
                sftp.mkdir(directory);
            }
            //进入目标路径
            sftp.cd(directory);
            if(file.isFile()){
                InputStream ins = new FileInputStream(file);
                //中文名称的
                sftp.put(ins, new String(file.getName().getBytes(),"UTF-8"));
                //sftp.setFilenameEncoding("UTF-8");
            }
        }
    }

    /**
     * sftp下载文件（夹）
     * @param srcFile 下载文件完全路径
     * @param saveFile 保存文件路径
     * @param sftp ChannelSftp
     * @throws UnsupportedEncodingException
     */
    public static void download(String srcFile, String saveFile, ChannelSftp sftp) throws UnsupportedEncodingException {
        Vector conts = null;
        try{
            conts = sftp.ls(srcFile);
        } catch (SftpException e) {
            e.printStackTrace();
        }
        File file = new File(saveFile);
        if(!file.exists()) file.mkdir();
        //文件
        if(srcFile.indexOf(".") > -1){
            try {
                sftp.get(srcFile, saveFile);
            } catch (SftpException e) {
                e.printStackTrace();
            }
        }
    }
}
