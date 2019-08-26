package cn.itcast;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 队列消息，消息的发送方
 */
public class QueueProducer {
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
        Queue queue = session.createQueue("email");
        //6.创建消息生产者
        MessageProducer producer = session.createProducer(queue);
        //7.创建消息
        TextMessage message = session.createTextMessage("发邮件...");
        //8.发送消息
        producer.send(message);
        System.out.println("发送消息：发邮件...");
        session.close();;
        connection.close();
    }
}