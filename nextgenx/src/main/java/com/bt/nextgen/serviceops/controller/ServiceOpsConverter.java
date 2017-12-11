package com.bt.nextgen.serviceops.controller;

import java.util.*;

import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.AddressTypeV2;
import com.bt.nextgen.api.client.v2.model.InvestorDto;
import com.bt.nextgen.api.client.v2.model.RegisteredEntityDto;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.serviceops.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.AddressFormatter;
import com.bt.nextgen.core.type.DateFormatType;
import com.bt.nextgen.core.type.DateUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.core.web.model.Intermediaries;
import com.bt.nextgen.core.web.model.PersonInterface;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;

@SuppressWarnings("squid:S1200")//Classes should not be coupled to too many other classes (Single Responsibility Principle)
public final class ServiceOpsConverter
{
    private static final Logger logger = LoggerFactory.getLogger(ServiceOpsConverter.class);
    private static final String INTERMEDIARIES = "Intermediaries";
    private static final String CLIENT = "client";
    private static final String UPLOADED_DATE = "uploadedDate";
    private static final String UPLOADED_ROLE = "uploadedRole";
    private static final String FINANCIAL_YEAR = "financialYear";
    private static final String DOCUMENT_TYPE = "documentType";
    private static final String DOCUMENT_SUB_TYPE = "documentSubType";
    private static final String DOCUMENT_RELATIONSHIP_TYPE = "panoramaipRelationshipType";
    private static final String STATUS = "status";
    private static final String AUDIT_FIELD = "audit";
    private static final String SOFT_DELETED ="deleted";
    private static final String DOCUMENT_SUB_SUB_TYPE="documentSubType2";

    private ServiceOpsConverter(){

    }

    public static ServiceOpsModel toServiceOpsModel(IndividualDetailImpl individual)
    {
        logger.info("Converting person object to serviceOpsModel object");

        //IndividualDetailImpl individual = (IndividualDetailImpl) client;

        ServiceOpsModel serviceOpsModel = new ServiceOpsModel();
        serviceOpsModel.setFirstName(individual.getFirstName());
        serviceOpsModel.setLastName(individual.getLastName());
        serviceOpsModel.setFullName(individual.getFullName());
        serviceOpsModel.setRegisteredSince(DateUtil.toFormattedDate(individual.getOpenDate().toDate(), DateFormatType.DATEFORMAT_FRONT_END));

        String oracleUser = individual.getCustomerId();

        if (oracleUser.startsWith("0"))
            oracleUser = oracleUser.substring(1);
        
        if (individual.getDateOfBirth() != null)
        	serviceOpsModel.setDob(DateUtil.toFormattedDate(individual.getDateOfBirth().toDate(), DateFormatType.DATEFORMAT_FRONT_END));

        serviceOpsModel.setUserId(oracleUser);
        serviceOpsModel.setGcmId(oracleUser);

        if (individual.getCISKey() != null) {
            serviceOpsModel.setCisId(individual.getCISKey().getId());
        }
        serviceOpsModel.setWestpacCustomerNumber(individual.getWestpacCustomerNumber());
        serviceOpsModel.setSafiDeviceId(individual.getSafiDeviceId());
        serviceOpsModel.setAvaloqStatusReg(individual.isRegistrationOnline());
        serviceOpsModel.setAdviserFlag(individual.isAdviserFlag());
        serviceOpsModel.setParaPlannerFlag(individual.isParaPlannerFlag());

		List <Phone> landLineList = new ArrayList <>();
		List <Phone> mobileList = new ArrayList <>();
        if(individual.getPhones() != null) {
            for (Phone phone : individual.getPhones()) {
                if ((AddressMedium.MOBILE_PHONE_PRIMARY == phone.getType() || AddressMedium.MOBILE_PHONE_SECONDARY
                        == phone.getType()))
                {
                    if (AddressMedium.MOBILE_PHONE_PRIMARY == phone.getType()) {
                        serviceOpsModel.setPrimaryMobileNumber(phone.getNumber());
                    }
                    mobileList.add(phone);
                } else if (phone.isPreferred()) {
                    landLineList.add(phone);
                }
            }
        }

         serviceOpsModel.setPhone(landLineList);
         serviceOpsModel.setMobilePhones(mobileList);
		 serviceOpsModel.setEmail(individual.getEmails());

        serviceOpsModel.setClientId(EncodedString.fromPlainText(individual.getClientKey().getId()));
        for (Address address : individual.getAddresses())
        {
			if (AddressMedium.POSTAL == address.getAddressType())
			{

				if (address.isDomicile())
				{

					serviceOpsModel.setResidentialAddress(toAddressModel(address));
				}
				if (address.isMailingAddress())
				{
					serviceOpsModel.setPostalAddress(toAddressModel(address));
                }
            }
        }
        if(serviceOpsModel.getResidentialAddress() == null) {
            serviceOpsModel.setResidentialAddress(serviceOpsModel.getPostalAddress());
        }
        serviceOpsModel.setPpIdFromAvaloq(individual.getPpId());
        return serviceOpsModel;
    }


    /**
     * Convert an Address (Cash Refactor) into an AddressModel (origianl Cash equivalent).
     * <p>
     * Used to preserve compatiblity between Addresses for legacy screens.
     * </p>
     * @param fromAddress Address bean used across the code base
     * @return AddressModel bean to be used for legacy code/screens
     */
    public static AddressModel toAddressModel(Address fromAddress)
    {
        AddressModel toAddress = new AddressModel();

        toAddress.setAddressCategory(fromAddress.getAddressType().getAddressType());
        toAddress.setAddressKind(fromAddress.getAddressType().name());
        toAddress.setAddressLine1(AddressFormatter.getAddressLineOne(fromAddress));
        toAddress.setAddressLine2(AddressFormatter.getAddressLineTwo(fromAddress));
        toAddress.setBoxPrefix(fromAddress.getPoBoxPrefix());
        toAddress.setBuildingName(fromAddress.getBuilding());
        toAddress.setCity(fromAddress.getCity());
        toAddress.setCountry(fromAddress.getCountry());
        toAddress.setFloorNumber(fromAddress.getFloor());
        toAddress.setIsDomicileAddress((new Boolean(fromAddress.isDomicile())).toString());
        toAddress.setIsMailingAddress(new Boolean(fromAddress.isMailingAddress()).toString());
        toAddress.setPin(fromAddress.getPostCode());
        toAddress.setPoBoxNumber(fromAddress.getPoBox());
        toAddress.setPostcode(fromAddress.getPostCode());
        toAddress.setState(fromAddress.getState());
        toAddress.setStreet(fromAddress.getStreetName());
        toAddress.setStreetNumber(fromAddress.getStreetNumber());
        toAddress.setStreetType(fromAddress.getStreetType());
        toAddress.setSuburb(fromAddress.getSuburb());
        toAddress.setType(fromAddress.getAddressType().getAddressType());
        toAddress.setUnitNumber(fromAddress.getUnit());

        return toAddress;
    }

    public static IntermediariesModel toIntermediariesModel(Intermediaries person)
    {
        logger.info("Converting Intermediaries object to IntermediariesModel object");
        IntermediariesModel intermediariesModel = new IntermediariesModel();

        intermediariesModel.setFirstName(person.getFirstName());
        intermediariesModel.setLastName(person.getLastName());
        intermediariesModel.setDealerGroup(person.getDealerGroupName());
        intermediariesModel.setCompanyName(person.getCompanyName());

        intermediariesModel.setEmail(person.getPrimaryEmailId());
        intermediariesModel.setPhone(person.getPrimaryMobileNumber());

        intermediariesModel.setUserId(person.getOracleUser());
        intermediariesModel.setClientId(person.getClientId().toString());
        if (person.getPrimaryDomiAddress() != null)
        {
            intermediariesModel.setCity(person.getPrimaryDomiAddress().getAddressLine2());
            intermediariesModel.setState(person.getPrimaryDomiAddress().getState());
        }
        intermediariesModel.setRole(person.getRole());
        intermediariesModel.setAvaloqUserId(person.getGcmId());
        return intermediariesModel;
    }

    public static ClientModel toClientModel(PersonInterface person)
    {
        logger.info("Converting PersonInterface to clientModel object");
        ClientModel clientModel = new ClientModel();

        clientModel.setFirstName(person.getFirstName());
        clientModel.setLastName(person.getLastName());
        clientModel.setEmail(person.getPrimaryEmailId());

        clientModel.setPhone(person.getPrimaryMobileNumber());

        clientModel.setClientId(person.getClientId().toString());
        clientModel.setUserId(person.getOracleUser());
        if (person.getPrimaryDomiAddress() != null)
        {
            clientModel.setCity(person.getPrimaryDomiAddress().getAddressLine2());
            clientModel.setState(person.getPrimaryDomiAddress().getState());
        }
        clientModel.setAvaloqUserId(person.getGcmId());
        return clientModel;
    }

    @SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.NPathComplexityCheck", "squid:MethodCyclomaticComplexity"})
    public static List<ApiSearchCriteria> toApiSearchCriteria(DocumentFilterModel documentFilterDto) {
        List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
        if(null != documentFilterDto) {
            if(null != documentFilterDto.getRelationshipType()){
                searchCriteriaList.add(new ApiSearchCriteria(DOCUMENT_RELATIONSHIP_TYPE,
                        ApiSearchCriteria.SearchOperation.EQUALS ,
                        documentFilterDto.getRelationshipType(),
                        ApiSearchCriteria.OperationType.STRING));
            }
            if(null != documentFilterDto.getDocumentSubType()){
                searchCriteriaList.add(new ApiSearchCriteria(DOCUMENT_SUB_TYPE,
                        ApiSearchCriteria.SearchOperation.EQUALS ,
                        documentFilterDto.getDocumentSubType(),
                        ApiSearchCriteria.OperationType.STRING));
            }
            if(null != documentFilterDto.getDocumentType()){
                searchCriteriaList.add(new ApiSearchCriteria(DOCUMENT_TYPE,
                        ApiSearchCriteria.SearchOperation.EQUALS ,
                        documentFilterDto.getDocumentType(),
                        ApiSearchCriteria.OperationType.STRING));
            }
            if(null != documentFilterDto.getFinancialYear()){
                searchCriteriaList.add(new ApiSearchCriteria(FINANCIAL_YEAR,
                        ApiSearchCriteria.SearchOperation.EQUALS ,
                        documentFilterDto.getFinancialYear(),
                        ApiSearchCriteria.OperationType.STRING));
            }
            if(null != documentFilterDto.getUploadedBy()){
                searchCriteriaList.add(new ApiSearchCriteria(UPLOADED_ROLE,
                        ApiSearchCriteria.SearchOperation.EQUALS ,
                        documentFilterDto.getUploadedBy(),
                        ApiSearchCriteria.OperationType.STRING));
            }
            if(null != documentFilterDto.getFromDate()){
                searchCriteriaList.add(new ApiSearchCriteria(UPLOADED_DATE,
                        ApiSearchCriteria.SearchOperation.NEG_LESS_THAN ,
                        documentFilterDto.getFromDate(),
                        ApiSearchCriteria.OperationType.DATE));
            }
            if( null != documentFilterDto.getToDate()) {
                searchCriteriaList.add(new ApiSearchCriteria(UPLOADED_DATE,
                        ApiSearchCriteria.SearchOperation.LESS_THAN ,
                        documentFilterDto.getToDate(),
                        ApiSearchCriteria.OperationType.DATE));
            }
            if(null != documentFilterDto.getDocumentStatus()){
                searchCriteriaList.add(new ApiSearchCriteria(STATUS,
                        ApiSearchCriteria.SearchOperation.EQUALS ,
                        documentFilterDto.getDocumentStatus(),
                        ApiSearchCriteria.OperationType.STRING));
            }
            if(null != documentFilterDto.getAuditFlag()){
                searchCriteriaList.add(new ApiSearchCriteria(AUDIT_FIELD,
                        ApiSearchCriteria.SearchOperation.EQUALS ,
                        documentFilterDto.getAuditFlag(),
                        ApiSearchCriteria.OperationType.BOOLEAN));
            }
            if(null != documentFilterDto.getSoftDeleted()){
                searchCriteriaList.add(new ApiSearchCriteria(SOFT_DELETED,
                        ApiSearchCriteria.SearchOperation.EQUALS ,
                        "Y",
                        ApiSearchCriteria.OperationType.STRING));
            }
            if(null != documentFilterDto.getDocumentSubSubType()){
                searchCriteriaList.add(new ApiSearchCriteria(DOCUMENT_SUB_SUB_TYPE,
                        ApiSearchCriteria.SearchOperation.EQUALS ,
                        documentFilterDto.getDocumentSubSubType(),
                        ApiSearchCriteria.OperationType.STRING));
            }
        }

        return searchCriteriaList;
    }

    /**
     * convert domain account to wrapaccountdetail dto
     *
     * @param account
     * @return
     */
    public static WrapAccountModel convertToWrapAccountModel(WrapAccountDetail account) {
        WrapAccountModel wrapAccountModel = null;
        if (account != null) {
            wrapAccountModel = new WrapAccountModel();
            com.bt.nextgen.api.account.v2.model.AccountKey key = new com.bt.nextgen.api.account.v2.model.AccountKey(EncodedString
                    .fromPlainText(account.getAccountKey().getId()).toString());
            wrapAccountModel.setAccountId(key.getAccountId());
            wrapAccountModel.setAccountStatus(account.getAccountStatus().getStatusDescription());
            wrapAccountModel.setAccountName(account.getAccountName());
            wrapAccountModel.setAccountType(account.getAccountStructureType().name());
            wrapAccountModel.setBsb(account.getBsb());
            wrapAccountModel.setAccountNumber(account.getAccountNumber());
            wrapAccountModel.setAdviserName(((WrapAccountDetailImpl)account).getAdviserName());
            wrapAccountModel.setProduct(((WrapAccountDetailImpl)account).getProductName());
            wrapAccountModel.setMigrationKey(((WrapAccountDetailImpl)account).getMigrationKey());
            if(null != ((WrapAccountDetailImpl)account).getOwnerNames()) {
                wrapAccountModel.setOwners(((WrapAccountDetailImpl)account).getOwnerNames());
                for (String name : wrapAccountModel.getOwners()) {
                    if(StringUtil.isNotNullorEmpty(wrapAccountModel.getOwnerNames())){
                        wrapAccountModel.setOwnerNames(wrapAccountModel.getOwnerNames() +", ");
                    }
                    wrapAccountModel.setOwnerNames(name);
                }
            }



            return wrapAccountModel;
        }
        return null;
    }

    /**
     * Convert WrapAccount to WrapAccountModel
     *
     * @param account
     * @return
     */
    public static WrapAccountModel convertToSimpleWrapAccountModel(WrapAccount account) {
        WrapAccountModel wrapAccountModel = null;
        if (account != null) {
            wrapAccountModel = new WrapAccountModel();
            com.bt.nextgen.api.account.v2.model.AccountKey key = new com.bt.nextgen.api.account.v2.model.AccountKey(EncodedString
                    .fromPlainText(account.getAccountKey().getId()).toString());
            wrapAccountModel.setAccountId(key.getAccountId());
            wrapAccountModel.setAccountStatus(account.getAccountStatus() != null ? account.getAccountStatus().getStatusDescription() : null);
            wrapAccountModel.setAccountName(account.getAccountName());
            wrapAccountModel.setmNumber(((WrapAccountDetailImpl)account).getMigrationKey());
            if (isSuper(account)) {
                if (isPension(account)) {
                    wrapAccountModel.setAccountType(StringUtils.capitalize(AccountSubType.PENSION.name().toLowerCase()));
                } else {
                    wrapAccountModel.setAccountType(StringUtils.capitalize(AccountStructureType.SUPER.name().toLowerCase()));
                }
            } else {
                wrapAccountModel.setAccountType(account.getAccountStructureType() != null ? account.getAccountStructureType().name() : null);
            }
            wrapAccountModel.setAccountNumber(account.getAccountNumber());
            wrapAccountModel.setProduct(account.getProductName());
        }
        return wrapAccountModel;
    }

    /**
     * Convert WrapAccountDetailDto to list of LinkedClientModels
     *
     * @param serviceOpsModel
     * @return
     */
    public static ServiceOpsModel convertWrapAccountDtoToLinkedClientModel(ServiceOpsModel serviceOpsModel) {
        ArrayList<LinkedClientModel> linkedClientModels =  getAdviserDetails(serviceOpsModel);
        WrapAccountDetailDto wrapAccountDetailDto = serviceOpsModel.getWrapAccountDetail();
        LinkedClientModel linkedClientModel = null;
        List<PersonRelationDto> personRelationDtos = wrapAccountDetailDto.getSettings();
        if ("Individual".equalsIgnoreCase(wrapAccountDetailDto.getAccountType()) || "Joint".equalsIgnoreCase(wrapAccountDetailDto.getAccountType())
                || "Super".equalsIgnoreCase(wrapAccountDetailDto.getAccountType())) {
            for (InvestorDto investorDto : wrapAccountDetailDto.getOwners()) {
                linkedClientModel = new LinkedClientModel();
                populateLinkedClientModelFrmInvestorDto(investorDto, linkedClientModel);
                linkedClientModel.setGcmId(investorDto.getGcmId());
                for (PersonRelationDto personRelationDto : personRelationDtos) {
                    if (investorDto.getKey().equals(personRelationDto.getClientKey())) {
                        populateLinkedClientModelFrmPersonRelationDto(personRelationDto, serviceOpsModel, linkedClientModel, false);
                    }
                }
                linkedClientModel.setActionMessage("Sign in as " + linkedClientModel.getFirstName() + " " + linkedClientModel.getLastName() + " (Investor)");
                linkedClientModels.add(linkedClientModel);
            }
        } else {
            for (InvestorDto owner : wrapAccountDetailDto.getOwners()) {
                for (InvestorDto linkedClient : ((RegisteredEntityDto) owner).getLinkedClients()) {
                    linkedClientModel = new LinkedClientModel();
                    populateLinkedClientModelFrmInvestorDto(linkedClient, linkedClientModel);
                    linkedClientModel.setActionMessage("Sign in as " + linkedClientModel.getFirstName() + " " + linkedClientModel.getLastName() + " (Investor)");
                    for (PersonRelationDto personRelationDto : personRelationDtos) {
                        if (linkedClient.getKey().equals(personRelationDto.getClientKey())) {
                            populateLinkedClientModelFrmPersonRelationDto(personRelationDto, serviceOpsModel, linkedClientModel, true);
                            linkedClientModel.setGcmId(linkedClient.getGcmId());
                        }
                    }
                    linkedClientModels.add(linkedClientModel);
                }
            }
        }

        serviceOpsModel.setLinkedClients(linkedClientModels);
        return serviceOpsModel;
    }

    private static void populateLinkedClientModelFrmInvestorDto(InvestorDto linkedClient, LinkedClientModel linkedClientModel) {
        linkedClientModel.setFirstName(linkedClient.getFirstName());
        linkedClientModel.setLastName(linkedClient.getLastName());
        linkedClientModel.setFullName(linkedClient.getFullName());
        linkedClientModel.setClientId(linkedClient.getKey().getClientId());
        linkedClientModel.setAdviserFlag(false);
        linkedClientModel.setPostalAddress(getAddressString(linkedClient.getAddresses(), false));
    }

    private static void populateLinkedClientModelFrmPersonRelationDto(PersonRelationDto personRelationDto, ServiceOpsModel serviceOpsModel, LinkedClientModel linkedClientModel, boolean roleSettingFlag) {
        linkedClientModel.setDetailPageUrl("../" + personRelationDto.getClientKey().getClientId() + "/detail");
        linkedClientModel.setPaymentSetting(personRelationDto.getPermissions());
        if (roleSettingFlag) {
            linkedClientModel.setRoles(getPersonRoles(personRelationDto.getPersonRoles()));
        }
        if (personRelationDto.isPrimaryContactPerson()) {
            linkedClientModel.setPrimaryContactFlag(true);
            serviceOpsModel.setPrimaryContactPerson(linkedClientModel.getFirstName() + " " + linkedClientModel.getLastName());
        }
    }

    /**
     * Populates adviser details into LinkedClientModel for display in account detail page
     * @param serviceOpsModel
     * @return
     */
    private static ArrayList<LinkedClientModel> getAdviserDetails(ServiceOpsModel serviceOpsModel) {
        WrapAccountDetailDto wrapAccountDetailDto = serviceOpsModel.getWrapAccountDetail();
        BrokerDto adviser = wrapAccountDetailDto.getAdviser();
        ArrayList<LinkedClientModel> linkedClientModels = new ArrayList<>();
        LinkedClientModel adviserDetails = new LinkedClientModel();
        if ("BT Invest".equalsIgnoreCase(adviser.getFullName())) {
            serviceOpsModel.setDirectInvestorFlag(true);
            return linkedClientModels;
        }
        adviserDetails.setFirstName(adviser.getFirstName());
        adviserDetails.setLastName(adviser.getLastName());
        adviserDetails.setFullName(adviser.getFullName());
        adviserDetails.setActionMessage("Sign in as " + adviser.getFullName() + " (Adviser)");
        adviserDetails.setAdviserFlag(true);
        adviserDetails.setPostalAddress(getAddressString(adviser.getAddresses(), true));
        for (PersonRelationDto personRelationDto : wrapAccountDetailDto.getSettings()) {
            if (personRelationDto.isAdviser()) {
                adviserDetails.setPaymentSetting(personRelationDto.getPermissions());
                adviserDetails.setDetailPageUrl("../" + personRelationDto.getClientKey().getClientId() + "/detail");
                adviserDetails.setClientId(personRelationDto.getClientKey().getClientId());
            }
        }
        linkedClientModels.add(adviserDetails);
        return linkedClientModels;
    }

    /**
     * Returns the list of roles for an Investor in a String format for display in account detail page
     * @param investorRoles
     * @return
     */
    private static String getPersonRoles(Set<InvestorRole> investorRoles) {
        StringBuilder roleBuilder = new StringBuilder();
        for (InvestorRole investorRole : investorRoles) {
            roleBuilder.append(investorRole.name()).append(", ");
        }
        roleBuilder.setLength(roleBuilder.length() - 2);
        return roleBuilder.toString();
    }

    /**
     * Returns the adviser / investor address details in a String format for display in account detail page
     * @param addressDtos
     * @param isAdviser
     * @return
     */
    private static String getAddressString(List<AddressDto> addressDtos, boolean isAdviser) {
        final String SPACE_DELIMITER = " ";
        final String COMMA_SPACE_DELIMITER = ", ";
        StringBuilder addressBuilder = new StringBuilder();
        for (AddressDto address : addressDtos) {
            if (address.getAddressType().equalsIgnoreCase(AddressTypeV2.POSTAL.name())) {
                if (address.isMailingAddress()) {
                    if (isAdviser) {
                        return addressBuilder.append(addressFieldValidator(address.getCity())).append(SPACE_DELIMITER).append(addressFieldValidator(address.getStateAbbr())).toString();
                    } else {
                        addressBuilder.append(addressFieldValidator(address.getStreetNumber())).append(SPACE_DELIMITER).append(addressFieldValidator(address.getStreetName())).append(SPACE_DELIMITER).append(addressFieldValidator(address.getStreetType())).append(COMMA_SPACE_DELIMITER);
                        addressBuilder.append("".equals(addressFieldValidator(address.getCity())) ? addressFieldValidator(address.getSuburb()) : addressFieldValidator(address.getCity())).append(COMMA_SPACE_DELIMITER).append(addressFieldValidator(address.getStateAbbr())).append(SPACE_DELIMITER).append(addressFieldValidator(address.getPostcode()));
                        break;
                    }
                }
           }
        }
        return addressBuilder.toString();
    }

    private static String addressFieldValidator(String addressField) {
        if (StringUtils.isNotEmpty(addressField)) {
            return addressField;
        } else {
            return "";
        }
    }

    /**
     * Sort retrieved documents in desc order.
     * @param documentList
     * @return
     */
    public static List<DocumentDto> sortDocumentsByUploadedDate(List<DocumentDto> documentList) {
        if (documentList != null)
        {
            Collections.sort(documentList, new Comparator<DocumentDto>() {

                @Override
                public int compare(DocumentDto documentDtoOne, DocumentDto documentDtoTwo) {
                    int compareValue = 0;
                    //check if uploadedDate is not null before comparison
                    if (documentDtoOne.getUploadedDate() != null && documentDtoTwo.getUploadedDate() != null) {
                        compareValue = documentDtoTwo.getUploadedDate().compareTo(documentDtoOne.getUploadedDate());
                    }

                    return compareValue;
                }
            });
        }
        return documentList;
    }

    private static boolean isSuper(WrapAccount account) {
        return AccountStructureType.SUPER.equals(account.getAccountStructureType());
    }

    private static boolean isPension(WrapAccount account) {
        return AccountSubType.PENSION.equals(account.getSuperAccountSubType());
    }
}