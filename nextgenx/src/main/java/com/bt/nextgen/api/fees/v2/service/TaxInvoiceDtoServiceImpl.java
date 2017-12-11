package com.bt.nextgen.api.fees.v2.service;

import com.bt.nextgen.api.fees.v2.model.FeeDto;
import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDetailsDto;
import com.bt.nextgen.api.fees.v2.model.TaxInvoiceDto;
import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.taxinvoice.InvstMngrInvoiceDetails;
import com.bt.nextgen.service.integration.taxinvoice.IpsInvoice;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoiceData;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoicePMF;
import com.bt.nextgen.service.integration.taxinvoice.TaxInvoicePMFIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("TaxInvoiceDtoServiceV2")
public class TaxInvoiceDtoServiceImpl implements TaxInvoiceDtoService {

    @Autowired
    private TaxInvoicePMFIntegrationService taxInvoiceService;

    @Override
    public TaxInvoiceDto find(DateRangeAccountKey key, ServiceErrors serviceErrors) {

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));

        TaxInvoicePMF taxInvoicePMF = taxInvoiceService.generateTaxInvoicePMF(accountKey, key.getStartDate(), key.getEndDate(),
                serviceErrors);

        List<TaxInvoiceDetailsDto> taxInvoiceDetailsDtoList = new ArrayList<>();

        if (taxInvoicePMF != null) {
            List<InvstMngrInvoiceDetails> invstMngrDetailsList = taxInvoicePMF.getInvstMngrInvoiceDetailsList();
            if (!invstMngrDetailsList.isEmpty()) {
                for (InvstMngrInvoiceDetails invstMngrDetails : invstMngrDetailsList) {
                    List<IpsInvoice> ipsList = invstMngrDetails.getIpsInvoiceList();
                    if (!ipsList.isEmpty()) {
                        for (IpsInvoice ips : ipsList) {
                            taxInvoiceDetailsDtoList = constructTaxInvoiceDetails(taxInvoiceDetailsDtoList, ips,
                                    invstMngrDetails.getInvstMngrName(), invstMngrDetails.getInvstMngrABN());
                        }
                    }

                }
            }

        }
        TaxInvoiceDto taxInvoiceDto = new TaxInvoiceDto(key, taxInvoiceDetailsDtoList);
        return taxInvoiceDto;
    }

    private List<TaxInvoiceDetailsDto> constructTaxInvoiceDetails(List<TaxInvoiceDetailsDto> taxInvoiceDetailsDtos,
            IpsInvoice ips, String invName, String abn) {

        List<TaxInvoiceData> taxInvoiceDetails = ips.getTaxInvoiceDetails();
        if (!taxInvoiceDetails.isEmpty()) {
            for (TaxInvoiceData taxInvoiceData : taxInvoiceDetails) {
                TaxInvoiceDetailsDto taxInvoiceDetailsDto = new TaxInvoiceDetailsDto(taxInvoiceData.getFeeDate(),
                        taxInvoiceData.getDescriptionOfSupply(), new FeeDto(taxInvoiceData.getFeeExcludingGST(),
                                taxInvoiceData.getGST(), taxInvoiceData.getFeeIncludingGST()), taxInvoiceData.getCurrency(),
                        taxInvoiceData.getReversalFlag(), ips.getIpsId());
                taxInvoiceDetailsDto.setAbn(abn);
                taxInvoiceDetailsDto.setInvestmentManagerName(invName);
                taxInvoiceDetailsDtos.add(taxInvoiceDetailsDto);
                taxInvoiceDetailsDto.setIpsName(ips.getIpsName());
            }

        }
        return taxInvoiceDetailsDtos;
    }
}
