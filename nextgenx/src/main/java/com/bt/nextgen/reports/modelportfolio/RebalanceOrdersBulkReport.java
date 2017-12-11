package com.bt.nextgen.reports.modelportfolio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrderGroupDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.RebalanceOrdersDto;
import com.bt.nextgen.api.modelportfolio.v2.service.rebalance.RebalanceOrdersDtoService;
import com.bt.nextgen.api.modelportfolio.v2.util.rebalance.RebalanceOrdersSortingHelper;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.stereotype.MultiDataReport;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.integration.ips.IpsKey;

@MultiDataReport("rebalanceOrdersBulkReport")
@SuppressWarnings("squid:S1172") // Unused params required by api
public class RebalanceOrdersBulkReport extends BaseReport {

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private RebalanceOrdersDtoService rebalanceOrdersService;

    @Autowired
    private RebalanceOrdersSortingHelper sortingHelper;

    private static final String PARAM_IPS_ID = "ips";
    private static final String REBAL = "REBAL_";
    private static final String UNDERSCORE = "_";
    private static final DateTimeFormatter filenameDateFormat = DateTimeFormat.forPattern("ddMMyyyy");
    private static final String DISCLAIMER_CONTENT = "DS-IP-0111";

    private List<RebalanceOrderGroupDto> getRebalanceOrderGroups(IpsKey ipsKey) {
        ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, rebalanceOrdersService, ipsKey)
                .performOperation();
        RebalanceOrdersDto rebalanceOrders = (RebalanceOrdersDto) response.getData();
        return rebalanceOrders.getOrderGroups();
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        IpsKey ipsKey = IpsKey.valueOf((String) params.get(PARAM_IPS_ID));
        List<RebalanceOrderGroupDto> orderGroupDtoList = getRebalanceOrderGroups(ipsKey);
        List<RebalanceOrderGroupDto> rebalanceOrderGroups = filterNonCashIfFullRedemption(orderGroupDtoList);
        sortingHelper.detailedSort(rebalanceOrderGroups);
        return rebalanceOrderGroups;
    }

    private List<RebalanceOrderGroupDto> filterNonCashIfFullRedemption(List<RebalanceOrderGroupDto> rebalanceOrderGroups) {
        List<RebalanceOrderGroupDto> rebalanceOrderGroupDtos = new ArrayList<>();
        for(RebalanceOrderGroupDto rebalanceOrderGroup: rebalanceOrderGroups){
            List<RebalanceOrderDetailsDto> rebalanceOrders = rebalanceOrderGroup.getOrderDetails();
            List<RebalanceOrderDetailsDto> rebalanceOrderDetails = new ArrayList<>();
            for (int i = 0; i < rebalanceOrders.size(); i++) {
                if (!rebalanceOrders.get(i).isHideOrder()) {
                    rebalanceOrderDetails.add(rebalanceOrders.get(i));
                }
            }
            RebalanceOrderGroupDto rebalanceOrderGroupDto = new RebalanceOrderGroupDto(rebalanceOrderGroup, rebalanceOrderDetails);
            rebalanceOrderGroupDtos.add(rebalanceOrderGroupDto);
        }
        return rebalanceOrderGroupDtos;
    }



    @Override
    public Collection<String> getReportPageNames(Collection<?> data) {
        Iterator<?> dataIterator = data.iterator();
        List<String> pageNames = new ArrayList<>();

        while (dataIterator.hasNext()) {
            RebalanceOrderGroupDto dto = (RebalanceOrderGroupDto) dataIterator.next();
            String pageName = REBAL + dto.getModelSymbol() + UNDERSCORE + dto.getAdviserNumber() + UNDERSCORE
                    + filenameDateFormat.print(dto.getRebalanceDate());
            pageNames.add(pageName);
        }
        return pageNames;
    }

    @Override
    public String getReportFileName(Collection<?> data) {
        Iterator<?> dataIterator = data.iterator();
        if (dataIterator.hasNext()) {
            RebalanceOrderGroupDto dto = (RebalanceOrderGroupDto) dataIterator.next();
            return REBAL + dto.getModelSymbol() + UNDERSCORE + filenameDateFormat.print(dto.getRebalanceDate());
        }
        return null;
    }

    @Override
    public int getThreadPoolSize() {
        return 5;
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params) {
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        return contentService.find(key, new FailFastErrorsImpl()).getContent();
    }
}
