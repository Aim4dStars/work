package com.bt.nextgen.reports.account.transfer.inspecie;

import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.integration.asset.Asset;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class InspecieAssetReportData {

    private static final String SEPARATOR = " &#183 ";

    private String assetCode;
    private String assetName;
    private BigDecimal quantity;
    private String accountNumber;
    private String custodian;
    private String srn;
    private String hin;
    private String sponsorName;

    public InspecieAssetReportData(SettlementRecordDto record, SponsorDetailsDtoImpl sponsor, Asset asset) {
        this.assetCode = asset.getAssetCode();
        this.assetName = asset.getAssetName();
        this.quantity = record.getQuantity();
        this.accountNumber = sponsor.getAccNumber();
        this.custodian = sponsor.getCustodian();
        this.srn = sponsor.getSrn();
        this.hin = sponsor.getHin();
        this.sponsorName = sponsor.getPidName();
    }

    public String getAssetTitle() {
        StringBuilder title = new StringBuilder();

        if (StringUtils.isNotBlank(assetCode)) {
            title.append("<b>");
            title.append(assetCode);
            title.append("</b>");
            title.append(SEPARATOR);
        }

        title.append(assetName);

        return title.toString();
    }

    public String getQuantity() {
        return ReportFormatter.format(ReportFormat.INTEGER, quantity);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustodian() {
        return custodian;
    }

    public String getSrn() {
        return srn;
    }

    public String getHin() {
        return hin;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public String getAccountNumberOrHin() {
        return accountNumber != null ? accountNumber : hin;
    }
}
