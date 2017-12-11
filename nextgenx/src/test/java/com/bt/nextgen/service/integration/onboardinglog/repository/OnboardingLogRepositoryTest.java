package com.bt.nextgen.service.integration.onboardinglog.repository;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.onboardinglog.model.OnboardingLog;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@TransactionConfiguration
public class OnboardingLogRepositoryTest extends BaseSecureIntegrationTest {

    @Autowired
    private OnboardingLogRepository onboardingLogRepository;

    @PersistenceContext
    EntityManager entityManager;

    private void setTestData() {
        OnboardingLog unlogged1 = new OnboardingLog();
        unlogged1.setApplicationId("1");
        unlogged1.setClientGcmId("201201201");
        unlogged1.setStatus("status");
        unlogged1.setFailureMessage("message");
        unlogged1.setEventType("FAILURE");
        unlogged1.setHasBeenLogged(false);

        OnboardingLog logged1 = new OnboardingLog();
        logged1.setApplicationId("2");
        logged1.setClientGcmId("201201202");
        logged1.setStatus("status");
        logged1.setFailureMessage("message");
        logged1.setEventType("FAILURE");
        logged1.setHasBeenLogged(true);

        entityManager.merge(unlogged1);
        entityManager.merge(logged1);
        entityManager.flush();
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testFindAll_returnsAllUnloggedRows() throws Exception {
        setTestData();

        List<OnboardingLog> result = onboardingLogRepository.findAll();
        Assert.assertEquals(1, result.size());

        OnboardingLog log = result.get(0);
        Assert.assertEquals("1", log.getApplicationId());
        Assert.assertEquals("201201201", log.getClientGcmId());
        Assert.assertEquals("status", log.getStatus());
        Assert.assertEquals("message", log.getFailureMessage());
        Assert.assertEquals("FAILURE", log.getEventType());
        Assert.assertEquals(false, log.getHasBeenLogged());
    }
}
