package com.activemq;

import com.activemq.listener.FileMessageListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ActiveMqReceiver {

    private static final String SUBJECT = "test-activemq-queue";

    public static void main(String[] args)throws Exception{

        //初始化ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
        //创建mq连接
        Connection conn = connectionFactory.createConnection();
        //启动连接
        conn.start();

        //创建会话 不开启事务 并由手动确认消息
        Session session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        //通过会话创建目标
        Destination dest = session.createQueue(SUBJECT);
        //创建mq消息的消费者
        MessageConsumer consumer = session.createConsumer(dest);

        //初始化MessageListener

        //给消费者设定监听对象
        consumer.setMessageListener(new FileMessageListener());
    }
}
