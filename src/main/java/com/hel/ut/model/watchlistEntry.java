/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author gchan
 */
@Entity
@Table(name = "dashboardwatchlistentries")
public class watchlistEntry {
    
    @Transient
    private Integer transportMethodId = 0;
    
    @Transient
    private String entryMessage = "", orgName, configName, transportMethod;
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;
    
    @Column(name = "watchlistentryId", nullable = false)
    private Integer watchlistentryId;

    @Column(name = "orgId", nullable = false)
    private Integer orgId;

    @Column(name = "configId", nullable = false)
    private Integer configId;

    @Column(name = "messageTypeId", nullable = true)
    private Integer messageTypeId;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();
    
    @Column(name = "watchListCompleted", nullable = true)
    private boolean watchListCompleted  = false;

    public Integer getTransportMethodId() {
        return transportMethodId;
    }

    public void setTransportMethodId(Integer transportMethodId) {
        this.transportMethodId = transportMethodId;
    }

    public String getEntryMessage() {
        return entryMessage;
    }

    public void setEntryMessage(String entryMessage) {
        this.entryMessage = entryMessage;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getTransportMethod() {
        return transportMethod;
    }

    public void setTransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getWatchlistentryId() {
        return watchlistentryId;
    }

    public void setWatchlistentryId(Integer watchlistentryId) {
        this.watchlistentryId = watchlistentryId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public Integer getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isWatchListCompleted() {
        return watchListCompleted;
    }

    public void setWatchListCompleted(boolean watchListCompleted) {
        this.watchListCompleted = watchListCompleted;
    }
}