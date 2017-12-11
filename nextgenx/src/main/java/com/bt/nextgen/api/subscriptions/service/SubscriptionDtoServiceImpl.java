package com.bt.nextgen.api.subscriptions.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.subscriptions.model.Offer;
import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.repository.SubscriptionStatus;
import com.bt.nextgen.core.repository.SubscriptionsRepository;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.additionalservices.AdditionalServicesIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static com.bt.nextgen.api.subscriptions.util.Converters.appIdCovnerter;
import static com.bt.nextgen.api.subscriptions.util.Converters.convertAccountId;
import static com.bt.nextgen.api.subscriptions.util.Converters.dtoConverterFromOffer;
import static com.bt.nextgen.api.subscriptions.util.Converters.setWorkFlow;
import static com.bt.nextgen.api.subscriptions.util.Converters.toSubscriptionDetail;
import static com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus.DISCARDED;
import static com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus.DONE;
import static com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus.RUN_CANCEL;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;


@Service
@Transactional(value = "springJpaTransactionManager")
public class SubscriptionDtoServiceImpl implements SubscriptionDtoService {

    @Autowired
    private SubscriptionsRepository repository;

    @Autowired
    private AdditionalServicesIntegrationService subscriptionService;

    @Autowired
    private OfferRules rules;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    @Override
    public List<SubscriptionDto> search(AccountKey key, ServiceErrors serviceErrors) {
        return getSubscriptions(key, serviceErrors);
    }

    @Override
    public List<SubscriptionDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        SubscriptionFilterUtil filterUtil = new SubscriptionFilterUtil(findAll(serviceErrors), criteriaList);
        return filterUtil.filter();
    }

    @Override
    public SubscriptionDto find(AccountKey key, ServiceErrors serviceErrors) {
        List<SubscriptionDto> subscriptions = getSubscriptions(key, serviceErrors);
        if (subscriptions != null && !subscriptions.isEmpty()) {
            return subscriptions.get(0);
        }
        return null;
    }

    @Override
    public List<SubscriptionDto> findAll(ServiceErrors serviceErrors) {
        List<String> accountIds = getAccountIds(serviceErrors);
        List<String> applicationNumbers = null;
        if (CollectionUtils.isNotEmpty(accountIds)) {
            applicationNumbers = repository.findAllByStatus(SubscriptionStatus.INPROGRESS, accountIds);
            if (applicationNumbers.isEmpty()) {
                return new ArrayList<>();
            }
        }else{
            return new ArrayList<>();
        }
        List<ApplicationDocument> applications = subscriptionService.loadApplications(serviceErrors, convert(applicationNumbers, appIdCovnerter()));
        updateDatabase(applications);
        List<ApplicationDocument> inProgress = selectInProgress(applications);
        return mapOfferApplication(Subscriptions.getOffers(), inProgress, serviceErrors);
    }

    @Override
    public SubscriptionDto submit(SubscriptionDto keyedObject, ServiceErrors serviceErrors) {
        ApplicationDocument application = toSubscriptionDetail(keyedObject);
        application = subscriptionService.subscribe(serviceErrors, application);
        Offer offer = Subscriptions.getOffer(application.getOrderType());
        application.setOrderType(offer.getOderType());
        repository.save(application);
        return convertDto(application, serviceErrors);
    }


    private List<SubscriptionDto> getSubscriptions(AccountKey key, ServiceErrors serviceErrors) {
        WrapAccountIdentifier accountKey = convertAccountId(key);
        List<Offer> offers = getAvailableOffers(convertAccountId(key));
        if (offers.isEmpty())
            return new ArrayList<>();
        List<ApplicationDocument> applications = subscriptionService.loadApplications(serviceErrors, accountKey);
        updateDatabase(applications);
        List<ApplicationDocument> activeApplications = filterCancelledApplications(applications);
        List<SubscriptionDto> subscriptions = mapOfferApplication(offers, activeApplications, serviceErrors);
        subscriptions.addAll(filterUnsubsidisedOffers(offers, subscriptions, accountKey, serviceErrors));
        return subscriptions;
    }


    /**
     * Return available Subscriptions for the account. Rules are  implemented in @see OfferRules.
     * if the predicate for the offer return true method filters it.
     *
     * @param key WrapAccountIdentified (Avaloq BP_ID)
     * @return list of offers - Subscriptions available.
     */
    public List<Offer> getAvailableOffers(WrapAccountIdentifier key) {
        List<Offer> offers = Subscriptions.getOffers();
        List<Offer> availableOffers = new ArrayList<>();
        for (Offer offer : offers) {
            if (rules.getPredicate(offer, key).apply(offer)) {
                availableOffers.add(offer);
            }
        }
        return availableOffers;
    }

    /**
     * @param offers
     * @param active
     * @return
     */
    public List<SubscriptionDto> mapOfferApplication(List<Offer> offers, List<ApplicationDocument> active, ServiceErrors serviceErrors) {
        List<SubscriptionDto> subscribed = new ArrayList<>();
        for (Offer offer : offers) {
            List<ApplicationDocument> applications = selectOrderTypeApplications(active, offer.getOderType());
            for (ApplicationDocument application : applications) {
                subscribed.add(convertDto(application, serviceErrors));
            }
        }
        return subscribed;
    }

    public SubscriptionDto convertDto(ApplicationDocument application, ServiceErrors serviceErrors) {
        SubscriptionDto dto = setWorkFlow(application);
        setAccountDetail(dto, application, serviceErrors);
        return dto;
    }

    /**
     * @param dto
     * @param application
     * @param serviceErrors
     */
    public void setAccountDetail(SubscriptionDto dto, ApplicationDocument application, ServiceErrors serviceErrors) {
        setAccountDetail(dto, application.getBpid().getId(), serviceErrors);
    }

    /**
     * @param dto
     * @param accountId
     * @param serviceErrors
     */
    public void setAccountDetail(SubscriptionDto dto, String accountId, ServiceErrors serviceErrors) {
        WrapAccount account = accountService.loadWrapAccountWithoutContainers(
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);
        BrokerUser broker = brokerIntegrationService.getAdviserBrokerUser(account.getAdviserPositionId(), serviceErrors);
        dto.setKey(new AccountKey(EncodedString.fromPlainText(account.getAccountKey().getId()).toString()));
        dto.setAccountName(account.getAccountName());
        dto.setAdviserFirstName(broker.getFirstName());
        dto.setAdviserLastName(broker.getLastName());
        dto.setAdviserName(broker.getLastName() + Constants.COMMA + Constants.SPACE_STRING + broker.getFirstName());
    }

    private List<String> getAccountIds(ServiceErrors serviceErrors) {
        return extract(accountService.loadWrapAccountWithoutContainers(serviceErrors).keySet(),
                on(com.bt.nextgen.service.integration.account.AccountKey.class).getId());
    }

    /**
     * Filters non cancelled applicaiton from the list
     *
     * @param subscriptions
     * @return
     */
    public List<ApplicationDocument> selectCancelledApplications(List<ApplicationDocument> subscriptions) {
        //TODO: there are other state which need to be added here
        return select(subscriptions,
                having(on(ApplicationDocument.class).getAppState(),
                        anyOf(equalTo(RUN_CANCEL),
                                equalTo(DISCARDED))));
    }

    public List<ApplicationDocument> filterCancelledApplications(List<ApplicationDocument> subscriptions) {
        //TODO: there are other state which need to be added here
        return filter(
                having(on(ApplicationDocument.class).getAppState(),
                        not(anyOf(equalTo(RUN_CANCEL),
                                equalTo(DISCARDED)))), subscriptions);
    }

    /**
     * @param subscriptions
     * @param orderType
     * @return
     */
    public List<ApplicationDocument> selectOrderTypeApplications(List<ApplicationDocument> subscriptions, String orderType) {
        return select(subscriptions,
                having(on(ApplicationDocument.class).getOrderType(), equalTo(orderType)));
    }


    public List<ApplicationDocument> selectInProgress(List<ApplicationDocument> applications) {
        return filter(
                having(on(ApplicationDocument.class).getAppState(),
                        not(anyOf(equalTo(RUN_CANCEL),
                                equalTo(DISCARDED),equalTo(DONE)))), applications);
    }

    public List<SubscriptionDto> filterUnsubsidisedOffers(List<Offer> offers, List<SubscriptionDto> subscribed, WrapAccountIdentifier accountKey, ServiceErrors serviceErrors) {
        Map<String, SubscriptionDto> types = index(subscribed, on(SubscriptionDto.class).getServiceType());
        List<SubscriptionDto> unSubscribed = new ArrayList<>();
        for (Offer offer : offers) {
            if (!types.keySet().contains(offer.getType().name())) {
                SubscriptionDto dto = dtoConverterFromOffer().convert(offer);
                setAccountDetail(dto,accountKey.getAccountIdentifier(), serviceErrors);
                dto.setStatus("Unsubscribed");
                unSubscribed.add(dto);
            }
        }
        return unSubscribed;
    }

    private void updateDatabase(List<ApplicationDocument> subscriptions) {
        SubscriptionDtoServiceImplRunnable.updateDatabase(subscriptions, repository);
    }
}