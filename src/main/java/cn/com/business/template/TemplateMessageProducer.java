package cn.com.business.template;

import cn.com.business.config.AmqConfigData;

import javax.jms.*;

public class TemplateMessageProducer extends AbstractMessageProducer {

    private AmqConfigData amqConfigData;

    private Connection connection;

    private Session session;

    private MessageProducer messageProducer;

    private Destination destination;

    //是否开启事务  默认不开启
    private boolean openTrancation=false;

    //默认自动确认消息
    private int ackownledge = Session.AUTO_ACKNOWLEDGE;

    //默认不开启持久化
    private int isPersistent = DeliveryMode.NON_PERSISTENT;

    //是否topic 默认不是 则为 queue
    private boolean topic = false;

    //队列或者主题名
    private String mqName="";


    public TemplateMessageProducer(){

    }



    public TemplateMessageProducer(AmqConfigData amqConfigData){
        this.amqConfigData = amqConfigData;
        this.refreshProperties(amqConfigData);
    }
    public boolean sendMessage(Object data) {
        try{
            Message message = handleData(data);
            send(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            afterBusiness();
        }
    }

    public void send(Message message) {
        try{
        this.messageProducer.send(message);
            if(this.openTrancation){
                this.session.commit();
            }
        }catch (JMSException e){
            e.printStackTrace();
        }
    }

    public void initData() {

        if(this.connection != null){
            boolean ok = true;
            try{
                this.connection.start();
                this.session = this.connection.createSession(openTrancation,ackownledge);
                if(this.topic){
                    this.destination = this.session.createTopic(this.mqName);
                }else{
                    this.destination = this.session.createQueue(this.mqName);
                }
                this.messageProducer = this.session.createProducer(destination);
                this.messageProducer.setDeliveryMode(this.isPersistent);
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
            if(session != null){
                session.close();
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
            this.connection = amqConfigData.getConnection();
            this.topic = amqConfigData.isTopic();
            this.isPersistent = amqConfigData.getIsPersistent();
            this.ackownledge = amqConfigData.getAckownledge();
            this.mqName = amqConfigData.getMqName();
            this.openTrancation = amqConfigData.isOpenTrancation();
            this.initData();
        }
    }

}
