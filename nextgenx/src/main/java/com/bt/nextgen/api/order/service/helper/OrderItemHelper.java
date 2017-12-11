package com.bt.nextgen.api.order.service.helper;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.order.model.FundsAllocationDto;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.model.OrderItemSummaryDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemSummaryImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.order.PriceType;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("OrderItemHelperV0.1")
public class OrderItemHelper {

    @Autowired
    private AssetHelper assetHelper;

    public List<OrderItemDto> toOrdersDto(AccountKey accountKey, List<OrderItem> orders, ServiceErrors serviceErrors) {
        List<OrderItemDto> orderDtos = new ArrayList<>();
        if (orders != null) {
            Map<String, AssetDto> assets = assetHelper.getAssetsForOrders(accountKey, orders, serviceErrors);
            for (OrderItem order : orders) {
                orderDtos.add(toOrderDto(order, assets));
            }
        }
        return orderDtos;
    }

    public OrderItemDto toOrderDto(OrderItem order, Map<String, AssetDto> assets) {
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

        OrderItemDto orderItemDto = new OrderItemDto(order.getOrderId(), assets.get(order.getAssetId()), null,
                order.getOrderType(), summaryDto, allocationDtos, order.getFirstNotification());
        return orderItemDto;
    }

    public OrderItem toOrderItem(OrderItemDto order, WrapAccountValuation valuation) {
        List<Pair<String, BigDecimal>> allocations = new ArrayList<>();
        for (FundsAllocationDto allocationDto : order.getFundsAllocation()) {
            Pair<String, BigDecimal> allocation = new ImmutablePair<>(EncodedString.toPlainText(allocationDto.getAccountId()),
                    allocationDto.getAllocation());
            allocations.add(allocation);
        }

        // Retrieve the subAccountKey based on both asset-id from open-subaccount.
        SubAccountKey subAccountKey = getSubAccountKey(order, valuation, Boolean.FALSE);
        if (subAccountKey == null) {
            // include closed-subaccount.
            subAccountKey = getSubAccountKey(order, valuation, Boolean.TRUE);
        }

        OrderItemSummaryImpl summary = new OrderItemSummaryImpl(order.getAmount(), order.getSellAll(),
                order.getDistributionMethod(), order.getUnits(), order.getPrice(), order.getExpiry(), PriceType.forIntlId(order
                        .getPriceType()));

        return new OrderItemImpl(subAccountKey, order.getOrderId(), order.getOrderType(), AssetType.forDisplay(order
                .getAssetType()), order.getAsset().getAssetId(), summary, allocations);
    }

    public List<OrderItem> toOrderItems(List<OrderItemDto> orderDtos, WrapAccountValuation valuation) {
        List<OrderItem> orders = new ArrayList<>();
        for (OrderItemDto orderDto : orderDtos) {
            orders.add(toOrderItem(orderDto, valuation));
        }
        return orders;
    }

    /**
     * Retrieve the subAccountkey based on the order and valuation specified. If
     * basedOnAssetOnly is true, the method will return the key as long as the
     * asset-id matches. Otherwise, it will only return the asset-id when the
     * account's available-balance is > 0.
     * 
     * @param order
     * @param valuation
     * @param includeClosed
     *            true if include closed subAccount, false otherwise.
     * @return
     */
    private SubAccountKey getSubAccountKey(OrderItemDto order, WrapAccountValuation valuation, boolean includeClosed) {
        SubAccountKey subAccountKey = null;
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            if (subAccount.getAssetType() == AssetType.MANAGED_PORTFOLIO
                    || subAccount.getAssetType() == AssetType.TAILORED_PORTFOLIO) {
                ManagedPortfolioAccountValuation mfAcc = (ManagedPortfolioAccountValuation) subAccount;
                Asset asset = mfAcc.getAsset();
                boolean hasAsset = asset != null && order.getAsset().getAssetId().equals(asset.getAssetId());
                if (hasAsset) {
                    boolean isClosed = BigDecimal.ZERO.compareTo(mfAcc.getAvailableBalance()) == 0;
                    if (includeClosed || !isClosed) {
                        subAccountKey = mfAcc.getSubAccountKey();
                        break;
                    }
                }
            }
        }

        return subAccountKey;
    }
}
