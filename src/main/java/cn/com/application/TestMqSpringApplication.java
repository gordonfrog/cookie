package cn.com.application;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "cn.com.business.config",
        "cn.com.business.controller",
        "cn.com.business.consumer",
})
public class TestMqSpringApplication {

    public static void main(String[] args){
        SpringApplication.run(TestMqSpringApplication.class, args);
    }
}
