package cn.com.business.producer;

import cn.com.business.config.AmqConfigData;
import cn.com.business.template.TemplateMessageProducer;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.Message;

public class FileProducer extends TemplateMessageProducer {

    public FileProducer(AmqConfigData amqConfigData){
        super(amqConfigData);
    }

    public Message handleData(Object data) throws Exception{
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText(data.toString());
        return textMessage;
    }
}
