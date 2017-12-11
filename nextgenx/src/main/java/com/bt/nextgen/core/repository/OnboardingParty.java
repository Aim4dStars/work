package com.bt.nextgen.core.repository;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="ONBOARDING_PARTY")
public class OnboardingParty {

    @EmbeddedId
    private OnboardingPartyKey onboardingPartyKey;
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    // TODO: use @Convert(converter=OnboardingPartyStatusConverter.class) when we are using >j2ee 6.0 and >JPA 2.0
    // OnboardingPartyStatusConverter implements javax.persistence.AttributeConverter<OnboardingPartyStatus, String> {
    // There are two methods:
    // public String convertToDatabaseColumn(OnboardingPartyStatus attribute){...}
    // public OnboardingPartyStatus convertToEntityAttribute(String dbString) {...}
    private OnboardingPartyStatus status;

    @Column(name = "GCM_PAN")
    private String gcmPan;

	@Column(name = "FAILURE_MESSAGE")
	private String failureMessage;

    @Column(name = "LAST_MODIFIED_ID")
    private String lastModifiedId;

    @Column(name = "LAST_MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    public OnboardingParty() {
    }

    public OnboardingParty(int onboardingPartySeq, Long onboardingApplicationId, String gcmPan){
        this.onboardingPartyKey =  new OnboardingPartyKey(onboardingPartySeq,onboardingApplicationId);
        this.gcmPan = gcmPan;
    }

    public OnboardingPartyKey getOnboardingPartyKey() {
        return onboardingPartyKey;
    }

    public @Nullable OnboardingPartyStatus getStatus() {
        return status;
    }

    public void setStatus(@Nullable OnboardingPartyStatus status) {
        this.status = status;
    }

    public String getGcmPan() {
        return gcmPan;
    }

    public void setGcmPan(String gcmPan) {
        this.gcmPan = gcmPan;
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

    @Embeddable
    static class OnboardingPartyKey implements Serializable {

        @Column(name="ONBOARDING_PARTY_SEQ")
        private int onboardingPartySeq;

        @Column(name="ONBOARDING_APPLICATION_ID")
        private Long onboardingApplicationId;

        public OnboardingPartyKey(int onboardingPartySeq, Long onboardingApplicationId)
        {
            this.onboardingPartySeq = onboardingPartySeq;
            this.onboardingApplicationId = onboardingApplicationId;
        }

        public OnboardingPartyKey(){
        }

        public int getOnboardingPartySeq() {
            return onboardingPartySeq;
        }

        public Long getOnboardingApplicationId() {
            return onboardingApplicationId;
        }

    }

    public int getOnboardingPartySeq() {
        return onboardingPartyKey.getOnboardingPartySeq();
    }
    public Long getOnboardingApplicationId() {
        return onboardingPartyKey.getOnboardingApplicationId();
    }

	/**
	 * @return the failureMessage
	 */
	public String getFailureMessage()
	{
		return failureMessage;
	}

	/**
	 * @param failureMessage the failureMessage to set
	 */
	public void setFailureMessage(String failureMessage)
	{
		this.failureMessage = failureMessage;
	}

}
