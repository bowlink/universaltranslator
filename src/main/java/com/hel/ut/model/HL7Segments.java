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
@Table(name = "CONFIGURATIONHL7SEGMENTS")
public class HL7Segments {

    @Transient
    private List<HL7Elements> HL7Elements = null;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "HL7ID", nullable = false)
    private Integer hl7Id;

    @NoHtml
    @Column(name = "segmentName", nullable = false)
    private String segmentName = "";

    @Column(name = "displayPos", nullable = false)
    private Integer displayPos = 1;

    public List<HL7Elements> getHL7Elements() {
        return HL7Elements;
    }

    public void setHL7Elements(List<HL7Elements> HL7Elements) {
        this.HL7Elements = HL7Elements;
    }

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

    public String getSegmentName() {
        return segmentName;
    }

    public void setSegmentName(String segmentName) {
        this.segmentName = segmentName;
    }

    public Integer getDisplayPos() {
        return displayPos;
    }

    public void setDisplayPos(Integer displayPos) {
        this.displayPos = displayPos;
    }
}