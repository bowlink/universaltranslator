package com.hel.ut.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name = "log_ftpconnectionerrors")
public class logftpconnectionerrors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "ftpConnectionId", nullable = false)
    private int ftpConnectionId;

    @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Column(name = "dateCreated", nullable = false)
    private Date dateCreated = new Date();
    
    @Column(name = "connectionError", nullable = true)
    private String connectionError;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public int getFtpConnectionId() {
	return ftpConnectionId;
    }

    public void setFtpConnectionId(int ftpConnectionId) {
	this.ftpConnectionId = ftpConnectionId;
    }

    public Date getDateCreated() {
	return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    public String getConnectionError() {
	return connectionError;
    }

    public void setConnectionError(String connectionError) {
	this.connectionError = connectionError;
    }
}
