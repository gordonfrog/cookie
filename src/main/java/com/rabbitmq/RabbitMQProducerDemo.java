package com.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.FileMessage;
import util.KryoUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RabbitMQProducerDemo {

    private final static String QUEUE_NAME = "sync-message";
    //private final static String EXCHANGE_NAME = "exchange-hello";

    private static String fileName= "C:\\Users\\Administrator\\Desktop\\Postman6.1.4";
    //private static String fileName= "C:\\Users\\Administrator\\Desktop\\BaiduNetdisk-6.2.4";
    private static String fileExt = ".rar";
    public static void main(String[] args)throws Exception{

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        //factory.setUsername("mengday");
        //factory.setPassword("mengday");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Map<String,Object> paraMap = new HashMap<String,Object>();
        paraMap.put("x-queue-mode","lazy");

        // 声明一个接收被删除的消息的交换机和队列
        //Dead letter exchange(死亡交换机) 和 Dead letter routing key(死亡路由键)
        //当队列中的消息过期，或者达到最大长度而被删除，
        //或者达到最大空间时而被删除时，可以将这些被删除的信息推送到其他交换机中，
        //让其他消费者订阅这些被删除的消息，处理这些消息
        //String EXCHANGE_DEAD_NAME = "exchange.dead";
        //String QUEUE_DEAD_NAME = "queue_dead";
        //channel.exchangeDeclare(EXCHANGE_DEAD_NAME, BuiltinExchangeType.DIRECT);
        //channel.queueDeclare(QUEUE_DEAD_NAME, false, false, false, null);
        //channel.queueBind(QUEUE_DEAD_NAME, EXCHANGE_DEAD_NAME, "routingkey.dead");


        //String EXCHANGE_NAME = "exchange.fanout";
        //String QUEUE_NAME = "queue_name";
        //channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        Map<String, Object> arguments = new HashMap<String, Object>();
        //消息剩余生存时间
        arguments.put("x-message-ttl", 15000);
        //x-max-length:用于指定队列的长度，如果不指定，可以认为是无限长，例如指定队列的长度是4，当超过4条消息，前面的消息将被删除，给后面的消息腾位
        arguments.put("x-max-length", 4);
        //x-max-length-bytes: 用于指定队列存储消息的占用空间大小，当达到最大值是会删除之前的数据腾出空间
        arguments.put("x-max-length-bytes", 1024);
        //x-expires用于当多长时间没有消费者访问该队列的时候，该队列会自动删除，
        //可以设置一个延迟时间，如仅启动一个生产者，10秒之后该队列会删除，或者启动一个生产者，再启动一个消费者，消费者运行结束后10秒，队列也会被删除
        arguments.put("x-expires", 30000);

        // 将删除的消息推送到指定的交换机，一般x-dead-letter-exchange和x-dead-letter-routing-key需要同时设置
        arguments.put("x-dead-letter-exchange", "exchange.dead");
        // 将删除的消息推送到指定的交换机对应的路由键
        arguments.put("x-dead-letter-routing-key", "routingkey.dead");
        // 设置消息的优先级，优先级大的优先被消费
        arguments.put("x-max-priority", 10);
        Object put = arguments.put("x-dead-letter-routing-key", "routingkey.dead");


        /**
         * 队列持久化
         * queue_name 队列名称
         * true durable 是否持久化
         * autoDelete 是否自动删除 当最后一个消费者断开连接之后队列是否自动被删除，可以通过RabbitMQ Management，查看某个队列的消费者数量，当consumers = 0时队列就会自动删除
         * arguments
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, paraMap);

        //String message = "Hello World!";

        boolean doSuccess = true;

        while(doSuccess){

            FileMessage fileMessage = new FileMessage();
            //设置文件生成路径以及名称
            fileMessage.setFileName("C:\\Users\\Administrator\\Desktop\\"+System.nanoTime()+fileExt);
            byte[] b = new byte[1024];
            int n = 0;
            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(fileName+fileExt),"r");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while((n=randomAccessFile.read(b))!=-1){
                out.write(b);
            }
            //设置文件字节流
            fileMessage.setFileByte(out.toByteArray());
            AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties().builder();
            // 设置消息是否持久化，1： 非持久化 2：持久化
            properties.deliveryMode(2);

            channel.basicPublish("", QUEUE_NAME, properties.build(), KryoUtil.doSerializable(fileMessage));
            //System.out.println(" [x] Sent '" + message + "'");

            randomAccessFile.close();
            out.close();

            System.out.println("发送完毕:"+new Date());


            Thread.sleep(30*1000);
        }


        channel.close();
        connection.close();
    }
}
