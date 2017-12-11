package com.bt.nextgen.service.integration.onboardinglog.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "ONBOARDING_LOG")
public class OnboardingLog implements Serializable {

    private static final long serialVersionUID = -57950251941603983L;

    @Id
    @Column(name = "LOG_ID")
    @SequenceGenerator(name = "ONBOARDING_LOG_SEQ", sequenceName = "ONBOARDING_LOG_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ONBOARDING_LOG_SEQ")
    private Integer logId;

    @Column(name = "ONBOARDING_APPLICATION_ID")
    private String applicationId;

    @Column(name = "CLIENT_GCM_ID")
    private String clientGcmId;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "FAILURE_MESSAGE")
    private String failureMessage;

    @Column(name = "EVENT_TYPE")
    private String eventType;

    @Column(name = "HAS_BEEN_LOGGED")
    private Boolean hasBeenLogged;

    public OnboardingLog() {
        // default constructor for hibernate
    }

    public Integer getLogId() {
        return logId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getClientGcmId() {
        return clientGcmId;
    }

    public String getStatus() {
        return status;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public String getEventType() {
        return eventType;
    }

    public Boolean getHasBeenLogged() {
        return hasBeenLogged;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public void setClientGcmId(String clientGcmId) {
        this.clientGcmId = clientGcmId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setHasBeenLogged(Boolean hasBeenLogged) {
        this.hasBeenLogged = hasBeenLogged;
    }
}