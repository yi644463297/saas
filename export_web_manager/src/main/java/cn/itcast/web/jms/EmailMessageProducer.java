package cn.itcast.web.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class EmailMessageProducer {

   @Autowired
   private JmsTemplate jmsTemplate;

   public void sendMailMessage(String to,String subject,String content) {
      jmsTemplate.send("email", session -> {
         MapMessage mapMessage = session.createMapMessage();
         mapMessage.setString("to",to);
         mapMessage.setString("subject",subject);
         mapMessage.setString("content",content);
         return mapMessage;
      });
   }
}