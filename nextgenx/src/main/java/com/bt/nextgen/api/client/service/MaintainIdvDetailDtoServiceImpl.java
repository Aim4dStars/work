/**
 * 
 */
package com.bt.nextgen.api.client.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

import au.com.westpac.gn.common.xsd.commontypes.v3.BooleanENUM;
import au.com.westpac.gn.common.xsd.identifiers.v1.CustomerIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.EmployeeIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.AddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.Agent;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.ContactMethodUsage;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.Customer;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.DocumentAttribute;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.DocumentAttributeName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.EmploymentDetails;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.GeographicArea;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.IdentityVerificationDocument;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.IndividualIDVAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.IndividualName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.InvolvedParty;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.InvolvedPartyNameType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.InvolvedPartyRegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.InvolvedPartyRelationshipRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MoneyLaunderingAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.Organisation;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.OrganisationIDVAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.PersonContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.RequestAction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.maintainidvdetail.v5.MaintainIdvDetailIntegrationService;
import com.bt.nextgen.service.gesb.maintainidvdetail.v5.MaintainIdvRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;

/**
 * @author L081050 This class create the request from the input request model
 *         and call the integration service by providing the newly created
 *         request
 */
@Service("maintainidvdetaildtoservice")
@SuppressWarnings("squid:S1200")
public class MaintainIdvDetailDtoServiceImpl implements MaintainIdvDetailDtoService {
	private static final Logger logger = LoggerFactory.getLogger(MaintainIdvDetailDtoServiceImpl.class);

	private final static String SELECT = "select";
	private final static String TIMEZONE_AUS = "Australia/Sydney";
	private final static String DATEFORMAT = "yyyy-MM-DD";
	private final static String ASSESSMENT_METHOD = "NON WBC";
	private final static String ASSESSMENT_SUBMETHOD = "ACE";
	private final static String LEGAL_STRUCTURE = "Proprietary";
	private final static String AGENT_TYPE = "Other";
	private final static String ROLE_TYPE = "Customer";
	private final static String NOT_APPLICABLE = "Not Applicable";
	private final static String DATE_TYPE_IDV = "idv";

	private final static String PERSON_TYPE_INDV = "Individual";
	private final static String PERSON_TYPE_ORG = "Organisation";

	private final static String DATE_TYPE_BIRTH = "birth";
	private final static QName ARRANGEMENT_QNAME_IDV = new QName("externalIDVDate");

	private final static QName ARRANGEMENT_QNAME_BIRTH = new QName("birthDate");
	@Autowired
	@Qualifier("maintainidvdetailintegrationservice")
	private MaintainIdvDetailIntegrationService maintainIdvDetailIntegrationService;

	@Override
	public CustomerRawData maintain(MaintainIdvDetailReqModel reqModel, ServiceErrors serviceErrors) {
		MaintainIdvRequest maintainIdvRequest = createRequest(reqModel);
		logger.info("Calling maintainIdvDetailIntegrationService.maintain");
		CustomerRawData customerData = maintainIdvDetailIntegrationService.maintain(maintainIdvRequest, serviceErrors);
		return customerData;
	}

	private MaintainIdvRequest createRequest(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		MaintainIdvRequest maintainIdvRequest = new MaintainIdvRequest();
		if (null != maintainIdvDetailReqModel.getRequestedAction() && !StringUtils.isBlank(maintainIdvDetailReqModel.getRequestedAction())
				&& !SELECT.equalsIgnoreCase(maintainIdvDetailReqModel.getRequestedAction())) {
			maintainIdvRequest.setRequestAction(RequestAction.fromValue(maintainIdvDetailReqModel.getRequestedAction().trim()));
		}
		if (PERSON_TYPE_INDV.equalsIgnoreCase(maintainIdvDetailReqModel.getIdvType())) {
			maintainIdvRequest.setIdentityVerificationAssessment(getIndividualIDVAssessment(maintainIdvDetailReqModel));
		} else {
			maintainIdvRequest.setIdentityVerificationAssessment(getOrganisationIDVAssessment(maintainIdvDetailReqModel));
		}
		return maintainIdvRequest;
	}

	private OrganisationIDVAssessment getOrganisationIDVAssessment(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {

		OrganisationIDVAssessment organisationIDVAssessment = new OrganisationIDVAssessment();
		organisationIDVAssessment.setAssessmentMethod(ASSESSMENT_METHOD);
		Agent agent = new Agent();
		agent.setAgentType(AGENT_TYPE);
		EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
		employeeIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		employeeIdentifier.setEmployeeNumber(maintainIdvDetailReqModel.getAgentCisKey());
		agent.setExternalIdentifier(employeeIdentifier);
		Individual individual = new Individual();
		IndividualName individualName = new IndividualName();
		individualName.setFullName(maintainIdvDetailReqModel.getAgentName());
		individual.getHasForName().add(individualName);
		EmploymentDetails employmentDetails = new EmploymentDetails();
		employmentDetails.setCurrentEmployer(maintainIdvDetailReqModel.getEmployerName());
		individual.setEmploymentDetails(employmentDetails);
		agent.setRoleIsPlayedBy(individual);
		organisationIDVAssessment.setPerformedExternallyBy(agent);
		organisationIDVAssessment.setExternalIDVDate(convertToGregorianDate(maintainIdvDetailReqModel.getExtIdvDate(), DATE_TYPE_IDV));
		organisationIDVAssessment.setNoABNReason(NOT_APPLICABLE);
		IdentityVerificationDocument identityVerificationDocument = new IdentityVerificationDocument();
		identityVerificationDocument.setDocumentType(maintainIdvDetailReqModel.getDocumentType());
		identityVerificationDocument.getDocumentAttribute().addAll(getDocumentAttributeList(maintainIdvDetailReqModel));
		organisationIDVAssessment.getIdentityVerificationDocument().add(identityVerificationDocument);
		Organisation org = new Organisation();
		org.getHasContactMethod().addAll(getAddressContactMethod(maintainIdvDetailReqModel));
		org.setHasForContactPerson(getPersonContactMethod(maintainIdvDetailReqModel));
		org.getInvolvedPartyIdentifier().add(getInvolvedPartyIdentifier(maintainIdvDetailReqModel));
		org.getHasForName().add(getIndividualName(maintainIdvDetailReqModel));
		MoneyLaunderingAssessment moneyLaunderingAssessment = new MoneyLaunderingAssessment();
		moneyLaunderingAssessment.setEntityType(maintainIdvDetailReqModel.getPersonType());
		org.getHasAntiMoneyLaunderingAssessment().add(moneyLaunderingAssessment);
		org.setIsASICRegistered(BooleanENUM.Y);
		org.setLegalStructure(LEGAL_STRUCTURE);
		org.setIsRegulatedBy(getInvolvedParty(maintainIdvDetailReqModel));
		org.setHasRegistration(getInvolvedPartyRegistrationArrangement(maintainIdvDetailReqModel));
		//org.setIsPlayingRole(getIsPlayingRole(maintainIdvDetailReqModel));
		if (null != maintainIdvDetailReqModel.getIsForeignRegistered() && !StringUtils.isBlank(maintainIdvDetailReqModel.getIsForeignRegistered())
				&& !SELECT.equalsIgnoreCase(maintainIdvDetailReqModel.getIsForeignRegistered())) {
			org.setIsForeignRegistered(BooleanENUM.fromValue(maintainIdvDetailReqModel.getIsForeignRegistered()));
		}
		org.setFoundationDate(convertToGregorianDate(maintainIdvDetailReqModel.getDateOfBirth(), DATE_TYPE_BIRTH));
		organisationIDVAssessment.getInvolvedPartyAssociationRole().add(getInvolvedPartyRelationshipRole(maintainIdvDetailReqModel));
		organisationIDVAssessment.setHasForSubject(org);
		//organisationIDVAssessment.setAssessmentSubMethod(ASSESSMENT_SUBMETHOD);
		//organisationIDVAssessment.setRoleType(ROLE_TYPE);
		return organisationIDVAssessment;
	}

	private IndividualIDVAssessment getIndividualIDVAssessment(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		IndividualIDVAssessment identityVerificationAssessment = new IndividualIDVAssessment();
		identityVerificationAssessment.setAssessmentMethod(ASSESSMENT_METHOD);
		Agent agent = new Agent();
		agent.setAgentType(AGENT_TYPE);
		EmployeeIdentifier employeeIdentifier = new EmployeeIdentifier();
		employeeIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		employeeIdentifier.setEmployeeNumber(maintainIdvDetailReqModel.getAgentCisKey());
		agent.setExternalIdentifier(employeeIdentifier);
		Individual individual = new Individual();
		IndividualName individualName = new IndividualName();
		individualName.setFullName(maintainIdvDetailReqModel.getAgentName());
		individual.getHasForName().add(individualName);
		EmploymentDetails employmentDetails = new EmploymentDetails();
		employmentDetails.setCurrentEmployer(maintainIdvDetailReqModel.getEmployerName());
		individual.setEmploymentDetails(employmentDetails);
		agent.setRoleIsPlayedBy(individual);
		identityVerificationAssessment.setPerformedExternallyBy(agent);
		identityVerificationAssessment.setExternalIDVDate(convertToGregorianDate(maintainIdvDetailReqModel.getExtIdvDate(), DATE_TYPE_IDV));
		identityVerificationAssessment.setNoABNReason(NOT_APPLICABLE);
		IdentityVerificationDocument identityVerificationDocument = new IdentityVerificationDocument();
		identityVerificationDocument.setDocumentType(maintainIdvDetailReqModel.getDocumentType());
		identityVerificationDocument.getDocumentAttribute().addAll(getDocumentAttributeList(maintainIdvDetailReqModel));
		identityVerificationAssessment.getIdentityVerificationDocument().add(identityVerificationDocument);
		Individual indiv = new Individual();
		indiv.getHasContactMethod().addAll(getAddressContactMethod(maintainIdvDetailReqModel));
		indiv.getInvolvedPartyIdentifier().add(getInvolvedPartyIdentifier(maintainIdvDetailReqModel));
		indiv.getHasForName().add(getIndividualName(maintainIdvDetailReqModel));
		MoneyLaunderingAssessment moneyLaunderingAssessment = new MoneyLaunderingAssessment();
		moneyLaunderingAssessment.setEntityType(maintainIdvDetailReqModel.getPersonType());
		indiv.getHasAntiMoneyLaunderingAssessment().add(moneyLaunderingAssessment);
		indiv.setHasRegistration(getInvolvedPartyRegistrationArrangement(maintainIdvDetailReqModel));
		//indiv.setIsPlayingRole(getIsPlayingRole(maintainIdvDetailReqModel));
		indiv.setBirthDate(convertToGregorianDate(maintainIdvDetailReqModel.getDateOfBirth(), DATE_TYPE_BIRTH));
		if (null != maintainIdvDetailReqModel.getIsSoleTrader() && !StringUtils.isBlank(maintainIdvDetailReqModel.getIsSoleTrader())
				&& !SELECT.equalsIgnoreCase(maintainIdvDetailReqModel.getIsSoleTrader())) {
			indiv.setIsSoleTrader(BooleanENUM.fromValue(maintainIdvDetailReqModel.getIsSoleTrader()));
		}
		identityVerificationAssessment.setHasForSubject(indiv);
		//identityVerificationAssessment.setAssessmentSubMethod(ASSESSMENT_SUBMETHOD);
		//identityVerificationAssessment.setRoleType(ROLE_TYPE);
		return identityVerificationAssessment;
	}

	private InvolvedPartyRelationshipRole getInvolvedPartyRelationshipRole(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		InvolvedPartyRelationshipRole involvedPartyRelationshipRole = new InvolvedPartyRelationshipRole();
		AddressContactMethod adressContactMethod = new AddressContactMethod();
		NonStandardPostalAddress nonStandardPostalAddress = new NonStandardPostalAddress();
		nonStandardPostalAddress.setAddressLine1(maintainIdvDetailReqModel.getIparAddressLine1());
		nonStandardPostalAddress.setCity(maintainIdvDetailReqModel.getIparCity());
		nonStandardPostalAddress.setCountry(maintainIdvDetailReqModel.getIparCountry());
		nonStandardPostalAddress.setPostCode(maintainIdvDetailReqModel.getIparPostCode());
		nonStandardPostalAddress.setState(maintainIdvDetailReqModel.getIparState());
		adressContactMethod.setHasAddress(nonStandardPostalAddress);
		involvedPartyRelationshipRole.getHasContactMethod().add(adressContactMethod);
		involvedPartyRelationshipRole.setRoleType(maintainIdvDetailReqModel.getIparType());
		InvolvedParty involvedParty = new InvolvedParty();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		involvedPartyIdentifier.setInvolvedPartyId(maintainIdvDetailReqModel.getIparCisKey());
		involvedParty.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);
		involvedPartyRelationshipRole.setRoleIsPlayedBy(involvedParty);
		involvedPartyRelationshipRole.setIPRelationshipRoleId(maintainIdvDetailReqModel.getIparId());
		return involvedPartyRelationshipRole;
	}

	/*private Customer getIsPlayingRole(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		Customer customer = new Customer();
		CustomerIdentifier customerIdentifier = new CustomerIdentifier();
		customerIdentifier.setCustomerNumber(maintainIdvDetailReqModel.getCustomerNumber());
		customer.setCustomerIdentifier(customerIdentifier);
		return customer;
	}*/

	private InvolvedPartyRegistrationArrangement getInvolvedPartyRegistrationArrangement(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		InvolvedPartyRegistrationArrangement involvedPartyRegistrationArrangement = new InvolvedPartyRegistrationArrangement();
		RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
		registrationIdentifier.setRegistrationNumber(maintainIdvDetailReqModel.getRegistrationNumber());
		if (null != maintainIdvDetailReqModel.getRegistrationNumberType()
				&& !StringUtils.isBlank(maintainIdvDetailReqModel.getRegistrationNumberType())
				&& !SELECT.equalsIgnoreCase(maintainIdvDetailReqModel.getRegistrationNumberType())) {
			registrationIdentifier.setRegistrationNumberType(RegistrationNumberType.fromValue(maintainIdvDetailReqModel.getRegistrationNumberType()));
		}
		involvedPartyRegistrationArrangement.getRegistrationIdentifier().add(registrationIdentifier);
		if (null != maintainIdvDetailReqModel.getIdvType() && PERSON_TYPE_ORG.equalsIgnoreCase(maintainIdvDetailReqModel.getIdvType())) {
			GeographicArea geographicArea = new GeographicArea();
			geographicArea.setState(maintainIdvDetailReqModel.getIsIssuedAtState());
			geographicArea.setCountry(maintainIdvDetailReqModel.getIsIssuedAtCountry());
			involvedPartyRegistrationArrangement.setIsIssuedAt(geographicArea);
		}
		return involvedPartyRegistrationArrangement;
	}

	private InvolvedParty getInvolvedParty(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		InvolvedParty involvedParty = new InvolvedParty();
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setInvolvedPartyId(maintainIdvDetailReqModel.getIsRegulatedBy());
		involvedParty.getInvolvedPartyIdentifier().add(involvedPartyIdentifier);
		return involvedParty;
	}

	private PersonContactMethod getPersonContactMethod(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		PersonContactMethod personContactMethod = new PersonContactMethod();
		Individual individual = new Individual();
		IndividualName individualName = new IndividualName();
		individualName.setFullName(maintainIdvDetailReqModel.getHfcpFullName());
		individual.getHasForName().add(individualName);
		personContactMethod.setContactPerson(individual);
		return personContactMethod;
	}

	private List<DocumentAttribute> getDocumentAttributeList(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		List<DocumentAttribute> documentAttributeList = new ArrayList<DocumentAttribute>();
		List<String> attrValList = null;
		List<String> attrNameList = null;
		String attrName = maintainIdvDetailReqModel.getOptAttrName();
		String attrVal = maintainIdvDetailReqModel.getOptAttrVal();
		if (null != attrName && null != attrVal) {
			attrNameList = Arrays.asList(attrName.split(","));
			attrValList = Arrays.asList(attrVal.split(","));
		}
		if (null != attrValList && null != attrNameList) {
			for (int i = 0; i < attrNameList.size(); i++) {
				DocumentAttribute documentAttribute = new DocumentAttribute();
				if (null != attrNameList.get(i) && !SELECT.equalsIgnoreCase(attrNameList.get(i)) && !StringUtils.isBlank(attrNameList.get(i))) {
					documentAttribute.setAttributeName(DocumentAttributeName.fromValue(attrNameList.get(i)));
					documentAttribute.setAttributeValue(attrValList.get(i));
				}
				documentAttributeList.add(documentAttribute);
			}
		}
		return documentAttributeList;
	}

	private List<AddressContactMethod> getAddressContactMethod(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		List<AddressContactMethod> addressList = new ArrayList<AddressContactMethod>();
		List<String> usageList = Arrays.asList(maintainIdvDetailReqModel.getUsage().split(","));
		List<String> addressl1List = Arrays.asList(maintainIdvDetailReqModel.getAddressline1().split(","));
		List<String> addressl2List = Arrays.asList(maintainIdvDetailReqModel.getAddressline2().split(","));
		List<String> cityList = Arrays.asList(maintainIdvDetailReqModel.getCity().split(","));
		List<String> stateList = Arrays.asList(maintainIdvDetailReqModel.getState().split(","));
		List<String> postcodeList = Arrays.asList(maintainIdvDetailReqModel.getPincode().split(","));
		List<String> countryList = Arrays.asList(maintainIdvDetailReqModel.getCountry().split(","));
		List<String> addressTypeList = Arrays.asList(maintainIdvDetailReqModel.getPostalAddressType().split(","));
		for (int i = 0; i < usageList.size(); i++) {
			AddressContactMethod addressContactMethod = new AddressContactMethod();
			addressContactMethod.setUsage(ContactMethodUsage.fromValue(usageList.get(i).trim()));
			NonStandardPostalAddress nonStandardPostalAddress = new NonStandardPostalAddress();
			nonStandardPostalAddress.setPostalAddressType(addressTypeList.get(i));
			nonStandardPostalAddress.setAddressLine1(addressl1List.get(i));

			nonStandardPostalAddress.setAddressLine2(addressl2List.get(i));
			nonStandardPostalAddress.setCity(cityList.get(i));
			nonStandardPostalAddress.setState(stateList.get(i));
			nonStandardPostalAddress.setPostCode(postcodeList.get(i));
			maintainIdvDetailReqModel.setCountry(countryList.get(i));
			addressContactMethod.setHasAddress(nonStandardPostalAddress);
			addressList.add(addressContactMethod);
		}
		return addressList;
	}

	private InvolvedPartyIdentifier getInvolvedPartyIdentifier(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		involvedPartyIdentifier.setInvolvedPartyId(maintainIdvDetailReqModel.getCisKey());
		return involvedPartyIdentifier;
	}

	private IndividualName getIndividualName(MaintainIdvDetailReqModel maintainIdvDetailReqModel) {
		IndividualName individualName = new IndividualName();
		if (null != maintainIdvDetailReqModel.getFullName() || !("".equalsIgnoreCase(maintainIdvDetailReqModel.getFullName()))) {
			individualName.setFullName(maintainIdvDetailReqModel.getFullName());
		}
		if (null != maintainIdvDetailReqModel.getMiddleName() || !("".equalsIgnoreCase(maintainIdvDetailReqModel.getMiddleName()))) {
			individualName.getMiddleNames().add(maintainIdvDetailReqModel.getMiddleName());
		}
		individualName.setFirstName(maintainIdvDetailReqModel.getFirstName());
		individualName.setLastName(maintainIdvDetailReqModel.getLastName());
		if (null != maintainIdvDetailReqModel.getInvolvedPartyNameType()
				&& !StringUtils.isBlank(maintainIdvDetailReqModel.getInvolvedPartyNameType())
				&& !SELECT.equalsIgnoreCase(maintainIdvDetailReqModel.getInvolvedPartyNameType())) {
			individualName.setInvolvedPartyNameType(InvolvedPartyNameType.fromValue(maintainIdvDetailReqModel.getInvolvedPartyNameType().trim()));
		}
		//individualName.setIsOtherName(maintainIdvDetailReqModel.getIsOtherName());
		return individualName;
	}

	@SuppressWarnings(value = { "deprecation" })
	private JAXBElement<XMLGregorianCalendar> convertToGregorianDate(String date, String dateType) {
		JAXBElement<XMLGregorianCalendar> jaxbElement = null;
		Date txnDate = null;
		if (null != date && !StringUtils.isBlank(date)) {
			try {
				txnDate = new Date(date);
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(txnDate);
				XMLGregorianCalendar transDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(calendar.get(Calendar.YEAR),
						calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
				if (DATE_TYPE_IDV.equalsIgnoreCase(dateType)) {
					jaxbElement = new JAXBElement<XMLGregorianCalendar>(ARRANGEMENT_QNAME_IDV, XMLGregorianCalendar.class,
							IndividualIDVAssessment.class, transDate);
				} else {
					jaxbElement = new JAXBElement<XMLGregorianCalendar>(ARRANGEMENT_QNAME_BIRTH, XMLGregorianCalendar.class,
							IndividualIDVAssessment.class, transDate);
				}
			} catch (DatatypeConfigurationException e) {
				logger.error("MaintainArrangementAndRelationshipServiceImpl.convertToGregorianDate(): Exception occured. " + e);
			}
		}
		return jaxbElement;
	}
}
