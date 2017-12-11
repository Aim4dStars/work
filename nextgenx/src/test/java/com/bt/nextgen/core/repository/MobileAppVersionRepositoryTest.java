package com.bt.nextgen.core.repository;

import java.util.List;

import ch.lambdaj.Lambda;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.config.BaseSecureIntegrationTest;

@TransactionConfiguration(defaultRollback = true)
public class MobileAppVersionRepositoryTest extends BaseSecureIntegrationTest {

    @Autowired
    MobileAppVersionRepositoryImpl appVersionRepository;

    @Test
    @Transactional("springJpaTransactionManager")
    @Rollback(true)
    public void testFindAppVersions() throws Exception {
        appVersionRepository.update(new MobileAppVersion("android", "2.0"));
        appVersionRepository.update(new MobileAppVersion("ios", "2.3"));

        List<MobileAppVersion> appVersions = appVersionRepository.findAppVersions();

        Assert.assertNotNull(appVersions);
        Assert.assertTrue(appVersions.size() > 0);

        List<MobileAppVersion> sortedAppVersions = Lambda.sort(appVersions, Lambda.on(MobileAppVersion.class).getPlatform());

        Assert.assertEquals(sortedAppVersions.get(0).getPlatform(), "android");
        Assert.assertEquals(sortedAppVersions.get(0).getVersion(), "2.0");
    }
}
