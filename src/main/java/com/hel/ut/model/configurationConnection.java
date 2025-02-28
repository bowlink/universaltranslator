/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.Date;
import java.util.List;
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
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONCONNECTIONS")
public class configurationConnection {

    @Transient
    private utConfiguration srcConfigDetails = null, tgtConfigDetails = null;

    @Transient
    private String targetOrgName = null, sourceConfigName = "", targetConfigName = "", sourceOrgName = "", sourceTransportMethod = "", targetTransportMethod = "", srcSystem = "", tgtSystem = "";

    @Transient
    private Integer targetOrgId = 0, messageTypeId = 0, sourceConfigType = 0, targetConfigType = 0, targetOrgCol = 0, sourceSubOrgCol = 0, transportMethodId = 0;

    @Transient
    private List<utUser> connectionSenders = null, connectionReceivers = null;

    @Transient
    private boolean allowExport = false;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "SOURCECONFIGID", nullable = false)
    private Integer sourceConfigId;

    @Column(name = "TARGETCONFIGID", nullable = false)
    private Integer targetConfigId;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "STATUS", nullable = false)
    private boolean status = true;

    public utConfiguration getSrcConfigDetails() {
        return srcConfigDetails;
    }

    public void setSrcConfigDetails(utConfiguration srcConfigDetails) {
        this.srcConfigDetails = srcConfigDetails;
    }

    public utConfiguration getTgtConfigDetails() {
        return tgtConfigDetails;
    }

    public void setTgtConfigDetails(utConfiguration tgtConfigDetails) {
        this.tgtConfigDetails = tgtConfigDetails;
    }

    public String getTargetOrgName() {
        return targetOrgName;
    }

    public void setTargetOrgName(String targetOrgName) {
        this.targetOrgName = targetOrgName;
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

    public String getSourceOrgName() {
        return sourceOrgName;
    }

    public void setSourceOrgName(String sourceOrgName) {
        this.sourceOrgName = sourceOrgName;
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

    public Integer getTargetOrgId() {
        return targetOrgId;
    }

    public void setTargetOrgId(Integer targetOrgId) {
        this.targetOrgId = targetOrgId;
    }

    public Integer getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public Integer getSourceConfigType() {
        return sourceConfigType;
    }

    public void setSourceConfigType(Integer sourceConfigType) {
        this.sourceConfigType = sourceConfigType;
    }

    public Integer getTargetConfigType() {
        return targetConfigType;
    }

    public void setTargetConfigType(Integer targetConfigType) {
        this.targetConfigType = targetConfigType;
    }

    public Integer getTargetOrgCol() {
        return targetOrgCol;
    }

    public void setTargetOrgCol(Integer targetOrgCol) {
        this.targetOrgCol = targetOrgCol;
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

    public List<utUser> getConnectionSenders() {
        return connectionSenders;
    }

    public void setConnectionSenders(List<utUser> connectionSenders) {
        this.connectionSenders = connectionSenders;
    }

    public List<utUser> getConnectionReceivers() {
        return connectionReceivers;
    }

    public void setConnectionReceivers(List<utUser> connectionReceivers) {
        this.connectionReceivers = connectionReceivers;
    }

    public boolean isAllowExport() {
        return allowExport;
    }

    public void setAllowExport(boolean allowExport) {
        this.allowExport = allowExport;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getSourceConfigId() {
        return sourceConfigId;
    }

    public void setSourceConfigId(Integer sourceConfigId) {
        this.sourceConfigId = sourceConfigId;
    }

    public Integer getTargetConfigId() {
        return targetConfigId;
    }

    public void setTargetConfigId(Integer targetConfigId) {
        this.targetConfigId = targetConfigId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getSrcSystem() {
        return srcSystem;
    }

    public void setSrcSystem(String srcSystem) {
        this.srcSystem = srcSystem;
    }

    public String getTgtSystem() {
        return tgtSystem;
    }

    public void setTgtSystem(String tgtSystem) {
        this.tgtSystem = tgtSystem;
    }
}