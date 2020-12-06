package cn.com.business.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

@Service
public class FileConsumer {

    private int bufferLength = 1024;
    private byte[] bytesArr = new byte[bufferLength*bufferLength];
    private ByteBuffer bbuf = ByteBuffer.allocate(bufferLength*bufferLength);


    @JmsListener(destination="testMqSpring",containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(Message message,Session session) {    // 进行消息接收处理
        System.err.println("【*** Got it ***】");
        try{
            if(message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                RandomAccessFile randomAccessFile = null;
                FileChannel fileChannel = null;
                File file = null;
                try {
                    String fileName = bytesMessage.getStringProperty("fileName");
                    file = new File(fileName);
                    randomAccessFile = new RandomAccessFile(file, "rw");
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
                    System.out.println("Now:" + new Date());
                    System.out.println("threadName:" + Thread.currentThread().getName() + " [x] Received end");
                    bytesMessage.clearBody();
                    //手动确认消息
                    message.acknowledge();

                    //文件处理休眠2秒
                    Thread.sleep(2 * 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                    //TODO移交异常日志
                } finally {
                    System.gc();
                    try {
                        if (fileChannel != null) {
                            fileChannel.close();
                            fileChannel = null;
                        }
                        if (randomAccessFile != null) {
                            randomAccessFile.close();
                            randomAccessFile = null;
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
