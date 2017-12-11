/**
 * 
 */
package com.bt.nextgen.api.client.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.westpac.gn.common.xsd.commontypes.v3.BooleanENUM;
import au.com.westpac.gn.common.xsd.identifiers.v1.AccountArrangementIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.ContactMethodIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.EmployeeIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.common.xsd.identifiers.v1.UCMPostalAddressIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.Agent;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.ContactMethodUsage;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.Customer;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttributeName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.EmploymentDetails;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IndividualIDVAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IndividualName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.InvolvedPartyNameType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.InvolvedPartyRegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.RetrieveIDVDetailsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.ChannelAccessArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.EmailAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.EmailAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyNameAddressee;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.ObjectFactory;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PriorityLevel;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PurposeOfBusinessRelationship;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.SourceOfFunds;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.SourceOfWealth;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.StandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.TelephoneAddress;

import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.serviceops.model.CreateIndividualIPEmailPhoneContactMethodsReqModel;
import com.bt.nextgen.serviceops.model.CreateIndividualIPReqModel;
import com.bt.nextgen.serviceops.model.MaintainArrangementAndRelationshipReqModel;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;
import com.bt.nextgen.serviceops.model.RetrieveIDVDetailsReqModel;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

/**
 * @author l081050
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class SiloMovementRequestBuilderTest {
	@InjectMocks
	private SiloMovementRequestBuilder siloMovementRequestBuilder;

	CreateIndividualIPResponse res336 = new CreateIndividualIPResponse();

	@Before
	public void setUp() {
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.Individual();
		InvolvedPartyIdentifier identifier = new InvolvedPartyIdentifier();
		identifier.setInvolvedPartyId("12345678905");
		individual.getInvolvedPartyIdentifier().add(identifier);
		res336.setIndividual(individual);
	}

	@Test
	public void testGet336ReqModell() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.IndividualName individualName = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.IndividualName();
		individualName.setFirstName("First Name");
		individualName.setLastName("Last Name");
		individualName.getMiddleNames().add("middele names");
		individualName.setSuffixTitle("Mr");
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.AlternateName alternateName = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.AlternateName();
		individualName.getHasAlternateName().add(alternateName);
		individual.setHasForName(individualName);
		PurposeOfBusinessRelationship purposeOfBusinessRelationship = new PurposeOfBusinessRelationship();
		purposeOfBusinessRelationship.setValue("Doing Transaction");
		individual.getPurposeOfBusinessRelationship().add(purposeOfBusinessRelationship);
		RegistrationArrangement registrationArrangement = new RegistrationArrangement();
		RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
		registrationIdentifier.setRegistrationNumber("1234555");
		registrationArrangement.getRegistrationIdentifier().add(registrationIdentifier);
		individual.getHasForeignRegistration().add(registrationArrangement);
		SourceOfFunds sourceOfFunds = new SourceOfFunds();
		sourceOfFunds.setValue("XYZ");
		individual.getSourceOfFunds().add(sourceOfFunds);
		SourceOfWealth SourceOfWealth = new SourceOfWealth();
		SourceOfWealth.setValue("ABC");
		individual.getSourceOfWealth().add(SourceOfWealth);
		StandardPostalAddress standardPostalAddress = new StandardPostalAddress();
		standardPostalAddress.setState("NSW");
		standardPostalAddress.setStreetName("ghdffhfj");
		standardPostalAddress.setStreetNumber("135546");
		standardPostalAddress.setStreetType("asdf");
		standardPostalAddress.setPostCode("2000");
		standardPostalAddress.setCity("Sydney");
		standardPostalAddress.setCountry("AUS");
		standardPostalAddress.setFloorNumber("123");
		standardPostalAddress.setUnitNumber("123");
		standardPostalAddress.setBuildingName("TEST");
		UCMPostalAddressIdentifier uCMPostalAddressIdentifier = new UCMPostalAddressIdentifier();
		uCMPostalAddressIdentifier.setAddressKeyLine1("tryuetutu");
		uCMPostalAddressIdentifier.setAddressKeyLine2("AFGDGDYYt");
		standardPostalAddress.setInternalIdentifier(uCMPostalAddressIdentifier);
		individual
				.setEmploymentDetails(new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.EmploymentDetails());
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PostalAddressContactMethod postalAddressContactMethod = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PostalAddressContactMethod();
		postalAddressContactMethod.setHasAddress(standardPostalAddress);
		InvolvedPartyNameAddressee involvedPartyNameAddressee = new InvolvedPartyNameAddressee();
		involvedPartyNameAddressee.setFullName("Full Name");
		involvedPartyNameAddressee.getNameText().add("Addresee Name Text");
		postalAddressContactMethod.setAddressee(involvedPartyNameAddressee);
		individual.getHasPostalAddressContactMethod().add(postalAddressContactMethod);
		individual.setBirthDate(getDate(27, 07, 2017));

		EmailAddressContactMethod emailAddressContactMethod = new EmailAddressContactMethod();
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.setEmailAddress("test@test.com");
		emailAddressContactMethod.setHasAddress(emailAddress);
		InvolvedPartyNameAddressee involvedPartyNameFullName = new InvolvedPartyNameAddressee();
		involvedPartyNameFullName.setFullName("full name");
		emailAddressContactMethod.setAddressee(involvedPartyNameFullName);
		emailAddressContactMethod.setPreferredContactTime("UNK");
		emailAddressContactMethod.setPriorityLevel(PriorityLevel.PRIMARY);
		emailAddressContactMethod.setUsage("OTH");
		emailAddressContactMethod.setValidityStatus("C");
		ContactMethodIdentifier contactMethodIdentifier = new ContactMethodIdentifier();
		contactMethodIdentifier.setContactMethodId("test@test.com");
		contactMethodIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		contactMethodIdentifier.setSourceSystem("Source System");
		emailAddressContactMethod.setContactMethodIdentifier(contactMethodIdentifier);

		individual.getHasEmailAddressContactMethod().add(emailAddressContactMethod);

		PhoneAddressContactMethod phoneAddressContactMethod = new PhoneAddressContactMethod();
		TelephoneAddress telephoneAddress = new TelephoneAddress();
		telephoneAddress.setAreaCode("04");
		telephoneAddress.setCountryCode("61");
		telephoneAddress.setLocalNumber("44444444");
		phoneAddressContactMethod.setHasAddress(telephoneAddress);
		phoneAddressContactMethod.setAddressee(involvedPartyNameFullName);
		phoneAddressContactMethod.setContactInstructions("instructions");
		phoneAddressContactMethod.setContactMedium("MOBILE");
		phoneAddressContactMethod.setPreferredContactTime("UNK");
		phoneAddressContactMethod.setPriorityLevel(PriorityLevel.PRIMARY);
		phoneAddressContactMethod.setUsage("OTH");
		phoneAddressContactMethod.setValidityStatus("C");
		contactMethodIdentifier.setContactMethodId("610406234234");
		contactMethodIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		contactMethodIdentifier.setSourceSystem("Source System");
		phoneAddressContactMethod.setContactMethodIdentifier(contactMethodIdentifier);

		individual.getHasPhoneAddressContactMethod().add(phoneAddressContactMethod);
		req.setIndividual(individual);
		CreateIndividualIPReqModel createIndividualIPReqModel = siloMovementRequestBuilder.get336ReqModel(req, RoleType.INDIVIDUAL, "BTPL");
		assertNotNull(createIndividualIPReqModel);
	}

	@Test
	public void testGet336EmailAndPhoneReqModel() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();

		EmailAddressContactMethod emailAddressContactMethod = new EmailAddressContactMethod();
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.setEmailAddress("test@test.com");
		emailAddressContactMethod.setHasAddress(emailAddress);
		InvolvedPartyNameAddressee involvedPartyNameFullName = new InvolvedPartyNameAddressee();
		involvedPartyNameFullName.setFullName("full name");
		emailAddressContactMethod.setAddressee(involvedPartyNameFullName);
		emailAddressContactMethod.setPreferredContactTime("UNK");
		emailAddressContactMethod.setPriorityLevel(PriorityLevel.PRIMARY);
		emailAddressContactMethod.setUsage("OTH");
		emailAddressContactMethod.setValidityStatus("C");
		ContactMethodIdentifier contactMethodIdentifier = new ContactMethodIdentifier();
		contactMethodIdentifier.setContactMethodId("test@test.com");
		contactMethodIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		contactMethodIdentifier.setSourceSystem("Source System");
		emailAddressContactMethod.setContactMethodIdentifier(contactMethodIdentifier);

		individual.getHasEmailAddressContactMethod().add(emailAddressContactMethod);

		PhoneAddressContactMethod phoneAddressContactMethod = new PhoneAddressContactMethod();
		TelephoneAddress telephoneAddress = new TelephoneAddress();
		telephoneAddress.setAreaCode("04");
		telephoneAddress.setCountryCode("61");
		telephoneAddress.setLocalNumber("44444444");
		phoneAddressContactMethod.setHasAddress(telephoneAddress);
		phoneAddressContactMethod.setAddressee(involvedPartyNameFullName);
		phoneAddressContactMethod.setContactInstructions("instructions");
		phoneAddressContactMethod.setContactMedium("MOBILE");
		phoneAddressContactMethod.setPreferredContactTime("UNK");
		phoneAddressContactMethod.setPriorityLevel(PriorityLevel.PRIMARY);
		phoneAddressContactMethod.setUsage("OTH");
		phoneAddressContactMethod.setValidityStatus("C");
		contactMethodIdentifier.setContactMethodId("610406234234");
		contactMethodIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		contactMethodIdentifier.setSourceSystem("Source System");
		phoneAddressContactMethod.setContactMethodIdentifier(contactMethodIdentifier);

		individual.getHasPhoneAddressContactMethod().add(phoneAddressContactMethod);
		req.setIndividual(individual);
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = siloMovementRequestBuilder.get336EmailPhoneReqModel(req,
				RoleType.INDIVIDUAL, "BTPL");
		assertNotNull(emailPhoneReqModel);
	}
	
	@Test
	public void testGet336PhoneReqModel() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();

		InvolvedPartyNameAddressee involvedPartyNameFullName = new InvolvedPartyNameAddressee();
		involvedPartyNameFullName.setFullName("full name");
		ContactMethodIdentifier contactMethodIdentifier = new ContactMethodIdentifier();
		contactMethodIdentifier.setContactMethodId("test@test.com");
		contactMethodIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		contactMethodIdentifier.setSourceSystem("Source System");

		PhoneAddressContactMethod phoneAddressContactMethod = new PhoneAddressContactMethod();
		TelephoneAddress telephoneAddress = new TelephoneAddress();
		telephoneAddress.setAreaCode("04");
		telephoneAddress.setCountryCode("61");
		telephoneAddress.setLocalNumber("44444444");
		phoneAddressContactMethod.setHasAddress(telephoneAddress);
		phoneAddressContactMethod.setAddressee(involvedPartyNameFullName);
		phoneAddressContactMethod.setContactInstructions("instructions");
		phoneAddressContactMethod.setContactMedium("MOBILE");
		phoneAddressContactMethod.setPreferredContactTime("UNK");
		phoneAddressContactMethod.setPriorityLevel(PriorityLevel.PRIMARY);
		phoneAddressContactMethod.setUsage("OTH");
		phoneAddressContactMethod.setValidityStatus("C");
		contactMethodIdentifier.setContactMethodId("610406234234");
		contactMethodIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		contactMethodIdentifier.setSourceSystem("Source System");
		phoneAddressContactMethod.setContactMethodIdentifier(contactMethodIdentifier);

		individual.getHasPhoneAddressContactMethod().add(phoneAddressContactMethod);
		req.setIndividual(individual);
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = siloMovementRequestBuilder.get336EmailPhoneReqModel(req,
				RoleType.INDIVIDUAL, "BTPL");
		assertNotNull(emailPhoneReqModel);
	}

	@Test
	public void testGet336EmailAndPhoneHasAddressAndAddreseeNullReqModel() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();

		EmailAddressContactMethod emailAddressContactMethod = new EmailAddressContactMethod();
		emailAddressContactMethod.setHasAddress(null);
		emailAddressContactMethod.setAddressee(null);
		emailAddressContactMethod.setPreferredContactTime("UNK");
		emailAddressContactMethod.setPriorityLevel(null);
		emailAddressContactMethod.setUsage("OTH");
		emailAddressContactMethod.setValidityStatus("C");
		emailAddressContactMethod.setContactMethodIdentifier(null);

		individual.getHasEmailAddressContactMethod().add(emailAddressContactMethod);

		PhoneAddressContactMethod phoneAddressContactMethod = new PhoneAddressContactMethod();
		phoneAddressContactMethod.setHasAddress(null);
		phoneAddressContactMethod.setAddressee(null);
		phoneAddressContactMethod.setContactInstructions("instructions");
		phoneAddressContactMethod.setContactMedium("MOBILE");
		phoneAddressContactMethod.setPreferredContactTime("UNK");
		phoneAddressContactMethod.setPriorityLevel(null);
		phoneAddressContactMethod.setUsage("OTH");
		phoneAddressContactMethod.setValidityStatus("C");
		phoneAddressContactMethod.setContactMethodIdentifier(null);

		individual.getHasPhoneAddressContactMethod().add(phoneAddressContactMethod);
		req.setIndividual(individual);
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = siloMovementRequestBuilder.get336EmailPhoneReqModel(req,
				RoleType.INDIVIDUAL, "BTPL");
		assertNotNull(emailPhoneReqModel);
	}

	@Test
	public void testGet336EmailAndPhoneIdentificationSchemeNullReqModel() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();

		EmailAddressContactMethod emailAddressContactMethod = new EmailAddressContactMethod();
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.setEmailAddress("test@test.com");
		emailAddressContactMethod.setHasAddress(emailAddress);
		InvolvedPartyNameAddressee involvedPartyNameFullName = new InvolvedPartyNameAddressee();
		involvedPartyNameFullName.setFullName("full name");
		emailAddressContactMethod.setAddressee(involvedPartyNameFullName);
		emailAddressContactMethod.setPreferredContactTime("UNK");
		emailAddressContactMethod.setPriorityLevel(PriorityLevel.PRIMARY);
		emailAddressContactMethod.setUsage("OTH");
		emailAddressContactMethod.setValidityStatus("C");
		ContactMethodIdentifier contactMethodIdentifier = new ContactMethodIdentifier();
		contactMethodIdentifier.setContactMethodId("test@test.com");
		contactMethodIdentifier.setIdentificationScheme(null);
		contactMethodIdentifier.setSourceSystem("Source System");
		emailAddressContactMethod.setContactMethodIdentifier(contactMethodIdentifier);

		individual.getHasEmailAddressContactMethod().add(emailAddressContactMethod);

		PhoneAddressContactMethod phoneAddressContactMethod = new PhoneAddressContactMethod();
		TelephoneAddress telephoneAddress = new TelephoneAddress();
		telephoneAddress.setAreaCode("04");
		telephoneAddress.setCountryCode("61");
		telephoneAddress.setLocalNumber("44444444");
		phoneAddressContactMethod.setHasAddress(telephoneAddress);
		phoneAddressContactMethod.setAddressee(involvedPartyNameFullName);
		phoneAddressContactMethod.setContactInstructions("instructions");
		phoneAddressContactMethod.setContactMedium("MOBILE");
		phoneAddressContactMethod.setPreferredContactTime("UNK");
		phoneAddressContactMethod.setPriorityLevel(PriorityLevel.PRIMARY);
		phoneAddressContactMethod.setUsage("OTH");
		phoneAddressContactMethod.setValidityStatus("C");
		contactMethodIdentifier.setContactMethodId("610406234234");
		contactMethodIdentifier.setIdentificationScheme(null);
		contactMethodIdentifier.setSourceSystem("Source System");
		phoneAddressContactMethod.setContactMethodIdentifier(contactMethodIdentifier);

		individual.getHasPhoneAddressContactMethod().add(phoneAddressContactMethod);

		individual.getHasPhoneAddressContactMethod().add(phoneAddressContactMethod);
		req.setIndividual(individual);
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = siloMovementRequestBuilder.get336EmailPhoneReqModel(req,
				RoleType.INDIVIDUAL, "BTPL");
		assertNotNull(emailPhoneReqModel);
	}

	@Test
	public void testGet336EmailAndPhoneReqModelEmpty() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();
		req.setIndividual(individual);
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = siloMovementRequestBuilder.get336EmailPhoneReqModel(req,
				RoleType.INDIVIDUAL, "BTPL");
		assertNull(emailPhoneReqModel);
	}

	@Test
	public void testGet336EmailAndPhoneReqModelIndividualNull() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();
		req.setIndividual(null);
		CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel = siloMovementRequestBuilder.get336EmailPhoneReqModel(req,
				RoleType.INDIVIDUAL, "BTPL");
		assertNull(emailPhoneReqModel);
	}

	@Test
	public void testGet336ReqModellAllBlank() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.IndividualName individualName = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.IndividualName();
		individualName.setFirstName(null);
		individualName.setLastName(null);
		individualName.getMiddleNames().add(null);
		individualName.setSuffixTitle(null);
		individual.setHasForName(individualName);
		PurposeOfBusinessRelationship purposeOfBusinessRelationship = new PurposeOfBusinessRelationship();
		purposeOfBusinessRelationship.setValue(null);
		individual.getPurposeOfBusinessRelationship().add(purposeOfBusinessRelationship);
		RegistrationArrangement registrationArrangement = new RegistrationArrangement();
		RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
		registrationIdentifier.setRegistrationNumber(null);
		registrationArrangement.getRegistrationIdentifier().add(registrationIdentifier);
		individual.getHasForeignRegistration().add(registrationArrangement);
		SourceOfFunds sourceOfFunds = new SourceOfFunds();
		sourceOfFunds.setValue(null);
		individual.getSourceOfFunds().add(sourceOfFunds);
		SourceOfWealth SourceOfWealth = new SourceOfWealth();
		SourceOfWealth.setValue(null);
		individual.getSourceOfWealth().add(SourceOfWealth);
		StandardPostalAddress standardPostalAddress = new StandardPostalAddress();
		standardPostalAddress.setState(null);
		standardPostalAddress.setStreetName(null);
		standardPostalAddress.setStreetNumber(null);
		standardPostalAddress.setStreetType(null);
		standardPostalAddress.setPostCode(null);
		standardPostalAddress.setCity(null);
		standardPostalAddress.setCountry(null);
		standardPostalAddress.setFloorNumber(null);
		standardPostalAddress.setUnitNumber(null);
		standardPostalAddress.setBuildingName(null);
		UCMPostalAddressIdentifier uCMPostalAddressIdentifier = new UCMPostalAddressIdentifier();
		uCMPostalAddressIdentifier.setAddressKeyLine1(null);
		uCMPostalAddressIdentifier.setAddressKeyLine2(null);
		standardPostalAddress.setInternalIdentifier(uCMPostalAddressIdentifier);
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PostalAddressContactMethod postalAddressContactMethod = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.PostalAddressContactMethod();
		postalAddressContactMethod.setHasAddress(standardPostalAddress);
		individual.getHasPostalAddressContactMethod().add(postalAddressContactMethod);
		req.setIndividual(individual);
		CreateIndividualIPReqModel createIndividualIPReqModel = siloMovementRequestBuilder.get336ReqModel(req, RoleType.INDIVIDUAL, "BTPL");
		assertNotNull(createIndividualIPReqModel);
	}

	@Test
	public void testGet336ReqModellNullIndividual() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		req.setIndividual(null);
		CreateIndividualIPReqModel createIndividualIPReqModel = siloMovementRequestBuilder.get336ReqModel(req, RoleType.INDIVIDUAL, "BTPL");
		assertNotNull(createIndividualIPReqModel);
	}

	@Test
	public void testGet336ReqModellNullHasForName() {
		RetrieveDetailsAndArrangementRelationshipsForIPsResponse req = new RetrieveDetailsAndArrangementRelationshipsForIPsResponse();
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual individual = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.Individual();
		individual.setHasForName(null);
		req.setIndividual(individual);
		CreateIndividualIPReqModel createIndividualIPReqModel = siloMovementRequestBuilder.get336ReqModel(req, RoleType.INDIVIDUAL, "BTPL");
		assertNotNull(createIndividualIPReqModel);
	}

	@Test
	public void testGet256ReqModel() {
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole involvedPartyArrangementRole = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole();
		ChannelAccessArrangement productArrangement = new ChannelAccessArrangement();
		AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
		accountArrangementIdentifier.setAccountNumber("12445");
		accountArrangementIdentifier.setCanonicalProductCode("457eba2b65ca4c2f937d0deae9866312");
		productArrangement.setAccountArrangementIdentifier(accountArrangementIdentifier);
		productArrangement.setLifecycleStatus("Active");
		productArrangement.setLifecycleStatusReason("dgshfh");
		au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier productArrangementIdentifier = new au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier();
		productArrangementIdentifier.setArrangementId("4356");
		productArrangement.setProductArrangementIdentifier(productArrangementIdentifier);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.MaintenanceAuditContext maintainAuditContext = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.MaintenanceAuditContext();
		maintainAuditContext.setVersionNumber("1");
		involvedPartyArrangementRole.setAuditContext(maintainAuditContext);
		involvedPartyArrangementRole.setHasForContext(productArrangement);
		MaintainArrangementAndRelationshipReqModel reqModel = siloMovementRequestBuilder.get256ReqModel("12345678904", "Individual",
				involvedPartyArrangementRole, "create");
		assertNotNull(reqModel);
	}

	@Test
	public void testGet256AuditContextVersionNumberNullCheck() {
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole involvedPartyArrangementRole = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole();
		ChannelAccessArrangement productArrangement = new ChannelAccessArrangement();
		AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
		accountArrangementIdentifier.setAccountNumber("12445");
		accountArrangementIdentifier.setCanonicalProductCode("457eba2b65ca4c2f937d0deae9866312");
		productArrangement.setAccountArrangementIdentifier(accountArrangementIdentifier);
		productArrangement.setLifecycleStatus("Active");
		productArrangement.setLifecycleStatusReason("dgshfh");

		au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier productArrangementIdentifier = new au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier();
		productArrangementIdentifier.setArrangementId("4356");
		productArrangement.setProductArrangementIdentifier(productArrangementIdentifier);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.MaintenanceAuditContext maintainAuditContext = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.MaintenanceAuditContext();
		maintainAuditContext.setVersionNumber(null);
		involvedPartyArrangementRole.setAuditContext(maintainAuditContext);
		involvedPartyArrangementRole.setHasForContext(productArrangement);
		MaintainArrangementAndRelationshipReqModel reqModel = siloMovementRequestBuilder.get256ReqModel("12345678904", "Individual",
				involvedPartyArrangementRole, "create");
		assertNotNull(reqModel);
	}

	@Test
	public void testGet256AuditContextNullCheck() {
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole involvedPartyArrangementRole = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole();
		ChannelAccessArrangement productArrangement = new ChannelAccessArrangement();
		AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
		accountArrangementIdentifier.setAccountNumber("12445");
		accountArrangementIdentifier.setCanonicalProductCode("457eba2b65ca4c2f937d0deae9866312");
		productArrangement.setAccountArrangementIdentifier(accountArrangementIdentifier);
		productArrangement.setLifecycleStatus("Active");
		productArrangement.setLifecycleStatusReason("dgshfh");

		au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier productArrangementIdentifier = new au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier();
		productArrangementIdentifier.setArrangementId("4356");
		productArrangement.setProductArrangementIdentifier(productArrangementIdentifier);
		involvedPartyArrangementRole.setHasForContext(productArrangement);
		involvedPartyArrangementRole.setAuditContext(null);
		MaintainArrangementAndRelationshipReqModel reqModel = siloMovementRequestBuilder.get256ReqModel("12345678904", "Individual",
				involvedPartyArrangementRole, "create");
		assertNotNull(reqModel);
	}

	@Test
	public void testGet256ReqDeleteModel() {
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole involvedPartyArrangementRole = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole();
		ChannelAccessArrangement productArrangement = new ChannelAccessArrangement();
		AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
		accountArrangementIdentifier.setAccountNumber("12445");
		accountArrangementIdentifier.setCanonicalProductCode("457eba2b65ca4c2f937d0deae9866312");
		productArrangement.setAccountArrangementIdentifier(accountArrangementIdentifier);
		productArrangement.setLifecycleStatus("Active");
		productArrangement.setLifecycleStatusReason("dgshfh");
		au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier productArrangementIdentifier = new au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier();
		productArrangementIdentifier.setArrangementId("4356");
		productArrangement.setProductArrangementIdentifier(productArrangementIdentifier);
		involvedPartyArrangementRole.setHasForContext(productArrangement);
		MaintainArrangementAndRelationshipReqModel reqModel = siloMovementRequestBuilder.get256ReqModel("12345678904", "Individual",
				involvedPartyArrangementRole, "delete");
		assertNotNull(reqModel);
	}

	@Test
	public void testGet256ReqModelForUSECASE1346() {
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole involvedPartyArrangementRole = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole();
		ChannelAccessArrangement productArrangement = new ChannelAccessArrangement();
		AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
		accountArrangementIdentifier.setAccountNumber("12445");
		accountArrangementIdentifier.setCanonicalProductCode("35d1b65704184ae3b87799400f7ab93c");
		productArrangement.setAccountArrangementIdentifier(accountArrangementIdentifier);
		productArrangement.setLifecycleStatus("Active");
		productArrangement.setLifecycleStatusReason("dgshfh");
		au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier productArrangementIdentifier = new au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier();
		productArrangementIdentifier.setArrangementId("4356");
		productArrangement.setProductArrangementIdentifier(productArrangementIdentifier);
		involvedPartyArrangementRole.setHasForContext(productArrangement);
		MaintainArrangementAndRelationshipReqModel reqModel = siloMovementRequestBuilder.get256ReqModel("12345678904", "Individual",
				involvedPartyArrangementRole, "create");
		assertNotNull(reqModel);
	}

	@Test
	public void testGet256ReqModelCreateCaseOne() {
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole involvedPartyArrangementRole = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole();
		ChannelAccessArrangement productArrangement = new ChannelAccessArrangement();
		AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
		accountArrangementIdentifier.setAccountNumber("12445");
		productArrangement.setLifecycleStatus("Active");
		productArrangement.setLifecycleStatusReason("dgshfh");
		au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier productArrangementIdentifier = new au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier();
		productArrangementIdentifier.setArrangementId("4356");
		productArrangementIdentifier.setCanonicalProductCode("35d1b65704184ae3b87799400f7ab93c");
		productArrangement.setProductArrangementIdentifier(productArrangementIdentifier);
		involvedPartyArrangementRole.setHasForContext(productArrangement);
		MaintainArrangementAndRelationshipReqModel reqModel = siloMovementRequestBuilder.get256ReqModel("12345678904", "Individual",
				involvedPartyArrangementRole, "create");
		assertNotNull(reqModel);
	}

	@Test
	public void testGet256ReqModelCaseOne() {
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole involvedPartyArrangementRole = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v11.svc0258.InvolvedPartyArrangementRole();
		ChannelAccessArrangement productArrangement = new ChannelAccessArrangement();
		AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
		accountArrangementIdentifier.setAccountNumber("12445");
		productArrangement.setLifecycleStatus("Active");
		productArrangement.setLifecycleStatusReason("dgshfh");
		au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier productArrangementIdentifier = new au.com.westpac.gn.common.xsd.identifiers.v1.ProductArrangementIdentifier();
		productArrangementIdentifier.setArrangementId("4356");
		productArrangementIdentifier.setCanonicalProductCode("35d1b65704184ae3b87799400f7ab93c");
		productArrangement.setProductArrangementIdentifier(productArrangementIdentifier);
		involvedPartyArrangementRole.setHasForContext(productArrangement);
		MaintainArrangementAndRelationshipReqModel reqModel = siloMovementRequestBuilder.get256ReqModel("12345678904", "Individual",
				involvedPartyArrangementRole, "delete");
		assertNotNull(reqModel);
	}

	@Test
	public void testGet324ReqModel() {
		RetrieveIDVDetailsReqModel reqModel = siloMovementRequestBuilder.get324ReqModel("12345678905", "Individual", "BTPL");
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModel() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		Agent agent = new Agent();
		Individual individual = new Individual();
		IndividualName individualName = new IndividualName();
		individualName.getMiddleNames().add("middleName");
		individualName.setInvolvedPartyNameType(InvolvedPartyNameType.TRADING_NAME);
		individual.setBirthDate(getDate(10, 10, 1987));
		individual.getHasForName().add(individualName);
		individual.getHasForName().add(individualName);
		individual.setIsSoleTrader(BooleanENUM.Y);
		EmploymentDetails employmentDetails = new EmploymentDetails();
		employmentDetails.setCurrentEmployer("BT");
		individual.setEmploymentDetails(employmentDetails);
		agent.setRoleIsPlayedBy(individual);
		EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
		employeeIdentifier.setEmployeeNumber("1234");
		agent.setExternalIdentifier(employeeIdentifier);
		individualIDVAssessment.setPerformedExternallyBy(agent);
		individualIDVAssessment.setPerformedInternallyBy(agent);
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IdentityVerificationDocument document = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IdentityVerificationDocument();
		document.setDocumentType("Type of document.");
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttribute documentAttribute = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttribute();
		documentAttribute.setAttributeName(DocumentAttributeName.IND_IDV_DOC_ACCREDITED_ENGLISH_TRANSLATION_FLG);
		documentAttribute.setAttributeValue("value");
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttribute documentAttributes = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttribute();
		documentAttributes.setAttributeName(DocumentAttributeName.IND_IDV_DOC_DATE_OF_ARRIVAL_AUSTRALIA);
		documentAttributes.setAttributeValue("value");
		document.getDocumentAttribute().add(documentAttribute);
		document.getDocumentAttribute().add(documentAttributes);
		individualIDVAssessment.getIdentityVerificationDocument().add(document);
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);

		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId("24354");
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod addressContactMethod = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod();
		NonStandardPostalAddress nonStandardPostalAddress = new NonStandardPostalAddress();
		nonStandardPostalAddress.setAddressLine1("sfdgd");
		nonStandardPostalAddress.setAddressLine2("asdf");
		nonStandardPostalAddress.setCity("ddfgfg");
		nonStandardPostalAddress.setCountry("dfggf");
		nonStandardPostalAddress.setPostalAddressType("gdgfhgf");
		nonStandardPostalAddress.setState("sdfdggf");
		nonStandardPostalAddress.setPostCode("2000");
		addressContactMethod.setHasAddress(nonStandardPostalAddress);
		addressContactMethod.setUsage(ContactMethodUsage.MAIN_BUSINESS_PREMISES);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod addressContactMethod2 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod();
		NonStandardPostalAddress nonStandardPostalAddress2 = new NonStandardPostalAddress();
		nonStandardPostalAddress2.setAddressLine1("sfdgd");
		nonStandardPostalAddress2.setAddressLine2("asdf");
		nonStandardPostalAddress2.setCity("ddfgfg");
		nonStandardPostalAddress2.setCountry("dfggf");
		nonStandardPostalAddress2.setPostalAddressType("gdgfhgf");
		nonStandardPostalAddress2.setState("sdfdggf");
		nonStandardPostalAddress2.setPostCode("2000");
		addressContactMethod2.setHasAddress(nonStandardPostalAddress2);
		addressContactMethod2.setUsage(ContactMethodUsage.MAIN_BUSINESS_PREMISES);
		individual.getHasContactMethod().add(addressContactMethod);
		individual.getHasContactMethod().add(addressContactMethod2);
		InvolvedPartyRegistrationArrangement arrngment = new InvolvedPartyRegistrationArrangement();
		RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
		registrationIdentifier.setRegistrationNumber("35454");
		registrationIdentifier.setRegistrationNumberType(RegistrationNumberType.ABN);
		arrngment.getRegistrationIdentifier().add(registrationIdentifier);
		individual.setHasRegistration(arrngment);
		individualIDVAssessment.setHasForSubject(individual);
		individualIDVAssessment.setExternalIDVDate(getDate(1, 1, 1990));
		Customer customer = new Customer();
		CustomerIdentifier customerIdentifier = new CustomerIdentifier();
		customerIdentifier.setCustomerNumber("2454646");
		customer.setCustomerIdentifier(customerIdentifier);
		individual.setIsPlayingRole(customer);
		res.setInvolvedParty(individual);
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForRegistrationIdentifierNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		Agent agent = new Agent();
		Individual individual = new Individual();
		IndividualName individualName = new IndividualName();
		individualName.setInvolvedPartyNameType(InvolvedPartyNameType.TRADING_NAME);
		individual.getHasForName().add(individualName);
		individual.getHasForName().add(individualName);
		individual.setIsSoleTrader(BooleanENUM.Y);
		EmploymentDetails employmentDetails = new EmploymentDetails();
		employmentDetails.setCurrentEmployer("BT");
		individual.setEmploymentDetails(employmentDetails);
		agent.setRoleIsPlayedBy(individual);
		EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
		employeeIdentifier.setEmployeeNumber("1234");
		agent.setExternalIdentifier(employeeIdentifier);
		individualIDVAssessment.setPerformedExternallyBy(agent);
		individualIDVAssessment.setPerformedInternallyBy(agent);
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IdentityVerificationDocument document = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IdentityVerificationDocument();
		document.setDocumentType("Type of document.");
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttribute documentAttribute = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttribute();
		documentAttribute.setAttributeName(DocumentAttributeName.IND_IDV_DOC_ACCREDITED_ENGLISH_TRANSLATION_FLG);
		documentAttribute.setAttributeValue("value");
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttribute documentAttributes = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.DocumentAttribute();
		documentAttributes.setAttributeName(DocumentAttributeName.IND_IDV_DOC_DATE_OF_ARRIVAL_AUSTRALIA);
		documentAttributes.setAttributeValue("value");
		document.getDocumentAttribute().add(documentAttribute);
		document.getDocumentAttribute().add(documentAttributes);
		individualIDVAssessment.getIdentityVerificationDocument().add(document);
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);

		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId("24354");
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod addressContactMethod = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod();
		NonStandardPostalAddress nonStandardPostalAddress = new NonStandardPostalAddress();
		nonStandardPostalAddress.setAddressLine1("sfdgd");
		nonStandardPostalAddress.setAddressLine2("asdf");
		nonStandardPostalAddress.setCity("ddfgfg");
		nonStandardPostalAddress.setCountry("dfggf");
		nonStandardPostalAddress.setPostalAddressType("gdgfhgf");
		nonStandardPostalAddress.setState("sdfdggf");
		nonStandardPostalAddress.setPostCode("2000");
		addressContactMethod.setHasAddress(nonStandardPostalAddress);
		addressContactMethod.setUsage(ContactMethodUsage.MAIN_BUSINESS_PREMISES);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod addressContactMethod2 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod();
		NonStandardPostalAddress nonStandardPostalAddress2 = new NonStandardPostalAddress();
		nonStandardPostalAddress2.setAddressLine1("sfdgd");
		nonStandardPostalAddress2.setAddressLine2("asdf");
		nonStandardPostalAddress2.setCity("ddfgfg");
		nonStandardPostalAddress2.setCountry("dfggf");
		nonStandardPostalAddress2.setPostalAddressType("gdgfhgf");
		nonStandardPostalAddress2.setState("sdfdggf");
		nonStandardPostalAddress2.setPostCode("2000");
		addressContactMethod2.setHasAddress(nonStandardPostalAddress2);
		addressContactMethod2.setUsage(ContactMethodUsage.MAIN_BUSINESS_PREMISES);
		individual.getHasContactMethod().add(addressContactMethod);
		individual.getHasContactMethod().add(addressContactMethod2);
		InvolvedPartyRegistrationArrangement arrngment = new InvolvedPartyRegistrationArrangement();
		RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
		registrationIdentifier.setRegistrationNumber("35454");
		registrationIdentifier.setRegistrationNumberType(null);
		arrngment.getRegistrationIdentifier().add(registrationIdentifier);
		individual.setHasRegistration(arrngment);
		individualIDVAssessment.setHasForSubject(individual);
		individualIDVAssessment.setExternalIDVDate(getDate(1, 1, 1990));
		Customer customer = new Customer();
		CustomerIdentifier customerIdentifier = new CustomerIdentifier();
		customerIdentifier.setCustomerNumber("2454646");
		customer.setCustomerIdentifier(customerIdentifier);
		individual.setIsPlayingRole(customer);
		res.setInvolvedParty(individual);
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForDocumentsNullCheck() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		Agent agent = new Agent();
		Individual individual = new Individual();
		IndividualName individualName = new IndividualName();
		individualName.setInvolvedPartyNameType(InvolvedPartyNameType.TRADING_NAME);
		individual.getHasForName().add(individualName);
		individual.getHasForName().add(individualName);
		individual.setIsSoleTrader(BooleanENUM.Y);
		EmploymentDetails employmentDetails = new EmploymentDetails();
		employmentDetails.setCurrentEmployer("BT");
		individual.setEmploymentDetails(employmentDetails);
		agent.setRoleIsPlayedBy(individual);
		EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
		employeeIdentifier.setEmployeeNumber("1234");
		agent.setExternalIdentifier(employeeIdentifier);
		individualIDVAssessment.setPerformedExternallyBy(agent);
		individualIDVAssessment.setPerformedInternallyBy(agent);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IdentityVerificationDocument document = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.IdentityVerificationDocument();
		individualIDVAssessment.getIdentityVerificationDocument().add(document);

		res.getIdentityVerificationAssessment().add(individualIDVAssessment);

		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId("24354");
		individual.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);
		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod addressContactMethod = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod();
		NonStandardPostalAddress nonStandardPostalAddress = new NonStandardPostalAddress();
		nonStandardPostalAddress.setAddressLine1("sfdgd");
		nonStandardPostalAddress.setAddressLine2("asdf");
		nonStandardPostalAddress.setCity("ddfgfg");
		nonStandardPostalAddress.setCountry("dfggf");
		nonStandardPostalAddress.setPostalAddressType("gdgfhgf");
		nonStandardPostalAddress.setState("sdfdggf");
		nonStandardPostalAddress.setPostCode("2000");
		addressContactMethod.setHasAddress(nonStandardPostalAddress);
		addressContactMethod.setUsage(ContactMethodUsage.MAIN_BUSINESS_PREMISES);

		au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod addressContactMethod2 = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.retrieveidvdetails.v6.svc0324.AddressContactMethod();
		NonStandardPostalAddress nonStandardPostalAddress2 = new NonStandardPostalAddress();
		nonStandardPostalAddress2.setAddressLine1("sfdgd");
		nonStandardPostalAddress2.setAddressLine2("asdf");
		nonStandardPostalAddress2.setCity("ddfgfg");
		nonStandardPostalAddress2.setCountry("dfggf");
		nonStandardPostalAddress2.setPostalAddressType("gdgfhgf");
		nonStandardPostalAddress2.setState("sdfdggf");
		nonStandardPostalAddress2.setPostCode("2000");
		addressContactMethod2.setHasAddress(nonStandardPostalAddress2);
		addressContactMethod2.setUsage(ContactMethodUsage.MAIN_BUSINESS_PREMISES);
		individual.getHasContactMethod().add(addressContactMethod);
		individual.getHasContactMethod().add(addressContactMethod2);
		InvolvedPartyRegistrationArrangement arrngment = new InvolvedPartyRegistrationArrangement();
		RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
		registrationIdentifier.setRegistrationNumber("35454");
		registrationIdentifier.setRegistrationNumberType(null);
		arrngment.getRegistrationIdentifier().add(registrationIdentifier);
		individual.setHasRegistration(arrngment);
		individualIDVAssessment.setHasForSubject(individual);
		individualIDVAssessment.setExternalIDVDate(getDate(1, 1, 1990));
		Customer customer = new Customer();
		CustomerIdentifier customerIdentifier = new CustomerIdentifier();
		customerIdentifier.setCustomerNumber("2454646");
		customer.setCustomerIdentifier(customerIdentifier);
		individual.setIsPlayingRole(customer);
		res.setInvolvedParty(individual);
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForIdentityVerificationAssessmentNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForIndividualIDVAssessmentNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForPerformedExternallyByNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		individualIDVAssessment.setPerformedExternallyBy(null);
		individualIDVAssessment.setPerformedInternallyBy(null);
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForRoleIsPlayedByNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		individualIDVAssessment.setPerformedExternallyBy(null);
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForHasForNameNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		Agent agent = new Agent();
		Individual individual = new Individual();
		agent.setRoleIsPlayedBy(individual);
		EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
		employeeIdentifier.setEmployeeNumber("1234");
		agent.setExternalIdentifier(employeeIdentifier);
		individualIDVAssessment.setPerformedExternallyBy(agent);
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForHasForNameEmptyChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		Agent agent = new Agent();
		Individual individual = new Individual();
		agent.setRoleIsPlayedBy(individual);
		EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
		employeeIdentifier.setEmployeeNumber("1234");
		agent.setExternalIdentifier(employeeIdentifier);
		individualIDVAssessment.setPerformedExternallyBy(agent);
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);
		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForInvolvedPartyIdentifierNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();

		res.setInvolvedParty(null);

		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForInvolvedPartyIdentifierEmptyChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();

		Individual individual = new Individual();

		res.setInvolvedParty(individual);

		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForIsPlayingRoleNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();

		Individual individual = new Individual();
		individual.setIsPlayingRole(null);
		res.setInvolvedParty(individual);

		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForCustomerIdentifierNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();

		Individual individual = new Individual();
		Customer customer = new Customer();
		individual.setIsPlayingRole(customer);
		res.setInvolvedParty(individual);

		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForHasForSubjectNullChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();

		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		individualIDVAssessment.setHasForSubject(null);
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);

		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	@Test
	public void testGet325ReqModelForHasContactMethodEmptyChecks() {
		RetrieveIDVDetailsResponse res = new RetrieveIDVDetailsResponse();
		Individual individual = new Individual();

		IndividualIDVAssessment individualIDVAssessment = new IndividualIDVAssessment();
		individualIDVAssessment.setHasForSubject(individual);
		res.getIdentityVerificationAssessment().add(individualIDVAssessment);

		MaintainIdvDetailReqModel reqModel = siloMovementRequestBuilder.get325ReqModel(res, res336);
		assertNotNull(reqModel);
	}

	private JAXBElement<XMLGregorianCalendar> getDate(int day, int month, int year) {
		ObjectFactory factory = new ObjectFactory();
		XMLGregorianCalendar birthdate = new XMLGregorianCalendarImpl();
		birthdate.setDay(day);
		birthdate.setMonth(month);
		birthdate.setYear(year);
		return factory.createIndividualBirthDate(birthdate);
	}
}
