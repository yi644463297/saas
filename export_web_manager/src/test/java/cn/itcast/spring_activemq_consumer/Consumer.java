package cn.itcast.spring_activemq_consumer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Spring整合ActiveMQ消费消息
 */
public class Consumer {
    public static void main(String[] args) throws IOException {
        ApplicationContext ac =
                new ClassPathXmlApplicationContext("applicationContext-activemq-consumer.xml");
        System.in.read();
    }
}