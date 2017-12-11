package com.bt.nextgen.api.order.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.asset.service.AssetDtoConverterV2;
import com.bt.nextgen.api.order.model.FundsAllocationDto;
import com.bt.nextgen.api.order.model.OrderFeeDto;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.model.OrderItemSummaryDto;
import com.bt.nextgen.api.order.model.PreferenceActionDto;
import com.bt.nextgen.api.order.model.SlidingScaleTierDto;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.payments.web.model.AccountVerificationStatus;
import com.bt.nextgen.payments.web.model.TwoFactorAccountVerificationKey;
import com.bt.nextgen.payments.web.model.TwoFactorRuleModel;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.FeesComponentType;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.FlatPercentFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.bt.nextgen.service.avaloq.order.ModelPreferenceActionImpl;
import com.bt.nextgen.service.avaloq.order.OrderGroupImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemSummaryImpl;
import com.bt.nextgen.service.avaloq.rules.AvaloqRulesIntegrationService;
import com.bt.nextgen.service.avaloq.rules.RuleAction;
import com.bt.nextgen.service.avaloq.rules.RuleCond;
import com.bt.nextgen.service.avaloq.rules.RuleImpl;
import com.bt.nextgen.service.avaloq.rules.RuleType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.account.IssuerAccount;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.order.ModelPreferenceAction;
import com.bt.nextgen.service.integration.order.OrderGroup;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.order.PriceType;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;

@Service("OrderGroupDtoServiceV0.1")
@Transactional(value = "springJpaTransactionManager")
@SuppressWarnings("squid:S1200")
public class OrderGroupDtoServiceImpl implements OrderGroupDtoService {

    private static final Logger logger = LoggerFactory.getLogger(OrderGroupDtoServiceImpl.class);
    private static final String SAFI_ORDER_SESSION_IDENTIFIER = "order-capture-safi";

    @Autowired
    @Qualifier("avaloqOrderIntegrationService")
    private OrderIntegrationService orderService;

    @Autowired
    private OrderGroupDtoErrorMapper orderDtoErrorMapper;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    @Autowired
    private AssetDtoConverterV2 assetDtoConverterV2;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private AvaloqRulesIntegrationService avaloqRulesIntegrationService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private FeatureTogglesService featureTogglesService;


    @Override
    public List<OrderGroupDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        List<OrderGroupDto> groups = new ArrayList<>();
        List<OrderGroup> orders = null;

        for (ApiSearchCriteria parameter : criteriaList) {
            if (Attribute.ACCOUNT_ID.equals(parameter.getProperty())) {
                orders = orderService.loadOrderGroups(EncodedString.toPlainText(parameter.getValue()), serviceErrors);
            } else {
                throw new IllegalArgumentException("Unsupported search");
            }
        }
        // convert to DTO
        for (OrderGroup order : orders) {
            groups.add(toOrderGroupDto(order, serviceErrors));
        }
        return groups;
    }

    @Override
    public List<OrderGroupDto> findAll(ServiceErrors serviceErrors) {
        List<OrderGroupDto> groups = new ArrayList<>();
        List<Broker> brokers = brokerService.getBrokersForJob(userProfileService.getActiveProfile(), serviceErrors);
        List<BrokerKey> brokerKeys = new ArrayList<>();
        for (Broker broker : brokers) {
            brokerKeys.add(broker.getKey());
        }
        List<OrderGroup> orders = orderService.loadOrderGroups(brokerKeys, serviceErrors);
        for (OrderGroup order : orders) {
            groups.add(toOrderGroupDto(order, serviceErrors));
        }

        return groups;
    }

    @Override
    public OrderGroupDto find(OrderGroupKey key, ServiceErrors serviceErrors) {
        OrderGroup order = orderService.loadOrderGroup(AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId())),
                key.getOrderGroupId(), serviceErrors);
        return toOrderGroupDto(order, serviceErrors);
    }

    @Override
    public OrderGroupDto create(OrderGroupDto dto, ServiceErrors serviceErrors) {
        OrderGroup order = orderService.saveOrderGroup(toOrderGroup(dto), serviceErrors);
        return toOrderGroupDto(order, serviceErrors);
    }

    @Override
    public OrderGroupDto update(OrderGroupDto dto, ServiceErrors serviceErrors) {
        OrderGroup order = orderService.saveOrderGroup(toOrderGroup(dto), serviceErrors);
        return toOrderGroupDto(order, serviceErrors);
    }

    @Override
    public OrderGroupDto validate(OrderGroupDto orderGroupDto, ServiceErrors serviceErrors) {
        if (orderGroupDto.getOwner() == null) {
            orderGroupDto.setOwner(userProfileService.getAvaloqId());
        }
        OrderGroup orderGroupResponse = orderService.validateOrderGroup(toOrderGroup(orderGroupDto), serviceErrors);
        OrderGroupDto validateOrderGroupDto = toOrderGroupDto(orderGroupResponse, serviceErrors);
        if (null != orderGroupDto.getOrders().get(0).getPayerAccount() && null != orderGroupDto.getOrders().get(0).getBankClearNumber())
            setTwoFactorAuthDetails(orderGroupDto, validateOrderGroupDto);
        else
            logger.info("Linked account number: {} or BSB: {} or both are null", orderGroupDto.getOrders().get(0).getPayerAccount(), orderGroupDto.getOrders().get(0).getBankClearNumber());

        return validateOrderGroupDto;
    }

    @Override
    public OrderGroupDto submit(OrderGroupDto dto, ServiceErrors serviceErrors) {
        OrderGroup order = new OrderGroupImpl();
        boolean safiAuthResult = true;
        TwoFactorRuleModel ruleModel = (TwoFactorRuleModel) httpSession.getAttribute(SAFI_ORDER_SESSION_IDENTIFIER);

        if (ruleModel != null) {
            TwoFactorAccountVerificationKey accountVerificationKey =
                    new TwoFactorAccountVerificationKey(dto.getOrders().get(0).getPayerAccount(), dto.getOrders().get(0).getBankClearNumber());
            logger.info("Found AccountStatusMap: {}", ruleModel.getAccountStatusMap());
            logger.info("Found AccountStatusMap for linked account: {}", ruleModel.getAccountStatusMap().get(accountVerificationKey));
            logger.info("Found AuthStatus: {}", ruleModel.getAccountStatusMap().get(accountVerificationKey).isAuthenticationDone());
            safiAuthResult = ruleModel.getAccountStatusMap().get(accountVerificationKey).isAuthenticationDone();
        }
        if (safiAuthResult)
            order = orderService.submitOrderGroup(toOrderGroup(dto), serviceErrors);
        else {
            OrderGroupDto orderGroupErrorDto = new OrderGroupDto();
            List<DomainApiErrorDto> errorList = new ArrayList<>();
            DomainApiErrorDto error = new DomainApiErrorDto("Err.IP-0315", null, "Payee is not a verified linked account",
                    cmsService.getContent("Err.IP-0315"), DomainApiErrorDto.ErrorType.ERROR);
            //TODO: Add SAFI Authentication specific Error
            errorList.add(error);
            orderGroupErrorDto.setWarnings(errorList);
            return orderGroupErrorDto;
        }
        return toOrderGroupDto(order, serviceErrors);
    }

    @Override
    public void delete(OrderGroupKey key, ServiceErrors serviceErrors) {
        orderService.deleteOrderGroup(key.getOrderGroupId(), serviceErrors);
    }

    private void setTwoFactorAuthDetails(OrderGroupDto requestingOrderGroupDto, OrderGroupDto orderGroupDto) {
        TwoFactorRuleModel ruleModel = new TwoFactorRuleModel();
        RuleImpl rule = new RuleImpl();
        if (null != httpSession.getAttribute(SAFI_ORDER_SESSION_IDENTIFIER)) {
            logger.info("Already found var {} in session, remove it", SAFI_ORDER_SESSION_IDENTIFIER);
            httpSession.removeAttribute(SAFI_ORDER_SESSION_IDENTIFIER);
        }

        final TwoFactorAccountVerificationKey accountVerificationKey =
                new TwoFactorAccountVerificationKey(requestingOrderGroupDto.getOrders().get(0).getPayerAccount(), requestingOrderGroupDto.getOrders().get(0).getBankClearNumber());

        rule = isTwoFAReq(requestingOrderGroupDto);
        if (rule != null && (rule.getAction() == RuleAction.CHK || rule.getAction() == RuleAction.CHK_UPD)) {
            logger.info("Found avaloq rule with id:{}, type:{}, action:{}", rule.getRuleId(), rule.getType(), rule.getAction());
            orderGroupDto.setTwoFaRequired(true);
            ruleModel.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus(rule.getRuleId(), false));
        } else {
            orderGroupDto.setTwoFaRequired(false);
            ruleModel.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus(rule != null ? rule.getRuleId() : null, true));
        }

        httpSession.setAttribute(SAFI_ORDER_SESSION_IDENTIFIER, ruleModel);
        logger.info("Session variable: {}, value: {}", SAFI_ORDER_SESSION_IDENTIFIER,
                ruleModel.getAccountStatusMap().get(accountVerificationKey));
    }

    private RuleImpl isTwoFAReq(OrderGroupDto requestingOrderGroupDto) {
        Map<RuleCond, String> ruleCondStringMap = new HashMap<>();
        ruleCondStringMap.put(RuleCond.LINK_ACC_NR, requestingOrderGroupDto.getOrders().get(0).getPayerAccount());
        ruleCondStringMap.put(RuleCond.LINK_BSB, requestingOrderGroupDto.getOrders().get(0).getBankClearNumber());
        ruleCondStringMap.put(RuleCond.BP_ID, EncodedString.toPlainText(requestingOrderGroupDto.getAccountKey().getAccountId()));
        return avaloqRulesIntegrationService.retrieveTwoFaRule(RuleType.LINK_ACC, ruleCondStringMap, new FailFastErrorsImpl());
    }

    protected OrderGroupDto toOrderGroupDto(OrderGroup orderGroup, ServiceErrors serviceErrors) {
        String accountName = null;

        if (orderGroup.getAccountKey() != null) {
            WrapAccount account = accountIntegrationService.loadWrapAccountWithoutContainers(orderGroup.getAccountKey(),
                    serviceErrors);
            accountName = account != null ? account.getAccountName() : "";
        }

        String adviserFullName = orderGroup.getOwnerName();
        if (adviserFullName == null && orderGroup.getOwner() != null) {
            BrokerUser brokerUser = brokerService.getBrokerUser(UserKey.valueOf(orderGroup.getOwner().getId()), serviceErrors);
            adviserFullName = brokerUser != null ? brokerUser.getFirstName() + " " + brokerUser.getLastName() : "";
        }

        AccountKey accountKey = orderGroup.getAccountKey();

        return new OrderGroupDto(
                orderGroup.getOrderGroupId() == null ? null
                        : new OrderGroupKey(EncodedString.fromPlainText(accountKey.getId()).toString(),
                                orderGroup.getOrderGroupId()),
                orderGroup.getLastUpdateDate(), orderGroup.getTransactionSeq(),
                toOrdersDto(accountKey, orderGroup.getOrders(), serviceErrors), orderDtoErrorMapper.map(orderGroup.getWarnings()),
                orderGroup.getOwner() == null ? null : EncodedString.fromPlainText(orderGroup.getOwner().getId()).toString(),
                adviserFullName, orderGroup.getReference(), accountName,
                new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText(accountKey.getId()).toString()));
    }

    protected List<OrderItemDto> toOrdersDto(AccountKey accountKey, List<OrderItem> orders, ServiceErrors serviceErrors) {
        if (orders == null) {
            return null;
        }

        Map<String, AssetDto> assets = getAssetsForOrders(accountKey, orders, serviceErrors);
        Map<AccountKey, IssuerAccount> issuers = getIssuersForOrders(orders, serviceErrors);

        List<OrderItemDto> orderDtos = new ArrayList<>();
        for (OrderItem order : orders) {
            orderDtos.add(toOrderDto(order, assets, issuers));
        }

        return orderDtos;
    }

    protected OrderItemDto toOrderDto(OrderItem order, Map<String, AssetDto> assets, Map<AccountKey, IssuerAccount> issuers) {
        List<Pair<String, BigDecimal>> allocations = order.getFundsSource();
        List<FundsAllocationDto> allocationDtos = new ArrayList<>();

        for (Pair<String, BigDecimal> allocation : allocations) {
            FundsAllocationDto fundsAllocation = new FundsAllocationDto();
            fundsAllocation.setAccountId(EncodedString.fromPlainText(allocation.getKey()).toString());
            fundsAllocation.setAllocation(allocation.getValue());
            allocationDtos.add(fundsAllocation);
        }

        OrderItemSummaryDto summaryDto = new OrderItemSummaryDto(order.getAmount(), order.getIsFull(),
                order.getDistributionMethod(), order.getUnits(), order.getPrice(), order.getExpiry(), order.getPriceType());

        OrderItemDto item = new OrderItemDto(order.getOrderId(), assets.get(order.getAssetId()), null, order.getOrderType(),
                summaryDto, allocationDtos);

        item.setPreferences(getPreferencesFromOrderItem(order, issuers));
        item.setFees(getOrderFeesDto(order.getFees()));
        item.setBankClearNumber(order.getBankClearNumber());
        item.setPayerAccount(order.getPayerAccount());
        item.setIncomePreference(order.getIncomePreference() == null ? null : order.getIncomePreference().getIntlId());

        return item;
    }

    private List<OrderFeeDto> getOrderFeesDto(Map<FeesType, List<FeesComponents>> fees) {
        //only supporting a single portfolio fee for now.
        List<OrderFeeDto> feesDtos = new ArrayList<>();
        List<FeesComponents> portfolioFee = fees.get(FeesType.PORTFOLIO_MANAGEMENT_FEE);
        if (portfolioFee != null && !portfolioFee.isEmpty()) {
            FeesComponents component = portfolioFee.get(0);
            if (component.getFeesComponentType() == FeesComponentType.PERCENTAGE_FEE) {
                feesDtos.add(new OrderFeeDto(FeesType.PORTFOLIO_MANAGEMENT_FEE, (FlatPercentFeesComponent) component));
            } else if (component.getFeesComponentType() == FeesComponentType.SLIDING_SCALE_FEE) {
                feesDtos.add(new OrderFeeDto(FeesType.PORTFOLIO_MANAGEMENT_FEE, (SlidingScaleFeesComponent) component));
            }
        }

        return feesDtos;
    }

    private List<PreferenceActionDto> getPreferencesFromOrderItem(OrderItem order, Map<AccountKey, IssuerAccount> issuers) {
        List<PreferenceActionDto> prefs = new ArrayList<>();
        for (ModelPreferenceAction preference : order.getPreferences()) {
            IssuerAccount issuer = issuers.get(preference.getIssuerKey());
            prefs.add(new PreferenceActionDto(preference.getIssuerKey().getId(),
 issuer == null ? null : issuer.getAccountName(),
                    preference.getPreference(), preference.getAction()));
        }
        return prefs;
    }


    protected OrderGroup toOrderGroup(OrderGroupDto orderGroupDto) {
        UserInformation userInfo = userProfileService.getActiveProfile();
        ClientKey userClient = userInfo.getClientKey();

        AccountKey wrapAccountKey = AccountKey.valueOf(EncodedString.toPlainText(orderGroupDto.getAccountKey().getAccountId()));

        return new OrderGroupImpl(wrapAccountKey, orderGroupDto.getKey() == null ? null : orderGroupDto.getKey()
                .getOrderGroupId(), userClient, orderGroupDto.getLastUpdateDate(), orderGroupDto.getTransactionSeq(),
                toOrderItems(orderGroupDto.getOrders()), orderDtoErrorMapper.mapWarnings(orderGroupDto.getWarnings()),
                orderGroupDto.getReference());
    }

    protected OrderItem toOrderItem(OrderItemDto order) {
        List<Pair<String, BigDecimal>> allocations = new ArrayList<>();
        for (FundsAllocationDto allocationDto : order.getFundsAllocation()) {
            Pair<String, BigDecimal> allocation = new ImmutablePair<>(EncodedString.toPlainText(allocationDto.getAccountId()),
                    allocationDto.getAllocation());
            allocations.add(allocation);
        }

        SubAccountKey subAccountKey = order.getSubAccountId() == null ? null
                : SubAccountKey.valueOf(EncodedString.toPlainText(order.getSubAccountId()));

        OrderItemSummaryImpl summary = new OrderItemSummaryImpl(order.getAmount(), order.getSellAll(),
                order.getDistributionMethod(), order.getUnits(), order.getPrice(), order.getExpiry(), PriceType.forIntlId(order
                .getPriceType()));

        OrderItemImpl orderItem = new OrderItemImpl(subAccountKey, order.getOrderId(), order.getOrderType(),
                AssetType.forDisplay(order.getAssetType()), order.getAsset().getAssetId(), summary, allocations);

        List<ModelPreferenceAction> prefs = new ArrayList<>();
        for (PreferenceActionDto preference : order.getPreferences()) {
            prefs.add(new ModelPreferenceActionImpl(AccountKey.valueOf(preference.getIssuerId()), preference.getPreference(),
                    preference.getAction()));
        }
        orderItem.setPreferences(prefs);
        orderItem.setFees(toOrderFees(order.getFees()));
        orderItem.setBankClearNumber(order.getBankClearNumber());
        orderItem.setPayerAccount(order.getPayerAccount());
        orderItem.setIncomePreference(IncomePreference.forIntlId(order.getIncomePreference()));
        return orderItem;
    }

    private Map<FeesType, List<FeesComponents>> toOrderFees(List<OrderFeeDto> dtos) {
        // only support a single portfolio fee for now
        Map<FeesType, List<FeesComponents>> result = new HashMap<>();
        if (dtos != null) {
            for (OrderFeeDto dto : dtos) {
                if (dto.getFeeType() == FeesType.PORTFOLIO_MANAGEMENT_FEE) {
                    List<FeesComponents> portfolioFees = new ArrayList<>();
                    if (dto.getStructure() == FeesComponentType.PERCENTAGE_FEE && dto.getPercentFee() != null) {
                        portfolioFees.add(new FlatPercentFeesComponent(dto.getPercentFee().getRate()));
                    } else if (dto.getStructure() == FeesComponentType.SLIDING_SCALE_FEE && dto.getSlidingFee() != null) {
                        List<SlidingScaleTiers> tiers = new ArrayList<>();
                        BigDecimal lowerBound = BigDecimal.ZERO;
                        for (SlidingScaleTierDto tier : dto.getSlidingFee().getTiers()) {
                            tiers.add(new SlidingScaleTiers(lowerBound, tier.getUpperBound(), tier.getRate()));
                            lowerBound = tier.getUpperBound();
                        }
                        portfolioFees.add(new SlidingScaleFeesComponent(tiers));
                    }
                    if (!portfolioFees.isEmpty()) {
                        result.put(FeesType.PORTFOLIO_MANAGEMENT_FEE, portfolioFees);
                    }
                }

            }
        }
        return result;
    }

    protected List<OrderItem> toOrderItems(List<OrderItemDto> orderDtos) {
        List<OrderItem> orders = new ArrayList<>();
        for (OrderItemDto orderDto : orderDtos) {
            orders.add(toOrderItem(orderDto));
        }
        return orders;
    }

    protected Map<String, AssetDto> getAssetsForOrders(AccountKey accountKey, List<OrderItem> orders, ServiceErrors serviceErrors) {
        List<String> assetIds = new ArrayList<>();
        boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("termDepositToggle");
        for (OrderItem order : orders) {
            assetIds.add(order.getAssetId());
        }

        WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        BrokerKey adviserKey = account.getAdviserKey();
        Broker broker = brokerService.getBroker(adviserKey, serviceErrors);

        Map<String, Asset> assets = assetService.loadAssets(assetIds, serviceErrors);
        List<Asset> assetList = new ArrayList<>(assets.values());
        List<String> assetIdList = Lambda.collect(assetList,on(Asset.class).getAssetId());

        if(termDepositToggle){
            TermDepositAssetRateSearchKey termDepositAssetRateSearchKey = new TermDepositAssetRateSearchKey(account.getProductKey(),broker.getDealerKey(),null,account.getAccountStructureType(),DateTime.now(),assetIdList);
            List<TermDepositInterestRate> termDepositInterestRates = assetService.loadTermDepositRates(termDepositAssetRateSearchKey, serviceErrors);
            return assetDtoConverterV2.toAssetDto(assets, termDepositInterestRates);
        }

        Map<String, TermDepositAssetDetail> termDepositAssetDetails = assetService.loadTermDepositRates(broker.getDealerKey(),
                DateTime.now(), assetList, serviceErrors);

        return assetDtoConverter.toAssetDto(assets, termDepositAssetDetails);

    }

    protected Map<AccountKey, IssuerAccount> getIssuersForOrders(List<OrderItem> orders, ServiceErrors serviceErrors) {
        HashSet<AccountKey> issuers = new HashSet<>();

        for (OrderItem item : orders) {
            issuers.addAll(Lambda.extract(item.getPreferences(), on(ModelPreferenceAction.class).getIssuerKey()));
        }
        if (!issuers.isEmpty()) {
            List<IssuerAccount> accounts = accountIntegrationService.loadIssuerAccount(issuers, serviceErrors);
            return Lambda.index(accounts, on(IssuerAccount.class).getAccountKey());
        }
        return Collections.emptyMap();
    }

}
