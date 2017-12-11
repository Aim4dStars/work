package com.bt.nextgen.api.subscriptions.service;

import ch.lambdaj.function.matcher.Predicate;
import com.bt.nextgen.api.draftaccount.service.OrderType;
import com.bt.nextgen.api.subscriptions.model.Offer;
import com.bt.nextgen.api.subscriptions.model.SubscriptionDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.repository.SubscriptionDetails;
import com.bt.nextgen.core.repository.SubscriptionStatus;
import com.bt.nextgen.core.repository.SubscriptionsRepositoryImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.avaloq.broker.BrokerUserImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;
import com.bt.nextgen.service.integration.additionalservices.AdditionalServicesIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.user.UserKey;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by L062329 on 17/11/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionDtoServiceImplTest {

    @InjectMocks
    private SubscriptionDtoServiceImpl subscriptionDtoService;

    @Mock
    private SubscriptionsRepositoryImpl subscriptionsRepository;

    @Mock
    private AdditionalServicesIntegrationService additionalServicesIntegrationService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private OfferRules offerRules;

    @Mock
    private Offer offer;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    List<ApplicationDocument> applicationDocuments = new ArrayList<>();

    @Before
    public void setUp(){
        List<SubscriptionDetails> subscriptionsDetails = new ArrayList<>();

        Predicate pr=new Predicate() {
            @Override
            public boolean apply(Object o) {
                return true;
            }
        };

        SubscriptionDetails subscriptionsDetail1 = new SubscriptionDetails();
        subscriptionsDetail1.setDocId("123");
        subscriptionsDetail1.setAccountId("123456");
        subscriptionsDetail1.setSubscriptionType("Fund administration");
        subscriptionsDetail1.setStatus(SubscriptionStatus.INPROGRESS.name());

        SubscriptionDetails subscriptionsDetail2 = new SubscriptionDetails();
        subscriptionsDetail2.setDocId("456");
        subscriptionsDetail2.setAccountId("123456");
        subscriptionsDetail2.setSubscriptionType("Power of Attorney");
        subscriptionsDetail2.setStatus(SubscriptionStatus.INPROGRESS.name());

        SubscriptionDetails subscriptionsDetail3 = new SubscriptionDetails();
        subscriptionsDetail3.setDocId("789");
        subscriptionsDetail3.setAccountId("123456");
        subscriptionsDetail3.setSubscriptionType("Investment Options");
        subscriptionsDetail3.setStatus(SubscriptionStatus.SUBSCRIBED.name());

        subscriptionsDetails.add(subscriptionsDetail1);
        subscriptionsDetails.add(subscriptionsDetail2);
        subscriptionsDetails.add(subscriptionsDetail3);

        ApplicationDocumentImpl applicationDocument1 = new ApplicationDocumentImpl();
        applicationDocument1.setAppNumber("123");
        applicationDocument1.setOrderType(OrderType.FundAdmin.getOrderType());
        applicationDocument1.setBpid(com.bt.nextgen.service.integration.account.AccountKey.valueOf("23345"));
        applicationDocument1.setAppState(ApplicationStatus.AWAITING_DOCUMENTS);
        applicationDocument1.setBpid(AccountKey.valueOf("1234"));

        ApplicationDocumentImpl applicationDocument2 = new ApplicationDocumentImpl();
        applicationDocument2.setAppNumber("456");
        applicationDocument2.setOrderType("Power of Attorney");
        applicationDocument2.setAppState(ApplicationStatus.DONE);
        applicationDocument2.setBpid(AccountKey.valueOf("1234"));

        ApplicationDocumentImpl applicationDocument3 = new ApplicationDocumentImpl();
        applicationDocument3.setAppNumber("789");
        applicationDocument3.setOrderType("Investment Options");
        applicationDocument3.setAppState(ApplicationStatus.DONE);
        applicationDocument3.setBpid(AccountKey.valueOf("1234"));

        applicationDocuments.add(applicationDocument1);
        applicationDocuments.add(applicationDocument2);
        applicationDocuments.add(applicationDocument3);

        Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap = new HashMap<>();
        com.bt.nextgen.service.integration.account.AccountKey key = com.bt.nextgen.service.integration.account.AccountKey.valueOf("123456");
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(key);
        wrapAccount.setAdviserName("abcd");
        wrapAccount.setAccountName("efgh");
        accountMap.put(key, wrapAccount);

        List<Code> codeList = new ArrayList<>();
        codeList.add(new CodeImpl("80000009", "9", "FORM#BTFG$CUSTR: Awaiting Documents", "aw_docm"));

        BrokerUserImpl brokerUser = new BrokerUserImpl(UserKey.valueOf("236589"));
        brokerUser.setFirstName("David");
        brokerUser.setLastName("Pit");

        //Mockito.when(subscriptionsRepository.findAllByStatus(Matchers.anyString(), (List)Matchers.anyObject())).thenReturn(subscriptionsDetails);
        Mockito.when(additionalServicesIntegrationService.loadApplications((ServiceErrors) Matchers.anyObject(),(List<ApplicationIdentifier>) Matchers.anyObject())).thenReturn(applicationDocuments);
        Mockito.when(additionalServicesIntegrationService.loadApplications((ServiceErrors) Matchers.anyObject(),(WrapAccountIdentifier) Matchers.anyObject())).thenReturn(applicationDocuments);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) Matchers.anyObject())).thenReturn(accountMap);
        Mockito.when(offerRules.getPredicate((Offer) Matchers.anyObject(), Matchers.anyObject())).thenReturn(pr);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(
                (com.bt.nextgen.service.integration.account.AccountKey) Matchers.anyObject(),
                (ServiceErrorsImpl) Matchers.anyObject())).thenReturn(wrapAccount);
        Mockito.when(brokerIntegrationService.getAdviserBrokerUser((BrokerKey) Matchers.anyObject(), (ServiceErrorsImpl) Matchers.anyObject())).thenReturn(brokerUser);
    }

    @Test
    public void testFindAll() {
        String appnumber="22345";
        List<String>appnumbers=new ArrayList<>();
        appnumbers.add(appnumber);
        Mockito.when(subscriptionsRepository.findAllByStatus((SubscriptionStatus) Matchers.anyObject(),
                (List<String>)Matchers.anyObject())).thenReturn(appnumbers);
        //Mockito.when(subscriptionsRepository.update((SubscriptionDetails) Matchers.anyObject())).thenReturn(new SubscriptionDetails());
        List<SubscriptionDto> subscriptionDtos = subscriptionDtoService.findAll(new ServiceErrorsImpl());
        Assert.assertNotNull(subscriptionDtos);
        Assert.assertEquals(1, subscriptionDtos.size());
    }

    @Test
    public void testSearchWithCriteria() {
        List<ApiSearchCriteria> searchCriterias = new ArrayList<>();
        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("subscriptionType", ApiSearchCriteria.SearchOperation.EQUALS, "FA");
        searchCriterias.add(searchCriteria);
        com.bt.nextgen.api.account.v2.model.AccountKey key=new com.bt.nextgen.api.account.v2.model.AccountKey("DE96213E60E40DB87F361502DCDE817B6A20E5F066E2B89D");
        List<SubscriptionDto> subscriptionDtos = subscriptionDtoService.search(key, new ServiceErrorsImpl());
        Assert.assertNotNull(subscriptionDtos);
        Assert.assertEquals(1, subscriptionDtos.size());
    }

    @Test
    public void testSearch() {
        com.bt.nextgen.api.account.v2.model.AccountKey key = new com.bt.nextgen.api.account.v2.model.AccountKey("DE96213E60E40DB87F361502DCDE817B6A20E5F066E2B89D");
        List<SubscriptionDto> subscriptionDtos = subscriptionDtoService.search(key, new ServiceErrorsImpl());
        Assert.assertNotNull(subscriptionDtos);
        Assert.assertEquals(1, subscriptionDtos.size());
    }

    @Test
    public void testSubmit()
    {
        ApplicationDocumentImpl applicationDocument2 = new ApplicationDocumentImpl();
        applicationDocument2.setAppNumber("456");
        applicationDocument2.setOrderType("opn_new_fa_aw_docm");
        applicationDocument2.setAppState(ApplicationStatus.AWAITING_DOCUMENTS);
        applicationDocument2.setBpid(com.bt.nextgen.service.integration.account.AccountKey.valueOf("12345"));
        Mockito.when(additionalServicesIntegrationService.subscribe((ServiceErrorsImpl)Matchers.anyObject(),
                (ApplicationDocument) Matchers.anyObject())).thenReturn(applicationDocument2);
        SubscriptionDto inputDto=new SubscriptionDto();
        com.bt.nextgen.api.account.v2.model.AccountKey key = new com.bt.nextgen.api.account.v2.model.AccountKey("DE96213E60E40DB87F361502DCDE817B6A20E5F066E2B89D");
        inputDto.setKey(key);
        inputDto.setServiceType("FA");
        SubscriptionDto dto = subscriptionDtoService.submit(inputDto, new ServiceErrorsImpl());
        Assert.assertNotNull(dto);
        Assert.assertEquals(dto.getAccountName(),"efgh");
    }

    @Test
    public void testFind()
    {
        com.bt.nextgen.api.account.v2.model.AccountKey key =new com.bt.nextgen.api.account.v2.model.AccountKey(
                "DE96213E60E40DB87F361502DCDE817B6A20E5F066E2B89D");
        SubscriptionDto subscriptionDto = subscriptionDtoService.find(key,new ServiceErrorsImpl());
        Assert.assertNotNull(subscriptionDto);
    }

    @Test
    public void testMapOfferApplication() {
        List<Offer> offers = Subscriptions.getOffers();
        List<SubscriptionDto> resultList =
                subscriptionDtoService.mapOfferApplication(offers, applicationDocuments, new ServiceErrorsImpl());
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1, resultList.size());
        SubscriptionDto dto = resultList.get(0);
        Assert.assertEquals("123", dto.getOrderNumber());
        Assert.assertEquals("efgh", dto.getAccountName());
        Assert.assertEquals("Pit, David", dto.getAdviserName());
    }
}