package com.bt.nextgen.service.group.customer.groupesb.address.v10;

import au.com.westpac.gn.common.xsd.identifiers.v1.RowSetItemIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.Action;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.InvolvedPartyName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.NonStandardPostalAddress;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PriorityLevel;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.integration.domain.Address;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by F057654 on 10/08/2015.
 */
@SuppressWarnings("squid:S1200")
public final class CustomerAddressUpdateV10Converter {

    private static final Logger logger = LoggerFactory.getLogger(CustomerAddressUpdateV10Converter.class);

    private CustomerAddressUpdateV10Converter(){}

    public static au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod createPostalAddressToBeSentForUpdate(
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddressContactMethod cachedPostalAddress,
            CustomerData customerData) {
        logger.info("Creating request for UpdateAddress for user{}", customerData.getRequest().getCISKey().getId());

        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod address = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod();
        address.setRequestedAction(Action.MODIFY);
        address.setAuditContext(createAuditContext(cachedPostalAddress));
        createAddressFromCachedAddressDetails(address, cachedPostalAddress);
        createOldAddressFromCachedAddress(address, cachedPostalAddress);

        if(customerData.getAddress().isInternationalAddress()){
            NonStandardPostalAddress nonStandardPostalAddress = convertInternationalAddressInRequest(customerData);
            address.setHasAddress(nonStandardPostalAddress);
        }else {
            StandardPostalAddress standardPostalAddress = convertAddressInRequest(customerData, cachedPostalAddress);
            address.setHasAddress(standardPostalAddress);
        }
       
        logger.info("Completed creating request for UpdateAddress for user{}",customerData.getRequest().getCISKey().getId());
        return address;
    }

    /**
     * Method to convert the Address interface into StandardPostalAddress of svc0418 which will be sent as a part of request when updating the address in gcm.
     * @param customerData - CustomerData containing user updated address
     * @return StandardPostalAddress
     */
    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck"})
    public static au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress convertAddressInRequest(
            CustomerData customerData,
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddressContactMethod cachedPostalAddress) {

        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress
                standardPostalAddress
                = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress();

        Address address = customerData.getAddress();
        if(StringUtils.isNotBlank(address.getFloor())){
            standardPostalAddress.setFloorNumber(address.getFloor());
        }

        if(StringUtils.isNotBlank(address.getUnit())){
            standardPostalAddress.setUnitNumber(address.getUnit());
        }

        if(StringUtils.isNotBlank(address.getStreetNumber())){
            standardPostalAddress.setStreetNumber(address.getStreetNumber());
        }

        if(StringUtils.isNotBlank(address.getStreetName())){
            standardPostalAddress.setStreetName(transformResponseValue(address.getStreetName()));
        }

        if(StringUtils.isNotBlank(address.getStreetType())){
            standardPostalAddress.setStreetType(transformResponseValue(address.getStreetType()));
        }

        if(StringUtils.isNotBlank(address.getCountry())){
            standardPostalAddress.setCountry(transformResponseValue(address.getCountry()));
        }

        if(StringUtils.isNotBlank(address.getPostCode())){
            standardPostalAddress.setPostCode(transformResponseValue(address.getPostCode()));
        }

        if(StringUtils.isNotBlank(address.getCity())){
            standardPostalAddress.setCity(transformResponseValue(address.getCity()));
        }

        /*if(StringUtils.isNotBlank(address.getState())){
            standardPostalAddress.setState(transformResponseValue(address.getState()));
        }*/

        if(StringUtils.isNotBlank(address.getBuilding())){
            standardPostalAddress.setBuildingName(transformResponseValue(address.getBuilding()));
        }

        if (cachedPostalAddress.getHasAddress() instanceof au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress) {
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress cachedStandardPostalAddress = (au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress) cachedPostalAddress
                    .getHasAddress();

            if(StringUtils.isNotBlank(cachedStandardPostalAddress.getBuildingType())){
                standardPostalAddress.setBuildingType(transformResponseValue(cachedStandardPostalAddress.getBuildingType()));
            }

            if(StringUtils.isNotBlank(cachedStandardPostalAddress.getStreetSuffix())) {
                standardPostalAddress.setStreetSuffix(transformResponseValue(cachedStandardPostalAddress.getStreetSuffix()));
            }
        }

        return standardPostalAddress;
    }

    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck")
    public static au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.NonStandardPostalAddress convertInternationalAddressInRequest(CustomerData customerData){

        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.NonStandardPostalAddress nonStandardPostalAddress
                = new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.NonStandardPostalAddress();

        Address address = customerData.getAddress();

        if(StringUtils.isNotBlank(address.getCountry())){
            nonStandardPostalAddress.setCountry(transformResponseValue(address.getCountry()));
        }

        if(StringUtils.isNotBlank(address.getPostCode())){
            nonStandardPostalAddress.setPostCode(address.getPostCode());
        }

        if(StringUtils.isNotBlank(address.getCity())){
            nonStandardPostalAddress.setCity(transformResponseValue(address.getCity()));
        }

        /*if(StringUtils.isNotBlank(address.getState())){
            nonStandardPostalAddress.setState(transformResponseValue(address.getState()));
        }*/

        if(StringUtils.isNotBlank(address.getCountry())){
            nonStandardPostalAddress.setCountry(transformResponseValue(address.getCountry()));
        }

        if(StringUtils.isNotBlank(address.getAddressLine1())){
            nonStandardPostalAddress.setAddressLine1(transformResponseValue(address.getAddressLine1()));
        }

        if(StringUtils.isNotBlank(address.getAddressLine2())){
            nonStandardPostalAddress.setAddressLine2(transformResponseValue(address.getAddressLine2()));
        }

        if(StringUtils.isNotBlank(address.getAddressLine3())){
            //If UI cant handle addressline3, it needs to be handled from the backend- needs to be set using the cached data
            nonStandardPostalAddress.setAddressLine3(transformResponseValue(address.getAddressLine3()));
        }
        return nonStandardPostalAddress;
    }

    /**
     * Method to create OldAddress from the cached data. This OldAddress needs to be sent as part of the update request.
     * Cached data from svc0258 is used to create the oldAddress in svc0418
     * @param cachedPostalAddress
     * @param oldAddress
     * @return PostalAddressContactMethod
     */
    @SuppressWarnings({"squid:MethodCyclomaticComplexity","checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck","checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck"})
    public static au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod createOldAddressFromCachedAddress(au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod oldAddress,
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddressContactMethod cachedPostalAddress) {

        logger.info("Creating old address request using the cached data");
        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod hasOldValuesAddress = new PostalAddressContactMethod();

        //use the old postalAddress instance of Service 258 and convert it into Service 418
        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddress cachedAddress = cachedPostalAddress
                .getHasAddress();
        if (cachedAddress instanceof au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress) {

            hasOldValuesAddress.setHasAddress(createOldStandardPostalAddressFromCachedAddress(
(au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress) cachedAddress));
        }
 else if (cachedAddress instanceof au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.NonStandardPostalAddress) {

            hasOldValuesAddress.setHasAddress(createOldNonStandardPostalAddressFromCachedAddress(
(au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.NonStandardPostalAddress) cachedAddress));
        }

        hasOldValuesAddress.setRequestedAction(Action.DELETE);
        hasOldValuesAddress.setAuditContext(createOldAuditContextForUpdate(cachedPostalAddress));
        hasOldValuesAddress.setInternalIdentifier(cachedPostalAddress.getContactMethodIdentifier());
        hasOldValuesAddress.setUsage(cachedPostalAddress.getUsage());
        hasOldValuesAddress.setAlternativeIdentifier(cachedPostalAddress.getAddressRelationshipIdentifier());
        if(cachedPostalAddress.getStartDate() != null){
            hasOldValuesAddress.setStartDate(cachedPostalAddress.getStartDate().getValue());
        }
        hasOldValuesAddress.setValidityStatus(cachedPostalAddress.getValidityStatus());
        hasOldValuesAddress.setEndDate(DateUtil.convertDateInGregorianCalendar(new Date()));

        if(cachedPostalAddress.getPriorityLevel() != null){
            hasOldValuesAddress.setPriorityLevel(PriorityLevel.fromValue(cachedPostalAddress.getPriorityLevel().value()));
        }

        //create the addressee
        if(cachedPostalAddress.getAddressee() != null){
            InvolvedPartyName partyName = new InvolvedPartyName();

            if(cachedPostalAddress.getAddressee().getFullName() != null) {
                partyName.setFullName(transformResponseValue(cachedPostalAddress.getAddressee().getFullName()));
            }

            if(cachedPostalAddress.getAddressee().getNameText() != null) {
                partyName.getNameText().addAll(cachedPostalAddress.getAddressee().getNameText());
            }
            hasOldValuesAddress.setAddressee(partyName);
        }

        oldAddress.setHasOldValues(hasOldValuesAddress);
        logger.info("Completed creating old address request using the cached data");
        return oldAddress;
    }

    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck"})
    public static au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress createOldStandardPostalAddressFromCachedAddress(
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.StandardPostalAddress cachedStandardPostalAddress) {

        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress oldStandardPostalAddress =
                new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.StandardPostalAddress();

        if(StringUtils.isNotBlank(cachedStandardPostalAddress.getFloorNumber())){
            oldStandardPostalAddress.setFloorNumber(cachedStandardPostalAddress.getFloorNumber());
        }

        if(StringUtils.isNotBlank(cachedStandardPostalAddress.getUnitNumber())){
            oldStandardPostalAddress.setUnitNumber(cachedStandardPostalAddress.getUnitNumber());
        }

        if(StringUtils.isNotBlank(cachedStandardPostalAddress.getStreetNumber())){
            oldStandardPostalAddress.setStreetNumber(cachedStandardPostalAddress.getStreetNumber());
        }

        if(StringUtils.isNotBlank(cachedStandardPostalAddress.getStreetName())){
            oldStandardPostalAddress.setStreetName(transformResponseValue(cachedStandardPostalAddress.getStreetName()));
        }

        if(StringUtils.isNotBlank(cachedStandardPostalAddress.getStreetType())){
            oldStandardPostalAddress.setStreetType(transformResponseValue(cachedStandardPostalAddress.getStreetType()));
        }

        if(StringUtils.isNotBlank(cachedStandardPostalAddress.getBuildingName())){
            oldStandardPostalAddress.setBuildingName(transformResponseValue(cachedStandardPostalAddress.getBuildingName()));
        }

        if(StringUtils.isNotBlank(cachedStandardPostalAddress.getCity())){
            oldStandardPostalAddress.setCity(transformResponseValue(cachedStandardPostalAddress.getCity()));
        }

        /*if(StringUtils.isNotBlank(cachedStandardPostalAddress.getState())){
            oldStandardPostalAddress.setState(transformResponseValue(cachedStandardPostalAddress.getState()));
        }*/
        if(StringUtils.isNotBlank(cachedStandardPostalAddress.getPostCode())){
            oldStandardPostalAddress.setPostCode(transformResponseValue(cachedStandardPostalAddress.getPostCode()));
        }
        if(StringUtils.isNotBlank(cachedStandardPostalAddress.getCountry())){
            oldStandardPostalAddress.setCountry(transformResponseValue(cachedStandardPostalAddress.getCountry()));
        }

        return oldStandardPostalAddress;
    }

    @SuppressWarnings({"squid:MethodCyclomaticComplexity", "checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck"})
    public static au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.NonStandardPostalAddress createOldNonStandardPostalAddressFromCachedAddress(
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.NonStandardPostalAddress cacheNonStandardPostalAddress) {
        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.NonStandardPostalAddress oldNonStandardPostalAddress =
                new au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.NonStandardPostalAddress();
        if(StringUtils.isNotBlank(cacheNonStandardPostalAddress.getAddressLine1())){
            oldNonStandardPostalAddress.setAddressLine1(transformResponseValue(cacheNonStandardPostalAddress.getAddressLine1()));
        }
        if(StringUtils.isNotBlank(cacheNonStandardPostalAddress.getAddressLine2())){
            oldNonStandardPostalAddress.setAddressLine2(transformResponseValue(cacheNonStandardPostalAddress.getAddressLine2()));
        }

        if(StringUtils.isNotBlank(cacheNonStandardPostalAddress.getAddressLine3())){
            oldNonStandardPostalAddress.setAddressLine3(transformResponseValue(cacheNonStandardPostalAddress.getAddressLine3()));
        }
        if(StringUtils.isNotBlank(cacheNonStandardPostalAddress.getCity())){
            oldNonStandardPostalAddress.setCity(transformResponseValue(cacheNonStandardPostalAddress.getCity()));
        }
        /*if(StringUtils.isNotBlank(cacheNonStandardPostalAddress.getState())){
            oldNonStandardPostalAddress.setState(transformResponseValue(cacheNonStandardPostalAddress.getState()));
        }*/
        if(StringUtils.isNotBlank(cacheNonStandardPostalAddress.getCountry())){
            oldNonStandardPostalAddress.setCountry(transformResponseValue(cacheNonStandardPostalAddress.getCountry()));
        }
        if(StringUtils.isNotBlank(cacheNonStandardPostalAddress.getPostCode())){
            oldNonStandardPostalAddress.setPostCode(cacheNonStandardPostalAddress.getPostCode());
        }

        return oldNonStandardPostalAddress;
    }

    /**
     * Method to create address from the cached data.
     * Cached data from svc0258 is used to populate the address related metadata in svc0418
     * @param cachedPostalAddress
     * @param address
     * @return PostalAddressContactMethod
     */
    public static au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod createAddressFromCachedAddressDetails(
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainipcontactmethods.v1.svc0418.PostalAddressContactMethod address,
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddressContactMethod cachedPostalAddress) {
        logger.info("Creating address request using the updated data for user");
        address.setUsage(cachedPostalAddress.getUsage());

        if(cachedPostalAddress.getStartDate() != null){
            address.setStartDate(cachedPostalAddress.getStartDate().getValue());
        }
        address.setValidityStatus(cachedPostalAddress.getValidityStatus());

        if(cachedPostalAddress.getPriorityLevel() != null){
            address.setPriorityLevel(PriorityLevel.fromValue(cachedPostalAddress.getPriorityLevel().value()));
        }

        logger.info("Completed creating address request using the updated data for user");
        //Setting the rowSetItemIdentifier to be same as the contactMethodId
        RowSetItemIdentifier rowSetItemIdentifier = new RowSetItemIdentifier();
        rowSetItemIdentifier.setSequenceNumber(cachedPostalAddress.getContactMethodIdentifier().getContactMethodId());
        address.setRowSetIdIdentifier(rowSetItemIdentifier);

        //create the addressee
        if(cachedPostalAddress.getAddressee() != null){
            InvolvedPartyName involvedPartyName = new InvolvedPartyName();

            if(cachedPostalAddress.getAddressee().getFullName() != null) {
                involvedPartyName.setFullName(transformResponseValue(cachedPostalAddress.getAddressee().getFullName()));
            }

            if(cachedPostalAddress.getAddressee().getNameText() != null) {
                involvedPartyName.getNameText().addAll(cachedPostalAddress.getAddressee().getNameText());
            }
            address.setAddressee(involvedPartyName);
        }
        return address;
    }

    /**
     * Method to create OldAuditContext that needs to be sent as part of OldAddress while updating address in gcm
     * Use the address cached data for creating the oldAddress
     * @param cachedPostalAddress
     * @return MaintenanceAuditContext
     */
    public static MaintenanceAuditContext createOldAuditContextForUpdate(
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddressContactMethod cachedPostalAddress) {
        MaintenanceAuditContext auditContext = new MaintenanceAuditContext();

        if(StringUtils.isNotEmpty(cachedPostalAddress.getAuditContext().getVersionNumber())) {
            auditContext.setVersionNumber(cachedPostalAddress.getAuditContext().getVersionNumber());
        }
        auditContext.setIsActive(false);

        if(cachedPostalAddress.getAuditContext().getLastUpdateTimestamp() != null){
            auditContext.setLastUpdateTimestamp(cachedPostalAddress.getAuditContext().getLastUpdateTimestamp().getValue());
        }

        return auditContext;
    }

    /**
     * Method to create AuditContext that needs to be sent as part of Address while updating address in gcm
     * Use the address cached data for creating the Address
     * @param cachedPostalAddress
     * @return MaintenanceAuditContext
     */
    public static MaintenanceAuditContext createAuditContext(
            au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v10.svc0258.PostalAddressContactMethod cachedPostalAddress) {
        MaintenanceAuditContext auditContext = new MaintenanceAuditContext();
        auditContext.setIsActive(true);
        if(cachedPostalAddress.getAuditContext().getLastUpdateTimestamp() != null) {
            auditContext.setLastUpdateTimestamp(DateUtil.convertDateInGregorianCalendar(new Date()));
        }
        return auditContext;
    }

    private static String transformResponseValue(String val){
        return val.toUpperCase();
    }
}
