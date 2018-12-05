package com.activemq;

import com.activemq.listener.FileMessageBlobMessageListener;
import com.activemq.listener.FileMessageListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;

public class ActiveMqBlobMessageReceiver {

    private static final String SUBJECT = "test-activemq-queue";
    private static String url = "tcp://localhost:61616?jms.blobTransferPolicy.defaultUploadUrl=http://localhost:8161/fileserver/";

    public static void main(String[] args)throws Exception{

        //初始化ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "admin",
                "admin",
                 url);
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
        consumer.setMessageListener(new FileMessageBlobMessageListener());
    }
}
