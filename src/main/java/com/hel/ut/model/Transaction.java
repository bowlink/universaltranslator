/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author chadmccue
 */
public class Transaction {

    private Integer orgId;
    private Integer sourceSubOrgId;
    private Integer userId;
    private Integer configId;
    private String batchName = null;
    private String frombatchName = null;
    private String fileName = null;
    private Integer transportMethodId;
    private String originalFileName = null;
    private Integer statusId;
    private String statusValue;
    private Integer messageTypeId;
    private Integer transactionStatusId;
    private Integer targetOrgId;
    private Integer targetSubOrgId = 0;
    private List<Integer> targetConfigId;
    private boolean autoRelease = true;
    private Date dateSubmitted = null;
    private String messageTypeName = null;
    private Integer batchId = 0;
    private Integer transactionId = 0;
    private Integer transactionRecordId = 0;
    private Integer transactionTargetId = 0;
    private Integer sourceType = 1;
    private Integer internalStatusId = 0;
    private Integer orginialTransactionId = 0;
    private String reportableField1 = null;
    private String reportableField2 = null;
    private String reportableField3 = null;
    private String reportableField4 = null;
    private String reportableFieldHeading1 = null;
    private String reportableFieldHeading2 = null;
    private String reportableFieldHeading3 = null;
    private String reportableFieldHeading4 = null;
    private List<transactionRecords> sourceOrgFields = null;
    private List<transactionRecords> sourceProviderFields = null;
    private List<transactionRecords> targetOrgFields = null;
    private List<transactionRecords> targetProviderFields = null;
    private List<transactionRecords> patientFields = null;
    private List<transactionRecords> detailFields = null;
    private Integer attachmentLimit;
    private Boolean attachmentRequired = false;
    private String attachmentNote = "";
    private Integer messageStatus = 1;
    private String activityStatus = "";
    private String activityStatusName = "";
    private String srcConfigName;
    private String srcOrgName;
    private String targetConfigName;
    private String targetOrgName;
    private Integer targetConfigId1;
    private String srcSiteName;
    private Integer parentOrgId = 0;
    private String patientInfo = "";
    private String sendingOrgInfo = "";
    private String targetOrgInfo = "";
    private String openClosed = "";
    private String referralId = "";
    private Integer totalRecordCount = 0;
    private Integer totalErrorCount = 0;

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getSourceSubOrgId() {
        return sourceSubOrgId;
    }

    public void setSourceSubOrgId(Integer sourceSubOrgId) {
        this.sourceSubOrgId = sourceSubOrgId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getFrombatchName() {
        return frombatchName;
    }

    public void setFrombatchName(String frombatchName) {
        this.frombatchName = frombatchName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getTransportMethodId() {
        return transportMethodId;
    }

    public void setTransportMethodId(Integer transportMethodId) {
        this.transportMethodId = transportMethodId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(String statusValue) {
        this.statusValue = statusValue;
    }

    public Integer getMessageTypeId() {
        return messageTypeId;
    }

    public void setMessageTypeId(Integer messageTypeId) {
        this.messageTypeId = messageTypeId;
    }

    public Integer getTransactionStatusId() {
        return transactionStatusId;
    }

    public void setTransactionStatusId(Integer transactionStatusId) {
        this.transactionStatusId = transactionStatusId;
    }

    public Integer getTargetOrgId() {
        return targetOrgId;
    }

    public void setTargetOrgId(Integer targetOrgId) {
        this.targetOrgId = targetOrgId;
    }

    public Integer getTargetSubOrgId() {
        return targetSubOrgId;
    }

    public void setTargetSubOrgId(Integer targetSubOrgId) {
        this.targetSubOrgId = targetSubOrgId;
    }

    public List<Integer> getTargetConfigId() {
        return targetConfigId;
    }

    public void setTargetConfigId(List<Integer> targetConfigId) {
        this.targetConfigId = targetConfigId;
    }

    public boolean isAutoRelease() {
        return autoRelease;
    }

    public void setAutoRelease(boolean autoRelease) {
        this.autoRelease = autoRelease;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getMessageTypeName() {
        return messageTypeName;
    }

    public void setMessageTypeName(String messageTypeName) {
        this.messageTypeName = messageTypeName;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getTransactionRecordId() {
        return transactionRecordId;
    }

    public void setTransactionRecordId(Integer transactionRecordId) {
        this.transactionRecordId = transactionRecordId;
    }

    public Integer getTransactionTargetId() {
        return transactionTargetId;
    }

    public void setTransactionTargetId(Integer transactionTargetId) {
        this.transactionTargetId = transactionTargetId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getInternalStatusId() {
        return internalStatusId;
    }

    public void setInternalStatusId(Integer internalStatusId) {
        this.internalStatusId = internalStatusId;
    }

    public Integer getOrginialTransactionId() {
        return orginialTransactionId;
    }

    public void setOrginialTransactionId(Integer orginialTransactionId) {
        this.orginialTransactionId = orginialTransactionId;
    }

    public String getReportableField1() {
        return reportableField1;
    }

    public void setReportableField1(String reportableField1) {
        this.reportableField1 = reportableField1;
    }

    public String getReportableField2() {
        return reportableField2;
    }

    public void setReportableField2(String reportableField2) {
        this.reportableField2 = reportableField2;
    }

    public String getReportableField3() {
        return reportableField3;
    }

    public void setReportableField3(String reportableField3) {
        this.reportableField3 = reportableField3;
    }

    public String getReportableField4() {
        return reportableField4;
    }

    public void setReportableField4(String reportableField4) {
        this.reportableField4 = reportableField4;
    }

    public String getReportableFieldHeading1() {
        return reportableFieldHeading1;
    }

    public void setReportableFieldHeading1(String reportableFieldHeading1) {
        this.reportableFieldHeading1 = reportableFieldHeading1;
    }

    public String getReportableFieldHeading2() {
        return reportableFieldHeading2;
    }

    public void setReportableFieldHeading2(String reportableFieldHeading2) {
        this.reportableFieldHeading2 = reportableFieldHeading2;
    }

    public String getReportableFieldHeading3() {
        return reportableFieldHeading3;
    }

    public void setReportableFieldHeading3(String reportableFieldHeading3) {
        this.reportableFieldHeading3 = reportableFieldHeading3;
    }

    public String getReportableFieldHeading4() {
        return reportableFieldHeading4;
    }

    public void setReportableFieldHeading4(String reportableFieldHeading4) {
        this.reportableFieldHeading4 = reportableFieldHeading4;
    }

    public List<transactionRecords> getSourceOrgFields() {
        return sourceOrgFields;
    }

    public void setSourceOrgFields(List<transactionRecords> sourceOrgFields) {
        this.sourceOrgFields = sourceOrgFields;
    }

    public List<transactionRecords> getSourceProviderFields() {
        return sourceProviderFields;
    }

    public void setSourceProviderFields(List<transactionRecords> sourceProviderFields) {
        this.sourceProviderFields = sourceProviderFields;
    }

    public List<transactionRecords> getTargetOrgFields() {
        return targetOrgFields;
    }

    public void setTargetOrgFields(List<transactionRecords> targetOrgFields) {
        this.targetOrgFields = targetOrgFields;
    }

    public List<transactionRecords> getTargetProviderFields() {
        return targetProviderFields;
    }

    public void setTargetProviderFields(List<transactionRecords> targetProviderFields) {
        this.targetProviderFields = targetProviderFields;
    }

    public List<transactionRecords> getPatientFields() {
        return patientFields;
    }

    public void setPatientFields(List<transactionRecords> patientFields) {
        this.patientFields = patientFields;
    }

    public List<transactionRecords> getDetailFields() {
        return detailFields;
    }

    public void setDetailFields(List<transactionRecords> detailFields) {
        this.detailFields = detailFields;
    }

    public Integer getAttachmentLimit() {
        return attachmentLimit;
    }

    public void setAttachmentLimit(Integer attachmentLimit) {
        this.attachmentLimit = attachmentLimit;
    }

    public Boolean getAttachmentRequired() {
        return attachmentRequired;
    }

    public void setAttachmentRequired(Boolean attachmentRequired) {
        this.attachmentRequired = attachmentRequired;
    }

    public String getAttachmentNote() {
        return attachmentNote;
    }

    public void setAttachmentNote(String attachmentNote) {
        this.attachmentNote = attachmentNote;
    }

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public String getActivityStatusName() {
        return activityStatusName;
    }

    public void setActivityStatusName(String activityStatusName) {
        this.activityStatusName = activityStatusName;
    }

    public String getSrcConfigName() {
        return srcConfigName;
    }

    public void setSrcConfigName(String srcConfigName) {
        this.srcConfigName = srcConfigName;
    }

    public String getSrcOrgName() {
        return srcOrgName;
    }

    public void setSrcOrgName(String srcOrgName) {
        this.srcOrgName = srcOrgName;
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

    public Integer getTargetConfigId1() {
        return targetConfigId1;
    }

    public void setTargetConfigId1(Integer targetConfigId1) {
        this.targetConfigId1 = targetConfigId1;
    }

    public String getSrcSiteName() {
        return srcSiteName;
    }

    public void setSrcSiteName(String srcSiteName) {
        this.srcSiteName = srcSiteName;
    }

    public Integer getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(Integer parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public String getPatientInfo() {
        return patientInfo;
    }

    public void setPatientInfo(String patientInfo) {
        this.patientInfo = patientInfo;
    }

    public String getSendingOrgInfo() {
        return sendingOrgInfo;
    }

    public void setSendingOrgInfo(String sendingOrgInfo) {
        this.sendingOrgInfo = sendingOrgInfo;
    }

    public String getTargetOrgInfo() {
        return targetOrgInfo;
    }

    public void setTargetOrgInfo(String targetOrgInfo) {
        this.targetOrgInfo = targetOrgInfo;
    }

    public String getOpenClosed() {
        return openClosed;
    }

    public void setOpenClosed(String openClosed) {
        this.openClosed = openClosed;
    }

    public String getReferralId() {
        return referralId;
    }

    public void setReferralId(String referralId) {
        this.referralId = referralId;
    }

    public Integer getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(Integer totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public Integer getTotalErrorCount() {
        return totalErrorCount;
    }

    public void setTotalErrorCount(Integer totalErrorCount) {
        this.totalErrorCount = totalErrorCount;
    }
}