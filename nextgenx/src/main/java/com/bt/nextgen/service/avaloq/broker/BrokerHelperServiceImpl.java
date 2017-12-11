package com.bt.nextgen.service.avaloq.broker;

import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.search.PersonSearchRequestImpl;
import com.bt.nextgen.service.avaloq.search.PersonType;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ApplicationDocumentDetail;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.account.WrapAccountDetailResponse;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.search.PersonResponse;
import com.bt.nextgen.service.integration.search.PersonSearchIntegrationService;
import com.bt.nextgen.service.integration.search.PersonSearchRequest;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.integration.userprofile.ProfileIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * Created by L070815 on 13/01/2015.
 */
@Component
@SuppressWarnings("squid:S1200")
class BrokerHelperServiceImpl implements BrokerHelperService {

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private PersonSearchIntegrationService personSearch;

    @Autowired
    private ProfileIntegrationService profileService;

    private static final Logger logger = LoggerFactory.getLogger(BrokerHelperServiceImpl.class);

    @Resource(name = "avaloqAccountIntegrationService")
    private AccountIntegrationService avaloqAccountIntegrationService;

    @Override
    public Broker getAdviserForInvestor(WrapAccount account, ServiceErrors serviceErrors) {

        if (account != null) {
            Broker adviserPosition = brokerIntegrationService.getBroker(account.getAdviserPositionId(),
                    serviceErrors);
            return adviserPosition;
        }

        serviceErrors.addError(new ServiceErrorImpl("No Adviser defined for user "));
        return null;
    }

    private Broker getAdviserForInvestor(ApplicationDocumentDetail applicationDocument, ServiceErrors serviceErrors) {

        if(null!=applicationDocument){
            BrokerKey brokerKey = applicationDocument.getAdviserKey();
            Broker adviserPosition = brokerIntegrationService.getBroker(brokerKey, serviceErrors);
            return adviserPosition;
        }
        serviceErrors.addError(new ServiceErrorImpl("No Adviser defined for user "));
        return null;
    }

    @Override
    public Broker getDealerGroupForInvestor(WrapAccount account, ServiceErrors serviceErrors) {
        Broker adviser = getAdviserForInvestor(account, serviceErrors);
        if (null == adviser) {
            logger.error("Adviser Cannot be retrieved for that account from cache. It is fatal. Account ID {}", account.getAccountKey().getId());
            ServiceError serviceError = new ServiceErrorImpl();
            serviceError.setReason("Adviser is Null.It is a fatal Error");
            serviceErrors.addError(serviceError);
            return null;
        }
        Broker dealerGroup = getDealerGroup(adviser, serviceErrors);
        return dealerGroup;
    }


    @Override
    public Set<Broker> getDealerGroupsforInvestor(ServiceErrors serviceErrors) {
        Map<AccountKey, WrapAccount> accountMap = avaloqAccountIntegrationService
                .loadWrapAccountWithoutContainers(serviceErrors);
        Collection<WrapAccount> accounts = accountMap.values();
        Broker broker = null;
        Set<Broker> brokersList=new HashSet<Broker>();
        if (CollectionUtils.isNotEmpty(accounts)) {
            for (WrapAccount account : accounts) {
                broker = getDealerGroupForInvestor(account, serviceErrors);
                brokersList.add(broker);
            }
        }
        return brokersList;
    }


    @Override
    public Broker getAdviserForInvestor(BankingCustomerIdentifier customerIdentifier, ServiceErrors serviceErrors) {

        logger.warn("Refrain from using this method as multi-advisers can be associated with an Investor");

        String advPositionId = null;
        PersonSearchRequest request = new PersonSearchRequestImpl();
        request.setSearchToken(customerIdentifier.getBankReferenceKey().getId());
        request.setRoleType(Roles.ROLE_INVESTOR.name());

        List<PersonResponse> searchResult = personSearch.searchUser(request, PersonType.NATURAL_PERSON.getName(), new ServiceErrorsImpl());
        if (searchResult != null) {
            for (PersonResponse person : searchResult) {
                if (person.getGcmId().equalsIgnoreCase(customerIdentifier.getBankReferenceKey().getId())) {
                    advPositionId = person.getAdviserPersonId();
                    if (null != advPositionId) {
                        Broker adviserPosition = brokerIntegrationService.getBroker(BrokerKey.valueOf(advPositionId), serviceErrors);
                        //Returning non direct dealer group
                        if (BooleanUtils.isNotTrue(adviserPosition.isDirectInvestment())) {
                            return adviserPosition;
                        }
                    }
                }
            }
        } else {
            logger.debug("PersonSearch service did not return any result");
        }
        serviceErrors.addError(new ServiceErrorImpl("No Adviser defined for user "));
        return null;
    }

    @Override
    public List<Broker> getAdviserListForInvestor(BankingCustomerIdentifier customerIdentifier, ServiceErrors serviceErrors) {
        List<Broker> brokerList = new ArrayList<>();
        WrapAccountDetailResponse response = avaloqAccountIntegrationService.loadWrapAccountDetailByGcm(customerIdentifier,
                serviceErrors);
        if (null != response && response.getWrapAccountDetails() !=null) {
            for (WrapAccountDetail wrapAccountDetail : response.getWrapAccountDetails()) {
                BrokerKey brokerKey = wrapAccountDetail.getAdviserKey();
                Broker broker = brokerIntegrationService.getBroker(brokerKey, serviceErrors);
                brokerList.add(broker);
            }
        }
        return brokerList;
    }

    @Override
    public List<Broker> getDealerGroupForIntermediary(BankingCustomerIdentifier customerIdentifier, ServiceErrors serviceErrors) {

        List<Broker> brokerList = new ArrayList<>();
        List<JobProfile> jobProfiles = profileService.loadAvailableJobProfilesForUser(customerIdentifier, serviceErrors);

        if (null != jobProfiles) {
            for (JobProfile jobProfile : jobProfiles) {
                //TODO: Implemented for single profile. Multi profiles to be handles in R2
                Broker dealerGroup = getDealerGroupForIntermediary(jobProfile, serviceErrors);
                brokerList.add(dealerGroup);

            }
        }
        return brokerList;
    }

    private Broker getDealerGroup(Broker position, ServiceErrors serviceErrors) {
        if (null == position.getDealerKey()) {
            logger.error("Dealer Key Found Null");
            ServiceError serviceError = new ServiceErrorImpl();
            serviceError.setReason("Dealer Key Found Null. It is a fatal error.");
            serviceErrors.addError(serviceError);
            return null;
        }
        return brokerIntegrationService.getBroker(position.getDealerKey(), serviceErrors);
    }

    @Override
    public Broker getDealerGroupForIntermediary(JobProfileIdentifier userKey, ServiceErrors serviceErrors) {
        Collection<Broker> intermediaryPositions = brokerIntegrationService.getBrokersForJob(userKey,
                serviceErrors);
        Broker dealerGroup = null;
        if (intermediaryPositions != null && intermediaryPositions.size() > 0)
            dealerGroup = getDealerGroup((Broker) (CollectionUtils.get(intermediaryPositions, 0)), serviceErrors);
        return dealerGroup;
    }

    @Override
    public boolean isDirectInvestor(WrapAccount account, ServiceErrors serviceErrors) {

        Broker adviserPosition;
        adviserPosition = getAdviserForInvestor(account, new ServiceErrorsImpl());
        if (adviserPosition != null && adviserPosition.isDirectInvestment() != null)
            return adviserPosition.isDirectInvestment();

        return false;
    }

    @Override
    public UserExperience getUserExperience(WrapAccount account, ServiceErrors serviceErrors) {
        Broker adviser = getAdviserForInvestor(account, serviceErrors);
        return adviser == null ? null : adviser.getUserExperience();
    }

    @Override
    public UserExperience getUserExperience(ApplicationDocumentDetail applicationDocument, ServiceErrors serviceErrors) {
        Broker adviser = getAdviserForInvestor(applicationDocument, serviceErrors);
        return adviser == null ? null : adviser.getUserExperience();
    }

    @Override
    public Map<BrokerKey, Broker> loadBrokersByIdList(List<BrokerKey> brokerKeys, ServiceErrors serviceErrors) {
        Map<BrokerKey, Broker> brokerMap = new HashMap<>();
        for (BrokerKey brokerKey : brokerKeys) {
            Broker broker = brokerIntegrationService.getBroker(brokerKey, serviceErrors);
            brokerMap.put(broker.getKey(), broker);
        }
        return brokerMap;
    }

    @Override
    public String getBrandSiloForIntermediary(JobProfileIdentifier userKey, ServiceErrors serviceErrors) {
        Broker broker = this.getDealerGroupForIntermediary(userKey,serviceErrors);
        return getBrandSiloFromBroker(serviceErrors, broker);
    }

    private String getBrandSiloFromBroker(ServiceErrors serviceErrors, Broker broker) {
        if(StringUtils.isNotEmpty(broker.getBrandSilo())){
           return  broker.getBrandSilo();
        } else {
            Broker superDealer = this.getDealerGroup(broker,serviceErrors);
            return StringUtils.isNotEmpty(superDealer.getBrandSilo()) ? superDealer.getBrandSilo() : null;
        }
    }

    @Override
    public String getBrandSiloForInvestor(ServiceErrors serviceErrors) {
        Map<AccountKey, WrapAccount> accountMap = avaloqAccountIntegrationService
                .loadWrapAccountWithoutContainers(serviceErrors);
        Collection<WrapAccount> accounts = accountMap.values();
        Broker broker = null;
        if (CollectionUtils.isNotEmpty(accounts)) {
            for (WrapAccount account : accounts) {
                broker = this.getDealerGroupForInvestor(account,serviceErrors);
                if (null == broker.isDirectInvestment() || !broker.isDirectInvestment()) {
                    return getBrandSiloFromBroker(serviceErrors, broker);
                }
            }
        }
        return null;
    }
}
