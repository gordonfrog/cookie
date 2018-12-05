package com.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class ActiveMqGroupProducer {

    private static String url = "tcp://localhost:61616";

    private static final String SUBJECT = "test-activemq-queue";


    //private static String fileName= "C:\\Users\\Administrator\\Desktop\\ZXXK20064271713256016";
    //private static String fileExt = ".avi";

    private static String fileName= "D:\\ChromeCoreDownloads\\mysql57221";
    private static String fileExt = ".zip";

    private long size = 1024*1024; //1MB

    private static ByteBuffer bbuf;

    private int bufferLength = 1024;

    public ActiveMqGroupProducer(){
        this.bbuf = ByteBuffer.allocate(bufferLength*bufferLength);
    }

    public static void main(String[] rags) throws Exception{

        //初始化连接工厂
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

        //获得连接
        Connection conn = connectionFactory.createConnection();
        //启动连接
        conn.start();

        //创建Session，此方法第一个参数表示会话是否在事务中执行，第二个参数设定会话的应答模式
        //若是group方式传递消息 则需要开始事务消息 最终一次commit
        //其他方式一次提交一条消息则可以不用开启事务消息
        Session session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
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
        ActiveMqGroupProducer activeMqProducer = new ActiveMqGroupProducer();
        boolean doSuccess = true;
        while(doSuccess){
            activeMqProducer.startDemo(producer,bytesMessage,session);
            //确认消息
            //session.commit();
            Thread.sleep(20*1000);
        }
        //关闭mq连接
        conn.close();

    }


    public void startDemo(MessageProducer producer,Message message,Session session) throws Exception{
        RandomAccessFile randomAccessFile = null;
        FileChannel fc = null;
        BytesMessage bytesMessage = null;
        try{
            if(message instanceof BytesMessage){
                System.out.println("发送开始:"+new Date());

                long groupId = System.nanoTime();

                //设置文件生成路径以及名称
                String targetFileName = "D:\\testMqSendFile\\"+groupId+fileExt;

                int n = 0;
                File file = new File(fileName+fileExt);

                System.out.println(file.length());

                randomAccessFile = new RandomAccessFile(file,"r");
                fc  = randomAccessFile.getChannel();

                bytesMessage = (BytesMessage)message;
                bytesMessage.setStringProperty("fileName",targetFileName);
                bytesMessage.setStringProperty("JMXGroupID",groupId+"");
                int i = 1;
                int currentsize = 0;
                while((n=fc.read(bbuf))!=-1){
                    bytesMessage.writeBytes(bbuf.array(),0,n);
                    currentsize += bbuf.position();
                    bbuf.clear();

                    if(currentsize > 40*size){
                        bytesMessage.setIntProperty("JMSXGroupSeq", i);
                        producer.send(bytesMessage);
                        bytesMessage.clearBody();
                        System.out.println("第"+i+"次发送");
                        currentsize=0;
                        i++;
                        //确认消息
                        //session.commit();
                    }
                }
                //设置文件字节流
                //System.out.println(" [x] Sent '" + message + "'");
                bbuf.clear();

                //关闭一个Message Groups
                bytesMessage.setIntProperty("JMSXGroupSeq", -1);

                //发送消息
                producer.send(bytesMessage);
                //session.commit();
                bytesMessage.clearBody();
                bytesMessage.clearProperties();
                System.out.println("第"+i+"次发送");
                System.out.println("发送完毕:"+new Date());
            }


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
