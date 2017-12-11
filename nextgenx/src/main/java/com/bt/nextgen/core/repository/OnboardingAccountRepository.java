package com.bt.nextgen.core.repository;

import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;

import java.util.List;

public interface OnboardingAccountRepository {
    public OnboardingAccount findByAccountNumber(String accountNumber);
    public OnboardingAccount findByOnboardingApplicationId(OnboardingApplicationKey key);
    public List<OnboardingAccount> findByOnboardingApplicationIds(List<Long> keys);
}
