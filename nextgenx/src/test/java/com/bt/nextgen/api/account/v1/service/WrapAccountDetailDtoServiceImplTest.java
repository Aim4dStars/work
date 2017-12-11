package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.WrapAccountDetailDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.CGTLMethod;
import com.bt.nextgen.service.avaloq.account.LinkedAccountImpl;
import com.bt.nextgen.service.avaloq.account.PersonRelationImpl;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.TaxLiability;
import com.bt.nextgen.service.avaloq.account.UpdateAccountDetailResponseImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.account.OnboardingDetails;
import com.bt.nextgen.service.integration.account.OnboardingDetailsType;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.account.UpdateAccountDetailResponse;
import com.bt.nextgen.service.integration.account.UpdatePrimContactRequest;
import com.bt.nextgen.service.integration.account.UpdateStmtCorrespondencePrefRequest;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WrapAccountDetailDtoServiceImplTest {
    @InjectMocks
    private WrapAccountDetailDtoServiceImpl wrapAccountDetailDtoService;

    @Mock
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    UpdateAccountDetailResponse response = null;
    ServiceErrors serviceErrors;
    UserProfile activeProfile;
    BrokerUser brokerUser;

    @Before
    public void setup() {
        serviceErrors = new FailFastErrorsImpl();
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        final List<Address> addresses = new ArrayList<>();
        final List<Phone> phones = new ArrayList<>();
        final List<Email> emails = new ArrayList<>();

        brokerUser = new BrokerUser() {
            @Override
            public Collection<BrokerRole> getRoles() {
                return null;
            }

            @Override
            public boolean isRegisteredOnline() {
                return false;
            }

            @Deprecated
            public String getPracticeName() {
                return null;
            }

            @Override
            public String getEntityId() {
                return null;
            }

            @Override
            public DateTime getReferenceStartDate() {
                return null;
            }

            @Override
            public String getFirstName() {
                return "person-120_505";
            }

            @Override
            public String getMiddleName() {
                return "person-120_505";
            }

            @Override
            public String getLastName() {
                return "person-120_505";
            }

            @Override
            public String getBankReferenceId() {
                return null;
            }

            @Override
            public UserKey getBankReferenceKey() {
                return null;
            }

            @Override
            public Collection<com.bt.nextgen.service.integration.account.AccountKey> getWrapAccounts() {
                return null;
            }

            @Override
            public Collection<ClientDetail> getRelatedPersons() {
                return null;
            }

            @Override
            public List<Email> getEmails() {
                return emails;
            }

            @Override
            public List<Phone> getPhones() {
                return phones;
            }

            @Override
            public int getAge() {
                return 0;
            }

            @Override
            public Gender getGender() {
                return null;
            }

            @Override
            public DateTime getDateOfBirth() {
                return null;
            }

            @Override
            public boolean isRegistrationOnline() {
                return false;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public String getSafiDeviceId() {
                return null;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public String getGcmId() {
                return null;
            }

            @Override
            public DateTime getOpenDate() {
                return null;
            }

            @Override
            public DateTime getCloseDate() {
                return null;
            }

            @Override
            public String getFullName() {
                return null;
            }

            @Override
            public ClientType getClientType() {
                return null;
            }

            @Override
            public List<Address> getAddresses() {
                return addresses;
            }

            @Override
            public InvestorType getLegalForm() {
                return null;
            }

            @Override
            public com.bt.nextgen.service.integration.userinformation.ClientKey getClientKey() {
                return null;
            }

            @Override
            public void setClientKey(com.bt.nextgen.service.integration.userinformation.ClientKey personId) {

            }

            @Override
            public JobKey getJob() {
                return null;
            }

            @Override
            public String getProfileId() {
                return null;
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public List<TaxResidenceCountry> getTaxResidenceCountries() {
                return null;
            }

            @Override
            public String getBrandSiloId() {
                return null;
            }

            @Override
            public String getCorporateName() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    @Test
    public void testFind() {
        com.avaloq.abs.screen_rep.hira.btfg$ui_bp_bp.Rep report = JaxbUtil.unmarshall("/webservices/response/Account_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_bp_bp.Rep.class);

        WrapAccountDetailImpl wrapAccountDetailImpl = new WrapAccountDetailImpl();

        wrapAccountDetailImpl.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("77021"));
        wrapAccountDetailImpl.setTaxLiability(TaxLiability.NON_RESIDENT_LIABLE);
        wrapAccountDetailImpl.setAdminFeeRate(new BigDecimal(9.98));
        wrapAccountDetailImpl.setOpenDate(new DateTime(2014, 8, 26, 0, 0));
        wrapAccountDetailImpl.setClosureDate(new DateTime());
        wrapAccountDetailImpl.setAccountStatus(AccountStatus.ACTIVE);
        wrapAccountDetailImpl.setAccountStructureType(AccountStructureType.Joint);
        wrapAccountDetailImpl.setSignDate(new DateTime(2014, 8, 26, 0, 0));
        wrapAccountDetailImpl.setBsb("262786");
        wrapAccountDetailImpl.setBillerCode("220186");
        wrapAccountDetailImpl.setcGTLMethod(CGTLMethod.MAX_GAIN);
        wrapAccountDetailImpl.setAccountNumber("120009279");
        wrapAccountDetailImpl.setModificationSeq("5");
        wrapAccountDetailImpl.setProductKey(ProductKey.valueOf("1234"));
        wrapAccountDetailImpl.setAccountOwners(new ArrayList<com.bt.nextgen.service.integration.userinformation.ClientKey>());
        wrapAccountDetailImpl.setOwners(new ArrayList<Client>());
        wrapAccountDetailImpl.setStatementPref("5004");
        wrapAccountDetailImpl.setCmaStatementPref("5004");

        List<LinkedAccount> linkedAccounts = new ArrayList<>();
        LinkedAccountImpl linkedAccount = new LinkedAccountImpl();
        linkedAccount.setAccountNumber("123456789");
        linkedAccount.setLimit(new BigDecimal(3000));
        linkedAccount.setPrimary(true);
        linkedAccount.setBsb("62111");
        linkedAccount.setName("Linked Account Name 50002");
        linkedAccount.setNickName("Linked Account Nickname 50002");
        linkedAccounts.add(linkedAccount);

        wrapAccountDetailImpl.setLinkedAccounts(linkedAccounts);
        wrapAccountDetailImpl.setAdviserKey(BrokerKey.valueOf("66773"));
        wrapAccountDetailImpl.setAccntPersonId(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("66773"));
        wrapAccountDetailImpl.setAccntOeId(BrokerKey.valueOf("66773"));

        OnboardingDetails onboardingDetails = new OnboardingDetails() {
            @Override
            public OnboardingDetailsType getOnboardingDetailsType() {
                return OnboardingDetailsType.APPROVE_OFFLINE;
            }
        };

        wrapAccountDetailImpl.setOnboardingDetails(Arrays.asList(onboardingDetails));

        List<SubAccount> subAccounts = new ArrayList<>();
        SubAccountImpl subAccount = new SubAccountImpl();
        subAccount.setSubAccountId(SubAccountKey.valueOf("119332"));
        subAccount.setSubAccountType(ContainerType.EXTERNAL_ASSET);
        subAccount.setAccntSoftware("Manual Entry");
        subAccount.setExternalAssetsFeedState("manual");
        subAccounts.add(subAccount);
        wrapAccountDetailImpl.setSubAccounts(subAccounts);

        Set<InvestorRole> personRoles = new HashSet<>();
        personRoles.add(InvestorRole.Member);

        Map<com.bt.nextgen.service.integration.userinformation.ClientKey, PersonRelation> personRelationMap = new HashMap<com.bt.nextgen.service.integration.userinformation.ClientKey, PersonRelation>();
        Map<com.bt.nextgen.service.integration.userinformation.ClientKey, PersonRelation> personRelationClientMap = new HashMap<com.bt.nextgen.service.integration.userinformation.ClientKey, PersonRelation>();

        PersonRelationImpl personRelation = new PersonRelationImpl(
                com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("1234"), true, personRoles, true, false);

        personRelationMap.put(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("1234"), personRelation);

        PersonRelationImpl personRelationClient = new PersonRelationImpl(
                com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("CLIENT_KEY"), true, personRoles, true, false);

        personRelationClientMap.put(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("1234"), personRelation);
        wrapAccountDetailImpl.setAssociatedPersons(personRelationMap);


        ProductImpl product = new ProductImpl();

        product.setProductKey(ProductKey.valueOf("1234"));
        product.setProductName("Offer 1");

        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);


        broker.setParentKey(BrokerKey.valueOf("66773"));

        when(
                accountService.loadWrapAccountDetail((com.bt.nextgen.service.integration.account.AccountKey) anyObject(),
                        (ServiceErrors) anyObject())).thenReturn(wrapAccountDetailImpl);
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(product);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(brokerUser);
        when(
                brokerIntegrationService.getPersonDetailsOfBrokerUser(
                        (com.bt.nextgen.service.integration.userinformation.ClientKey) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(brokerUser);

        WrapAccountDetailDto dto = (WrapAccountDetailDto) wrapAccountDetailDtoService.find(new AccountKey(
                "29BC330B5F7F81DE8D266EFAAA2BA693D130D8CF991F4610"), serviceErrors);

        assertNotNull(dto);
        assertEquals(wrapAccountDetailImpl.getAccountStructureType().name(), dto.getAccountType());
        assertEquals(product.getProductName(), dto.getProduct().getProductName());
        assertEquals(brokerUser.getFirstName(), dto.getAdviser().getFirstName());
        assertEquals(brokerUser.getLastName(), dto.getAdviser().getLastName());
        assertEquals(brokerUser.getFirstName(), dto.getAccountant().getFirstName());
        assertEquals(brokerUser.getLastName(), dto.getAccountant().getLastName());
        assertEquals("Manual Entry", dto.getAccountant().getAccountingSoftware());
        assertEquals("manual", dto.getAccountant().getExternalAssetsFeedState());
        assertThat(dto.getStatementPref(), equalTo(wrapAccountDetailImpl.getStatementPref()));
        assertThat(dto.getCmaStatementPref(), equalTo(wrapAccountDetailImpl.getCmaStatementPref()));
        assertThat(dto.getOnboardingDetails().get(0).getOnboardingDetailsType(),is(OnboardingDetailsType.APPROVE_OFFLINE));

        // TODO: Julian to discuss with Stephen
        // assertEquals(wrapAccountDetailImpl.getTaxLiability().getName(),
        // dto.getTaxLiability());
    }

    @Test
    public void testUpdatePrimaryContactId() {
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        InvestorDto investorDto = new InvestorDto();

        AccountKey accountKey = new AccountKey("29BC330B5F7F81DE8D266EFAAA2BA693D130D8CF991F4610");
        ClientKey clientKey = new ClientKey("E60F3326F378EF783D4BE77399FDBBBC116689DA655FAFB9");

        investorDto.setKey(clientKey);

        wrapAccountDetailDto.setKey(accountKey);
        wrapAccountDetailDto.setPrimaryContact(investorDto);
        wrapAccountDetailDto.setModificationSeq("5");

        response = new UpdateAccountDetailResponseImpl();

        // Response if update successfully
        response.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(accountKey
                .getAccountId())));
        response.setModificationIdentifier(new BigDecimal("6"));
        response.setUpdatedFlag(true);

        when(accountService.updatePrimaryContact((UpdatePrimContactRequest) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(response);

        wrapAccountDetailDto = (WrapAccountDetailDto) wrapAccountDetailDtoService.update(wrapAccountDetailDto, serviceErrors);
        verifyUpdateBPDetailsResponse(response, wrapAccountDetailDto);

        // Response if update fails
        response.setUpdatedFlag(false);

        when(accountService.updatePrimaryContact((UpdatePrimContactRequest) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(response);

        wrapAccountDetailDto = (WrapAccountDetailDto) wrapAccountDetailDtoService.update(wrapAccountDetailDto, serviceErrors);
        verifyUpdateBPDetailsResponse(response, wrapAccountDetailDto);
    }

    private void verifyUpdateBPDetailsResponse(UpdateAccountDetailResponse response, WrapAccountDetailDto wrapAccountDetailDto) {
        assertNotNull(response);
        assertEquals(response.getModificationIdentifier().toString(), wrapAccountDetailDto.getModificationSeq());
    }


    @Test
    public void updateStatementPrefTest() {
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        InvestorDto investorDto = new InvestorDto();
        AccountKey accountKey = new AccountKey("29BC330B5F7F81DE8D266EFAAA2BA693D130D8CF991F4610");
        ClientKey clientKey = new ClientKey("E60F3326F378EF783D4BE77399FDBBBC116689DA655FAFB9");

        investorDto.setKey(clientKey);

        wrapAccountDetailDto.setKey(accountKey);
        wrapAccountDetailDto.setStatementPref("5004");
        wrapAccountDetailDto.setModificationSeq("5");

        UpdateAccountDetailResponse response = new UpdateAccountDetailResponseImpl();

        // Response if update successfully
        response.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(accountKey.getAccountId())));
        response.setModificationIdentifier(new BigDecimal("6"));
        response.setUpdatedFlag(true);

        when(accountService.updateStmtCorrespondence((UpdateStmtCorrespondencePrefRequest) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(response);

        wrapAccountDetailDto = wrapAccountDetailDtoService.update(wrapAccountDetailDto, serviceErrors);
        verifyUpdateBPDetailsResponse(response, wrapAccountDetailDto);

        // Response if update fails
        response.setUpdatedFlag(false);

        when(accountService.updateStmtCorrespondence((UpdateStmtCorrespondencePrefRequest) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(response);

        wrapAccountDetailDto = wrapAccountDetailDtoService.update(wrapAccountDetailDto, serviceErrors);
        verifyUpdateBPDetailsResponse(response, wrapAccountDetailDto);
    }



    @Test
    public void updateCmaStatementPrefTest() {
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        InvestorDto investorDto = new InvestorDto();
        AccountKey accountKey = new AccountKey("29BC330B5F7F81DE8D266EFAAA2BA693D130D8CF991F4610");
        ClientKey clientKey = new ClientKey("E60F3326F378EF783D4BE77399FDBBBC116689DA655FAFB9");

        investorDto.setKey(clientKey);

        wrapAccountDetailDto.setKey(accountKey);
        wrapAccountDetailDto.setCmaStatementPref("5003");
        wrapAccountDetailDto.setModificationSeq("5");

        UpdateAccountDetailResponse response = new UpdateAccountDetailResponseImpl();

        // Response if update successfully
        response.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(accountKey.getAccountId())));
        response.setModificationIdentifier(new BigDecimal("6"));
        response.setUpdatedFlag(true);

        when(accountService.updateStmtCorrespondence((UpdateStmtCorrespondencePrefRequest) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(response);

        wrapAccountDetailDto = wrapAccountDetailDtoService.update(wrapAccountDetailDto, serviceErrors);
        verifyUpdateBPDetailsResponse(response, wrapAccountDetailDto);

        // Response if update fails
        response.setUpdatedFlag(false);

        when(accountService.updateStmtCorrespondence((UpdateStmtCorrespondencePrefRequest) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(response);

        wrapAccountDetailDto = wrapAccountDetailDtoService.update(wrapAccountDetailDto, serviceErrors);
        verifyUpdateBPDetailsResponse(response, wrapAccountDetailDto);
    }



    private UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf(customerId));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));

        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }

    @Test
    public void testFind_Company(){

        com.avaloq.abs.screen_rep.hira.btfg$ui_bp_bp.Rep report = JaxbUtil.unmarshall("/webservices/response/Account_UT.xml",
                com.avaloq.abs.screen_rep.hira.btfg$ui_bp_bp.Rep.class);

        List<Client> ownerList = new ArrayList<>();

        CompanyImpl client = new CompanyImpl();
        com.bt.nextgen.service.integration.userinformation.ClientKey clientKey = com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("CLIENT_KEY");
        client.setClientKey(clientKey);
        client.setFullName("Full name");
        client.setFirstName("First name");
        client.setLastName("Last name");
        client.setLegalForm(InvestorType.COMPANY);
        client.setExemptionReason(ExemptionReason.NO_EXEMPTION);
        client.setRegistrationDate(new DateTime("2015-08-26T00:00:00.000+05:30"));
        AddressImpl address = new AddressImpl();
        address.setState("NSW");
        address.setCountry("Australia");
        address.setAddressKey(AddressKey.valueOf("NSW"));
        client.setAddresses(Arrays.<Address>asList(address));
        ownerList.add(client)   ;


        WrapAccountDetailImpl wrapAccountDetailImplSMSF = new WrapAccountDetailImpl();

        wrapAccountDetailImplSMSF.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("77021"));
        wrapAccountDetailImplSMSF.setTaxLiability(TaxLiability.NON_RESIDENT_LIABLE);
        wrapAccountDetailImplSMSF.setAdminFeeRate(new BigDecimal(9.98));
        wrapAccountDetailImplSMSF.setOpenDate(new DateTime(2014, 8, 26, 0, 0));
        wrapAccountDetailImplSMSF.setClosureDate(new DateTime());
        wrapAccountDetailImplSMSF.setAccountStatus(AccountStatus.ACTIVE);
        wrapAccountDetailImplSMSF.setAccountStructureType(AccountStructureType.Company);
        wrapAccountDetailImplSMSF.setSignDate(new DateTime(2014, 8, 26, 0, 0));
        wrapAccountDetailImplSMSF.setBsb("262786");
        wrapAccountDetailImplSMSF.setBillerCode("220186");
        wrapAccountDetailImplSMSF.setcGTLMethod(CGTLMethod.MAX_GAIN);
        wrapAccountDetailImplSMSF.setAccountNumber("120009279");
        wrapAccountDetailImplSMSF.setModificationSeq("5");
        wrapAccountDetailImplSMSF.setProductKey(ProductKey.valueOf("1234"));
        wrapAccountDetailImplSMSF.setAccountOwners(new ArrayList<com.bt.nextgen.service.integration.userinformation.ClientKey>());
        wrapAccountDetailImplSMSF.setOwners(ownerList);

        List<LinkedAccount> linkedAccounts = new ArrayList<>();
        LinkedAccountImpl linkedAccount = new LinkedAccountImpl();
        linkedAccount.setAccountNumber("123456789");
        linkedAccount.setLimit(new BigDecimal(3000));
        linkedAccount.setPrimary(true);
        linkedAccount.setBsb("62111");
        linkedAccount.setName("Linked Account Name 50002");
        linkedAccount.setNickName("Linked Account Nickname 50002");
        linkedAccounts.add(linkedAccount);

        wrapAccountDetailImplSMSF.setLinkedAccounts(linkedAccounts);
        wrapAccountDetailImplSMSF.setAdviserKey(BrokerKey.valueOf("CLIENT_KEY"));
        wrapAccountDetailImplSMSF.setAccntPersonId(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("CLIENT_KEY"));
        wrapAccountDetailImplSMSF.setAccntOeId(BrokerKey.valueOf("CLIENT_KEY"));

        List<SubAccount> subAccounts = new ArrayList<>();
        SubAccountImpl subAccount = new SubAccountImpl();
        subAccount.setSubAccountId(SubAccountKey.valueOf("119432"));
        subAccount.setSubAccountType(ContainerType.EXTERNAL_ASSET);
        subAccount.setAccntSoftware("Manual Entry");
        subAccount.setExternalAssetsFeedState("manual");
        subAccounts.add(subAccount);
        wrapAccountDetailImplSMSF.setSubAccounts(subAccounts);

        Set<InvestorRole> personRoles = new HashSet<>();
        personRoles.add(InvestorRole.Member);

        Map<com.bt.nextgen.service.integration.userinformation.ClientKey, PersonRelation> personRelationMap = new HashMap<com.bt.nextgen.service.integration.userinformation.ClientKey, PersonRelation>();
        Map<com.bt.nextgen.service.integration.userinformation.ClientKey, PersonRelation> personRelationClientMap = new HashMap<com.bt.nextgen.service.integration.userinformation.ClientKey, PersonRelation>();


        PersonRelationImpl personRelationClient = new PersonRelationImpl(
                com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("CLIENT_KEY"), true, personRoles, true, false);

        personRelationClientMap.put(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("1234"), personRelationClient);

        wrapAccountDetailImplSMSF.setAssociatedPersons(personRelationClientMap);

        ProductImpl product = new ProductImpl();

        product.setProductKey(ProductKey.valueOf("1234"));
        product.setProductName("Offer 1");

        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);


        broker.setParentKey(BrokerKey.valueOf("66773"));

        when(
                accountService.loadWrapAccountDetail((com.bt.nextgen.service.integration.account.AccountKey) anyObject(),
                        (ServiceErrors) anyObject())).thenReturn(wrapAccountDetailImplSMSF);
        when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class))).thenReturn(product);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(brokerUser);
        when(
                brokerIntegrationService.getPersonDetailsOfBrokerUser(
                        (com.bt.nextgen.service.integration.userinformation.ClientKey) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(brokerUser);

        WrapAccountDetailDto smsfDto = (WrapAccountDetailDto) wrapAccountDetailDtoService.find(new AccountKey(
                "29BC330B5F7F81DE8D266EFAAA2BA693D130D8CF991F4610"), serviceErrors);

        assertNotNull(smsfDto);
        assertEquals(wrapAccountDetailImplSMSF.getAccountStructureType().name(), smsfDto.getAccountType());

        assertEquals(((CompanyImpl) (wrapAccountDetailImplSMSF.getOwners().get(0))).getRegistrationDate(), smsfDto.getRegisteredSinceDate().toDate());

    }
}

