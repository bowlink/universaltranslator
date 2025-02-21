/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
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
@Table(name = "CONFIGURATIONHL7ELEMENTVALUES")
public class HL7ElementComponents {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "ELEMENTID", nullable = false)
    private Integer elementId;

    @NoHtml
    @Column(name = "fieldDescriptor", nullable = true)
    private String fieldDescriptor = "";

    @NoHtml
    @Column(name = "fieldAppendText", nullable = true)
    private String fieldAppendText = "";

    @NoHtml
    @Column(name = "fieldValue", nullable = true)
    private String fieldValue = "";

    @Column(name = "displayPos", nullable = true)
    private Integer displayPos = 1;

    @NoHtml
    @Column(name = "defaultValue", nullable = true)
    private String defaultValue = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getElementId() {
        return elementId;
    }

    public void setElementId(Integer elementId) {
        this.elementId = elementId;
    }

    public String getFieldDescriptor() {
        return fieldDescriptor;
    }

    public void setFieldDescriptor(String fieldDescriptor) {
        this.fieldDescriptor = fieldDescriptor;
    }

    public String getFieldAppendText() {
        return fieldAppendText;
    }

    public void setFieldAppendText(String fieldAppendText) {
        this.fieldAppendText = fieldAppendText;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public Integer getDisplayPos() {
        return displayPos;
    }

    public void setDisplayPos(Integer displayPos) {
        this.displayPos = displayPos;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}