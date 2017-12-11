package com.bt.nextgen.reports.modelportfolio.orderstatus;

import com.bt.nextgen.api.modelportfolio.v2.model.orderstatus.ModelOrderDetailsDto;
import com.bt.nextgen.api.modelportfolio.v2.service.orderstatus.ModelOrderSummaryDtoService;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.MultiDataReport;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@MultiDataReport("modelOrderSummaryCsvReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
@SuppressWarnings("squid:S1172") // Unused params required by api
public class ModelOrderSummaryCsvReport extends BaseReport {

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private ModelOrderSummaryDtoService orderSummaryService;

    private static final DateTimeFormatter filenameDateFormat = DateTimeFormat.forPattern("ddMMyyyy");
    private static final String ORDER_STATUS_REPORT = "OrderStatus";
    private static final String EFFECTIVE_DATE = "effective-date";
    private static final String UNDERSCORE = "_";
    private static final String DISCLAIMER_CONTENT = "Ins-IP-0341";
    private static final String HEADER_TEXT = "Ins-IP-0334";



    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {

        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria(EFFECTIVE_DATE, SearchOperation.EQUALS, (String) params.get(EFFECTIVE_DATE),
                OperationType.STRING));
        
        ApiResponse response = new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, orderSummaryService, criteria)
                .performOperation();
        ResultListDto<ModelOrderDetailsDto> dtoList = (ResultListDto<ModelOrderDetailsDto>) response.getData();

        ModelOrderReportData orderData = new ModelOrderReportData(DateTime.parse((String) params.get(EFFECTIVE_DATE)),
                dtoList.getResultList());
        return Collections.singletonList(orderData);
    }


    @Override
    public String getReportFileName(Collection<?> data) {
        Iterator<?> dataIterator = data.iterator();
        if (dataIterator.hasNext()) {
            ModelOrderReportData dto = (ModelOrderReportData) dataIterator.next();
            return ORDER_STATUS_REPORT + UNDERSCORE + filenameDateFormat.print(dto.getReportDate());
        }
        return ORDER_STATUS_REPORT;
    }

    @Override
    public Collection<String> getReportPageNames(Collection<?> data) {
        return Collections.singletonList("pages");
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
        String rebDate = formatter.print(DateTime.parse((String) params.get(EFFECTIVE_DATE)));

        return cmsService.getDynamicContent(DISCLAIMER_CONTENT, new String[] { rebDate });
    }

    @ReportBean("headerText")
    public String getHeaderText(Map<String, Object> params) {

        ContentKey key = new ContentKey(HEADER_TEXT);
        return contentService.find(key, new FailFastErrorsImpl()).getContent();
    }

    @ReportBean("rebalanceDate")
    public String getRebalanceDate(Map<String, Object> params) {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, DateTime.parse((String) params.get(EFFECTIVE_DATE)));
    }
}
