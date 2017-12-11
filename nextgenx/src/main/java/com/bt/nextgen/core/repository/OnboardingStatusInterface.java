package com.bt.nextgen.core.repository;

public interface OnboardingStatusInterface
{

	void setStatus(OnboardingPartyDisplayStatus status);

	OnboardingPartyDisplayStatus getStatus();

	void setFailureMsg(String failureMsg);

	String getFailureMsg();
}
