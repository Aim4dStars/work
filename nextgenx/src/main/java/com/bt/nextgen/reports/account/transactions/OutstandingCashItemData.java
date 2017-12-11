package com.bt.nextgen.reports.account.transactions;

import org.apache.commons.lang3.StringUtils;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.OutstandingCash;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.btfin.panorama.service.integration.asset.AssetType;

public class OutstandingCashItemData {
    private OutstandingCash outstandingCash;
    private static final String SEPARATOR = " &#183 ";

    public OutstandingCashItemData(OutstandingCash outstandingCash) {
        this.outstandingCash = outstandingCash;
    }

    public String getAssetName() {
        String code = outstandingCash.getAssetCode();
        String name = outstandingCash.getAssetName();
        StringBuilder assetName = new StringBuilder();
        if (StringUtils.isNotBlank(code)) {
            assetName.append("<b>");
            assetName.append(code);
            assetName.append("</b>");
            assetName.append(SEPARATOR);
        }
        if (StringUtils.isNotBlank(name)) {
            assetName.append(name);
        }
        return assetName.toString();
    }

    public String getAssetBrandClass() {
        String assetBrandClass = null;
        if (outstandingCash.getAssetType() == AssetType.TERM_DEPOSIT) {
            assetBrandClass = outstandingCash.getTermDepositDetails().getBrandClass();
        }
        return assetBrandClass;
    }

    public String getTermDepositDetail() {
        String tdDetail = null;
        TermDepositPresentation termDepositPresentation = outstandingCash.getTermDepositDetails();
        if (outstandingCash.getAssetType() == AssetType.TERM_DEPOSIT) {
            tdDetail = new StringBuilder(termDepositPresentation.getTerm()).append(" term, interest payment ")
                    .append(termDepositPresentation.getPaymentFrequency()).toString();
        }
        return tdDetail;
    }

    public String getAssetType() {
        return outstandingCash.getAssetType().getDisplayName();
    }

    public String getMarketPrice() {
        return ReportFormatter.format(ReportFormat.CURRENCY, outstandingCash.getMarketPrice());
    }

    public String getTransactionDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, outstandingCash.getTransactionDate());
    }

    public String getQuantity() {
        AssetType assetType = outstandingCash.getAssetType();
        return assetType == AssetType.MANAGED_FUND ? ReportFormatter.format(ReportFormat.MANAGED_FUND_UNIT,
                outstandingCash.getQuantity()) : ReportFormatter.format(ReportFormat.INTEGER, outstandingCash.getQuantity());
    }

    public String getSettlementDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, outstandingCash.getSettlementDate());
    }

    public String getAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, outstandingCash.getAmount());
    }
}
