package cn.itcast;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 主题消息，消息的消费方
 */
public class TopicConsumer {
    public static void main(String[] args) throws Exception {
        //1.创建连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.58.100:61616");
        //2.创建连接
        Connection connection = factory.createConnection();
        //3.打开连接
        connection.start();
        //4.创建session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5.创建目标地址（Queue:点对点消息，Topic：发布订阅消息）
        Topic topic = session.createTopic("sms");
        //6.创建消息的消费者
        MessageConsumer consumer = session.createConsumer(topic);
        //7.配置消息监听器
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
                try {
                    System.out.println("消费消息：" + textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}