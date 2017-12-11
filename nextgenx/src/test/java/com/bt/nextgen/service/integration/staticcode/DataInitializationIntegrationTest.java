package com.bt.nextgen.service.integration.staticcode;

import com.bt.nextgen.config.*;
import com.bt.nextgen.service.avaloq.DataInitialization;
import com.bt.nextgen.service.avaloq.code.CacheManagedStaticCodeIntegrationServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

/**
 * Created by L070589 on 2/02/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestConfig.class, com.btfin.panorama.core.security.aes.AESEncryptService.class })
@TestExecutionListeners(listeners = { IntegrationTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        ServletTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
public class DataInitializationIntegrationTest {

    @Autowired
    DataInitialization dataInitialization;

    @Autowired
    private CacheManagedStaticCodeIntegrationServiceImpl staticCodes;

    @Before
    public void setupBase() {
        //StaticTestDataInitialiser.init(staticCodes);
    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "iccexplodes")
    public void testLoadCodesICCError() throws Exception {
        dataInitialization.loadAllStaticCodes();

    }

    @Test(expected = Exception.class)

    @SecureTestContext(username = "explode")
    public void testLoadCodesAvaloqError() throws Exception {
        dataInitialization.loadAllStaticCodes();

    }

    @Test
    @SecureTestContext
    public void testLoadCodesSuccess() throws Exception {
        dataInitialization.loadAllStaticCodes();
    }

    @Test
    @SecureTestContext
    public void testBrokerProductAssetsSuccess() throws Exception {
        dataInitialization.loadBrokerProductAssets();
    }

    @Test
    @SecureTestContext
    public void testAalIndexSuccess() throws Exception {
        dataInitialization.loadAalIndexes();
    }

    @Test
    @SecureTestContext
    public void testIndexAssetsSuccess() throws Exception {
        dataInitialization.loadIndexAssets();
    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "iccexplodes")
    public void testLoadHiearchyICCError() throws Exception {
        dataInitialization.loadChunkedBrokers();

    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "iccendsystemexplode", jobRole = "adviser", customerId = "201601934", profileId = "971", jobId = "")
    public void testLoadCodesIccError() throws Exception {
        dataInitialization.loadAllStaticCodes();

    }

    @Test(expected = Exception.class)

    @SecureTestContext(username = "explode")
    public void testLoadHiearchyAvaloqError() throws Exception {
        dataInitialization.loadChunkedBrokers();

    }

    @Test
    @SecureTestContext
    public void testLoadChunkedHiearchySuccess() throws Exception {
        dataInitialization.loadChunkedBrokers();
    }

    @Test
    @SecureTestContext
    public void testAssetsSuccess() throws Exception {
        dataInitialization.loadAssets();
    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "iccexplodes")
    public void testLoadAPLICCError() throws Exception {
        dataInitialization.loadApl();

    }

    @Test(expected = Exception.class)

    @SecureTestContext(username = "explode")
    public void testLoadAPLAvaloqError() throws Exception {
        dataInitialization.loadApl();

    }

    @Test
    @SecureTestContext
    public void testAPLSuccess() throws Exception {
        dataInitialization.loadApl();
    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "iccexplodes")
    public void testTermDepositAssetICCError() throws Exception {
        dataInitialization.loadTermDepositAssetRates();

    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "explode")
    public void testTermDepositAssetAvaloqError() throws Exception {
        dataInitialization.loadTermDepositProductRates();

    }


    @Test(expected = Exception.class)
    @SecureTestContext(username = "iccexplodes")
    public void testTermDepositProductICCError() throws Exception {
        dataInitialization.loadTermDepositProductRates();

    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "explode")
    public void testTermDepositProductAvaloqError() throws Exception {
        dataInitialization.loadTermDepositProductRates();

    }

    @Test
    @SecureTestContext
    public void loadTermDepositAssetRatesSuccess() throws Exception {
        dataInitialization.loadTermDepositAssetRates();
    }


    @Test
    @SecureTestContext
    public void loadTermDepositProductRatesSuccess() throws Exception {
        dataInitialization.loadTermDepositProductRates();
    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "iccexplodes")
    public void testTermDepositAssetRateICCError() throws Exception {
        dataInitialization.loadTermDepositAssetRates();

    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "explode")
    public void testTermDepositAssetRateAvaloqError() throws Exception {
        dataInitialization.loadTermDepositAssetRates();

    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "iccexplodes")
    public void testTermDepositProductRateICCError() throws Exception {
        dataInitialization.loadTermDepositProductRates();

    }

    @Test(expected = Exception.class)
    @SecureTestContext(username = "explode")
    public void testTermDepositProductRateAvaloqError() throws Exception {
        dataInitialization.loadTermDepositProductRates();

    }

    @Test
    @SecureTestContext
    public void loadloadLicenseAdviserFeesSuccess() throws Exception {
        dataInitialization.loadLicenseAdviserFees();
    }

    @Test
    @SecureTestContext
    public void loadChessSponsorData() throws Exception {
     dataInitialization.loadChessParameters();
    }

    @Ignore
    @Test(expected = Exception.class)
    @SecureTestContext(username = "iccexplodes")
    public void testBankDateICCError() throws Exception {
        dataInitialization.loadBankDate();

    }

    @Ignore
    @Test(expected = Exception.class)
    @SecureTestContext(username = "explode")
    public void testBankDateAvaloqError() throws Exception {
        dataInitialization.loadBankDate();

    }

    @Test
    @SecureTestContext
    public void loadBankDateSuccess() throws Exception {
        dataInitialization.loadBankDate();
    }

    @Test
    @SecureTestContext
    public void testLoadPaginatedBroker() throws Exception {
        dataInitialization.loadBrokers();
    }
}
