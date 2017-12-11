package com.bt.nextgen.serviceops.model;

import java.util.List;

import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingPartyDto;

/**
 * Created by L091297 on 08/06/2017.
 */

public class MaintainOnboardingStatusModel {

	private OnBoardingApplication onBoardingApplication;
	
	private List <OnboardingPartyDto> onboardingPartyList;
	
	private String applicationStatus;
	
	private String statusMessage;

	public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public OnBoardingApplication getOnBoardingApplication() {
        return onBoardingApplication;
    }

    public void setOnBoardingApplication(OnBoardingApplication onBoardingApplication) {
        this.onBoardingApplication = onBoardingApplication;
    }

    public List<OnboardingPartyDto> getOnboardingPartyList() {
        return onboardingPartyList;
    }

    public void setOnboardingPartyList(List<OnboardingPartyDto> onboardingPartyList) {
        this.onboardingPartyList = onboardingPartyList;
    }

   

	

}
