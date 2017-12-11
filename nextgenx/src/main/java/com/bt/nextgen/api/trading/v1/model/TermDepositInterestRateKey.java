package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**

 * Created by L069552 on 3/08/17.
 */
public class TermDepositInterestRateKey {
    private ProductKey whiteLabelProductKey;
    private BrokerKey dealerGroupKey;
    private AccountStructureType accountStructureType;
    private DateTime bankDate;

    public TermDepositInterestRateKey(ProductKey productKey,BrokerKey brokerKey,AccountStructureType accountStructureType,DateTime startDate){
        this.whiteLabelProductKey = productKey;
        this.dealerGroupKey = brokerKey;
        this.accountStructureType = accountStructureType;
        this.bankDate = startDate;
    }

    public ProductKey getWhiteLabelProductKey() {
        return whiteLabelProductKey;
    }

    public BrokerKey getDealerGroupKey() {
        return dealerGroupKey;
    }

    public AccountStructureType getAccountStructureType() {
        return accountStructureType;
    }

    public DateTime getBankDate() {
        return bankDate;
    }
}
