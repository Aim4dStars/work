package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.AccountPaymentPermission;
import com.bt.nextgen.api.account.v3.model.AccountantDto;
import com.bt.nextgen.api.account.v3.model.DirectOffer;
import com.bt.nextgen.api.account.v3.model.InitialInvestmentDto;
import com.bt.nextgen.api.account.v3.model.MigrationDetailsDto;
import com.bt.nextgen.api.account.v3.model.PensionDetailsDto;
import com.bt.nextgen.api.account.v3.model.WrapAccountDetailDto;
import com.bt.nextgen.api.account.v3.util.AccountProductsHelper;
import com.bt.nextgen.api.broker.model.BrokerDto;
import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.v2.model.InvestorDto;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.account.CGTLMethod;
import com.bt.nextgen.service.avaloq.account.LinkedAccountImpl;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.avaloq.account.PersonRelationImpl;
import com.bt.nextgen.service.avaloq.account.SubAccountImpl;
import com.bt.nextgen.service.avaloq.account.TaxLiability;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.account.UpdateAccountDetailResponseImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.domain.RegisteredEntityImpl;
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
import com.bt.nextgen.service.integration.account.direct.ProductSubscriptionImpl;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.InvestorRole;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.client.dto.account.PersonRelationClientImpl;
import com.btfin.panorama.service.client.dto.account.WrapAccountDetailClientImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.ProductSubscription;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType.STRING;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WrapAccountDetailDtoServiceImplTest {
    @InjectMocks
    private WrapAccountDetailDtoServiceImpl wrapAccountDetailDtoService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private AvaloqAccountIntegrationServiceFactory avaloqAccountIntegrationServiceFactory;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private TransactionIntegrationService transactionListService;

    @Mock
    private AccountProductsHelper accountProductsHelper;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    private ServiceErrors serviceErrors;
    private UserProfile activeProfile;
    private BrokerUser brokerUser;
    private List<InitialInvestmentDto> initialInvestmentAssetDtos;
    private final Collection<Code> typeCodeList = new ArrayList<>();

    @Before
    public void setup() {
        serviceErrors = new FailFastErrorsImpl();
        activeProfile = getProfile(JobRole.ADVISER);
        final List<Address> addresses = new ArrayList<>();
        Address address = Mockito.mock(Address.class);
        addresses.add(address);
        final List<Phone> phones = new ArrayList<>();
        Phone phone = Mockito.mock(Phone.class);
        when(phone.getType()).thenReturn(AddressMedium.MOBILE_PHONE_PRIMARY);
        when(phone.getPhoneKey()).thenReturn(AddressKey.valueOf("1111111111"));
        phones.add(phone);
        final List<Email> emails = new ArrayList<>();
        Email email = Mockito.mock(Email.class);
        when(email.getType()).thenReturn(AddressMedium.EMAIL_PRIMARY);
        when(email.getEmailKey()).thenReturn(AddressKey.valueOf("email@email.com"));
        emails.add(email);

        final ManagedFundAssetImpl managedFundAsset = new ManagedFundAssetImpl();
        managedFundAsset.setAssetId("asset1");
        managedFundAsset.setAssetCode("code1");
        managedFundAsset.setAssetName("asset1");

        initialInvestmentAssetDtos = Collections.singletonList(new InitialInvestmentDto(managedFundAsset, TEN));

        Code code1 = new CodeImpl("51057", "MAN", "Manual Entry", "man");
        Code code2 = new CodeImpl("51061", "BGL360", "BGL SF360", "bgl360");
        typeCodeList.add(code1);
        typeCodeList.add(code2);

        brokerUser = mock(BrokerUser.class);
        when(brokerUser.isRegisteredOnline()).thenReturn(false);
        when(brokerUser.getFirstName()).thenReturn("person-120_505");
        when(brokerUser.getMiddleName()).thenReturn("person-120_505");
        when(brokerUser.getLastName()).thenReturn("person-120_505");
        when(brokerUser.getCorporateName()).thenReturn("corporateName");
        when(brokerUser.getEmails()).thenReturn(emails);
        when(brokerUser.getPhones()).thenReturn(phones);
        when(brokerUser.getAddresses()).thenReturn(addresses);
        when(brokerUser.getAge()).thenReturn(0);

        when(avaloqAccountIntegrationServiceFactory.getInstance(any(String.class))).thenReturn(accountService);
        when(accountProductsHelper.getAccountFeatureKey(any(WrapAccount.class), any(ServiceErrors.class))).thenReturn("advised.individual");

        CodeImpl code3 = new CodeImpl("4", "UNVERIFIED", "unverify", "unfy");
        code3.addField("can_gen_code", "+");
        code3.addField("can_vfy_code", "+");
        code3.addField("can_direct_debit", "+");
        code3.addField("is_grace_period", "+");

        when(staticIntegrationService.loadCode(eq(CodeCategory.LINKED_ACCOUNT_STATUS), any(String.class), any(ServiceErrors.class))).thenReturn(code3);
    }

    @Test
    public void searchNonPensionAccount() {
        search(initAccountDetail(new WrapAccountDetailImpl()));
    }

    //OffThread Test for WrapAccountDetailClientImpl
    @Test
    public void searchWrapAccountDetailClientImpl() {
        search(initAccountDetail(new WrapAccountDetailClientImpl()));
    }

    @Test
    public void searchPensionAccount1() {
        final PensionAccountDetailImpl pensionAccountDetail = new PensionAccountDetailImpl();
        final WrapAccountDetailDto dto;
        final PensionDetailsDto pensionDetailsDto;

        initAccountDetail(pensionAccountDetail);

        pensionAccountDetail.setPaymentPaidYtd(TEN);
        pensionAccountDetail.setProjectedFuturePayment(BigDecimal.valueOf(300));
        pensionAccountDetail.setTaxYtd(BigDecimal.valueOf(100));
        pensionAccountDetail.setLifeExpectancyCentreLinkRelevantNumber(BigDecimal.valueOf(23.35));
        pensionAccountDetail.setEstimatedRolloverCount(1);
        pensionAccountDetail.setActualRolloverCount(3);
        pensionAccountDetail.setNextPaymentDate(new DateTime(2014, 9, 9, 0, 0));

        dto = search(pensionAccountDetail);
        pensionDetailsDto = dto.getPensionDetails();

        assertThat("pensionDetailsDto", pensionDetailsDto, notNullValue());
        assertThat("pensionDetailsDto - pensionType", pensionDetailsDto.getPensionType(), nullValue());
        assertThat("pensionDetailsDto - indexationType", pensionDetailsDto.getIndexationType(), nullValue());
        assertThat("pensionDetailsDto - indexationAmount", pensionDetailsDto.getIndexationAmount(), nullValue());
        assertThat("pensionDetailsDto - pensionPaidYtd", pensionDetailsDto.getPensionPaidYtd(),
                equalTo(pensionAccountDetail.getPaymentPaidYtd()));
        assertThat("pensionDetailsDto - projectedPensionPayment", pensionDetailsDto.getProjectedPensionPayment(),
                equalTo(pensionAccountDetail.getTotalProjectedPayment()));
        assertThat("pensionDetailsDto - taxYtd", pensionDetailsDto.getTaxYtd(), equalTo(pensionAccountDetail.getTaxYtd()));
        assertThat("pensionDetailsDto - lifeExpectancyCentrelinkSchedule",
                pensionDetailsDto.getLifeExpectancyCentrelinkSchedule(),
                equalTo(pensionAccountDetail.getLifeExpectancyCentreLinkRelevantNumber()));
        assertThat("pensionDetailsDto - estimatedRolloverCount", pensionDetailsDto.getEstimatedRolloverCount(),
                equalTo(pensionAccountDetail.getEstimatedRolloverCount()));
        assertThat("pensionDetailsDto - actualRolloverCount", pensionDetailsDto.getActualRolloverCount(),
                equalTo(pensionAccountDetail.getActualRolloverCount()));
        assertThat("pensionDetailsDto - firstPaymentDate", pensionDetailsDto.getFirstPaymentDate(), nullValue());
        assertThat("pensionDetailsDto - daysToFirstPayment", pensionDetailsDto.getDaysToFirstPayment(), nullValue());
        assertThat("pensionDetailsDto - nextPaymentDate", pensionDetailsDto.getNextPaymentDate().toDate(),
                equalTo(pensionAccountDetail.getNextPaymentDate().toDate()));
        assertThat("pensionDetailsDto - paymentFrequency", pensionDetailsDto.getPaymentFrequency(), nullValue());
    }

    @Test
    public void searchPensionAccount2() {
        final PensionAccountDetailImpl pensionAccountDetail = new PensionAccountDetailImpl();
        final WrapAccountDetailDto dto;
        final PensionDetailsDto pensionDetailsDto;

        initAccountDetail(pensionAccountDetail);

        pensionAccountDetail.setPensionType(PensionType.STANDARD);
        pensionAccountDetail.setIndexationType(IndexationType.CPI);
        pensionAccountDetail.setIndexationAmount(new BigDecimal("123.45"));
        pensionAccountDetail.setPaymentPaidYtd(TEN);
        pensionAccountDetail.setProjectedFuturePayment(BigDecimal.valueOf(300));
        pensionAccountDetail.setTaxYtd(BigDecimal.valueOf(100));
        pensionAccountDetail.setLifeExpectancyCentreLinkRelevantNumber(BigDecimal.valueOf(23.35));
        pensionAccountDetail.setEstimatedRolloverCount(1);
        pensionAccountDetail.setActualRolloverCount(3);
        pensionAccountDetail.setPaymentFirstDate(DateTime.now().minusDays(22));
        pensionAccountDetail.setNextPaymentDate(new DateTime(2014, 9, 9, 0, 0));
        pensionAccountDetail.setPaymentFrequency(RecurringFrequency.Fortnightly);
        pensionAccountDetail.setUsableCash(new BigDecimal("123.45"));
        pensionAccountDetail.setLumpSumUsableCash(new BigDecimal("123.45"));

        dto = search(pensionAccountDetail);
        pensionDetailsDto = dto.getPensionDetails();

        assertThat("pensionDetailsDto", pensionDetailsDto, notNullValue());
        assertThat("pensionDetailsDto - pensionType", pensionDetailsDto.getPensionType(),
                equalTo(pensionAccountDetail.getPensionType().getValue()));
        assertThat("pensionDetailsDto - indexationType", pensionDetailsDto.getIndexationType(),
                equalTo(pensionAccountDetail.getIndexationType().getLabel()));
        assertThat("pensionDetailsDto - indexationAmount", pensionDetailsDto.getIndexationAmount(),
                equalTo(pensionAccountDetail.getIndexationAmount()));
        assertThat("pensionDetailsDto - pensionPaidYtd", pensionDetailsDto.getPensionPaidYtd(),
                equalTo(pensionAccountDetail.getPaymentPaidYtd()));
        assertThat("pensionDetailsDto - projectedPensionPayment", pensionDetailsDto.getProjectedPensionPayment(),
                equalTo(pensionAccountDetail.getTotalProjectedPayment()));
        assertThat("pensionDetailsDto - taxYtd", pensionDetailsDto.getTaxYtd(), equalTo(pensionAccountDetail.getTaxYtd()));
        assertThat("pensionDetailsDto - lifeExpectancyCentrelinkSchedule",
                pensionDetailsDto.getLifeExpectancyCentrelinkSchedule(),
                equalTo(pensionAccountDetail.getLifeExpectancyCentreLinkRelevantNumber()));
        assertThat("pensionDetailsDto - estimatedRolloverCount", pensionDetailsDto.getEstimatedRolloverCount(),
                equalTo(pensionAccountDetail.getEstimatedRolloverCount()));
        assertThat("pensionDetailsDto - actualRolloverCount", pensionDetailsDto.getActualRolloverCount(),
                equalTo(pensionAccountDetail.getActualRolloverCount()));
        assertThat("pensionDetailsDto - firstPaymentDate", pensionDetailsDto.getFirstPaymentDate(),
                equalTo(pensionAccountDetail.getFirstPaymentDate()));
        assertThat("pensionDetailsDto - daysToFirstPayment", pensionDetailsDto.getDaysToFirstPayment(), equalTo(-22));
        assertThat("pensionDetailsDto - nextPaymentDate", pensionDetailsDto.getNextPaymentDate().toDate(),
                equalTo(pensionAccountDetail.getNextPaymentDate().toDate()));
        assertThat("pensionDetailsDto - paymentFrequency", pensionDetailsDto.getPaymentFrequency(),
                equalTo(pensionAccountDetail.getPaymentFrequency().getDescription()));
        assertThat("pensionDetailsDto - usableCash", pensionDetailsDto.getUsableCash(), equalTo(pensionAccountDetail.getUsableCash()));
        assertThat("pensionDetailsDto - lumpSumUsableCash", pensionDetailsDto.getLumpSumUsableCash(), equalTo(
                pensionAccountDetail.getLumpSumUsableCash()));
    }

    @Test
    public void updatePrimaryContactId() {
        WrapAccountDetailDto wrapAccountDetailDto = new WrapAccountDetailDto();
        InvestorDto investorDto = new InvestorDto();

        AccountKey accountKey = new AccountKey("29BC330B5F7F81DE8D266EFAAA2BA693D130D8CF991F4610");
        ClientKey clientKey = new ClientKey("E60F3326F378EF783D4BE77399FDBBBC116689DA655FAFB9");

        investorDto.setKey(clientKey);

        wrapAccountDetailDto.setKey(accountKey);
        wrapAccountDetailDto.setPrimaryContact(investorDto);
        wrapAccountDetailDto.setModificationSeq("5");

        UpdateAccountDetailResponse response = new UpdateAccountDetailResponseImpl();

        // Response if update successfully
        response.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(accountKey.getAccountId())));
        response.setModificationIdentifier(new BigDecimal("6"));
        response.setUpdatedFlag(true);

        when(accountService.updatePrimaryContact((UpdatePrimContactRequest) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(response);

        wrapAccountDetailDto = wrapAccountDetailDtoService.update(wrapAccountDetailDto, serviceErrors);
        verifyUpdateBPDetailsResponse(response, wrapAccountDetailDto);

        // Response if update fails
        response.setUpdatedFlag(false);

        when(accountService.updatePrimaryContact((UpdatePrimContactRequest) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(response);

        wrapAccountDetailDto = wrapAccountDetailDtoService.update(wrapAccountDetailDto, serviceErrors);
        verifyUpdateBPDetailsResponse(response, wrapAccountDetailDto);
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


    private WrapAccountDetailDto search(WrapAccountDetailImpl wrapAccountDetailImpl) {
        final ProductDto product = makeProduct();
        final BrokerImpl broker = makeBroker();
        final List<ApiSearchCriteria> criteria = MakeSearchCriteriaList();
        final WrapAccountDetailDto dto;

        when(staticIntegrationService.loadCodes(eq(CodeCategory.EXT_HOLDING_SRC), any(ServiceErrors.class)))
                .thenReturn(typeCodeList);
        when(accountService.loadWrapAccountDetail((com.bt.nextgen.service.integration.account.AccountKey) anyObject(),
                (ServiceErrors) anyObject())).thenReturn(wrapAccountDetailImpl);
        when(accountProductsHelper.getProductDto(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(product);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(brokerUser);
        when(brokerIntegrationService.getPersonDetailsOfBrokerUser(
                (com.bt.nextgen.service.integration.userinformation.ClientKey) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(brokerUser);
        when(transactionListService.loadRecentCashTransactions((WrapAccountIdentifier) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(new ArrayList<TransactionHistory>());
        when(accountProductsHelper.getSubscriptionType(any(WrapAccountDetail.class), any(ServiceErrors.class)))
                .thenReturn("active");
        when(accountProductsHelper.getInitialInvestments(any(WrapAccountDetail.class), any(ServiceErrors.class)))
                .thenReturn(initialInvestmentAssetDtos);

        dto = wrapAccountDetailDtoService.search(criteria, serviceErrors);

        assertThat(dto, notNullValue());
        assertThat(dto.getAccountType(), equalTo(wrapAccountDetailImpl.getAccountStructureType().name()));
        assertThat(dto.getProduct().getProductName(), equalTo(product.getProductName()));
        assertThat(dto.getAdviser().getFirstName(), equalTo(brokerUser.getFirstName()));
        assertThat(dto.getAdviser().getLastName(), equalTo(brokerUser.getLastName()));
        assertThat(dto.getAdviser().getCorporateName(), equalTo(brokerUser.getCorporateName()));
        assertThat(dto.getAdviser().getDisplayName(), equalTo(brokerUser.getFirstName() + " " + brokerUser.getLastName()));
        assertThat(dto.isHasMinCash(), equalTo(wrapAccountDetailImpl.isHasMinCash()));
        assertThat(dto.getMinCashAmount(), equalTo(wrapAccountDetailImpl.getMinCashAmount()));
        assertThat(dto.getSubscriptionType(), equalTo(DirectOffer.ACTIVE.getSubscriptionType()));
        assertThat(dto.getInitialInvestments(), notNullValue());
        assertThat(dto.getHasIhin(), equalTo(wrapAccountDetailImpl.getHasIhin()));
        assertThat(dto.getIhin(), equalTo(wrapAccountDetailImpl.getIhin()));
        assertThat(dto.getStatementPref(), equalTo(wrapAccountDetailImpl.getStatementPref()));
        assertThat(dto.getCmaStatementPref(), equalTo(wrapAccountDetailImpl.getCmaStatementPref()));
        assertThat(dto.getTaxAndPreservationDetails().getTaxableAmount().doubleValue(),
                equalTo(wrapAccountDetailImpl.getTaxableComponent().doubleValue()));

        assertThat(dto.getAccountant().getFirstName(), equalTo(brokerUser.getFirstName()));
        assertThat(dto.getAccountant().getLastName(), equalTo(brokerUser.getLastName()));
        assertThat(dto.getAccountant().getCorporateName(), equalTo(brokerUser.getCorporateName()));
        assertThat(dto.getAccountant().getDisplayName(), equalTo(brokerUser.getFirstName() + " " + brokerUser.getLastName()));
        assertThat(dto.getAccountant().getAccountingSoftware(), equalTo("man"));
        assertThat(dto.getAccountant().getAccountingSoftwareDisplayName(), equalTo("Manual Entry"));
        assertThat(dto.getAccountant().getExternalAssetsFeedState(), equalTo("manual"));

        assertThat(dto.getPersonalBillerCode(), equalTo("20567"));
        assertThat(dto.getSpouseBillerCode(), equalTo("20345"));

        assertThat(dto.getOnboardingDetails().get(0).getOnboardingDetailsType(), is(OnboardingDetailsType.APPROVE_OFFLINE));

        assertThat(dto.getInitialInvestments().size(), is(1));
        assertThat(dto.getInitialInvestments().get(0).getAsset().getAssetId(), is(initialInvestmentAssetDtos.get(0).getAsset().getAssetId()));
        assertThat(dto.getInitialInvestments().get(0).getAmount(), is(initialInvestmentAssetDtos.get(0).getAmount()));
        assertThat(dto.getTypeId(), is("advised.individual"));

        assertThat(dto.getSettings().size(), is(1));
        assertThat(dto.getSettings().get(0).getName(), is("Clark Kent"));
        assertThat(dto.getSettings().get(0).getPermissions(),
                is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_ALL.getPermissionDesc()));

        // should this be the company registered date?
        assertThat(dto.getRegisteredSinceDate(), is(DateTime.parse("2014-08-26")));

        return dto;
    }

    private WrapAccountDetailDto search(WrapAccountDetailClientImpl wrapAccountDetailImpl) {
        final ProductDto product = makeProduct();
        final BrokerImpl broker = makeBroker();
        final List<ApiSearchCriteria> criteria = MakeSearchCriteriaList();
        final WrapAccountDetailDto dto;

        when(staticIntegrationService.loadCodes(eq(CodeCategory.EXT_HOLDING_SRC), any(ServiceErrors.class)))
                .thenReturn(typeCodeList);
        when(accountService.loadWrapAccountDetail((com.bt.nextgen.service.integration.account.AccountKey) anyObject(),
                (ServiceErrors) anyObject())).thenReturn(wrapAccountDetailImpl);
        when(accountProductsHelper.getProductDto(any(WrapAccountDetail.class), any(ServiceErrors.class))).thenReturn(product);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(brokerUser);
        when(brokerIntegrationService.getPersonDetailsOfBrokerUser(
                (com.bt.nextgen.service.integration.userinformation.ClientKey) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(brokerUser);
        when(transactionListService.loadRecentCashTransactions((WrapAccountIdentifier) anyObject(), (ServiceErrors) anyObject()))
                .thenReturn(new ArrayList<TransactionHistory>());
        when(accountProductsHelper.getSubscriptionType(any(WrapAccountDetail.class), any(ServiceErrors.class)))
                .thenReturn("active");
        when(accountProductsHelper.getInitialInvestments(any(WrapAccountDetail.class), any(ServiceErrors.class)))
                .thenReturn(initialInvestmentAssetDtos);
        setMigrationDetails(wrapAccountDetailImpl, wrapAccountDetailDtoService);

        dto = wrapAccountDetailDtoService.search(criteria, serviceErrors);

        assertThat(dto, notNullValue());
        assertThat(dto.getAccountType(), equalTo(wrapAccountDetailImpl.getAccountStructureType().name()));
        assertThat(dto.getProduct().getProductName(), equalTo(product.getProductName()));
        assertThat(dto.getAdviser().getFirstName(), equalTo(brokerUser.getFirstName()));
        assertThat(dto.getAdviser().getLastName(), equalTo(brokerUser.getLastName()));
        assertThat(dto.getAdviser().getCorporateName(), equalTo(brokerUser.getCorporateName()));
        assertThat(dto.getAdviser().getDisplayName(), equalTo(brokerUser.getFirstName() + " " + brokerUser.getLastName()));
        assertThat(dto.isHasMinCash(), equalTo(wrapAccountDetailImpl.isHasMinCash()));
        assertThat(dto.getMinCashAmount(), equalTo(wrapAccountDetailImpl.getMinCashAmount()));
        assertThat(dto.getSubscriptionType(), equalTo(DirectOffer.ACTIVE.getSubscriptionType()));
        assertThat(dto.getInitialInvestments(), notNullValue());
        assertThat(dto.getHasIhin(), equalTo(wrapAccountDetailImpl.getHasIhin()));
        assertThat(dto.getIhin(), equalTo(wrapAccountDetailImpl.getIhin()));

        assertThat(dto.getAccountant().getFirstName(), equalTo(brokerUser.getFirstName()));
        assertThat(dto.getAccountant().getLastName(), equalTo(brokerUser.getLastName()));
        assertThat(dto.getAccountant().getCorporateName(), equalTo(brokerUser.getCorporateName()));
        assertThat(dto.getAccountant().getDisplayName(), equalTo(brokerUser.getFirstName() + " " + brokerUser.getLastName()));
        assertThat(dto.getAccountant().getAccountingSoftware(), equalTo("man"));
        assertThat(dto.getAccountant().getAccountingSoftwareDisplayName(), equalTo("Manual Entry"));
        assertThat(dto.getAccountant().getExternalAssetsFeedState(), equalTo("manual"));

        assertThat(dto.getPersonalBillerCode(), equalTo("20567"));
        assertThat(dto.getSpouseBillerCode(), equalTo("20345"));

        assertThat(dto.getOnboardingDetails().get(0).getOnboardingDetailsType(), is(OnboardingDetailsType.APPROVE_OFFLINE));
        assertThat(dto.getInitialInvestments().size(), is(1));
        assertThat(dto.getInitialInvestments().get(0).getAsset().getAssetId(), is(initialInvestmentAssetDtos.get(0).getAsset().getAssetId()));
        assertThat(dto.getInitialInvestments().get(0).getAmount(), is(initialInvestmentAssetDtos.get(0).getAmount()));
        return dto;
    }

    private void setMigrationDetails(WrapAccountDetailClientImpl wrapAccountDetailImpl, WrapAccountDetailDtoServiceImpl wrapAccountDetailDtoService) {
        final MigrationDetailsDto migrationDetailsDto = new MigrationDetailsDto();
        final List<ApiSearchCriteria> criteria = MakeSearchCriteriaList();
        final DateTime migratedDate = new DateTime("2016-06-08");

        // sets Migration details as null if the account is not migrated
        WrapAccountDetailDto dto = wrapAccountDetailDtoService.search(criteria, serviceErrors);
        assertNull(dto.getMigrationDetails());

        wrapAccountDetailImpl.setMigrationDate(new DateTime("2016-06-08"));
        wrapAccountDetailImpl.setMigrationKey("M00721465");


        //sets Migration details in the dto if the account is migrated
        dto = wrapAccountDetailDtoService.search(criteria, serviceErrors);
        assertNotNull(dto.getMigrationDetails());
        assertEquals(migratedDate, dto.getMigrationDetails().getMigrationDate());
        assertEquals("M00721465", dto.getMigrationDetails().getAccountId());
        assertEquals(null, dto.getMigrationDetails().getSourceId());

        wrapAccountDetailImpl.setMigrationSourceId(SystemType.WRAP);
        dto = wrapAccountDetailDtoService.search(criteria, serviceErrors);
        assertEquals("Wrap", dto.getMigrationDetails().getSourceId());
    }

    private List<ApiSearchCriteria> MakeSearchCriteriaList() {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();

        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, EQUALS,
                "29BC330B5F7F81DE8D266EFAAA2BA693D130D8CF991F4610", STRING));

        return criteria;
    }

    private WrapAccountDetailImpl initAccountDetail(WrapAccountDetailImpl wrapAccountDetailImpl) {
        final List<ProductSubscription> subscriptions = new ArrayList<>();

        subscriptions.add(getProductSubscription("prod1"));
        subscriptions.add(getProductSubscription("prod2"));

        wrapAccountDetailImpl.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("77021"));
        wrapAccountDetailImpl.setTaxLiability(TaxLiability.NON_RESIDENT_LIABLE);
        wrapAccountDetailImpl.setAdminFeeRate(new BigDecimal(9.98));
        wrapAccountDetailImpl.setOpenDate(new DateTime(2014, 8, 26, 0, 0));
        wrapAccountDetailImpl.setClosureDate(new DateTime());
        wrapAccountDetailImpl.setAccountStatus(AccountStatus.ACTIVE);
        wrapAccountDetailImpl.setAccountStructureType(AccountStructureType.Trust);
        wrapAccountDetailImpl.setSignDate(new DateTime(2014, 8, 26, 0, 0));
        wrapAccountDetailImpl.setBsb("262786");
        wrapAccountDetailImpl.setBillerCode("220186");
        wrapAccountDetailImpl.setcGTLMethod(CGTLMethod.MAX_GAIN);
        wrapAccountDetailImpl.setAccountNumber("120009279");
        wrapAccountDetailImpl.setModificationSeq("5");
        wrapAccountDetailImpl.setProductKey(ProductKey.valueOf("1234"));
        wrapAccountDetailImpl.setAccountOwners(new ArrayList<com.bt.nextgen.service.integration.userinformation.ClientKey>());
        wrapAccountDetailImpl.setTaxableComponent(BigDecimal.valueOf(55));
        wrapAccountDetailImpl.setStatementPref("5004");
        wrapAccountDetailImpl.setCmaStatementPref("5004");

        List<Client> owners = new ArrayList<>();
        InvestorDetail owner = Mockito.mock(InvestorDetail.class);
        when(owner.getClientKey()).thenReturn(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("5678"));
        when(owner.getInvestorType()).thenReturn(InvestorType.TRUST);
        Collection<ClientDetail> relatedPersons = new ArrayList<>();
        InvestorDetail relatedPerson = Mockito.mock(InvestorDetail.class);
        when(relatedPerson.getClientKey())
                .thenReturn(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("1234"));
        when(relatedPerson.getFullName()).thenReturn("Clark Kent");
        relatedPersons.add(relatedPerson);
        when(owner.getRelatedPersons()).thenReturn(relatedPersons);

        RegisteredEntityImpl owner2 = Mockito.mock(RegisteredEntityImpl.class);
        when(owner2.getClientKey()).thenReturn(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("15975"));
        when(owner2.getInvestorType()).thenReturn(InvestorType.COMPANY);
        when(owner2.getRegistrationDate()).thenReturn(new Date(2017, 1, 1));
        Collection<ClientDetail> relatedPersons2 = new ArrayList<>();
        InvestorDetail relatedPerson2 = Mockito.mock(InvestorDetail.class);
        when(relatedPerson2.getClientKey())
                .thenReturn(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("1234"));
        when(relatedPerson2.getFullName()).thenReturn("Daily Planet");
        relatedPersons2.add(relatedPerson);
        when(owner2.getRelatedPersons()).thenReturn(relatedPersons);

        owners.add(owner);
        owners.add(owner2);

        wrapAccountDetailImpl.setOwners(owners);
        wrapAccountDetailImpl.setHasMinCash(true);
        wrapAccountDetailImpl.setMinCashAmount(BigDecimal.valueOf(2000d));
        wrapAccountDetailImpl.setProductSubscription(subscriptions);
        wrapAccountDetailImpl.setSpouseBillerCode("20345");
        wrapAccountDetailImpl.setPersonalBillerCode("20567");
        wrapAccountDetailImpl.setHasIhin(true);
        wrapAccountDetailImpl.setIhin("2332762219");

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
        wrapAccountDetailImpl.setAccntPersonId(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("66773"));
        wrapAccountDetailImpl.setAdviserKey(BrokerKey.valueOf("66773"));

        OnboardingDetails onboardingDetails = new OnboardingDetails() {
            @Override
            public OnboardingDetailsType getOnboardingDetailsType() {
                return OnboardingDetailsType.APPROVE_OFFLINE;
            }
        };

        wrapAccountDetailImpl.setOnboardingDetails(Collections.singletonList(onboardingDetails));

        List<SubAccount> subAccounts = new ArrayList<>();
        SubAccountImpl subAccount = new SubAccountImpl();
        subAccount.setSubAccountId(SubAccountKey.valueOf("119332"));
        subAccount.setSubAccountType(ContainerType.EXTERNAL_ASSET);
        subAccount.setAccntSoftware("man");
        subAccount.setExternalAssetsFeedState("manual");
        subAccounts.add(subAccount);
        wrapAccountDetailImpl.setSubAccounts(subAccounts);

        Set<InvestorRole> personRoles = new HashSet<>();
        personRoles.add(InvestorRole.Member);

        Map<com.bt.nextgen.service.integration.userinformation.ClientKey, PersonRelation> personRelationMap = new HashMap<>();

        PersonRelationImpl personRelation = new PersonRelationImpl(
                com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("1234"), true, personRoles, true, false);
        Set<TransactionPermission> transactionPermissions = new HashSet<>();
        transactionPermissions.add(TransactionPermission.Payments_Deposits);
        personRelation.setPermissions(transactionPermissions);

        personRelationMap.put(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("1234"), personRelation);
        wrapAccountDetailImpl.setAssociatedPersons(personRelationMap);

        return wrapAccountDetailImpl;
    }

    private WrapAccountDetailClientImpl initAccountDetail(WrapAccountDetailClientImpl wrapAccountDetailImpl) {
        final List<ProductSubscription> subscriptions = new ArrayList<>();

        subscriptions.add(getProductSubscription("prod1"));
        subscriptions.add(getProductSubscription("prod2"));

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
        wrapAccountDetailImpl.setHasMinCash(true);
        wrapAccountDetailImpl.setMinCashAmount(BigDecimal.valueOf(2000d));
        wrapAccountDetailImpl.setProductSubscription(subscriptions);
        wrapAccountDetailImpl.setSpouseBillerCode("20345");
        wrapAccountDetailImpl.setPersonalBillerCode("20567");
        wrapAccountDetailImpl.setHasIhin(true);
        wrapAccountDetailImpl.setIhin("2332762219");

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
        wrapAccountDetailImpl.setAccntPersonId(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("66773"));
        wrapAccountDetailImpl.setAdviserKey(BrokerKey.valueOf("66773"));

        OnboardingDetails onboardingDetails = new OnboardingDetails() {
            @Override
            public OnboardingDetailsType getOnboardingDetailsType() {
                return OnboardingDetailsType.APPROVE_OFFLINE;
            }
        };

        wrapAccountDetailImpl.setOnboardingDetails(Collections.singletonList(onboardingDetails));

        List<SubAccount> subAccounts = new ArrayList<>();
        SubAccountImpl subAccount = new SubAccountImpl();
        subAccount.setSubAccountId(SubAccountKey.valueOf("119332"));
        subAccount.setSubAccountType(ContainerType.EXTERNAL_ASSET);
        subAccount.setAccntSoftware("man");
        subAccount.setExternalAssetsFeedState("manual");
        subAccounts.add(subAccount);
        wrapAccountDetailImpl.setSubAccounts(subAccounts);

        Set<InvestorRole> personRoles = new HashSet<>();
        personRoles.add(InvestorRole.Member);

        Map<String, PersonRelationClientImpl> personRelationMap = new HashMap<>();

        PersonRelationClientImpl personRelation = new PersonRelationClientImpl(
                com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("1234"), true, personRoles, true, false);

        personRelationMap.put("1234", personRelation);
        wrapAccountDetailImpl.setAssociatedPersons(personRelationMap);

        return wrapAccountDetailImpl;
    }


    private BrokerImpl makeBroker() {
        final BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);

        broker.setParentKey(BrokerKey.valueOf("66773"));

        return broker;
    }

    private ProductDto makeProduct() {
        final ProductDto product = mock(ProductDto.class);
        when(product.getKey()).thenReturn(new com.bt.nextgen.api.product.v1.model.ProductKey("1234"));
        when(product.getProductName()).thenReturn("Offer 1");
        return product;
    }

    private void verifyUpdateBPDetailsResponse(UpdateAccountDetailResponse response, WrapAccountDetailDto wrapAccountDetailDto) {
        assertThat(response, notNullValue());
        assertThat(response.getModificationIdentifier().toString(), equalTo(wrapAccountDetailDto.getModificationSeq()));
    }

    private UserProfile getProfile(final JobRole role) {
        final UserInformationImpl user = new UserInformationImpl();

        user.setClientKey(com.bt.nextgen.service.integration.userinformation.ClientKey.valueOf("client1"));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf("job id 1"));

        return new UserProfileAdapterImpl(user, job);
    }

    private ProductSubscription getProductSubscription(String prodId) {
        ProductSubscriptionImpl productSubscription = new ProductSubscriptionImpl();
        productSubscription.setSubscribedProductId(prodId);
        return productSubscription;
    }

    @Test
    public void testGetBrokerDetails_whenAdviserLogin_thenCorporateNameNotVisible() {
        when(userProfileService.isInvestor()).thenReturn(Boolean.FALSE);
        when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) anyObject(), (ServiceErrors) anyObject())).thenReturn(
                brokerUser);

        BrokerImpl broker = makeBroker();
        BrokerDto brokerDto = wrapAccountDetailDtoService.getBrokerDto(broker, serviceErrors);
        Assert.assertEquals("corporateName", brokerDto.getCorporateName());
        Assert.assertEquals("person-120_505 person-120_505", brokerDto.getDisplayName());

        AccountantDto accountantDto = wrapAccountDetailDtoService.getAccountantDetails(Mockito.mock(WrapAccountDetail.class),
                brokerUser, serviceErrors);
        Assert.assertEquals("corporateName", accountantDto.getCorporateName());
        Assert.assertEquals("person-120_505 person-120_505", accountantDto.getDisplayName());
    }

    @Test
    public void testGetBrokerDetails_whenInvestorLogin_thenCorporateNameVisible() {
        when(userProfileService.isInvestor()).thenReturn(Boolean.TRUE);
        when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) anyObject(), (ServiceErrors) anyObject())).thenReturn(
                brokerUser);

        BrokerImpl broker = makeBroker();
        BrokerDto brokerDto = wrapAccountDetailDtoService.getBrokerDto(broker, serviceErrors);
        Assert.assertEquals("corporateName", brokerDto.getCorporateName());
        Assert.assertEquals("corporateName", brokerDto.getDisplayName());

        AccountantDto accountantDto = wrapAccountDetailDtoService.getAccountantDetails(Mockito.mock(WrapAccountDetail.class),
                brokerUser, serviceErrors);
        Assert.assertEquals("corporateName", accountantDto.getCorporateName());
        Assert.assertEquals("corporateName", accountantDto.getDisplayName());
    }
}
