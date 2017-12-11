package com.bt.nextgen.api.account.v3.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.account.v3.model.AccountPaymentPermission;
import com.bt.nextgen.api.account.v3.model.AccountantDto;
import com.bt.nextgen.api.account.v3.model.LinkedAccountDto;
import com.bt.nextgen.api.account.v3.model.MigrationDetailsDto;
import com.bt.nextgen.api.account.v3.model.PensionDetailsDto;
import com.bt.nextgen.api.account.v3.model.PersonRelationDto;
import com.bt.nextgen.api.account.v3.model.TaxAndPreservationDetailsDto;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.bt.nextgen.api.account.v3.util.PermissionConverter;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.v2.model.InvestorDto;
import com.bt.nextgen.api.client.v2.util.ClientDetailDtoConverter;
import com.bt.nextgen.api.movemoney.v3.util.DepositUtils;
import com.bt.nextgen.api.smsf.service.AccountingSoftwareDtoConverter;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.account.CGTLMethod;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.account.UpdateAccountDetailResponseImpl;
import com.bt.nextgen.service.avaloq.account.UpdateBPDetailsImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.domain.RegisteredEntityImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.bt.nextgen.service.integration.account.UpdateAccountDetailResponse;
import com.bt.nextgen.service.integration.account.UpdateBPDetailsRequest;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.domain.Smsf;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.Person;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.client.dto.account.WrapAccountDetailClientImpl;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service("WrapAccountDetailDtoServiceV3")
// TODO Suppression copied from v2 - this still needs to be fixed
@SuppressWarnings("squid:S1200")
public class WrapAccountDetailDtoServiceImpl implements WrapAccountDetailDtoService {
    private static final int DECIMAL_POINTS_PERCENTAGE = 2;
    private static final int PERCENTAGE_100 = 100;
    private static final int DECIMAL_POINTS_AMOUNT_TAX_REPORT = 2;

    @Autowired
    private AvaloqAccountIntegrationServiceFactory avaloqAccountIntegrationServiceFactory;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private AccActivationIntegrationService accActivationIntegrationService;

    @Autowired
    private AccountProductsHelper accountProductsHelper;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    /**
     * Search using a list of criteria.
     * 
     * @param criteriaList
     *            List of criteria.
     * @param serviceErrors
     *            Object to store errors.
     * 
     * @return Single result of the search.
     */
    @Override
    public WrapAccountDetailDto search(List<ApiSearchCriteria> criteriaList, final ServiceErrors serviceErrors) {
        String accountId = null;
        String useCache = null;

        for (ApiSearchCriteria criteria : criteriaList) {
            if (Attribute.ACCOUNT_ID.equalsIgnoreCase(criteria.getProperty())) {
                accountId = criteria.getValue();
            } else if (Attribute.CACHE.equalsIgnoreCase(criteria.getProperty())) {
                useCache = criteria.getValue();
            }
        }

        final com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(accountId));

        final WrapAccountDetail account = avaloqAccountIntegrationServiceFactory.getInstance(useCache).loadWrapAccountDetail(
                accountKey, serviceErrors);

        final WrapAccountDetailDto accountDto = convertToDto(account);
        accountDto.setProduct(accountProductsHelper.getProductDto(account, serviceErrors));
        accountDto.setLinkedAccounts(getLinkedAccountDtoList(account.getLinkedAccounts()));
        Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(account.getAdviserKey().getId()), serviceErrors);
        accountDto.setAdviser(getBrokerDto(broker, serviceErrors));
        if (account.getAccntPersonId() != null) {
            Person brokerUser = brokerIntegrationService.getPersonDetailsOfBrokerUser(account.getAccntPersonId(), serviceErrors);
            accountDto.setAccountant(getAccountantDetails(account, brokerUser, serviceErrors));
        }

        //Modified for client-account OffThread Implementation as OffThread response type will be WrapAccountDetailClientImpl
        List<Client> investors;
        if (account instanceof WrapAccountDetailClientImpl) {
            investors = ((WrapAccountDetailClientImpl) account).getOwners();
            accountDto.setOnboardingDetails(((WrapAccountDetailClientImpl) account).getOnboardingDetails());
        } else {
            investors = ((WrapAccountDetailImpl) account).getOwners();
            accountDto.setOnboardingDetails(((WrapAccountDetailImpl) account).getOnboardingDetails());
        }
        Collection<PersonRelation> associatedPersons = account.getAssociatedPersons().values();
        if (null != investors) {
            accountDto.setOwners(getInvestorDto(accountKey, investors, serviceErrors));
        }
        accountDto.setSettings(getPersonRelationDto(associatedPersons, accountDto.getAdviser(), investors));
        UserProfile activeProfile = userProfileService.getActiveProfile();
        if (activeProfile.getClientKey() != null) {
            accountDto.setLoggedInClientId(EncodedString.fromPlainTextUsingTL(activeProfile.getClientKey().getId()).toString());
        }
        EncodedString.clearEncodedStringTLMap();
        accountDto.setSubscriptionType(accountProductsHelper.getSubscriptionType(account, serviceErrors));
        accountDto.setInitialInvestments(accountProductsHelper.getInitialInvestments(account, serviceErrors));
        accountDto.setPersonalBillerCode(account.getPersonalBillerCode());
        accountDto.setSpouseBillerCode(account.getSpouseBillerCode());
        accountDto.setTypeId(accountProductsHelper.getAccountFeatureKey(account, serviceErrors));
        return accountDto;
    }

    protected AccountantDto getAccountantDetails(WrapAccountDetail account, Person brokerUser, ServiceErrors serviceErrors) {
        AccountantDto brokerDto = new AccountantDto();
        brokerDto.setFirstName(brokerUser.getFirstName());
        brokerDto.setMiddleName(brokerUser.getMiddleName());
        brokerDto.setLastName(brokerUser.getLastName());
        brokerDto.setCorporateName(brokerUser.getCorporateName());

        if (userProfileService.isInvestor()) {
            brokerDto.setDisplayName(brokerDto.getCorporateName());
        } else {
            brokerDto.setDisplayName(brokerDto.getFullName());
        }

        if (account.getSubAccounts() != null) {
            for (SubAccount subAccount : account.getSubAccounts()) {
                if (subAccount.getSubAccountType() != null && subAccount.getSubAccountType().equals(ContainerType.EXTERNAL_ASSET)) {
                    brokerDto.setExternalAssetsFeedState(SoftwareFeedStatus.getDisplayValueFor(
                            subAccount.getExternalAssetsFeedState()).getDisplayValue());
                    if (subAccount.getAccntSoftware() != null) {
                        brokerDto.setAccountingSoftware(subAccount.getAccntSoftware());
                        Collection<Code> categoryCodes = staticIntegrationService.loadCodes(CodeCategory.EXT_HOLDING_SRC,
                                serviceErrors);
                        brokerDto.setAccountingSoftwareDisplayName(AccountingSoftwareDtoConverter.getSoftwareDisplayName(
                                categoryCodes, subAccount.getAccntSoftware().toLowerCase()));
                        break;
                    }
                }
            }
        }
        setBrokerDtoAddresses(brokerUser, brokerDto);
        return brokerDto;
    }

    /**
     * convert domain account to wrapaccountdetail dto
     * 
     * @param account
     * 
     * @return
     */
    protected WrapAccountDetailDto convertToDto(WrapAccountDetail account) {
        final WrapAccountDetailDto accountDto;
        if (account != null) {
            com.bt.nextgen.api.account.v3.model.AccountKey key = new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString
                    .fromPlainText(account.getAccountKey().getId()).toString());
            accountDto = new WrapAccountDetailDto(key, account.getOpenDate(), account.getClosureDate());
            accountDto.setAccountStatus(account.getAccountStatus().getStatus());
            accountDto.setAccountName(account.getAccountName());
            accountDto.setAccountType(account.getAccountStructureType().name());
            accountDto.setSuperAccountSubType((account.getSuperAccountSubType() != null ? account.getSuperAccountSubType()
                    .getIntlId() : null));
            accountDto.setSignDate(account.getSignDate());
            accountDto.setBsb(account.getBsb());
            accountDto.setBillerCode(account.getBillerCode());
            accountDto.setcGTLMethodId(account.getcGTLMethod().getIntlId());
            accountDto.setcGTLMethod(account.getcGTLMethod().getDescription());
            accountDto.setAccountNumber(account.getAccountNumber());
            accountDto.setModificationSeq(account.getModificationSeq());
            accountDto.setMinCashAmount(account.getMinCashAmount());
            accountDto.setHasMinCash(account.isHasMinCash());
            if (account.getTaxLiability() != null) {
                accountDto.setTaxLiability(account.getTaxLiability().getName());
            }
            accountDto.setInTransition(Boolean.FALSE);
            if (account instanceof WrapAccountImpl) {
                accountDto.setInTransition(((WrapAccountImpl) account).getIsInTransition());
            }
            accountDto.setAdminFeeRate(account.getAdminFeeRate());
            accountDto.setRegisteredSinceDate(setRegsisterdDate(account, account.getAccountStructureType().name()));
            accountDto.setHasIhin(account.getHasIhin());
            accountDto.setIhin(account.getIhin());
            accountDto.setStatementPref(account.getStatementPref());
            accountDto.setCmaStatementPref(account.getCmaStatementPref());
            setTaxDetails(account, accountDto);
            setPensionDetails(account, accountDto);
            setMigrationDetails(account, accountDto);
            return accountDto;
        }
        return null;
    }

    /**
     * @param accounts
     * 
     * @return
     */
    protected List<LinkedAccountDto> getLinkedAccountDtoList(List<LinkedAccount> accounts) {
        return Lambda.convert(accounts, new Converter<LinkedAccount, LinkedAccountDto>() {
            @Override
            public LinkedAccountDto convert(LinkedAccount linkedAccount) {
                LinkedAccountDto linkedAccountDto = new LinkedAccountDto();
                linkedAccountDto.setAccountNumber(linkedAccount.getAccountNumber());
                linkedAccountDto.setLimit(linkedAccount.getLimit());
                linkedAccountDto.setPrimary(linkedAccount.isPrimary());
                linkedAccountDto.setBsb(linkedAccount.getBsb());
                linkedAccountDto.setName(linkedAccount.getName());
                linkedAccountDto.setNickName(linkedAccount.getNickName());
                linkedAccountDto.setPensionPayment(linkedAccount.isPensionPayment());
                linkedAccountDto.setLinkedAccountStatus(DepositUtils.linkedAccountStatus(linkedAccount,staticIntegrationService));
                return linkedAccountDto;
            }
        });
    }

    protected BrokerDto getBrokerDto(Broker broker, ServiceErrors serviceErrors) {
        BrokerDto brokerDto = new BrokerDto();
        BrokerUser brokerUser = brokerIntegrationService.getAdviserBrokerUser(broker.getKey(), serviceErrors);
        BrokerKey key = broker.getKey();
        String encodedKey = EncodedString.fromPlainText(key.getId()).toString();
        com.bt.nextgen.api.broker.model.BrokerKey brokerKey = new com.bt.nextgen.api.broker.model.BrokerKey(encodedKey);
        brokerDto.setKey(brokerKey);
        brokerDto.setFirstName(brokerUser.getFirstName());
        brokerDto.setMiddleName(brokerUser.getMiddleName());
        brokerDto.setLastName(brokerUser.getLastName());
        brokerDto.setCorporateName(brokerUser.getCorporateName());

        if (userProfileService.isInvestor()) {
            brokerDto.setDisplayName(brokerDto.getCorporateName());
        } else {
            brokerDto.setDisplayName(brokerDto.getFullName());
        }

        String brokerParentEncodedKey = EncodedString.fromPlainText(broker.getParentKey().getId()).toString();
        com.bt.nextgen.api.broker.model.BrokerKey brokerParentKey = new com.bt.nextgen.api.broker.model.BrokerKey(
                brokerParentEncodedKey);
        brokerDto.setBrokerParentKey(brokerParentKey);
        setBrokerDtoAddresses(brokerUser, brokerDto);
        return brokerDto;
    }

    protected void setBrokerDtoAddresses(Person broker, BrokerDto brokerDto) {
        List<AddressDto> addressDtos = new ArrayList<AddressDto>();
        List<PhoneDto> phoneDtos = new ArrayList<PhoneDto>();
        List<EmailDto> emailDtos = new ArrayList<EmailDto>();

        if (broker.getAddresses() != null) {
            for (Address addressModel : broker.getAddresses()) {
                AddressDto addressDto = new AddressDto();
                ClientDetailDtoConverter.toAddressDto(addressModel, addressDto);
                addressDtos.add(addressDto);
            }
        }

        if (broker.getPhones() != null) {
            for (Phone phoneModel : broker.getPhones()) {
                PhoneDto phoneDto = new PhoneDto();
                ClientDetailDtoConverter.toPhoneDto(phoneModel, phoneDto);
                phoneDtos.add(phoneDto);
            }
        }

        if (broker.getEmails() != null) {
            for (Email emailModel : broker.getEmails()) {
                EmailDto emailDto = new EmailDto();
                ClientDetailDtoConverter.toEmailDto(emailModel, emailDto);
                emailDtos.add(emailDto);
            }
        }

        brokerDto.setAddresses(addressDtos);
        brokerDto.setEmail(emailDtos);
        brokerDto.setPhone(phoneDtos);

    }

    /**
     * Sets and Returns the personrelationDto retrieving value from broker (OE) and person details
     * 
     * @param personRelations
     * @param broker
     * @param owners
     * 
     * @return
     */
    protected List<PersonRelationDto> getPersonRelationDto(final Collection<PersonRelation> personRelations,
                                                           final BrokerDto broker, final List<Client> owners) {

        List<PersonRelationDto> personRelationDtoList = new ArrayList<>();
        for (PersonRelation personRelation : personRelations) {

            PersonRelationDto personRelationDto = new PersonRelationDto();
            personRelationDto.setApprover(personRelation.isApprover());
            personRelationDto.setAdviser(personRelation.isAdviser());
            personRelationDto.setPrimaryContactPerson(personRelation.isPrimaryContact());
            Set<InvestorRole> roles = personRelation.getPersonRoles();
            personRelationDto.setPersonRoles(roles);
            if (null != owners) {
                InvestorDetail client = getRelatedPerson(owners, personRelation);
                if (client == null) {
                    personRelationDto.setClientKey(new com.bt.nextgen.api.client.model.ClientKey(EncodedString.fromPlainTextUsingTL(
                            personRelation.getClientKey().getId()).toString()));
                    if (personRelation.isAdviser() && broker != null) {
                        personRelationDto.setName(broker.getFullName());
                    }
                } else {
                    personRelationDto.setName(client.getFullName());
                    personRelationDto.setClientKey(new com.bt.nextgen.api.client.model.ClientKey((EncodedString
                            .fromPlainTextUsingTL(client.getClientKey().getId())).toString()));
                }
            }

            Set<TransactionPermission> permissions = personRelation.getPermissions();
            setPermission(permissions, personRelationDto);

            personRelationDtoList.add(personRelationDto);
        }

        return personRelationDtoList;
    }

    /**
     * Sets the permission of the persons associated with the account in the personrelationdto object
     * 
     * @param permissions
     * @param personRelationDto
     */
    private void setPermission(Set<TransactionPermission> permissions, PersonRelationDto personRelationDto) {
        String personPermission = Constants.EMPTY_STRING;
        if (!CollectionUtils.isEmpty(permissions)) {
            PermissionConverter permissionConverter = new PermissionConverter(permissions, personRelationDto.isAdviser());
            AccountPaymentPermission permission = permissionConverter.getAccountPermission();
            if (permission != null) {
                personPermission = permission.getPermissionDesc();
            }
            personRelationDto.setPermissions(personPermission);
        }
    }

    /**
     * Retrieve the related person name from the client for account permissions
     * 
     * @param investors
     * @param personRelation
     * 
     * @return
     */
    private InvestorDetail getRelatedPerson(List<Client> investors, PersonRelation personRelation) {
        if (personRelation.isAdviser()) {
            return null;
        } else {
            InvestorDetail client = null;
            ClientDetail clientDetail = findPersonFromOwnerList(investors, personRelation.getClientKey());
            if (clientDetail != null && clientDetail instanceof InvestorDetail) {
                client = (InvestorDetail) clientDetail;
            }
            return client;
        }
    }

    /**
     * @param owners
     * @param serviceErrors
     * 
     * @return
     */
    protected List<InvestorDto> getInvestorDto(final com.bt.nextgen.service.integration.account.AccountKey key,
                                               List<Client> owners, final ServiceErrors serviceErrors) {
        return Lambda.convert(owners, new Converter<ClientDetail, InvestorDto>() {
            @Override
            public InvestorDto convert(ClientDetail client) {
                List<AssociatedPerson> fullAssociatedPersons = fullAssociatedPersonsForTrusteesAndDirectors(key, client,
                        serviceErrors);
                return (InvestorDto) ClientDetailDtoConverter.toClientDto(client, fullAssociatedPersons);
            }
        });
    }

    private @Nullable
    List<AssociatedPerson> fullAssociatedPersonsForTrusteesAndDirectors(
            final com.bt.nextgen.service.integration.account.AccountKey key, final ClientDetail clientModel,
            final ServiceErrors serviceErrors) {
        List<AssociatedPerson> fullAssociatedPersons = null;
        if (clientModel instanceof Smsf) { // TODO: perhaps we can do better if ((Smsf) clientModel).getTrustees(); also returns
            // directors
            WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
            wrapAccountIdentifier.setBpId(key.getId());
            UserProfile activeProfile= userProfileService.getActiveProfile();
            fullAssociatedPersons = accActivationIntegrationService.loadAccApplicationForPortfolio(wrapAccountIdentifier,activeProfile.getJobRole(),activeProfile.getClientKey(),
                    serviceErrors); // expensive (2sec)
        }
        return fullAssociatedPersons;
    }

    /**
     * @param owners
     * @param investorKey
     * 
     * @return Client detail
     */
    private ClientDetail findPersonFromOwnerList(List<? extends Client> owners, ClientKey investorKey) {
        for (Client owner : owners) {
            ClientDetail client = (ClientDetail) owner;
            // Individual persons should match here and Legal Person match done
            // in else part
            if (client.getClientKey().equals(investorKey)) {
                return client;
            } else {
                // To avoid individual in this match loop - the individual
                // should be handled above itself
                if (!InvestorType.INDIVIDUAL.equals(((InvestorDetail) client).getInvestorType())) {
                    Collection<ClientDetail> relatedPersons = client.getRelatedPersons();

                    if (!CollectionUtils.isEmpty(relatedPersons)) {
                        for (Iterator<ClientDetail> iterator = relatedPersons.iterator(); iterator.hasNext();) {
                            ClientDetail relatedClient = iterator.next();
                            if (relatedClient.getClientKey().equals(investorKey)) {
                                return relatedClient;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public WrapAccountDetailDto update(WrapAccountDetailDto wrapAccountDetailDto, ServiceErrors serviceErrors) {
        UpdateAccountDetailResponse response = new UpdateAccountDetailResponseImpl();
        UpdateBPDetailsRequest request = createRequestToUpdateBPDetails(wrapAccountDetailDto);
        if (request.getPrimaryContactPersonId() != null) {
            response = avaloqAccountIntegrationServiceFactory.getInstance(null).updatePrimaryContact(request, serviceErrors);
        } else if (request.getCGTLMethod() != null) {
            response = avaloqAccountIntegrationServiceFactory.getInstance(null).updateTaxPreference(request, serviceErrors);
        } else if (StringUtils.isNotEmpty(request.getStatementPref()) || StringUtils.isNotEmpty(request.getCmaStatementPref())) {
            response = avaloqAccountIntegrationServiceFactory.getInstance(null).updateStmtCorrespondence(request, serviceErrors);
        }
        return createUpdateBPDetailsResponse(response, wrapAccountDetailDto);
    }

    private UpdateBPDetailsRequest createRequestToUpdateBPDetails(WrapAccountDetailDto wrapAccountDetailDto) {
        UpdateBPDetailsRequest request = new UpdateBPDetailsImpl();
        com.bt.nextgen.api.account.v3.model.AccountKey key = wrapAccountDetailDto.getKey();
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(key.getAccountId()));
        request.setAccountKey(accountKey);
        request.setModificationIdentifier(new BigDecimal(wrapAccountDetailDto.getModificationSeq()));
        if (wrapAccountDetailDto.getPrimaryContact() != null) {
            request.setPrimaryContactPersonId(ClientKey.valueOf(EncodedString.toPlainText(wrapAccountDetailDto
                    .getPrimaryContact().getKey().getClientId())));
        }
        if (wrapAccountDetailDto.getcGTLMethodId() != null) {
            request.setCGTLMethod(CGTLMethod.getcGTLMethod(wrapAccountDetailDto.getcGTLMethodId()));
        }
        if (StringUtils.isNotEmpty(wrapAccountDetailDto.getStatementPref())) {
            request.setStatementPref(wrapAccountDetailDto.getStatementPref());
        }
        if (StringUtils.isNotEmpty(wrapAccountDetailDto.getCmaStatementPref())) {
            request.setCmaStatementPref(wrapAccountDetailDto.getCmaStatementPref());
        }
        return request;
    }

    private WrapAccountDetailDto createUpdateBPDetailsResponse(UpdateAccountDetailResponse response,
                                                               WrapAccountDetailDto wrapAccountDetailDto) {
        if (response.isUpdatedFlag()) {
            wrapAccountDetailDto.setModificationSeq(response.getModificationIdentifier().toString());
            if (wrapAccountDetailDto.getcGTLMethodId() != null) {
                wrapAccountDetailDto.setcGTLMethod(CGTLMethod.getcGTLMethod(wrapAccountDetailDto.getcGTLMethodId())
                        .getDescription());
            }
        }
        return wrapAccountDetailDto;
    }

    private DateTime setRegsisterdDate(WrapAccountDetail account, String accountType) {

        List<Client> investors = Collections.emptyList();

        //Check if response is from OffThread as client-account-service-client is returns other type.
        //TODO : need to find way to return exact type from client.
        if (account instanceof WrapAccountDetailClientImpl) {
            investors = ((WrapAccountDetailClientImpl) account).getOwners();
        } else {
            investors = ((WrapAccountDetailImpl) account).getOwners();
        }

        switch (accountType) {

            case "Company":
            case "SMSF":
            case "Trust":
                calculateRegisteredDate(investors, account);
                break;

            case "Individual":
            case "Joint":

                return account.getSignDate();

            default:

        }

        return account.getSignDate();
    }

    private DateTime calculateRegisteredDate(List<Client> investors, WrapAccountDetail account) {
        // OffThread client-account service response having owners as null.
        // TODO: Check Json serialization logic in client-account-service side and remove null check for offthread
        if (null != investors) {
            for (Client client : investors) {
                if (client instanceof RegisteredEntityImpl) {
                    RegisteredEntityImpl cmpnyInvestor = (RegisteredEntityImpl) client;
                    return processRegisteredDate(cmpnyInvestor, account);
                }
            }
        }

        return account.getSignDate();
    }

    private DateTime processRegisteredDate(RegisteredEntityImpl client, WrapAccountDetail account) {

        if (null != client.getRegistrationDate()) {

            if (client.getRegistrationDate().after(account.getSignDate().toDate())) {

                return new DateTime(client.getRegistrationDate());

            } else {
                return new DateTime(account.getSignDate());
            }
        } else {

            return new DateTime(account.getSignDate());

        }
    }

    /**
     * Set the tax and preservation details of the account
     * 
     * @param account
     * @param accountDetailDto
     * 
     * @return
     */
    private void setTaxDetails(WrapAccountDetail account, WrapAccountDetailDto accountDetailDto) {
        TaxAndPreservationDetailsDto tax = new TaxAndPreservationDetailsDto();

        tax.setNonTaxableAmount(getSafeValue(account.getTaxFreeComponent()).setScale(DECIMAL_POINTS_AMOUNT_TAX_REPORT,
                RoundingMode.HALF_UP));
        tax.setTaxableAmount(getSafeValue(account.getTaxableComponent()).setScale(DECIMAL_POINTS_AMOUNT_TAX_REPORT,
                RoundingMode.HALF_UP));
        tax.setTotalAmount(getSafeValue(tax.getTaxableAmount()).add(getSafeValue(tax.getNonTaxableAmount())));
        if (getSafeValue(tax.getTotalAmount()).compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxablePercentage = getSafeValue(tax.getTaxableAmount())
                    .divide(getSafeValue(tax.getTotalAmount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(PERCENTAGE_100))
                    .setScale(DECIMAL_POINTS_PERCENTAGE);

            BigDecimal nonTaxablePercentage = getSafeValue(tax.getNonTaxableAmount())
                    .divide(getSafeValue(tax.getTotalAmount()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(PERCENTAGE_100))
                    .setScale(DECIMAL_POINTS_PERCENTAGE);
            resetPercentageValues(tax, taxablePercentage, nonTaxablePercentage);
        }

        tax.setPreservedAmount(account.getPreservedAmount());
        tax.setRestrictedNonPreservedAmount(account.getRestrictedNonPreservedAmount());
        tax.setUnrestrictedNonPreservedAmount(account.getUnrestrictedNonPreservedAmount());
        tax.setEligibleServiceDate(account.getEligibleServiceDate());
        // TODO service date

        accountDetailDto.setTaxAndPreservationDetails(tax);
    }

    /**
     * Set the pension details of the account
     * 
     * @param account
     * @param accountDetailDto
     * 
     * @return
     */
    private void setPensionDetails(WrapAccountDetail account, WrapAccountDetailDto accountDetailDto) {
        final PensionDetailsDto pension = new PensionDetailsDto();

        if (account instanceof PensionAccountDetail) {
            final PensionAccountDetail pensionAccount = (PensionAccountDetail) account;

            pension.setCommencementPending(pensionAccount.isCommencementPending());
            pension.setCommencementDate(pensionAccount.getCommenceDate());
            pension.setCommencementValue(pensionAccount.getCommenceValue());
            pension.setAccountBalance(account.getAccntBalance());
            pension.setMinAmount(pensionAccount.getMinimumAmount());
            pension.setMaxAmount(pensionAccount.getMaximumAmount());
            pension.setPensionReviewInProgress(pensionAccount.isPensionReviewInProgress());
            pension.setAccountBalanceDate(pensionAccount.getAccountBalanceDate());

            pension.setEstimatedRolloverCount(pensionAccount.getEstimatedRolloverCount());
            pension.setActualRolloverCount(pensionAccount.getActualRolloverCount());

            if (pensionAccount.getPensionType() != null) {
                pension.setPensionType(pensionAccount.getPensionType().getValue());
            }

            // Life Expectancy - Centre link
            if (pensionAccount.getLifeExpectancyCentreLinkRelevantNumber() != null) {
                pension.setLifeExpectancyCentrelinkSchedule(pensionAccount.getLifeExpectancyCentreLinkRelevantNumber().setScale(
                        DECIMAL_POINTS_PERCENTAGE, RoundingMode.HALF_UP));
            }

            pension.setPensionPaidYtd(pensionAccount.getPaymentPaidYtd());
            pension.setProjectedPensionPayment(pensionAccount.getTotalProjectedPayment());
            pension.setTaxYtd(pensionAccount.getTaxYtd());
            pension.setUsableCash(pensionAccount.getUsableCash());
            pension.setLumpSumUsableCash(pensionAccount.getLumpSumUsableCash());

            if (pensionAccount.getIndexationType() != null) {
                pension.setIndexationType(pensionAccount.getIndexationType().getLabel());
                pension.setIndexationAmount(pensionAccount.getIndexationType() == IndexationType.PERCENTAGE ? pensionAccount
                        .getIndexationPercent() : pensionAccount.getIndexationAmount());

            }

            setPensionPaymentDetails(pensionAccount, pension);
        }

        accountDetailDto.setPensionDetails(pension);
    }

    //TODO to be added after change in BP_details XSD for wrap migration.
    private void setMigrationDetails(WrapAccountDetail account, WrapAccountDetailDto accountDetailDto) {
        if (StringUtils.isNotBlank(account.getMigrationKey())) {
            MigrationDetailsDto migrationAccount = new MigrationDetailsDto();
            migrationAccount.setAccountId(account.getMigrationKey());
            migrationAccount.setMigrationDate(account.getMigrationDate());
            migrationAccount.setSourceId(account.getMigrationSourceId() != null ? account.getMigrationSourceId().getName() : null);
            accountDetailDto.setMigrationDetails(migrationAccount);
        }
    }

    private void setPensionPaymentDetails(PensionAccountDetail pensionAccount, PensionDetailsDto pension) {
        if (pensionAccount.getPaymentType() != null) {
            pension.setPaymentType(pensionAccount.getPaymentType().getLabel());
        }

        pension.setFirstPaymentDate(pensionAccount.getFirstPaymentDate());

        if (pensionAccount.getFirstPaymentDate() != null) {
            // number of days from today
            pension.setDaysToFirstPayment(Days.daysBetween(DateTime.now().withTimeAtStartOfDay(),
                    pensionAccount.getFirstPaymentDate().withTimeAtStartOfDay()).getDays());
        }

        pension.setPaymentAmount(pensionAccount.getPaymentAmount());
        pension.setNextPaymentDate(pensionAccount.getNextPaymentDate());
        pension.setNextPaymentAmount(pensionAccount.getNextPaymentAmount());

        if (pensionAccount.getPaymentFrequency() != null) {
            pension.setPaymentFrequency(pensionAccount.getPaymentFrequency().getDescription());
        }
    }

    private BigDecimal getSafeValue(BigDecimal value) {
        if (value != null) {
            return value;
        }
        return BigDecimal.ZERO;
    }

    private void resetPercentageValues(TaxAndPreservationDetailsDto taxDetails, BigDecimal taxablePercentage,
                                       BigDecimal nonTaxablePercentage) {
        final MathContext scale = new MathContext(DECIMAL_POINTS_PERCENTAGE, RoundingMode.HALF_UP);
        final BigDecimal hundred = new BigDecimal(PERCENTAGE_100);
        final BigDecimal totalPercentage = taxablePercentage.add(nonTaxablePercentage);
        final BigDecimal fraction = hundred.subtract(totalPercentage, scale);

        taxDetails.setTaxablePercentage(taxablePercentage);
        taxDetails.setNonTaxablePercentage(nonTaxablePercentage);

        if (fraction.compareTo(BigDecimal.ZERO.setScale(DECIMAL_POINTS_PERCENTAGE)) != 0) {
            if (taxablePercentage.compareTo(nonTaxablePercentage) > 0) {
                taxDetails.setTaxablePercentage(taxDetails.getTaxablePercentage().add(fraction));
            } else {
                taxDetails.setNonTaxablePercentage(taxDetails.getNonTaxablePercentage().add(fraction));
            }
        }
    }
}
