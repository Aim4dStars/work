package com.bt.nextgen.api.account.v2.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.account.v2.model.*;
import com.bt.nextgen.api.account.v2.util.PermissionConverter;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.util.ClientDetailDtoConverter;
import com.bt.nextgen.api.product.model.ProductDto;
import com.bt.nextgen.api.product.model.ProductKey;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.account.*;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.bt.nextgen.service.avaloq.domain.RegisteredEntityImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.*;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.*;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.*;

@Deprecated
@Service("WrapAccountDetailDtoServiceV2")
// Surpressing existing issue from V1, refactor/fix scheduled for sprint 1508
@SuppressWarnings("squid:S1200")
public class WrapAccountDetailDtoServiceImpl implements WrapAccountDetailDtoService {
    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService userProfileService;
    
    @Autowired
    @Qualifier("AvaloqTransactionIntegrationServiceImpl")
    private TransactionIntegrationService transactionListService;

	@Autowired
	private AccActivationIntegrationService accActivationIntegrationService;

    @Autowired
    private AccountSubscriptionDtoService accountSubscriptionDtoService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Override
    public WrapAccountDetailDto find(final com.bt.nextgen.api.account.v2.model.AccountKey key, final ServiceErrors serviceErrors) {
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(key.getAccountId()));
        WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);
        WrapAccountDetailDto accountDto = convertToDto(account);
        accountDto.setProduct(this.getProductDto(account.getProductKey(), serviceErrors));
        accountDto.setLinkedAccounts(getLinkedAccountDtoList(account.getLinkedAccounts()));
        Broker broker = brokerIntegrationService.getBroker(BrokerKey.valueOf(account.getAdviserKey().getId()), serviceErrors);
        accountDto.setAdviser(getBrokerDto(broker, serviceErrors));
        List<Client> investors = ((WrapAccountDetailImpl) account).getOwners();
        Collection<PersonRelation> associatedPersons = account.getAssociatedPersons().values();
        accountDto.setOwners(getInvestorDto(accountKey, investors, serviceErrors));
        accountDto.setSettings(getPersonRelationDto(associatedPersons, accountDto.getAdviser(), investors));
        accountDto.setOnboardingDetails(((WrapAccountDetailImpl) account).getOnboardingDetails());
        UserProfile activeProfile = userProfileService.getActiveProfile();
        if (activeProfile.getClientKey() != null) {
            accountDto.setLoggedInClientId(EncodedString.fromPlainTextUsingTL(activeProfile.getClientKey().getId()).toString());
        }
        EncodedString.clearEncodedStringTLMap();
        accountDto.setSubscriptionType(accountSubscriptionDtoService.getSubscriptionType(
                ((WrapAccountDetailImpl) account).getProductSubscription(), productIntegrationService.loadProductsMap(serviceErrors)));
        if (AccountSubscription.SIMPLE.getSubscriptionType().equals(accountDto.getSubscriptionType())) {
            accountDto.setInitialInvestmentAssets(accountSubscriptionDtoService.getInitialInvestments(
                    ((WrapAccountDetailImpl) account).getInitialInvestmentAsset(), serviceErrors));
        }
        return accountDto;
    }

	/**
     * convert domain account to wrapaccountdetail dto
     *
     * @param account
     * @return
     */
    protected WrapAccountDetailDto convertToDto(WrapAccountDetail account) {
        WrapAccountDetailDto accountDto = null;
        if (account != null) {
            com.bt.nextgen.api.account.v2.model.AccountKey key = new com.bt.nextgen.api.account.v2.model.AccountKey(EncodedString
                    .fromPlainText(account.getAccountKey().getId()).toString());
            accountDto = new WrapAccountDetailDto(key, account.getOpenDate(), account.getClosureDate());
            accountDto.setAccountStatus(account.getAccountStatus().getStatus());
            accountDto.setAccountName(account.getAccountName());
            accountDto.setAccountType(account.getAccountStructureType().name());
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
            accountDto.setAdminFeeRate(account.getAdminFeeRate());
            accountDto.setRegisteredSinceDate(setRegsisterdDate(account, account.getAccountStructureType().name()))  ;

            return accountDto;
        }
        return null;
    }



    /**
     * set the product information
     *
     * @param productId
     * @param serviceErrors
     * @return
     */

    protected ProductDto getProductDto(com.bt.nextgen.service.integration.product.ProductKey productId,
                                       ServiceErrors serviceErrors) {
        Product product = productIntegrationService.getProductDetail(productId, serviceErrors);
        ProductDto productDto = new ProductDto();
        ProductKey productKey = new ProductKey(EncodedString.fromPlainText(product.getProductKey().getId()).toString());
        productDto.setKey(productKey);
        productDto.setProductName(product.getProductName());
        return productDto;
    }

    /**
     * @param accounts
     * @return
     */
    protected List<LinkedAccountDto> getLinkedAccountDtoList(List<LinkedAccount> accounts) {
        return Lambda.convert(accounts, new Converter<LinkedAccount, LinkedAccountDto>() {
            public LinkedAccountDto convert(LinkedAccount linkedAccount) {
                LinkedAccountDto linkedAccountDto = new LinkedAccountDto();
                linkedAccountDto.setAccountNumber(linkedAccount.getAccountNumber());
                linkedAccountDto.setLimit(linkedAccount.getLimit());
                linkedAccountDto.setPrimary(linkedAccount.isPrimary());
                linkedAccountDto.setBsb(linkedAccount.getBsb());
                linkedAccountDto.setName(linkedAccount.getName());
                linkedAccountDto.setNickName(linkedAccount.getNickName());
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

        String brokerParentEncodedKey = EncodedString.fromPlainText(broker.getParentKey().getId()).toString();
        com.bt.nextgen.api.broker.model.BrokerKey brokerParentKey = new com.bt.nextgen.api.broker.model.BrokerKey(
                brokerParentEncodedKey);
        brokerDto.setBrokerParentKey(brokerParentKey);
        setBrokerDtoAddresses(brokerUser, brokerDto, serviceErrors);
        return brokerDto;
    }

    protected void setBrokerDtoAddresses(BrokerUser broker, BrokerDto brokerDto, ServiceErrors serviceErrors) {
        List<AddressDto> addressDtos = new ArrayList<AddressDto>();
        List<PhoneDto> phoneDtos = new ArrayList<PhoneDto>();
        List<EmailDto> emailDtos = new ArrayList<EmailDto>();

        if (broker.getAddresses() != null) {
            for (Address addressModel : broker.getAddresses()) {
                AddressDto addressDto = new AddressDto();
                ClientDetailDtoConverter.toAddressDto(addressModel, addressDto, serviceErrors);
                addressDtos.add(addressDto);
            }
        }

        if (broker.getPhones() != null) {
            for (Phone phoneModel : broker.getPhones()) {
                PhoneDto phoneDto = new PhoneDto();
                ClientDetailDtoConverter.toPhoneDto(phoneModel, phoneDto, serviceErrors);
                phoneDtos.add(phoneDto);
            }
        }

        if (broker.getEmails() != null) {
            for (Email emailModel : broker.getEmails()) {
                EmailDto emailDto = new EmailDto();
                ClientDetailDtoConverter.toEmailDto(emailModel, emailDto, serviceErrors);
                emailDtos.add(emailDto);
            }
        }

        brokerDto.setAddresses(addressDtos);
        brokerDto.setEmail(emailDtos);
        brokerDto.setPhone(phoneDtos);

    }

    /**
     * Sets and Returns the personrelationDto retrieving value from broker (OE)
     * and person details
     *
     * @param personRelations
     * @param broker
     * @param owners
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

            Set<TransactionPermission> permissions = personRelation.getPermissions();
            setPermission(permissions, personRelationDto);

            personRelationDtoList.add(personRelationDto);
        }

        return personRelationDtoList;
    }

    /**
     * Sets the permission of the persons associated with the account in the
     * personrelationdto object
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
     * @return
     */
    protected List<InvestorDto> getInvestorDto(final com.bt.nextgen.service.integration.account.AccountKey key, List<Client> owners, final ServiceErrors serviceErrors) {
        return Lambda.convert(owners, new Converter<ClientDetail, InvestorDto>() {
            public InvestorDto convert(ClientDetail client) {
                List<AssociatedPerson> fullAssociatedPersons = fullAssociatedPersonsForTrusteesAndDirectors(key, client, serviceErrors);
                return (InvestorDto) ClientDetailDtoConverter.toClientDto(client, fullAssociatedPersons, staticIntegrationService, serviceErrors);
            }
        });
    }


    private @Nullable List<AssociatedPerson> fullAssociatedPersonsForTrusteesAndDirectors(
            final com.bt.nextgen.service.integration.account.AccountKey key, final ClientDetail clientModel,
            final ServiceErrors serviceErrors) {
        List<AssociatedPerson> fullAssociatedPersons = null;
        if (clientModel instanceof Smsf) { // TODO: perhaps we can do better if ((Smsf) clientModel).getTrustees(); also returns directors
            WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
            wrapAccountIdentifier.setBpId(key.getId());
            UserProfile activeProfile=userProfileService.getActiveProfile();
            fullAssociatedPersons = accActivationIntegrationService.loadAccApplicationForPortfolio(wrapAccountIdentifier,activeProfile.getJobRole(),activeProfile.getClientKey(),
                    serviceErrors); // expensive (2sec)
        }
        return fullAssociatedPersons;
    }


    /**
     * @param owners
     * @param investorKey
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
            response = accountService.updatePrimaryContact(request, serviceErrors);
        } else if (request.getCGTLMethod() != null) {
            response = accountService.updateTaxPreference(request, serviceErrors);
        }
        return createUpdateBPDetailsResponse(response, wrapAccountDetailDto);
    }

    private UpdateBPDetailsRequest createRequestToUpdateBPDetails(WrapAccountDetailDto wrapAccountDetailDto) {
        UpdateBPDetailsRequest request = new UpdateBPDetailsImpl();
        com.bt.nextgen.api.account.v2.model.AccountKey key = wrapAccountDetailDto.getKey();
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

        List<Client> investors = ((WrapAccountDetailImpl) account).getOwners();

        switch(accountType) {

            case "Company":
            case "SMSF":
            case "Trust":

                return calculateRegisteredDate(investors, account);


            case "Individual":
            case "Joint":

                return account.getSignDate();

            default:

        }

        return  account.getSignDate();
    }

    private DateTime calculateRegisteredDate(List< Client> investors, WrapAccountDetail account ) {

        for(Client client :  investors){

            if(client instanceof RegisteredEntityImpl ) {

                RegisteredEntityImpl cmpnyInvestor = (RegisteredEntityImpl)client;
                return processRegisteredDate(cmpnyInvestor, account);

            }

        }

        return account.getSignDate();
    }

    private DateTime processRegisteredDate(RegisteredEntityImpl client, WrapAccountDetail account  ) {

        if (account.getSignDate() == null) {
            return null;
        }

        if (client.getRegistrationDate() != null && client.getRegistrationDate().after(account.getSignDate().toDate())) {
            return new DateTime(client.getRegistrationDate());
        }

        return new DateTime(account.getSignDate());
    }

}
