package cn.itcast.jms;

import cn.itcast.utils.MailUtil;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Component
public class EmailMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;
        try {
            String to = mapMessage.getString("to");
            String subject = mapMessage.getString("subject");
            String content = mapMessage.getString("content");
            System.out.println(to);
            System.out.println(subject);
            System.out.println(content);
            //3.调用工具类发送邮件
            MailUtil.sendMsg(to,subject,content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}