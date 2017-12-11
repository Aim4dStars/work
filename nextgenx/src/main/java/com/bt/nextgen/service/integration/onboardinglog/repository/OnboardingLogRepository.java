package com.bt.nextgen.service.integration.onboardinglog.repository;

import com.bt.nextgen.service.integration.onboardinglog.model.OnboardingLog;

import java.util.List;

public interface OnboardingLogRepository {

    public List<OnboardingLog> findAll();

}