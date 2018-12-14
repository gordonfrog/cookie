package cn.com.business.producer;

import cn.com.business.config.AmqConfigData;
import cn.com.business.template.TemplateMessageProducer;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.Message;
import java.util.Date;

public class TextProducer extends TemplateMessageProducer {

    public TextProducer(AmqConfigData amqConfigData){
        super(amqConfigData);
    }

    public Message handleData(Object data) throws Exception{

        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("这是发送的消息："+ System.nanoTime()+"...."+data);
        return activeMQTextMessage;
    }
}
