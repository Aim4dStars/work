package com.bt.nextgen.api.regularinvestment.v2.service;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.order.model.FundsAllocationDto;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.model.OrderItemSummaryDto;
import com.bt.nextgen.api.order.service.helper.OrderItemHelper;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapperImpl;
import com.bt.nextgen.api.regularinvestment.v2.model.InvestmentPeriodDto;
import com.bt.nextgen.api.regularinvestment.v2.model.RIPAction;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.account.LinkedAccountImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.avaloq.order.OrderGroupImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemSummaryImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.avaloq.regularinvestment.RIPScheduleImpl;
import com.bt.nextgen.service.avaloq.regularinvestment.RegularInvestmentImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.OrderGroup;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.order.PriceType;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.regularinvestment.RIPRecurringFrequency;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentDelegateService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentIntegrationService;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class RegularInvestmentDtoServiceTest {

    @InjectMocks
    private RegularInvestmentDtoServiceImpl regularInvestmentService;

    @Mock
    private RegularInvestmentIntegrationService ripIntegrationService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private OrderGroupDtoErrorMapper orderGroupDtoErrorMapper;

    @Mock
    private OrderItemHelper orderItemHelper;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Mock
    private AssetDtoConverter assetDtoConverter;

    @Mock
    private OrderIntegrationService orderIntegrationService;

    @Mock
    private AccountHelper accountHelper;

    @Mock
    private RegularInvestmentDelegateService delegate;

    @Spy
    private final OrderGroupDtoErrorMapper orderDtoErrorMapper = new OrderGroupDtoErrorMapperImpl();

    private List<ValidationError> warnings;
    private List<DomainApiErrorDto> apiErrors;

    private AssetDto assetDto;
    private Map<String, AssetDto> assetDtos;

    // models
    private OrderItemSummaryImpl summary;
    private OrderItemImpl buyOrderModel1;
    private OrderItemImpl buyOrderModel2;
    private OrderItemImpl buyOrderModel3;
    private OrderItemImpl buyOrderShare;
    private OrderItemImpl sellOrderModel;
    private OrderItemImpl sellOrderShare;

    private List<OrderItem> orderItems;

    private OrderGroupImpl orderGroup;
    private OrderGroupImpl orderGroup1;
    private OrderGroupImpl orderGroup2;
    private List<OrderGroup> orderGroupList;

    private Collection<Broker> brokers;

    // dtos
    private OrderItemDto buyOrderDto;
    private List<OrderItemDto> buysDto;
    private OrderGroupDto orderGroupDto;
    private RegularInvestmentDto invDto;
    private OrderGroupDto nullKeyOrderGroupDto;
    private DepositDto depDto;

    private RegularInvestmentImpl regInv;

    private RegularInvestmentDto suspendInvDto;
    private RegularInvestmentDto activateInvDto;
    private RegularInvestmentDto cancelInvDto;

    RecurringDepositDetailsImpl recurrDeposit;

    private WrapAccountValuationImpl valuation;

    @Before
    public void setup() throws Exception {
        DateTime startDate = DateTime.now();
        DateTime endDate = getDefaultEndDate();

        orderGroupList = new ArrayList<OrderGroup>();
        warnings = new ArrayList<>();
        warnings.add(new ValidationError("avaloqErrorId", "field", "message", ErrorType.WARNING));
        warnings.add(new ValidationError("avaloqErrorId2", "field2", "message2", ErrorType.WARNING));
        apiErrors = new ArrayList<>();
        apiErrors.add(new DomainApiErrorDto("errorId", "domain", "reason", "message", DomainApiErrorDto.ErrorType.WARNING));
        apiErrors.add(new DomainApiErrorDto("errorId2", "domain2", "reason2", "message2", DomainApiErrorDto.ErrorType.WARNING));

        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetId()).thenReturn("1234");
        Mockito.when(asset.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);

        orderGroup1 = new OrderGroupImpl();
        orderGroup1.setAccountKey(AccountKey.valueOf("accountKey"));
        orderGroup1.setOrderGroupId("1234");
        orderGroup1.setLastUpdateDate(new DateTime());
        orderGroup1.setOwner(ClientKey.valueOf("testClient"));
        orderGroup1.setOrderType("buy");
        orderGroup1.setReference("Bob's Transaction");
        orderGroup1.setWarnings(warnings);

        orderGroup2 = new OrderGroupImpl();
        orderGroup2.setAccountKey(AccountKey.valueOf("accountKey"));
        orderGroup2.setOrderGroupId("4321");
        orderGroup2.setLastUpdateDate(new DateTime());
        orderGroup2.setOwner(ClientKey.valueOf("testClient"));
        orderGroup2.setOrderType("sell");
        orderGroup2.setReference("To Cayman Island");
        orderGroup2.setWarnings(warnings);

        orderGroupList.add(orderGroup1);
        orderGroupList.add(orderGroup2);

        Pair<String, BigDecimal> allocation = new ImmutablePair<String, BigDecimal>("9876", new BigDecimal("12345"));
        Pair<String, BigDecimal> allocation2 = new ImmutablePair<String, BigDecimal>("54321", new BigDecimal("67890"));
        List<Pair<String, BigDecimal>> allocations = new ArrayList<>();
        allocations.add(allocation);
        allocations.add(allocation2);
        orderItems = new ArrayList<>();

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, DistributionMethod.CASH.getDisplayName(), null, null,
                null, null);
        buyOrderModel1 = new OrderItemImpl("641961", "buy", AssetType.MANAGED_FUND, asset.getAssetId(), summary, allocations);
        buyOrderModel1.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        buyOrderModel1.setFirstNotification("BuyOrderModel1 failed due to insufficient funds");
        orderItems.add(buyOrderModel1);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        buyOrderModel2 = new OrderItemImpl("641962", "buy", AssetType.MANAGED_PORTFOLIO, asset.getAssetId(), summary,
                Collections.singletonList(allocation));
        buyOrderModel2.setSubAccountKey(null);
        orderItems.add(buyOrderModel2);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, DistributionMethod.REINVEST.getDisplayName(), null,
                null, null, null);
        sellOrderModel = new OrderItemImpl("641963", "sell", AssetType.MANAGED_FUND, asset.getAssetId(), summary,
                Collections.singletonList(allocation));
        sellOrderModel.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        orderItems.add(sellOrderModel);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        buyOrderModel3 = new OrderItemImpl("641960", "sell", AssetType.MANAGED_PORTFOLIO, asset.getAssetId(), summary,
                Collections.singletonList(allocation));
        buyOrderModel3.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        orderItems.add(buyOrderModel3);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, BigInteger.valueOf(1000), BigDecimal.valueOf(10),
                ExpiryMethod.GFD.getIntlId(), PriceType.LIMIT);
        buyOrderShare = new OrderItemImpl("641964", "buy", AssetType.SHARE, asset.getAssetId(), summary,
                Collections.singletonList(allocation));
        buyOrderShare.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        orderItems.add(buyOrderShare);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, BigInteger.valueOf(1000), BigDecimal.valueOf(10),
                ExpiryMethod.GTC.getIntlId(), PriceType.MARKET);
        sellOrderShare = new OrderItemImpl("641965", "sell", AssetType.SHARE, asset.getAssetId(), summary,
                Collections.singletonList(allocation));
        sellOrderShare.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        orderItems.add(sellOrderShare);

        orderGroup = new OrderGroupImpl();
        orderGroup.setOrders(orderItems);
        orderGroup.setFirstNotification("RIP Failed due to insufficient funds");

        List<FundsAllocationDto> fundsAllocations = new ArrayList<>();
        FundsAllocationDto fundsAllocation = new FundsAllocationDto("46804E8B5F179DA38D92E506C2A825BD771E85B9A85D17C6",
                BigDecimal.ONE);
        fundsAllocations.add(fundsAllocation);

        // initialise dtos
        AssetImpl mpAsset = new AssetImpl();
        mpAsset.setAssetId("1234");
        mpAsset.setStatus(AssetStatus.OPEN);
        assetDto = new ManagedPortfolioAssetDto(asset);

        OrderItemSummaryDto summaryDto = new OrderItemSummaryDto(new BigDecimal(10000), false, null, null, null, null, null);

        buyOrderDto = new OrderItemDto(null, assetDto, AssetType.MANAGED_PORTFOLIO.getDisplayName(), "Buy", summaryDto,
                fundsAllocations);

        buysDto = new ArrayList<>();
        buysDto.add(buyOrderDto);
        buysDto.add(buyOrderDto);

        orderGroupDto = new OrderGroupDto();
        orderGroupDto.setKey(new OrderGroupKey("account", "1234"));
        orderGroupDto.setAccountKey(
                new com.bt.nextgen.api.account.v3.model.AccountKey("9BE792880789BE27BDF51D56C27A8B10EE538C9A0834620A"));
        orderGroupDto.setLastUpdateDate(new DateTime());
        orderGroupDto.setOrders(buysDto);
        orderGroupDto.setOwner("testClient");
        orderGroupDto.setOwnerName("Robert Gilby");
        orderGroupDto.setReference("Bob's Transaction");
        orderGroupDto.setStatus("submit");
        orderGroupDto.setWarnings(apiErrors);

        nullKeyOrderGroupDto = new OrderGroupDto();
        nullKeyOrderGroupDto.setAccountKey(
                new com.bt.nextgen.api.account.v3.model.AccountKey("9BE792880789BE27BDF51D56C27A8B10EE538C9A0834620A"));
        nullKeyOrderGroupDto.setLastUpdateDate(new DateTime());
        nullKeyOrderGroupDto.setOrders(buysDto);
        nullKeyOrderGroupDto.setOwner("testClient");
        nullKeyOrderGroupDto.setOwnerName("Robert Gilby");
        nullKeyOrderGroupDto.setReference("Bob's Transaction");
        nullKeyOrderGroupDto.setStatus("submit");
        nullKeyOrderGroupDto.setWarnings(apiErrors);

        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setAccountId("accountId");

        depDto = new DepositDto();
        depDto.setAmount(new BigDecimal(1000d));
        depDto.setDescription("Test");
        depDto.setIsRecurring(true);
        depDto.setFromPayDto(payeeDto);
        depDto.setFrequency("Quarterly");
        depDto.setTransactionDate("01 MAY 2018");
        depDto.setRepeatEndDate("01 DEC 2018");
        depDto.setEndRepeat("setDate");

        Map<String, Asset> assets = new HashMap<>();
        assets.put(asset.getAssetId(), asset);

        assetDtos = new HashMap<>();
        assetDtos.put(assetDto.getAssetId(), assetDto);

        invDto = new RegularInvestmentDto(orderGroupDto, null, new InvestmentPeriodDto(startDate, endDate, null, "Monthly"),
                "Active", null);

        suspendInvDto = new RegularInvestmentDto(orderGroupDto, null,
                new InvestmentPeriodDto(startDate, endDate, null, "Monthly"), "suspend", null);

        activateInvDto = new RegularInvestmentDto(orderGroupDto, null,
                new InvestmentPeriodDto(startDate, endDate, null, "Monthly"), "activate", null);

        cancelInvDto = new RegularInvestmentDto(orderGroupDto, null, new InvestmentPeriodDto(startDate, endDate, null, "Monthly"),
                "cancel", null);

        BankAccountDto bankDto = new BankAccountDto();
        bankDto.setAccountNumber("accountNumber");
        bankDto.setBsb("BSB");
        bankDto.setName("Name");
        Mockito.when(accountHelper.getBankAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(bankDto);

        LinkedAccountImpl la = new LinkedAccountImpl();
        la.setAccountNumber("123456789");
        la.setBsb("123456");
        la.setName("abcd");
        List<LinkedAccount> laList = new ArrayList<>();
        laList.add(la);
        PayeeDetailsImpl payeeDetails = new PayeeDetailsImpl();
        payeeDetails.setLinkedAccountList(laList);

        Mockito.when(accountHelper.getPayeeDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(payeeDetails);

        regInv = new RegularInvestmentImpl(this.orderGroup);
        regInv.setAccountKey(orderGroup1.getAccountKey());
        regInv.setRIPSchedule(new RIPScheduleImpl(DateTime.now(), getDefaultEndDate(), RIPRecurringFrequency.Quarterly));
        regInv.setRIPStatus(RIPStatus.SUSPENDED);

        recurrDeposit = new RecurringDepositDetailsImpl();
        PayAnyoneAccountDetails payAnyoneAccountDetails = new PayAnyoneAccountDetailsImpl();
        payAnyoneAccountDetails.setAccount("123456789");
        payAnyoneAccountDetails.setBsb("123456");
        recurrDeposit.setPositionId("1234");
        recurrDeposit.setCurrencyType(CurrencyType.AustralianDollar);
        recurrDeposit.setReceiptNumber("123456");
        recurrDeposit.setTransactionDate(new DateTime());
        recurrDeposit.setPayAnyoneAccountDetails(payAnyoneAccountDetails);
        recurrDeposit.setDepositAmount(new BigDecimal(999d));
        recurrDeposit.setDepositDate(new DateTime());
        recurrDeposit.setEndDate(getDefaultEndDate());
        recurrDeposit.setRecurringFrequency(RecurringFrequency.Monthly);
        regInv.setDirectDebitDetails(recurrDeposit);

        BrokerUser brokerUser = Mockito.mock(BrokerUser.class);
        Mockito.when(brokerUser.getBankReferenceKey()).thenReturn(UserKey.valueOf("testUser"));
        Mockito.when(brokerUser.getJob()).thenReturn(JobKey.valueOf("testJob"));
        Mockito.when(brokerUser.getFirstName()).thenReturn("Bob");
        Mockito.when(brokerUser.getLastName()).thenReturn("Gilby");
        Mockito.when(brokerUser.getClientKey()).thenReturn(ClientKey.valueOf("testClient"));
        Mockito.when(brokerUser.isRegisteredOnline()).thenReturn(false);
        Mockito.when(brokerUser.getAge()).thenReturn(0);
        Mockito.when(brokerUser.isRegistrationOnline()).thenReturn(false);

        Mockito.when(orderGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);
        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assets);

        Mockito.when(brokerService.getBrokerUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(brokerUser);

        brokers = new ArrayList<Broker>();
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("testUser"));
        Mockito.when(broker.isPayableParty()).thenReturn(false);

        brokers.add(broker);

        Mockito.when(userProfileService.getUserId()).thenReturn("201601388");

        UserProfile userInfo = Mockito.mock(UserProfile.class);
        Mockito.when(userInfo.getClientKey()).thenReturn(ClientKey.valueOf("testClient"));

        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userInfo);

        Mockito.when(brokerService.getBrokersForUser(Mockito.any(UserKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(brokers);
        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(broker);

        WrapAccountDetail accountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(accountDetail.getAdviserKey()).thenReturn(BrokerKey.valueOf("brokerKey"));
        Mockito.when(accountDetail.isOpen()).thenReturn(false);
        Mockito.when(accountDetail.getAdminFeeRate()).thenReturn(new BigDecimal("9.98"));
        Mockito.when(accountDetail.isHasMinCash()).thenReturn(false);

        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);

        valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf("AccountKey"));
        valuation.setSubAccountValuations(new ArrayList<SubAccountValuation>());

        Mockito.when(
                cachedPortfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        Mockito.when(orderItemHelper.toOrderItems(buysDto, valuation)).thenReturn(orderItems);

        Map<String, AssetDto> assetDtoMap = new HashMap<>();
        assetDtoMap.put(asset.getAssetId(), new AssetDto());

        Mockito.when(assetDtoConverter.toAssetDto(Mockito.any(Map.class), Mockito.any(Map.class))).thenReturn(assetDtoMap);
    }

    @Test
    public void testToRegularInvestment_NoDepositDetail() {
        DateTime startDate = DateTime.now();
        DateTime endDate = getDefaultEndDate();
        RegularInvestmentDto invDto = new RegularInvestmentDto(orderGroupDto, null,
                new InvestmentPeriodDto(startDate, endDate, null, "Monthly"), "Active", null);

        RegularInvestment regInv = regularInvestmentService.toRegularInvestment(invDto, new ServiceErrorsImpl());
        Assert.assertNull(regInv.getDirectDebitDetails());
    }

    @Test
    public void testToRegularInvestment_WithDepositDetail() {

        Mockito.when(accountHelper.getRecurringDepositDetails(Mockito.any(RegularInvestmentDto.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new RecurringDepositDetailsImpl());

        DateTime startDate = DateTime.now();
        DateTime endDate = getDefaultEndDate();
        RegularInvestmentDto invDto = new RegularInvestmentDto(orderGroupDto, depDto,
                new InvestmentPeriodDto(startDate, endDate, null, "Monthly"), "Active", null);

        RegularInvestment regInv = regularInvestmentService.toRegularInvestment(invDto, new ServiceErrorsImpl());
        Assert.assertNotNull(regInv.getDirectDebitDetails());
    }

    @Test
    public void testToRegularInvestmentDto_NoDepositDetails() {
        RegularInvestmentImpl regInv = new RegularInvestmentImpl(this.orderGroup);
        regInv.setAccountKey(orderGroup1.getAccountKey());
        regInv.setRIPSchedule(new RIPScheduleImpl(DateTime.now(), getDefaultEndDate(), RIPRecurringFrequency.Quarterly));
        regInv.setRIPStatus(RIPStatus.SUSPENDED);

        RegularInvestmentDto dto = regularInvestmentService.toRegularInvestmentDto(regInv, new RegularInvestmentDto(),
                new ServiceErrorsImpl(), new ServiceErrorsImpl());
        Assert.assertTrue(dto != null);
        Assert.assertTrue(dto.getDepositDetails() == null);
        Assert.assertTrue(dto.getRipStatus().equals(regInv.getRIPStatus().getDisplayName()));
        Assert.assertTrue(dto.getFirstNotification().equals(regInv.getFirstNotification()));
        Assert.assertEquals(dto.getInvestmentEndDate(), regInv.getRIPSchedule().getLastExecDate());
        Assert.assertEquals(dto.getNextDueDate(), regInv.getRIPSchedule().getNextExecDate());
    }

    @Test
    public void testToRegularInvestmentDto_WithDepositDetails() {
        RegularInvestmentImpl regInv = new RegularInvestmentImpl(this.orderGroup);
        regInv.setAccountKey(orderGroup1.getAccountKey());
        regInv.setRIPSchedule(new RIPScheduleImpl(DateTime.now(), getDefaultEndDate(), RIPRecurringFrequency.Quarterly));
        regInv.setRIPStatus(RIPStatus.ACTIVE);

        RecurringDepositDetailsImpl recurrDeposit = new RecurringDepositDetailsImpl();
        recurrDeposit.setDepositAmount(new BigDecimal(999d));
        recurrDeposit.setDepositDate(new DateTime());
        recurrDeposit.setEndDate(getDefaultEndDate());
        recurrDeposit.setRecurringFrequency(RecurringFrequency.Monthly);
        recurrDeposit.setNextTransactionDate(getDefaultEndDate().plusMonths(4).toDateTime());
        regInv.setDirectDebitDetails(recurrDeposit);

        MoneyAccountIdentifierImpl moneyAcc = new MoneyAccountIdentifierImpl();
        moneyAcc.setMoneyAccountId("moneyAccountId");
        Mockito.when(accountHelper.getMoneyAccountIdentifier(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(moneyAcc);
        BankAccountDto bankDto = new BankAccountDto();
        bankDto.setAccountNumber("accountNumber");
        bankDto.setBsb("BSB");
        bankDto.setName("Name");
        Mockito.when(accountHelper.getBankAccountDetails(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(bankDto);

        ServiceErrors ddErr = null;
        ServiceErrors ripErr = null;
        RegularInvestmentDto dto = regularInvestmentService.toRegularInvestmentDto(regInv, new RegularInvestmentDto(), ddErr,
                ripErr);

        Assert.assertTrue(dto != null);
        Assert.assertTrue(dto.getDepositDetails() != null);
        Assert.assertTrue(dto.getRipStatus().equals(regInv.getRIPStatus().getDisplayName()));
        Assert.assertEquals(dto.getInvestmentEndDate(), regInv.getDirectDebitDetails().getEndDate());
        Assert.assertEquals(dto.getNextDueDate(), regInv.getDirectDebitDetails().getNextTransactionDate());
    }

    @Test
    public void testValidate() {

        Mockito.when(delegate.validateRegularInvestment(Mockito.any(RegularInvestment.class), Mockito.any(ServiceErrors.class),
                Mockito.any(ServiceErrors.class))).thenReturn(regInv);
        regInv.setOrderGroupId("1111");
        RegularInvestmentDto ripDto = regularInvestmentService.validate(invDto, null);
        Assert.assertNotNull(ripDto);
        Assert.assertEquals(ripDto.getDepositDetails().getAmount(), regInv.getDirectDebitDetails().getDepositAmount());
        Assert.assertEquals(ripDto.getKey().getOrderGroupId(), regInv.getOrderGroupId());
    }

    @Test
    public void testSubmit() {

        Mockito.when(delegate.submitRegularInvestment(Mockito.any(RegularInvestment.class), Mockito.any(ServiceErrors.class),
                Mockito.any(ServiceErrors.class))).thenReturn(regInv);
        regInv.setOrderGroupId("1111");
        RegularInvestmentDto ripDto = regularInvestmentService.submit(invDto, null);
        Assert.assertNotNull(ripDto);
        Assert.assertEquals(ripDto.getDepositDetails().getAmount(), regInv.getDirectDebitDetails().getDepositAmount());
        Assert.assertEquals(ripDto.getKey().getOrderGroupId(), regInv.getOrderGroupId());

    }
    @Test
    public void testSave(){
        Mockito.when(delegate.saveRegularInvestment(Mockito.any(RegularInvestment.class), Mockito.any(ServiceErrors.class),
                Mockito.any(ServiceErrors.class))).thenReturn(regInv);
        regInv.setOrderGroupId("1111");
        RegularInvestmentDto ripDto = regularInvestmentService.create(invDto, null);
        Assert.assertNotNull(ripDto);
        Assert.assertEquals(ripDto.getDepositDetails().getAmount(), regInv.getDirectDebitDetails().getDepositAmount());
        Assert.assertEquals(ripDto.getKey().getOrderGroupId(), regInv.getOrderGroupId());
    }

    @Test
    public void testSuspend_withDirectDebit() {
        Mockito.when(delegate.suspendRegularInvestment(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(regInv);
        regInv.setOrderGroupId("1111");
        regInv.setRIPStatus(RIPStatus.SUSPENDED);
        RegularInvestmentDto ripDto = regularInvestmentService.update(suspendInvDto, null);
        Assert.assertNotNull(ripDto);
        Assert.assertEquals(ripDto.getDepositDetails().getAmount(), regInv.getDirectDebitDetails().getDepositAmount());
        Assert.assertEquals(ripDto.getKey().getOrderGroupId(), regInv.getOrderGroupId());
        Assert.assertEquals(ripDto.getRipStatus(), "Suspended");
    }

    @Test
    public void testCancel_withDirectDebit() {
        Mockito.when(delegate.cancelRegularInvestment(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(regInv);
        regInv.setOrderGroupId("1111");
        regInv.setRIPStatus(RIPStatus.CANCELLED);
        RegularInvestmentDto ripDto = regularInvestmentService.update(cancelInvDto, null);
        Assert.assertNotNull(ripDto);
        Assert.assertEquals(ripDto.getDepositDetails().getAmount(), regInv.getDirectDebitDetails().getDepositAmount());
        Assert.assertEquals(ripDto.getKey().getOrderGroupId(), regInv.getOrderGroupId());
        Assert.assertEquals(ripDto.getRipStatus(), "Cancelled");

    }

    @Test
    public void testResume_withDirectDebit() {
        Mockito.when(delegate.resumeRegularInvestment(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class),
                Mockito.any(ServiceErrors.class))).thenReturn(regInv);
        regInv.setOrderGroupId("1111");
        regInv.setRIPStatus(RIPStatus.ACTIVE);
        RegularInvestmentDto ripDto = regularInvestmentService.update(activateInvDto, null);
        Assert.assertNotNull(ripDto);
        Assert.assertEquals(ripDto.getDepositDetails().getAmount(), regInv.getDirectDebitDetails().getDepositAmount());
        Assert.assertEquals(ripDto.getKey().getOrderGroupId(), regInv.getOrderGroupId());

        Assert.assertEquals(ripDto.getRipStatus(), "Active");
    }

    public void testUpdateSuspend_withoutDirectDebit() {
        RegularInvestmentImpl regInv = new RegularInvestmentImpl();
        regInv.setRIPStatus(RIPStatus.ACTIVE);
        regInv.setAccountKey(AccountKey.valueOf("accountKey"));

        RegularInvestmentDto invDto = new RegularInvestmentDto(this.orderGroupDto, null, new InvestmentPeriodDto(null),
                RIPAction.SUSPEND.getAction(), null);

        regInv.setRIPStatus(RIPStatus.SUSPENDED);
        Mockito.when(delegate.suspendRegularInvestment(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(regInv);

        RegularInvestmentDto dto = regularInvestmentService.update(invDto, new ServiceErrorsImpl());
        Assert.assertTrue(dto != null);
        Assert.assertEquals(RIPStatus.SUSPENDED.getDisplayName(), dto.getRipStatus());
    }

    @Test
    public void testUpdateResume_withoutDirectDebit() {
        RegularInvestmentImpl regInv = new RegularInvestmentImpl();
        regInv.setRIPStatus(RIPStatus.ACTIVE);
        regInv.setAccountKey(AccountKey.valueOf("accountKey"));

        RegularInvestmentDto invDto = new RegularInvestmentDto(this.orderGroupDto, null, new InvestmentPeriodDto(null),
                RIPAction.RESUME.getAction(), null);

        regInv.setRIPStatus(RIPStatus.ACTIVE);
        Mockito.when(delegate.resumeRegularInvestment(Mockito.any(OrderGroupKey.class), Mockito.any(ServiceErrors.class),
                Mockito.any(ServiceErrors.class))).thenReturn(regInv);

        RegularInvestmentDto dto = regularInvestmentService.update(invDto, new ServiceErrorsImpl());
        Assert.assertTrue(dto != null);
        Assert.assertEquals(RIPStatus.ACTIVE.getDisplayName(), dto.getRipStatus());
    }

    @Test
    public void testGetRIP() {
        Mockito.when(delegate.loadRegularInvestment(Mockito.any(AccountKey.class), Mockito.anyString(),
                Mockito.any(ServiceErrors.class), Mockito.any(ServiceErrors.class))).thenReturn(regInv);
        regInv.setOrderGroupId("1234");
        regInv.setRIPStatus(RIPStatus.ACTIVE);

        String accountId = EncodedString.fromPlainText("account").toString();
        RegularInvestmentDto ripDto = regularInvestmentService.find(new OrderGroupKey(accountId, "1234"), null);
        Assert.assertNotNull(ripDto);
        Assert.assertEquals(ripDto.getDepositDetails().getAmount(), regInv.getDirectDebitDetails().getDepositAmount());
        Assert.assertEquals(ripDto.getKey().getOrderGroupId(), regInv.getOrderGroupId());
        Assert.assertEquals(ripDto.getRipStatus(), "Active");

    }

    private DateTime getDefaultEndDate() {
        return DateTime.now().plusYears(2);
    }

}
