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
@Table(name = "directmessagesout")
public class directmessagesout {
    
    @Transient
    private String orgName = null, statusName = null, batchName = null;
    
    @Transient
    private Integer totalMessages = 0;
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "hispId", nullable = false)
    private Integer hispId = 0;

    @Column(name = "fromDirectAddress", nullable = false)
    private String fromDirectAddress;
    
    @Column(name = "toDirectAddress", nullable = false)
    private String toDirectAddress;
    
    @Column(name = "outputFileName", nullable = false)
    private String outputFileName;
    
    @Column(name = "statusId", nullable = false)
    private Integer statusId = 1; //set to reject

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    @Column(name = "batchUploadId", nullable = true)
    private Integer batchUploadId = 0;
    
    @Column(name = "batchDownloadId", nullable = true)
    private Integer batchDownloadId = 0;

    @Column(name = "configId", nullable = true)
    private Integer configId = 0;
    
    @Column(name = "orgId", nullable = true)
    private Integer orgId = 0;
    
    @Column(name = "responseStatus", nullable = true)
    private Integer responseStatus;
    
    @Column(name = "responseMessage", nullable = true)
    private String responseMessage;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public Integer getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(Integer totalMessages) {
        this.totalMessages = totalMessages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getHispId() {
        return hispId;
    }

    public void setHispId(Integer hispId) {
        this.hispId = hispId;
    }

    public String getFromDirectAddress() {
        return fromDirectAddress;
    }

    public void setFromDirectAddress(String fromDirectAddress) {
        this.fromDirectAddress = fromDirectAddress;
    }

    public String getToDirectAddress() {
        return toDirectAddress;
    }

    public void setToDirectAddress(String toDirectAddress) {
        this.toDirectAddress = toDirectAddress;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getBatchUploadId() {
        return batchUploadId;
    }

    public void setBatchUploadId(Integer batchUploadId) {
        this.batchUploadId = batchUploadId;
    }

    public Integer getBatchDownloadId() {
        return batchDownloadId;
    }

    public void setBatchDownloadId(Integer batchDownloadId) {
        this.batchDownloadId = batchDownloadId;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}