package cn.com.business.template;

import cn.com.business.config.ActiveMQUtil;
import cn.com.business.config.AmqConfigData;

import javax.jms.*;
import javax.jms.IllegalStateException;

public class TemplateMessageProducer extends AbstractMessageProducer {

    private AmqConfigData amqConfigData;

    ActiveMQUtil activeMQUtil;

    public TemplateMessageProducer(){

    }

    public TemplateMessageProducer(AmqConfigData amqConfigData, ActiveMQUtil activeMQUtil){
        this.activeMQUtil = activeMQUtil;
        this.refreshProperties(amqConfigData);
    }
    public boolean sendMessage(Object data) {
        try{
            Message message = handleData(data);
            send(message);
            return true;
        }catch (Exception e){
            System.err.println("sendMessage failed :"+e.getMessage());
            return false;
        }finally {
            afterBusiness();
        }
    }

    public void send(Message message) throws Exception{
        try{
            if(this.amqConfigData.getMessageProducer() == null){
                throw new JMSException("MessageProducer is null","1111");
            }
            this.amqConfigData.getMessageProducer().send(message);
            if(this.amqConfigData.isOpenTrancation()){
                this.amqConfigData.getSession().commit();
            }
        }catch (JMSException e){
            //mq连接曾经可用 所以存在连接池 但是后来activemq连接失败
            if((e instanceof IllegalStateException && e.getMessage().equals("The producer is closed"))
                    ||
                    //mq初始化失败 导致producer是null
                    "1111".equals(e.getErrorCode())
                    ){
                //需要达到效果是 重启mq后 无需重启应用服务器
                //目前方案缺点 需要不断重试才能发现mq服务是否正常
                try{
                    this.refreshConnection();
                    this.refreshProperties(amqConfigData);
                    this.amqConfigData.getMessageProducer().send(message);
                }catch (JMSException jmsException){
                    System.err.println("retry send error "+jmsException.getMessage());
                }
            }else{
                e.printStackTrace();
            }

        }finally {
            if(message != null){
                message.clearBody();
            }
        }
    }

    public void initData() {

        if(this.amqConfigData.getConnection() != null){
            boolean ok = true;
            try{
                this.amqConfigData.getConnection().start();
                this.amqConfigData.setSession(this.amqConfigData.getConnection().createSession(
                        this.amqConfigData.isOpenTrancation(),this.amqConfigData.getAckownledge()));
                if(this.amqConfigData.isTopic()){
                    this.amqConfigData.setDestination(this.amqConfigData.getSession().createTopic(this.amqConfigData.getMqName()));
                }else{
                    this.amqConfigData.setDestination(this.amqConfigData.getSession().createQueue(this.amqConfigData.getMqName()));
                }
                this.amqConfigData.setMessageProducer(this.amqConfigData.getSession().createProducer(this.amqConfigData.getDestination()));
                this.amqConfigData.getMessageProducer().setDeliveryMode(this.amqConfigData.getIsPersistent());
            }catch (Exception e){
                e.printStackTrace();
                ok = false;
            }finally {
                if(!ok){
                    afterBusiness();
                }
            }
        }else{
            System.err.println("-------activeMQConnectionFactory is null ------------");
        }
    }

    public void afterBusiness() {
        try{

            if(!this.amqConfigData.isOpenTrancation() && this.amqConfigData.getSession() != null){
                this.amqConfigData.getSession().close();
            }
            //if(connection != null){
            //    connection.stop();
            //}
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * connection 更新的时候未做关闭 是因为从连接池中获取
     * @param amqConfigData
     */
    public void refreshProperties(AmqConfigData amqConfigData){
        if(amqConfigData != null){
            this.amqConfigData = amqConfigData;
            this.initData();
        }
    }


    private void refreshConnection() throws JMSException{
        if(this.amqConfigData.getConnection() != null){
            this.amqConfigData.getConnection().close();
        }
        this.amqConfigData.setConnection(activeMQUtil.getConnection());
    }
}
