package com.bt.nextgen.core.repository;

import com.bt.nextgen.api.subscriptions.service.Subscriptions;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationDocumentImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@TransactionConfiguration(defaultRollback = true)
public class SubscriptionsRepositoryImplTest extends BaseSecureIntegrationTest {

    @Autowired
    SubscriptionsRepositoryImpl repository;

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testFindAllByStatus() throws Exception {
        SubscriptionDetails details = new SubscriptionDetails ();
        details.setAccountId("1234");
        details.setDocId("1472");
        details.setStatus(SubscriptionStatus.INPROGRESS.name());
        details.setSubscriptionType(Subscriptions.FA.getOrderType());
        repository.save(details);
        List<String> docIds =  repository.findAllByStatus(SubscriptionStatus.INPROGRESS, Collections.singletonList("1234"));
        Assert.assertNotNull(docIds);
        Assert.assertTrue(docIds.size() > 0);
        Assert.assertEquals(details.getDocId(), docIds.get(0));
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testFindAll() throws Exception {
        SubscriptionDetails details = new SubscriptionDetails ();
        details.setAccountId("1234");
        details.setDocId("1472");
        details.setStatus(SubscriptionStatus.INPROGRESS.name());
        details.setSubscriptionType(Subscriptions.FA.getOrderType());
        repository.save(details);
        List<SubscriptionDetails> subscriptionDetails =  repository.findAll(AccountKey.valueOf("1234"));
        Assert.assertNotNull(subscriptionDetails);
        Assert.assertTrue(subscriptionDetails.size() > 0);
        Assert.assertEquals(details.getDocId(), subscriptionDetails.get(0).getDocId());
        Assert.assertEquals(details.getSubscriptionType(), subscriptionDetails.get(0).getSubscriptionType());
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testSave() throws Exception {
        SubscriptionDetails details = new SubscriptionDetails ();
        details.setAccountId("1234");
        details.setDocId("1472");
        details.setStatus(SubscriptionStatus.INPROGRESS.name());
        details.setSubscriptionType(Subscriptions.FA.getOrderType());
        repository.save(details);
        SubscriptionDetails subscriptionsDetails =  repository.find("1472");
        Assert.assertNotNull(subscriptionsDetails);
        Assert.assertEquals(details.getAccountId(), subscriptionsDetails.getAccountId());
        Assert.assertEquals(details.getSubscriptionType(), subscriptionsDetails.getSubscriptionType());
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testSaveInprogressDocument() throws Exception {
        ApplicationDocument document = new ApplicationDocumentImpl();
        document.setBpid(AccountKey.valueOf("1111"));
        document.setOrderType(Subscriptions.FA.getOrderType());
        document.setAppNumber("1245");
        document.setAppSubmitDate(new Date());
        document.setAppState(ApplicationStatus.AWAITING_DOCUMENTS);
        repository.save(document);
        SubscriptionDetails subscriptionsDetails =  repository.find("1245");
        Assert.assertNotNull(subscriptionsDetails);
        Assert.assertEquals(document.getBpid().getId(), subscriptionsDetails.getAccountId());
        Assert.assertEquals(document.getOrderType(), subscriptionsDetails.getSubscriptionType());
        Assert.assertEquals(SubscriptionStatus.INPROGRESS.name(), subscriptionsDetails.getStatus());
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testSaveCancelledDocument() throws Exception {
        ApplicationDocument document = new ApplicationDocumentImpl();
        document.setBpid(AccountKey.valueOf("1111"));
        document.setOrderType(Subscriptions.FA.getOrderType());
        document.setAppNumber("1245");
        document.setAppSubmitDate(new Date());
        document.setAppState(ApplicationStatus.DISCARDED);
        repository.save(document);
        SubscriptionDetails subscriptionsDetails =  repository.find("1245");
        Assert.assertNotNull(subscriptionsDetails);
        Assert.assertEquals(document.getBpid().getId(), subscriptionsDetails.getAccountId());
        Assert.assertEquals(document.getOrderType(), subscriptionsDetails.getSubscriptionType());
        Assert.assertEquals(SubscriptionStatus.CANCELLED.name(), subscriptionsDetails.getStatus());
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testSaveDoneDocument() throws Exception {
        ApplicationDocument document = new ApplicationDocumentImpl();
        document.setBpid(AccountKey.valueOf("1111"));
        document.setOrderType(Subscriptions.FA.getOrderType());
        document.setAppNumber("1245");
        document.setAppSubmitDate(new Date());
        document.setAppState(ApplicationStatus.DONE);
        repository.save(document);
        SubscriptionDetails subscriptionsDetails =  repository.find("1245");
        Assert.assertNotNull(subscriptionsDetails);
        Assert.assertEquals(document.getBpid().getId(), subscriptionsDetails.getAccountId());
        Assert.assertEquals(document.getOrderType(), subscriptionsDetails.getSubscriptionType());
        Assert.assertEquals(SubscriptionStatus.SUBSCRIBED.name(), subscriptionsDetails.getStatus());
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testUpdateMultipleRecords() throws Exception {
        SubscriptionDetails document = new SubscriptionDetails();
        document.setAccountId("1111");
        document.setSubscriptionType(Subscriptions.FA.getOrderType());
        document.setDocId("1245");
        document.setStatus(SubscriptionStatus.INPROGRESS.name());
        List<SubscriptionDetails> applicationDocuments =  new ArrayList<>();
        applicationDocuments.add(document);
        List<SubscriptionDetails> updatedDocuments = repository.update(applicationDocuments);
        Assert.assertNotNull(updatedDocuments);
        Assert.assertTrue(updatedDocuments.size() > 0);
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testUpdate() throws Exception {
        SubscriptionDetails  details = new SubscriptionDetails ();
        details.setAccountId("1234");
        details.setDocId("14721");
        details.setStatus(SubscriptionStatus.INPROGRESS.name());
        details.setSubscriptionType(Subscriptions.FA.getOrderType());
        SubscriptionDetails subscriptionsDetails =  repository.update(details);
        Assert.assertNotNull(subscriptionsDetails);
        Assert.assertEquals(details.getAccountId(), subscriptionsDetails.getAccountId());
        Assert.assertEquals(details.getSubscriptionType(), subscriptionsDetails.getSubscriptionType());
    }
}