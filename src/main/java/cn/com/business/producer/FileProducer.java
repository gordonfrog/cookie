package cn.com.business.producer;

import cn.com.business.config.AmqConfigData;
import cn.com.business.template.TemplateMessageProducer;
import org.apache.activemq.command.ActiveMQBytesMessage;

import javax.jms.Message;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

public class FileProducer extends TemplateMessageProducer {

    private static String fileName= "D:\\ChromeCoreDownloads\\34278383465125";
    private static String fileExt = ".zip";

    private ByteBuffer bbuf;

    public FileProducer(AmqConfigData amqConfigData){
        super(amqConfigData);
        this.bbuf = ByteBuffer.allocate(1024*1024);
    }

    public Message handleData(Object data) throws Exception{
        //ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        //textMessage.setText(data.toString());

        ActiveMQBytesMessage bytesMessage = new ActiveMQBytesMessage();
        RandomAccessFile randomAccessFile = null;
        FileChannel fc = null;
        try{
                System.out.println("发送开始:"+new Date());
                //设置文件生成路径以及名称
                String targetFileName = "D:\\testMqSendFile\\"+System.nanoTime()+fileExt;
                int n = 0;
                randomAccessFile = new RandomAccessFile(new File(fileName+fileExt),"r");
                fc  = randomAccessFile.getChannel();

                bytesMessage.setStringProperty("fileName",targetFileName);

                while((n=fc.read(bbuf))!=-1){
                    bytesMessage.writeBytes(bbuf.array(),0,n);
                    bbuf.clear();
                }
                //设置文件字节流
                bbuf.clear();
                //发送消息
                //bytesMessage.clearBody();
                //bytesMessage.clearProperties();

                System.out.println("发送完毕:"+new Date());
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
        return bytesMessage;
    }
}
