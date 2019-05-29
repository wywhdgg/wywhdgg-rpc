package com.wywhdgg.framework.discovery;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author: dongzhb
 * @date: 2019/5/27
 * @Description:本地文件配置获取
 */
public class LocationConfigServiceInfoDiscoverer implements ServiceInfoDiscoverer {
    @Override
    public ServiceInfo getServiceInfo(String name) {
        return null;
    }

    public static void readTxtByNIO(String filePath) {
        long time1 = System.currentTimeMillis();
        FileInputStream fis = null;
        FileChannel inChannel = null;
        int bufSize = 1024 * 10;
        try {
            fis = new FileInputStream(filePath);
            inChannel = fis.getChannel();
            System.out.println("file size --->" + inChannel.size());
            ByteBuffer buffer = ByteBuffer.allocate(bufSize);
            // System.out.println("buffer init position --->"+buffer.position()+"---- buffer init remaining --->"+buffer.remaining());
            //这里标记了后面才可以调用buffer.reset(), 而且只能调用一次,
            //不然会抛出java.nio.InvalidMarkException
            //buffer.mark();
            String enterStr = "\n";
            StringBuffer strBuf = new StringBuffer("");
            int lineNum = 0;
            while (inChannel.read(buffer) != -1) {
                int rSize = buffer.position();
                buffer.clear();
                String tempString = new String(buffer.array(), 0, rSize);
                if (fis.available() == 0) {//最后一行，加入"\n分割符"
                    tempString += "\n";
                }

                int fromIndex = 0;

                int endIndex = 0;
                while ((endIndex = tempString.indexOf(enterStr, fromIndex)) != -1) {

                    String line = tempString.substring(fromIndex, endIndex);

                    line = new String(strBuf.toString() + line);

                    System.out.println(line);

                    strBuf.delete(0, strBuf.length());

                    fromIndex = endIndex + 1;
                    lineNum++;
                }

                if (rSize > tempString.length()) {
                    strBuf.append(tempString.substring(fromIndex, tempString.length()));
                } else {
                    strBuf.append(tempString.substring(fromIndex, rSize));
                }
                System.out.println("lineNum =" + lineNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件读取错误!");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            long time2 = System.currentTimeMillis();
            long time = (time1 - time2) / 1000;
            System.out.println("共花费" + time + "秒");
        }
    }

    public static void main(String[] args) {
        String filePath = "D:\\github-workspace\\wywhdgg-rpc\\src\\main\\resources\\application.properties";
        System.out.println(filePath);
        //readTxtByBuffer(filePath);
       readTxtByNIO(filePath);
    }



}
