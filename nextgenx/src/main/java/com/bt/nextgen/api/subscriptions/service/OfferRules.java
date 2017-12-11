package com.bt.nextgen.api.subscriptions.service;

import ch.lambdaj.function.closure.Switcher;
import ch.lambdaj.function.matcher.Predicate;
import com.bt.nextgen.api.subscriptions.model.Offer;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import static ch.lambdaj.Lambda.closure;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.var;
import static com.bt.nextgen.api.subscriptions.util.Converters.convertAccountId;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * This class implements any rules for offers.
 */
@Component
public class OfferRules {

    private static final Logger logger = LoggerFactory.getLogger(OfferRules.class);

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private ProductIntegrationService productService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    private Switcher<Predicate> switzh;

    /**
     * Default constructor
     */
    public OfferRules() {

    }

    public Predicate getPredicate(Offer service, Object... objects) {
        if (switzh == null) {
            switzh = new Switcher<>();
            switzh.addCase(Subscriptions.FA, closure().of(this, "fundAdminPredicate", var(WrapAccountIdentifier.class)));
            switzh.setDefault(closure().of(this, "defaultPredicate", var(WrapAccountIdentifier.class)));
        }
        return switzh.exec(service.getType(), objects);
    }

    /**
     * Rules for fund admin, Rally user story id :
     * It's only available to SMSF account
     *
     * @param key
     * @return return Predicate
     */
    public Predicate fundAdminPredicate(final WrapAccountIdentifier key) {
        return new Predicate<Offer>() {
            @Override
            public boolean apply(Offer offer) {
                WrapAccount account = accountService
                        .loadWrapAccountWithoutContainers(convertAccountId(key.getAccountIdentifier()), new ServiceErrorsImpl());
                if (null!= account && (AccountStructureType.SMSF.equals(account.getAccountStructureType()))) {
                    Broker broker = brokerIntegrationService.getBroker(account.getAdviserPositionId(), new ServiceErrorsImpl());
                    List<Product> products = productService.getSubscriptionProducts(broker.getDealerKey(), new ServiceErrorsImpl());
                    List<Product> faProducts = select(products,
                            having(on(Product.class).getProductName(),
                                    equalTo(Subscriptions.FA.getProductType())));//TODO: include another product name
                    if (!faProducts.isEmpty())
                        return true;
                }
                return false;
            }
        };
    }

    /**
     * Default predicate always return false.
     *
     * @return
     */
    public Predicate defaultPredicate(final WrapAccountIdentifier key) {
        return new Predicate<Offer>() {
            @Override
            public boolean apply(Offer item) {
                logger.info("defaultPredicator", key.getAccountIdentifier());
                return false;
            }
        };
    }
}