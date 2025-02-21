package com.hel.ut.model;

import java.util.Date;
import java.util.Map;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "CONFIGURATIONDATATRANSLATIONS")
public class configurationDataTranslations {

    @Transient
    String fieldName = null, crosswalkName = null, macroName = null, fieldDesc = null;

    @Transient
    Integer fieldNo;
    
    @Transient
    boolean requiredField;

    @Transient
    Map<String, String> defaultValues;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGID", nullable = false)
    private Integer configId;

    @Column(name = "FIELDID", nullable = false)
    private Integer fieldId;

    @Column(name = "CROSSWALKID", nullable = false)
    private Integer crosswalkId;

    @Column(name = "MACROID", nullable = true)
    private Integer macroId;

    @Column(name = "PASSCLEAR", nullable = true)
    private Integer passClear = 1;

    @Column(name = "FIELDA", nullable = true)
    private String fieldA = null;

    @Column(name = "FIELDB", nullable = true)
    private String fieldB = null;

    @Column(name = "CONSTANT1", nullable = true)
    private String constant1 = null;

    @Column(name = "CONSTANT2", nullable = true)
    private String constant2 = null;

    @Column(name = "PROCESSORDER", nullable = false)
    private Integer processOrder;

    @Column(name = "CategoryId", nullable = false)
    private Integer categoryId = 1; // while processing

    @Column(name = "DEFAULTVALUE", nullable = true)
    private String defaultValue;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "dateAdded", nullable = true)
    private Date dateAdded = new Date();
    
    @Column(name = "updatedByImport", nullable = true)
    private boolean updatedByImport = false;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getCrosswalkName() {
        return crosswalkName;
    }

    public void setCrosswalkName(String crosswalkName) {
        this.crosswalkName = crosswalkName;
    }

    public String getMacroName() {
        return macroName;
    }

    public void setMacroName(String macroName) {
        this.macroName = macroName;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public Integer getFieldNo() {
        return fieldNo;
    }

    public void setFieldNo(Integer fieldNo) {
        this.fieldNo = fieldNo;
    }

    public boolean isRequiredField() {
        return requiredField;
    }

    public void setRequiredField(boolean requiredField) {
        this.requiredField = requiredField;
    }

    public Map<String, String> getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(Map<String, String> defaultValues) {
        this.defaultValues = defaultValues;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

    public Integer getCrosswalkId() {
        return crosswalkId;
    }

    public void setCrosswalkId(Integer crosswalkId) {
        this.crosswalkId = crosswalkId;
    }

    public Integer getMacroId() {
        return macroId;
    }

    public void setMacroId(Integer macroId) {
        this.macroId = macroId;
    }

    public Integer getPassClear() {
        return passClear;
    }

    public void setPassClear(Integer passClear) {
        this.passClear = passClear;
    }

    public String getFieldA() {
        return fieldA;
    }

    public void setFieldA(String fieldA) {
        this.fieldA = fieldA;
    }

    public String getFieldB() {
        return fieldB;
    }

    public void setFieldB(String fieldB) {
        this.fieldB = fieldB;
    }

    public String getConstant1() {
        return constant1;
    }

    public void setConstant1(String constant1) {
        this.constant1 = constant1;
    }

    public String getConstant2() {
        return constant2;
    }

    public void setConstant2(String constant2) {
        this.constant2 = constant2;
    }

    public Integer getProcessOrder() {
        return processOrder;
    }

    public void setProcessOrder(Integer processOrder) {
        this.processOrder = processOrder;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public boolean isUpdatedByImport() {
        return updatedByImport;
    }

    public void setUpdatedByImport(boolean updatedByImport) {
        this.updatedByImport = updatedByImport;
    }
}