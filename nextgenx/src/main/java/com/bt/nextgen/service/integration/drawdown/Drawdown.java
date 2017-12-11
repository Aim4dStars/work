package com.bt.nextgen.service.integration.drawdown;

import com.bt.nextgen.service.integration.account.AccountKey;

@Deprecated
public interface Drawdown {

    public AccountKey getAccountKey();

    public DrawdownOption getDrawdownOption();

}
