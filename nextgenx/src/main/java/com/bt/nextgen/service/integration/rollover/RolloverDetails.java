package com.bt.nextgen.service.integration.rollover;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import org.joda.time.DateTime;

import java.util.List;

public interface RolloverDetails extends RolloverFund {

    public AccountKey getAccountKey();

    public String getRolloverId();

    public Boolean getPanInitiated();

    public DateTime getRequestDate();

    public String getAccountNumber();

    public RolloverOption getRolloverOption();

    public Boolean getIncludeInsurance();

    public String getLastTransSeqId();

    public List<TransactionValidation> getWarnings();

}
