package com.bt.nextgen.api.order.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.model.OrderTransactionDto;
import com.bt.nextgen.api.order.model.TradeOrderDto;
import com.bt.nextgen.api.order.util.OrderUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.order.OrderDetail;
import com.bt.nextgen.service.integration.order.OrderIntegrationService;
import com.bt.nextgen.service.integration.order.OrderTransaction;
import com.bt.nextgen.service.integration.order.OrderType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service("TradeOrderDtoServiceV0.1")
public class TradeOrderDtoServiceImpl implements TradeOrderDtoService {

    @Autowired
    @Qualifier("avaloqOrderIntegrationService")
    private OrderIntegrationService orderService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    private static final String PRICE = "price";
    private static final String QTY = "qty";

    @Override
    public TradeOrderDto find(OrderKey key, ServiceErrors serviceErrors) {
        List<OrderTransactionDto> orderTransactionDtos = new ArrayList<OrderTransactionDto>();
        Boolean cancellable = true;
        TradeOrderDto tradeOrderDto = null;
        List<OrderTransaction> orderTransactions = orderService.loadTransactionData(key.getOrderId(), serviceErrors);
        if (orderTransactions != null) {
            OrderType orderTypeEnum = orderTransactions.get(0).getOrderType();
            for (OrderTransaction orderTransaction : orderTransactions) {
                if (cancellable && key.getOrderId().equals(orderTransaction.getOrderId())) {
                    cancellable = false;
                }
                if ((!orderTransaction.getOrderId().equals(key.getOrderId()))
                        || (orderTransaction.getOrderId().equals(key.getOrderId())
                                && orderTransaction.getConsideration() != null)) {
                    BigDecimal averagePrice = getAveragePrice(orderTransaction);
                    BigDecimal filledQuantity = getFilledQuantity(orderTransaction);
                    BigDecimal consideration = orderTypeEnum.isBuy() ? orderTransaction.getConsideration().abs()
                            : orderTransaction.getConsideration();
                    orderTransactionDtos.add(new OrderTransactionDto(orderTransaction.getTradeDate(), filledQuantity,
                            averagePrice.abs(), orderTransaction.getTransactionFee().abs(), consideration,
                            orderTransaction.getSettlementDate()));
                }
            }
            Collections.sort(orderTransactionDtos, new Comparator<OrderTransactionDto>() {
                @Override
                public int compare(OrderTransactionDto o1, OrderTransactionDto o2) {
                    return o1.getTradeDate().compareTo(o2.getTradeDate());
                }
            });
            AssetDto assetDto = getAsset(orderTransactions.get(0).getAssetId(), serviceErrors);
            String orderType = OrderUtil.getOrderType(orderTypeEnum, assetDto);
            tradeOrderDto = new TradeOrderDto(key, orderType, assetDto, cancellable, orderTransactions.get(0),
                    orderTransactionDtos);
        }
        return tradeOrderDto;
    }

    private AssetDto getAsset(String assetId, ServiceErrors serviceErrors) {
        Asset asset = assetIntegrationService.loadAsset(assetId, serviceErrors);
        AssetDto assetDto = assetDtoConverter.toAssetDto(asset, null);
        return assetDto;
    }

    protected BigDecimal getAveragePrice(OrderTransaction orderTransaction) {
        List<OrderDetail> details = orderTransaction.getDetails();
        if (CollectionUtils.isNotEmpty(details)) {
            for (OrderDetail detail : details) {
                if (PRICE.equals(detail.getKey())) {
                    return detail.getValue();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    protected BigDecimal getFilledQuantity(OrderTransaction orderTransaction) {
        List<OrderDetail> details = orderTransaction.getDetails();
        if (CollectionUtils.isNotEmpty(details)) {
            for (OrderDetail detail : details) {
                if (QTY.equals(detail.getKey())) {
                    return detail.getValue().abs();
                }
            }
        }

        return BigDecimal.ZERO;
    }
}
