/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONCONNECTIONS")
public class configurationConnection {

    @Transient
    private utConfiguration srcConfigDetails = null, tgtConfigDetails = null;

    @Transient
    private String targetOrgName = null, sourceConfigName = "", targetConfigName = "", sourceOrgName = "", sourceTransportMethod = "", targetTransportMethod = "";

    @Transient
    private int targetOrgId = 0, messageTypeId = 0, sourceConfigType = 0, targetConfigType = 0;

    @Transient
    private List<utUser> connectionSenders = null, connectionReceivers = null;

    @Transient
    private Integer targetOrgCol = 0, sourceSubOrgCol = 0, transportMethodId = 0;
    
    @Transient
    private boolean allowExport = false;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "SOURCECONFIGID", nullable = false)
    private int sourceConfigId;

    @Column(name = "TARGETCONFIGID", nullable = false)
    private int targetConfigId;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "STATUS", nullable = false)
    private boolean status = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getsourceConfigId() {
        return sourceConfigId;
    }

    public void setsourceConfigId(int sourceConfigId) {
        this.sourceConfigId = sourceConfigId;
    }

    public int gettargetConfigId() {
        return targetConfigId;
    }

    public void settargetConfigId(int targetConfigId) {
        this.targetConfigId = targetConfigId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public utConfiguration getsrcConfigDetails() {
        return srcConfigDetails;
    }

    public void setsrcConfigDetails(utConfiguration srcConfigDetails) {
        this.srcConfigDetails = srcConfigDetails;
    }

    public utConfiguration gettgtConfigDetails() {
        return tgtConfigDetails;
    }

    public void settgtConfigDetails(utConfiguration tgtConfigDetails) {
        this.tgtConfigDetails = tgtConfigDetails;
    }

    public String gettargetOrgName() {
        return targetOrgName;
    }

    public void settargetOrgName(String targetOrgName) {
        this.targetOrgName = targetOrgName;
    }

    public int gettargetOrgId() {
        return targetOrgId;
    }

    public void settargetOrgId(int targetOrgId) {
        this.targetOrgId = targetOrgId;
    }

    public List<utUser> getconnectionSenders() {
        return connectionSenders;
    }

    public void setconnectionSenders(List<utUser> senders) {
        this.connectionSenders = senders;
    }

    public List<utUser> getconnectionReceivers() {
        return connectionReceivers;
    }

    public void setconnectionReceivers(List<utUser> receivers) {
        this.connectionReceivers = receivers;
    }

    public Integer getTargetOrgCol() {
        return targetOrgCol;
    }

    public void setTargetOrgCol(Integer targetOrgCol) {
        this.targetOrgCol = targetOrgCol;
    }

    public int getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(int messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public Integer getSourceSubOrgCol() {
        return sourceSubOrgCol;
    }

    public void setSourceSubOrgCol(Integer sourceSubOrgCol) {
        this.sourceSubOrgCol = sourceSubOrgCol;
    }

    public Integer getTransportMethodId() {
	return transportMethodId;
    }

    public void setTransportMethodId(Integer transportMethodId) {
	this.transportMethodId = transportMethodId;
    }

    public boolean isAllowExport() {
	return allowExport;
    }

    public void setAllowExport(boolean allowExport) {
	this.allowExport = allowExport;
    }

    public String getSourceConfigName() {
        return sourceConfigName;
    }

    public void setSourceConfigName(String sourceConfigName) {
        this.sourceConfigName = sourceConfigName;
    }

    public String getTargetConfigName() {
        return targetConfigName;
    }

    public void setTargetConfigName(String targetConfigName) {
        this.targetConfigName = targetConfigName;
    }

    public String getTargetOrgName() {
        return targetOrgName;
    }

    public void setTargetOrgName(String targetOrgName) {
        this.targetOrgName = targetOrgName;
    }

    public String getSourceOrgName() {
        return sourceOrgName;
    }

    public void setSourceOrgName(String sourceOrgName) {
        this.sourceOrgName = sourceOrgName;
    }

    public int getSourceConfigType() {
        return sourceConfigType;
    }

    public void setSourceConfigType(int sourceConfigType) {
        this.sourceConfigType = sourceConfigType;
    }

    public int getTargetConfigType() {
        return targetConfigType;
    }

    public void setTargetConfigType(int targetConfigType) {
        this.targetConfigType = targetConfigType;
    }

    public String getSourceTransportMethod() {
        return sourceTransportMethod;
    }

    public void setSourceTransportMethod(String sourceTransportMethod) {
        this.sourceTransportMethod = sourceTransportMethod;
    }

    public String getTargetTransportMethod() {
        return targetTransportMethod;
    }

    public void setTargetTransportMethod(String targetTransportMethod) {
        this.targetTransportMethod = targetTransportMethod;
    }
    
    
}
