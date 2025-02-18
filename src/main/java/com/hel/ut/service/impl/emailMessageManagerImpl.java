/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service.impl;

import com.hel.ut.model.mailMessage;
import com.hel.ut.service.emailMessageManager;
import jakarta.annotation.Resource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author chad
 */
@Service
public class emailMessageManagerImpl implements emailMessageManager {
    
    @Resource(name = "myProps")
    private Properties myProps;

    @Async
    public void sendEmail(mailMessage messageDetails) throws Exception {
        
        Properties props = new Properties();
        props.put("mail.smtp.host", myProps.getProperty("mailserver.host"));
        props.put("mail.smtp.port", myProps.getProperty("mailserver.port"));
        props.put("mail.smtp.starttls.enable", "true");
	props.put("mail.smtp.ssl.protocols", myProps.getProperty("mailserver.protocols"));
        
        Session session = Session.getInstance(props);
        
        Transport transport = session.getTransport("smtp");

        transport.connect(myProps.getProperty("mailserver.host"), myProps.getProperty("mailserver.username"), myProps.getProperty("mailserver.password"));

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(messageDetails.getfromEmailAddress()));
        InternetAddress[] address = {new InternetAddress(messageDetails.gettoEmailAddress())};
        message.setRecipients(Message.RecipientType.TO, address);
        message.setReplyTo(InternetAddress.parse(messageDetails.getfromEmailAddress()));

        message.setSubject(messageDetails.getmessageSubject());
        message.setSentDate(new Date());
        
        message.setContent(messageDetails.getmessageBody(), "text/html");
        message.saveChanges();
        transport.sendMessage(message, address);
        transport.close();
    }
}