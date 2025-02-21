package com.hel.ut.model;


public class appenedNewconfigurationFormFields {

    private Integer fieldNo;
    private Integer configId;
    private Integer transportDetailId;
    private String fieldDesc, sampleData;
    private Integer validationType = 1;
    private boolean required = false;
    private boolean useField = false;

    public Integer getFieldNo() {
        return fieldNo;
    }

    public void setFieldNo(Integer fieldNo) {
        this.fieldNo = fieldNo;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public Integer getTransportDetailId() {
        return transportDetailId;
    }

    public void setTransportDetailId(Integer transportDetailId) {
        this.transportDetailId = transportDetailId;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getSampleData() {
        return sampleData;
    }

    public void setSampleData(String sampleData) {
        this.sampleData = sampleData;
    }

    public Integer getValidationType() {
        return validationType;
    }

    public void setValidationType(Integer validationType) {
        this.validationType = validationType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isUseField() {
        return useField;
    }

    public void setUseField(boolean useField) {
        this.useField = useField;
    }
}