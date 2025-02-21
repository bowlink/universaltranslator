package com.hel.ut.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "generatedactivityreportagencies")
public class generatedActivityReportAgencies {
    
    @Transient
    private String orgName, helRegistrySchemaName;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "id", nullable = false)
    private int id;
    
    @Column(name = "orgId", nullable = true)
    private Integer orgId = 0;
    
    @Column(name = "reportId", nullable = true)
    private Integer reportId = 0;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getHelRegistrySchemaName() {
        return helRegistrySchemaName;
    }

    public void setHelRegistrySchemaName(String helRegistrySchemaName) {
        this.helRegistrySchemaName = helRegistrySchemaName;
    }

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

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }
}