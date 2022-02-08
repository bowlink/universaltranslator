/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author gchan
 */
@Entity
@Table(name = "rel_crosswalkdata")
public class CrosswalkData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "crosswalkId", nullable = false)
    private int crosswalkId;

    @Column(name = "sourceValue", nullable = false)
    private String sourceValue;

    @Column(name = "targetValue", nullable = true)
    private String targetValue = null;

    @Column(name = "descValue", nullable = true)
    private String descValue = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCrosswalkId() {
        return crosswalkId;
    }

    public void setCrosswalkId(int crosswalkId) {
        this.crosswalkId = crosswalkId;
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public String getDescValue() {
        return descValue;
    }

    public void setDescValue(String descValue) {
        this.descValue = descValue;
    }

}
