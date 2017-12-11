package com.bt.nextgen.api.draftaccount.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoMapImpl;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.model.ServiceOpsClientApplicationDto;
import com.bt.nextgen.api.tracking.model.PersonInfo;
import com.bt.nextgen.api.tracking.model.TrackingDto;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationStatus;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.serviceops.service.ServiceOpsClientApplicationStatus;
import com.bt.nextgen.test.MockAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class ServiceOpsClientApplicationDtoConverterServiceTest extends MockAuthentication
{
	@Mock
	private ClientApplicationDtoConverterService clientApplicationDtoConverterService;
	@InjectMocks
	private ServiceOpsClientApplicationDtoConverterService service = new ServiceOpsClientApplicationDtoConverterService();
	ClientApplication clientApplication = null;

	@Before
	public void setup() throws Exception
	{
		clientApplication = new ClientApplication();
		Field field = ClientApplication.class.getDeclaredField("id");
		field.setAccessible(true);
		field.set(clientApplication, 10001L);
		
		clientApplication.setFormData("{\"accountType\":\"individual\",\"investors\":[{\"displayTaxOptions\":true,\"cismandatory\":true,\"fullName\":\"Mr Test Submit\",\"title\":\"mr\",\"firstname\":\"Test\",\"middlename\":\"\",\"lastname\":\"Submit\",\"preferredname\":\"\",\"dateofbirth\":\"01/01/1990\",\"gender\":\"male\"}] ,\"accountsettings\":{\"professionalspayment\":\"linkedaccountsonly\",\"investorAccountSettings\":[{\"paymentSetting\":\"allpayments\",\"hasRoles\":false,\"hasApprovers\":false}],\"primarycontact\":\"0\",\"adviserName\":\"AdviserThreeFN Adviser-ThreeLN\",\"adviserLocation\":\"New South Wales\"}}");
		
		OnBoardingApplication onboardingApplication = new OnBoardingApplication();
		onboardingApplication.setFailureMessage("Server failure");
		onboardingApplication.setStatus(OnboardingApplicationStatus.ApplicationCreationInProgress);
		clientApplication.setOnboardingApplication(onboardingApplication);
		clientApplication.setLastModifiedId("1100022");
		clientApplication.setAdviserPositionId("2222222");
	
		ClientApplicationDto clientApplicationDto = new ClientApplicationDtoMapImpl();
		clientApplicationDto.setLastModified(new DateTime());
		clientApplicationDto.setLastModifiedByName("Doe, John");
		clientApplicationDto.setAdviserId("2222222");
		clientApplicationDto.setAdviserName("whayne, Bruce");
		clientApplicationDto.setReferenceNumber("R001000101");
		clientApplicationDto.setKey(new ClientApplicationKey(10001L));
		clientApplicationDto.setProductName("Test product");

		when(clientApplicationDtoConverterService.convertToDto(any(ClientApplication.class), any(ServiceErrors.class))).thenReturn(clientApplicationDto);
	}

	@Test
	public void testConvertToDto()
	{
		ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = service.convertToDto(clientApplication, new ServiceErrorsImpl());
		assertThat(serviceOpsClientApplicationDto.getAccountType(), Is.is("individual"));
		assertThat(serviceOpsClientApplicationDto.getAccountName(),Is.is("Test Submit"));
		assertThat(serviceOpsClientApplicationDto.getAdviserName(),Is.is("whayne, Bruce"));
		assertThat(serviceOpsClientApplicationDto.getFailureMessage(),Is.is("ApplicationCreationInProgress : Server failure"));
		assertThat(serviceOpsClientApplicationDto.getLastModifiedByName(),Is.is("Doe, John"));
		assertThat(serviceOpsClientApplicationDto.getProductName(), Is.is("Test product"));
		assertThat(serviceOpsClientApplicationDto.getReferenceNumber(),Is.is("R001000101"));
	}

	@Test
	public void testFailureMessageContainsOnlyTheOnboardingStatus()
	{
		clientApplication.getOnboardingApplication().setFailureMessage(null);

		ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = service.convertToDto(clientApplication, new ServiceErrorsImpl());
		assertThat(serviceOpsClientApplicationDto.getFailureMessage(),Is.is("ApplicationCreationInProgress"));
	}

	@Test
	public void convertToDtoFromTrackingDto() {
		TrackingDto trackingDto = mock(TrackingDto.class);
		PersonInfo personInfo = new PersonInfo("FirstName", "LastName", "personId");
		PersonInfo lastModifiedBy = new PersonInfo("FirstName", "");
		when(trackingDto.getAdviser()).thenReturn(personInfo);
		when(trackingDto.getLastModifiedBy()).thenReturn(lastModifiedBy);
		when(trackingDto.getStatus()).thenReturn(OnboardingApplicationStatus.active); // anything
		when(trackingDto.getAccountType()).thenReturn("individual");
		when(trackingDto.getProductName()).thenReturn("productName");
		when(trackingDto.getDisplayName()).thenReturn("AccountName");
		when(trackingDto.getReferenceNumber()).thenReturn("R001000101");
		when(trackingDto.getAccountId()).thenReturn("111111111");
		when(trackingDto.getClientApplicationId()).thenReturn(new ClientApplicationKey(100l));
		ServiceOpsClientApplicationDto serviceOpsClientApplicationDto = service.convertToDto(trackingDto, ServiceOpsClientApplicationStatus.APPROVED);

		assertThat(serviceOpsClientApplicationDto.getAccountType(), Is.is("individual"));
		assertThat(serviceOpsClientApplicationDto.getAccountName(), Is.is("AccountName"));
		assertThat(serviceOpsClientApplicationDto.getAdviserName(), Is.is("LastName, FirstName"));
		assertThat(serviceOpsClientApplicationDto.getStatus(), Is.is(ServiceOpsClientApplicationStatus.APPROVED));
		assertThat(serviceOpsClientApplicationDto.getLastModifiedByName(), Is.is("FirstName"));
		assertThat(serviceOpsClientApplicationDto.getProductName(), Is.is("productName"));
		assertThat(serviceOpsClientApplicationDto.getReferenceNumber(), Is.is("R001000101"));
		assertThat(serviceOpsClientApplicationDto.getAccountNumber(), is("111111111"));
		assertThat(serviceOpsClientApplicationDto.getKey(), is(trackingDto.getClientApplicationId()));
	}

}
