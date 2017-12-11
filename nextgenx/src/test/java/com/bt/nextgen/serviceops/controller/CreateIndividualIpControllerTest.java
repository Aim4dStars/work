package com.bt.nextgen.serviceops.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;

import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.PriorityLevel;

import com.bt.nextgen.api.client.service.CreateIndividualIPDtoService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.serviceops.model.CreateIndividualIPEmailPhoneContactMethodsReqModel;
import com.bt.nextgen.serviceops.model.CreateIndividualIPReqModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class CreateIndividualIpControllerTest {

	@InjectMocks
	private CreateIndividualIpController createIndividualIpController;

	@Mock
	private CreateIndividualIPDtoService createIndividualIPDtoService;

	@Mock
	ServiceOpsService serviceOpsService;
	@Mock
	BindingResult bindingResult;

	@Test
	public void testCreateIndividualIP() {

		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		reqModel.setFirstName("abcd");
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneContactMethodsReqModel = new CreateIndividualIPEmailPhoneContactMethodsReqModel();
		setEmailAndPhoneContactMethod(emailPhoneContactMethodsReqModel);
		when(createIndividualIPDtoService.create(reqModel, emailPhoneContactMethodsReqModel, serviceErrors)).thenReturn(customerRawData);
		ModelAndView modelAndView = createIndividualIpController.createIndividualIP(reqModel,emailPhoneContactMethodsReqModel, bindingResult);
		assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
		assertThat(modelAndView.getModel(), notNullValue());
	}
	
	@Test
	public void testCreateIndividualIPWithoutEmailPhoneReqModel() {

		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		reqModel.setFirstName("abcd");
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(true);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(createIndividualIPDtoService.create(reqModel,null , serviceErrors)).thenReturn(customerRawData);
		ModelAndView modelAndView = createIndividualIpController.createIndividualIP(reqModel, bindingResult);
		assertThat(modelAndView.getViewName(), is(View.GCM_SERVICEOPS_RESPONSE));
		assertThat(modelAndView.getModel(), notNullValue());
	}

	@Test
	public void testCreateIndividualIPWithErrorView() {

		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(createIndividualIPDtoService.create(reqModel, null, serviceErrors)).thenReturn(customerRawData);
		ModelAndView modelAndView = createIndividualIpController.createIndividualIP(reqModel,null, bindingResult);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
	}
	
	@Test
	public void testCreateIndividualIPWithoutEmailAndPhoneReqModelWithErrorView() {

		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(createIndividualIPDtoService.create(reqModel, null, serviceErrors)).thenReturn(customerRawData);
		ModelAndView modelAndView = createIndividualIpController.createIndividualIP(reqModel, bindingResult);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
	}

	@Test
	public void testCreateIndividualIPWithBindingresultError() {

		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneContactMethodsReqModel = new CreateIndividualIPEmailPhoneContactMethodsReqModel();
		setEmailAndPhoneContactMethod(emailPhoneContactMethodsReqModel);
		when(createIndividualIPDtoService.create(reqModel, emailPhoneContactMethodsReqModel, serviceErrors)).thenReturn(customerRawData);
		when(bindingResult.hasErrors()).thenReturn(true);
		ModelAndView modelAndView = createIndividualIpController.createIndividualIP(reqModel,emailPhoneContactMethodsReqModel, bindingResult);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
	}
	
	@Test
	public void testCreateIndividualIPWithoutEmailAndPhoneReqModelWithBindingresultError() {

		CreateIndividualIPReqModel reqModel = new CreateIndividualIPReqModel();
		Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		CustomerRawData customerRawData = mock(CustomerRawDataImpl.class);
		when(createIndividualIPDtoService.create(reqModel, null, serviceErrors)).thenReturn(customerRawData);
		when(bindingResult.hasErrors()).thenReturn(true);
		ModelAndView modelAndView = createIndividualIpController.createIndividualIP(reqModel, bindingResult);
		assertThat(modelAndView.getViewName(), is(View.ERROR));
	}

	@Test
	public void testCreateIndividualIPReq() {

		when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(true);
		String modelAndView = createIndividualIpController.createIndividualIPReq();
		assertThat(modelAndView, is(View.CREATE_INDIVIDUAL_IP));
	}

	@Test
	public void testCreateIndividualIPReqError() {

		when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		String modelAndView = createIndividualIpController.createIndividualIPReq();
		assertThat(modelAndView, is(View.ERROR));
	}

	@Test
	public void testCreateIndividualIPReqBinder() {

		when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(false);
		WebDataBinder binder = Mockito.mock(WebDataBinder.class);
		createIndividualIpController.createIndividualIPBinder(binder);
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