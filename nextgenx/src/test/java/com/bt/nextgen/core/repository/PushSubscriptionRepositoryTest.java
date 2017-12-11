package com.bt.nextgen.core.repository;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.config.BaseSecureIntegrationTest;

@TransactionConfiguration(defaultRollback = true)
public class PushSubscriptionRepositoryTest extends BaseSecureIntegrationTest {

    @Autowired
    PushSubscriptionRepositoryImpl pushNotificationRepository;

    @Test
    @Transactional("springJpaTransactionManager")
    @Rollback(true)
    public void testUpdateUser() throws Exception {
        PushSubscriptionDetails details = new PushSubscriptionDetails();
        details.setKey(new PushSubscriptionKey("device1", "user1"));
        details.setPlatform("android");
        details.setActive(true);

        PushSubscriptionDetails updatedDetails = pushNotificationRepository.update(details);
        Assert.assertNotNull(updatedDetails);
        Assert.assertEquals(updatedDetails.getKey().getDeviceUid(), "device1");
        Assert.assertEquals(updatedDetails.getKey().getUserId(), "user1");
        Assert.assertEquals(updatedDetails.getPlatform(), "android");
        Assert.assertTrue(updatedDetails.isActive());
    }

    @Test
    @Transactional("springJpaTransactionManager")
    @Rollback(true)
    public void testUpdateMoreUser() throws Exception {
        PushSubscriptionDetails details = new PushSubscriptionDetails();
        details.setKey(new PushSubscriptionKey("device1", "user2"));
        details.setPlatform("ios");
        details.setActive(true);

        PushSubscriptionDetails updatedDetails = pushNotificationRepository.update(details);
        Assert.assertNotNull(updatedDetails);
        Assert.assertEquals(updatedDetails.getKey().getDeviceUid(), "device1");
        Assert.assertEquals(updatedDetails.getKey().getUserId(), "user2");
        Assert.assertEquals(updatedDetails.getPlatform(), "ios");
        Assert.assertTrue(updatedDetails.isActive());
    }

    @Test
    @Transactional("springJpaTransactionManager")
    @Rollback(true)
    public void testSaveRemoveUser() throws Exception {
        PushSubscriptionDetails details = new PushSubscriptionDetails();
        details.setKey(new PushSubscriptionKey("device1", "user1"));
        details.setPlatform("android");
        details.setActive(false);

        PushSubscriptionDetails updatedDetails = pushNotificationRepository.update(details);
        Assert.assertNotNull(updatedDetails);
        Assert.assertEquals(updatedDetails.getKey().getDeviceUid(), "device1");
        Assert.assertEquals(updatedDetails.getKey().getUserId(), "user1");
        Assert.assertEquals(updatedDetails.getPlatform(), "android");
        Assert.assertFalse(updatedDetails.isActive());
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testFindPushDetailsSuccess() throws Exception {
        PushSubscriptionDetails pushSubscriptionDetails = new PushSubscriptionDetails();
        PushSubscriptionKey key = new PushSubscriptionKey();
        key.setUserId("user1");
        key.setDeviceUid("device1");
        pushSubscriptionDetails.setKey(key);
        pushSubscriptionDetails.setPlatform("ios");
        pushSubscriptionDetails.setActive(true);
        pushNotificationRepository.update(pushSubscriptionDetails);

        pushSubscriptionDetails = pushNotificationRepository.find("device1", "user1");
        Assert.assertEquals(pushSubscriptionDetails.getKey().getUserId(), "user1");
        Assert.assertEquals(pushSubscriptionDetails.getKey().getDeviceUid(), "device1");
        Assert.assertEquals(pushSubscriptionDetails.getPlatform(), "ios");
        Assert.assertTrue(pushSubscriptionDetails.isActive());
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testFindPushDetailsFailure() throws Exception {
        PushSubscriptionDetails pushSubscriptionDetails = new PushSubscriptionDetails();
        PushSubscriptionKey key = new PushSubscriptionKey();
        key.setUserId("user2");
        key.setDeviceUid("device2");
        pushSubscriptionDetails.setKey(key);
        pushSubscriptionDetails.setPlatform("android");
        pushSubscriptionDetails.setActive(true);
        pushNotificationRepository.update(pushSubscriptionDetails);

        pushSubscriptionDetails = pushNotificationRepository.find("device1", "user1");
        Assert.assertNull(pushSubscriptionDetails);
    }

}
