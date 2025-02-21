package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "CROSSWALKS")
public class Crosswalks {

    @Transient
    private MultipartFile file;
    
    @Transient
    private Integer dtsId = 0;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @NoHtml
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "FILEDELIMITER", nullable = false)
    private Integer fileDelimiter = 0;

    @NoHtml
    @Column(name = "FILENAME", nullable = false)
    private String fileName;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "ORGID", nullable = true)
    private Integer orgId = 0;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "lastUpdated", nullable = true)
    private Date lastUpdated = null;

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

    public Integer getFileDelimiter() {
        return fileDelimiter;
    }

    public void setFileDelimiter(Integer fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Integer getDtsId() {
	return dtsId;
    }

    public void setDtsId(Integer dtsId) {
	this.dtsId = dtsId;
    }

    public Date getLastUpdated() {
	return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
	this.lastUpdated = lastUpdated;
    }
}