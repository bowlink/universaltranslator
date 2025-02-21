package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "CONFIGURATIONFORMFIELDS")
public class configurationFormFields {

    @Transient
    private String fieldValue = null, associatedFieldDetails;
    
    @Transient
    private Integer copiedId = 0, mappedToField = 0, mappedErrorField = 0;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "associatedFieldId", nullable = false)
    private int associatedFieldId = 0;

    @Column(name = "CONFIGID", nullable = false)
    private int configId;

    @Column(name = "TRANSPORTDETAILID", nullable = false)
    private int transportDetailId;

    @Column(name = "FIELDNO", nullable = false)
    private int fieldNo;

    @NoHtml
    @Column(name = "FIELDDESC", nullable = true)
    private String fieldDesc;

    @Column(name = "VALIDATIONTYPE", nullable = true)
    private int validationType = 1;

    @Column(name = "REQUIRED", nullable = false)
    private boolean required = false;

    @Column(name = "USEFIELD", nullable = false)
    private boolean useField = false;
    
    @Column(name="associatedFieldNo", nullable = false)
    private Integer associatedFieldNo = 0;

    @Column(name = "defaultValue", nullable = true)
    private String defaultValue;
    
    @Column(name = "sampleData", nullable = true)
    private String sampleData;

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getAssociatedFieldDetails() {
        return associatedFieldDetails;
    }

    public void setAssociatedFieldDetails(String associatedFieldDetails) {
        this.associatedFieldDetails = associatedFieldDetails;
    }

    public Integer getCopiedId() {
        return copiedId;
    }

    public void setCopiedId(Integer copiedId) {
        this.copiedId = copiedId;
    }

    public Integer getMappedToField() {
        return mappedToField;
    }

    public void setMappedToField(Integer mappedToField) {
        this.mappedToField = mappedToField;
    }

    public Integer getMappedErrorField() {
        return mappedErrorField;
    }

    public void setMappedErrorField(Integer mappedErrorField) {
        this.mappedErrorField = mappedErrorField;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssociatedFieldId() {
        return associatedFieldId;
    }

    public void setAssociatedFieldId(int associatedFieldId) {
        this.associatedFieldId = associatedFieldId;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public int getTransportDetailId() {
        return transportDetailId;
    }

    public void setTransportDetailId(int transportDetailId) {
        this.transportDetailId = transportDetailId;
    }

    public int getFieldNo() {
        return fieldNo;
    }

    public void setFieldNo(int fieldNo) {
        this.fieldNo = fieldNo;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public int getValidationType() {
        return validationType;
    }

    public void setValidationType(int validationType) {
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

    public Integer getAssociatedFieldNo() {
        return associatedFieldNo;
    }

    public void setAssociatedFieldNo(Integer associatedFieldNo) {
        this.associatedFieldNo = associatedFieldNo;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getSampleData() {
        return sampleData;
    }

    public void setSampleData(String sampleData) {
        this.sampleData = sampleData;
    }
}