package com.bt.nextgen.reports.account.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.TermDepositValuationDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.integration.base.SystemType;
import org.joda.time.DateTime;

public class TermDepositValuationReportData extends SimpleValuationReportData {
    private String assetBrandClass;
    private String maturityInstruction;
    private DateTime maturityDate;
    private String paymentFrequency;
    private String term;

    public TermDepositValuationReportData(TermDepositValuationDto termDepositValuationDto, String thirdPartySource) {
        super(termDepositValuationDto.getName(), termDepositValuationDto.getInterestRate(), termDepositValuationDto.getBalance(),
                termDepositValuationDto.getPortfolioPercent(), termDepositValuationDto.getExternalAsset(),
                termDepositValuationDto.getSource(), thirdPartySource);
        this.maturityDate = termDepositValuationDto.getMaturityDate();
        this.maturityInstruction = termDepositValuationDto.getMaturityInstruction();
        this.paymentFrequency = termDepositValuationDto.getPaymentFrequency();
        this.term = termDepositValuationDto.getTerm();
        this.assetBrandClass = termDepositValuationDto.getAssetBrandClass();
    }

    public String getAssetBrandClass() {
        return this.assetBrandClass;
    }

    public String getMaturityInstruction() {
        return this.maturityInstruction;
    }

    public String getMaturityDetail() {
        if (SystemType.WRAP.name().equals(getThirdPartySource())) {
            return null;
        }
        else {
            return new StringBuilder("Matures on ").append(ReportFormatter.format(ReportFormat.SHORT_DATE, maturityDate)).toString();
        }
    }

    public String getTermDetail() {
        if (SystemType.WRAP.name().equals(getThirdPartySource())) {
            return null;
        }
        return new StringBuilder(term).append(" term interest ").append(paymentFrequency).toString();
    }
}
