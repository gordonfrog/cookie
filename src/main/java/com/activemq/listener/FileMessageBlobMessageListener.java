package com.activemq.listener;

import org.apache.activemq.BlobMessage;
import org.apache.activemq.command.ActiveMQBlobMessage;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class FileMessageBlobMessageListener implements MessageListener{

    private static int bufferLength = 1024;
    private static byte[] bytesArr = new byte[bufferLength*bufferLength];
    private static ByteBuffer bbuf = ByteBuffer.allocate(bufferLength*bufferLength);

    public void onMessage(Message message) {

        if(message instanceof BlobMessage) {
            BlobMessage blobMessage = (BlobMessage) message;
            RandomAccessFile randomAccessFile = null;
            FileChannel fileChannel = null;
            File file = null;
            try {
                String fileName = blobMessage.getStringProperty("fileName");
                file = new File(fileName);
                randomAccessFile = new RandomAccessFile(file, "rw");
                fileChannel = randomAccessFile.getChannel();
                InputStream inputStream = blobMessage.getInputStream();
                int n = 0;
                while ((n = inputStream.read(bytesArr)) != -1) {
                    bbuf.put(bytesArr, 0, n);
                    //转换缓存区
                    bbuf.flip();
                    fileChannel.write(bbuf);
                    bbuf.clear();
                }
                bbuf.clear();
                System.out.println("结束时间:" + new Date());
                System.out.println("threadName:" + Thread.currentThread().getName() + " [x] Received end");
                inputStream.close();
                blobMessage.clearBody();
                //手动确认消息
                message.acknowledge();
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
                    ((ActiveMQBlobMessage)blobMessage).deleteFile();
                } catch (Exception ee) {
                    ee.printStackTrace();
                }

            }
        }

    }
}
