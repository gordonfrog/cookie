package cn.com.business.config;

import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

public class ActiveMQUtil{

    private PooledConnectionFactory pooledConnectionFactory;

    public ActiveMQUtil(PooledConnectionFactory pooledConnectionFactory){
        this.pooledConnectionFactory = pooledConnectionFactory;
    }

    public Connection getConnection() throws JMSException{
        return pooledConnectionFactory.createConnection();
    }

    public PooledConnectionFactory getPooledConnectionFactory() {
        return pooledConnectionFactory;
    }
}
