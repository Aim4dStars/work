package com.bt.nextgen.reports.account.investmentorders.ordercapture;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import ch.lambdaj.Lambda;
import static ch.lambdaj.Lambda.on;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.asset.service.AssetDtoConverterV2;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import net.sf.jasperreports.engine.Renderable;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.model.OrderSummaryDto;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("squid:S1200")
public abstract class AbstractOrderReportV2 extends AccountReportV2 {

    private static final String ORDER_GROUP_DETAILS = "order-group-details";
    private static final String ORDER_SUMMARY = "order-summary";
    private static final String ICON_WARNING_LOCATION = "/images/icon-warning-sml.png";
    private static final String ORDER_GROUP_DATA_KEY = "AbstractOrderReport.orderGroupData";

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private AssetDtoConverterV2 assetDtoConverter;

    public AbstractOrderReportV2() {
        super();
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return Collections.singletonList(getOrderGroupDataV2(params, dataCollections));
    }

    protected OrderGroupReportData getOrderGroupDataV2(Map<String, Object> params, Map<String, Object> dataCollections) {
        OrderGroupReportData orderGroupData = null;
        synchronized (dataCollections) {
            if (dataCollections.get(ORDER_GROUP_DATA_KEY) == null) {
                String orderGroup = (String) params.get(ORDER_GROUP_DETAILS);
                String orderSummary = (String) params.get(ORDER_SUMMARY);
                ObjectMapper mapper = new ObjectMapper();
                OrderGroupDto orderGroupDto = null;
                OrderSummaryDto orderSummaryDto = null;
                try {
                    orderGroupDto = orderGroup != null ? mapper.readValue(orderGroup, OrderGroupDto.class): new OrderGroupDto();
                    orderSummaryDto = mapper.readValue(orderSummary, OrderSummaryDto.class);
                } catch (IOException e) {
                    throw new IllegalArgumentException("JSON Message illegal", e);
                }
                Broker broker = getAdviser(getAccountKey(params), params, dataCollections);
                WrapAccountDetail accountDetail = accountService.loadWrapAccountDetail(
                        getAccountKey(params), new FailFastErrorsImpl());
                orderGroupData = generateOrderGroupDataV2(orderGroupDto, orderSummaryDto, broker,accountDetail.getProductKey(),accountDetail.getAccountStructureType());
                dataCollections.put(ORDER_GROUP_DATA_KEY, orderGroupData);
            }
        }
        orderGroupData = (OrderGroupReportData) dataCollections.get(ORDER_GROUP_DATA_KEY);
        return orderGroupData;
    }

    private OrderGroupReportData generateOrderGroupDataV2(OrderGroupDto orderGroupDto, OrderSummaryDto orderSummaryDto,
                                                        Broker broker, ProductKey productKey,AccountStructureType accountStructureType) {
        Map<String,Asset> assetMap = getAssetMap(orderGroupDto.getOrders());
        if(CollectionUtils.isNotEmpty(orderGroupDto.getOrders())){
            for(OrderItemDto orderItem: orderGroupDto.getOrders()){
                String assetId = orderItem.getAsset().getAssetId();
                Asset asset = assetMap.get(assetId);
                AssetDto assetDto = assetDtoConverter.toAssetDto(asset, null);
                if (orderItem.getAssetType().equals(AssetType.TERM_DEPOSIT.getDisplayName())) {
                    assetDto = generateTermDepositDto(asset, orderItem, broker,productKey,accountStructureType);
                }
                orderItem.setAsset(assetDto);
            }
        }
        return sortAndConstructReportData(orderGroupDto, orderSummaryDto);
    }

    private OrderGroupReportData sortAndConstructReportData(OrderGroupDto orderGroupDto, OrderSummaryDto orderSummaryDto) {
        List<OrderItemDto> orderItems = orderGroupDto.getOrders();
        if(CollectionUtils.isNotEmpty(orderItems)){
            Collections.sort(orderItems, new Comparator<OrderItemDto>() {
                @Override
                public int compare(OrderItemDto o1, OrderItemDto o2) {
                    return AssetType.forDisplay(o1.getAssetType()).getSortOrder()
                            - AssetType.forDisplay(o2.getAssetType()).getSortOrder();
                }
            });
        }
        OrderGroupReportData orderGroupData = new OrderGroupReportData(orderGroupDto, orderSummaryDto);
        return orderGroupData;
    }

    /**
     * This loads the assets from the given order items.
     *
     * @param orderItems
     *            the order items
     * @return the asset map
     */
    private Map<String, Asset> getAssetMap(List<OrderItemDto> orderItems) {
        Map<String, Asset> assetMap = new HashMap<>();
         if(CollectionUtils.isNotEmpty(orderItems)){
            ListIterator<OrderItemDto> orderItemDtos = orderItems.listIterator();
            Collection<String> assetIds = new ArrayList<String>();

            while (orderItemDtos.hasNext()) {
                OrderItemDto orderItem = orderItemDtos.next();
                String assetId = orderItem.getAsset().getAssetId();
                assetIds.add(assetId);
            }

            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            assetMap = assetService.loadAssets(assetIds, serviceErrors);
        }

        return assetMap;
    }

    /**
     * Loads term deposit rates.
     *
     *
     *            the encoded account id
     * @return the map
     */
    private  List<TermDepositInterestRate> loadTermDepositRates(Asset asset,
                                                                     Broker broker,ProductKey productKey,AccountStructureType accountStructureType) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final List<String> assetIds = Lambda.collect(Collections.singleton(asset),on(Asset.class).getAssetId());
        TermDepositAssetRateSearchKey termDepositAssetRateSearchKey = new TermDepositAssetRateSearchKey(productKey,broker.getDealerKey(),null,accountStructureType,DateTime.now(),assetIds);

       List<TermDepositInterestRate> termDepositAssetDetails = assetService.loadTermDepositRates(termDepositAssetRateSearchKey, serviceErrors);
        return termDepositAssetDetails;
    }

    /**
     * Generate the TermDepositDto with the indicative interest rate set.
     *
     * @param asset
     *            the term deposit asset

     * @param orderItem
     *            the term deposit order item
     * @return the TermDepositDto
     */
    private TermDepositAssetDtoV2 generateTermDepositDto(final Asset asset, OrderItemDto orderItem,
                                                       Broker broker,ProductKey productKey,AccountStructureType accountStructureType) {
        List<TermDepositInterestRate> termDepositAssetDetails = loadTermDepositRates(asset,
                broker,productKey,accountStructureType);
        SortedSet<TermDepositInterestRate> termDepositInterestRateTreeSet = new TreeSet<>(termDepositAssetDetails);
        TermDepositAssetDtoV2 termDepositAssetDto = (TermDepositAssetDtoV2) assetDtoConverter.toAssetDto(asset,termDepositInterestRateTreeSet);
        BigDecimal interestRate = orderItem.getIntRate();
        return new TermDepositAssetDtoV2(asset, termDepositAssetDto.getAssetName(), termDepositAssetDto.getIssuer(),
                termDepositAssetDto.getTerm(), termDepositAssetDto.getMaturityDate(),
                termDepositAssetDto.getInterestPaymentFrequency(), termDepositAssetDto.getMinInvest(),
                termDepositAssetDto.getMaxInvest(), termDepositAssetDto.getInterestBands(), interestRate);
    }

    @ReportBean("warningIconLogo")
    public Renderable getWarningIconImageV2() {
        return getRasterImage(ICON_WARNING_LOCATION);
    }

}
