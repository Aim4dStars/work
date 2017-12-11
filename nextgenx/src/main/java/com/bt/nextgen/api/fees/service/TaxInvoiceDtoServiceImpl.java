package com.bt.nextgen.api.fees.service;

import com.bt.nextgen.api.fees.model.TaxInvoiceDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.taxinvoice.TaxInvoiceRequestImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.taxinvoice.DealerGroupInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxAdviserDetails;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceIntegrationService;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaxInvoiceDtoServiceImpl implements TaxInvoiceDtoService {

    @Autowired
    private TaxInvoiceIntegrationService taxInvoiceService;

    @Override
    public List<TaxInvoiceDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {

        String accId = null;
        String month = null;
        String year = null;
        for (ApiSearchCriteria parameter : criteriaList) {
            if (Attribute.ACCOUNT_ID.equals(parameter.getProperty())) {
                accId = new EncodedString(parameter.getValue()).plainText();
            }
            if (Attribute.MONTH.equals(parameter.getProperty())) {
                month = parameter.getValue();
            }
            if (Attribute.YEAR.equals(parameter.getProperty())) {
                year = parameter.getValue();
            }
        }

        List<TaxInvoiceDto> dtoList = getTaxInvoiceDetails(accId.toString(), month, year, serviceErrors);
        return dtoList;
    }

    public List<TaxInvoiceDto> getTaxInvoiceDetails(String accId, String monthInput, String yearInput, ServiceErrors serviceErrors) {
        List<TaxInvoiceDto> dtoList = new ArrayList<TaxInvoiceDto>();
        TaxInvoiceRequestImpl request = new TaxInvoiceRequestImpl();

        // String accountId = new EncodedString(accId).plainText();
        WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
        identifier.setBpId(accId);

        int month = Integer.parseInt(monthInput);
        int year = Integer.parseInt(yearInput);
        getStartAndEndDate(month, year, request);

        request.setWrapAccountIdentifier(identifier);
        List<DealerGroupInvoiceDetails> detailList = taxInvoiceService.generateTaxInvoice(request, serviceErrors);

        if (CollectionUtils.isNotEmpty(detailList)) {
            for (DealerGroupInvoiceDetails dealerGroupDetails : detailList) {
                for (TaxAdviserDetails taxAdviserDetails : dealerGroupDetails.getAdviserList()) {
                    TaxInvoiceDto taxInvoiceDto = new TaxInvoiceDto();
                    taxInvoiceDto.setDealerGroupABN(dealerGroupDetails.getDealerGroupABN());
                    taxInvoiceDto.setDealerGroupName(dealerGroupDetails.getDealerGroupName());
                    taxInvoiceDto.setAdviserDetails(taxAdviserDetails);
                    taxInvoiceDto.setStartDate(ApiFormatter.asShortDate(request.getStartDate()));
                    taxInvoiceDto.setEndDate(ApiFormatter.asShortDate(request.getEndDate()));
                    if ((taxAdviserDetails.getTotalfeeIncludingGST().compareTo(BigDecimal.ZERO)) > 0) {
                        taxInvoiceDto.setReportTypeTitle("Adjustment note");
                    } else {
                        taxInvoiceDto.setReportTypeTitle("Tax invoice");
                    }
                    dtoList.add(taxInvoiceDto);
                }
            }
        }
        return dtoList;
    }

    public void getStartAndEndDate(int month, int year, TaxInvoiceRequestImpl request) {
        DateTime startDate = new DateTime(year, month, 1, 0, 0, 0, 0);
        DateTime lastDate = startDate.dayOfMonth().withMaximumValue();

        request.setEndDate(lastDate);
        request.setStartDate(startDate);
    }

}