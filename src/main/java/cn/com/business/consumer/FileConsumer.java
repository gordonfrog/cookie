package cn.com.business.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Service
public class FileConsumer {

    @JmsListener(destination="testMqSpring",containerFactory = "jmsListenerContainerFactory")
    public void receiveMessage(Message message,Session session) {    // 进行消息接收处理
        System.err.println("【*** 接收消息 ***】");
        try{
            if(message instanceof TextMessage){
                System.out.println("message:"+((TextMessage) message).getText());
            }
            message.acknowledge();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
