package cn.com.business.controller;

import cn.com.business.template.IMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMQController {

    @Autowired
    private IMessageProducer fileProducer;

    @Autowired
    private IMessageProducer textProducer;

    @RequestMapping(value="testMqSend")
    public String testMqSend(){
        fileProducer.sendMessage("");
        return "success";
    }

    @RequestMapping(value="textQueue")
    public String textQueue() throws Exception{

        int i = 0;
        while(i<100){
            textProducer.sendMessage("这是消息内容");
            Thread.sleep(100);
            i++;
        }


        return "success";
    }
}
