package com.bt.nextgen.api.fees.v2.model;

import com.bt.nextgen.service.integration.CurrencyType;
import org.joda.time.DateTime;

public class TaxInvoiceDetailsDto {

    private DateTime feeDate;

    private String description;

    private FeeDto feeDto;

    private CurrencyType currency;

    private boolean reversalFlag;

    private String ipsId;

    private String abn;

    private String investmentManagerName;

    private String ipsName;

    public TaxInvoiceDetailsDto(DateTime feeDate, String description, FeeDto feeDto, CurrencyType currency, boolean reversalFlag,
            String ipsId) {
        super();
        this.feeDate = feeDate;
        this.description = description;
        this.feeDto = feeDto;
        this.currency = currency;
        this.reversalFlag = reversalFlag;
        this.ipsId = ipsId;
    }

    public DateTime getFeeDate() {
        return feeDate;
    }

    public String getDescription() {
        return description;
    }

    public FeeDto getFeeDto() {
        return feeDto;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public boolean isReversalFlag() {
        return reversalFlag;
    }

    public String getIpsId() {
        return ipsId;
    }

    public String getAbn() {
        return abn;
    }

    public String getInvestmentManagerName() {
        return investmentManagerName;
    }

    public void setAbn(String abn) {
        this.abn = abn;
    }

    public void setInvestmentManagerName(String investmentManagerName) {
        this.investmentManagerName = investmentManagerName;
    }

    public String getIpsName() {
        return ipsName;
    }

    public void setIpsName(String ipsName) {
        this.ipsName = ipsName;
    }

}
