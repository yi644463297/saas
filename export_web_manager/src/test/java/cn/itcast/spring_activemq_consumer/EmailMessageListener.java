package cn.itcast.spring_activemq_consumer;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class EmailMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;
        try {
            String email = mapMessage.getString("email");
            System.out.println("消费消息：" + email);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}