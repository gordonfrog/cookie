package com.activemq.listener;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class FileMessageGroupListener implements MessageListener{

    private static int bufferLength = 1024;
    private static byte[] bytesArr = new byte[bufferLength*bufferLength];
    private static ByteBuffer bbuf = ByteBuffer.allocate(bufferLength*bufferLength);

    private Session session;

    public FileMessageGroupListener(Session session){
        this.session = session;
    }

    public FileMessageGroupListener(){
    }

    public void onMessage(Message message) {

        if(message instanceof BytesMessage) {
            BytesMessage bytesMessage = (BytesMessage) message;
            RandomAccessFile randomAccessFile = null;
            FileChannel fileChannel = null;
            File file = null;
            try {
                String fileName = bytesMessage.getStringProperty("fileName");
                int JMSXGroupSeq = bytesMessage.getIntProperty("JMSXGroupSeq");

                System.out.println("JMSXGroupSeq:"+JMSXGroupSeq);

                file = new File(fileName);

                randomAccessFile = new RandomAccessFile(file, "rw");
                //开始之前判断文件是否存在  若存在 则重头开始写入
                if(file.exists()){
                    if(JMSXGroupSeq == 1){
                        randomAccessFile.setLength(0);
                    }else{
                        randomAccessFile.seek(randomAccessFile.length());
                    }
                }
                fileChannel = randomAccessFile.getChannel();


                int n = 0;
                while ((n = bytesMessage.readBytes(bytesArr)) != -1) {
                    bbuf.put(bytesArr, 0, n);
                    //转换缓存区
                    bbuf.flip();
                    fileChannel.write(bbuf);
                    bbuf.clear();
                }
                bbuf.clear();

                //偌确认是某一组消息的结束 则 打印内容
                if(JMSXGroupSeq == -1){
                    System.out.println("结束时间:" + new Date());
                    System.out.println("threadName:" + Thread.currentThread().getName() + " [x] Received end");
                    bytesMessage.clearBody();
                    //手动确认消息
                    message.acknowledge();
                    //session.commit();
                }

                System.gc();
                //文件处理休眠2秒
                Thread.sleep(2 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                if (file != null && file.exists()) {
                    file.delete();
                }
                //TODO移交异常日志
            } finally {
                try {
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }

            }
        }

    }
}
