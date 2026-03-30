/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hel.ut.model.custom;

import java.util.Date;

/**
 *
 * @author chadmccue
 */
public class configAuditLogs {
    
    private String moduleName = "", updatedBy = "", snapShotType = "", fileName = "";
    
    private Date dateUpdated = new Date();

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getSnapShotType() {
        return snapShotType;
    }

    public void setSnapShotType(String snapShotType) {
        this.snapShotType = snapShotType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}