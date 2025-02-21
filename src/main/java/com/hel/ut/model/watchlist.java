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
@Table(name = "dashboardwatchlist")
public class watchlist {
    
    @Transient
    private String orgName, configName, messageTypeName, transportMethod, expectedTimeAMPM = "AM";

    @Transient
    private Integer expectedTimeHour = 12, expectedTimeMinute = 0, messageTypeId = 0;
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "orgId", nullable = false)
    private Integer orgId;

    @Column(name = "configId", nullable = false)
    private Integer configId;

    @Column(name = "expected", nullable = true)
    private String expected = "Daily";
    
    @Column(name = "expectFirstFile", nullable = true)
    private String expectFirstFile;
    
    @Column(name = "expectFirstFileTime", nullable = true)
    private String expectFirstFileTime;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();
    
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Column(name = "nextInsertDate", nullable = true)
    private Date nextInsertDate = new Date();
    
    @Column(name = "entryMessage", nullable = true)
    private String entryMessage;

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

    public String getMessageTypeName() {
        return messageTypeName;
    }

    public void setMessageTypeName(String messageTypeName) {
        this.messageTypeName = messageTypeName;
    }

    public String getTransportMethod() {
        return transportMethod;
    }

    public void setTransportMethod(String transportMethod) {
        this.transportMethod = transportMethod;
    }

    public String getExpectedTimeAMPM() {
        return expectedTimeAMPM;
    }

    public void setExpectedTimeAMPM(String expectedTimeAMPM) {
        this.expectedTimeAMPM = expectedTimeAMPM;
    }

    public Integer getExpectedTimeHour() {
        return expectedTimeHour;
    }

    public void setExpectedTimeHour(Integer expectedTimeHour) {
        this.expectedTimeHour = expectedTimeHour;
    }

    public Integer getExpectedTimeMinute() {
        return expectedTimeMinute;
    }

    public void setExpectedTimeMinute(Integer expectedTimeMinute) {
        this.expectedTimeMinute = expectedTimeMinute;
    }

    public Integer getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getExpectFirstFile() {
        return expectFirstFile;
    }

    public void setExpectFirstFile(String expectFirstFile) {
        this.expectFirstFile = expectFirstFile;
    }

    public String getExpectFirstFileTime() {
        return expectFirstFileTime;
    }

    public void setExpectFirstFileTime(String expectFirstFileTime) {
        this.expectFirstFileTime = expectFirstFileTime;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getNextInsertDate() {
        return nextInsertDate;
    }

    public void setNextInsertDate(Date nextInsertDate) {
        this.nextInsertDate = nextInsertDate;
    }

    public String getEntryMessage() {
        return entryMessage;
    }

    public void setEntryMessage(String entryMessage) {
        this.entryMessage = entryMessage;
    }
}