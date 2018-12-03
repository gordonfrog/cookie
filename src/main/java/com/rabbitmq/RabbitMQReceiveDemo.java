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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RabbitMQReceiveDemo {

    private final static String QUEUE_NAME = "sync-message";
    //private static String fileName= "C:\\Users\\Administrator\\Desktop\\test123456.rar";
    private static BusinessKryoFactory businessKryoFactory = new BusinessKryoFactory();

    public static void main(String[] args){
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            Map<String,Object> paraMap = new HashMap<String,Object>();
            paraMap.put("x-queue-mode","lazy");

            channel.queueDeclare(QUEUE_NAME, true, false, false, paraMap);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(QUEUE_NAME, false, consumer);
            int bufferLength = 1024;
            byte[] b  = new byte[bufferLength*bufferLength];
            ByteBuffer bbuf = ByteBuffer.allocate(bufferLength*bufferLength);
            FileMessage fileMessage = null;
            RandomAccessFile randomAccessFile = null;
            FileChannel fileChannel = null;
            ByteArrayInputStream fis = null;
            while (true) {
                try{
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    MessageData messageData = (MessageData) KryoUtil.parseSerializable(delivery.getBody());

                    if(messageData instanceof FileMessage){
                        fileMessage = (FileMessage)messageData;
                    }
                    System.out.println("开始时间:"+ new Date());
                    randomAccessFile = new RandomAccessFile(new File(fileMessage.getFileName()),"rw");
                    fileChannel = randomAccessFile.getChannel();
                    System.out.println("-------"+fileMessage.getFileByte().length);

                    fis = new ByteArrayInputStream(fileMessage.getFileByte());

                    int n = 0;
                    while((n=fis.read(b))!= -1){
                        bbuf.put(b,0,n);
                        //转换缓存区
                        bbuf.flip();
                        fileChannel.write(bbuf);
                        bbuf.clear();
                    }
                    bbuf.clear();
                    fis.close();
                    fileChannel.close();
                    randomAccessFile.close();;
                    System.out.println("结束时间:"+ new Date());
                    System.out.println("threadName:"+Thread.currentThread().getName()+" [x] Received end");
                    //手动确认消息
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),true);
                }catch (Exception e){
                    e.printStackTrace();
                    //TODO 记录异常
                    Thread.sleep(5*1000);
                }finally {
                    if(fis != null){
                        fis.close();
                    }
                    if(fileChannel != null){
                        fileChannel.close();
                    }
                    if(randomAccessFile != null){
                        randomAccessFile.close();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }
}
