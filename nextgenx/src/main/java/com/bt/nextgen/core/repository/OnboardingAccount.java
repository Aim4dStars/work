package com.bt.nextgen.core.repository;

import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ONBOARDING_ACCOUNT")
public class OnboardingAccount {

    @EmbeddedId
    private OnboardingKey onboardingKey;

    @Column(name = "ACCOUNT_NUMBER")
    private String accountNumber;

    public OnboardingAccount() {
    }

    public OnboardingAccount(Long onboardingAccountSeq, OnboardingApplicationKey onboardingApplicationId) {
        this.onboardingKey = new OnboardingKey(onboardingAccountSeq, onboardingApplicationId.getId());
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public OnboardingKey getOnboardingKey() {
        return onboardingKey;
    }

    public OnboardingApplicationKey getOnboardingApplicationKey() {
        return OnboardingApplicationKey.valueOf(onboardingKey.getOnboardingApplicationId());
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Embeddable
    static class OnboardingKey implements Serializable {
        @Column(name = "ONBOARDING_APPLICATION_ID")
        private Long onboardingApplicationId;

        @Column(name = "ONBOARDING_ACCOUNT_SEQ")
        private Long onboardingAccountSeq;

        OnboardingKey(Long onboardingAccountSeq, Long onboardingApplicationId) {
            this.onboardingApplicationId = onboardingApplicationId;
            this.onboardingAccountSeq = onboardingAccountSeq;
        }

        public OnboardingKey() {
        }

        public Long getOnboardingApplicationId() {
            return onboardingApplicationId;
        }

        public Long getOnboardingAccountSeq() {
            return onboardingAccountSeq;
        }
    }

}
