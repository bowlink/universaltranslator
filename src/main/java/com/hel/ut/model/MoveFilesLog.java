package com.hel.ut.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.hel.ut.validator.NoHtml;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "moveFilesLog")
public class MoveFilesLog {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "transportMethodId", nullable = true)
    private Integer transportMethodId;

    @Column(name = "transportId", nullable = true)
    private Integer transportId;

    @Column(name = "statusId", nullable = false)
    private Integer statusId;

    @Column(name = "method", nullable = false)
    private Integer method;

    @NoHtml
    @Column(name = "folderPath", nullable = true)
    private String folderPath;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "startDateTime", nullable = true)
    private Date startDateTime = new Date();

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "endDateTime", nullable = true)
    private Date endDateTime = new Date();

    @NoHtml
    @Column(name = "notes", nullable = true)
    private String notes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTransportMethodId() {
        return transportMethodId;
    }

    public void setTransportMethodId(Integer transportMethodId) {
        this.transportMethodId = transportMethodId;
    }

    public Integer getTransportId() {
        return transportId;
    }

    public void setTransportId(Integer transportId) {
        this.transportId = transportId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }  
}