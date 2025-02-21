package com.hel.ut.model;

import com.hel.ut.validator.NoHtml;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "USERS")
public class utUser {

    @Transient
    private String orgName, password, roleType, lastLogInDate = "";

    @Transient
    private Date dateOrgWasCreated = null, dateLastLoggedIn = null;

    @Transient
    private Integer orgType, timesloggedIn, totalTimeLoggedIn, totalLogins;

    @Transient
    private boolean connectionAssociated = false, sendSentEmail = false, sendReceivedEmail = false, uploadFiles = false, downloadFiles = false;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @GenericGenerator(name = "native",strategy = "native")
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "STATUS", nullable = false)
    private boolean status = false;

    @Column(name = "ORGID", nullable = false)
    private Integer orgId;

    @NotEmpty
    @NoHtml
    @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;

    @NotEmpty
    @NoHtml
    @Column(name = "LASTNAME", nullable = true)
    private String lastName;

    /**
     * @NotEmpty @NoHtml @Column(name = "PASSWORD", nullable = false) private String password;
     *
     */
    @NotEmpty
    @NoHtml
    @Size(min = 4, max = 15)
    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "ROLEID", nullable = false)
    private Integer roleId = 2;

    @Column(name = "MAINCONTACT", nullable = false)
    private Integer mainContact = 0;

    @Column(name = "SENDEMAILALERT", nullable = true)
    private boolean sendEmailAlert = false;

    @Column(name = "RECEIVEEMAILALERT", nullable = true)
    private boolean receiveEmailAlert = false;

    @Email
    @NoHtml
    @Column(name = "EMAIL", nullable = false)
    private String email;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "DATECREATED", nullable = true)
    private Date dateCreated = new Date();

    @Column(name = "USERTYPE", nullable = false)
    private Integer userType = 1;

    @Column(name = "DELIVERAUTHORITY", nullable = false)
    private boolean deliverAuthority = false;

    @Column(name = "EDITAUTHORITY", nullable = false)
    private boolean editAuthority = false;

    @Column(name = "CREATEAUTHORITY", nullable = false)
    private boolean createAuthority = false;

    @Column(name = "CANCELAUTHORITY", nullable = false)
    private boolean cancelAuthority = false;

    @NoHtml
    @Column(name = "RESETCODE", nullable = true)
    private String resetCode = null;

    @Column(name = "randomSalt", nullable = true)
    private byte[] randomSalt;

    @Column(name = "encryptedPw", nullable = true)
    private byte[] encryptedPw;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getLastLogInDate() {
        return lastLogInDate;
    }

    public void setLastLogInDate(String lastLogInDate) {
        this.lastLogInDate = lastLogInDate;
    }

    public Date getDateOrgWasCreated() {
        return dateOrgWasCreated;
    }

    public void setDateOrgWasCreated(Date dateOrgWasCreated) {
        this.dateOrgWasCreated = dateOrgWasCreated;
    }

    public Date getDateLastLoggedIn() {
        return dateLastLoggedIn;
    }

    public void setDateLastLoggedIn(Date dateLastLoggedIn) {
        this.dateLastLoggedIn = dateLastLoggedIn;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public void setOrgType(Integer orgType) {
        this.orgType = orgType;
    }

    public Integer getTimesloggedIn() {
        return timesloggedIn;
    }

    public void setTimesloggedIn(Integer timesloggedIn) {
        this.timesloggedIn = timesloggedIn;
    }

    public Integer getTotalTimeLoggedIn() {
        return totalTimeLoggedIn;
    }

    public void setTotalTimeLoggedIn(Integer totalTimeLoggedIn) {
        this.totalTimeLoggedIn = totalTimeLoggedIn;
    }

    public Integer getTotalLogins() {
        return totalLogins;
    }

    public void setTotalLogins(Integer totalLogins) {
        this.totalLogins = totalLogins;
    }

    public boolean isConnectionAssociated() {
        return connectionAssociated;
    }

    public void setConnectionAssociated(boolean connectionAssociated) {
        this.connectionAssociated = connectionAssociated;
    }

    public boolean isSendSentEmail() {
        return sendSentEmail;
    }

    public void setSendSentEmail(boolean sendSentEmail) {
        this.sendSentEmail = sendSentEmail;
    }

    public boolean isSendReceivedEmail() {
        return sendReceivedEmail;
    }

    public void setSendReceivedEmail(boolean sendReceivedEmail) {
        this.sendReceivedEmail = sendReceivedEmail;
    }

    public boolean isUploadFiles() {
        return uploadFiles;
    }

    public void setUploadFiles(boolean uploadFiles) {
        this.uploadFiles = uploadFiles;
    }

    public boolean isDownloadFiles() {
        return downloadFiles;
    }

    public void setDownloadFiles(boolean downloadFiles) {
        this.downloadFiles = downloadFiles;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getMainContact() {
        return mainContact;
    }

    public void setMainContact(Integer mainContact) {
        this.mainContact = mainContact;
    }

    public boolean isSendEmailAlert() {
        return sendEmailAlert;
    }

    public void setSendEmailAlert(boolean sendEmailAlert) {
        this.sendEmailAlert = sendEmailAlert;
    }

    public boolean isReceiveEmailAlert() {
        return receiveEmailAlert;
    }

    public void setReceiveEmailAlert(boolean receiveEmailAlert) {
        this.receiveEmailAlert = receiveEmailAlert;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public boolean isDeliverAuthority() {
        return deliverAuthority;
    }

    public void setDeliverAuthority(boolean deliverAuthority) {
        this.deliverAuthority = deliverAuthority;
    }

    public boolean isEditAuthority() {
        return editAuthority;
    }

    public void setEditAuthority(boolean editAuthority) {
        this.editAuthority = editAuthority;
    }

    public boolean isCreateAuthority() {
        return createAuthority;
    }

    public void setCreateAuthority(boolean createAuthority) {
        this.createAuthority = createAuthority;
    }

    public boolean isCancelAuthority() {
        return cancelAuthority;
    }

    public void setCancelAuthority(boolean cancelAuthority) {
        this.cancelAuthority = cancelAuthority;
    }

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

    public byte[] getRandomSalt() {
        return randomSalt;
    }

    public void setRandomSalt(byte[] randomSalt) {
        this.randomSalt = randomSalt;
    }

    public byte[] getEncryptedPw() {
        return encryptedPw;
    }

    public void setEncryptedPw(byte[] encryptedPw) {
        this.encryptedPw = encryptedPw;
    }
}