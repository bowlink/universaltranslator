/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONCONNECTIONRECEIVERS")
public class configurationConnectionReceivers {
    
    @Transient
    private String contactType;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONNECTIONID", nullable = false)
    private Integer connectionId;

    @Column(name = "emailAddress", nullable = false)
    private String emailAddress;

    @Column(name = "sendEmailNotifications", nullable = false)
    private Boolean sendEmailNotifications = false;

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Integer connectionId) {
        this.connectionId = connectionId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Boolean getSendEmailNotifications() {
        return sendEmailNotifications;
    }

    public void setSendEmailNotifications(Boolean sendEmailNotifications) {
        this.sendEmailNotifications = sendEmailNotifications;
    }
}