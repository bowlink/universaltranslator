/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.List;

/**
 *
 * @author chadmccue
 */
public class transactionRecords {
    
    List<fieldSelectOptions> fieldSelectOptions = null;

    private String fieldValue = null;
    private String fieldHelp = null;
    private String saveToTable = null;
    private String saveToTableCol = null;
    private Integer fieldNo;
    private boolean required = true;
    private String validation = null;
    private String fieldLabel = null;
    private Integer transactionId;
    private boolean readOnly = false;
    private Integer fieldType = 1;
    private String errorDesc = null;
    private String errorData = null;
    private boolean useField = true;

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldHelp() {
        return fieldHelp;
    }

    public void setFieldHelp(String fieldHelp) {
        this.fieldHelp = fieldHelp;
    }

    public String getSaveToTable() {
        return saveToTable;
    }

    public void setSaveToTable(String saveToTable) {
        this.saveToTable = saveToTable;
    }

    public String getSaveToTableCol() {
        return saveToTableCol;
    }

    public void setSaveToTableCol(String saveToTableCol) {
        this.saveToTableCol = saveToTableCol;
    }

    public Integer getFieldNo() {
        return fieldNo;
    }

    public void setFieldNo(Integer fieldNo) {
        this.fieldNo = fieldNo;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Integer getFieldType() {
        return fieldType;
    }

    public void setFieldType(Integer fieldType) {
        this.fieldType = fieldType;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getErrorData() {
        return errorData;
    }

    public void setErrorData(String errorData) {
        this.errorData = errorData;
    }

    public boolean isUseField() {
        return useField;
    }

    public void setUseField(boolean useField) {
        this.useField = useField;
    }

    public List<fieldSelectOptions> getFieldSelectOptions() {
        return fieldSelectOptions;
    }

    public void setFieldSelectOptions(List<fieldSelectOptions> fieldSelectOptions) {
        this.fieldSelectOptions = fieldSelectOptions;
    }
}