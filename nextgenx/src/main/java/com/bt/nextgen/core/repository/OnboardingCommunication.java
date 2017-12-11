package com.bt.nextgen.core.repository;

import java.util.Date;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ONBOARDING_COMMUNICATION")
public class OnboardingCommunication {

    @Id()
    @Column(name = "COMMUNICATION_ID")
    private String communicationId;

    @Column(name = "ONBOARDING_APPLICATION_ID", nullable = false)
    private Long onboardingApplicationId;

    @Column(name = "GCM_PAN", nullable = false)
    private String gcmPan;

    /**
     * TODO: as part of US21070: J2EE7 / JPA2.1 upgrade
     * Currently "SoftBounce", "HardBounce", "Processed", "Initiated", "Error", "Success".
     * Database has no nulls, although the column is nullable.
     * Leave it as a string for now as it is only used for an error message string.
     */
    @Column(name = "STATUS")
    private String status;

    @Column(name = "EMAIL_ADDRESS", nullable = false)
    private String emailAddress;

    @Column(name = "COMMUNICATION_INITIATION_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date communicationInitiationTime;

    @Column(name = "TRACKING_ID")
    private String trackingId;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "FAILURE_MESSAGE")
    private String failureMessage;

    @Column(name = "LAST_MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Column(name = "LAST_MODIFIED_ID")
    private String lastModifiedId;

    public void setOnboardingApplicationId(Long onboardingApplicationId) {
        this.onboardingApplicationId = onboardingApplicationId;
    }

    public String getGcmPan() {
        return gcmPan;
    }

    public void setGcmPan(String gcmPan) {
        this.gcmPan = gcmPan;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Date getCommunicationInitiationTime() {
        return communicationInitiationTime;
    }

    public void setCommunicationInitiationTime(Date communicationInitiationTime) {
        this.communicationInitiationTime = communicationInitiationTime;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public String getCommunicationId() {
        return communicationId;
    }

    public void setCommunicationId(String communicationId) {
        this.communicationId = communicationId;
    }

    public Long getOnboardingApplicationId() {
        return onboardingApplicationId;
    }

    /**
     * Currently "SoftBounce", "HardBounce", "Processed", "Initiated", "Error", "Success".
     * Database has no nulls, although the column is nullable.
     */
    public @Nullable String getStatus() {
        return status;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedId() {
        return lastModifiedId;
    }

    public void setLastModifiedId(String lastModifiedId) {
        this.lastModifiedId = lastModifiedId;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("trackingId=" + getTrackingId());
        buffer.append(", createdDate=" + getCreatedDate());
        buffer.append(", failureMessage=" + getFailureMessage());
        buffer.append(", communicationId=" + getCommunicationId());
        buffer.append(", onboardingApplicationId=" + getOnboardingApplicationId());
        buffer.append(", status=" + getStatus());
        buffer.append(", lastModifiedDate=" + getLastModifiedDate());
        buffer.append(", lastModifiedId=" + getLastModifiedId());
        return buffer.toString();
    }
}
