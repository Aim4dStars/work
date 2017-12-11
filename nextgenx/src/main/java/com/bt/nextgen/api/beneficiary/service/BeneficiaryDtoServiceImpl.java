package com.bt.nextgen.api.beneficiary.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.service.AccountDtoService;
import com.bt.nextgen.api.beneficiary.builder.BeneficiaryDtoConverter;
import com.bt.nextgen.api.beneficiary.model.BeneficiaryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetailsIntegrationServiceFactory;
import com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetailsResponseHolderImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@inheritDoc}
 * Created by M035995 on 11/07/2016.
 */
@SuppressWarnings("squid:S1200") //Single Responsibility Principle
@Service
public class BeneficiaryDtoServiceImpl implements BeneficiaryDtoService {

    private static final Logger logger = LoggerFactory.getLogger(BeneficiaryDtoServiceImpl.class);

    @Autowired
    private BeneficiaryDetailsIntegrationServiceFactory beneficiaryDetailsIntegrationServiceFactory;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private AccountDtoService accountDtoService;

    /**
     * This method retrieves {@link com.bt.nextgen.service.avaloq.beneficiary.BeneficiaryDetails} for a
     * particular account id.
     *
     * @param criteriaList  search criteria list.
     *                      Account level, beneficiary screen it should only have the account id for which beneficiary details are required.
     *                      Criteria list will be empty for an adviser login and a supportstaff with one adviser for the beneficiary report screen
     *                      Criteria list will have broker id for a support staff login with more than one adviser
     * @param serviceErrors Object of {@link ServiceErrors}
     *
     * @return List of {@link com.bt.nextgen.api.beneficiary.model.Beneficiary} to be sent back to UI.
     */
    @Override
    public List<BeneficiaryDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        logger.info("BeneficiaryDtoServiceImpl::search: method invoked");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        String mode = null;
        AccountKey accountKey = null;
        String brokerId = null;
        for (ApiSearchCriteria criteria : criteriaList) {
            if ("accountId".equalsIgnoreCase(criteria.getProperty())) {
                accountKey = new AccountKey(criteriaList.get(0).getValue());
            }
            else if ("useCache".equalsIgnoreCase(criteria.getProperty()) && "true".equalsIgnoreCase(criteria.getValue())) {
                mode = "CACHE";
            }
            else if (Attribute.BROKER_ID.equals(criteria.getProperty())) {
                brokerId = criteria.getValue();
            }
        }

        List<BeneficiaryDto> beneficiaryDtoList = new ArrayList<BeneficiaryDto>();
        if (accountKey != null) {
            beneficiaryDtoList = getBeneficiaryDetails(accountKey, serviceErrors, mode);
        }
        else {
            beneficiaryDtoList = getBeneficiaryDetails(brokerId, serviceErrors, mode);
        }

        stopWatch.stop();
        logger.info("BeneficiaryDtoServiceImpl::search: complete time taken = {} ms", stopWatch.getTime());

        return beneficiaryDtoList;
    }

    /**
     * Retrieves the beneficiary details of an Account
     * This method is invoked for account level beneficiary screen
     *
     * @param accountKey    object of {@link AccountKey}
     * @param serviceErrors object of {@link ServiceErrors}
     * @param mode          Whether the beneficiary details are from CACHE
     *
     * @return List<BeneficiaryDto>
     */
    @Override
    public List<BeneficiaryDto> getBeneficiaryDetails(AccountKey accountKey, ServiceErrors serviceErrors, String mode) {
        final BeneficiaryDetailsResponseHolderImpl beneficiaryDetailsResponse = beneficiaryDetailsIntegrationServiceFactory.getInstance(mode).getBeneficiaryDetails(accountKey, serviceErrors);
        if (beneficiaryDetailsResponse != null && CollectionUtils.isNotEmpty(beneficiaryDetailsResponse.getBeneficiaryDetailsList())) {
            BeneficiaryDto beneficiaryDto = new BeneficiaryDtoConverter().getBeneficiaryDetails(beneficiaryDetailsResponse.getBeneficiaryDetailsList().get(0));
            ArrayList<BeneficiaryDto> beneficiaryDtos = new ArrayList<>();
            beneficiaryDtos.add(beneficiaryDto);
            return beneficiaryDtos;
        }
        return new ArrayList<BeneficiaryDto>();
    }

    /**
     * Retrieves the beneficiary details of all the accounts of an adviser
     * This method is invoked for business spline - beneficiary report screen
     *
     * @param brokerId      broker Id of support staff
     * @param serviceErrors object of {@link ServiceErrors}
     * @param mode          Whether the beneficiary details are from CACHE
     *
     * @return List<BeneficiaryDto>
     */
    @Override
    public List<BeneficiaryDto> getBeneficiaryDetails(String brokerId, ServiceErrors serviceErrors, String mode) {

        logger.info("BeneficiaryDtoServiceImpl::getBeneficiaryDetails: method invoked");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<BeneficiaryDto> beneficiaryDtoList = new ArrayList<BeneficiaryDto>();

        BrokerKey brokerKey = getBrokerKey(brokerId, serviceErrors);
        final BeneficiaryDetailsResponseHolderImpl response = beneficiaryDetailsIntegrationServiceFactory.getInstance(mode)
                .getBeneficiaryDetails(brokerKey, serviceErrors);

        if (response != null && CollectionUtils.isNotEmpty(response.getBeneficiaryDetailsList())) {
            final BeneficiaryDtoConverter beneficiaryDtoConverter = new BeneficiaryDtoConverter();
            beneficiaryDtoList = beneficiaryDtoConverter.getBeneficiaryDetails(response.getBeneficiaryDetailsList(),
                    staticIntegrationService, serviceErrors);
        }

        stopWatch.stop();
        logger.info("BeneficiaryDtoServiceImpl::getBeneficiaryDetails: complete time taken = {} ms", stopWatch.getTime());

        return beneficiaryDtoList;
    }

    /**
     * Retrieves the BrokerKey of the adviser
     * -Adviser login: retrieves position Id from userprofileservice
     * -Support Staff login:
     * -mulitple adviser: broker id is passed as parameter
     * -only one adviser: retrieves position id from brokerIntegrationservice
     * (as UI can't differentiate between adviser and a paraplanner with one adviser)
     *
     * @param brokerId      - BrokerId of an adviser
     * @param brokerId
     * @param serviceErrors
     *
     * @return BrokerKey
     */
    private BrokerKey getBrokerKey(String brokerId, ServiceErrors serviceErrors) {
        logger.info("BeneficiaryDtoServiceImpl::getBrokerKey: method invoked");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        BrokerKey brokerKey = null;
        if (userProfileService.isAdviser()) { //For all Adviser
            brokerKey = BrokerKey.valueOf(userProfileService.getPositionId());
        }
        else {
            if (StringUtils.isBlank(brokerId)) { //For support staff with only one adviser
                final Collection<BrokerIdentifier> brokers = getBrokers(serviceErrors);
                if (CollectionUtils.isNotEmpty(brokers)) {
                    final BrokerIdentifier broker = (BrokerIdentifier) (CollectionUtils.get(brokers, 0));
                    brokerKey = broker.getKey();
                }
            }
            else { //For support staff with input brokerId
                brokerKey = BrokerKey.valueOf(new ConsistentEncodedString(brokerId).plainText());
            }
        }

        stopWatch.stop();
        logger.info("BeneficiaryDtoServiceImpl::getBrokerKey: complete time taken = {} ms", stopWatch.getTime());

        return brokerKey;
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

}
