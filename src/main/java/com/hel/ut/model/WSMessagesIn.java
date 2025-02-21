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
@Table(name = "wsMessagesIn")
public class WSMessagesIn {

    @Transient
    private String orgName = null, statusName = null, errorDisplayText = null, batchName = null;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "orgId", nullable = true)
    private Integer orgId = 0;

    @Column(name = "fromAddress", nullable = true)
    private String fromAddress;

    @Column(name = "payload", nullable = true)
    private String payload;

    /**
     * 1 - not processed 2 - processed 3 - rejected
     *
     */
    @Column(name = "statusId", nullable = false)
    private Integer statusId = 3; //set to reject

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    @Column(name = "errorId", nullable = true)
    private Integer errorId = 0;

    @Column(name = "domain", nullable = true)
    private String domain;

    @Column(name = "batchUploadId", nullable = true)
    private Integer batchUploadId = 0;

    @Column(name = "foundPosition", nullable = true)
    private Integer foundPosition = 0;

    @Column(name = "positionMatched", nullable = true)
    private boolean positionMatched = false;

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

    public String getErrorDisplayText() {
        return errorDisplayText;
    }

    public void setErrorDisplayText(String errorDisplayText) {
        this.errorDisplayText = errorDisplayText;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
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

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
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

    public Integer getErrorId() {
        return errorId;
    }

    public void setErrorId(Integer errorId) {
        this.errorId = errorId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getBatchUploadId() {
        return batchUploadId;
    }

    public void setBatchUploadId(Integer batchUploadId) {
        this.batchUploadId = batchUploadId;
    }

    public Integer getFoundPosition() {
        return foundPosition;
    }

    public void setFoundPosition(Integer foundPosition) {
        this.foundPosition = foundPosition;
    }

    public boolean isPositionMatched() {
        return positionMatched;
    }

    public void setPositionMatched(boolean positionMatched) {
        this.positionMatched = positionMatched;
    }
}