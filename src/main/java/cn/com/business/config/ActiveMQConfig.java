package cn.com.business.config;


import cn.com.business.producer.FileProducer;
import cn.com.business.template.IMessageProducer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.DeliveryMode;
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

    public ActiveMQConnectionFactory activeMQConnectionFactory(){
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(userName,password,url);
        connectionFactory.setUseCompression(true);
        return connectionFactory;
    }
    @Bean
    public PooledConnectionFactory pooledConnectionFactory(){
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory());
        pooledConnectionFactory.setMaxConnections(20);
        return pooledConnectionFactory;
    }
    @Bean("fileProducer")
    public IMessageProducer fileProducer(PooledConnectionFactory pooledConnectionFactory) throws Exception{
        AmqConfigData amqConfigData = new AmqConfigData();
        amqConfigData.setConnection(pooledConnectionFactory.createConnection());
        amqConfigData.setIsPersistent(DeliveryMode.PERSISTENT);
        amqConfigData.setAckownledge(Session.CLIENT_ACKNOWLEDGE);
        amqConfigData.setMqName("testMqSpring");
        IMessageProducer iMessageProducer = new FileProducer(amqConfigData);

        return iMessageProducer;
    }


    /**
     * 控制 jmslistener连接
     * @param pooledConnectionFactory
     * @return
     */
    @Bean("jmsListenerContainerFactory")
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(PooledConnectionFactory pooledConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory((ActiveMQConnectionFactory)pooledConnectionFactory.getConnectionFactory());
        factory.setSessionAcknowledgeMode(4);
        //设置连接数
        factory.setConcurrency("3");
        return factory;
    }














}
