package com.bt.nextgen.reports.account.transfer;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.portfolio.v3.service.valuation.ValuationDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Report("intraAccountTransferReport")
public class IntraAccountTransferReport extends AccountReportV2 {
    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper mapper;

    @Autowired
    @Qualifier("ValuationDtoServiceV3")
    private ValuationDtoService valuationService;

    @Autowired
    private ContentDtoService contentService;

    private static final String DISCLAIMER_CONTENT = "DS-IP-0028";

    private InspecieTransferDto buildTransferFromRequest(Map<String, Object> params) throws IOException {
        InspecieTransferDto xferDto = mapper.readValue((String) params.get("transferData"), InspecieTransferDtoImpl.class);
        return xferDto;
    }

    @Override
    public List<Object> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        try {
            InspecieTransferDto dto = buildTransferFromRequest(params);
            List<Object> result = new ArrayList<>();
            result.add(new TransferGroupReportData(dto, params));

            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse request", e);
        }
    }

    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "";
    }

    @Override
    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "";
    }

    @Override
    @ReportBean("reportTitle")
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "Intra account transfer receipt";
    }

    @Override
    public String getReportFileName(Collection<?> data) {
        return "intraAccountTransferReport";
    }

    @ReportBean("reportSubTitle")
    public String getReportSubTitle() {
        return "Intra account transfer has been submitted";
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

}