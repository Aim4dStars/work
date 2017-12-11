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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationNumberType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.Characteristic;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.CharacteristicsType;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.GeographicArea;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.IndustryClassification;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.InvolvedPartyName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.InvolvedPartyNameAddressee;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.InvolvedPartyRole;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.Organisation;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.PriorityLevel;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.PurposeOfBusinessRelationship;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.RegistrationArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.SourceOfFunds;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.SourceOfWealth;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createorganisationip.v5.svc0337.StandardPostalAddress;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.groupesb.createorganisationip.v5.CreateOrganisationIPIntegrationService;
import com.bt.nextgen.service.groupesb.createorganisationip.v5.CreateOrganisationIPReq;
import com.bt.nextgen.serviceops.controller.ServiceOpsController;
import com.bt.nextgen.serviceops.model.CreateOraganisationIPReqModel;

@Service("createOrganisationIPDataDtoService")
@SuppressWarnings("squid:S1200")
public class CreateOrganisationIPDataDtoServiceImpl implements CreateOrganisationIPDataDtoService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceOpsController.class);

    private static final String LIFE_CYCLE_STATUS = "P";

    private final static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss.SS'Z'";

    private final static String TIMEZONE_AUS = "Australia/Sydney";

    private final static String STATUS = "C";
    
    private final static String HAS_CHAR = "yes";
    
    private final static String SELECT = "select";

    private final static boolean IS_ACTIVE = true;

    private final static String DATE_TYPE = "frn";
    
    private final static String NO_TIMEZONE ="noTimeZone";

    private final static QName START_DATE = new QName("startDate");

    private final static QName LAST_UPDATE_TIME = new QName("lastUpdateTimestamp");

    @Autowired
    @Qualifier("createorganisationipintegrationservice")
    private CreateOrganisationIPIntegrationService createOrganisationIPIntegrationService;

    @Override
    public CustomerRawData create(CreateOraganisationIPReqModel reqModel, ServiceErrors serviceErrors) {
        CreateOrganisationIPReq request = createOrganisationIPRequest(reqModel);
        logger.info("Calling CreateOrganisationIPDataDtoService.createOrganisationIPRequest");
        CustomerRawData customerData =
                createOrganisationIPIntegrationService.createorganisationIP(request, serviceErrors);

        return customerData;
    }

    private CreateOrganisationIPReq createOrganisationIPRequest(CreateOraganisationIPReqModel reqModel) {
        logger.info("Calling CreateOrganisationIPIntegrationService.createOrganisationIPRequest");
        CreateOrganisationIPReq createOrganisationIPRequest = new CreateOrganisationIPReq();
        Organisation organisation = new Organisation();
        organisation.setIsActive(IS_ACTIVE);
        InvolvedPartyName involvedPartyName = new InvolvedPartyName();
        involvedPartyName.setFullName(reqModel.getFullName());
        organisation.setHasForName(involvedPartyName);
        organisation.setLifecycleStatus(LIFE_CYCLE_STATUS);
        InvolvedPartyRole InvolvedPartyRole = new InvolvedPartyRole();
        InvolvedPartyRole.setRoleType(reqModel.getPersonType());
        organisation.setIsPlayingRole(InvolvedPartyRole);
        organisation.setIsForeignRegistered(reqModel.getIsForeignRegistered());
        organisation.getHasRegistration().add(getRegistrationArrangement(reqModel));
        organisation.setHasIndustryClassification(getIndustryClassification(reqModel));
        organisation.getHasPostalAddressContactMethod().add(getPostalAddressContactMethod(reqModel));
        organisation.setOrganisationLegalStructure(getCharacteristicsType(reqModel));
        organisation.getPurposeOfBusinessRelationship().add(getPurposeOfBusinessRelationship(reqModel));
        organisation.getSourceOfFunds().add(getSourceOfFunds(reqModel));
        organisation.getSourceOfWealth().add(getSourceOfWealth(reqModel));
        organisation.getHasCharacteristic().add(getCharacteristic(reqModel));
        organisation.getHasForeignRegistration().add(getRegistrationArrangements(reqModel));
        createOrganisationIPRequest.setOragnaisation(organisation);
        return createOrganisationIPRequest;
    }

    private RegistrationArrangement getRegistrationArrangements(CreateOraganisationIPReqModel reqModel) {
        RegistrationArrangement registrationArrangement = new RegistrationArrangement();
        MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
        maintenanceAuditContext.setIsActive(IS_ACTIVE);
        maintenanceAuditContext.setLastUpdateTimestamp(convertToXMLGregorianDate(LAST_UPDATE_TIME,NO_TIMEZONE,""));
        registrationArrangement.setAuditContext(maintenanceAuditContext);
        RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
        registrationIdentifier.setRegistrationNumber(reqModel.getFrn());
        if(!SELECT.equalsIgnoreCase(reqModel.getFrntype())){
        registrationIdentifier.setRegistrationNumberType(RegistrationNumberType.fromValue(reqModel.getFrntype()));
        registrationArrangement.setRegistrationIdentifier(registrationIdentifier);
        }
        
        registrationArrangement.setStartDate(convertToGregorianDate(null, DATE_TYPE));
        ;
        return registrationArrangement;
    }

    private Characteristic getCharacteristic(CreateOraganisationIPReqModel reqModel) {
        Characteristic characteristic = new Characteristic();
        MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
        maintenanceAuditContext.setIsActive(IS_ACTIVE);
        maintenanceAuditContext.setLastUpdateTimestamp(convertToXMLGregorianDate(LAST_UPDATE_TIME,NO_TIMEZONE,""));
        characteristic.setAuditContext(maintenanceAuditContext);
        characteristic.setStartDate(convertToXMLGregorianDate(START_DATE,NO_TIMEZONE,HAS_CHAR));
        characteristic.setCharacteristicType(reqModel.getCharacteristicType());
        characteristic.setCharacteristicCode(reqModel.getCharacteristicCode());
        characteristic.setCharacteristicValue(reqModel.getCharacteristicValue());
        return characteristic;
    }

    private SourceOfWealth getSourceOfWealth(CreateOraganisationIPReqModel reqModel) {
        SourceOfWealth sourceOfWealth = new SourceOfWealth();
        sourceOfWealth.setStartDate(convertToXMLGregorianDate(START_DATE,"",""));
        sourceOfWealth.setValue(reqModel.getSourceOfWealth());
        return sourceOfWealth;
    }

    private SourceOfFunds getSourceOfFunds(CreateOraganisationIPReqModel reqModel) {
        SourceOfFunds sourceOfFunds = new SourceOfFunds();
        sourceOfFunds.setStartDate(convertToXMLGregorianDate(START_DATE,"",""));
        sourceOfFunds.setValue(reqModel.getSourceOfFunds());
        return sourceOfFunds;
    }

    private PurposeOfBusinessRelationship getPurposeOfBusinessRelationship(CreateOraganisationIPReqModel reqModel) {
        PurposeOfBusinessRelationship purposeOfBusinessRelationship = new PurposeOfBusinessRelationship();
        purposeOfBusinessRelationship.setStartDate(convertToXMLGregorianDate(START_DATE,"",""));
        purposeOfBusinessRelationship.setValue(reqModel.getPurposeOfBusinessRelationship());
        return purposeOfBusinessRelationship;
    }

    private CharacteristicsType getCharacteristicsType(CreateOraganisationIPReqModel reqModel) {
        CharacteristicsType characteristicsType = new CharacteristicsType();
        MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
        maintenanceAuditContext.setIsActive(IS_ACTIVE);
        maintenanceAuditContext.setLastUpdateTimestamp(convertToXMLGregorianDate(LAST_UPDATE_TIME,NO_TIMEZONE,""));
        characteristicsType.setAuditContext(maintenanceAuditContext);
        characteristicsType.setValue(reqModel.getOrganisationLegalStructureValue());

        return characteristicsType;
    }

    private PostalAddressContactMethod getPostalAddressContactMethod(CreateOraganisationIPReqModel reqModel) {
        PostalAddressContactMethod postalAddressContactMethod = new PostalAddressContactMethod();
        postalAddressContactMethod.setStartDate(convertToXMLGregorianDate(START_DATE,NO_TIMEZONE,""));
        postalAddressContactMethod.setValidityStatus(STATUS);
        if(!SELECT.equalsIgnoreCase(reqModel.getUsage())){
        postalAddressContactMethod.setUsage(reqModel.getUsage());
        }
        if(!SELECT.equalsIgnoreCase(reqModel.getAddrspriorityLevel())){
        postalAddressContactMethod.setPriorityLevel(PriorityLevel.fromValue(reqModel.getAddrspriorityLevel()));
        }
        InvolvedPartyNameAddressee involvedPartyNameAddressee = new InvolvedPartyNameAddressee();
        involvedPartyNameAddressee.getNameText().add(reqModel.getAddresseeNameText());
        postalAddressContactMethod.setAddressee(involvedPartyNameAddressee);
        StandardPostalAddress postalAddress = new StandardPostalAddress();
        postalAddress.setStreetNumber(reqModel.getStreetNumber());
        postalAddress.setStreetName(reqModel.getStreetName());
        postalAddress.setStreetType(reqModel.getStreetType());
        postalAddress.setCity(reqModel.getCity());
        postalAddress.setState(reqModel.getState());
        postalAddress.setPostCode(reqModel.getPostCode());
        postalAddress.setCountry(reqModel.getCountry());
        postalAddressContactMethod.setHasAddress(postalAddress);
        return postalAddressContactMethod;
    }

    private IndustryClassification getIndustryClassification(CreateOraganisationIPReqModel reqModel) {
        IndustryClassification industryClassification = new IndustryClassification();
        industryClassification.setIndustryCode(reqModel.getIndustryCode());
        if(!SELECT.equalsIgnoreCase(reqModel.getPriorityLevel())){
        industryClassification.setPriorityLevel(PriorityLevel.fromValue(reqModel.getPriorityLevel()));
        }
        return industryClassification;
    }

    private RegistrationArrangement getRegistrationArrangement(CreateOraganisationIPReqModel reqModel) {
        RegistrationArrangement registrationArrangement = new RegistrationArrangement();
        registrationArrangement.setEffectiveDate(convertToGregorianDate(reqModel.getEffectiveStartDate(),null));
        RegistrationIdentifier registrationIdentifier = new RegistrationIdentifier();
        registrationIdentifier.setRegistrationNumber(reqModel.getRegistrationNumber());
        if(!SELECT.equalsIgnoreCase(reqModel.getRegistrationNumberType())){
        registrationIdentifier.setRegistrationNumberType(RegistrationNumberType.fromValue(reqModel
                .getRegistrationNumberType()));
        registrationArrangement.setRegistrationIdentifier(registrationIdentifier);
        }
        GeographicArea geographicArea = new GeographicArea();
        geographicArea.setCountry(reqModel.getIsIssuedAtC());
        geographicArea.setState(reqModel.getIsIssuedAtS());
        registrationArrangement.setIsIssuedAt(geographicArea);
        return registrationArrangement;
    }
    
    private XMLGregorianCalendar convertToGregorianDate(String date, String type) {

        Date txnDate = null;
        XMLGregorianCalendar transDate = null;
        try {
            if( null !=date) {
                txnDate = new Date(date);
            } else {
                    DateFormat df = new SimpleDateFormat("YYYYMMDD");
                    String strDate = df.format(new Date());

                    txnDate = new SimpleDateFormat("YYYYMMDD").parse(strDate);
                
            }

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(txnDate);

            transDate =
                    DatatypeFactory.newInstance().newXMLGregorianCalendarDate(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                            DatatypeConstants.FIELD_UNDEFINED);

        } catch (ParseException e1) {
            logger.error("CreateOrganisationIPIntegrationService.convertToGregorianDate(): ParseException occured. " + e1.getMessage());

        } catch (DatatypeConfigurationException e) {
            logger.error("CreateOrganisationIPIntegrationService.convertToGregorianDate(): Exception occured. "
                    + e);
        }
        return transDate;
    }

    private JAXBElement<XMLGregorianCalendar> convertToXMLGregorianDate(QName qName , String type,String hasChar) {
        JAXBElement<XMLGregorianCalendar> jaxbElement = null;
        Date txnDate = null;
        try {
            txnDate = new Date();
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(txnDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
            dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE_AUS));
            XMLGregorianCalendar transDate;
            if(HAS_CHAR.equalsIgnoreCase(hasChar)){
            transDate =
                    DatatypeFactory.newInstance().newXMLGregorianCalendarDate(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                            DatatypeConstants.FIELD_UNDEFINED);
            }else{
           
            transDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            }
            if("noTimeZone".equalsIgnoreCase(type)){
            transDate.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
            }
            jaxbElement =
                    new JAXBElement<XMLGregorianCalendar>(qName, XMLGregorianCalendar.class, Organisation.class,
                            transDate);
            return jaxbElement;
        } catch (DatatypeConfigurationException e) {
            logger.error("CreateOrganisationIPIntegrationService.convertToGregorianDate(): Exception occured. "
                    + e);
        }
        return jaxbElement;
    }
}
