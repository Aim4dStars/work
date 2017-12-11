package com.bt.nextgen.reports.account.fees.taxinvoice;

import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDto;
import com.bt.nextgen.api.fees.v2.service.TaxInvoiceDtoService;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Report("taxInvoicePmfForm")
public class TaxInvoicePmfForm extends AbstractTaxInvoiceAuthorisationForm {
    private static final Logger logger = LoggerFactory.getLogger(TaxInvoicePmfForm.class);

    @Autowired
    private TaxInvoiceDtoService taxInvoiceDtoService;

    private static final String TAX_INVOICE_DATA_KEY = "TaxInvoicePmfForm.taxInvoiceData.";
    private static final String TITLE_SUFFIX = "- portfolio management fee";

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        List<TaxInvoiceAuthorisationData> data = (List<TaxInvoiceAuthorisationData>) getData(params, dataCollections);
        String title = data.get(0).getReportTitle();
        return title + TITLE_SUFFIX;
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);
        String month = (String) params.get(MONTH);
        String year = (String) params.get(YEAR);

        DateRangeAccountKey key = new DateRangeAccountKey(accountId, getStartDate(month, year), getEndDate(month, year));

        TaxInvoiceDto dto = getTaxInvoiceData(key, dataCollections);
        TaxInvoiceAuthorisationData reportData = new TaxInvoiceAuthorisationData(dto);
        return Collections.singletonList(reportData);
    }

    private DateTime getStartDate(String month, String year) {
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.DATE, 1);
        startDate.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        startDate.set(Calendar.YEAR, Integer.parseInt(year));
        return new DateTime(startDate.getTime());
    }

    private DateTime getEndDate(String month, String year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        return new DateTime(cal.getTime());
    }

    private TaxInvoiceDto getTaxInvoiceData(DateRangeAccountKey key, Map<String, Object> dataCollections) {
        String cacheKey = TAX_INVOICE_DATA_KEY + key.getAccountId();
        synchronized (dataCollections) {
            TaxInvoiceDto taxInvoiceDto = (TaxInvoiceDto) dataCollections.get(cacheKey);
            if (taxInvoiceDto == null) {
                ServiceErrors serviceErrors = new FailFastErrorsImpl();
                taxInvoiceDto = taxInvoiceDtoService.find(key, serviceErrors);
                dataCollections.put(cacheKey, taxInvoiceDto);
                if (serviceErrors.hasErrors()) {
                    logger.error("Errors during creation of tax invoice pmf report: {}", serviceErrors.getErrorList());
                }
            }
            return taxInvoiceDto;
        }
    }
}