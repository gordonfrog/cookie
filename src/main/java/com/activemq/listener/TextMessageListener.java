package com.activemq.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.nio.ByteBuffer;

public class TextMessageListener implements MessageListener{

    private static int bufferLength = 1024;
    private static byte[] bytesArr = new byte[bufferLength*bufferLength];
    private static ByteBuffer bbuf = ByteBuffer.allocate(bufferLength*bufferLength);

    public void onMessage(Message message) {

        if(message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try{
                System.out.println("textMessage:"+textMessage.getText());
                //手动确认消息
                textMessage.acknowledge();
                System.gc();
                //文件处理休眠2秒
                Thread.sleep(2 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                //TODO移交异常日志
            } finally {

            }
        }

    }
}
