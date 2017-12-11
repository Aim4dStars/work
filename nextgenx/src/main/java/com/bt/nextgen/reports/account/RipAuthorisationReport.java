package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.AccountHelper;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.bt.nextgen.core.reporting.stereotype.ReportInitializer;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.Renderable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@Report("ripAuthorisationReport")
public class RipAuthorisationReport extends AccountReport {
    private static final String DECLARATION = "DS-IP-0080";
    private static final String SUPER_DECLARATION = "DS-IP-0181";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private AssetDtoConverter assetDtoConverter;

    @Autowired
    private AccountHelper accHelper;

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper mapper;

    protected final String IS_SUPER = "isSuper";

    @ReportBean("regularInvestmentDto")
    public RegularInvestmentDto getRegularInvestment(Map<String, String> params) throws IOException {

        String rip = params.get("investmentOrder");
        RegularInvestmentDto ripDto = mapper.readValue(rip, RegularInvestmentDto.class);

        // Process order items.
        List<OrderItemDto> orderItems = ripDto.getOrders();
        Map<String, Asset> assetMap = getAssetMap(orderItems);
        List<OrderItemDto> assetOrders = new ArrayList<>();
        for (OrderItemDto orderItem : orderItems) {
            Asset asset = assetMap.get(orderItem.getAsset().getAssetId());
            orderItem.setAsset(assetDtoConverter.toAssetDto(asset, null));
            assetOrders.add(orderItem);
        }
        ripDto.getInvestmentAmount();
        ripDto.setOrders(assetOrders);

        String accountId = params.get("account-id");
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(EncodedString.toPlainText(accountId));
        ripDto.setCashAccountDto(accHelper.getBankAccountDto(wrapAccountIdentifierImpl, new ServiceErrorsImpl()));

        return ripDto;
    }

    @ReportBean("investmentAccount")
    public AccountDto getInvestmentAccount(Map<String, String> params) {
        return this.getAccount(params).iterator().next();
    }

    @ReportBean("formattedStartDate")
    public String getStartDate(Map<String, String> params) {
        return params.get("formattedStartDate");
    }

    @ReportBean("formattedEndDate")
    public String getEndDate(Map<String, String> params) {
        return params.get("formattedEndDate");
    }

    @ReportBean("reportType")
    @SuppressWarnings("squid:S1172")
    public String getReportName(Map<String, String> params) {
        return "Client authorisation";
    }

    /**
     * Gets the sub report name.
     * 
     * @param params
     *            the params
     * @return the sub report name
     */
    @ReportBean("subReportType")
    @SuppressWarnings("squid:S1172")
    public String getSubReportName(Map<String, String> params) {
        return "Regular investment plan";
    }

    @ReportImage("paymentFromToIcon")
    @SuppressWarnings("squid:S1172")
    public Renderable getPaymentFromToIcon(Map<String, String> params) {
        String imageLocation = cmsService.getContent("paymentFromToIcon");
        return getRasterImage(imageLocation);
    }

    @ReportBean("declaration")
    @SuppressWarnings("squid:S1172")
    public String getDescription(Map<String, String> params) {
        String description = cmsService.getContent(DECLARATION);
        if (Boolean.valueOf(params.get("isSuper"))) {
            description = cmsService.getContent(SUPER_DECLARATION);
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

    @ReportInitializer
    public void init(Map<String, String> params) {
        Collection<AccountDto> result = super.getAccount(params);
        Boolean isSuper = AccountStructureType.SUPER.name().equals(result.iterator().next().getAccountType());
        params.put(IS_SUPER, isSuper.toString());
    }
}
