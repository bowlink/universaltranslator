package com.hel.ut.model;

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
@Table(name = "CONFIGURATIONSCHEDULE")
public class configurationSchedules {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGID", nullable = false)
    private Integer configId;

    @Column(name = "TYPE", nullable = false)
    private Integer type = 5;

    @Column(name = "PROCESSINGTYPE", nullable = false)
    private Integer processingType = 0;

    @Column(name = "NEWFILECHECK", nullable = false)
    private Integer newfileCheck = 0;

    @Column(name = "PROCESSINGDAY", nullable = false)
    private Integer processingDay = 0;

    @Column(name = "PROCESSINGTIME", nullable = false)
    private Integer processingTime = 0;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getProcessingType() {
        return processingType;
    }

    public void setProcessingType(Integer processingType) {
        this.processingType = processingType;
    }

    public Integer getNewfileCheck() {
        return newfileCheck;
    }

    public void setNewfileCheck(Integer newfileCheck) {
        this.newfileCheck = newfileCheck;
    }

    public Integer getProcessingDay() {
        return processingDay;
    }

    public void setProcessingDay(Integer processingDay) {
        this.processingDay = processingDay;
    }

    public Integer getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Integer processingTime) {
        this.processingTime = processingTime;
    }
}