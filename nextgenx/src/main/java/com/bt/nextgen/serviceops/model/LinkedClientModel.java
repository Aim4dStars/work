package com.bt.nextgen.serviceops.model;

import com.btfin.panorama.core.security.integration.userprofile.JobProfile;

import java.util.List;

/**
 * Created by l078482 on 10/05/2017.
 */
public class LinkedClientModel {

    private String gcmId;
    private String title;
    private String firstName;
    private String lastName;
    private String fullName;
    private String postalAddress;
    private List<JobProfile> jobProfiles;
    private String paymentSetting;
    private boolean adviserFlag;
    private boolean primaryContactFlag;
    private String clientId;
    private String detailPageUrl;
    private String modificationSequenceNumber;
    public String getDetailPageUrl() {
        return detailPageUrl;
    }

    public void setDetailPageUrl(String detailPageUrl) {
        this.detailPageUrl = detailPageUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    private String roles;

    public String getActionMessage() {
        return actionMessage;
    }

    public void setActionMessage(String actionMessage) {
        this.actionMessage = actionMessage;
    }

    private String actionMessage;

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public List<JobProfile> getJobProfiles() {
        return jobProfiles;
    }

    public void setJobProfiles(List<JobProfile> jobProfiles) {
        this.jobProfiles = jobProfiles;
    }

    public String getPaymentSetting() {
        return paymentSetting;
    }

    public void setPaymentSetting(String paymentSetting) {
        this.paymentSetting = paymentSetting;
    }

    public boolean isAdviserFlag() {
        return adviserFlag;
    }

    public void setAdviserFlag(boolean adviserFlag) {
        this.adviserFlag = adviserFlag;
    }

    public boolean isPrimaryContactFlag() {
        return primaryContactFlag;
    }

    public void setPrimaryContactFlag(boolean primaryContactFlag) {
        this.primaryContactFlag = primaryContactFlag;
    }

    public String getModificationSequenceNumber() {
        return modificationSequenceNumber;
    }

    public void setModificationSequenceNumber(String modificationSequenceNumber) {
        this.modificationSequenceNumber = modificationSequenceNumber;
    }
    
}
