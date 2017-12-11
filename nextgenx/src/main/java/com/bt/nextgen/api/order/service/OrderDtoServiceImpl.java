package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.order.model.GeneralOrderDto;
import com.bt.nextgen.api.order.model.OrderDto;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.ShareOrderDto;
import com.bt.nextgen.api.order.util.OrderUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.util.SearchResultsUtil;
import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.order.OrderImpl;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderDetail;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.order.PriceType;
import com.btfin.panorama.service.integration.account.AccountSecurityIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("OrderDtoServiceV0.1")
@Transactional(value = "springJpaTransactionManager")
@SuppressWarnings("squid:S1200")
public class OrderDtoServiceImpl implements OrderDtoService {
    private static final Logger logger = LoggerFactory.getLogger(OrderDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqOrderIntegrationService")
    private OrderIntegrationService orderService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountSecurityIntegrationService accountService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private OrderSearchMapper orderSearchMapper;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    @Autowired
    private UserProfileService userProfileService;

    private static final String PRICE = "price";

    @Override
    public OrderDto find(OrderKey key, ServiceErrors serviceErrors) {
        List<Order> orders;
        orders = orderService.loadOrder(key.getOrderId(), serviceErrors);
        if (CollectionUtils.isNotEmpty(orders)) {
            Order order = orders.get(0);
            List<Order> newOrders = new ArrayList<>();
            newOrders.add(order);
            List<OrderDto> orderDTOList = toOrderDtos(newOrders, serviceErrors);
            if (orderDTOList != null && !orderDTOList.isEmpty()) {
                return orderDTOList.get(0);
            }
        }

        return null;
    }

    @Override
    public OrderDto update(OrderDto orderDto, ServiceErrors serviceErrors) {
        if (orderDto instanceof ShareOrderDto
                || AssetType.MANAGED_FUND.getDisplayName().equals(orderDto.getAsset().getAssetType())) {
            if (orderDto.getCancellable()) {
                orderService.updateStexOrder(toOrder(orderDto), serviceErrors);
            } else {
                orderService.cancelOrder(new BigInteger(orderDto.getKey().getOrderId().toString()), new BigInteger(orderDto
                        .getLastTranSeqId().toString()), serviceErrors);
            }
        } else {
            if (OrderStatus.CANCELLED.getDisplayName().equals(orderDto.getStatus())) {
                orderService.cancelOrder(new BigInteger(orderDto.getKey().getOrderId().toString()), new BigInteger(orderDto
                        .getLastTranSeqId().toString()), serviceErrors);
            } else {
                throw new IllegalArgumentException("invalid status " + orderDto.getStatus());
            }
        }
        return find(orderDto.getKey(), serviceErrors);
    }

    private Order toOrder(OrderDto orderDto) {
        OrderImpl order = new OrderImpl();
        order.setOrderId(orderDto.getKey().getOrderId());
        order.setStatus(OrderStatus.forDisplayName(orderDto.getStatus()));
        order.setLastTranSeqId(orderDto.getLastTranSeqId());

        if (orderDto instanceof ShareOrderDto) {
            ShareOrderDto shareOrder = (ShareOrderDto) orderDto;
            order.setExpiryType(ExpiryMethod.forName(shareOrder.getExpiryType()));
            order.setPriceType(PriceType.forDisplayName(shareOrder.getPriceType()));
            order.setLimitPrice(shareOrder.getLimitPrice());
            order.setOriginalQuantity(shareOrder.getQuantity());
        }

        return order;
    }

    @Override
    public List<OrderDto> findAll(ServiceErrors serviceErrors) {
        List<ApiSearchCriteria> criteriaList = Collections.emptyList();
        SearchParameters search = SearchResultsUtil.buildSearchQueryFor(criteriaList, orderSearchMapper);
        List<Order> orders;
        orders = orderService.loadOrders(search, serviceErrors);
        return toOrderDtos(orders, serviceErrors);
    }

    @Override
    public List<OrderDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {

        List<ApiSearchCriteria> modifiedCriteriaList = manipulateSearchCriteria(criteriaList);
        SearchParameters search = SearchResultsUtil.buildSearchQueryFor(modifiedCriteriaList, orderSearchMapper);
        List<Order> orders;
        orders = orderService.loadOrders(search, serviceErrors);
        return toOrderDtos(orders, serviceErrors);
    }

    @Override
    public List<OrderDto> search(OrderKey key, ServiceErrors serviceErrors) {
        List<Order> orders;
        orders = orderService.searchOrders(key.getOrderId(), serviceErrors);
        return toOrderDtos(orders, serviceErrors);
    }

    private List<ApiSearchCriteria> manipulateSearchCriteria(List<ApiSearchCriteria> criteriaList) {
        // Workaround for QC9109.
        List<ApiSearchCriteria> modifiedCriteriaList = new ArrayList<>(criteriaList);
        for (ApiSearchCriteria criteria : criteriaList) {
            if (criteria.getProperty().equals(Attribute.LAST_UPDATE_DATE)
                    && SearchOperation.NEG_GREATER_THAN == criteria.getOperation()) {
                // if max date is today set one day in the future to accommodate future dated term deposits (happens if order submission after 5pm).
                // Will still be incorrect for historical searches and business have accepted that pending an R2 fix.
                DateTime endDate = new DateTime(criteria.getValue());
                DateTime today = new DateTime().withTimeAtStartOfDay();
                if (endDate.equals(today)) {
                    modifiedCriteriaList.remove(criteria);
                    modifiedCriteriaList.add(new ApiSearchCriteria(criteria.getProperty(), criteria.getOperation(), today
                            .plusDays(1).toString(), criteria.getOperationType()));
                }
            }
        }
        return modifiedCriteriaList;
    }

    protected OrderDto toOrderDto(Order order, WrapAccount account, Map<String, AssetDto> assetDtoMap) {
        AssetDto assetDto = assetDtoMap.get(order.getAssetId());

        GeneralOrderDto orderDto = new GeneralOrderDto();
        if (assetDto != null && AssetType.SHARE == AssetType.forDisplay(assetDto.getAssetType())) {
            orderDto = new ShareOrderDto();
            setShareOrderDetails((ShareOrderDto) orderDto, order);
        }

        orderDto.setExternal(Boolean.valueOf(order.getOrigin().isExternal()));
        setOrderDetails(orderDto, order);
        orderDto.setOrderType(OrderUtil.getOrderType(order.getOrderType(), assetDto));
        orderDto.setAmendable(order.getCancellable() && assetDto instanceof ShareAssetDto);
        orderDto.setBrokerage(order.getBrokerage() != null ? order.getBrokerage().setScale(2, RoundingMode.HALF_UP) : null);
        setPrice(orderDto, order);
        orderDto.setAsset(assetDto);
        setStatus(orderDto, order, assetDto);
        setAmount(orderDto, order);

        if (account != null) {
            orderDto.setAccountNumber(account.getAccountNumber());
            orderDto.setAccountName(account.getAccountName());
            orderDto.setAccountKey(EncodedString.fromPlainText(account.getAccountKey().getId()).toString());
        }

        return orderDto;
    }

    protected void setOrderDetails(GeneralOrderDto orderDto, Order order) {
        orderDto.setKey(new OrderKey(order.getOrderId()));
        orderDto.setDisplayOrderId(order.getDisplayOrderId());
        orderDto.setSubmitDate(order.getOrigin().isExternal()
                ? new DateTime(order.getTradeDate()).withTimeAtStartOfDay() : order.getCreateDate());
        orderDto.setOrigin(order.getOrigin() == null ? null : order.getOrigin().getName());
        orderDto.setCancellable(order.getCancellable());
        orderDto.setLastTranSeqId(order.getLastTranSeqId());
        orderDto.setQuantity(order.getOriginalQuantity() == null ? null : order.getOriginalQuantity().abs());
        orderDto.setContractNotes(Boolean.TRUE.equals(order.getContractNotes()));
    }

    private void setShareOrderDetails(ShareOrderDto shareOrderDto, Order order) {
        if (order.getOrigin() == Origin.IPO) {
            shareOrderDto.setFilledQuantity(
                    Math.abs(order.getOriginalQuantity() == null || order.getStatus() == OrderStatus.IN_PROGRESS ? 0
                            : order.getOriginalQuantity().intValue()));
        } else {
            shareOrderDto.setFilledQuantity(Math.abs(order.getFilledQuantity() == null ? 0 : order.getFilledQuantity()));
        }
        shareOrderDto.setExpiryType(order.getExpiryType() == null ? null : order.getExpiryType().name());
        shareOrderDto.setPriceType(order.getPriceType() == null ? null : order.getPriceType().getDisplayName());
        shareOrderDto.setLimitPrice(order.getLimitPrice());
        shareOrderDto.setRejectionReason(order.getRejectionReason());
        shareOrderDto.setCancellationCount(order.getCancellationCount());
        shareOrderDto.setMaxCancellationCount(order.getMaxCancellationCount());

        if (Origin.PANEL_BROKER.equals(order.getOrigin())) {
            shareOrderDto.setBrokerName(order.getBrokerName());
            shareOrderDto.setExternalOrderId(order.getExternalOrderId());
        }
    }

    protected void setPrice(GeneralOrderDto orderDto, Order order) {
        if (order.getOrigin() == Origin.IPO) {
            orderDto.setPrice(order.getEstimatedPrice() == null ? BigDecimal.ZERO : order.getEstimatedPrice());
        } else {
            if (CollectionUtils.isNotEmpty(order.getDetails())) {
                for (OrderDetail detail : order.getDetails()) {
                    if (detail.getValue() != null && PRICE.equals(detail.getKey())) {
                        orderDto.setPrice(detail.getValue().abs());
                    }
                }
            }
        }
    }

    @SuppressWarnings("squid:S1067")
    protected void setStatus(GeneralOrderDto orderDto, Order order, AssetDto assetDto) {
        // show completed orders as in progress if they're still cancellable for managed portfolio orders
        if (assetDto != null
                && (AssetType.MANAGED_PORTFOLIO.equals(AssetType.forDisplay(assetDto.getAssetType()))
                || AssetType.MANAGED_FUND.equals(AssetType.forDisplay(assetDto.getAssetType())))
                && OrderStatus.COMPLETED.equals(order.getStatus()) && Boolean.TRUE.equals(order.getCancellable())) {
            orderDto.setStatus(OrderStatus.IN_PROGRESS.getDisplayName());
        } else {
            orderDto.setStatus(order.getStatus() == null ? null : order.getStatus().getDisplayName());
        }
    }

    protected void setAmount(GeneralOrderDto orderDto, Order order) {
        if (Origin.IPO.equals(order.getOrigin())) {
            orderDto.setAmount(order.getNetAmount().abs());
        } else if (((!OrderType.FULL_REDEMPTION.equals(order.getOrderType())
                && !OrderType.FULL_REDEMPTION_F.equals(order.getOrderType()))
                || OrderStatus.COMPLETED.getDisplayName().equals(orderDto.getStatus())) && order.getAmount() != null) {
            // hide amount for full redemption orders if the status is not completed
            orderDto.setAmount(order.getOrderType().isBuy() ? order.getAmount().abs() : order.getAmount());
        }
    }

    protected List<OrderDto> toOrderDtos(List<Order> orders, ServiceErrors serviceErrors) {
        List<OrderDto> orderDtos = new ArrayList<>();

        if (!CollectionUtils.isEmpty(orders)) {
            Map<AccountKey, WrapAccount> accountMap = accountService.loadWrapAccountWithoutContainers(serviceErrors);
            Map<String, AssetDto> assetMap = getAssetMapForOrders(orders, serviceErrors);
            for (Order order : orders) {
                orderDtos.add(toOrderDto(order, accountMap.get(AccountKey.valueOf(order.getAccountId())), assetMap));
            }

            // Sort in descending order of submit-date.
            Collections.sort(orderDtos, new Comparator<OrderDto>() {
                @Override
                public int compare(OrderDto o1, OrderDto o2) {
                    return o2.getSubmitDate().compareTo(o1.getSubmitDate());
                }
            });
        }

        return orderDtos;
    }

    private Map<String, AssetDto> getAssetMapForOrders(List<Order> orders, ServiceErrors serviceErrors) {
        logger.debug("Entering into getAssetMapForOrders:");
        Set<String> assetIds = new HashSet<>();
        Set<String> accountIds = new HashSet<>();
        for (Order order : orders) {
            if (order.getAssetId() != null)
                assetIds.add(order.getAssetId());
            if (order.getAccountId() != null)
                accountIds.add(order.getAccountId());

        }
        logger.debug("Asset Ids size:{}", assetIds.size());
        Map<String, Asset> assets = assetService.loadAssets(assetIds, serviceErrors);
        List<Asset> assetList = new ArrayList<>(assets.values());
        // warn if assets not found

        if (assets.size() != assetIds.size()) {
            for (String assetId : assetIds) {
                if (assets.get(assetId) == null) {
                    logger.warn("Asset id {} not found in asset service", assetId);
                }
            }
        }

        Map<String, TermDepositAssetDetail> termDepositAssetDetails = new HashMap<>();
        //TODO - UPS REFACTOR1 -  This should be loaded in terms of the account ID on the order not on the users profile (in case they have more than one dealer group).
        Broker broker = userProfileService.getDealerGroupBroker();

        if ((broker != null) && (broker.getDealerKey() != null)) {
            logger.debug("broker dealer key:{}", broker.getDealerKey().getId());
            termDepositAssetDetails = assetService.loadTermDepositRates(broker.getDealerKey(), DateTime.now(), assetList,
                    serviceErrors);
        }

        return assetDtoConverter.toAssetDto(assets, termDepositAssetDetails, true);
    }
}
