package com.hel.ut.model;

import java.util.List;
import com.hel.ut.validator.NoHtml;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "rel_transportWebServiceDetails")
public class configurationWebServiceFields {

    @Transient
    private List<configurationWebServiceSenders> senderDomainList = null;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "TRANSPORTID", nullable = false)
    private Integer transportId = 0;

    @NoHtml
    @Column(name = "email", nullable = true)
    private String email = null;

    @Column(name = "Method", nullable = true)
    private Integer method = 0;

    @Column(name = "tagPosition", nullable = true)
    private Integer tagPosition = 1;

    @NoHtml
    @Column(name = "tagName", nullable = true)
    private String tagName = null;

    @Column(name = "textInAttachment", nullable = true)
    private String textInAttachment = null;

    @Column(name = "mimeType", nullable = true)
    private String mimeType = "text/xml";

    public List<configurationWebServiceSenders> getSenderDomainList() {
        return senderDomainList;
    }

    public void setSenderDomainList(List<configurationWebServiceSenders> senderDomainList) {
        this.senderDomainList = senderDomainList;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public Integer getTagPosition() {
        return tagPosition;
    }

    public void setTagPosition(Integer tagPosition) {
        this.tagPosition = tagPosition;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTextInAttachment() {
        return textInAttachment;
    }

    public void setTextInAttachment(String textInAttachment) {
        this.textInAttachment = textInAttachment;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}