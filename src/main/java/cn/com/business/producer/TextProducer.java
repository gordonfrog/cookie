package cn.com.business.producer;

import cn.com.business.config.ActiveMQUtil;
import cn.com.business.config.AmqConfigData;
import cn.com.business.template.TemplateMessageProducer;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.Message;

public class TextProducer extends TemplateMessageProducer {

    public TextProducer(AmqConfigData amqConfigData,ActiveMQUtil activeMQUtil){
        super(amqConfigData,activeMQUtil);
    }

    public Message handleData(Object data) throws Exception{

        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("这是发送的消息："+ System.nanoTime()+"...."+data);
        return activeMQTextMessage;
    }
}
