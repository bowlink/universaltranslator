/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONTRANSPORTMESSAGETYPES")
public class configurationTransportMessageTypes {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGTRANSPORTID", nullable = false)
    private Integer configTransportId;

    @Column(name = "CONFIGID", nullable = false)
    private Integer configId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getConfigTransportId() {
        return configTransportId;
    }

    public void setConfigTransportId(Integer configTransportId) {
        this.configTransportId = configTransportId;
    }

    public Integer getConfigId() {
        return configId;
    }

    public void setConfigId(Integer configId) {
        this.configId = configId;
    }
}