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

@Entity
@Table(name = "REL_TRANSPORTFTPDETAILS")
public class configurationFTPFields {

    @Transient
    private MultipartFile file;
    
    @Transient
    private String FTPPassword = "";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "TRANSPORTID", nullable = false)
    private Integer transportId = 0;

    @Column(name = "IP", nullable = true)
    private String ip = null;

    @NoHtml
    @Column(name = "DIRECTORY", nullable = true)
    private String directory = null;

    @Column(name = "USERNAME", nullable = true)
    private String username = null;

    @Column(name = "PASSWORD", nullable = true)
    private byte[] password = null;

    @Column(name = "METHOD", nullable = false)
    private Integer method = 1;

    @Column(name = "PORT", nullable = true)
    private Integer port = 0;

    @NoHtml
    @Column(name = "PROTOCOL", nullable = true)
    private String protocol = "FTP";

    @Column(name = "CERTIFICATION", nullable = true)
    private String certification = null;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFTPPassword() {
        return FTPPassword;
    }

    public void setFTPPassword(String FTPPassword) {
        this.FTPPassword = FTPPassword;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTransportId() {
        return transportId;
    }

    public void setTransportId(Integer transportId) {
        this.transportId = transportId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }
}
