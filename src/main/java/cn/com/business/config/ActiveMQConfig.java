package cn.com.business.config;


import cn.com.business.producer.FileProducer;
import cn.com.business.producer.TextProducer;
import cn.com.business.template.IMessageProducer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Session;


@Configuration
@EnableJms
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String url;

    @Value("${spring.activemq.user}")
    private String userName;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("${business.textQueue}")
    private String textQueue;

    public ActiveMQConnectionFactory activeMQConnectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName,password,url);
        connectionFactory.setUseCompression(true);
        return connectionFactory;
    }
    /*
    @Bean
    public PooledConnectionFactory pooledConnectionFactory(){
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory());
        pooledConnectionFactory.setMaxConnections(20);
        return pooledConnectionFactory;
    }
    */

    @Bean
    public ActiveMQUtil activeMQUtil(){
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory());
        pooledConnectionFactory.setMaxConnections(20);
        return new ActiveMQUtil(pooledConnectionFactory);
    }

    @Bean("fileProducer")
    public IMessageProducer fileProducer(ActiveMQUtil activeMQUtil){
        AmqConfigData amqConfigData = new AmqConfigData();
        try{
            amqConfigData.setIsPersistent(DeliveryMode.PERSISTENT);
            amqConfigData.setAckownledge(Session.CLIENT_ACKNOWLEDGE);
            amqConfigData.setMqName("testMqSpring");
            amqConfigData.setConnection(activeMQUtil.getConnection());
            return new FileProducer(amqConfigData,activeMQUtil);
        }catch (JMSException e){
            System.err.println(" init fileProducer error "+e.getMessage());
        }
        return new FileProducer(amqConfigData,activeMQUtil);
    }


    @Bean("textProducer")
    public IMessageProducer textProducer(ActiveMQUtil activeMQUtil){
        AmqConfigData amqConfigData = new AmqConfigData();
        try{
            amqConfigData.setIsPersistent(DeliveryMode.PERSISTENT);
            amqConfigData.setAckownledge(Session.CLIENT_ACKNOWLEDGE);
            amqConfigData.setMqName(textQueue);
            amqConfigData.setConnection(activeMQUtil.getConnection());
            return  new TextProducer(amqConfigData,activeMQUtil);
        }catch (JMSException e){
            System.err.println(" init textProducer error "+e.getMessage());
        }
        return new TextProducer(amqConfigData,activeMQUtil);
    }



    /**
     * 控制 jmslistener连接
     * @param
     * @return
     */
    @Bean("jmsListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ActiveMQUtil activeMQUtil) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory((ActiveMQConnectionFactory)activeMQUtil.getPooledConnectionFactory().getConnectionFactory());
        //设置消息确认模式 这里设置的是单挑消息确认 由于和spring集成 用client确认模式 会被spring自动确认
        factory.setSessionAcknowledgeMode(4);
        //设置连接数
        factory.setConcurrency("1");
        return factory;
    }














}
