/**
 * 
 */
package com.bt.nextgen.api.client.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.com.westpac.gn.common.xsd.identifiers.v1.ContactMethodIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.Arrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.AlternateName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.EmailAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.EmailAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.EmploymentDetails;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.FinancialSummaryInformation;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.GeographicArea;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.IndividualName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.InvolvedPartyNameAddressee;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.InvolvedPartyNameFullName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.InvolvedPartyRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.PriorityLevel;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.PurposeOfBusinessRelationship;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.RegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.SourceOfFunds;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.SourceOfWealth;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.StandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.TelephoneAddress;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.createindividualip.v5.CreateIndividualIPIntegrationService;
import com.bt.nextgen.service.gesb.createindividualip.v5.CreateIndvIPRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.CreateIndividualIPEmailPhoneContactMethodsReqModel;
import com.bt.nextgen.serviceops.model.CreateIndividualIPReqModel;

/**
 * @author L081050
 */
@Service("createindividualipdtoservice")
@SuppressWarnings("squid:S1200")
public class CreateIndividualIPDtoServiceImpl implements CreateIndividualIPDtoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRawData.class);

	private final static String DATEFORMAT_MS = "yyyy-MM-dd HH:mm:ss.SSS'Z'";

	private final static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss.SS'Z'";

	private final static String TIMEZONE_AUS = "Australia/Sydney";

	private final static QName LAST_UPDATED_TIMESTAMP = new QName("lastUpdateTimestamp");

	private final static QName STARTDATE = new QName("startDate");

	@Autowired
	@Qualifier("createindividualipintegrationservice")
	private CreateIndividualIPIntegrationService integrationService;

	@Override
	public CustomerRawData create(CreateIndividualIPReqModel reqModel, CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel,
			ServiceErrors serviceErrors) {

		CreateIndvIPRequest createIndividualIPRequest = createIndividualIPRequestModel(reqModel, emailPhoneReqModel);
		CustomerRawData customerData = integrationService.create(createIndividualIPRequest, serviceErrors);
		return customerData;
	}

	@SuppressWarnings("deprecation")
	private CreateIndvIPRequest createIndividualIPRequestModel(CreateIndividualIPReqModel input,
			CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel) {
		Individual individual = new Individual();
		individual.setBirthDate(populateDate(input.getBirthDate()));
		IndividualName individualName = new IndividualName();
		individualName.setPrefixTitle(input.getPrefix());
		individualName.setFirstName(input.getFirstName());
		individualName.setLastName(input.getLastName());
		individualName.getMiddleNames().add(input.getMiddleNames());
		individualName.setPreferredName(input.getPreferredName());
		AlternateName alternateName = new AlternateName();
		if (null != input.getAlternateName()) {
			alternateName.setName(input.getAlternateName());
			alternateName.setIsPreferred(input.getIsPreferred());
			individualName.getHasAlternateName().add(alternateName);
		} else {
			individual.setNoAlternateName(true);
		}
		individual.setHasForName(individualName);
		individual.setStartDate(populateDate(null));
		individual.setGender(input.getGender());
		individual.setBirthDate(populateDate(input.getBirthDate()));
		individual.setIsForeignRegistered(input.isForeignRegistered());

		individual.setLifecycleStatus("P");
		individual.setIsForeignRegistered(input.isForeignRegistered());
		InvolvedPartyRole involvedPartyRole = new InvolvedPartyRole();
		involvedPartyRole.setRoleType(input.getRoleType());
		individual.setIsPlayingRole(involvedPartyRole);
		FinancialSummaryInformation financialSummaryInformation = new FinancialSummaryInformation();
		financialSummaryInformation.setHasLoanWithOtherBank(input.getHasLoansWithOtherBanks());
		individual.setCustomerBehaviourSummary(financialSummaryInformation);
		EmploymentDetails employmentDetails = new EmploymentDetails();
		employmentDetails.setEmploymentType(input.getEmploymentType());
		employmentDetails.setOccupationCode(input.getOccupationCode());
		individual.setEmploymentDetails(employmentDetails);
		MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
		maintenanceAuditContext.setIsActive(true);
		maintenanceAuditContext.setLastUpdateTimestamp(convertToJaxbGregorianDate(LAST_UPDATED_TIMESTAMP));
		setRegistrationArrangementData(input, individual, maintenanceAuditContext);
		PurposeOfBusinessRelationship purposeOfBusinessRelationship = new PurposeOfBusinessRelationship();
		purposeOfBusinessRelationship.setValue(input.getPurposeOfBusinessRelationship());
		purposeOfBusinessRelationship.setStartDate(convertToJaxbGregorianDateMs(STARTDATE));
		individual.getPurposeOfBusinessRelationship().add(purposeOfBusinessRelationship);

		setSourceOfWealthAndFund(input, individual);
		setPostalAddressContactMethodData(input, individual);
		if (null != emailPhoneReqModel) {
			setPhoneAddressContactMethoddData(emailPhoneReqModel, individual);
			setEmailAddressContactMethoddData(emailPhoneReqModel, individual);
		}

		CreateIndvIPRequest createIndividualIPRequest = new CreateIndvIPRequest();
		createIndividualIPRequest.setIndividual(individual);
		return createIndividualIPRequest;
	}

	private void setPhoneAddressContactMethoddData(CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel, Individual individual) {
		PhoneAddressContactMethod phoneAddressContactMethod = new PhoneAddressContactMethod();
		phoneAddressContactMethod.setStartDate(convertToJaxbGregorianDateMs(STARTDATE));
		TelephoneAddress telephoneAddress = new TelephoneAddress();
		telephoneAddress.setAreaCode(emailPhoneReqModel.getPhoneAddressContactAreaCode());
		telephoneAddress.setCountryCode(emailPhoneReqModel.getPhoneAddressContactCountryCode());
		telephoneAddress.setLocalNumber(emailPhoneReqModel.getPhoneAddressContactLocalNumber());
		phoneAddressContactMethod.setHasAddress(telephoneAddress);
		if (null != emailPhoneReqModel.getPhoneAddressContactAddressee()) {
			InvolvedPartyNameFullName involvedPartyNameFullName = new InvolvedPartyNameFullName();
			involvedPartyNameFullName.setFullName(emailPhoneReqModel.getPhoneAddressContactAddressee());
			phoneAddressContactMethod.setAddressee(involvedPartyNameFullName);
		}

		phoneAddressContactMethod.setContactInstructions(emailPhoneReqModel.getPhoneAddressContactContactInstructions());
		phoneAddressContactMethod.setContactMedium(emailPhoneReqModel.getPhoneAddressContactContactMedium());
		// phoneAddressContactMethod.setEndDate(populateDate(input.getPhoneAddressContactEndDate()));
		// phoneAddressContactMethod.setIsPrimary(input.getPho);
		phoneAddressContactMethod.setPreferredContactTime(emailPhoneReqModel.getPhoneAddressContactPreferredContactTime());
		phoneAddressContactMethod.setPriorityLevel((null != emailPhoneReqModel.getPhoneAddressContactPriorityLevel() && !StringUtils
				.isBlank(emailPhoneReqModel.getPhoneAddressContactPriorityLevel())) ? PriorityLevel.valueOf(emailPhoneReqModel
				.getPhoneAddressContactPriorityLevel()) : null);
		// phoneAddressContactMethod.setStartDate(input.getPhoneAddressContactStartDate());
		phoneAddressContactMethod.setUsage(emailPhoneReqModel.getHasPhoneContactMethodUsage());
		phoneAddressContactMethod.setValidityStatus(emailPhoneReqModel.getPhoneAddressContactValidityStatus());
		ContactMethodIdentifier contactMethodIdentifier = new ContactMethodIdentifier();
		contactMethodIdentifier.setContactMethodId(emailPhoneReqModel.getPhoneAddressContactContactMethodId());
		contactMethodIdentifier.setIdentificationScheme((null != emailPhoneReqModel.getPhoneAddressContactIdentificationScheme() && !StringUtils
				.isBlank(emailPhoneReqModel.getPhoneAddressContactIdentificationScheme())) ? IdentificationScheme.valueOf(emailPhoneReqModel
				.getPhoneAddressContactIdentificationScheme()) : null);
		contactMethodIdentifier.setSourceSystem(emailPhoneReqModel.getPhoneAddressContactSourceSystem());
		phoneAddressContactMethod.setContactMethodIdentifier(contactMethodIdentifier);
		individual.getHasPhoneAddressContactMethod().add(phoneAddressContactMethod);
	}

	private void setEmailAddressContactMethoddData(CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqModel, Individual individual) {
		EmailAddressContactMethod emailAddressContactMethod = new EmailAddressContactMethod();
		emailAddressContactMethod.setStartDate(convertToJaxbGregorianDateMs(STARTDATE));
		EmailAddress emailAddress = new EmailAddress();
		emailAddress.setEmailAddress(emailPhoneReqModel.getEmailAddressContactEmailAddress());
		emailAddressContactMethod.setHasAddress(emailAddress);
		if (null != emailPhoneReqModel.getEmailAddressContactAddressee()) {
			InvolvedPartyNameFullName involvedPartyNameFullName = new InvolvedPartyNameFullName();
			involvedPartyNameFullName.setFullName(emailPhoneReqModel.getEmailAddressContactAddressee());
			emailAddressContactMethod.setAddressee(involvedPartyNameFullName);
		}
		// phoneAddressContactMethod.setEndDate(populateDate(input.getEmailAddressContactEndDate()));
		// phoneAddressContactMethod.setIsPrimary(input.getPho);
		emailAddressContactMethod.setPreferredContactTime(emailPhoneReqModel.getEmailAddressContactPreferredContactTime());
		emailAddressContactMethod.setPriorityLevel((null != emailPhoneReqModel.getEmailAddressContactPriorityLevel() && !StringUtils
				.isEmpty(emailPhoneReqModel.getEmailAddressContactPriorityLevel())) ? PriorityLevel.valueOf(emailPhoneReqModel
				.getEmailAddressContactPriorityLevel()) : null);
		// phoneAddressContactMethod.setStartDate(input.getEmailAddressContactStartDate());
		emailAddressContactMethod.setUsage(emailPhoneReqModel.getHasEmailContactMethodUsage());
		emailAddressContactMethod.setValidityStatus(emailPhoneReqModel.getEmailAddressContactValidityStatus());
		ContactMethodIdentifier contactMethodIdentifier = new ContactMethodIdentifier();
		contactMethodIdentifier.setContactMethodId(emailPhoneReqModel.getEmailAddressContactContactMethodId());
		contactMethodIdentifier.setIdentificationScheme((null != emailPhoneReqModel.getEmailAddressContactIdentificationScheme() && !StringUtils
				.isBlank(emailPhoneReqModel.getEmailAddressContactIdentificationScheme())) ? IdentificationScheme.valueOf(emailPhoneReqModel
				.getEmailAddressContactIdentificationScheme()) : null);
		contactMethodIdentifier.setSourceSystem(emailPhoneReqModel.getEmailAddressContactSourceSystem());
		emailAddressContactMethod.setContactMethodIdentifier(contactMethodIdentifier);
		individual.getHasEmailAddressContactMethod().add(emailAddressContactMethod);
	}

	/**
	 * @param input
	 * @param individual
	 */
	private void setPostalAddressContactMethodData(CreateIndividualIPReqModel input, Individual individual) {
		PostalAddressContactMethod postalAddressContactMethod = new PostalAddressContactMethod();
		if ("StandardPostalAddress".equals(input.getAddressType())) {
			StandardPostalAddress standardPostalAddress = getStandardPostalAddressData(input);
			postalAddressContactMethod.setHasAddress(standardPostalAddress);
		} else if ("NonStandardPostalAddress".equals(input.getAddressType())) {
			NonStandardPostalAddress nonStandardPostalAddress = getNonStandardPostalAddress(input);
			postalAddressContactMethod.setHasAddress(nonStandardPostalAddress);
		}
		postalAddressContactMethod.setStartDate(convertToJaxbGregorianDate(STARTDATE));
		postalAddressContactMethod.setValidityStatus("C");
		postalAddressContactMethod.setUsage(input.getUsage());

		postalAddressContactMethod.setIsPrimary(true);
		InvolvedPartyNameAddressee involvedPartyNameAddressee = new InvolvedPartyNameAddressee();
		involvedPartyNameAddressee.getNameText().add(input.getAddresseeNameText());

		// involvedPartyNameAddressee.getNameText().add(input.getFloorNumber());
		// involvedPartyNameAddressee.getNameText().add(input.getUnitNumber());
		postalAddressContactMethod.setAddressee(involvedPartyNameAddressee);
		individual.getHasPostalAddressContactMethod().add(postalAddressContactMethod);
	}

	/**
	 * @param input
	 * @param individual
	 * @param maintenanceAuditContext
	 */
	private void setRegistrationArrangementData(CreateIndividualIPReqModel input, Individual individual,
			MaintenanceAuditContext maintenanceAuditContext) {
		RegistrationArrangement registrationArrangement = new RegistrationArrangement();
		// registrationArrangement.setAuditContext(maintenanceAuditContext);
		registrationArrangement.setEffectiveDate(convertToGregorianDate());
		individual.getHasRegistration().add(registrationArrangement);
		RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
		registrationIdentifier.setRegistrationNumberType(RegistrationNumberType.FOREIGN);
		registrationIdentifier.setRegistrationNumber(input.getRegistrationIdentifierNumber());
		registrationArrangement.setRegistrationIdentifier(registrationIdentifier);
		registrationArrangement.setStartDate(convertToGregorianDate());

		GeographicArea geographicArea = new GeographicArea();
		geographicArea.setCountry(input.getCountry());
		geographicArea.setState(input.getState());
		registrationArrangement.setIsIssuedAt(geographicArea);
		individual.getHasForeignRegistration().add(registrationArrangement);
	}

	/**
	 * @param input
	 * @param individual
	 */
	private void setSourceOfWealthAndFund(CreateIndividualIPReqModel input, Individual individual) {
		SourceOfFunds sourceOfFund = new SourceOfFunds();
		sourceOfFund.setValue(input.getSourceOfFunds());
		sourceOfFund.setStartDate(convertToJaxbGregorianDateMs(STARTDATE));
		individual.getSourceOfFunds().add(sourceOfFund);

		SourceOfWealth sourceOfWealth = new SourceOfWealth();
		sourceOfWealth.setValue(input.getSourceOfWealth());
		sourceOfWealth.setStartDate(convertToJaxbGregorianDateMs(STARTDATE));
		individual.getSourceOfWealth().add(sourceOfWealth);

	}

	/**
	 * @param input
	 * @return
	 */
	private StandardPostalAddress getStandardPostalAddressData(CreateIndividualIPReqModel input) {
		StandardPostalAddress standardPostalAddress = new StandardPostalAddress();
		standardPostalAddress.setState(input.getState());
		standardPostalAddress.setCity(input.getCity());
		standardPostalAddress.setStreetNumber(input.getStreetNumber());
		standardPostalAddress.setStreetName(input.getStreetName());
		standardPostalAddress.setStreetType(input.getStreetType());
		standardPostalAddress.setState(input.getState());
		standardPostalAddress.setCountry(input.getCountry());
		standardPostalAddress.setPostCode(input.getPostCode());
		standardPostalAddress.setUnitNumber(input.getUnitNumber());
		standardPostalAddress.setBuildingName(input.getBuildingName());
		standardPostalAddress.setFloorNumber(input.getFloorNumber());
		return standardPostalAddress;
	}

	/**
	 * @param input
	 * @return
	 */
	private NonStandardPostalAddress getNonStandardPostalAddress(CreateIndividualIPReqModel input) {
		NonStandardPostalAddress nonStandardPostalAddress = new NonStandardPostalAddress();
		nonStandardPostalAddress.setAddressLine1(input.getAddressLine1());
		nonStandardPostalAddress.setAddressLine2(input.getAddressLine2());
		nonStandardPostalAddress.setAddressLine3(input.getAddressLine3());
		nonStandardPostalAddress.setCity(input.getCity());
		nonStandardPostalAddress.setState(input.getState());
		nonStandardPostalAddress.setPostCode(input.getPostCode());
		nonStandardPostalAddress.setCountry(input.getCountry());
		return nonStandardPostalAddress;
	}

	private XMLGregorianCalendar convertToGregorianDate() {
		GregorianCalendar gregCalendar = new GregorianCalendar();
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);

		// Here you say to java the initial timezone. This is the secret
		sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE_AUS));
		// Will print in UTC
		XMLGregorianCalendar transDate;
		try {
			transDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCalendar);
			return transDate;
		} catch (DatatypeConfigurationException e) {
			LOGGER.error("CreateIndividualIPDtoServiceImpl.convertToJaxbGregorianDate1(): Exception occured. " + e);
		}

		return null;

	}

	@SuppressWarnings("deprecation")
	private JAXBElement<XMLGregorianCalendar> convertToJaxbGregorianDateMs(QName qName) {
		JAXBElement<XMLGregorianCalendar> jaxbElement = null;
		GregorianCalendar gregCalendar = new GregorianCalendar();

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT_MS);

		// Here you say to java the initial timezone. This is the secret
		sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE_AUS));
		// Will print in UTC

		XMLGregorianCalendar transDate;
		try {
			transDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCalendar);
			jaxbElement = new JAXBElement<XMLGregorianCalendar>(qName, XMLGregorianCalendar.class, Arrangement.class, transDate);
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			LOGGER.error("CreateIndividualIPDtoServiceImpl.convertToJaxbGregorianDateMs(): Exception occured. " + e);
		}

		return jaxbElement;
	}

	@SuppressWarnings("deprecation")
	private JAXBElement<XMLGregorianCalendar> convertToJaxbGregorianDate(QName qName) {
		JAXBElement<XMLGregorianCalendar> jaxbElement = null;
		GregorianCalendar gregCalendar = new GregorianCalendar();

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);

		// Here you say to java the initial timezone. This is the secret
		sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE_AUS));
		// Will print in UTC

		XMLGregorianCalendar transDate;
		try {
			transDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCalendar);
			jaxbElement = new JAXBElement<XMLGregorianCalendar>(qName, XMLGregorianCalendar.class, Arrangement.class, transDate);
		} catch (DatatypeConfigurationException e) {
			LOGGER.error("CreateIndividualIPDtoServiceImpl.convertToJaxbGregorianDate(): Exception occured. " + e);
		}

		return jaxbElement;
	}

	private XMLGregorianCalendar populateDate(String date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		Date dateObj = null;

		try {
			if (null != date && !date.isEmpty()) {
				dateObj = new Date(date);
			} else {
				DateFormat df = new SimpleDateFormat("YYYYMMDD");
				String strDate = df.format(new Date());
				dateObj = new SimpleDateFormat("YYYYMMDD").parse(strDate);
			}
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(dateObj);
			XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
			return xmlDate;

		} catch (ParseException e1) {
			LOGGER.error("CreateIndividualIPDtoServiceImpl.populateDate(): ParseException occured. " + e1);

		} catch (DatatypeConfigurationException e) {
			LOGGER.error("CreateIndividualIPDtoServiceImpl.populateDate(): Exception occured. " + e);
		}

		return null;
	}
}
