package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import jakarta.persistence.*;
import java.util.List;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "CONFIGURATIONTRANSPORTDETAILS")
public class configurationTransport {

    @Transient
    private List<configurationFormFields> fields = null;
    
    @Transient
    private List<appenedNewconfigurationFormFields> newfields = null;

    @Transient
    private List<configurationFTPFields> FTPfields = null;

    @Transient
    private List<configurationFileDropFields> fileDropFields = null;

    @Transient
    private List<configurationWebServiceFields> webServiceFields = null;
    
     @Transient
    private List<organizationDirectDetails> directMessageFields = null;

    @Transient
    private String delimChar = null;

    @Transient
    private boolean containsHeaderRow;

    @Transient
    private List<Integer> messageTypes = null;
    
    @Transient
    private Integer threshold = 100;

    @Transient
    private MultipartFile ccdTemplatefile = null, hl7PDFTemplatefile = null;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGID", nullable = false)
    private Integer configId;

    @Column(name = "TRANSPORTMETHODID", nullable = false)
    private Integer transportMethodId;

    @Column(name = "FILETYPE", nullable = true)
    private Integer fileType = 1;

    @Column(name = "FILEDELIMITER", nullable = true)
    private Integer fileDelimiter = 2;

    @Column(name = "STATUS", nullable = false)
    private boolean status = true;

    @NoHtml
    @Column(name = "TARGETFILENAME", nullable = true)
    private String targetFileName = null;

    @Column(name = "APPENDDATETIME", nullable = false)
    private boolean appendDateTime = false;

    @Column(name = "MAXFILESIZE", nullable = false)
    private Integer maxFileSize = 10;

    @Column(name = "CLEARRECORDS", nullable = false)
    private boolean clearRecords = true;

    @Column(name = "FILELOCATION", nullable = true)
    private String fileLocation = null;

    @Column(name = "AUTORELEASE", nullable = false)
    private boolean autoRelease = true;

    @Column(name = "ERRORHANDLING", nullable = false)
    private Integer errorHandling = 2;

    @Column(name = "MERGEBATCHES", nullable = false)
    private boolean mergeBatches = true;

    @Column(name = "COPIEDTRANSPORTID", nullable = false)
    private Integer copiedTransportId = 0;

    @Column(name = "massTranslation", nullable = false)
    private boolean massTranslation = true;

    @NoHtml
    @Column(name = "FILEEXT", nullable = false)
    private String fileExt = null;

    @Column(name = "encodingId", nullable = false)
    private Integer encodingId = 1;

    @Column(name = "ccdSampleTemplate", nullable = true)
    private String ccdSampleTemplate = null;

    @Column(name = "HL7PDFSampleTemplate", nullable = true)
    private String HL7PDFSampleTemplate = null;
    
    @Column(name = "ZIPPED", nullable = false)
    private boolean zipped = false;
    
    @Column(name = "zipType", nullable = true)
    private Integer zipType = 0;
    
    @Column(name = "restAPIURL", nullable = true)
    private String restAPIURL = null;
    
    @Column(name = "restAPIUsername", nullable = true)
    private String restAPIUsername = null;
    
    @Column(name = "restAPIPassword", nullable = true)
    private String restAPIPassword = null;
    
    @Column(name = "restAPIType", nullable = true)
    private Integer restAPIType = 1;
    
    @Column(name = "waitForResponse", nullable = false)
    private boolean waitForResponse = false;
    
    @Column(name = "restAPIFunctionId", nullable = true)
    private Integer restAPIFunctionId = 0;
    
    @Column(name = "jsonWrapperElement", nullable = true)
    private String jsonWrapperElement = "";
    
    @Column(name = "lineTerminator", nullable = true)
    private String lineTerminator = "\\n";
    
    @Column(name = "helRegistryConfigId", nullable = false)
    private Integer helRegistryConfigId = 0;
    
    @Column(name = "helSchemaName", nullable = false)
    private String helSchemaName = "";
    
    @Column(name = "helRegistryId", nullable = false)
    private Integer helRegistryId = 0;
    
    @Column(name = "dmConfigKeyword", nullable = true)
    private String dmConfigKeyword = "";
    
    @Column(name = "ergFileDownload", nullable = false)
    private boolean ergFileDownload = false;
    
    @Column(name = "populateInboundAuditReport", nullable = false)
    private boolean populateInboundAuditReport = false;

    @Column(name = "addTargetFileHeaderRow", nullable = false)
    private boolean addTargetFileHeaderRow = false;
    
    @Column(name = "errorEmailAddresses", nullable = true)
    private String errorEmailAddresses = "";

    public List<configurationFormFields> getFields() {
        return fields;
    }

    public void setFields(List<configurationFormFields> fields) {
        this.fields = fields;
    }

    public List<appenedNewconfigurationFormFields> getNewfields() {
        return newfields;
    }

    public void setNewfields(List<appenedNewconfigurationFormFields> newfields) {
        this.newfields = newfields;
    }

    public List<configurationFTPFields> getFTPfields() {
        return FTPfields;
    }

    public void setFTPfields(List<configurationFTPFields> FTPfields) {
        this.FTPfields = FTPfields;
    }

    public List<configurationFileDropFields> getFileDropFields() {
        return fileDropFields;
    }

    public void setFileDropFields(List<configurationFileDropFields> fileDropFields) {
        this.fileDropFields = fileDropFields;
    }

    public List<configurationWebServiceFields> getWebServiceFields() {
        return webServiceFields;
    }

    public void setWebServiceFields(List<configurationWebServiceFields> webServiceFields) {
        this.webServiceFields = webServiceFields;
    }

    public List<organizationDirectDetails> getDirectMessageFields() {
        return directMessageFields;
    }

    public void setDirectMessageFields(List<organizationDirectDetails> directMessageFields) {
        this.directMessageFields = directMessageFields;
    }

    public String getDelimChar() {
        return delimChar;
    }

    public void setDelimChar(String delimChar) {
        this.delimChar = delimChar;
    }

    public boolean isContainsHeaderRow() {
        return containsHeaderRow;
    }

    public void setContainsHeaderRow(boolean containsHeaderRow) {
        this.containsHeaderRow = containsHeaderRow;
    }

    public List<Integer> getMessageTypes() {
        return messageTypes;
    }

    public void setMessageTypes(List<Integer> messageTypes) {
        this.messageTypes = messageTypes;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public MultipartFile getCcdTemplatefile() {
        return ccdTemplatefile;
    }

    public void setCcdTemplatefile(MultipartFile ccdTemplatefile) {
        this.ccdTemplatefile = ccdTemplatefile;
    }

    public MultipartFile getHl7PDFTemplatefile() {
        return hl7PDFTemplatefile;
    }

    public void setHl7PDFTemplatefile(MultipartFile hl7PDFTemplatefile) {
        this.hl7PDFTemplatefile = hl7PDFTemplatefile;
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

    public Integer getTransportMethodId() {
        return transportMethodId;
    }

    public void setTransportMethodId(Integer transportMethodId) {
        this.transportMethodId = transportMethodId;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Integer getFileDelimiter() {
        return fileDelimiter;
    }

    public void setFileDelimiter(Integer fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public boolean isAppendDateTime() {
        return appendDateTime;
    }

    public void setAppendDateTime(boolean appendDateTime) {
        this.appendDateTime = appendDateTime;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public boolean isClearRecords() {
        return clearRecords;
    }

    public void setClearRecords(boolean clearRecords) {
        this.clearRecords = clearRecords;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public boolean isAutoRelease() {
        return autoRelease;
    }

    public void setAutoRelease(boolean autoRelease) {
        this.autoRelease = autoRelease;
    }

    public Integer getErrorHandling() {
        return errorHandling;
    }

    public void setErrorHandling(Integer errorHandling) {
        this.errorHandling = errorHandling;
    }

    public boolean isMergeBatches() {
        return mergeBatches;
    }

    public void setMergeBatches(boolean mergeBatches) {
        this.mergeBatches = mergeBatches;
    }

    public Integer getCopiedTransportId() {
        return copiedTransportId;
    }

    public void setCopiedTransportId(Integer copiedTransportId) {
        this.copiedTransportId = copiedTransportId;
    }

    public boolean isMassTranslation() {
        return massTranslation;
    }

    public void setMassTranslation(boolean massTranslation) {
        this.massTranslation = massTranslation;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public Integer getEncodingId() {
        return encodingId;
    }

    public void setEncodingId(Integer encodingId) {
        this.encodingId = encodingId;
    }

    public String getCcdSampleTemplate() {
        return ccdSampleTemplate;
    }

    public void setCcdSampleTemplate(String ccdSampleTemplate) {
        this.ccdSampleTemplate = ccdSampleTemplate;
    }

    public String getHL7PDFSampleTemplate() {
        return HL7PDFSampleTemplate;
    }

    public void setHL7PDFSampleTemplate(String HL7PDFSampleTemplate) {
        this.HL7PDFSampleTemplate = HL7PDFSampleTemplate;
    }

    public boolean isZipped() {
        return zipped;
    }

    public void setZipped(boolean zipped) {
        this.zipped = zipped;
    }

    public Integer getZipType() {
        return zipType;
    }

    public void setZipType(Integer zipType) {
        this.zipType = zipType;
    }

    public String getRestAPIURL() {
        return restAPIURL;
    }

    public void setRestAPIURL(String restAPIURL) {
        this.restAPIURL = restAPIURL;
    }

    public String getRestAPIUsername() {
        return restAPIUsername;
    }

    public void setRestAPIUsername(String restAPIUsername) {
        this.restAPIUsername = restAPIUsername;
    }

    public String getRestAPIPassword() {
        return restAPIPassword;
    }

    public void setRestAPIPassword(String restAPIPassword) {
        this.restAPIPassword = restAPIPassword;
    }

    public Integer getRestAPIType() {
        return restAPIType;
    }

    public void setRestAPIType(Integer restAPIType) {
        this.restAPIType = restAPIType;
    }

    public boolean isWaitForResponse() {
        return waitForResponse;
    }

    public void setWaitForResponse(boolean waitForResponse) {
        this.waitForResponse = waitForResponse;
    }

    public Integer getRestAPIFunctionId() {
        return restAPIFunctionId;
    }

    public void setRestAPIFunctionId(Integer restAPIFunctionId) {
        this.restAPIFunctionId = restAPIFunctionId;
    }

    public String getJsonWrapperElement() {
        return jsonWrapperElement;
    }

    public void setJsonWrapperElement(String jsonWrapperElement) {
        this.jsonWrapperElement = jsonWrapperElement;
    }

    public String getLineTerminator() {
        return lineTerminator;
    }

    public void setLineTerminator(String lineTerminator) {
        this.lineTerminator = lineTerminator;
    }

    public Integer getHelRegistryConfigId() {
        return helRegistryConfigId;
    }

    public void setHelRegistryConfigId(Integer helRegistryConfigId) {
        this.helRegistryConfigId = helRegistryConfigId;
    }

    public String getHelSchemaName() {
        return helSchemaName;
    }

    public void setHelSchemaName(String helSchemaName) {
        this.helSchemaName = helSchemaName;
    }

    public Integer getHelRegistryId() {
        return helRegistryId;
    }

    public void setHelRegistryId(Integer helRegistryId) {
        this.helRegistryId = helRegistryId;
    }

    public String getDmConfigKeyword() {
        return dmConfigKeyword;
    }

    public void setDmConfigKeyword(String dmConfigKeyword) {
        this.dmConfigKeyword = dmConfigKeyword;
    }

    public boolean isErgFileDownload() {
        return ergFileDownload;
    }

    public void setErgFileDownload(boolean ergFileDownload) {
        this.ergFileDownload = ergFileDownload;
    }

    public boolean isPopulateInboundAuditReport() {
        return populateInboundAuditReport;
    }

    public void setPopulateInboundAuditReport(boolean populateInboundAuditReport) {
        this.populateInboundAuditReport = populateInboundAuditReport;
    }

    public boolean isAddTargetFileHeaderRow() {
        return addTargetFileHeaderRow;
    }

    public void setAddTargetFileHeaderRow(boolean addTargetFileHeaderRow) {
        this.addTargetFileHeaderRow = addTargetFileHeaderRow;
    }

    public String getErrorEmailAddresses() {
        return errorEmailAddresses;
    }

    public void setErrorEmailAddresses(String errorEmailAddresses) {
        this.errorEmailAddresses = errorEmailAddresses;
    }
}