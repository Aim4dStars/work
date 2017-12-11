/**
 * 
 */
package com.bt.nextgen.api.client.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

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

import au.com.westpac.gn.common.xsd.identifiers.v1.EmployeeIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.AddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.Agent;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.ContactMethodUsage;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.DocumentAttribute;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.DocumentAttributeName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.EmploymentDetails;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.IdentityVerificationDocument;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.IndividualIDVAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.IndividualName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.MoneyLaunderingAssessment;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartyidvassessment.xsd.maintainidvdetails.v5.svc0325.RequestAction;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.Arrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.AuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.Action;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.EmailAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.EmailAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.InvolvedPartyType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintainIPContactMethodsRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PriorityLevel;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.TelephoneAddress;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.gesb.maintainidvdetail.v5.MaintainIdvDetailIntegrationService;
import com.bt.nextgen.service.gesb.maintainidvdetail.v5.MaintainIdvRequest;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactIntegrationService;
import com.bt.nextgen.service.gesb.maintainipcontactmethod.v1.MaintainIpContactRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.MaintainIPContactMethodModel;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;

/**
 * @author L081050
 */
@Service("maintainipcontactdtoservice")
@SuppressWarnings("squid:S1200")
public class MaintainIpContactDtoServiceImpl implements
		MaintainIpContactDtoService {
	private static final Logger logger = LoggerFactory
			.getLogger(MaintainIpContactDtoServiceImpl.class);
	private final static String TIMEZONE_AUS = "Australia/Sydney";
	private final static String DATEFORMAT = "yyyy-MM-DD";
	 private final static String DATE_TYPE_START = "start";

	  private final static String DATE_TYPE_END = "end";

	private static String EMAIL = "Email";

	private static String MOBILE = "Mobile";
	private final static String DATE_TYPE_IDV = "idv";

	private final static String DATE_TYPE_BIRTH = "birth";
	private final static QName ARRANGEMENT_QNAME_IDV = new QName(
			"externalIDVDate");

	private final static QName ARRANGEMENT_QNAME_BIRTH = new QName("birthDate");
	@Autowired
	@Qualifier("maintainipcontactintegrationservice")
	private MaintainIpContactIntegrationService maintainIpContactIntegrationService;

	@Override
	public CustomerRawData maintain(MaintainIPContactMethodModel reqModel,
			ServiceErrors serviceErrors) {
		MaintainIpContactRequest maintainIpContactRequest = createRequest(reqModel);
		logger.info("Calling maintainIpContactIntegrationService.maintain");
		CustomerRawData customerData = maintainIpContactIntegrationService
				.maintain(maintainIpContactRequest, serviceErrors);
		return customerData;
	}

	private MaintainIpContactRequest createRequest(
			MaintainIPContactMethodModel maintainIPContactMethodModel) {
		MaintainIpContactRequest maintainIpContactRequest = new MaintainIpContactRequest();
		maintainIpContactRequest.setInvolvedPartyType(InvolvedPartyType.INDIVIDUAL);
		InvolvedPartyIdentifier involvedPartyIdentifier=new InvolvedPartyIdentifier();
		involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
		involvedPartyIdentifier.setInvolvedPartyId(maintainIPContactMethodModel.getCisKey());
		maintainIpContactRequest.setInvolvedPartyIdentifier(involvedPartyIdentifier);
		if(maintainIPContactMethodModel.getAddressType().equalsIgnoreCase(MOBILE))
		{
			maintainIpContactRequest.setHasPhoneAddressContactMethod(getPhoneAddressContact(maintainIPContactMethodModel));		
		}
		else
		{
		maintainIpContactRequest.setHasEmailAddressContactMethod(getEmailAddressContactMethod(maintainIPContactMethodModel));
		}
	
		return maintainIpContactRequest;
	}

	private PhoneAddressContactMethod getPhoneAddressContact(MaintainIPContactMethodModel maintainIPContactMethodModel)
	
	{
		PhoneAddressContactMethod phoneAddressContactMethod=new PhoneAddressContactMethod();
		phoneAddressContactMethod.setRequestedAction(Action.fromValue(maintainIPContactMethodModel.getRequestedAction()));
		phoneAddressContactMethod.setPriorityLevel(PriorityLevel.fromValue(maintainIPContactMethodModel.getPriorityLevel()));
		phoneAddressContactMethod.setUsageId(maintainIPContactMethodModel.getUsageId());
		phoneAddressContactMethod.setStartDate(convertToGregorianDate());
		phoneAddressContactMethod.setValidityStatus(maintainIPContactMethodModel.getValidityStatus());
		phoneAddressContactMethod.setIsActive("true");
		MaintenanceAuditContext maintenanceAuditContext=new MaintenanceAuditContext();
		maintenanceAuditContext.setIsActive(true);
		maintenanceAuditContext.setLastUpdateTimestamp(convertToGregorianDate());
		phoneAddressContactMethod.setAuditContext(maintenanceAuditContext);
		TelephoneAddress telephoneAddress=new TelephoneAddress();
		telephoneAddress.setAreaCode(maintainIPContactMethodModel.getAreaCode());
		telephoneAddress.setContactMedium(maintainIPContactMethodModel.getContactMedium());
		telephoneAddress.setCountryCode(maintainIPContactMethodModel.getCountryCode());
		telephoneAddress.setLocalNumber(maintainIPContactMethodModel.getLocalNumber());
		phoneAddressContactMethod.setHasAddress(telephoneAddress);
		return phoneAddressContactMethod;
	}
	
	private EmailAddressContactMethod getEmailAddressContactMethod(MaintainIPContactMethodModel maintainIPContactMethodModel)
	{
		EmailAddressContactMethod emailAddressContactMethod=new EmailAddressContactMethod();
		emailAddressContactMethod.setRequestedAction(Action.fromValue(maintainIPContactMethodModel.getRequestedAction()));
		MaintenanceAuditContext maintenanceAuditContext=new MaintenanceAuditContext();
		maintenanceAuditContext.setIsActive(true);
		maintenanceAuditContext.setLastUpdateTimestamp(convertToGregorianDate());
		emailAddressContactMethod.setAuditContext(maintenanceAuditContext);
		emailAddressContactMethod.setPriorityLevel(PriorityLevel.fromValue(maintainIPContactMethodModel.getPriorityLevel()));
		emailAddressContactMethod.setValidityStatus(maintainIPContactMethodModel.getValidityStatus());
		emailAddressContactMethod.setUsageId(maintainIPContactMethodModel.getUsageId());
		emailAddressContactMethod.setIsActive("true");
		emailAddressContactMethod.setStartDate(convertToGregorianDate());
		EmailAddress emailAddress=new EmailAddress();
		emailAddress.setEmailAddress(maintainIPContactMethodModel.getEmailAddress());
		emailAddressContactMethod.setHasAddress(emailAddress);
		return emailAddressContactMethod;	
	}
	
	private XMLGregorianCalendar convertToGregorianDate() {
        GregorianCalendar gregCalendar = new GregorianCalendar();    
        Calendar calendar =  GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);

        //Here you say to java the initial timezone. This is the secret
        sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE_AUS));
        //Will print in UTC

        XMLGregorianCalendar transDate;
        try {
            transDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCalendar);
            return transDate;
        } catch (DatatypeConfigurationException e) {
            logger.error("MaintainIpContactDtoServiceImpl.convertToJaxbGregorianDate1(): Exception occured. "
                    + e);
        }

        return null;

    }

}
