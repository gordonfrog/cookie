package cn.com.business.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

@Service
public class TextConsumer {

    @JmsListener(destination="${business.textQueue}",containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(Message message,Session session) {    // 进行消息接收处理
        //System.err.println("【*** 接收消息 ***】");
        TextMessage textMessage = (TextMessage)message;
        try{
            System.out.println(Thread.currentThread().getName()+"-------content------....."+textMessage.getText());
            message.acknowledge();
        }catch (JMSException e){
             e.printStackTrace();
        }
    }
}
