package com.bt.nextgen.api.policy.service;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.basil.ImageDetails;
import com.bt.nextgen.service.avaloq.client.TwoFASecuredClient;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.exception.ServiceException;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.btfin.panorama.core.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This utility class defines methods which can be used from multiple insurance screens
 * Created by M035995 on 11/10/2016.
 */
@Component
public class PolicyUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyUtility.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    /**
     * Getting the pins and password id for a particular broker
     *
     * @param brokerId      broker Id
     * @param serviceErrors object of {@link ServiceErrors}
     *
     * @return ppid for the broker.
     */
    public String getAdviserPpId(String brokerId, ServiceErrors serviceErrors) {
        if (userProfileService.isAdviser()) {
            LOGGER.info("The AdviserPpId from UserProfileService is: {}", userProfileService.getPpId());
            return userProfileService.getPpId();
        }
        else {
            BrokerKey brokerKey = null;
            if (StringUtil.isNotNullorEmpty(brokerId)) {
                brokerKey = BrokerKey.valueOf(new ConsistentEncodedString(brokerId).plainText());
            }
            else {
                final Collection<BrokerIdentifier> brokers = getBrokers(serviceErrors);
                if (CollectionUtils.isNotEmpty(brokers)) {
                    final BrokerIdentifier broker = (BrokerIdentifier) (org.apache.commons.collections.CollectionUtils.get(brokers, 0));
                    brokerKey = broker.getKey();
                }
            }

            if (brokerKey != null) {
                final BrokerUser brokerUser = brokerIntegrationService.getAdviserBrokerUser(brokerKey, serviceErrors);
                final TwoFASecuredClient clientDetail = clientIntegrationService.
                        loadGenericClientDetails(brokerUser.getClientKey(), serviceErrors);
                LOGGER.info("The AdviserPpId from ClientDetailService is: {}", clientDetail.getPpId());
                return clientDetail.getPpId();
            }
        }
        return null;
    }

    /**
     * Returns Collection of {@link Broker} for the current profile
     *
     * @param serviceErrors object of {@link ServiceErrors}
     *
     * @return Collection of {@link Broker}
     */
    public Collection<BrokerIdentifier> getBrokers(ServiceErrors serviceErrors) {
        return brokerIntegrationService.getAdvisersForUser(userProfileService.getActiveProfile(), serviceErrors);
    }

    /**
     * Create a {@link PolicyDtoConverter} for an {@code accountId}
     * @param accountId Wrap Account ID
     * @param serviceErrors service Errors to be return
     * @return policy converter
     */
    public PolicyDtoConverter getPolicyDtoConverter(String accountId, ServiceErrors serviceErrors) {
        if(!StringUtils.hasText(accountId)) {
            throw new ServiceException();
        }
        AccountKey accountKey = AccountKey.valueOf(accountId);
        WrapAccount wrapAccount = accountIntegrationService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);
        final Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(accountKey, wrapAccount);
        PolicyDtoConverter dtoConverter = new PolicyDtoConverter(accountMap);
        return dtoConverter;
    }

    public PolicyDtoConverter getPolicyDtoConverter(ServiceErrors serviceErrors) {
        final Map<AccountKey, WrapAccount> accountMap = accountIntegrationService.loadWrapAccountWithoutContainers(serviceErrors);
        PolicyDtoConverter dtoConverter = new PolicyDtoConverter(accountMap);
        return dtoConverter;
    }

    /**
     * Verify the insurance document list contains the docId of the document
     * user selected to download
     *
     * @param imageDetails
     * @param documentId
     *
     * @return boolean
     */
    public static boolean verifyDocumentExists(List<ImageDetails> imageDetails, String documentId) {
        final String decodedDocId = ConsistentEncodedString.toPlainText(documentId).toString();
        for (ImageDetails imageDetail : imageDetails) {
            if (decodedDocId.equalsIgnoreCase(imageDetail.getDocumentId())) {
                return true;
            }
        }
        return false;
    }
}
