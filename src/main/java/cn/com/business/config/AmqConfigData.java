package cn.com.business.config;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class AmqConfigData {

    private Connection connection;

    private Session session;

    private MessageProducer messageProducer;

    private Destination destination;

    private MessageConsumer messageConsumer;

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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public MessageProducer getMessageProducer() {
        return messageProducer;
    }

    public void setMessageProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public boolean isOpenTrancation() {
        return openTrancation;
    }

    public void setOpenTrancation(boolean openTrancation) {
        this.openTrancation = openTrancation;
    }

    public int getAckownledge() {
        return ackownledge;
    }

    public void setAckownledge(int ackownledge) {
        this.ackownledge = ackownledge;
    }

    public int getIsPersistent() {
        return isPersistent;
    }

    public void setIsPersistent(int isPersistent) {
        this.isPersistent = isPersistent;
    }

    public String getMqName() {
        return mqName;
    }

    public void setMqName(String mqName) {
        this.mqName = mqName;
    }

    public boolean isTopic() {
        return topic;
    }

    public void setTopic(boolean topic) {
        this.topic = topic;
    }

    public MessageConsumer getMessageConsumer() {
        return messageConsumer;
    }

    public void setMessageConsumer(MessageConsumer messageConsumer) {
        this.messageConsumer = messageConsumer;
    }
}
