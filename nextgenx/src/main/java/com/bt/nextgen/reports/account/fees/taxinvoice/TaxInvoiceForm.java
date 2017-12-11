package com.bt.nextgen.reports.account.fees.taxinvoice;

import com.bt.nextgen.api.fees.model.TaxInvoiceDto;
import com.bt.nextgen.api.fees.service.TaxInvoiceDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Report("taxInvoiceFormV2")
public class TaxInvoiceForm extends AbstractTaxInvoiceAuthorisationForm {

    @Autowired
    private TaxInvoiceDtoService taxInvoiceDtoService;

    private List<TaxInvoiceDto> buildFeesFromRequest(Map<String, Object> params) throws IOException {

        String accountId = (String) params.get(ACCOUNT_ID);
        String month = (String) params.get(MONTH);
        String year = (String) params.get(YEAR);

        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.MONTH, SearchOperation.EQUALS, month, OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.YEAR, SearchOperation.EQUALS, year, OperationType.STRING));

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        List<TaxInvoiceDto> resultlist = taxInvoiceDtoService.search(criteria, serviceErrors);
        return resultlist;
    }

    @Override
    public List<Object> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        try {
            List<TaxInvoiceDto> dtoList = buildFeesFromRequest(params);
            // Based on existing report, only the first taxInvoiceDto is being used for the report.
            TaxInvoiceDto dto = dtoList.get(0);

            // Process underlying tax-invoice data.
            List<TaxInvoiceData> taxInvoiceDataRptList = new ArrayList<>();
            for (com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceData taxData : dto.getAdviserDetails().getTaxInvoice()) {
                TaxInvoiceData rptData = new TaxInvoiceData(taxData.getFeeDate(), taxData.getDescriptionOfSupply(),
                        taxData.getFeeExcludingGST(), taxData.getGST(), taxData.getFeeIncludingGST());
                taxInvoiceDataRptList.add(rptData);

            }
            // Construct taxInvoiceAuthorisationData based on dto.
            TaxInvoiceAuthorisationData taxData = new TaxInvoiceAuthorisationData(dto.getStartDate(), dto.getEndDate(), dto
                    .getAdviserDetails().getTotalfeeExcludingGST(), dto.getAdviserDetails().getTotalfeeIncludingGST(), dto
                    .getAdviserDetails().getTotalGST(), taxInvoiceDataRptList);
            taxData.setDealerGroupAbn(dto.getDealerGroupABN());
            taxData.setDealerGroupName(dto.getDealerGroupName());

            List<Object> result = new ArrayList<>();
            result.add(taxData);

            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse request", e);
        }
    }

}