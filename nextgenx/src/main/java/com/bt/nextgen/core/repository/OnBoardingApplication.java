package com.bt.nextgen.core.repository;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;

@Entity
@Table(name = "ONBOARDING_APPLICATION")
public class OnBoardingApplication implements Serializable {

    @Id()
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ONBOARDING_APPLICATION_SEQ")
    @SequenceGenerator(name = "ONBOARDING_APPLICATION_SEQ", sequenceName = "ONBOARDING_APPLICATION_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private OnboardingApplicationStatus status = null;

    @Column(name = "AVALOQ_ORDER_ID")
    private String avaloqOrderId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="ONBOARDING_APPLICATION_ID", referencedColumnName = "id")
    private List<OnboardingParty> parties = Collections.emptyList();

	@Column(name = "FAILURE_MESSAGE")
	private String failureMessage;

    @Column(name = "APPLICATION_TYPE")
    private String applicationType;

    @Column(name = "OFFLINE_APPROVAL")
    private boolean offline = false;

    public void setFailureMessage(String failureMessage)
	{
		this.failureMessage = failureMessage;
	}

	public OnBoardingApplication(){
    }

    public OnBoardingApplication(String applicationType, boolean offline) {
        this.applicationType = applicationType;
        this.offline = offline;
    }

    public OnBoardingApplication(@Nullable OnboardingApplicationStatus status, String avaloqOrderId) {
        this.status = status;
        this.avaloqOrderId = avaloqOrderId;
    }

    public OnboardingApplicationKey getKey() {
        if (id != null) {
            return OnboardingApplicationKey.valueOf(id);
        }
        return null;
    }

    public @Nullable OnboardingApplicationStatus getStatus() {
        return status;
    }

    public String getAvaloqOrderId() {
        return avaloqOrderId;
    }


    public List<OnboardingParty> getParties() {
        return parties;
    }

    public void setParties(List<OnboardingParty> parties) {
        this.parties = parties;
	}

	/**
	 * @return the failureMessage
	 */
	public String getFailureMessage()
	{
		return failureMessage;
	}

    public void setStatus(@Nullable OnboardingApplicationStatus status) {
        this.status = status;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }
}
