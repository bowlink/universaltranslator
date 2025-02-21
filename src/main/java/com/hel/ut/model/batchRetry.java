/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "batchretry")
public class batchRetry {
	
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "batchUploadId", nullable = false)
    private Integer batchUploadId;

    @Column(name = "fromStatusId", nullable = false)
    private Integer fromStatusId;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATECREATED", nullable = false)
    private Date dateCreated = new Date();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getBatchUploadId() {
        return batchUploadId;
    }

    public void setBatchUploadId(Integer batchUploadId) {
        this.batchUploadId = batchUploadId;
    }

    public Integer getFromStatusId() {
        return fromStatusId;
    }

    public void setFromStatusId(Integer fromStatusId) {
        this.fromStatusId = fromStatusId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}