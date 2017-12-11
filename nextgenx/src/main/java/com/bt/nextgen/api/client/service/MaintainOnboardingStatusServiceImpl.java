package com.bt.nextgen.api.client.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingAccountRepository;
import com.bt.nextgen.core.repository.OnboardingApplicationRepository;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyDto;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.serviceops.model.MaintainOnboardingStatusModel;
import com.btfin.panorama.core.security.profile.UserProfileService;

@Service("maintainOnboardingStatusService")
public class MaintainOnboardingStatusServiceImpl implements MaintainOnboardingStatusService {

	private static final Logger logger = LoggerFactory.getLogger(MaintainOnboardingStatusServiceImpl.class);
	
	private static final String ACTION = "UPDATE_ONB_APP_STATUS";
	 private static final String ONB_APP_ID = "ONB_APP_ID= ";
	 private static final String STATUS_FROM = "STATUS_FROM= ";
	 private static final String STATUS_TO = " STATUS_TO= ";
	
	@Autowired
    private UserProfileService userProfileService;
	
	@Autowired
	private OnboardingApplicationRepository onboardingApplicationRepository;
	@Autowired
	private OnboardingPartyRepository onboardingPartyRepository;
	@Autowired
    private ServiceOpsAuditService serviceOpsAuditService;
	@Override
	public MaintainOnboardingStatusModel update(MaintainOnboardingStatusModel reqModel , Long appId) {
	    logger.info("Updating Onboarding Application Status");
	    OnBoardingApplication onBoardingApplication = onboardingApplicationRepository.update(reqModel.getOnBoardingApplication());
	    if(null !=onBoardingApplication){
	    serviceOpsAuditService.createLog(userProfileService.getUserId(), ACTION, ONB_APP_ID + appId
	            +STATUS_FROM + reqModel.getOnBoardingApplication().getStatus().toString() + STATUS_TO+ onBoardingApplication.getStatus().toString());
	    }
	    reqModel.setOnBoardingApplication(onBoardingApplication);
	    return reqModel;
	    }
    @Override
    public MaintainOnboardingStatusModel find(Long appId) {
        logger.info("Retriving Onboarding Application Status");
        // TODO Auto-generated method stub
        MaintainOnboardingStatusModel maintainOnboardingStatusModel = new MaintainOnboardingStatusModel();
        List<Long> applicationIds = new ArrayList<Long>();
        applicationIds.add(appId);
        OnBoardingApplication onBoardingApplication = onboardingApplicationRepository.find(OnboardingApplicationKey.valueOf(appId));
        if(null != onBoardingApplication && null != onBoardingApplication.getStatus())
        {
            maintainOnboardingStatusModel.setApplicationStatus(onBoardingApplication.getStatus().toString());
        }
        List<OnboardingParty> onboardingPartyList = onboardingPartyRepository.findOnboardingPartiesByApplicationIds(applicationIds);
        List<OnboardingPartyDto> partyList = new ArrayList<OnboardingPartyDto>();
        for (OnboardingParty party : onboardingPartyList) {
            OnboardingPartyDto partyDto = new OnboardingPartyDto();
            partyDto.setOnBaordingId(party.getOnboardingApplicationId());
            partyDto.setPartyId(party.getOnboardingPartySeq());
            partyDto.setGcmPan(party.getGcmPan());
            if(null != party.getStatus() ){
            partyDto.setStatus(party.getStatus().toString());
            }
            partyList.add(partyDto);
        }
        maintainOnboardingStatusModel.setOnBoardingApplication(onBoardingApplication);
        maintainOnboardingStatusModel.setOnboardingPartyList(partyList);
        return maintainOnboardingStatusModel;
    }
}
