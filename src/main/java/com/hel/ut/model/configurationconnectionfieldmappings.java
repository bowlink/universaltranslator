package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "configurationconnectionfieldmappings")
public class configurationconnectionfieldmappings {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "connectionId", nullable = false)
    private Integer connectionId = 0;

    @Column(name = "sourceConfigId", nullable = false)
    private Integer sourceConfigId;

    @Column(name = "targetConfigId", nullable = false)
    private Integer targetConfigId;

    @Column(name = "fieldNo", nullable = false)
    private Integer fieldNo;

    @NoHtml
    @Column(name = "fieldDesc", nullable = true)
    private String fieldDesc;

    @Column(name = "useField", nullable = false)
    private boolean useField = false;
    
    @Column(name="associatedFieldNo", nullable = false)
    private Integer associatedFieldNo = 0;
    
    @Column(name="populateErrorFieldNo", nullable = false)
    private Integer populateErrorFieldNo = 0;
    
    @Column(name = "defaultValue", nullable = true)
    private String defaultValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Integer connectionId) {
        this.connectionId = connectionId;
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

    public Integer getFieldNo() {
        return fieldNo;
    }

    public void setFieldNo(Integer fieldNo) {
        this.fieldNo = fieldNo;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
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

    public Integer getPopulateErrorFieldNo() {
        return populateErrorFieldNo;
    }

    public void setPopulateErrorFieldNo(Integer populateErrorFieldNo) {
        this.populateErrorFieldNo = populateErrorFieldNo;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}