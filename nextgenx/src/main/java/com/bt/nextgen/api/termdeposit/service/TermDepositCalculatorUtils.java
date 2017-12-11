package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorAccountKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDealerKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by l079353 on 11/07/2017.
 */

@Service
public class TermDepositCalculatorUtils {

    @Qualifier("cacheAvaloqAccountIntegrationService")
    @Autowired
    private AccountIntegrationService accountService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    private static final Logger logger = LoggerFactory.getLogger(TermDepositCalculatorUtils.class);

    public WrapAccount getAccount(final TermDepositCalculatorKey key, final ServiceErrors serviceErrors) {
        WrapAccount account = null;
        if (key instanceof TermDepositCalculatorAccountKey) {
            final AccountKey accountKey = AccountKey.valueOf(
                    EncodedString.toPlainText(((TermDepositCalculatorAccountKey) key).getAccountKey().getAccountId()));
            account = accountService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);
        }
        return account;
    }

    public BrokerKey getBrokerKey(final TermDepositCalculatorKey key, final WrapAccount account,
            final ServiceErrors serviceErrors) {
        if (key instanceof TermDepositCalculatorAccountKey) {
            return brokerHelperService.getDealerGroupForInvestor(account, serviceErrors).getDealerKey();
        } else if (key instanceof TermDepositCalculatorDealerKey) {
            return ((TermDepositCalculatorDealerKey) key).getBrokerKey();
        }
        return null;
    }

    public List<Product> getProducts(final TermDepositCalculatorKey key, final BrokerKey brokerKey,
                                     final WrapAccount account, final ServiceErrors serviceErrors) {
        final List<Product> products = new ArrayList<>();
        if (key instanceof TermDepositCalculatorAccountKey) {
            products.add(productIntegrationService.getProductDetail(account.getProductKey(), serviceErrors));
        } else if (key instanceof TermDepositCalculatorDealerKey) {
            products.addAll(productIntegrationService.getDealerGroupProductList(brokerKey, serviceErrors));
        }
        return products;
    }

    public List<Product> getProducts(final TermDepositCalculatorKey key, final BrokerKey brokerKey,
                                     final WrapAccount account, final Map<ProductKey, Product> productMap,
                                     final ServiceErrors serviceErrors) {
        final List<Product> products = new ArrayList<>();
         if (key instanceof TermDepositCalculatorAccountKey) {
            products.add(productMap.get(account.getProductKey()));
        } else if (key instanceof TermDepositCalculatorDealerKey) {
            products.addAll(productIntegrationService.getDealerGroupProductList(brokerKey, serviceErrors));
        }
        return products;
    }

    /**
     * Returns the system date if there is some error in retrieving the bank date from avaloq.
     */
    public DateTime getBankDate() {
        // TODO: Date needs to come from other service.
        DateTime bankDate = DateTime.now();
        try {
            bankDate = bankDateIntegrationService.getBankDate(new ServiceErrorsImpl());
            logger.info("Bank Date is {}", bankDate);
        } catch (final Exception ex) {
            logger.error("Error in fetching avaloq bank date: {}", ex);
        }
        return bankDate;
    }
}
