package com.bt.nextgen.reports.account.fees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.fees.model.TaxInvoiceDto;
import com.bt.nextgen.api.fees.service.TaxInvoiceDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.AccountReport;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * Java bean tax invoice report generation
 */
@Report("TaxInvoice")
public class TaxInvoiceAuthorisationForm extends AccountReport
{

	@Autowired
	private TaxInvoiceDtoService taxInvoiceDtoService;

	@ReportBean("reportType")
	public String getReportName(Map <String, String> params)
	{
		return "Tax Invoice";
	}

	public List <TaxInvoiceDto> retrieveTaxInvoiceDetails(Map <String, String> params)
	{
		String accId = params.get("account-id");
		String month = params.get("month");
		String year = params.get("year");

		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accId, OperationType.STRING));

		criteria.add(new ApiSearchCriteria(Attribute.MONTH, SearchOperation.EQUALS, month, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.YEAR, SearchOperation.EQUALS, year, OperationType.STRING));

		ApiResponse res = new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, taxInvoiceDtoService, criteria).performOperation();
		List <TaxInvoiceDto> resultlist = ((ResultListDto)res.getData()).getResultList();

		return resultlist;
	}


    @ReportBean("taxInvoiceDtosMap")
    public Map<Integer, Object> retrieveTaxInvoiceDetailsMap(Map <String, String> params) {
        List<TaxInvoiceDto> resultlist = retrieveTaxInvoiceDetails(params);
        Map<Integer, Object> reportNameMap = new HashMap<>();
        Integer count = 0;
        reportNameMap.put(count, resultlist); //Adding the taxinvoice dto to hashmap, first element
        count++;
        for (TaxInvoiceDto taxInvoiceDto : resultlist) {
            int listCount = taxInvoiceDto.getAdviserDetails().getTaxInvoice().size() / 19; //approximate one page invoices count - to set header/footer
            reportNameMap.put(count, taxInvoiceDto.getReportTypeTitle());
            count++;
            for (int i = 1; i < listCount; i++) {
                reportNameMap.put(count, taxInvoiceDto.getReportTypeTitle());
                count++;
            }
        }
        return reportNameMap;
    }

}