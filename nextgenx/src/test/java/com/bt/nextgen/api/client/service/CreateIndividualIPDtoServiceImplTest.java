package com.bt.nextgen.api.client.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.PriorityLevel;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.createindividualip.v5.CreateIndividualIPIntegrationService;
import com.bt.nextgen.service.gesb.createindividualip.v5.CreateIndvIPRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.serviceops.model.CreateIndividualIPEmailPhoneContactMethodsReqModel;
import com.bt.nextgen.serviceops.model.CreateIndividualIPReqModel;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(MockitoJUnitRunner.class)
public class CreateIndividualIPDtoServiceImplTest {

	@InjectMocks
	private CreateIndividualIPDtoServiceImpl createIndividualIPDtoServiceImpl;

	@Mock
	private CreateIndividualIPIntegrationService integrationService;

	@Test
	public void testCreate() throws JsonProcessingException {

		CustomerRawData customerRawData = new CustomerRawDataImpl("aaadf");
		when(integrationService.create(any(CreateIndvIPRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);
		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		reqModel.setAlternateName("Alternate Name");
		reqModel.setIsPreferred("Y");
		reqModel.setAddressType("StandardPostalAddress");
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = new CreateIndividualIPEmailPhoneContactMethodsReqModel();
		setEmailAndPhoneContactMethod(emailPhoneReqModel);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		customerRawData = createIndividualIPDtoServiceImpl.create(reqModel, emailPhoneReqModel, serviceErrors);
		assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"aaadf\""));
	}

	@Test
	public void testCreateWithoutEmailPhoneContactMethod() throws JsonProcessingException {

		CustomerRawData customerRawData = new CustomerRawDataImpl("aaadf");
		when(integrationService.create(any(CreateIndvIPRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);
		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		reqModel.setAlternateName("Alternate Name");
		reqModel.setIsPreferred("Y");
		reqModel.setAddressType("StandardPostalAddress");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		customerRawData = createIndividualIPDtoServiceImpl.create(reqModel, null, serviceErrors);
		assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"aaadf\""));
	}

	@Test
	public void testCreateNoAlternateName() throws JsonProcessingException {

		CustomerRawData customerRawData = new CustomerRawDataImpl("aaadf");
		when(integrationService.create(any(CreateIndvIPRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);
		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		reqModel.setAddressType("NonStandardPostalAddress");
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = new CreateIndividualIPEmailPhoneContactMethodsReqModel();
		setEmailAndPhoneContactMethod(emailPhoneReqModel);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		customerRawData = createIndividualIPDtoServiceImpl.create(reqModel, emailPhoneReqModel, serviceErrors);
		assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"aaadf\""));
	}
	
	@Test
	public void testCreateNoPriorityLevelAndIdentificationScheme() throws JsonProcessingException {

		CustomerRawData customerRawData = new CustomerRawDataImpl("aaadf");
		when(integrationService.create(any(CreateIndvIPRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);
		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		reqModel.setAddressType("NonStandardPostalAddress");
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = new CreateIndividualIPEmailPhoneContactMethodsReqModel();
		setEmailAndPhoneContactMethod(emailPhoneReqModel);
		emailPhoneReqModel.setPhoneAddressContactPriorityLevel("");
		emailPhoneReqModel.setPhoneAddressContactIdentificationScheme("");
		
		emailPhoneReqModel.setEmailAddressContactPriorityLevel("");
		emailPhoneReqModel.setEmailAddressContactIdentificationScheme("");
		
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		customerRawData = createIndividualIPDtoServiceImpl.create(reqModel, emailPhoneReqModel, serviceErrors);
		assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"aaadf\""));
	}
	
	@Test
	public void testCreateNullPriorityLevelAndIdentificationScheme() throws JsonProcessingException {

		CustomerRawData customerRawData = new CustomerRawDataImpl("aaadf");
		when(integrationService.create(any(CreateIndvIPRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);
		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		reqModel.setAddressType("NonStandardPostalAddress");
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = new CreateIndividualIPEmailPhoneContactMethodsReqModel();
		setEmailAndPhoneContactMethod(emailPhoneReqModel);
		emailPhoneReqModel.setPhoneAddressContactPriorityLevel(null);
		emailPhoneReqModel.setPhoneAddressContactIdentificationScheme(null);
		
		emailPhoneReqModel.setEmailAddressContactPriorityLevel(null);
		emailPhoneReqModel.setEmailAddressContactIdentificationScheme(null);
		
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		customerRawData = createIndividualIPDtoServiceImpl.create(reqModel, emailPhoneReqModel, serviceErrors);
		assertThat(customerRawData.getRawResponse(), CoreMatchers.equalTo("\"aaadf\""));
	}

	private void setEmailAndPhoneContactMethod(CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel) {
		emailPhoneReqModel.setPhoneAddressContactAddressee("full name");
		emailPhoneReqModel.setPhoneAddressContactAreaCode("Area code");
		emailPhoneReqModel.setPhoneAddressContactContactInstructions("instructions");
		emailPhoneReqModel.setPhoneAddressContactContactMedium("medium");
		emailPhoneReqModel.setPhoneAddressContactContactMethodId("contactmethod id");
		emailPhoneReqModel.setPhoneAddressContactCountryCode("country code");
		emailPhoneReqModel.setPhoneAddressContactLocalNumber("local number");
		// createIndividualIPRequest.setPhoneAddressContactEndDate(phoneAddressContactMethod.getEndDate());
		emailPhoneReqModel.setPhoneAddressContactFullTelephoneNumber("telephone number");
		emailPhoneReqModel.setPhoneAddressContactIdentificationScheme(IdentificationScheme.CIS_KEY.name());
		emailPhoneReqModel.setPhoneAddressContactPreferredContactTime("preferred time");
		emailPhoneReqModel.setPhoneAddressContactPriorityLevel(PriorityLevel.PRIMARY.name());
		emailPhoneReqModel.setPhoneAddressContactSourceSystem("Source System");
		// createIndividualIPRequest.setPhoneAddressContactStartDate(phoneAddressContactMethod.getStartDate());
		emailPhoneReqModel.setHasPhoneContactMethodUsage("USAge");
		emailPhoneReqModel.setPhoneAddressContactValidityStatus("C");

		emailPhoneReqModel.setEmailAddressContactAddressee("Addressee");
		emailPhoneReqModel.setEmailAddressContactContactMethodId("Contact method id");
		emailPhoneReqModel.setEmailAddressContactEmailAddress("email@email.com");
		// createIndividualIPRequest.setEmailAddressContactEndDate(phoneAddressContactMethod.getEndDate());
		emailPhoneReqModel.setEmailAddressContactIdentificationScheme(IdentificationScheme.CIS_KEY.name());
		emailPhoneReqModel.setHasEmailContactMethodUsage("usage");
		emailPhoneReqModel.setEmailAddressContactPreferredContactTime("contact method");
		emailPhoneReqModel.setEmailAddressContactPriorityLevel(PriorityLevel.PRIMARY.name());
		emailPhoneReqModel.setEmailAddressContactSourceSystem("Source System");
		// createIndividualIPRequest.setEmailAddressContactStartDate(phoneAddressContactMethod.getStartDate());
		emailPhoneReqModel.setEmailAddressContactValidityStatus("C");

	}

}