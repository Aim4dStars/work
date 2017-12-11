package com.bt.nextgen.service.avaloq.drawdown;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.drawdown.Drawdown;
import com.bt.nextgen.service.integration.drawdown.DrawdownOption;

@Deprecated
@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class DrawdownImpl implements Drawdown {
    public static final String XML_HEADER = "//data/";

    private AccountKey accountKey;

    @ServiceElement(xpath = XML_HEADER + "strat/val")
    private DrawdownOption drawdownOption;

    public DrawdownImpl() {

    }

    public DrawdownImpl(AccountKey accountKey, DrawdownOption drawdownOption) {
        this.accountKey = accountKey;
        this.drawdownOption = drawdownOption;
    }

    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    @Override
    public DrawdownOption getDrawdownOption() {
        return drawdownOption;
    }

    public void setDrawdownOption(DrawdownOption drawdownOption) {
        this.drawdownOption = drawdownOption;
    }

}
