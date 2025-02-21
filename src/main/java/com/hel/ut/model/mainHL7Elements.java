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
@Table(name = "HL7ELEMENTS")
public class mainHL7Elements {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "HL7ID", nullable = false)
    private Integer hl7Id;

    @Column(name = "SEGMENTID", nullable = false)
    private Integer segmentId;

    @NoHtml
    @Column(name = "ELEMENTNAME", nullable = false)
    private String elementName = "";

    @NoHtml
    @Column(name = "DEFAULTVALUE", nullable = true)
    private String defaultValue = "";

    @Column(name = "DISPLAYPOS", nullable = true)
    private Integer displayPos = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getHl7Id() {
        return hl7Id;
    }

    public void setHl7Id(Integer hl7Id) {
        this.hl7Id = hl7Id;
    }

    public Integer getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Integer segmentId) {
        this.segmentId = segmentId;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getDisplayPos() {
        return displayPos;
    }

    public void setDisplayPos(Integer displayPos) {
        this.displayPos = displayPos;
    }
}