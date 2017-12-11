package com.bt.nextgen.reports.account.transactions;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

public class CashTransactionsData {
    private CashTransactionHistoryDto dto;

    public CashTransactionsData(CashTransactionHistoryDto dto) {
        this.dto = dto;
    }

    public String getDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, dto.getValDate());
    }

    public String getDescription() {
        StringBuilder description = new StringBuilder(dto.getDescriptionFirst());
        if (dto.getDescriptionSecond() != null) {
            description.append("<br/>");
            description.append(dto.getDescriptionSecond());
        }
        return description.toString();
    }

    public String getCredit() {
        StringBuilder builder = new StringBuilder();
        if (dto.getNetAmount().compareTo(BigDecimal.ZERO) >= 0) {
            builder.append(ReportFormatter.format(ReportFormat.CURRENCY, dto.getNetAmount()));
            if (!dto.getCleared()) {
                builder.append("<br/><font color=\"#ea6d00\">Uncleared</font>");
            }
        }
        return builder.toString();
    }

    public String getDebit() {
        if (dto.getNetAmount().compareTo(BigDecimal.ZERO) >= 0) {
            return "";
        }
        return ReportFormatter.format(ReportFormat.CURRENCY, dto.getNetAmount().abs());
    }

    public String getBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, dto.getBalance());
    }

    public Boolean getDetailed() {
        return !dto.getSystemTransaction();
    }

    private boolean isInflow() {
        return dto.getNetAmount().compareTo(BigDecimal.ZERO) >= 0;
    }

    public String getDetailedDescription() {
        if (isInflow()) {
            return getPayer();
        } else {
            return getPayee();
        }
    }

    public String getDirection() {
        if (isInflow()) {
            return "From";
        } else {
            return "To";
        }
    }

    private String getPayer() {
        StringBuilder source = new StringBuilder();
        if (StringUtils.isNotBlank(dto.getPayerName())) {
            source.append(dto.getPayerName());
            source.append(" • ");
        }
        source.append("BSB ");
        source.append(dto.getPayerBsb());
        source.append(" Account no. ");
        source.append(dto.getPayerAccount());
        return source.toString();
    }


    private String getPayee() {
        StringBuilder source = new StringBuilder();
        if (StringUtils.isNotBlank(dto.getPayerName())) {
            source.append(dto.getPayeeName());
            source.append(" • ");
        }
        if ("pay.pay_bpay".equals(dto.getOrderType())) {
            source.append("Biller code ");
            source.append(dto.getPayeeBillerCode());
            source.append(" CRN ");
            source.append(dto.getPayeeCustrRef());
        } else {
            source.append("BSB ");
            source.append(dto.getPayeeBsb());
            source.append(" Account no. ");
            source.append(dto.getPayeeAccount());
        }
        return source.toString();
    }

    public String getReceipt() {
        return dto.getDocId();
    }
}