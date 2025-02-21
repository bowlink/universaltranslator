/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

/**
 *
 * @author chad
 */
public class mailMessage {

    String messageSubject = null, toEmailAddress = null, fromEmailAddress = null,  messageBody = null;
    String[] ccEmailAddress = null, bccEmailAddress = null;

    public String getMessageSubject() {
        return messageSubject;
    }

    public void setMessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public void setToEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    public String[] getCcEmailAddress() {
        return ccEmailAddress;
    }

    public void setCcEmailAddress(String[] ccEmailAddress) {
        this.ccEmailAddress = ccEmailAddress;
    }

    public String[] getBccEmailAddress() {
        return bccEmailAddress;
    }

    public void setBccEmailAddress(String[] bccEmailAddress) {
        this.bccEmailAddress = bccEmailAddress;
    }

    public String getFromEmailAddress() {
        return fromEmailAddress;
    }

    public void setFromEmailAddress(String fromEmailAddress) {
        this.fromEmailAddress = fromEmailAddress;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}