/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.service.impl;

import com.hel.ut.model.emailMessage;
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
        
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", myProps.getProperty("mailserver.host"));
        properties.put("mail.smtp.port", "" + myProps.getProperty("mailserver.port"));
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.sendpartial", "true");
	properties.put("mail.smtp.ssl.protocols", myProps.getProperty("mailserver.protocols"));
	properties.put("mail.smtp.auth", "true");
        
        try {
            Session session = Session.getInstance(properties);
        
            Transport transport = session.getTransport("smtp");
            transport.connect(myProps.getProperty("mailserver.host"), myProps.getProperty("mailserver.username"), myProps.getProperty("mailserver.password"));
            
           
            emailMessage emailmessage = new emailMessage();
            
            if(!messageDetails.getToEmailAddress().equals(myProps.getProperty("admin.email"))) {
                emailmessage.settoEmailAddress(messageDetails.getToEmailAddress());
            }
            else {
                emailmessage.settoEmailAddress(myProps.getProperty("admin.email"));
            }
            
            emailmessage.setfromEmailAddress("helpdesk@health-e-link.net");
            
            if(!messageDetails.getMessageSubject().equals("")) {
                emailmessage.setmessageSubject(messageDetails.getMessageSubject() + " - " + myProps.getProperty("server.identity"));
            }
            else {
                emailmessage.setmessageSubject("Exception Error - " + myProps.getProperty("server.identity"));
            }
            emailmessage.setmessageBody(messageDetails.getMessageBody());

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailmessage.getfromEmailAddress()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailmessage.gettoEmailAddress()));

            InternetAddress[] replyToAddress = {new InternetAddress(emailmessage.getfromEmailAddress())};
            message.setReplyTo(replyToAddress);
            message.setSubject(emailmessage.getmessageSubject());
            message.setSentDate(new Date());

            message.setContent(emailmessage.getmessageBody(), "text/html; charset=utf-8");

            message.saveChanges();

            try {
                transport.sendMessage(message, message.getAllRecipients());
            }
            catch (jakarta.mail.SendFailedException ex) {
                if(ex.getInvalidAddresses() != null) {
                    if(ex.getInvalidAddresses().length > 0) {
                        Address[] invalidAddresses = ex.getInvalidAddresses();
                        StringBuilder addressStr = new StringBuilder();
                        for (Address address : invalidAddresses) {
                                addressStr.append(address.toString()).append("; ");
                        }
                    }
                }
            }
            catch (Exception ex2) {
                System.out.println("Exception Error: " + ex2.getMessage());
            }
             transport.close();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }  
    }
}