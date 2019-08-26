package cn.itcast.spring_activemq_producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Spring整合ActiveMQ发送消息
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-activemq-send.xml")
public class Sender {

    // 注入发送Queue、Topic消息的模板对象
    @Resource
    private JmsTemplate jmsQueueTemplate;
    @Resource
    private JmsTemplate jmsTopicTemplate;

    @Test
    public void senderQueue() throws Exception {
        // 发送Queue队列消息
        jmsQueueTemplate.send("email", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("email", "610731230@qq.com");
                return mapMessage;
            }
        });
    }

    @Test
    public void senderTopic() throws Exception {
        // 发送Queue队列消息
        jmsTopicTemplate.send("sms", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("sms", "18665591009");
                return mapMessage;
            }
        });
    }
}