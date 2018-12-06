package cn.com.business.template;


import javax.jms.Message;

public abstract class AbstractMessageProducer implements IMessageProducer{
    public Message handleData(Object data)  throws Exception{
        return null;
    }
}
