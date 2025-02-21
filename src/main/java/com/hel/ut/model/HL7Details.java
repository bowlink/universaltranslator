/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONHL7DETAILS")
public class HL7Details {

    @Transient
    private List<HL7Segments> HL7Segments = null;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGID", nullable = false)
    private Integer configId;

    @NoHtml
    @Column(name = "fieldSeparator", nullable = false)
    private String fieldSeparator = "|";

    @NoHtml
    @Column(name = "componentSeparator", nullable = false)
    private String componentSeparator = "^";

    @NoHtml
    @Column(name = "EscapeChar", nullable = false)
    private String EscapeChar = "";

    public List<HL7Segments> getHL7Segments() {
        return HL7Segments;
    }

    public void setHL7Segments(List<HL7Segments> HL7Segments) {
        this.HL7Segments = HL7Segments;
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

    public String getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public String getComponentSeparator() {
        return componentSeparator;
    }

    public void setComponentSeparator(String componentSeparator) {
        this.componentSeparator = componentSeparator;
    }

    public String getEscapeChar() {
        return EscapeChar;
    }

    public void setEscapeChar(String EscapeChar) {
        this.EscapeChar = EscapeChar;
    }
}