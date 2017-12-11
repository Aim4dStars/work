/**
 * 
 */
package com.bt.nextgen.api.client.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.ContactMethodUsage;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.maintainidvdetail.v5.MaintainIdvDetailIntegrationService;
import com.bt.nextgen.service.gesb.maintainidvdetail.v5.MaintainIdvRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author L081050
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class MaintainIdvDetailDtoServiceImplTest {
	@InjectMocks
	private MaintainIdvDetailDtoServiceImpl maintainIdvDetailDtoServiceImpl;

	@Mock
	private CustomerRawData customerRawData;

	@Mock
	private MaintainIdvRequest maintainIdvRequest;

	@Mock
	private MaintainIdvDetailIntegrationService maintainIdvDetailIntegrationService;

	@Test
	public void testMaintainIndiVidual() throws JsonProcessingException {
		MaintainIdvDetailReqModel req = new MaintainIdvDetailReqModel();
		req.setAddressline1("pune");
		req.setCisKey("12345678901");
		req.setAgentName("xyz");
		req.setAgentCisKey("12345678901");
		req.setCity("dfgfgfhdfh");
		req.setCountry("rttyyeyuu");
		req.setDocumentType("Passport");
		req.setEmployerName("asdfghjjk");
		req.setFirstName("assddghhf");
		req.setLastName("gdgfhfjjfj");
		req.setPersonType("Individual");
		req.setPincode("123456");
		req.setSilo("WP");
		req.setState("NSW");
		req.setUsage(ContactMethodUsage.MAIN_BUSINESS_PREMISES.value());
		req.setOptAttrName("orgIdvDocRefNum,orgIdvDocIssuer");
		req.setOptAttrVal("sffggdd,sdfdghff");
		req.setIdvType("Individual");
		req.setRequestedAction("update");
		req.setAddressline2("asdd,asd");
		req.setExtIdvDate("20 Oct 1987");
		req.setCity("weer");
		req.setPostalAddressType("sffgghdf");
		req.setInvolvedPartyNameType("RegisteredName");
		req.setRegistrationNumberType("ACN");
		req.setIsSoleTrader("Y");
		req.setDateOfBirth("20 Oct 1987");
		req.setIparId("iparId");
		req.setIparCisKey("IparCisKey");
		req.setIparType("IparType");
		req.setIparCountry("IparCountry");
		req.setIparPostCode("IparPostCode");
		req.setIparState("IparState");
		req.setIparCity("IparCity");
		req.setIparAddressLine1("IparAddressLine1");
		req.setIsIssuedAtState("IsIssuedAtState");
		req.setIsIssuedAtCountry("IsIssuedAtCountry");
		req.setIsRegulatedBy("IsRegulatedBy");
		req.setIsOtherName("IsOtherName");
		req.setHfcpFullName("HfcpFullName");
		req.setRegistrationNumber("1234567");
		req.setCustomerNumber("1234567");
		req.setEvBusinessEntityName("Test Name");
		req.setEvRecepientEmail1("test@test.com");
		req.setEvRecepientEmail2("test@test.com");
		req.setMiddleName("Middle Name");
		req.setFullName("Full Name");

		ServiceErrors serviceError = new ServiceErrorsImpl();
		when(maintainIdvDetailIntegrationService.maintain(any(MaintainIdvRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);

		CustomerRawData customerRawData = maintainIdvDetailDtoServiceImpl.maintain(req, serviceError);
		assertNotNull(customerRawData);
		Assert.assertTrue(req.getIsOtherName().equals("IsOtherName"));
		Assert.assertTrue(req.getEvBusinessEntityName().equals("Test Name"));
		Assert.assertTrue(req.getEvRecepientEmail1().equals("test@test.com"));
		Assert.assertTrue(req.getEvRecepientEmail2().equals("test@test.com"));
		Assert.assertTrue(req.getSilo().equals("WP"));
	}
	
	@Test
	public void testMaintainIndiVidualWithNullValues() throws JsonProcessingException {
		MaintainIdvDetailReqModel req = new MaintainIdvDetailReqModel();
		req.setAddressline1(null);
		req.setCisKey(null);
		req.setAgentName(null);
		req.setAgentCisKey(null);
		req.setCity(null);
		req.setCountry(null);
		req.setDocumentType(null);
		req.setEmployerName(null);
		req.setFirstName(null);
		req.setLastName(null);
		req.setPersonType(null);
		req.setPincode(null);
		req.setSilo(null);
		req.setState(null);
		req.setUsage(null);
		req.setOptAttrName(null);
		req.setOptAttrVal(null);
		req.setIdvType(null);
		req.setRequestedAction(null);
		req.setAddressline2(null);
		req.setExtIdvDate(null);
		req.setCity(null);
		req.setPostalAddressType(null);
		req.setInvolvedPartyNameType(null);
		req.setRegistrationNumberType(null);
		req.setIsSoleTrader(null);
		req.setDateOfBirth(null);
		req.setIparId(null);
		req.setIparCisKey(null);
		req.setIparType(null);
		req.setIparCountry(null);
		req.setIparPostCode(null);
		req.setIparState(null);
		req.setIparCity(null);
		req.setIparAddressLine1(null);
		req.setIsIssuedAtState(null);
		req.setIsIssuedAtCountry(null);
		req.setIsRegulatedBy(null);
		req.setIsOtherName(null);
		req.setHfcpFullName(null);
		req.setRegistrationNumber(null);
		req.setCustomerNumber(null);
		req.setEvBusinessEntityName(null);
		req.setEvRecepientEmail1(null);
		req.setEvRecepientEmail2(null);
		req.setMiddleName(null);
		req.setFullName(null);

		ServiceErrors serviceError = new ServiceErrorsImpl();
		when(maintainIdvDetailIntegrationService.maintain(any(MaintainIdvRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);

		CustomerRawData customerRawData = maintainIdvDetailDtoServiceImpl.maintain(req, serviceError);
		assertNotNull(customerRawData);
		Assert.assertTrue(req.getIsOtherName().equals(""));
		Assert.assertTrue(req.getEvBusinessEntityName().equals(""));
		Assert.assertTrue(req.getEvRecepientEmail1().equals(""));
		Assert.assertTrue(req.getEvRecepientEmail2().equals(""));
		Assert.assertTrue(req.getSilo().equals(""));
	}

	@Test
	public void testMaintainOrganisation() throws JsonProcessingException {
		MaintainIdvDetailReqModel req = new MaintainIdvDetailReqModel();
		req.setAddressline1("pune");
		req.setCisKey("12345678901");
		req.setAgentName("xyz");
		req.setAgentCisKey("12345678901");
		req.setCity("dfgfgfhdfh");
		req.setCountry("rttyyeyuu");
		req.setDocumentType("Passport");
		req.setEmployerName("asdfghjjk");
		req.setFirstName("assddghhf");
		req.setLastName("gdgfhfjjfj");
		req.setPersonType("Individual");
		req.setPincode("123456");
		req.setSilo("WP");
		req.setState("NSW");
		req.setExtIdvDate("20 Oct 1987");
		req.setUsage(ContactMethodUsage.MAIN_BUSINESS_PREMISES.value());
		req.setOptAttrName("orgIdvDocRefNum,orgIdvDocIssuer");
		req.setOptAttrVal("sffggdd,sdfdghff");
		req.setIdvType("Organisation");
		req.setRequestedAction("update");
		req.setAddressline2("asdd,asd");
		req.setCity("weer");
		req.setPostalAddressType("sffgghdf");
		req.setInvolvedPartyNameType("RegisteredName");
		req.setRegistrationNumberType("ACN");
		req.setIsSoleTrader("Y");
		req.setDateOfBirth("20 Oct 1987");
		req.setIsForeignRegistered("Y");
		ServiceErrors serviceError = new ServiceErrorsImpl();
		when(maintainIdvDetailIntegrationService.maintain(any(MaintainIdvRequest.class), any(ServiceErrors.class))).thenReturn(customerRawData);

		CustomerRawData customerRawData = maintainIdvDetailDtoServiceImpl.maintain(req, serviceError);
		assertNotNull(customerRawData);
	}
}
