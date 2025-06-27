/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.ArrayList;

/**
 *
 * @author chadmccue
 */
public class emailMessage {

    String messageSubject = null, toEmailAddress = null, fromEmailAddress = null, messageBody, attachmentFileLocation = null;
    String[] ccEmailAddress = null,bccEmailAddress = null;
    ArrayList<String> bccEmailAddresses;

    public void setmessageSubject(String messageSubject) {
        this.messageSubject = messageSubject;
    }

    public String getmessageSubject() {
        return messageSubject;
    }

    public void settoEmailAddress(String toEmailAddress) {
        this.toEmailAddress = toEmailAddress;
    }

    public String gettoEmailAddress() {
        return toEmailAddress;
    }

    public void setfromEmailAddress(String fromEmailAddress) {
        this.fromEmailAddress = fromEmailAddress;
    }

    public String getfromEmailAddress() {
        return fromEmailAddress;
    }

    public void setmessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getmessageBody() {
        return messageBody;
    }

    public void setccEmailAddress(String[] ccEmailAddress) {
        this.ccEmailAddress = ccEmailAddress;
    }

    public String[] getccEmailAddress() {
        return ccEmailAddress;
    }

    /**
     * @return the bccEmailAddress
     */
    public String[] getBccEmailAddress() {
        return bccEmailAddress;
    }

    /**
     * @param bccEmailAddress the bccEmailAddress to set
     */
    public void setBccEmailAddress(String[] bccEmailAddress) {
        this.bccEmailAddress = bccEmailAddress;
    }

    public ArrayList<String> getBccEmailAddresses() {
	return bccEmailAddresses;
    }

    public void setBccEmailAddresses(ArrayList<String> bccEmailAddresses) {
	this.bccEmailAddresses = bccEmailAddresses;
    }

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

    public String getAttachmentFileLocation() {
        return attachmentFileLocation;
    }

    public void setAttachmentFileLocation(String attachmentFileLocation) {
        this.attachmentFileLocation = attachmentFileLocation;
    }

    public String[] getCcEmailAddress() {
        return ccEmailAddress;
    }

    public void setCcEmailAddress(String[] ccEmailAddress) {
        this.ccEmailAddress = ccEmailAddress;
    }

    
    
}
