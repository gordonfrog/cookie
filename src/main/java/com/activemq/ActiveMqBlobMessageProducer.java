package com.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;

import javax.jms.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class ActiveMqBlobMessageProducer {

    private static String url = "tcp://localhost:61616?jms.blobTransferPolicy.defaultUploadUrl=http://localhost:8080/fileserver/";
    //private static String url = "tcp://localhost:61616";

    private static final String SUBJECT = "test-activemq-queue";


    //private static String fileName= "C:\\Users\\Administrator\\Desktop\\ZXXK20064271713256016";
    //private static String fileExt = ".avi";


    //private static String fileName= "D:\\ChromeCoreDownloads\\34278383465125";
    //private static String fileExt = ".zip";


    private static String fileName= "D:\\ChromeCoreDownloads\\mysql57221";
    private static String fileExt = ".zip";





    private static ByteBuffer bbuf;

    private int bufferLength = 1024;

    public ActiveMqBlobMessageProducer(){
        this.bbuf = ByteBuffer.allocate(bufferLength*bufferLength);
    }

    public static void main(String[] rags) throws Exception{

        //初始化连接工厂
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                "admin",
                "admin",
                url);

        connectionFactory.setUseCompression(true);

        //获得连接
        Connection conn = connectionFactory.createConnection();
        //启动连接
        conn.start();

        //创建Session，此方法第一个参数表示会话是否在事务中执行，第二个参数设定会话的应答模式
        //若是group方式传递消息 则需要开始事务消息 最终一次commit
        //其他方式一次提交一条消息则可以不用开启事务消息
        ActiveMQSession session = (ActiveMQSession)conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        //创建队列
        Destination dest = session.createQueue(SUBJECT);

        //createTopic方法用来创建Topic
        //session.createTopic("TOPIC");

        //通过session可以创建消息的生产者
        MessageProducer producer = session.createProducer(dest);
        // 设置不持久化，此处学习，实际根据项目决定
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

        //初始化一个mq消息
        //TextMessage message = session.createTextMessage("hello active mq 中文" );
        BytesMessage bytesMessage  = session.createBytesMessage();

        //TextMessage textMessage = session.createTextMessage("这是文本消息!");

        //发送消息
        ActiveMqBlobMessageProducer activeMqProducer = new ActiveMqBlobMessageProducer();
        boolean doSuccess = true;
        //while(doSuccess){
            activeMqProducer.startDemo(producer,bytesMessage,session);
            //确认消息
            //session.commit();
            Thread.sleep(20*1000);
        //}
        //关闭mq连接
        conn.close();

    }


    public void startDemo(MessageProducer producer,Message message,Session session) throws Exception{
        RandomAccessFile randomAccessFile = null;
        FileChannel fc = null;
        BlobMessage blobMessageblobMessage = null;
        try{

            ActiveMQSession activeMQSession = (ActiveMQSession)session;

            File file = new File(fileName+fileExt);

            blobMessageblobMessage = activeMQSession.createBlobMessage(file);

            String targetFileName = "D:\\testMqSendFile\\"+System.nanoTime()+fileExt;

            blobMessageblobMessage.setStringProperty("fileName",targetFileName);

            blobMessageblobMessage.setLongProperty("fileSize",file.length());

            producer.send(blobMessageblobMessage);

        }catch (Exception e){
            e.printStackTrace();

            Thread.sleep(5*1000);
        }finally {
            System.gc();
            try{
                if(fc != null){
                    fc.close();
                }
                if(randomAccessFile != null){
                    randomAccessFile.close();
                }
            }catch (Exception ee){
                ee.printStackTrace();
            }

        }
    }
}
