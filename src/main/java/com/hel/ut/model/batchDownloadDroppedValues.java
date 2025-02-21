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
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "batchdownloaddroppedvalues")
public class batchDownloadDroppedValues {
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;
    
    @Column(name = "fieldNo", nullable = false)
    private Integer fieldNo = 0;
    
    @Column(name = "batchUploadId", nullable = false)
    private Integer batchUploadId = 0;
    
    @Column(name = "batchDownloadId", nullable = false)
    private Integer batchDownloadId = 0;
    
    @Column(name = "configId", nullable = false)
    private Integer configId = 0;
    
    @Column(name = "transactionInRecordsId", nullable = false)
    private Integer transactionInRecordsId = 0;
    
    @Column(name = "transactionOutRecordsId", nullable = false)
    private Integer transactionOutRecordsId = 0;
    
    @Column(name = "fieldName", nullable = false)
    private String fieldName;
    
    @Column(name = "fieldValue", nullable = false)
    private String fieldValue;
    
    @Column(name = "reportField1Data", nullable = false)
    private String reportField1Data;
    
    @Column(name = "reportField2Data", nullable = false)
    private String reportField2Data;
    
    @Column(name = "reportField3Data", nullable = false)
    private String reportField3Data;
    
    @Column(name = "reportField4Data", nullable = false)
    private String reportField4Data;
    
    @Column(name = "translatedReportField1Data", nullable = false)
    private String translatedReportField1Data;
    
    @Column(name = "crosswalkId", nullable = true)
    private Integer crosswalkId = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getFieldNo() {
        return fieldNo;
    }

    public void setFieldNo(Integer fieldNo) {
        this.fieldNo = fieldNo;
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

    public Integer getTransactionInRecordsId() {
        return transactionInRecordsId;
    }

    public void setTransactionInRecordsId(Integer transactionInRecordsId) {
        this.transactionInRecordsId = transactionInRecordsId;
    }

    public Integer getTransactionOutRecordsId() {
        return transactionOutRecordsId;
    }

    public void setTransactionOutRecordsId(Integer transactionOutRecordsId) {
        this.transactionOutRecordsId = transactionOutRecordsId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getReportField1Data() {
        return reportField1Data;
    }

    public void setReportField1Data(String reportField1Data) {
        this.reportField1Data = reportField1Data;
    }

    public String getReportField2Data() {
        return reportField2Data;
    }

    public void setReportField2Data(String reportField2Data) {
        this.reportField2Data = reportField2Data;
    }

    public String getReportField3Data() {
        return reportField3Data;
    }

    public void setReportField3Data(String reportField3Data) {
        this.reportField3Data = reportField3Data;
    }

    public String getReportField4Data() {
        return reportField4Data;
    }

    public void setReportField4Data(String reportField4Data) {
        this.reportField4Data = reportField4Data;
    }

    public String getTranslatedReportField1Data() {
        return translatedReportField1Data;
    }

    public void setTranslatedReportField1Data(String translatedReportField1Data) {
        this.translatedReportField1Data = translatedReportField1Data;
    }

    public Integer getCrosswalkId() {
        return crosswalkId;
    }

    public void setCrosswalkId(Integer crosswalkId) {
        this.crosswalkId = crosswalkId;
    }
}