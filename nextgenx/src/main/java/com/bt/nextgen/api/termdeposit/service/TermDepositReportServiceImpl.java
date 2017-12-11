package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.Badge;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorAccountKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDealerKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositRateDetails;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.termdeposit.service.ProductToAccountType;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.broker.Broker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by M044020 on 3/08/2017.
 */
@Service
public class TermDepositReportServiceImpl implements TermDepositReportService {
    private static final String DIRECT_CHANNEL = "direct";
    private static final Logger logger = LoggerFactory.getLogger(TermDepositReportServiceImpl.class);

    @Autowired
    private TermDepositRateCalculatorCsvUtils csvUtils;

    @Autowired
    private TermDepositRateCalculatorDtoService rateCalculatorDtoService;

    @Autowired
    private BrokerHelperService brokerHelperService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    public String getTermDepositRatesAsCsv(String brand, String channel, String encodedProductId, String accountId) {
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        BrokerKey brokerKey = null;
        if (brand == null) {
            throw new IllegalArgumentException("Brand is null. Brand is expected as an argument.");
        }
        if (encodedProductId == null) {
            throw new IllegalArgumentException("Product is null. Product ID is expected as an argument.");
        }

        String productId = decodeProductId(encodedProductId);
        Product product = productIntegrationService.getProductDetail(ProductKey.valueOf(productId), serviceErrors);

        if (null != channel && DIRECT_CHANNEL.equalsIgnoreCase(channel)) {
            final Collection<Broker> brokerList = brokerHelperService.getDealerGroupsforInvestor(serviceErrors);
            if (CollectionUtils.isNotEmpty(brokerList)) {
                for (final Broker brokers : brokerList) {
                    if (brokers.isDirectInvestment()) {
                        brokerKey = brokers.getDealerKey();
                        break;
                    }
                }
            }
        } else {
            brokerKey = userProfileService.getDealerGroupBroker().getDealerKey();
        }
        TermDepositCalculatorKey key;
        Set<Badge> badgeSet = new TreeSet<>();

        if (DIRECT_CHANNEL.equalsIgnoreCase(channel) || StringUtils.isNotBlank(accountId)) {
            key = new TermDepositCalculatorAccountKey(ProductKey.valueOf(productId), null, new AccountKey(accountId));
        } else {
            String accountType = product!= null ? ProductToAccountType.getDefaultAccountTypeForProduct(product.getProductName()) : null;
            key = new TermDepositCalculatorDealerKey(ProductKey.valueOf(productId), null, brokerKey,accountType);
        }
        TermDepositRateDetails termDepositRateDetails = rateCalculatorDtoService
                .getTermDepositInterestRatesWithBadges(key, badgeSet, new ServiceErrorsImpl());
        return csvUtils.getTermDepositRatesCsv(brand, termDepositRateDetails.getTermDepositInterestRates());
    }

    /**
     * Determines the decoded productId.
     *
     * The encodedProductId is provided to the client as either a consistently encoded string or a inconsistently encoded string.
     *
     * @param encodedProductId
     *            the encoded product id
     * @return the decoded productId or the original string if decoding fails.
     */
    private String decodeProductId(final String encodedProductId) {
        try {
            return ConsistentEncodedString.toPlainText(encodedProductId);
        } catch (final EncryptionOperationNotPossibleException e) {
            try {
                logger.debug("Encoded productId: {}, is not consitently encoded.", encodedProductId, e);
                return EncodedString.toPlainText(encodedProductId);
            } catch (final EncryptionOperationNotPossibleException e1) {
                logger.debug("Encoded productId: {}, is not encoded.", encodedProductId, e1);
                return encodedProductId;
            }
        }
    }

}
