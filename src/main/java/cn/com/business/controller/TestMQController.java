package cn.com.business.controller;

import cn.com.business.template.IMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMQController {

    @Autowired
    private IMessageProducer fileProducer;

    @RequestMapping(value="testMqSend")
    public String testMqSend(){
        fileProducer.sendMessage("这是消息内容");
        return "success";
    }
}
