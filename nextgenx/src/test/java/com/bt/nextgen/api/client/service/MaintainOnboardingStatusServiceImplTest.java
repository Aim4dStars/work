package com.bt.nextgen.api.client.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationRepository;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyDto;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.bt.nextgen.serviceops.model.MaintainOnboardingStatusModel;
import com.btfin.panorama.core.security.profile.UserProfileService;

@RunWith(MockitoJUnitRunner.class)
public class MaintainOnboardingStatusServiceImplTest {
	
	@InjectMocks
	MaintainOnboardingStatusServiceImpl maintainOnboardingStatusServiceImpl;
	
	@Mock
	OnboardingApplicationRepository onboardingApplicationRepository;
	
	@Mock
	OnboardingPartyRepository onboardingPartyRepository;
    @Mock
    private ServiceOpsAuditService serviceOpsAuditService;

    @Mock
    private UserProfileService userProfileService;
   
   @Test
   public void updateTest() {
	   OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
	   onBoardingApplication.setStatus(OnboardingApplicationStatus.active);
	   
	   MaintainOnboardingStatusModel maintainOnboardingStatusModel = new MaintainOnboardingStatusModel();
	   maintainOnboardingStatusModel.setOnBoardingApplication(onBoardingApplication);
	   when(userProfileService.getUserId()).thenReturn("201603884");
       doNothing().when(serviceOpsAuditService).createLog("userId", "update status", "message");
	   when(onboardingApplicationRepository.update(onBoardingApplication)).thenReturn(onBoardingApplication);
	   
	   MaintainOnboardingStatusModel model=maintainOnboardingStatusServiceImpl.update(maintainOnboardingStatusModel, Long.valueOf(12));
	   assertNotNull(model);
   }
	
	@Test
	public void findTest() {
		OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
		onBoardingApplication.setStatus(OnboardingApplicationStatus.active);
		when(onboardingApplicationRepository.find(OnboardingApplicationKey.valueOf(Long.valueOf(12)))).thenReturn(onBoardingApplication);
		
		List<Long> applicationIds = new ArrayList<Long>();
		applicationIds.add(Long.valueOf(12));
		
		OnboardingParty onboardingParty = new OnboardingParty(11,Long.valueOf(1),"Test");
		List<OnboardingParty> partiesList = new ArrayList<OnboardingParty>();
		partiesList.add(onboardingParty);
		when(onboardingPartyRepository.findOnboardingPartiesByApplicationIds(applicationIds)).thenReturn(partiesList);
 
   		MaintainOnboardingStatusModel maintainOnboardingStatusModel = maintainOnboardingStatusServiceImpl.find(Long.valueOf(12));
        assertNotNull(maintainOnboardingStatusModel); 
	}
	 
	
	@Test
	public void testFindIfPartyStatusNotNull() {
		OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
		onBoardingApplication.setStatus(OnboardingApplicationStatus.active);
		when(onboardingApplicationRepository.find(OnboardingApplicationKey.valueOf(Long.valueOf(12)))).thenReturn(onBoardingApplication);
		
		List<Long> applicationIds = new ArrayList<Long>();
		applicationIds.add(Long.valueOf(12));
		
		OnboardingParty onboardingParty = new OnboardingParty(11,Long.valueOf(12),"Test");
		onboardingParty.setStatus(OnboardingPartyStatus.NotificationSent);
		List<OnboardingParty> partiesList = new ArrayList<OnboardingParty>();
		partiesList.add(onboardingParty);
		
        when(onboardingPartyRepository.findOnboardingPartiesByApplicationIds(applicationIds)).thenReturn(partiesList);
        
        OnboardingPartyDto partyDto = new OnboardingPartyDto();
        partyDto.setOnBaordingId(onboardingParty.getOnboardingApplicationId());
        partyDto.setPartyId(onboardingParty.getOnboardingPartySeq());
        partyDto.setStatus(onboardingParty.getStatus().toString());
        List<OnboardingPartyDto> partyList = new ArrayList<OnboardingPartyDto>();
        partyList.add(partyDto);
        
        
        assertEquals(partyDto.getStatus(),OnboardingPartyStatus.NotificationSent.toString());
        MaintainOnboardingStatusModel maintainOnboardingStatusModel = maintainOnboardingStatusServiceImpl.find(Long.valueOf(12));
        assertNotNull(maintainOnboardingStatusModel); 
        
		
	}
	
	
	
	
	@Test 
	public void testFindIfPartyStatusNull(){
		OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
		onBoardingApplication.setStatus(OnboardingApplicationStatus.active);
		when(onboardingApplicationRepository.find(OnboardingApplicationKey.valueOf(Long.valueOf(12)))).thenReturn(onBoardingApplication);
		
		List<Long> applicationIds = new ArrayList<Long>();
		applicationIds.add(Long.valueOf(12));
		
		OnboardingParty onboardingParty = new OnboardingParty(11,Long.valueOf(12),"Test");
		List<OnboardingParty> partiesList = new ArrayList<OnboardingParty>();
		partiesList.add(onboardingParty);
		
        when(onboardingPartyRepository.findOnboardingPartiesByApplicationIds(applicationIds)).thenReturn(partiesList);
        
        OnboardingPartyDto partyDto = new OnboardingPartyDto();
        partyDto.setOnBaordingId(onboardingParty.getOnboardingApplicationId());
        partyDto.setPartyId(onboardingParty.getOnboardingPartySeq());
        
        List<OnboardingPartyDto> partyList = new ArrayList<OnboardingPartyDto>();
        partyList.add(partyDto);
        
        
        assertEquals(partyDto.getStatus(),null);
        MaintainOnboardingStatusModel maintainOnboardingStatusModel = maintainOnboardingStatusServiceImpl.find(Long.valueOf(12));
        assertNotNull(maintainOnboardingStatusModel); 
		
	}
	
	
}
