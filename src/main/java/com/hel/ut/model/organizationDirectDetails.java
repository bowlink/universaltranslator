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

@Entity
@Table(name = "organizationdirectdetails")
public class organizationDirectDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "id", nullable = false)
    private int id;
    
    @Column(name = "orgId", nullable = false)
    private Integer orgId = 0;
    
    @Column(name = "status", nullable = false)
    private boolean status = true;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "dateCreated", nullable = false)
    private Date dateCreated = new Date();
    
    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "dateModified", nullable = false)
    private Date dateModified = new Date();
    
    @Column(name = "hispId", nullable = false)
    private Integer hispId = 0;
    
    @Column(name = "directDomain", nullable = false)
    private String directDomain;
    
    @Column(name = "fileTypeId", nullable = false)
    private Integer fileTypeId = 0;
    
    @Column(name = "expectedFileExt", nullable = false)
    private String expectedFileExt;
    
    @Column(name = "dmFindConfig", nullable = false)
    private Integer dmFindConfig = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Integer getHispId() {
        return hispId;
    }

    public void setHispId(Integer hispId) {
        this.hispId = hispId;
    }

    public String getDirectDomain() {
        return directDomain;
    }

    public void setDirectDomain(String directDomain) {
        this.directDomain = directDomain;
    }

    public Integer getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(Integer fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public String getExpectedFileExt() {
        return expectedFileExt;
    }

    public void setExpectedFileExt(String expectedFileExt) {
        this.expectedFileExt = expectedFileExt;
    }

    public Integer getDmFindConfig() {
        return dmFindConfig;
    }

    public void setDmFindConfig(Integer dmFindConfig) {
        this.dmFindConfig = dmFindConfig;
    }
}