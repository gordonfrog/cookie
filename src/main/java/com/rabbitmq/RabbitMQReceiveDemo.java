package com.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import factory.BusinessKryoFactory;
import model.FileMessage;
import model.MessageData;
import util.KryoUtil;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Date;

public class RabbitMQReceiveDemo {

    private final static String QUEUE_NAME = "sync-message";
    //private static String fileName= "C:\\Users\\Administrator\\Desktop\\test123456.rar";
    private static String fileName= "C:\\Users\\Administrator\\Desktop\\123123.exe";
    private static BusinessKryoFactory businessKryoFactory = new BusinessKryoFactory();

    public static void main(String[] args)throws Exception{

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);

        channel.basicConsume(QUEUE_NAME, false, consumer);


        while (true) {
            try{
                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                MessageData messageData = (MessageData) KryoUtil.parseSerializable(delivery.getBody());
                FileMessage fileMessage = null;
                if(messageData instanceof FileMessage){
                    fileMessage = (FileMessage)messageData;
                }

                System.out.println("开始时间:"+ new Date());
                File file = new File(fileMessage.getFileName());
                FileOutputStream outputStream = new FileOutputStream(file);
                FileChannel fileChannel = outputStream.getChannel();
                System.out.println("-------"+fileMessage.getFileByte().length);
                int bufferLength = 1024;
                int length = fileMessage.getFileByte().length;
                ByteArrayInputStream fis  = new ByteArrayInputStream(fileMessage.getFileByte());
                byte[] b  = new byte[bufferLength*bufferLength];

                int n = 0;
                while((n=fis.read(b))!= -1){
                    outputStream.write(b);
                }
                fileChannel.close();
                outputStream.close();
                System.out.println("结束时间:"+ new Date());
                System.out.println("threadName:"+Thread.currentThread().getName()+" [x] Received end");
                //手动确认消息
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(),true);

            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }
}
