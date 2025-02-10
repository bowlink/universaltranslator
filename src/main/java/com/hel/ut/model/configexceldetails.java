package com.hel.ut.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "REL_CONFIGEXCELDETAILS")
public class configexceldetails {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "ORGID", nullable = false)
    private Integer orgId;

    @Column(name = "CONFIGID", nullable = false)
    private Integer configId;
    
    @Column(name = "STARTROW", nullable = false)
    private Integer startRow = 1;
    
    @Column(name = "DISCARDLASTROWS", nullable = false)
    private Integer discardLastRows = 0;

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

    public Integer getConfigId() {
	return configId;
    }

    public void setConfigId(Integer configId) {
	this.configId = configId;
    }

    public Integer getStartRow() {
	return startRow;
    }

    public void setStartRow(Integer startRow) {
	this.startRow = startRow;
    }

    public Integer getDiscardLastRows() {
	return discardLastRows;
    }

    public void setDiscardLastRows(Integer discardLastRows) {
	this.discardLastRows = discardLastRows;
    }
    
}
