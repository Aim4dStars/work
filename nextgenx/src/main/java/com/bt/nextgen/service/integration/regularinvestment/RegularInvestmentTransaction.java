package com.bt.nextgen.service.integration.regularinvestment;

import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface RegularInvestmentTransaction {

    public String getOrderGroupId();

    public DateTime getLastUpdateDate();

    public String getOwnerName();

    public String getOrderType();

    public DateTime getTransactionDate();

    public String getAccountKey();

    public String getDescription();

    public String getOwner();

    public RIPRecurringFrequency getRipFrequency();

    public BigDecimal getRipAmount();

    public DateTime getRipFirstExecDate();

    public DateTime getRipCurrExecDate();

    public DateTime getRipNextExecDate();

    public DateTime getRipLastExecDate();

    public RIPStatus getRipStatus();

    public String getRefDocId();

    public DateTime getDDFirstExecDate();

    public DateTime getDDNextExecDate();

    public DateTime getDDLastExecDate();

    public RecurringFrequency getDDFrequency();

    public BigDecimal getDDAmount();

    public String getPayerAccountId();

    public String getPayerAccountName();

    public String getPayerBSB();

    public String getRipCashAccountId();

    public RIPStatus getCurrExecStatus();
}