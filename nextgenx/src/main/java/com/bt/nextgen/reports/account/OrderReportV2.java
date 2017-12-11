package com.bt.nextgen.reports.account;

import ch.lambdaj.Lambda;
import static ch.lambdaj.Lambda.on;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.asset.service.AssetDtoConverterV2;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.order.model.OrderSummaryDto;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.investmentorders.ordercapture.AbstractOrderReportV2;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.order.PriceType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The Class OrderReport generates the necessary report data for the order
 * preview and receipt. The data consists of AssetDto.
 *
 * Suppress warnings - Classes should not be coupled to too many other classes
 * (Single Responsibility Principle). It needs this many imports and is not
 * practical to split this class into smaller ones.
 */
@SuppressWarnings("squid:S1200")
public class OrderReportV2 extends AbstractOrderReport {

    private static final String DECLARATION = "DS-IP-0044";
    private static final String ORDER_GROUP_DETAILS = "order-group-details";
    private static final String ORDER_SUMMARY = "order-summary";
    private static final String LISTED_SECURITY_ORDERS = "listedSecurityOrders";
    private static final String MANAGED_FUND_ORDERS = "managedFundOrders";
    private static final String MANAGED_PORTFOLIO_ORDERS = "managedPortfolioOrders";
    private static final String TERM_DEPOSIT_ORDERS = "termDepositOrders";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private AssetDtoConverterV2 assetDtoConverter;


    public OrderReportV2(String reportType, String subReportType) {
        super(reportType, subReportType, DECLARATION);
    }

    /**
     * Gets the managed fund orders report data.
     *
     * @param params
     *            the params of the report request.
     * @return the managed fund orders report data.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @ReportBean(MANAGED_FUND_ORDERS)
    public OrderGroupDto getManagedFundOrders(Map<String, String> params) throws IOException {
        return generateOrderGroupDto(AssetType.MANAGED_FUND, params);
    }

    /**
     * Gets the managed portfolio orders.
     *
     * @param params
     *            the params of the report request.
     * @return the managed portfolio orders report data.
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @ReportBean(MANAGED_PORTFOLIO_ORDERS)
    public OrderGroupDto getManagedPortfolioOrders(Map<String, String> params) throws IOException {
        return generateOrderGroupDto(AssetType.MANAGED_PORTFOLIO, params);
    }

    /**
     * Gets the term deposit orders.
     *
     * @param params
     *            the params
     * @return the term deposit orders
     * @throws Exception
     *             the exception
     */
    @ReportBean(TERM_DEPOSIT_ORDERS)
    public OrderGroupDto getTermDepositOrders(Map<String, String> params) throws IOException {
        return generateOrderGroupDto(AssetType.TERM_DEPOSIT, params);
    }

    /**
     * Gets the listed security orders.
     *
     * @param params
     *            the params
     * @return the listed security orders
     * @throws Exception
     *             the exception
     */
    @ReportBean(LISTED_SECURITY_ORDERS)
    public OrderGroupDto getListedSecurityOrders(Map<String, String> params) throws IOException {
        return generateOrderGroupDto(AssetType.SHARE, params);
    }

    /**
     * Gets the page level warnings.
     *
     * @param params
     *            the params
     * @return the page level warnings
     * @throws Exception
     *             the exception
     */
    /**
     * Suppress generic wildcard warning
     */
    @SuppressWarnings("squid:S1452")
    @ReportBean("pageLevelWarnings")
    public Collection<?> getPageLevelWarnings(Map<String, String> params) throws IOException {
        String orderGroup = params.get(ORDER_GROUP_DETAILS);
        ObjectMapper mapper = new ObjectMapper();
        OrderGroupDto orderGroupDto = mapper.readValue(orderGroup, OrderGroupDto.class);

        if (orderGroupDto.getWarnings() != null) {
            return orderGroupDto.getWarnings();
        }

        return Collections.emptyList();
    }

    /**
     * Gets the order summary.
     *
     * @param params
     *            the params
     * @return the order summary
     * @throws Exception
     *             the exception
     */
    @ReportBean("orderSummary")
    public OrderSummaryDto getOrderSummary(Map<String, String> params) throws IOException {
        String orderSummary = params.get(ORDER_SUMMARY);
        ObjectMapper mapper = new ObjectMapper();
        OrderSummaryDto orderSummaryDto = mapper.readValue(orderSummary, OrderSummaryDto.class);
        return orderSummaryDto;
    }

    /**
     * This loads the assets from the given order items.
     *
     * @param orderItems
     *            the order items
     * @return the asset map
     */
    private Map<String, Asset> getAssetMap(List<OrderItemDto> orderItems) {
        ListIterator<OrderItemDto> orderItemDtos = orderItems.listIterator();
        Collection<String> assetIds = new ArrayList<String>();

        while (orderItemDtos.hasNext()) {
            OrderItemDto orderItem = orderItemDtos.next();
            String assetId = orderItem.getAsset().getAssetId();
            assetIds.add(assetId);
        }

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        Map<String, Asset> assetMap = assetService.loadAssets(assetIds, serviceErrors);
        return assetMap;
    }

    /**
     * Loads term deposit rates.
     *
     * @param encodedAccountId
     *            the encoded account id
     * @return the map
     */
    private List<TermDepositInterestRate>  loadTermDepositRates(EncodedString encodedAccountId, Asset asset) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        WrapAccountDetail accountDetail = accountService.loadWrapAccountDetail(
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(encodedAccountId.plainText()), serviceErrors);
        Broker adviser = brokerService.getBroker(accountDetail.getAdviserKey(), serviceErrors);
        List<Asset> assets = new ArrayList<>();
        assets.add(asset);
        final List<String> assetIdList = Lambda.collect(assets, on(Asset.class).getAssetId());
        TermDepositAssetRateSearchKey termDepositAssetRateSearchKey = new TermDepositAssetRateSearchKey(accountDetail.getProductKey(),adviser.getDealerKey(),null, accountDetail.getAccountStructureType(),DateTime.now(),assetIdList);
        List<TermDepositInterestRate> termDepositAssetDetails = assetService.loadTermDepositRates(termDepositAssetRateSearchKey, serviceErrors);
        return termDepositAssetDetails;
    }



    /**
     * Generate order group dto. This will build up the list of OrderItemDto for
     * orders that match the given assetType populating the specific assetDto
     * for that order item.
     *
     * @param assetType
     *            the asset type for the order group
     * @param params
     *            the params of the report request.
     * @return the order group dto
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private OrderGroupDto generateOrderGroupDto(final AssetType assetType, final Map<String, String> params) throws IOException {
        List<OrderItemDto> assetOrders = new ArrayList<>();
        String orderGroup = params.get(ORDER_GROUP_DETAILS);
        ObjectMapper mapper = new ObjectMapper();
        OrderGroupDto orderGroupDto = mapper.readValue(orderGroup, OrderGroupDto.class);
        List<OrderItemDto> orderItems = orderGroupDto.getOrders();
        Map<String, Asset> assetMap = getAssetMap(orderGroupDto.getOrders());

        for (OrderItemDto orderItem : orderItems) {
            if (assetType.equals(AssetType.forDisplay(orderItem.getAssetType()))) {
                Asset asset = assetMap.get(orderItem.getAsset().getAssetId());
                AssetDto assetDto = assetDtoConverter.toAssetDto(asset, null);

                switch (assetType) {
                    case TERM_DEPOSIT:
                        assetDto = generateTermDepositDto(asset, params, orderItem);
                        break;
                    case SHARE:
                        orderItem.setPriceType(orderItem.getPriceType().equals(PriceType.LIMIT.getIntlId()) ? PriceType.LIMIT
                                .getDisplayName() : PriceType.MARKET.getDisplayName());
                        orderItem.setAmount(orderItem.getEstimated());
                        break;
                    case MANAGED_FUND:
                    case MANAGED_PORTFOLIO:
                    case CASH:
                    default:
                        break;
                }

                orderItem.setAsset(assetDto);
                assetOrders.add(orderItem);
            }
        }

        orderGroupDto.setOrders(assetOrders);
        return orderGroupDto;
    }

    /**
     * Generate the TermDepositDto with the indicative interest rate set.
     *
     * @param asset
     *            the term deposit asset
     * @param params
     *            the params from the report request
     * @param orderItem
     *            the term deposit order item
     * @return the TermDepositDto
     */
    private TermDepositAssetDto generateTermDepositDto(final Asset asset, final Map<String, String> params, OrderItemDto orderItem) {
       EncodedString encodedAccountId = new EncodedString(params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING));
       List<TermDepositInterestRate> termDepositAssetDetails = loadTermDepositRates(encodedAccountId, asset);
       SortedSet<TermDepositInterestRate> termDepositSet = new TreeSet<>(termDepositAssetDetails);
       TermDepositAssetDtoV2 termDepositAssetDto = (TermDepositAssetDtoV2) assetDtoConverter.toAssetDto(asset,termDepositSet);

        BigDecimal interestRate = orderItem.getIntRate();
        return new TermDepositAssetDto(asset, termDepositAssetDto.getAssetName(), termDepositAssetDto.getIssuer(),
                termDepositAssetDto.getTerm(), termDepositAssetDto.getMaturityDate(),
                termDepositAssetDto.getInterestPaymentFrequency(), termDepositAssetDto.getMinInvest(),
                termDepositAssetDto.getMaxInvest(), termDepositAssetDto.getInterestBands(), interestRate);
    }

}
