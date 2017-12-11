package com.bt.nextgen.api.client.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.com.westpac.gn.common.xsd.identifiers.v1.AccountArrangementIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.ProductSystemIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.Arrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.ArrangementAccessCondition;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.ArrangementChannelAccessCondition;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.Channel;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.ChannelAccessArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.InvolvedPartyArrangementRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.Organisation;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.Product;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.ProductArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainarrangementandiparrangementrelationships.v1.svc0256.ActionCode;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.address.GroupEsbCustomerAddressManagementImpl;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.ArrangementAndRelationshipManagementRequest;
import com.bt.nextgen.service.group.maintainarrangementandiparrangementrelationships.MaintainArrangementAndRelationshipIntegrationService;
import com.bt.nextgen.serviceops.model.MaintainArrangementAndRelationshipReqModel;

@Service("maintainArrangementAndRelationship")
@SuppressWarnings("squid:S1200")
public class MaintainArrangementAndRelationshipServiceImpl implements MaintainArrangementAndRelationshipService {
	private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerAddressManagementImpl.class);
	 
    private static final String IP_AR_SOL = "ipar";

    private static final String IP_SAR = "ipsar";

    private static final String IP_AR_THIRDPARTY = "iparthirdparty";

    private static final String END_DATE_IP_AR_SOL = "enddateiparsol";

    private static final String END_DATE_IP_SAR = "enddateipsar";

    private static final String END_DATE_IP_AR_THIRDPARTY = "enddateiparthirdparty";

    private final static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss.SS'Z'";

    private final static String PRODUCT_SYSTEM_ID = "NG-002";

    private final static String LIFE_CYCLE_STATUS_ACTIVE = "Active";

    private final static String LIFE_CYCLE_STATUS_INACTIVE = "Inactive";

    private final static String ROLE_TYPE_SOL = "SOL";

    private final static String ROLE_TYPE_TP = "Third Party";

    private final static String CHANNEL_TYPE = "ONL";

    private final static String ACCESS_LEVEL = "View";

    private final static String DATE_TYPE_START = "start";

    private final static String DATE_TYPE_END = "end";

    private final static String PERSON_TYPE_INDIVIDUAL = "individual";

    private final static String ACCOUNT_PREFIX = "NG-002-";

    private final static String TIMEZONE_AUS = "Australia/Sydney";

    private final static QName ARRANGEMENT_QNAME_START = new QName(
            "http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/common/xsd/v1/",
            "startDate");

    private final static QName ARRANGEMENT_QNAME_END = new QName(
            "http://www.westpac.com.au/gn/involvedPartyManagement/services/involvedPartyManagement/common/xsd/v1/",
            "endDate");

    @Autowired
    @Qualifier("maintainArrangementAndRelationshipIntegrationService")
    private MaintainArrangementAndRelationshipIntegrationService integrationService;

    @Override
    public CustomerRawData createArrangementAndRelationShip(MaintainArrangementAndRelationshipReqModel input,
            ServiceErrors serviceError) {
        ArrangementAndRelationshipManagementRequest req = createRequestOnUseCase(input);
        req.setRequestedAction(ActionCode.MAINTAIN_ARRANGEMENT_AND_RELATIONSHIPS);
        CustomerRawData customerData = integrationService.createArrangementAndRelationShip(req, serviceError);

        return customerData;
    }

    private ArrangementAndRelationshipManagementRequest createRequestOnUseCase(
            MaintainArrangementAndRelationshipReqModel input) {
        String caseName = input.getUseCase();
        ArrangementAndRelationshipManagementRequest req = null;
        switch (caseName) {
        case IP_AR_SOL:
            req = getReqCaseOne(input);
            break;
        case IP_SAR:
            req = getReqCaseTwo(input);
            break;
        case IP_AR_THIRDPARTY:
            req = getReqCaseThree(input);
            break;
        case END_DATE_IP_AR_SOL:
            req = getReqCaseFour(input);
            break;
        case END_DATE_IP_SAR:
            req = getReqCaseFive(input);
            break;
        case END_DATE_IP_AR_THIRDPARTY:
            req = getReqCaseSix(input);
            break;
        default:
            break;
        }
        return req;
    }
    private  List<ProductArrangement> getListOfArrangement(MaintainArrangementAndRelationshipReqModel input){
        List<ProductArrangement> arrangements = new ArrayList<ProductArrangement>();
        ProductArrangement productArrangement = new ChannelAccessArrangement();
        InvolvedPartyArrangementRole involvedPartyArrangementRole = new InvolvedPartyArrangementRole();
        if(IP_AR_SOL.equalsIgnoreCase(input.getUseCase()) || IP_SAR.equalsIgnoreCase(input.getUseCase()) || IP_AR_THIRDPARTY.equalsIgnoreCase(input.getUseCase()) ){
            involvedPartyArrangementRole.setStartDate(convertToGregorianDate(input.getStartDate(), DATE_TYPE_START,
                    null));
            productArrangement.setStartDate(convertToGregorianDate(input.getStartDate(), DATE_TYPE_START, IP_AR_SOL));
            productArrangement.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
            involvedPartyArrangementRole.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        }else{
            productArrangement.setLifecycleStatus(LIFE_CYCLE_STATUS_INACTIVE); 
            involvedPartyArrangementRole.setLifecycleStatus(LIFE_CYCLE_STATUS_INACTIVE);
        }
        if(END_DATE_IP_AR_THIRDPARTY.equalsIgnoreCase(input.getUseCase())||IP_AR_THIRDPARTY.equalsIgnoreCase(input.getUseCase()) ){
            involvedPartyArrangementRole.setRoleType(ROLE_TYPE_TP);
        }else{
            involvedPartyArrangementRole.setRoleType(ROLE_TYPE_SOL); 
        }
        Product product = new Product();
        ProductSystemIdentifier productSystemIdentifier = new ProductSystemIdentifier();
        productSystemIdentifier.setProductSystemId(PRODUCT_SYSTEM_ID);
        product.setAlternativeIdentifier(productSystemIdentifier);
        productArrangement.setIsBasedOn(product);
        InvolvedPartyIdentifier involvedPartyIdentifier = new InvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setInvolvedPartyId(input.getCisKey());
        if (PERSON_TYPE_INDIVIDUAL.equalsIgnoreCase(input.getPersonType())) {
            Individual individual = new Individual();
            individual.getAlternativeIdentifier().add(involvedPartyIdentifier);
            involvedPartyArrangementRole.setRoleIsPlayedBy(individual);
        } else {
            Organisation organisation = new Organisation();
            organisation.getAlternativeIdentifier().add(involvedPartyIdentifier);
            involvedPartyArrangementRole.setRoleIsPlayedBy(organisation);
        }
        productArrangement.setLifecycleStatusReason(input.getLifecycleStatusReason());
        productArrangement.getHasArrangementRole().add(involvedPartyArrangementRole);
        arrangements.add(productArrangement);
        return arrangements;
    }
    private ArrangementAndRelationshipManagementRequest getReqCaseOne(MaintainArrangementAndRelationshipReqModel input) {
        logger.info("Creating maintainArrangementRequest for UseCase one");
        ArrangementAndRelationshipManagementRequest arrangementAndRelationshipReq =
                new ArrangementAndRelationshipManagementRequest();
        List<ProductArrangement> arrangements = getListOfArrangement(input);
        ProductArrangement productArrangement = arrangements.get(0);
        AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
        accountArrangementIdentifier.setCanonicalProductCode(input.getProductCpc());
        accountArrangementIdentifier.setAccountNumber(input.getAccountNumber());
        accountArrangementIdentifier.setBsbNumber(input.getBsbNumber());
        productArrangement.setExternalIdentifier(accountArrangementIdentifier);
        productArrangement.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        arrangements.remove(0);
        arrangements.add(productArrangement);
        arrangementAndRelationshipReq.setArrangement(arrangements);
        return arrangementAndRelationshipReq;
    }

    private ArrangementAndRelationshipManagementRequest getReqCaseTwo(MaintainArrangementAndRelationshipReqModel input) {
        logger.info("Creating maintainArrangementRequest for UseCase two");
        ArrangementAndRelationshipManagementRequest arrangementAndRelationshipReq =
                new ArrangementAndRelationshipManagementRequest();
        List<ProductArrangement> arrangements = getListOfArrangement(input);
        ProductArrangement productArrangement = arrangements.get(0);
        AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
        accountArrangementIdentifier.setCanonicalProductCode(input.getProductCpc());
        accountArrangementIdentifier.setAccountNumber(ACCOUNT_PREFIX + input.getPanNumber());
        productArrangement.setExternalIdentifier(accountArrangementIdentifier);
        productArrangement.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        arrangements.remove(0);
        arrangements.add(productArrangement);
        arrangementAndRelationshipReq.setArrangement(arrangements);
        return arrangementAndRelationshipReq;
    }

    private ArrangementAndRelationshipManagementRequest getReqCaseThree(MaintainArrangementAndRelationshipReqModel input) {
        logger.info("Creating maintainArrangementRequest for UseCase three");
        ArrangementAndRelationshipManagementRequest arrangementAndRelationshipReq =
                new ArrangementAndRelationshipManagementRequest();
        List<ProductArrangement> arrangements = getListOfArrangement(input);
        ProductArrangement productArrangement = arrangements.get(0);
        InvolvedPartyArrangementRole involvedPartyArrangementRole = productArrangement.getHasArrangementRole().get(0);
         ArrangementChannelAccessCondition arrangementChannelAccessCondition = new ArrangementChannelAccessCondition();
        ArrangementAccessCondition arrangementAccessCondition = new ArrangementAccessCondition();
        arrangementAccessCondition.setStartDate(convertToGregorianDate(input.getStartDate(), DATE_TYPE_START,
                IP_AR_THIRDPARTY));
        arrangementAccessCondition.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        arrangementAccessCondition.setAccessLevel(ACCESS_LEVEL);
        Channel channel = new Channel();
        channel.setChannelType(CHANNEL_TYPE);
        arrangementChannelAccessCondition.setChannel(channel);
        arrangementChannelAccessCondition.getHasArrangementAuthority().add(arrangementAccessCondition);
        involvedPartyArrangementRole.getHasAccessCondition().add(arrangementChannelAccessCondition);
        productArrangement.getHasArrangementRole().remove(0);
        productArrangement.getHasArrangementRole().add(involvedPartyArrangementRole);
        AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
        accountArrangementIdentifier.setCanonicalProductCode(input.getProductCpc());
        accountArrangementIdentifier.setAccountNumber(input.getAccountNumber());
        accountArrangementIdentifier.setBsbNumber(input.getBsbNumber());
        productArrangement.setExternalIdentifier(accountArrangementIdentifier);
        productArrangement.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        arrangements.remove(0);
        arrangements.add(productArrangement);
        arrangementAndRelationshipReq.setArrangement(arrangements);
        return arrangementAndRelationshipReq;
    }

    private ArrangementAndRelationshipManagementRequest getReqCaseFour(MaintainArrangementAndRelationshipReqModel input) {
        logger.info("Creating maintainArrangementRequest for UseCase four");
        ArrangementAndRelationshipManagementRequest arrangementAndRelationshipReq =
                new ArrangementAndRelationshipManagementRequest();
        List<ProductArrangement> arrangements = getListOfArrangement(input);
        ProductArrangement productArrangement = arrangements.get(0);
        MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
        maintenanceAuditContext.setVersionNumber(input.getVersionNumberAr());
        productArrangement.setAuditContext(maintenanceAuditContext);
               InvolvedPartyArrangementRole involvedPartyArrangementRole = productArrangement.getHasArrangementRole().get(0);
        involvedPartyArrangementRole.setEndDate(convertToGregorianDate(input.getStartDate(), DATE_TYPE_END, END_DATE_IP_AR_SOL));
        involvedPartyArrangementRole.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        productArrangement.getHasArrangementRole().remove(0);
         productArrangement.getHasArrangementRole().add(involvedPartyArrangementRole);
        AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
        accountArrangementIdentifier.setCanonicalProductCode(input.getProductCpc());
        accountArrangementIdentifier.setAccountNumber(input.getAccountNumber());
        accountArrangementIdentifier.setBsbNumber(input.getBsbNumber());
        productArrangement.setExternalIdentifier(accountArrangementIdentifier);
        arrangements.remove(0);
        arrangements.add(productArrangement);
        arrangementAndRelationshipReq.setArrangement(arrangements);
        return arrangementAndRelationshipReq;
    }

    private ArrangementAndRelationshipManagementRequest getReqCaseFive(MaintainArrangementAndRelationshipReqModel input) {
        logger.info("Creating maintainArrangementRequest for UseCase five");
        ArrangementAndRelationshipManagementRequest arrangementAndRelationshipReq =
                new ArrangementAndRelationshipManagementRequest();
        List<ProductArrangement> arrangements = getListOfArrangement(input);
        ProductArrangement productArrangement = arrangements.get(0);
        MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
        maintenanceAuditContext.setVersionNumber(input.getVersionNumberAr());
        productArrangement.setAuditContext(maintenanceAuditContext);
        InvolvedPartyArrangementRole involvedPartyArrangementRole = productArrangement.getHasArrangementRole().get(0);
        involvedPartyArrangementRole.setEndDate(convertToGregorianDate(input.getStartDate(), DATE_TYPE_END, END_DATE_IP_SAR));
        productArrangement.getHasArrangementRole().remove(0);
        productArrangement.getHasArrangementRole().add(involvedPartyArrangementRole);
        AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
        accountArrangementIdentifier.setCanonicalProductCode(input.getProductCpc());
        accountArrangementIdentifier.setAccountNumber(ACCOUNT_PREFIX + input.getPanNumber());
        productArrangement.setExternalIdentifier(accountArrangementIdentifier);
        productArrangement.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        arrangements.remove(0);
        arrangements.add(productArrangement);
        arrangementAndRelationshipReq.setArrangement(arrangements);
        return arrangementAndRelationshipReq;
    }

    private ArrangementAndRelationshipManagementRequest getReqCaseSix(MaintainArrangementAndRelationshipReqModel input) {
        logger.info("Creating maintainArrangementRequest for UseCase six");
        List<ProductArrangement> arrangements = getListOfArrangement(input);
        ProductArrangement productArrangement = arrangements.get(0);
        MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
        maintenanceAuditContext.setVersionNumber(input.getVersionNumberIpAr());
        productArrangement.setAuditContext(maintenanceAuditContext);     
        MaintenanceAuditContext maintenanceAuditContextArrange = new MaintenanceAuditContext();
        maintenanceAuditContextArrange.setVersionNumber(input.getVersionNumberIpAr());
        InvolvedPartyArrangementRole involvedPartyArrangementRole = productArrangement.getHasArrangementRole().get(0);
        involvedPartyArrangementRole.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        involvedPartyArrangementRole.setAuditContext(maintenanceAuditContextArrange);
        ArrangementChannelAccessCondition arrangementChannelAccessCondition = new ArrangementChannelAccessCondition();
        ArrangementAccessCondition arrangementAccessCondition = new ArrangementAccessCondition();
        arrangementAccessCondition.setStartDate(convertToGregorianDate(input.getStartDate(), DATE_TYPE_START, END_DATE_IP_AR_THIRDPARTY));
        arrangementAccessCondition.setEndDate(convertToGregorianDate(input.getStartDate(), DATE_TYPE_END, END_DATE_IP_AR_THIRDPARTY));
        arrangementAccessCondition.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        arrangementAccessCondition.setAccessLevel(ACCESS_LEVEL);
        Channel channel = new Channel();
        channel.setChannelType(CHANNEL_TYPE);
        arrangementChannelAccessCondition.setChannel(channel);
        arrangementChannelAccessCondition.getHasArrangementAuthority().add(arrangementAccessCondition);
        involvedPartyArrangementRole.getHasAccessCondition().add(arrangementChannelAccessCondition);
        productArrangement.getHasArrangementRole().remove(0);
        productArrangement.getHasArrangementRole().add(involvedPartyArrangementRole);
        AccountArrangementIdentifier accountArrangementIdentifier = new AccountArrangementIdentifier();
        accountArrangementIdentifier.setCanonicalProductCode(input.getProductCpc());
        accountArrangementIdentifier.setAccountNumber(input.getAccountNumber());
        accountArrangementIdentifier.setBsbNumber(input.getBsbNumber());
        productArrangement.setExternalIdentifier(accountArrangementIdentifier);
        productArrangement.setLifecycleStatus(LIFE_CYCLE_STATUS_ACTIVE);
        arrangements.remove(0);
        arrangements.add(productArrangement);
        ArrangementAndRelationshipManagementRequest arrangementAndRelationshipReq=new ArrangementAndRelationshipManagementRequest();
        arrangementAndRelationshipReq.setArrangement(arrangements);
        return arrangementAndRelationshipReq;
    }

	private JAXBElement<XMLGregorianCalendar> convertToGregorianDate(
			String date, String dateType, String useCase) {
		JAXBElement<XMLGregorianCalendar> jaxbElement = null;
		Date txnDate = null;
		try {
			if (END_DATE_IP_AR_THIRDPARTY.equalsIgnoreCase(useCase) && DATE_TYPE_START.equalsIgnoreCase(dateType)) {
				txnDate = new Date(date);
			} else {
				txnDate = new Date();
			}
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(txnDate);
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
			dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_AUS));
			XMLGregorianCalendar transDate;
			transDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					calendar);

			if (DATE_TYPE_START.equalsIgnoreCase(dateType)) {
				jaxbElement = new JAXBElement<XMLGregorianCalendar>(
						ARRANGEMENT_QNAME_START, XMLGregorianCalendar.class,
						Arrangement.class, transDate);
			} else {
				jaxbElement = new JAXBElement<XMLGregorianCalendar>(
						ARRANGEMENT_QNAME_END, XMLGregorianCalendar.class,
						Arrangement.class, transDate);
			}
			return jaxbElement;
		} catch (DatatypeConfigurationException e) {
			logger.error("MaintainArrangementAndRelationshipServiceImpl.convertToGregorianDate(): Exception occured. "
					+ e.getMessage());
		}
		return null;
	}
}
