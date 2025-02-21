/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONMESSAGESPECS")
public class configurationMessageSpecs {

    @Transient
    private MultipartFile file = null, parsingScriptFile = null;
    
    @Transient
    private boolean hasHeader = false;
    
    @Transient
    private Integer fileLayout = 1; //1 = Horizontal 2= Vertical

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGID", nullable = false)
    private Integer configId;

    @NoHtml
    @Column(name = "TEMPLATEFILE", nullable = true)
    private String templateFile = null;

    @Column(name = "MESSAGETYPECOL", nullable = false)
    private Integer messageTypeCol = 0;

    @NoHtml
    @Column(name = "MESSAGETYPEVAL", nullable = true)
    private String messageTypeVal = null;

    @Column(name = "TARGETORGCOL", nullable = false)
    private Integer targetOrgCol = 0;

    @Column(name = "sourceSubOrgCol", nullable = false)
    private Integer sourceSubOrgCol = 0;

    @Column(name = "CONTAINSHEADERROW", nullable = false)
    private boolean containsHeaderRow = false;

    @Column(name = "RPTFIELD1", nullable = false)
    private Integer rptField1 = 0;

    @Column(name = "RPTFIELD2", nullable = false)
    private Integer rptField2 = 0;

    @Column(name = "RPTFIELD3", nullable = false)
    private Integer rptField3 = 0;

    @Column(name = "RPTFIELD4", nullable = false)
    private Integer rptField4 = 0;
    
    @Column(name = "EXCELSTARTROW", nullable = false)
    private Integer excelstartrow = 1;
    
    @Column(name = "EXCELSKIPROWS", nullable = false)
    private Integer excelskiprows = 0;
    
    @NoHtml
    @Column(name = "PARSINGTEMPLATE", nullable = true)
    private String parsingTemplate = null;
    
    @Column(name = "fileNameConfigHeader", nullable = false)
    private String fileNameConfigHeader;
    
    @Column(name = "totalHeaderRows", nullable = false)
    private Integer totalHeaderRows = 1;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getParsingScriptFile() {
        return parsingScriptFile;
    }

    public void setParsingScriptFile(MultipartFile parsingScriptFile) {
        this.parsingScriptFile = parsingScriptFile;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public Integer getFileLayout() {
        return fileLayout;
    }

    public void setFileLayout(Integer fileLayout) {
        this.fileLayout = fileLayout;
    }

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

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public Integer getMessageTypeCol() {
        return messageTypeCol;
    }

    public void setMessageTypeCol(Integer messageTypeCol) {
        this.messageTypeCol = messageTypeCol;
    }

    public String getMessageTypeVal() {
        return messageTypeVal;
    }

    public void setMessageTypeVal(String messageTypeVal) {
        this.messageTypeVal = messageTypeVal;
    }

    public Integer getTargetOrgCol() {
        return targetOrgCol;
    }

    public void setTargetOrgCol(Integer targetOrgCol) {
        this.targetOrgCol = targetOrgCol;
    }

    public Integer getSourceSubOrgCol() {
        return sourceSubOrgCol;
    }

    public void setSourceSubOrgCol(Integer sourceSubOrgCol) {
        this.sourceSubOrgCol = sourceSubOrgCol;
    }

    public boolean isContainsHeaderRow() {
        return containsHeaderRow;
    }

    public void setContainsHeaderRow(boolean containsHeaderRow) {
        this.containsHeaderRow = containsHeaderRow;
    }

    public Integer getRptField1() {
        return rptField1;
    }

    public void setRptField1(Integer rptField1) {
        this.rptField1 = rptField1;
    }

    public Integer getRptField2() {
        return rptField2;
    }

    public void setRptField2(Integer rptField2) {
        this.rptField2 = rptField2;
    }

    public Integer getRptField3() {
        return rptField3;
    }

    public void setRptField3(Integer rptField3) {
        this.rptField3 = rptField3;
    }

    public Integer getRptField4() {
        return rptField4;
    }

    public void setRptField4(Integer rptField4) {
        this.rptField4 = rptField4;
    }

    public Integer getExcelstartrow() {
        return excelstartrow;
    }

    public void setExcelstartrow(Integer excelstartrow) {
        this.excelstartrow = excelstartrow;
    }

    public Integer getExcelskiprows() {
        return excelskiprows;
    }

    public void setExcelskiprows(Integer excelskiprows) {
        this.excelskiprows = excelskiprows;
    }

    public String getParsingTemplate() {
        return parsingTemplate;
    }

    public void setParsingTemplate(String parsingTemplate) {
        this.parsingTemplate = parsingTemplate;
    }

    public String getFileNameConfigHeader() {
        return fileNameConfigHeader;
    }

    public void setFileNameConfigHeader(String fileNameConfigHeader) {
        this.fileNameConfigHeader = fileNameConfigHeader;
    }

    public Integer getTotalHeaderRows() {
        return totalHeaderRows;
    }

    public void setTotalHeaderRows(Integer totalHeaderRows) {
        this.totalHeaderRows = totalHeaderRows;
    }
}