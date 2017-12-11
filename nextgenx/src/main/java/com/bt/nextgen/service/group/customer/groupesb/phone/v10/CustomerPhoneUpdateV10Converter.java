package com.bt.nextgen.service.group.customer.groupesb.phone.v10;

import au.com.westpac.gn.common.xsd.identifiers.v1.RowSetItemIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.Action;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.ObjectFactory;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PriorityLevel;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.TelephoneAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.RoleType;
import com.bt.nextgen.service.group.customer.groupesb.phone.CustomerPhone;
import com.bt.nextgen.service.integration.domain.Phone;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by F057654 on 3/09/2015.
 */
@SuppressWarnings({"squid:S1200", "squid:MethodCyclomaticComplexity", "squid:S1142"})
public final class CustomerPhoneUpdateV10Converter {

    private CustomerPhoneUpdateV10Converter() {}

    private static final Logger logger = LoggerFactory.getLogger(CustomerPhoneUpdateV10Converter.class);

    public static List<au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod>
                convertResponseInPhone(CustomerData customerData,  RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse) {

        ObjectFactory objectFactory = new ObjectFactory();
        List<PhoneAddressContactMethod> allPhoneAddressToBeSentInReqList = new ArrayList<>();
        List<au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PhoneAddressContactMethod> cachedPhoneResponse;

        if(customerData.getRequest().getInvolvedPartyRoleType().equals(RoleType.INDIVIDUAL)) {
            cachedPhoneResponse = cachedResponse.getIndividual().getHasPhoneAddressContactMethod();
        }
        else {
            cachedPhoneResponse = cachedResponse.getOrganisation().getHasPhoneAddressContactMethod();
        }

        //create the list the needs to be processed for Phone Add/Modify/Delete
        List<Phone> phoneToBeUpdatedInGCM = customerData.getPhoneNumbers();

        for (Phone phone : phoneToBeUpdatedInGCM) {
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PhoneAddressContactMethod phoneAddressContactMethod = objectFactory.createPhoneAddressContactMethod();
            CustomerPhone customerPhone = (CustomerPhone)phone;

            switch (customerPhone.getAction()) {
                 case ADD:
                     phoneAddressContactMethod = createAddPhoneRequest(phoneAddressContactMethod, objectFactory, customerPhone);
                    break;

                 case DELETE:
                     phoneAddressContactMethod = deletePhoneAddress(phoneAddressContactMethod, cachedPhoneResponse, objectFactory, customerPhone);
                    break;

                default:
                    logger.error("Not supportive operation for gcm Phone update");
            }

            if(phoneAddressContactMethod.getHasAddress() != null){
                allPhoneAddressToBeSentInReqList.add(phoneAddressContactMethod);
            }

        }
        
        return allPhoneAddressToBeSentInReqList;
    }

    /**
     * Method to create request for Add Phone
     * @param phoneAddressContactMethod
     * @param objectFactory
     * @param phone
     */
    private static PhoneAddressContactMethod createAddPhoneRequest(PhoneAddressContactMethod phoneAddressContactMethod, ObjectFactory objectFactory, CustomerPhone phone) {
        logger.info("Creating ADD request for phone number");
        phoneAddressContactMethod.setRequestedAction(Action.ADD);

        MaintenanceAuditContext maintenanceAudiContext = objectFactory.createMaintenanceAuditContext();
        maintenanceAudiContext.setIsActive(Boolean.TRUE);
        phoneAddressContactMethod.setAuditContext(maintenanceAudiContext);

        phoneAddressContactMethod.setStartDate(DateUtil.convertDateInGregorianCalendar(new Date()));
        phoneAddressContactMethod.setValidityStatus("C");

        phoneAddressContactMethod.setHasAddress(createPhoneAddressContactMethodForAdd(phone, objectFactory));

        //creates the usage and contact medium for address update
        CustomerPhoneV10Converter.createAddressMediumForUpdate(phone, phoneAddressContactMethod);
        phoneAddressContactMethod.setRowSetIdIdentifier(createRowSetIdentifierForAdd(phone));

        logger.info("Completed creating ADD request for phone number");

        return phoneAddressContactMethod;
    }

    /**
     * Method to create request for ADD Phone
     * @param phone
     * @param objectFactory
     */
    private static TelephoneAddress createPhoneAddressContactMethodForAdd(CustomerPhone phone, ObjectFactory objectFactory){
        TelephoneAddress phoneAddress = objectFactory.createTelephoneAddress();

        if(StringUtils.isNotBlank(phone.getCountryCode())) {
            phoneAddress.setCountryCode(phone.getCountryCode());
        }

        if(StringUtils.isNotBlank(phone.getAreaCode())) {
            phoneAddress.setAreaCode(phone.getAreaCode());
        }

        if(StringUtils.isNotBlank(phone.getNumber())) {
            phoneAddress.setLocalNumber(phone.getNumber());
        }

        logger.info("Created Add phone number {}",phone.getNumber());
        return phoneAddress;
    }

    /**
     * Method to create row set identifier for Add phone number
     * @param phone
     * @return
     */
    private static RowSetItemIdentifier createRowSetIdentifierForAdd(CustomerPhone phone){
        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        RowSetItemIdentifier rowIde = factory.createRowSetItemIdentifier();

        //Combine all the values to get the contact identifier
        String identifier = phone.getNumber();
        rowIde.setSequenceNumber(identifier);
        return rowIde;
    }

    /**
     * Method to create
     * @param objectFactory
     */
    private static PhoneAddressContactMethod deletePhoneAddress(
            PhoneAddressContactMethod phoneAddressContactMethod,
            List<au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PhoneAddressContactMethod> cachedPhoneResponse,
            ObjectFactory objectFactory, CustomerPhone customerPhone) {

        logger.info("Creating DELETE request for phone number");
        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PhoneAddressContactMethod cachedPhoneAddressContactMethod = null;
        logger.info("Size of the cached phone response {}",cachedPhoneResponse.size());
        for (au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PhoneAddressContactMethod phone : cachedPhoneResponse) {
            logger.info("Identifier received from the UI {}",customerPhone.getModificationSeq());
            if(phone.getContactMethodIdentifier() != null && phone.getContactMethodIdentifier().getContactMethodId().equals(customerPhone.getModificationSeq())){
                logger.info("Identifier matched for deleting the phone {}", phone.getContactMethodIdentifier().getContactMethodId());
                cachedPhoneAddressContactMethod = phone;

                phoneAddressContactMethod.setRequestedAction(Action.DELETE);

                MaintenanceAuditContext maintenanceAudiContext = objectFactory.createMaintenanceAuditContext();
                maintenanceAudiContext.setIsActive(Boolean.FALSE);
                if(cachedPhoneAddressContactMethod.getAuditContext() != null && StringUtils.isNotBlank(cachedPhoneAddressContactMethod.getAuditContext().getVersionNumber())) {
                    maintenanceAudiContext.setVersionNumber(cachedPhoneAddressContactMethod.getAuditContext().getVersionNumber());
                }
                phoneAddressContactMethod.setAuditContext(maintenanceAudiContext);

                phoneAddressContactMethod.setInternalIdentifier(cachedPhoneAddressContactMethod.getContactMethodIdentifier());
                phoneAddressContactMethod.setUsageId(cachedPhoneAddressContactMethod.getUsage());

                if(cachedPhoneAddressContactMethod.getStartDate() != null) {
                    phoneAddressContactMethod.setStartDate(cachedPhoneAddressContactMethod.getStartDate().getValue());
                }
                phoneAddressContactMethod.setEndDate(DateUtil.convertDateInGregorianCalendar(new Date()));
                phoneAddressContactMethod.setValidityStatus(cachedPhoneAddressContactMethod.getValidityStatus());

                if(cachedPhoneAddressContactMethod.getPriorityLevel() != null){
                    phoneAddressContactMethod.setPriorityLevel(PriorityLevel.fromValue(cachedPhoneAddressContactMethod.getPriorityLevel().value()));
                }

                createAddressForDelete(phoneAddressContactMethod, cachedPhoneAddressContactMethod);
            }
        }

        logger.info("Completed creating DELETE request for phone number");
        return phoneAddressContactMethod;
    }

    /**
     * Method to create Address for deletion
     * @param phoneAddressContactMethod
     * @param cachedPhone
     */
    private static void createAddressForDelete(
            PhoneAddressContactMethod phoneAddressContactMethod,
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PhoneAddressContactMethod cachedPhone) {
        if(cachedPhone.getHasAddress() != null){
            TelephoneAddress telephoneAddress = new TelephoneAddress();
            telephoneAddress.setAreaCode(cachedPhone.getHasAddress().getAreaCode());
            telephoneAddress.setCountryCode(cachedPhone.getHasAddress().getCountryCode());
            telephoneAddress.setLocalNumber(cachedPhone.getHasAddress().getLocalNumber());
            telephoneAddress.setContactMedium(cachedPhone.getContactMedium());

            if(StringUtils.isNotBlank(cachedPhone.getHasAddress().getFullTelephoneNumber())){
                telephoneAddress.setFullTelephoneNumber(cachedPhone.getHasAddress().getFullTelephoneNumber());
            }
            phoneAddressContactMethod.setHasAddress(telephoneAddress);
        }
    }


    /**
     * Method to create phone list for gcm update
     * @param userUpdatedPhoneList
     * @param gcmCachedPhoneData
     */

    /*private static List<Phone> createPhoneListForGCMUpdate(List<Phone> userUpdatedPhoneList, List<Phone> gcmCachedPhoneData){
        List<Phone> processedPhones = new ArrayList<>();
        List<Phone> uniqueGCMPhones = filterDuplicateAndKeepGCMPhone(userUpdatedPhoneList);

        //contains the customerphone
        filterMatchingPhonesAndProcess(uniqueGCMPhones, gcmCachedPhoneData);

        List<CustomerPhone> customerPhoneToBeAdded = convertPhoneToCustomerPhone(uniqueGCMPhones, PhoneAction.ADD);
        processedPhones.addAll(customerPhoneToBeAdded);

        List<CustomerPhone> customerPhoneToBeDeleted = convertPhoneToCustomerPhone(gcmCachedPhoneData, PhoneAction.DELETE);
        processedPhones.addAll(customerPhoneToBeDeleted);

        return processedPhones;
    }

    private static void filterMatchingPhonesAndProcess(List<Phone> uniqueGCMPhones, List<Phone> gcmCachedPhoneData){
        List<Phone> tempList = new ArrayList<>();
        tempList.addAll(uniqueGCMPhones);

        List<Phone> tempGcmDataList = new ArrayList<>();
        tempGcmDataList.addAll(gcmCachedPhoneData);

        for(Phone phone : tempList){
            for(Phone tempCachedPhone : tempGcmDataList){
                boolean isPhoneSame = comparePhones(phone, tempCachedPhone);
                if(isPhoneSame){
                    uniqueGCMPhones.remove(phone);
                    gcmCachedPhoneData.remove(tempCachedPhone);
                }
            }
        }
    }

    private static boolean comparePhones(Phone userUpdatedPhone, Phone cachedPhone){
        if (!userUpdatedPhone.getCountryCode().equalsIgnoreCase(cachedPhone.getCountryCode()))
            return false;
        if (!userUpdatedPhone.getAreaCode().equalsIgnoreCase(cachedPhone.getAreaCode()))
            return false;
        if (!userUpdatedPhone.getNumber().equalsIgnoreCase(cachedPhone.getNumber()))
            return false;
       return true;
    }

    private static List<CustomerPhone> convertPhoneToCustomerPhone(List<Phone> phoneList, PhoneAction action){
        List<CustomerPhone> phoneToBeActioned = new ArrayList<>();

        for(Phone phone : phoneList){
            CustomerPhone customerPhone = new CustomerPhone();
            customerPhone.setAreaCode(phone.getAreaCode());
            customerPhone.setCountryCode(phone.getCountryCode());
            customerPhone.setNumber(phone.getNumber());
            if(StringUtils.isNotBlank(phone.getModificationSeq())) {
                customerPhone.setModificationSeq(phone.getModificationSeq());
            }
            customerPhone.setType(phone.getType());
            customerPhone.setAction(action);
            phoneToBeActioned.add(customerPhone);
        }
        return phoneToBeActioned;
    }

    private static List<Phone> filterDuplicateAndKeepGCMPhone(List<Phone> userUpdatedPhoneList){
        List<Phone> uniqueGCMPhones = new ArrayList<>();

        for(Phone phone : userUpdatedPhoneList){
            CustomerPhone customerPhone = (CustomerPhone)phone;
            if(!uniqueGCMPhones.contains(customerPhone)){
                    uniqueGCMPhones.add(customerPhone);
            }
            else{
                    uniqueGCMPhones.remove(customerPhone);
                    uniqueGCMPhones.add(customerPhone);
            }
        }

        if(uniqueGCMPhones.size() >= MAX_NUMBER_OF_PHONES){
            logger.error("More than 99 phone numbers being added to GCM : throwing exception");
            throw new IllegalArgumentException();
        }
        return uniqueGCMPhones;
    }*/

    //Commented out the MODIFY phone numbers - Can be used in future if requirement comes for modify phone
    /**
     * Method to create the Modify phone request with the current data
     * @param phoneAddressContactMethod
     * @param cachedPhoneResponse
     * @param objectFactory
     * @param phone
     */
    /*
     * private static PhoneAddressContactMethod
     * createModifyPhoneRequest(PhoneAddressContactMethod
     * phoneAddressContactMethod,
     * List<au.com.westpac.gn.involvedpartymanagement.
     * services.involvedpartymanagement
     * .xsd.retrievedetailsandarrangementrelationshipsforips
     * .v10.svc0258.PhoneAddressContactMethod> cachedPhoneResponse,
     * ObjectFactory objectFactory, CustomerPhone phone) {
     * phoneAddressContactMethod.setRequestedAction(Action.MODIFY);
     * 
     * MaintenanceAuditContext maintenanceAudiContext =
     * objectFactory.createMaintenanceAuditContext();
     * maintenanceAudiContext.setIsActive(Boolean.TRUE);
     * phoneAddressContactMethod.setAuditContext(maintenanceAudiContext);
     * 
     * phoneAddressContactMethod.setStartDate(DateUtil.
     * convertDateInGregorianCalendar(new Date()));
     * phoneAddressContactMethod.setValidityStatus("C");
     * 
     * //Check for priority level and addressee
     * checkForPriorityAndAddressee(cachedPhoneResponse, phone,
     * phoneAddressContactMethod, objectFactory);
     * phoneAddressContactMethod.setHasAddress
     * (createPhoneAddressContactMethodForAdd(phone, objectFactory));
     * 
     * //creates the usage and contact medium for address update
     * CustomerPhoneConverter.createAddressMediumForUpdate(phone,
     * phoneAddressContactMethod); // phoneAddressContactMethod.sethas return
     * phoneAddressContactMethod; }
     */

    /**
     * Method to create Priority and Addressee for modify phone address
     * @param cachedPhoneResponse
     * @param phone
     * @param phoneAddressContactMethod
     * @param objectFactory
     */
    /*
     * private static void
     * checkForPriorityAndAddressee(List<au.com.westpac.gn.involvedpartymanagement
     * .services.involvedpartymanagement.xsd.
     * retrievedetailsandarrangementrelationshipsforips
     * .v10.svc0258.PhoneAddressContactMethod> cachedPhoneResponse,
     * CustomerPhone phone, PhoneAddressContactMethod phoneAddressContactMethod,
     * ObjectFactory objectFactory){
     * 
     * for(au.com.westpac.gn.involvedpartymanagement.services.
     * involvedpartymanagement
     * .xsd.retrievedetailsandarrangementrelationshipsforips
     * .v10.svc0258.PhoneAddressContactMethod cachedPhone :
     * cachedPhoneResponse){ //While retrieve we set the contactMethodIdentifier
     * in the modification sequence and so when updating we will retrieve the
     * same way
     * if(cachedPhone.getContactMethodIdentifier().getContactMethodId().
     * equalsIgnoreCase(phone.getModificationSeq())){
     * phoneAddressContactMethod.setPriorityLevel
     * (PriorityLevel.fromValue(cachedPhone.getPriorityLevel().value()));
     * 
     * //set the addressee details if(cachedPhone.getAddressee() != null){
     * InvolvedPartyName involvedPartyName =
     * objectFactory.createInvolvedPartyName(); if (null !=
     * cachedPhone.getAddressee() && null !=
     * cachedPhone.getAddressee().getFullName()) {
     * involvedPartyName.setFullName(cachedPhone.getAddressee().getFullName());
     * } phoneAddressContactMethod.setAddressee(involvedPartyName); } //set the
     * row set identifier phoneAddressContactMethod.setRowSetIdIdentifier(
     * createRowSetIdentifierForModify(cachedPhone));
     * 
     * //set the old phone address of the user
     * phoneAddressContactMethod.setHasOldValues
     * (createOldAddressForModifyPhone(objectFactory, cachedPhone)); } } }
     */

    /**
     * Method to create rowsetIdentifier for Modify Phone address
     * @param cachedPhone
     * @return RowSetItemIdentifier
     */
    /*
     * private static RowSetItemIdentifier
     * createRowSetIdentifierForModify(au.com
     * .westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.
     * retrievedetailsandarrangementrelationshipsforips
     * .v10.svc0258.PhoneAddressContactMethod cachedPhone){ final
     * au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new
     * au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
     * RowSetItemIdentifier rowIde = factory.createRowSetItemIdentifier();
     * rowIde.setSequenceNumber(cachedPhone.getContactMethodIdentifier().
     * getContactMethodId()); return rowIde; }
     */

    /**
     * Method to create Old Phone address using the cached address
     * @param objectFactory
     * @param cachedPhone
     */
    /*
     * private static PhoneAddressContactMethod
     * createOldAddressForModifyPhone(ObjectFactory objectFactory,
     * au.com.westpac
     * .gn.involvedpartymanagement.services.involvedpartymanagement
     * .xsd.retrievedetailsandarrangementrelationshipsforips
     * .v10.svc0258.PhoneAddressContactMethod cachedPhone){
     * PhoneAddressContactMethod oldPhone = new PhoneAddressContactMethod();
     * oldPhone.setRequestedAction(Action.DELETE);
     * 
     * MaintenanceAuditContext maintenanceAudiContext =
     * objectFactory.createMaintenanceAuditContext();
     * maintenanceAudiContext.setIsActive(Boolean.FALSE);
     * oldPhone.setAuditContext(maintenanceAudiContext);
     * 
     * oldPhone.setInternalIdentifier(cachedPhone.getContactMethodIdentifier());
     * oldPhone.setUsageId(cachedPhone.getUsage());
     * oldPhone.setStartDate(cachedPhone.getStartDate().getValue());
     * oldPhone.setEndDate(DateUtil.convertDateInGregorianCalendar(new Date()));
     * oldPhone.setValidityStatus(cachedPhone.getValidityStatus());
     * 
     * if(cachedPhone.getPriorityLevel() != null) {
     * oldPhone.setPriorityLevel(PriorityLevel
     * .fromValue(cachedPhone.getPriorityLevel().value())); }
     * 
     * if(cachedPhone.getAddressee() != null){ InvolvedPartyName
     * involvedPartyName = objectFactory.createInvolvedPartyName(); if (null !=
     * cachedPhone.getAddressee() && null !=
     * cachedPhone.getAddressee().getFullName()) {
     * involvedPartyName.setFullName(cachedPhone.getAddressee().getFullName());
     * } oldPhone.setAddressee(involvedPartyName); } return
     * createOldPhoneAddress(objectFactory, oldPhone, cachedPhone); }
     */

    /**
     * Method to create Old Address using the cached Data
     * @param objectFactory
     * @param cachedPhone
     */
    /*
     * private static PhoneAddressContactMethod
     * createOldPhoneAddress(ObjectFactory objectFactory ,
     * PhoneAddressContactMethod oldPhone,
     * au.com.westpac.gn.involvedpartymanagement
     * .services.involvedpartymanagement
     * .xsd.retrievedetailsandarrangementrelationshipsforips
     * .v10.svc0258.PhoneAddressContactMethod cachedPhone){ TelephoneAddress
     * phoneAddress = objectFactory.createTelephoneAddress();
     * 
     * if(StringUtils.isNotBlank(cachedPhone.getHasAddress().getAreaCode())) {
     * phoneAddress.setAreaCode(cachedPhone.getHasAddress().getAreaCode()); }
     * 
     * if(StringUtils.isNotBlank(cachedPhone.getHasAddress().getCountryCode()))
     * {
     * phoneAddress.setCountryCode(cachedPhone.getHasAddress().getCountryCode()
     * ); }
     * 
     * if(StringUtils.isNotBlank(cachedPhone.getHasAddress().getLocalNumber()))
     * {
     * phoneAddress.setLocalNumber(cachedPhone.getHasAddress().getLocalNumber()
     * ); }
     * 
     * if(StringUtils.isNotBlank(cachedPhone.getContactMedium())) {
     * phoneAddress.setContactMedium(cachedPhone.getContactMedium()); }
     * 
     * oldPhone.setHasAddress(phoneAddress); return oldPhone; }
     */

    /*
    private static boolean findIfPhoneDetailsAreSame(Phone phone, Phone cachedPhone){
        boolean isAddressSame = false;

        if(phone.equals(cachedPhone)){
            isAddressSame = true;
        }
        return isAddressSame;
    }*/
}
