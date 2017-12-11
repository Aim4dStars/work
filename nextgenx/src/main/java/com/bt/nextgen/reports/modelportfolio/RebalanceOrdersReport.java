package com.bt.nextgen.reports.modelportfolio;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceAccountDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioRebalanceDetailDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderGroupDto;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.ModelPortfolioRebalanceDetailDtoService;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.RebalanceOrdersDtoService;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportImage;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.AccountReport;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import net.sf.jasperreports.engine.Renderable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Report("rebalanceOrdersReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
public class RebalanceOrdersReport extends AccountReport {

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private RebalanceOrdersDtoService rebalanceOrdersService;

    @Autowired
    private ModelPortfolioRebalanceDetailDtoService rebalanceDetailsService;

    private static final String PARAM_IPS_ID = "ips";
    private static final String PARAM_ORDER_ID = "order-id";
    private static final String PARAM_ACCOUNT_ID = "account-id";
    private static final String DISCLAIMER_CONTENT = "DS-IP-0111";
    private static final String REBALANCE_ORDERS_KEY = "RebalanceOrdersReport.rebalanceOrders";


    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        IpsKey ipsKey = IpsKey.valueOf((String) params.get(PARAM_IPS_ID));
        String orderId = (String) params.get(PARAM_ORDER_ID);

        List<RebalanceOrderGroupDto> rebalanceOrderGroups = getRebalanceOrders(ipsKey, orderId, dataCollections);

        populateTotals(rebalanceOrderGroups, params);

        List<RebalanceOrderGroupDto> rebalanceOrderGroupDtos = filterNonCashIfFullRedemption(rebalanceOrderGroups);

        return rebalanceOrderGroupDtos;
    }

    private List<RebalanceOrderGroupDto> getRebalanceOrders(IpsKey ipsKey, String orderId, Map<String, Object> dataCollections) {
        synchronized (dataCollections) {
            List<RebalanceOrderGroupDto> rebalanceOrderGroups = (List<RebalanceOrderGroupDto>) dataCollections
                    .get(REBALANCE_ORDERS_KEY);
            if (rebalanceOrderGroups == null) {
                rebalanceOrderGroups = rebalanceOrdersService.findByDocIds(ipsKey,
                        Collections.singletonList(orderId), new FailFastErrorsImpl()).getOrderGroups();
                dataCollections.put(REBALANCE_ORDERS_KEY, rebalanceOrderGroups);
            }
            return rebalanceOrderGroups;
        }

    }

    private List<RebalanceOrderGroupDto> filterNonCashIfFullRedemption(List<RebalanceOrderGroupDto> rebalanceOrderGroups) {
        RebalanceOrderGroupDto rebalanceOrderGroup = rebalanceOrderGroups.get(0);
        List<RebalanceOrderDetailsDto> rebalanceOrders = rebalanceOrderGroup.getOrderDetails();
        List<RebalanceOrderDetailsDto> rebalanceOrderDetails = new ArrayList<>();
        for (int i = 0; i < rebalanceOrders.size(); i++) {
            if (!rebalanceOrders.get(i).isHideOrder()) {
                rebalanceOrderDetails.add(rebalanceOrders.get(i));
            }
        }
        RebalanceOrderGroupDto rebalanceOrderGroupDto = new RebalanceOrderGroupDto(rebalanceOrderGroup, rebalanceOrderDetails);
        return Collections.singletonList(rebalanceOrderGroupDto);
    }

    private ModelPortfolioRebalanceAccountDto getRebalanceAccount(ModelPortfolioKey ipsKey, AccountKey accountKey) {
        ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, rebalanceDetailsService, ipsKey).performOperation();
        ModelPortfolioRebalanceDetailDto rebalanceDetails = (ModelPortfolioRebalanceDetailDto) response.getData();

        for (ModelPortfolioRebalanceAccountDto accountDto : rebalanceDetails.getRebalanceAccounts()) {
            if (EncodedString.toPlainText(accountDto.getKey().getAccountId()).equals(
                    EncodedString.toPlainText(accountKey.getAccountId()))) {
                return accountDto;
            }
        }
        return null;
    }

    private void populateTotals(List<RebalanceOrderGroupDto> data, Map<String, Object> params) {
        BigDecimal totalBuys = BigDecimal.ZERO;
        BigDecimal totalSells = BigDecimal.ZERO;

        for (RebalanceOrderGroupDto group : data) {
            for (RebalanceOrderDetailsDto detail : group.getOrderDetails()) {
                if (detail.getOrderType() != null && detail.getOrderAmount() != null) {
                    if ("Buy".equalsIgnoreCase(detail.getOrderType())) {
                        totalBuys = totalBuys.add(detail.getOrderAmount());
                    } else if ("Sell".equalsIgnoreCase(detail.getOrderType())) {
                        totalSells = totalSells.add(detail.getOrderAmount());
                    }
                }
            }
        }

        params.put("totalBuys", totalBuys);
        params.put("totalSells", totalSells);
    }

    @ReportBean("modelValue")
    public BigDecimal getModelValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        ModelPortfolioKey modelPortfolioKey = new ModelPortfolioKey((String) params.get(PARAM_IPS_ID));
        IpsKey ipsKey = IpsKey.valueOf((String) params.get(PARAM_IPS_ID));
        String orderId = (String) params.get(PARAM_ORDER_ID);
        AccountKey accountKey = new AccountKey((String) params.get(PARAM_ACCOUNT_ID));

        List<RebalanceOrderGroupDto> rebalanceOrderGroups = getRebalanceOrders(ipsKey, orderId, dataCollections);

        Boolean isFullRedemption = getIsFullRedemption(rebalanceOrderGroups);
        if (isFullRedemption) {
            return BigDecimal.ZERO;
        } else {
            ModelPortfolioRebalanceAccountDto account = getRebalanceAccount(modelPortfolioKey, accountKey);
            if (account != null) {
                return account.getModelValue();
            }
            return null;
        }
    }

    private Boolean getIsFullRedemption(List<RebalanceOrderGroupDto> rebalanceOrderGroups) {
        for (RebalanceOrderGroupDto rebalanceOrderGroupDto : rebalanceOrderGroups) {
            for (RebalanceOrderDetailsDto rebalanceOrderDetail : rebalanceOrderGroupDto.getOrderDetails()) {
                if (rebalanceOrderDetail.getIsFullModelRedemption().equals(true)) {
                    return true;
                }
            }
        }
        return false;
    }

    @ReportBean("totalBuys")
    public BigDecimal getTotalBuys(Map<String, Object> params) {
        return (BigDecimal) params.get("totalBuys");
    }

    @ReportBean("totalSells")
    public BigDecimal getTotalSells(Map<String, Object> params) {
        return (BigDecimal) params.get("totalSells");
    }

    @ReportBean("reportType")
    public String getReportName(Map<String, Object> params) {
        return "Rebalance orders";
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params) {
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        return contentService.find(key, new FailFastErrorsImpl()).getContent();
    }

    @ReportImage("iconBuy")
    public Renderable getIconBuy(Map<String, Object> params) {
        return generateIconRenderer("iconBuy");
    }

    @ReportImage("iconSell")
    public Renderable getIconSell(Map<String, Object> params) {
        return generateIconRenderer("iconSell");
    }

    private Renderable generateIconRenderer(final String iconName) {
        String imageLocation = cmsService.getContent(iconName);
        return getVectorImage(imageLocation);
    }



}
