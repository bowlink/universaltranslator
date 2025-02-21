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
@Table(name = "batchuploaddroppedvalues")
public class batchUploadDroppedValues {
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;
    
    @Column(name = "transactionInRecordsId", nullable = false)
    private Integer transactionInRecordsId = 0;
    
    @Column(name = "fieldNo", nullable = false)
    private Integer fieldNo = 0;
    
    @Column(name = "batchUploadId", nullable = false)
    private Integer batchUploadId = 0;
    
    @Column(name = "configId", nullable = false)
    private Integer configId = 0;
    
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
    
    @Column(name = "fromOutboundConfig", nullable = false)
    private boolean fromOutboundConfig = false;
    
    @Column(name = "sourceFieldNo", nullable = true)
    private boolean sourceFieldNo = true;
    
    @Column(name = "targetFieldNo", nullable = true)
    private boolean targetFieldNo = true;
    
    @Column(name = "crosswalkId", nullable = true)
    private Integer crosswalkId = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTransactionInRecordsId() {
        return transactionInRecordsId;
    }

    public void setTransactionInRecordsId(Integer transactionInRecordsId) {
        this.transactionInRecordsId = transactionInRecordsId;
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

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
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

    public boolean isFromOutboundConfig() {
        return fromOutboundConfig;
    }

    public void setFromOutboundConfig(boolean fromOutboundConfig) {
        this.fromOutboundConfig = fromOutboundConfig;
    }

    public boolean isSourceFieldNo() {
        return sourceFieldNo;
    }

    public void setSourceFieldNo(boolean sourceFieldNo) {
        this.sourceFieldNo = sourceFieldNo;
    }

    public boolean isTargetFieldNo() {
        return targetFieldNo;
    }

    public void setTargetFieldNo(boolean targetFieldNo) {
        this.targetFieldNo = targetFieldNo;
    }

    public Integer getCrosswalkId() {
        return crosswalkId;
    }

    public void setCrosswalkId(Integer crosswalkId) {
        this.crosswalkId = crosswalkId;
    }
}