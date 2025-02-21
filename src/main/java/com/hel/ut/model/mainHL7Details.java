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
@Table(name = "HL7SPECS")
public class mainHL7Details {

    @Transient
    private List<mainHL7Segments> HL7Segments = null;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @NoHtml
    @Column(name = "NAME", nullable = false)
    private String name = "";

    @NoHtml
    @Column(name = "FIELDSEPARATOR", nullable = false)
    private String fieldSeparator = "|";

    @NoHtml
    @Column(name = "COMPONENTSEPARATOR", nullable = false)
    private String componentSeparator = "^";

    @NoHtml
    @Column(name = "ESCAPECHAR", nullable = false)
    private String EscapeChar = "";

    public List<mainHL7Segments> getHL7Segments() {
        return HL7Segments;
    }

    public void setHL7Segments(List<mainHL7Segments> HL7Segments) {
        this.HL7Segments = HL7Segments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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