package com.bt.nextgen.reports.account.investmentorders.rips;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.AccountHelper;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionNames;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@Report("ripAuthorisationReportV2")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_Client_Orders')")
public class RipAuthorisationReport extends AccountReportV2 {

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper mapper;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    @Autowired
    private AccountHelper accHelper;

    @Autowired
    private OptionsService optionsService;

    private static final String DECLARATION = "DS-IP-0080";
    private static final String SUPER_DECLARATION = "DS-IP-0181";

    private static final String REPORT_TYPE = "Client authorisation - regular investment plan";
    private static final String REPORT_TITLE = "Your client authorisation for a regular investment plan";

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TYPE;
    }

    @ReportBean("reportTitle")
    public String getReportTitle(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TITLE;
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        // Map details for new RIP from JSON
        String rip = (String) params.get("investmentOrder");
        RegularInvestmentDto ripDto = null;
        try {
            ripDto = mapper.readValue(rip, RegularInvestmentDto.class);
        } catch (IOException ie) {
            throw new IllegalArgumentException("JSON Message illegal", ie);
        }

        List<OrderItemDto> processedOrders = processOrders(ripDto.getOrders());
        ripDto.setOrders(processedOrders);

        String accountId = (String) params.get("account-id");
        BankAccountDto cashAccount = getBankAccountDto(accountId);
        ripDto.setCashAccountDto(cashAccount);

        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));
        
        String formattedStartDate = (String) params.get("formattedStartDate");
        String formattedEndDate = (String) params.get("formattedEndDate");

        return Collections.singletonList(new RegularInvestmentReportData(ripDto, account, formattedStartDate, formattedEndDate));
    }

    @ReportBean("declaration")
    public String getDeclaration(Map<String, Object> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        String description = getContent(DECLARATION);
        boolean investorGuideFeature = optionsService.hasFeature(OptionKey.valueOf(OptionNames.DECLARATION_INVESTORGUIDE),
                getAccountKey(params),
                serviceErrors);
        if (!investorGuideFeature) {
            description = getContent(SUPER_DECLARATION);
        }
        return description;
    }

    /**
     * This loads the assets from the given order items.
     * 
     * @param orderItems
     *            the order items
     * @return the asset map
     */
    protected Map<String, Asset> getAssetMap(List<OrderItemDto> orderItems) {
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

    private List<OrderItemDto> processOrders(List<OrderItemDto> orders) {
        List<OrderItemDto> processedOrders = new ArrayList<>();
        Map<String, Asset> assetMap = getAssetMap(orders);

        for (OrderItemDto order : orders) {
            Asset asset = assetMap.get(order.getAsset().getAssetId());
            order.setAsset(assetDtoConverter.toAssetDto(asset, null));
            processedOrders.add(order);
        }
        return processedOrders;
    }

    private BankAccountDto getBankAccountDto(String accountId) {
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(EncodedString.toPlainText(accountId));

        return accHelper.getBankAccountDto(wrapAccountIdentifierImpl, new ServiceErrorsImpl());
    }
}
