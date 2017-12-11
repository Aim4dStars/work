package com.bt.nextgen.reports.account.investmentorders.rips;

import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.reports.account.investmentorders.ordercapture.OrderItemData;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;

import java.util.ArrayList;
import java.util.List;

public class RegularInvestmentReportData {
    private RegularInvestmentDto regularInvestmentDto;
    private WrapAccountDetail account;
    private String formattedStartDate;
    private String formattedEndDate;

    public RegularInvestmentReportData(RegularInvestmentDto regularInvestmentDto, WrapAccountDetail account,
            String formattedStartDate, String formattedEndDate) {
        this.regularInvestmentDto = regularInvestmentDto;
        this.account = account;
        this.formattedStartDate = formattedStartDate;
        this.formattedEndDate = formattedEndDate;
    }

    public RegularInvestmentReportData(RegularInvestmentDto regularInvestmentDto, WrapAccountDetail account) {
        this.regularInvestmentDto = regularInvestmentDto;
        this.account = account;
    }

    public String getStatus() {
        if ("Active".equals(regularInvestmentDto.getRipStatus())) {
            return "This regular investment plan is currently active";
        }
        return "This regular investment plan is currently inactive";
    }

    public boolean getShowDepositDetails() {
        return regularInvestmentDto.getDepositDetails() == null ? false : true;
    }

    public String getDepositPayer() {
        if (regularInvestmentDto.getDepositDetails() != null) {
            StringBuilder payer = new StringBuilder();
            payer.append(regularInvestmentDto.getDepositDetails().getFromPayDto().getAccountId());
            payer.append(" ");
            payer.append(regularInvestmentDto.getDepositDetails().getFromPayDto().getAccountName());
            return payer.toString();
        }
        return null;
    }

    public String getDepositPayerCashAccount() {
        if (regularInvestmentDto.getDepositDetails() != null) {
            StringBuilder payer = new StringBuilder();
            payer.append("BSB ");
            payer.append(getFormattedBsb(regularInvestmentDto.getDepositDetails().getFromPayDto().getCode()));
            payer.append(" Account no. ");
            payer.append(regularInvestmentDto.getDepositDetails().getFromPayDto().getAccountId());
            return payer.toString();
        }
        return null;
    }

    public String getDepositDetails() {
        if (regularInvestmentDto.getDepositDetails() != null) {
            StringBuilder deposit = new StringBuilder();
            deposit.append("Deposit <b>");
            deposit.append(ReportFormatter.format(ReportFormat.CURRENCY, regularInvestmentDto.getDepositDetails().getAmount()));
            deposit.append("</b> starting <b>");
            deposit.append(regularInvestmentDto.getDepositDetails().getTransactionDate());
            deposit.append("</b>, ");
            deposit.append(regularInvestmentDto.getDepositDetails().getFrequency().toLowerCase());
            return deposit.toString();
        }
        return null;
    }

    public String getDepositPayee() {
        if (regularInvestmentDto.getDepositDetails() != null) {
            StringBuilder payee = new StringBuilder();
            payee.append(regularInvestmentDto.getDepositDetails().getToPayeeDto().getAccountName());
            payee.append(" investment account ");
            payee.append(regularInvestmentDto.getDepositDetails().getToPayeeDto().getAccountId());
            return payee.toString();
        }
        return null;
    }

    public String getDepositPayeeCashAccount() {
        if (regularInvestmentDto.getDepositDetails() != null) {
            StringBuilder payee = new StringBuilder();
            payee.append("BSB ");
            payee.append(getFormattedBsb(regularInvestmentDto.getCashAccountDto().getBsb()));
            payee.append(" Account no. ");
            payee.append(regularInvestmentDto.getCashAccountDto().getAccountNumber());
            return payee.toString();
        }
        return null;
    }

    public boolean getShowDateRange() {
        return regularInvestmentDto.getInvestmentEndDate() == null ? false : true;
    }

    public String getInvestmentDateRange() {
        String startDate = formattedStartDate != null ? formattedStartDate : ReportFormatter.format(ReportFormat.SHORT_DATE,
                regularInvestmentDto.getInvestmentStartDate());
        String endDate = formattedEndDate != null ? formattedEndDate : ReportFormatter.format(ReportFormat.SHORT_DATE,
                regularInvestmentDto.getInvestmentEndDate());

        StringBuilder range = new StringBuilder();
        range.append("From ");
        range.append(startDate);
        range.append(" to ");
        range.append(endDate);
        return range.toString();
    }

    public String getInvestmentAccount() {
        StringBuilder details = new StringBuilder();
        details.append(account.getAccountName());
        details.append(" ");
        details.append(account.getAccountNumber());
        return details.toString();
    }

    public String getInvestmentCashAccount() {
        StringBuilder cash = new StringBuilder();
        cash.append("BSB ");
        cash.append(getFormattedBsb(regularInvestmentDto.getCashAccountDto().getBsb()));
        cash.append(" Account no. ");
        cash.append(regularInvestmentDto.getCashAccountDto().getAccountNumber());
        return cash.toString();
    }

    public String getInvestmentDetails() {
        String startDate = formattedStartDate != null ? formattedStartDate : ReportFormatter.format(ReportFormat.SHORT_DATE,
                regularInvestmentDto.getInvestmentStartDate());

        StringBuilder deposit = new StringBuilder();
        deposit.append("Invest <b>");
        deposit.append(ReportFormatter.format(ReportFormat.CURRENCY, regularInvestmentDto.getInvestmentAmount()));
        deposit.append("</b> starting <b>");
        deposit.append(startDate);
        deposit.append("</b>, ");
        deposit.append(regularInvestmentDto.getFrequency().toLowerCase());
        return deposit.toString();
    }

    public List<OrderItemData> getOrders() {
        List<OrderItemData> orderItems = new ArrayList<>();
        for (OrderItemDto orderItem : regularInvestmentDto.getOrders()) {
            orderItems.add(new OrderItemData(regularInvestmentDto, orderItem));
        }
        return orderItems;
    }

    public String getInvestmentAmount() {
        return ReportFormatter.format(ReportFormat.CURRENCY, regularInvestmentDto.getInvestmentAmount());
    }
    
    private String getFormattedBsb(String bsb) {
        if (bsb != null && bsb.length() == 6) {
            return bsb.substring(0, 3) + "-" + bsb.substring(3);
        }
        return bsb;
    }
}
