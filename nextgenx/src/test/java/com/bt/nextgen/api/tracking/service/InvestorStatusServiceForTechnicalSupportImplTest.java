package com.bt.nextgen.api.tracking.service;

import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.bt.nextgen.core.repository.OnboardingCommunicationRepositoryImpl;
import com.bt.nextgen.core.repository.OnboardingCommunicationStatus;
import com.bt.nextgen.core.repository.OnboardingParty;
import com.bt.nextgen.core.repository.OnboardingPartyDisplayStatus;
import com.bt.nextgen.core.repository.OnboardingPartyRepository;
import com.bt.nextgen.core.repository.OnboardingPartyStatus;
import com.bt.nextgen.core.repository.OnboardingStatusInterface;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvestorStatusServiceForTechnicalSupportImplTest
{

	@InjectMocks
	private InvestorStatusServiceForTechnicalSupportImpl investorStatusServiceForTechnicalSupport;

	@Mock
	private OnboardingPartyRepository partyRepository;

	@Mock
	private OnboardingCommunicationRepositoryImpl communicationRepository;

	@Mock
	private EmailStatusService emailStatusService;

	@Mock
	private DateTimeService dateTimeService;

	@Mock
	private UserProfileService userProfileService;

	@Test
	public void shouldReturnNullValuesWhenOnboardingPartyDetailsNull()
	{
		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg("gcmId");
		assertNotNull(onboardingStatusInterface);
		assertNull(onboardingStatusInterface.getStatus());
		assertNull(onboardingStatusInterface.getFailureMsg());
	}

	@Test
	public void shouldReturnStatusEOUAndFailMsgNullWhenOnboardPartyStatusEOU()
	{
		OnboardingParty party = getOnboardingParty("TEST_GCM", OnboardingPartyStatus.ExistingPanoramaOnlineUser);
		when(partyRepository.findByGCMId(eq("TEST_GCM"))).thenReturn(Arrays.asList(party));

		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg("TEST_GCM");
		assertNotNull(onboardingStatusInterface);
		assertEquals(OnboardingPartyDisplayStatus.EXISTING_PANORAMA_ONLINE_USER, onboardingStatusInterface.getStatus());
		assertNull(onboardingStatusInterface.getFailureMsg());
	}


	@Test
	public void shouldReturnStatusNotificationSentToExistingOnlineUserAndFailMsgNullForExistingOnlineUser()
	{
		OnboardingParty party = getOnboardingParty("TEST_GCM", OnboardingPartyStatus.NotificationSentToExistingOnlineUser);
		when(partyRepository.findByGCMId(eq("TEST_GCM"))).thenReturn(Arrays.asList(party));

		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg("TEST_GCM");
		assertNotNull(onboardingStatusInterface);
		assertEquals(OnboardingPartyDisplayStatus.NOTIFICATION_SENT_TO_EXISTING_ONLINE_USER, onboardingStatusInterface.getStatus());
		assertNull(onboardingStatusInterface.getFailureMsg());
	}

	@Test
	public void shouldReturnStatusAndFailMsgWhenOnboardPartyStatusFailed()
	{
		OnboardingParty party = getOnboardingParty("TEST_GCM", OnboardingPartyStatus.DeviceRegistrationFailed);
		party.setFailureMessage("Not able to Register the device");
		when(partyRepository.findByGCMId(eq("TEST_GCM"))).thenReturn(Arrays.asList(party));

		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg("TEST_GCM");
		assertNotNull(onboardingStatusInterface);
		assertEquals(OnboardingPartyDisplayStatus.DEVICE_REGISTRATION_FAILED, onboardingStatusInterface.getStatus());
		assertEquals(party.getFailureMessage(), onboardingStatusInterface.getFailureMsg());
	}

	@Test
	public void shouldReturnStatusNSAndFailMsgNullWhenOnboardPartyStatusNS()
	{
		String gcmId = "TEST_GCM";
		OnboardingParty party = getOnboardingParty(gcmId, OnboardingPartyStatus.NotificationSent);
		when(partyRepository.findByGCMId(eq(gcmId))).thenReturn(Arrays.asList(party));
		when(emailStatusService.isCommunicationSuccessfulForTheParty(eq(gcmId))).thenReturn(true);

		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg(gcmId);
		assertNotNull(onboardingStatusInterface);
		assertEquals(OnboardingPartyDisplayStatus.NOTIFICATION_SENT, onboardingStatusInterface.getStatus());
		assertNull(onboardingStatusInterface.getFailureMsg());
	}

	@Test
	public void shouldSetFailedReasonAsErrorWhenPartyStatusIsNSAndCommunicationStatusIsError()
	{
		String gcmId = "TEST_GCM";
		OnboardingParty party = getOnboardingParty(gcmId, OnboardingPartyStatus.NotificationSent);
		when(partyRepository.findByGCMId(eq(gcmId))).thenReturn(Arrays.asList(party));
		when(emailStatusService.isCommunicationSuccessfulForTheParty(eq(gcmId))).thenReturn(false);

		OnboardingCommunication communication = getOnboardingCommunication(gcmId, new Date(), "TEST_COMM_ID_2", "Test2@email.com", OnboardingCommunicationStatus.ERROR);
		when(communicationRepository.findCommunicationsByGcmId(eq(gcmId))).thenReturn(Arrays.asList(communication));

		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg(gcmId);
		assertNotNull(onboardingStatusInterface);
		assertEquals(OnboardingPartyDisplayStatus.FAILED_EMAIL, onboardingStatusInterface.getStatus());
		assertEquals(OnboardingCommunicationStatus.ERROR, onboardingStatusInterface.getFailureMsg());
	}

	@Test
	public void shouldSetFailedReasonAsHardBounceWhenPartyStatusIsNSAndCommunicationStatusIsHardBounce()
	{
		String gcmId = "TEST_GCM";
		OnboardingParty party = getOnboardingParty(gcmId, OnboardingPartyStatus.NotificationSent);
		when(partyRepository.findByGCMId(eq(gcmId))).thenReturn(Arrays.asList(party));
		when(emailStatusService.isCommunicationSuccessfulForTheParty(eq(gcmId))).thenReturn(false);

		OnboardingCommunication communication = getOnboardingCommunication(gcmId, new Date(), "TEST_COMM_ID_2", "Test2@email.com", OnboardingCommunicationStatus.HARD_BOUNCE);
		when(communicationRepository.findCommunicationsByGcmId(eq(gcmId))).thenReturn(Arrays.asList(communication));

		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg(gcmId);
		assertNotNull(onboardingStatusInterface);
		assertEquals(OnboardingPartyDisplayStatus.FAILED_EMAIL, onboardingStatusInterface.getStatus());
		assertEquals(OnboardingCommunicationStatus.HARD_BOUNCE, onboardingStatusInterface.getFailureMsg());
	}

	@Test
	public void shouldSetFailedReasonAsFormatErrWhenPartyStatusIsNSAndCommunicationStatusIsFormatErr()
	{
		String gcmId = "TEST_GCM";
		OnboardingParty party = getOnboardingParty(gcmId, OnboardingPartyStatus.NotificationSent);
		when(partyRepository.findByGCMId(eq(gcmId))).thenReturn(Arrays.asList(party));
		when(emailStatusService.isCommunicationSuccessfulForTheParty(eq(gcmId))).thenReturn(false);

		OnboardingCommunication communication = getOnboardingCommunication(gcmId, new Date(), "TEST_COMM_ID_2", "Test2@email.com", OnboardingCommunicationStatus.FORMAT_ERROR);
		when(communicationRepository.findCommunicationsByGcmId(eq(gcmId))).thenReturn(Arrays.asList(communication));

		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg(gcmId);
		assertNotNull(onboardingStatusInterface);
		assertEquals(OnboardingPartyDisplayStatus.FAILED_EMAIL, onboardingStatusInterface.getStatus());
		assertEquals(OnboardingCommunicationStatus.FORMAT_ERROR, onboardingStatusInterface.getFailureMsg());
	}

	@Test
	public void shouldSetFailedReasonAsPerLastModifiedDateWhenPartyStatusIsNSAndCommunicationStatusFails()
	{
		String gcmId = "TEST_GCM";
		OnboardingParty party = getOnboardingParty(gcmId, OnboardingPartyStatus.NotificationSent);
		when(partyRepository.findByGCMId(eq(gcmId))).thenReturn(Arrays.asList(party));
		when(emailStatusService.isCommunicationSuccessfulForTheParty(eq(gcmId))).thenReturn(false);

		OnboardingCommunication communication1 = getOnboardingCommunication(gcmId, new Date(), "TEST_COMM_ID_1", "Test1@email.com", OnboardingCommunicationStatus.INITIATED);
		communication1.setLastModifiedDate(new Date("16 Feb 2014"));

		OnboardingCommunication communication2 = getOnboardingCommunication(gcmId, new Date(), "TEST_COMM_ID_2", "Test2@email.com", OnboardingCommunicationStatus.FORMAT_ERROR);
		communication2.setLastModifiedDate(new Date("16 March 2014"));
		when(communicationRepository.findCommunicationsByGcmId(eq(gcmId))).thenReturn(Arrays.asList(communication1,communication2));

		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg(gcmId);
		assertNotNull(onboardingStatusInterface);
		assertEquals(OnboardingPartyDisplayStatus.FAILED_EMAIL, onboardingStatusInterface.getStatus());
		assertEquals(OnboardingCommunicationStatus.FORMAT_ERROR, onboardingStatusInterface.getFailureMsg());
	}

	@Test
	public void statusShouldUpdateWhenRegistrationRegoSuccess()
	{
		final String gcmId = "TEST_GCM";
		OnboardingParty party = getOnboardingParty(gcmId, OnboardingPartyStatus.DeviceRegistrationFailed);
		party.setFailureMessage("Not able to Register the device");
		when(dateTimeService.getCurrentDateTime()).thenReturn(new DateTime());
		when(userProfileService.getGcmId()).thenReturn(gcmId);
		investorStatusServiceForTechnicalSupport.updatePartyStatusWhenResendRegistrationCodeSuccess(party);
		verify(partyRepository).update(any(OnboardingParty.class));
	}

	@Test
	public void shouldReturnStatusCreationPending_whenOnboardPartyStatusNull()
	{
		final String gcmId = "TEST_GCM";
		OnboardingParty party = new OnboardingParty(1, 2L, gcmId);
		partyRepository.save(party);
		when(partyRepository.findByGCMId(eq(gcmId))).thenReturn(Arrays.asList(party));

		OnboardingStatusInterface onboardingStatusInterface = investorStatusServiceForTechnicalSupport.getOnboardingStatusAndFailureMsg(gcmId);
		assertNotNull(onboardingStatusInterface);
		assertEquals(OnboardingPartyDisplayStatus.CUSTOMER_CREATION_PENDING, onboardingStatusInterface.getStatus());
		assertNull(onboardingStatusInterface.getFailureMsg());
	}

	private OnboardingCommunication getOnboardingCommunication(String gcmId, Date currentTime, String communicationId, String emailAddress, String status) {
		OnboardingCommunication communication1 = new OnboardingCommunication();
		communication1.setCommunicationId(communicationId);
		communication1.setOnboardingApplicationId(2l);
		communication1.setGcmPan(gcmId);
		communication1.setEmailAddress(emailAddress);
		communication1.setStatus(status);
		communication1.setCommunicationInitiationTime(currentTime);
		communication1.setCreatedDate(currentTime);
		return communication1;
	}

	private OnboardingParty getOnboardingParty(String test_gcm, OnboardingPartyStatus status) {
		OnboardingParty party = new OnboardingParty(1, 2L, test_gcm);
		party.setStatus(status);
		return party;
	}

}
