package com.bt.nextgen.core.repository;


import com.bt.nextgen.config.BaseSecureIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@TransactionConfiguration(defaultRollback = true)
public class UserPreferenceRepositoryTest extends BaseSecureIntegrationTest {
    @Autowired
    UserRepositoryImpl userRepository;

    @Autowired
    UserPreferenceRepositoryImpl userPreferenceRepository;

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testFindNoUserPreference() throws Exception {
        UserPreference userPreference = userPreferenceRepository.find("user1", "pref1");
        Assert.assertNull(userPreference);
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testSaveUserPreference() throws Exception {
        userRepository.newUser("user1");

        UserPreference userPreference = new UserPreference();
        userPreference.setKey(new UserPreferenceKey("user1", "pref1"));
        userPreference.setValue("preference details");
        userPreference = userPreferenceRepository.save(userPreference);
        Assert.assertEquals(userPreference.getKey().getUserId(), "user1");
        Assert.assertEquals(userPreference.getValue(), "preference details");
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testFindUserPreference() throws Exception {
        userRepository.newUser("user1");

        UserPreference userPreference = new UserPreference();
        UserPreferenceKey key = new UserPreferenceKey();
        key.setUserId("user1");
        key.setPreferenceId("pref1");
        userPreference.setKey(key);
        userPreference.setValue("preference details");
        userPreferenceRepository.save(userPreference);

        userPreference = userPreferenceRepository.find("user1", "pref1");
        Assert.assertEquals(userPreference.getKey().getUserId(), "user1");
        Assert.assertEquals(userPreference.getKey().getPreferenceId(), "pref1");
        Assert.assertEquals(userPreference.getValue(), "preference details");
    }
}
