package com.hel.ut.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "REL_USERLOGINS")
public class utUserLogin {
    
    @Transient
    private int totalTimeLoggedIn;
    
    @Transient
    private String dateLastLoggedIn = "", logInDate = "";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "USERID", nullable = false)
    private int userId;

    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();
    
    @Column(name = "dateLoggedOut", nullable = true)
    private Date dateLoggedOut = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateLoggedOut(Date dateLoggedOut) {
	this.dateLoggedOut = dateLoggedOut;
    }

    public int getTotalTimeLoggedIn() {
	return totalTimeLoggedIn;
    }

    public void setTotalTimeLoggedIn(int totalTimeLoggedIn) {
	this.totalTimeLoggedIn = totalTimeLoggedIn;
    }

    public String getDateLastLoggedIn() {
	return dateLastLoggedIn;
    }

    public void setDateLastLoggedIn(String dateLastLoggedIn) {
	this.dateLastLoggedIn = dateLastLoggedIn;
    }

    public String getLogInDate() {
	return logInDate;
    }

    public void setLogInDate(String logInDate) {
	this.logInDate = logInDate;
    }

    public void setDateCreated(Date dateCreated) {
	this.dateCreated = dateCreated;
    }

    
}
