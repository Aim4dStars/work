package com.bt.nextgen.api.order.service.helper;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.order.model.FundsAllocationDto;
import com.bt.nextgen.api.order.model.OrderFeeDto;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.model.OrderItemSummaryDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.FlatPercentFeesComponent;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.avaloq.order.OrderItemSummaryImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.order.PriceType;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class OrderItemHelperTest {
    @InjectMocks
    private OrderItemHelper helper;

    @Mock
    private AssetHelper assetHelper;

    List<Pair<String, BigDecimal>> fundsSource;
    OrderItemSummaryImpl summary1;
    OrderItemSummaryImpl summary2;
    OrderItemImpl order1;
    OrderItemImpl order2;
    List<OrderItem> orderList;
    FundsAllocationDto fundsAllocation;
    OrderItemSummaryDto summaryDto;
    OrderItemDto buyOrderDto;
    List<OrderItemDto> orderDtoList;
    WrapAccountValuationImpl valuation;

    @Before
    public void setup() throws Exception {
        fundsSource = new ArrayList<Pair<String, BigDecimal>>();
        fundsSource.add(new ImmutablePair<String, BigDecimal>("1111111", new BigDecimal("159753")));

        summary1 = new OrderItemSummaryImpl(new BigDecimal("7000"), true, "reinvest", new BigInteger("100"),
                new BigDecimal("5.55"), "never", PriceType.MARKET);
        order1 = new OrderItemImpl("1234", "buy", AssetType.SHARE, "bhp", summary1, fundsSource);
        order1.setFirstNotification("notifi");

        summary2 = new OrderItemSummaryImpl(new BigDecimal("8000"), false, "cash", new BigInteger("200"), new BigDecimal("6.67"),
                "never", PriceType.LIMIT);
        order2 = new OrderItemImpl("56789", "buy", AssetType.SHARE, "wbc", summary2, new ArrayList<Pair<String, BigDecimal>>());

        orderList = new ArrayList<>();
        orderList.add(order1);
        orderList.add(order2);

        AssetImpl mpAsset = new AssetImpl();
        mpAsset.setAssetId("1234");
        mpAsset.setStatus(AssetStatus.OPEN);
        AssetDto assetDto = new ManagedPortfolioAssetDto(mpAsset);

        fundsAllocation = new FundsAllocationDto("46804E8B5F179DA38D92E506C2A825BD771E85B9A85D17C6", BigDecimal.ONE);
        List<FundsAllocationDto> fundsAllocations = new ArrayList<>();
        fundsAllocations.add(fundsAllocation);
        summaryDto = new OrderItemSummaryDto(new BigDecimal(10000), false, null, null, null, null, null);

        buyOrderDto = new OrderItemDto(null, assetDto, AssetType.MANAGED_PORTFOLIO.getDisplayName(), "Buy", summaryDto,
                fundsAllocations);
        buyOrderDto.setFees(Collections.singletonList(
                new OrderFeeDto(FeesType.PORTFOLIO_MANAGEMENT_FEE, new FlatPercentFeesComponent(BigDecimal.valueOf(0.1)))));
        buyOrderDto.setBankClearNumber("032140");
        buyOrderDto.setPayerAccount("1234561");
        buyOrderDto.setIncomePreference(IncomePreference.TRANSFER.toString());
        buyOrderDto.setPriceType(PriceType.LIMIT.getIntlId());

        orderDtoList = new ArrayList<>();
        orderDtoList.add(buyOrderDto);

        valuation = new WrapAccountValuationImpl();
        valuation.setAccountKey(AccountKey.valueOf("AccountKey"));
        ManagedPortfolioAccountValuation mpValuation = Mockito.mock(ManagedPortfolioAccountValuation.class);
        Mockito.when(mpValuation.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);
        Mockito.when(mpValuation.getAsset()).thenReturn(mpAsset);
        Mockito.when(mpValuation.getAvailableBalance()).thenReturn(BigDecimal.ZERO);
        ArrayList<SubAccountValuation> subAccountValuations = new ArrayList<>();
        subAccountValuations.add(mpValuation);
        valuation.setSubAccountValuations(subAccountValuations);

        Mockito.when(assetHelper.getAssetsForOrders(Mockito.any(AccountKey.class), Mockito.anyList(),
                Mockito.any(ServiceErrors.class))).thenReturn(new HashMap<String, AssetDto>());
    }

    @Test
    public void testToOrdersDto_whenOrdersIsNull_thenEmptyListReturned() {
        List<OrderItemDto> orderItemDtos = helper.toOrdersDto(AccountKey.valueOf("accountId"), null, new ServiceErrorsImpl());
        Assert.assertEquals(0, orderItemDtos.size());
    }

    @Test
    public void testToOrdersDto_whenOrders_thenOrderItemsMappedCorrectly() {
        List<OrderItemDto> orderItemDtos = helper.toOrdersDto(AccountKey.valueOf("accountId"), orderList,
                new ServiceErrorsImpl());
        Assert.assertEquals(2, orderItemDtos.size());
        Assert.assertEquals(orderList.get(0).getAmount(), orderItemDtos.get(0).getAmount());
        Assert.assertEquals(orderList.get(0).getIsFull(), orderItemDtos.get(0).getSellAll());
        Assert.assertEquals(orderList.get(0).getDistributionMethod(), orderItemDtos.get(0).getDistributionMethod());
        Assert.assertEquals(orderList.get(0).getUnits(), orderItemDtos.get(0).getUnits());
        Assert.assertEquals(orderList.get(0).getPrice(), orderItemDtos.get(0).getPrice());
        Assert.assertEquals(orderList.get(0).getExpiry(), orderItemDtos.get(0).getExpiry());
        Assert.assertEquals(orderList.get(0).getPriceType().getIntlId(), orderItemDtos.get(0).getPriceType());
        Assert.assertEquals(orderList.get(0).getOrderId(), orderItemDtos.get(0).getOrderId());
        Assert.assertEquals(orderList.get(0).getOrderType(), orderItemDtos.get(0).getOrderType());
        Assert.assertEquals(orderList.get(0).getFirstNotification(), orderItemDtos.get(0).getFirstNotification());
    }

    @Test
    public void testToOrderItems_whenOrders_thenOrderItemsMappedCorrectly() {
        List<OrderItem> orderItems = helper.toOrderItems(orderDtoList, valuation);
        Assert.assertEquals(1, orderItems.size());
        OrderItem order = orderItems.get(0);
        Assert.assertEquals(EncodedString.toPlainText(buyOrderDto.getFundsAllocation().get(0).getAccountId()),
                order.getFundsSource().get(0).getKey());
        Assert.assertEquals(buyOrderDto.getFundsAllocation().get(0).getAllocation(), order.getFundsSource().get(0).getValue());
        Assert.assertEquals(buyOrderDto.getOrderId(), order.getOrderId());
        Assert.assertEquals(buyOrderDto.getAsset().getAssetId(), order.getAssetId());
        Assert.assertEquals(AssetType.forDisplay(buyOrderDto.getAssetType()), order.getAssetType());
        Assert.assertEquals(buyOrderDto.getOrderType(), order.getOrderType());
        Assert.assertEquals(buyOrderDto.getAmount(), order.getAmount());
        Assert.assertEquals(buyOrderDto.getSellAll(), order.getIsFull());
        Assert.assertEquals(buyOrderDto.getDistributionMethod(), order.getDistributionMethod());
        Assert.assertEquals(buyOrderDto.getPriceType(), order.getPriceType().getIntlId());
    }
}
