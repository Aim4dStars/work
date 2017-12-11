package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.asset.service.AssetDtoConverterV2;
import com.bt.nextgen.api.order.model.FundsAllocationDto;
import com.bt.nextgen.api.order.model.OrderFeeDto;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.model.OrderItemSummaryDto;
import com.bt.nextgen.api.order.model.SlidingScaleFeeDto;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapperImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.payments.web.model.AccountVerificationStatus;
import com.bt.nextgen.payments.web.model.TwoFactorAccountVerificationKey;
import com.bt.nextgen.payments.web.model.TwoFactorRuleModel;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.fees.FeesComponentType;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.FlatPercentFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.bt.nextgen.service.avaloq.order.OrderGroupImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemSummaryImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.avaloq.rules.AvaloqRulesIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.rules.RuleAction;
import com.bt.nextgen.service.avaloq.rules.RuleImpl;
import com.bt.nextgen.service.avaloq.rules.RuleType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.account.IssuerAccount;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.modelpreferences.Preference;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.OrderGroup;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.order.PreferenceAction;
import com.bt.nextgen.service.integration.order.PriceType;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class OrderGroupDtoServiceTest {
    private static final String SAFI_ORDER_SESSION_IDENTIFIER = "order-capture-safi";

    @InjectMocks
    private OrderGroupDtoServiceImpl orderGroupService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private OrderGroupDtoErrorMapper orderGroupDtoErrorMapper;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private PortfolioIntegrationService portfolioIntegrationService;

    @Mock
    private AssetDtoConverter assetDtoConverter;

    @Mock
    private OrderIntegrationService orderIntegrationService;

    @Mock
    private HttpSession httpSession;

    @Mock
    private AvaloqRulesIntegrationServiceImpl avaloqRulesIntegrationService;

    @Mock
    private CmsService cmsService;

    @Mock
    private AssetDtoConverterV2 assetDtoConverterV2;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @Mock
    private FeatureToggles featureToggles;

    @Spy
    private final OrderGroupDtoErrorMapper orderDtoErrorMapper = new OrderGroupDtoErrorMapperImpl();

    private List<ValidationError> warnings;
    private List<DomainApiErrorDto> apiErrors;

    private String assetId;
    private AssetDto assetDto;
    private Map<String, AssetDto> assetDtos;
    private final Map<AccountKey, IssuerAccount> issuerMap = Collections.emptyMap();

    // models
    private OrderItemSummaryImpl summary;
    private OrderItemImpl buyOrderModel1;
    private OrderItemImpl buyOrderModel2;
    private OrderItemImpl buyOrderModel3;
    private OrderItemImpl buyOrderShare;
    private OrderItemImpl buyOrderTd;
    private OrderItemImpl sellOrderModel;
    private OrderItemImpl sellOrderShare;

    private List<OrderItem> orderItems;

    private OrderGroupImpl orderGroup;
    private OrderGroupImpl orderGroup1;
    private OrderGroupImpl orderGroup2;
    private OrderGroupImpl orderGroup3;
    private List<OrderGroup> orderGroupList;

    // dtos
    private OrderItemDto buyOrderDto;
    private OrderItemDto buyOrderDto2;
    private SlidingScaleFeesComponent slidingFee;
    private List<OrderItemDto> buysDto;
    private OrderGroupDto orderGroupDto;
    private OrderGroupDto nullKeyOrderGroupDto;

    private WrapAccountValuationImpl valuation;

    private TwoFactorAccountVerificationKey accountVerificationKey;
    private TwoFactorRuleModel twoFactorRuleModelAuthDone;
    private TwoFactorRuleModel twoFactorRuleModelAuthNotDone;
    List<OrderItem> listTDOrders;

    @Before
    public void setup() throws Exception {
        orderGroupList = new ArrayList<OrderGroup>();
        warnings = new ArrayList<>();
        warnings.add(new ValidationError("avaloqErrorId", "field", "message", ErrorType.WARNING));
        warnings.add(new ValidationError("avaloqErrorId2", "field2", "message2", ErrorType.WARNING));
        apiErrors = new ArrayList<>();
        apiErrors.add(new DomainApiErrorDto("errorId", "domain", "reason", "message", DomainApiErrorDto.ErrorType.WARNING));
        apiErrors.add(new DomainApiErrorDto("errorId2", "domain2", "reason2", "message2", DomainApiErrorDto.ErrorType.WARNING));

        assetId = "1234";

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

        orderGroup3 = new OrderGroupImpl();
        orderGroup3.setAccountKey(AccountKey.valueOf("accountKey"));
        orderGroup3.setOrderGroupId("1234");
        orderGroup3.setLastUpdateDate(new DateTime());
        orderGroup3.setOwner(ClientKey.valueOf("testClient"));
        orderGroup3.setOrderType("buy");
        orderGroup3.setReference("ref001");
        orderGroup3.setWarnings(warnings);

        orderGroupList.add(orderGroup1);
        orderGroupList.add(orderGroup2);
        orderGroupList.add(orderGroup3);

        Pair<String, BigDecimal> allocation = new ImmutablePair<String, BigDecimal>("9876", new BigDecimal("12345"));
        Pair<String, BigDecimal> allocation2 = new ImmutablePair<String, BigDecimal>("54321", new BigDecimal("67890"));
        List<Pair<String, BigDecimal>> allocations = new ArrayList<>();
        allocations.add(allocation);
        allocations.add(allocation2);
        orderItems = new ArrayList<>();

        slidingFee = new SlidingScaleFeesComponent();
        slidingFee.setMinFees(new BigDecimal("1"));
        slidingFee.setMaxFees(new BigDecimal("5"));
        List<SlidingScaleTiers> tierList = new ArrayList<SlidingScaleTiers>();
        SlidingScaleTiers tier = new SlidingScaleTiers();
        tier.setLowerBound(new BigDecimal("2"));
        tier.setUpperBound(new BigDecimal("3"));
        tier.setPercent(new BigDecimal("0.1"));
        tierList.add(tier);

        SlidingScaleTiers tier1 = new SlidingScaleTiers();
        tier1.setLowerBound(new BigDecimal("3"));
        tier1.setUpperBound(new BigDecimal("34"));
        tier1.setPercent(new BigDecimal("0.2"));
        tierList.add(tier1);
        slidingFee.setTiers(tierList);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, DistributionMethod.CASH.getDisplayName(), null, null,
                null, null);
        buyOrderModel1 = new OrderItemImpl("641961", "buy", AssetType.MANAGED_FUND, assetId, summary, allocations);
        buyOrderModel1.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        buyOrderModel1.setIncomePreference(IncomePreference.REINVEST);
        List<FeesComponents> feeComponents = new ArrayList<>();
        feeComponents.add(new FlatPercentFeesComponent(BigDecimal.valueOf(0.1)));
        Map<FeesType, List<FeesComponents>> feeMap = new HashMap<>();
        feeMap.put(FeesType.PORTFOLIO_MANAGEMENT_FEE, feeComponents);
        buyOrderModel1.setFees(feeMap);
        orderItems.add(buyOrderModel1);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        buyOrderModel2 = new OrderItemImpl("641962", "buy", AssetType.MANAGED_PORTFOLIO, assetId, summary,
                Collections.singletonList(allocation));
        buyOrderModel2.setSubAccountKey(null);
        List<FeesComponents> feeComponents2 = new ArrayList<>();
        feeComponents2.add(slidingFee);
        Map<FeesType, List<FeesComponents>> feeMap2 = new HashMap<>();
        feeMap2.put(FeesType.PORTFOLIO_MANAGEMENT_FEE, feeComponents2);
        buyOrderModel2.setFees(feeMap2);
        orderItems.add(buyOrderModel2);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, DistributionMethod.REINVEST.getDisplayName(), null,
                null, null, null);
        sellOrderModel = new OrderItemImpl("641963", "sell", AssetType.MANAGED_FUND, assetId, summary,
                Collections.singletonList(allocation));
        sellOrderModel.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        orderItems.add(sellOrderModel);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, null, null, null, null);
        buyOrderModel3 = new OrderItemImpl("641960", "sell", AssetType.MANAGED_PORTFOLIO, assetId, summary,
                Collections.singletonList(allocation));
        buyOrderModel3.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        orderItems.add(buyOrderModel3);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, BigInteger.valueOf(1000), BigDecimal.valueOf(10),
                ExpiryMethod.GFD.getIntlId(), PriceType.LIMIT);
        buyOrderShare = new OrderItemImpl("641964", "buy", AssetType.SHARE, assetId, summary,
                Collections.singletonList(allocation));
        buyOrderShare.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        orderItems.add(buyOrderShare);

        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, null, BigInteger.valueOf(1000), BigDecimal.valueOf(10),
                ExpiryMethod.GTC.getIntlId(), PriceType.MARKET);
        sellOrderShare = new OrderItemImpl("641965", "sell", AssetType.SHARE, assetId, summary,
                Collections.singletonList(allocation));
        sellOrderShare.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        orderItems.add(sellOrderShare);

        listTDOrders = new ArrayList<>();
        summary = new OrderItemSummaryImpl(BigDecimal.valueOf(10), false, DistributionMethod.CASH.getDisplayName(), null, null,
                null, null);
        buyOrderTd = new OrderItemImpl("641961", "buy", AssetType.TERM_DEPOSIT, assetId, summary, allocations);
        buyOrderTd.setSubAccountKey(SubAccountKey.valueOf("mpsubaccount"));
        buyOrderTd.setIncomePreference(IncomePreference.REINVEST);
        listTDOrders.add(buyOrderTd);
        orderGroup3.setOrders(listTDOrders);

        orderGroup = new OrderGroupImpl();
        orderGroup.setOrders(orderItems);

        List<FundsAllocationDto> fundsAllocations = new ArrayList<>();
        FundsAllocationDto fundsAllocation = new FundsAllocationDto("46804E8B5F179DA38D92E506C2A825BD771E85B9A85D17C6",
                BigDecimal.ONE);
        fundsAllocations.add(fundsAllocation);

        // initialise dtos
        AssetImpl mpAsset = new AssetImpl();
        mpAsset.setAssetId("1234");
        mpAsset.setStatus(AssetStatus.OPEN);
        assetDto = new ManagedPortfolioAssetDto(mpAsset);

        OrderItemSummaryDto summaryDto = new OrderItemSummaryDto(new BigDecimal(10000), false, null, null, null, null, null);

        buyOrderDto = new OrderItemDto(null, assetDto, AssetType.MANAGED_PORTFOLIO.getDisplayName(), "Buy", summaryDto,
                fundsAllocations);
        buyOrderDto.setFees(Collections.singletonList(
                new OrderFeeDto(FeesType.PORTFOLIO_MANAGEMENT_FEE, new FlatPercentFeesComponent(BigDecimal.valueOf(0.1)))));
        buyOrderDto.setBankClearNumber("032140");
        buyOrderDto.setPayerAccount("1234561");
        buyOrderDto.setIncomePreference(IncomePreference.TRANSFER.toString());

        buyOrderDto2 = new OrderItemDto(null, assetDto, AssetType.MANAGED_PORTFOLIO.getDisplayName(), "Buy", summaryDto,
                fundsAllocations);
        buyOrderDto2.setFees(Collections.singletonList(new OrderFeeDto(FeesType.PORTFOLIO_MANAGEMENT_FEE, slidingFee)));
        buyOrderDto2.setBankClearNumber("032140");
        buyOrderDto2.setPayerAccount("1234561");
        buyOrderDto2.setIncomePreference(IncomePreference.TRANSFER.toString());

        buysDto = new ArrayList<>();
        buysDto.add(buyOrderDto);
        buysDto.add(buyOrderDto2);

        orderGroupDto = new OrderGroupDto();
        orderGroupDto.setKey(new OrderGroupKey("account", "1234"));
        orderGroupDto.setAccountKey(new com.bt.nextgen.api.account.v3.model.AccountKey(
                EncodedString.fromPlainText("36846").toString()));
        orderGroupDto.setLastUpdateDate(new DateTime());
        orderGroupDto.setOrders(buysDto);
        orderGroupDto.setOwner("testClient");
        orderGroupDto.setOwnerName("Robert Gilby");
        orderGroupDto.setReference("Bob's Transaction");
        orderGroupDto.setStatus("submit");
        orderGroupDto.setWarnings(apiErrors);

        nullKeyOrderGroupDto = new OrderGroupDto();
        nullKeyOrderGroupDto.setAccountKey(new com.bt.nextgen.api.account.v3.model.AccountKey(
                "9BE792880789BE27BDF51D56C27A8B10EE538C9A0834620A"));
        nullKeyOrderGroupDto.setLastUpdateDate(new DateTime());
        nullKeyOrderGroupDto.setOrders(buysDto);
        nullKeyOrderGroupDto.setOwner("testClient");
        nullKeyOrderGroupDto.setOwnerName("Robert Gilby");
        nullKeyOrderGroupDto.setReference("Bob's Transaction");
        nullKeyOrderGroupDto.setStatus("submit");
        nullKeyOrderGroupDto.setWarnings(apiErrors);

        assetDtos = new HashMap<>();
        assetDtos.put(assetDto.getAssetId(), assetDto);

        twoFactorRuleModelAuthDone = new TwoFactorRuleModel();
        twoFactorRuleModelAuthNotDone = new TwoFactorRuleModel();
        accountVerificationKey = new TwoFactorAccountVerificationKey("1234561", "032140");
        twoFactorRuleModelAuthNotDone.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus("1234", false));
        twoFactorRuleModelAuthDone.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus("1234", true));

        Mockito.when(orderGroupDtoErrorMapper.map(Mockito.anyList())).thenReturn(apiErrors);

        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyCollection(), any(ServiceErrors.class))).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        Collection<String> assetIds = (Collection<String>) args[0];
                        Map<String, Asset> assetMap = new HashMap<>();
                        for (String assetId : assetIds) {
                            if (assetId.equals(assetId)) {
                                AssetImpl mpAsset = mock(AssetImpl.class);
                                Mockito.when(mpAsset.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);
                                assetMap.put(assetId, mpAsset);
                            }
                        }
                        return assetMap;
                    }
                });

        Mockito.when(brokerService.getBrokerUser(any(UserKey.class), any(ServiceErrors.class))).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        BrokerUser brokerUser = mock(BrokerUser.class);
                        Mockito.when(brokerUser.getBankReferenceKey()).thenReturn(UserKey.valueOf("testUser"));
                        Mockito.when(brokerUser.getJob()).thenReturn(JobKey.valueOf("testJob"));
                        Mockito.when(brokerUser.getFirstName()).thenReturn("Bob");
                        Mockito.when(brokerUser.getLastName()).thenReturn("Gilby");
                        Mockito.when(brokerUser.getClientKey()).thenReturn(ClientKey.valueOf("testClient"));
                        Mockito.when(brokerUser.isRegisteredOnline()).thenReturn(false);
                        return brokerUser;
                    }
                });

        Mockito.when(userProfileService.getUserId()).thenReturn("201601388");
        Mockito.when(userProfileService.getActiveProfile()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                UserProfile userInfo = mock(UserProfile.class);
                Mockito.when(userInfo.getClientKey()).thenReturn(ClientKey.valueOf("testClient"));
                return userInfo;
            }
        });

        Mockito.when(brokerService.getBrokersForUser(any(UserKey.class), any(ServiceErrors.class))).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        Collection<Broker> brokers = new ArrayList<>();
                        Broker broker = mock(Broker.class);
                        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("testUser"));
                        Mockito.when(broker.isPayableParty()).thenReturn(false);
                        brokers.add(broker);
                        return brokers;
                    }
                });

        Mockito.when(brokerService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        Broker broker = mock(Broker.class);
                        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("testUser"));
                        Mockito.when(broker.isPayableParty()).thenReturn(false);
                        return broker;
                    }
                });
        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(any(AccountKey.class), any(ServiceErrors.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        WrapAccountDetail accountDetail = mock(WrapAccountDetail.class);
                        Mockito.when(accountDetail.isOpen()).thenReturn(false);
                        Mockito.when(accountDetail.getAdviserKey()).thenReturn(BrokerKey.valueOf("brokerKey"));
                        Mockito.when(accountDetail.getAdminFeeRate()).thenReturn(new BigDecimal("9.98"));
                        return accountDetail;
                    }
                });

        valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf("AccountKey"));
        valuation.setSubAccountValuations(new ArrayList<SubAccountValuation>());

        Mockito.when(portfolioIntegrationService.loadWrapAccountValuation(Mockito.any(AccountKey.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        Map<String, AssetDto> assetDtoMap = new HashMap<>();
        assetDtoMap.put(assetId, new AssetDto());

        Map<String, Asset> assetDtoMapwithTD = new HashMap<>();
        TermDepositAssetImpl assetTD; assetTD = new TermDepositAssetImpl();
        assetTD.setAssetId("28100");
        assetTD.setAssetCode("TD0001");
        assetTD.setAssetType(AssetType.TERM_DEPOSIT);
        assetTD.setAssetName("St George 6 monthts");
        assetTD.setStatus(AssetStatus.OPEN);
        assetDtoMapwithTD.put("28100", assetTD);

        Answer<Map<String, AssetDto>> assetAnswer = new Answer<Map<String, AssetDto>>() {
            @Override
            public Map<String, AssetDto> answer(InvocationOnMock invocation) throws Throwable {
                Map<String, Asset> assets = (Map<String, Asset>) invocation.getArguments()[0];
                Map<String, AssetDto> result = new HashMap<>();
                for (Asset asset : assets.values()) {
                    if (asset.getAssetType() == AssetType.MANAGED_PORTFOLIO) {
                        result.put(asset.getAssetId(), new ManagedPortfolioAssetDto(asset));
                    } else if (asset.getAssetType() == AssetType.MANAGED_FUND) {
                        ManagedFundAsset mfAsset = (ManagedFundAsset) asset;
                        result.put(asset.getAssetId(), new ManagedFundAssetDto(mfAsset));
                    } else if (asset.getAssetType() == AssetType.SHARE) {
                        ShareAsset shareAsset = (ShareAsset) asset;
                        result.put(asset.getAssetId(), new ShareAssetDto(shareAsset));
                    } else {
                        List<InterestRateDto> interestBands = Collections.emptyList();
                        result.put(asset.getAssetId(), new TermDepositAssetDto(asset, asset.getAssetName(), null, null, null,
                                null, null, null, interestBands, null));
                    }
                }

                return result;
            }
        };

        Mockito.when(assetDtoConverter.toAssetDto(any(Map.class), any(Map.class))).thenReturn(assetDtoMap);
        Mockito.when(assetDtoConverterV2.toAssetDto(Mockito.anyMap(), Mockito.anyList(), Mockito.anyBoolean())).thenAnswer(assetAnswer);
        when(featureTogglesService.findOne(any(FailFastErrorsImpl.class))).thenReturn(featureToggles);
    }

    @Test
    public void testToOrderDto_valuesMatch() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        for (OrderGroup orderGroup : orderGroupList) {
            WrapAccountImpl accDetail = new WrapAccountImpl();
            accDetail.setAccountName("Wrap Account Name 1");

            Mockito.when(
                    accountIntegrationService.loadWrapAccountWithoutContainers(any(AccountKey.class), any(ServiceErrors.class)))
                    .thenReturn(accDetail);

            OrderGroupDto group = orderGroupService.toOrderGroupDto(orderGroup, new ServiceErrorsImpl());
            assertNotNull(group);

            Assert.assertEquals(orderGroup.getOrderGroupId(), group.getKey().getOrderGroupId());
            Assert.assertEquals(orderGroup.getReference(), group.getReference());
            Assert.assertEquals(orderGroup.getLastUpdateDate(), group.getLastUpdateDate());
            Assert.assertEquals(orderGroup.getOwner().getId(), EncodedString.toPlainText(group.getOwner()));
            Assert.assertEquals(orderGroup.getWarnings().size(), group.getWarnings().size());
        }
    }

    @Test
    public void testToOrdersDto_whenNullOrders_thenNull() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        List<OrderItemDto> orders = orderGroupService.toOrdersDto(AccountKey.valueOf("accountId"), null, new ServiceErrorsImpl());
        Assert.assertNull(orders);
    }

    @Test
    public void testToOrdersDto_whenOrders_thenSizeMatches() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        List<OrderItemDto> orders = orderGroupService.toOrdersDto(AccountKey.valueOf("accountId"), orderItems,
                new ServiceErrorsImpl());
        Assert.assertEquals(orderItems.size(), orders.size());
    }

    @Test
    public void testToOrderDto_whenAllocations_thenSizeMatches() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderItemDto order = orderGroupService.toOrderDto(buyOrderModel1, assetDtos, issuerMap);
        Assert.assertEquals(buyOrderModel1.getFundsSource().size(), order.getFundsAllocation().size());
    }

    @Test
    public void testToOrderDto_whenOrder_thenValuesMatch() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderItemDto order = orderGroupService.toOrderDto(buyOrderModel1, assetDtos, issuerMap);
        Assert.assertEquals(buyOrderModel1.getFundsSource().get(0).getValue(), order.getFundsAllocation().get(0).getAllocation());
        Assert.assertEquals(buyOrderModel1.getOrderId(), order.getOrderId());
        Assert.assertEquals(buyOrderModel1.getAssetId(), order.getAsset().getAssetId());
        Assert.assertEquals(buyOrderModel1.getOrderType(), order.getOrderType());
        Assert.assertEquals(buyOrderModel1.getAmount(), order.getAmount());
        Assert.assertEquals(buyOrderModel1.getIsFull(), order.getSellAll());
        Assert.assertEquals(buyOrderModel1.getDistributionMethod(), order.getDistributionMethod());
        Assert.assertEquals(buyOrderModel1.getIncomePreference().getIntlId(), order.getIncomePreference());
    }

    @Test
    public void testToOrderDto_whenShareOrder_thenValuesMatch() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderItemDto order = orderGroupService.toOrderDto(buyOrderShare, assetDtos, issuerMap);
        Assert.assertEquals(buyOrderShare.getFundsSource().get(0).getValue(), order.getFundsAllocation().get(0).getAllocation());
        Assert.assertEquals(buyOrderShare.getOrderId(), order.getOrderId());
        Assert.assertEquals(buyOrderShare.getAssetId(), order.getAsset().getAssetId());
        Assert.assertEquals(buyOrderShare.getOrderType(), order.getOrderType());
        Assert.assertEquals(buyOrderShare.getAmount(), order.getAmount());
        Assert.assertEquals(buyOrderShare.getIsFull(), order.getSellAll());
        Assert.assertEquals(buyOrderShare.getUnits(), order.getUnits());
        Assert.assertEquals(buyOrderShare.getPrice(), order.getPrice());
        Assert.assertEquals(buyOrderShare.getExpiry(), order.getExpiry());
        Assert.assertEquals(buyOrderShare.getPriceType().getIntlId(), order.getPriceType());
    }

    @Test
    public void testToOrderDto_whenPercentageFeeSupplied_thenMatchingFeeInModel() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderItemDto order = orderGroupService.toOrderDto(buyOrderModel1, assetDtos, issuerMap);
        Assert.assertEquals(FeesType.PORTFOLIO_MANAGEMENT_FEE, order.getFees().get(0).getFeeType());
        Assert.assertEquals(BigDecimal.valueOf(0.1), order.getFees().get(0).getPercentFee().getRate());
    }

    @Test
    public void testToOrderDto_whenSlidingScaleFeeSupplied_thenMatchingFeeInModel() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderItemDto order = orderGroupService.toOrderDto(buyOrderModel2, assetDtos, issuerMap);
        SlidingScaleFeeDto fee = order.getFees().get(0).getSlidingFee();
        Assert.assertEquals(FeesType.PORTFOLIO_MANAGEMENT_FEE, order.getFees().get(0).getFeeType());
        Assert.assertEquals(2, fee.getTiers().size());
        Assert.assertEquals(slidingFee.getTiers().get(0).getLowerBound(), fee.getTiers().get(0).getLowerBound());
        Assert.assertEquals(slidingFee.getTiers().get(0).getUpperBound(), fee.getTiers().get(0).getUpperBound());
        Assert.assertEquals(slidingFee.getTiers().get(0).getPercent(), fee.getTiers().get(0).getRate());
        Assert.assertEquals(slidingFee.getTiers().get(1).getLowerBound(), fee.getTiers().get(1).getLowerBound());
        Assert.assertEquals(slidingFee.getTiers().get(1).getUpperBound(), fee.getTiers().get(1).getUpperBound());
        Assert.assertEquals(slidingFee.getTiers().get(1).getPercent(), fee.getTiers().get(1).getRate());
    }

    @Test
    public void testToOrderGroup_whenNullKey_thenNullOrderId() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderGroup orderGroup = orderGroupService.toOrderGroup(nullKeyOrderGroupDto);
        Assert.assertNull(orderGroup.getOrderGroupId());
    }

    @Test
    public void testToOrderGroup_whenOrder_thenValuesMatch() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderGroup orderGroup = orderGroupService.toOrderGroup(orderGroupDto);
        Assert.assertEquals(orderGroupDto.getKey().getOrderGroupId(), orderGroup.getOrderGroupId());
        Assert.assertEquals(orderGroupDto.getOwner(), orderGroup.getOwner().getId());
        Assert.assertEquals(orderGroupDto.getLastUpdateDate(), orderGroup.getLastUpdateDate());
        // Assert.assertEquals(orderGroupDto.getWarnings().size(),
        // orderGroup.getWarnings().size());
        Assert.assertEquals(orderGroupDto.getReference(), orderGroup.getReference());
    }

    @Test
    public void testToOrderItem_whenPercentageFeeSupplied_thenMatchingFeeInModel() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderItem order = orderGroupService.toOrderItem(buyOrderDto);
        Assert.assertEquals(FeesComponentType.PERCENTAGE_FEE, order.getFees().get(FeesType.PORTFOLIO_MANAGEMENT_FEE).get(0).getFeesComponentType());
        Assert.assertEquals(BigDecimal.valueOf(0.1),
                ((FlatPercentFeesComponent) order.getFees().get(FeesType.PORTFOLIO_MANAGEMENT_FEE).get(0)).getRate());
    }

    @Test
    public void testToOrderItem_whenSlidingScaleFeeSupplied_thenMatchingFeeInModel() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderItem order = orderGroupService.toOrderItem(buyOrderDto2);
        FeesComponents fee = order.getFees().get(FeesType.PORTFOLIO_MANAGEMENT_FEE).get(0);
        Assert.assertEquals(FeesComponentType.SLIDING_SCALE_FEE, fee.getFeesComponentType());
        List<SlidingScaleTiers> tiers = ((SlidingScaleFeesComponent) fee).getTiers();
        Assert.assertEquals(2, tiers.size());
        Assert.assertEquals(BigDecimal.ZERO, tiers.get(0).getLowerBound());
        Assert.assertEquals(slidingFee.getTiers().get(0).getUpperBound(), tiers.get(0).getUpperBound());
        Assert.assertEquals(slidingFee.getTiers().get(0).getPercent(), tiers.get(0).getPercent());
        Assert.assertEquals(slidingFee.getTiers().get(0).getUpperBound(), tiers.get(1).getLowerBound());
        Assert.assertEquals(slidingFee.getTiers().get(1).getUpperBound(), tiers.get(1).getUpperBound());
        Assert.assertEquals(slidingFee.getTiers().get(1).getPercent(), tiers.get(1).getPercent());
    }

    @Test
    public void testToOrderItem_whenFundsAllocations_thenSizeMatches() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderItem order = orderGroupService.toOrderItem(buyOrderDto);
        Assert.assertEquals(buyOrderDto.getFundsAllocation().size(), order.getFundsSource().size());
    }

    @Test
    public void testToOrderItem_whenOrder_thenValuesMatch() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        OrderItem order = orderGroupService.toOrderItem(buyOrderDto);
        Assert.assertEquals(EncodedString.toPlainText(buyOrderDto.getFundsAllocation().get(0).getAccountId()), order
                .getFundsSource().get(0).getKey());
        Assert.assertEquals(buyOrderDto.getFundsAllocation().get(0).getAllocation(), order.getFundsSource().get(0).getValue());
        Assert.assertEquals(buyOrderDto.getOrderId(), order.getOrderId());
        Assert.assertEquals(buyOrderDto.getAsset().getAssetId(), order.getAssetId());
        Assert.assertEquals(AssetType.forDisplay(buyOrderDto.getAssetType()), order.getAssetType());
        Assert.assertEquals(buyOrderDto.getOrderType(), order.getOrderType());
        Assert.assertEquals(buyOrderDto.getAmount(), order.getAmount());
        Assert.assertEquals(buyOrderDto.getSellAll(), order.getIsFull());
        Assert.assertEquals(buyOrderDto.getDistributionMethod(), order.getDistributionMethod());
        Assert.assertEquals(buyOrderDto.getPriceType(), "");
        Assert.assertEquals(buyOrderDto.getBankClearNumber(), order.getBankClearNumber());
        Assert.assertEquals(buyOrderDto.getPayerAccount(), order.getPayerAccount());
        Assert.assertEquals(IncomePreference.forIntlId(buyOrderDto.getIncomePreference()), order.getIncomePreference());
    }

    @Test
    public void testToOrderItems_whenOrders_thenSizeMatches() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        List<OrderItem> orders = orderGroupService.toOrderItems(buysDto);
        Assert.assertEquals(buysDto.size(), orders.size());
    }

    @Test
    public void testGetAssetsForOrders_whenOrders_thenAssetsInMap() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        Map<String, AssetDto> assets = orderGroupService.getAssetsForOrders(AccountKey.valueOf("accountId"), orderItems,
                new ServiceErrorsImpl());
        for (OrderItem order : orderItems) {
            Assert.assertNotNull(assets.get(order.getAssetId()));
        }
    }

    @Test
    public void testSearch() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        Mockito.when(orderIntegrationService.loadOrderGroups(Mockito.anyString(), any(ServiceErrors.class))).thenReturn(
                orderGroupList);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        ApiSearchCriteria accountId = new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, EncodedString
                .fromPlainText("accountKey").toString(), OperationType.STRING);

        criteriaList.add(accountId);

        List<OrderGroupDto> orderGroupDtos = orderGroupService.search(criteriaList, null);

        Assert.assertNotNull(orderGroupDtos);
        Assert.assertEquals(EncodedString.toPlainText(orderGroupDtos.get(0).getAccountKey().getAccountId()), "accountKey");
    }

    @Test
    public void testFindAll() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        Mockito.when(orderIntegrationService.loadOrderGroups(any(List.class), any(ServiceErrors.class)))
                .thenReturn(orderGroupList);
        Mockito.when(userProfileService.getUserId()).thenReturn("201601388");

        List<OrderGroupDto> orderGroupDtos = orderGroupService.findAll(null);

        Assert.assertNotNull(orderGroupDtos);
        Assert.assertEquals(EncodedString.toPlainText(orderGroupDtos.get(0).getAccountKey().getAccountId()), "accountKey");
    }

    @Test
    public void testToModelDto_preferencesShouldBeSetCorrectly() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        ModelPreferenceAction pref = mock(ModelPreferenceAction.class);
        Mockito.when(pref.getIssuerKey()).thenReturn(AccountKey.valueOf("issuerId"));
        Mockito.when(pref.getAction()).thenReturn(PreferenceAction.SET);
        Mockito.when(pref.getPreference()).thenReturn(Preference.CASH);
        OrderItem item = mock(OrderItem.class);
        Mockito.when(item.getPreferences()).thenReturn(Collections.singletonList(pref));

        Map<String, AssetDto> assets = new HashMap<>();
        OrderItemDto order = orderGroupService.toOrderDto(item, assets, issuerMap);

        Assert.assertNotNull(order);
        Assert.assertEquals(Preference.CASH, order.getPreferences().get(0).getPreference());
        Assert.assertEquals(PreferenceAction.SET, order.getPreferences().get(0).getAction());
    }

    @Test
    public void testFind() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        Mockito.when(
                orderIntegrationService.loadOrderGroup(any(AccountKey.class), any(String.class),
                        any(ServiceErrors.class))).thenReturn(orderGroupList.get(0));

        OrderGroupDto orderGroupDto = orderGroupService.find(new OrderGroupKey(EncodedString.fromPlainText("accountKey")
                .toString(), "1234"), null);

        Assert.assertNotNull(orderGroupDto);
    }

    @Test
    public void testCreate() {

        Mockito.when(orderIntegrationService.saveOrderGroup(any(OrderGroup.class), any(ServiceErrors.class)))
                .thenReturn(orderGroupList.get(0));
        OrderGroupDto orderGroupDtoTest = orderGroupService.create(orderGroupDto, null);
        Assert.assertNotNull(orderGroupDtoTest);
    }

    @Test
    public void testUpdate() {

        Mockito.when(orderIntegrationService.saveOrderGroup(any(OrderGroup.class), any(ServiceErrors.class)))
                .thenReturn(orderGroupList.get(0));

        OrderGroupDto orderGroupDtoTest = orderGroupService.update(orderGroupDto, null);
        Assert.assertNotNull(orderGroupDtoTest);
    }

    @Test
    public void testValidate() throws Exception {
        Mockito.when(orderIntegrationService.validateOrderGroup(any(OrderGroup.class), any(ServiceErrors.class)))
                .thenReturn(orderGroupList.get(0));

        when(httpSession.getAttribute(SAFI_ORDER_SESSION_IDENTIFIER)).thenReturn("SessionVar");

        RuleImpl ruleImp = mock(RuleImpl.class);
        when(ruleImp.getAction()).thenReturn(RuleAction.CHK_UPD);

        when(avaloqRulesIntegrationService.retrieveTwoFaRule(eq(RuleType.LINK_ACC), anyMap(), any(ServiceErrors.class))).thenReturn(ruleImp);
        OrderGroupDto orderGroupDtoTest = orderGroupService.validate(orderGroupDto, null);

        Assert.assertNotNull(orderGroupDtoTest);
        Assert.assertTrue("Expect 2FA required", orderGroupDtoTest.isTwoFaRequired());
    }

    @Test
    public void testValidateTwoFANotRequired() throws Exception {
        Mockito.when(orderIntegrationService.validateOrderGroup(any(OrderGroup.class), any(ServiceErrors.class)))
                .thenReturn(orderGroupList.get(0));

        when(httpSession.getAttribute(SAFI_ORDER_SESSION_IDENTIFIER)).thenReturn("SessionVar");

        RuleImpl ruleImp = mock(RuleImpl.class);
        when(ruleImp.getAction()).thenReturn(RuleAction.CHK_UPD);

        //when(avaloqRulesIntegrationService.retrieveTwoFaRule(eq(RuleType.LINK_ACC), anyMap(), any(ServiceErrors.class))).thenReturn(ruleImp);
        OrderGroupDto orderGroupDtoTest = orderGroupService.validate(orderGroupDto, null);

        Assert.assertNotNull(orderGroupDtoTest);
        Assert.assertFalse("Expect 2FA not required", orderGroupDtoTest.isTwoFaRequired());
    }

    @Test
    public void testSubmit() {
        Mockito.when(httpSession.getAttribute(SAFI_ORDER_SESSION_IDENTIFIER)).thenReturn(twoFactorRuleModelAuthDone);
        Mockito.when(orderIntegrationService.submitOrderGroup(any(OrderGroup.class), any(ServiceErrors.class)))
                .thenReturn(orderGroupList.get(0));
        OrderGroupDto orderGroupDtoTest = orderGroupService.submit(orderGroupDto, null);
        Assert.assertNotNull(orderGroupDtoTest);
    }

    @Test
    public void testSubmitTwoFAFailed () {
        Mockito.when(httpSession.getAttribute(SAFI_ORDER_SESSION_IDENTIFIER)).thenReturn(twoFactorRuleModelAuthNotDone);
        Mockito.when(orderIntegrationService.submitOrderGroup(any(OrderGroup.class), any(ServiceErrors.class)))
                .thenReturn(orderGroupList.get(0));
        OrderGroupDto orderGroupDtoTest = orderGroupService.submit(orderGroupDto, null);
    }

    @Test
    public void testDelete() {
        Mockito.doNothing().when(orderIntegrationService).deleteOrderGroup(Mockito.anyString(), any(ServiceErrors.class));
        orderGroupService.delete(new OrderGroupKey("accountKey", "1234"), null);
    }

    @Test
    public void testToOrderDto_valuesMatchwithTD() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);

        for (OrderGroup orderGroup : orderGroupList) {
            WrapAccountImpl accDetail = new WrapAccountImpl();
            accDetail.setAccountName("Wrap Account Name 1");

            Mockito.when(
                    accountIntegrationService.loadWrapAccountWithoutContainers(any(AccountKey.class), any(ServiceErrors.class)))
                    .thenReturn(accDetail);

            OrderGroupDto group = orderGroupService.toOrderGroupDto(orderGroup, new ServiceErrorsImpl());
            assertNotNull(group);

            Assert.assertEquals(orderGroup.getOrderGroupId(), group.getKey().getOrderGroupId());
            Assert.assertEquals(orderGroup.getReference(), group.getReference());
            Assert.assertEquals(orderGroup.getLastUpdateDate(), group.getLastUpdateDate());
            Assert.assertEquals(orderGroup.getOwner().getId(), EncodedString.toPlainText(group.getOwner()));
            Assert.assertEquals(orderGroup.getWarnings().size(), group.getWarnings().size());
        }
        verify(assetIntegrationService, times(1)).loadTermDepositRates(any(TermDepositAssetRateSearchKey.class), any(ServiceErrors.class));
    }

    @Test
    public void testGetAssetsForOrders_whenOrders_thenTDAssetsInMap() {
        Map<String, AssetDto> assets = orderGroupService.getAssetsForOrders(AccountKey.valueOf("accountId"), listTDOrders,
                new ServiceErrorsImpl());
        for (OrderItem order : listTDOrders) {
            Assert.assertNotNull(assets.get(order.getAssetId()));
        }
    }
}
