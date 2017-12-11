package com.bt.nextgen.config;

import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.avaloq.asset.CacheManagedAssetIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.asset.aal.AalIndexIntegrationService;
import com.bt.nextgen.service.avaloq.asset.aal.BrokerProductAssetIntegrationService;
import com.bt.nextgen.service.avaloq.asset.aal.IndexAssetIntegrationService;
import com.bt.nextgen.service.avaloq.broker.CacheManagedPaginatedUserBrokerIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.broker.CacheManagedUserBrokerIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.code.CacheManagedStaticCodeIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.licenseadviserfee.CacheManagedAvaloqLicenseAdviserFeeIntegrationService;
import com.bt.nextgen.service.avaloq.product.CacheAvaloqProductIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.transactionfee.CacheManagedAvaloqTransactionFeeIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import org.junit.Before;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestConfig.class, com.btfin.panorama.core.security.aes.AESEncryptService.class })
@TestExecutionListeners(listeners = { IntegrationTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        ServletTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
public abstract class BaseSecureIntegrationTest {

    @Autowired
    private ParsingContext context;

    @Autowired
    private CacheManagedUserBrokerIntegrationServiceImpl cacheManagedUserBrokerService;
    
    @Autowired
    private CacheManagedPaginatedUserBrokerIntegrationServiceImpl cacheManagedPaginatedUserBrokerService;

    @Autowired
    private CacheManagedStaticCodeIntegrationServiceImpl staticCodes;

    @Autowired
    CacheManagedAssetIntegrationServiceImpl cacheManagedAssetIntegrationService;

    @Autowired
    private BrokerProductAssetIntegrationService brokerProductAssetService;

    @Autowired
    private IndexAssetIntegrationService indexAssetIntegrationService;

    @Autowired
    private AalIndexIntegrationService aalIndexIntegrationService;

    @Autowired
    private CacheAvaloqProductIntegrationServiceImpl aplService;

    @Autowired
    protected CacheManagedAvaloqTransactionFeeIntegrationService transactionFeeService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    protected CacheManagedAvaloqLicenseAdviserFeeIntegrationService licenseAdviserFeeIntegrationService;


    @Before
    public void setupBase() {
        init(staticCodes);

        init(cacheManagedPaginatedUserBrokerService);
        init(cacheManagedAssetIntegrationService);
        init(brokerProductAssetService);
        init(indexAssetIntegrationService);
        init(aalIndexIntegrationService);
        init(aplService);
        init(transactionFeeService);
        init(licenseAdviserFeeIntegrationService);

    }

    protected void init(CacheManagedStaticCodeIntegrationServiceImpl latestInitialiser) {
        StaticTestDataInitialiser.init(latestInitialiser);
    }

    protected void init(CacheManagedUserBrokerIntegrationServiceImpl latestInitialiser) {
        StaticTestDataInitialiser.init(latestInitialiser);
    }
    
    protected void init(CacheManagedPaginatedUserBrokerIntegrationServiceImpl latestInitialiser) {
        StaticTestDataInitialiser.init(latestInitialiser);
    }


    protected void init(CacheAvaloqProductIntegrationServiceImpl latestInitialiser) {
        StaticTestDataInitialiser.init(latestInitialiser);
    }

    protected void init(CacheManagedAssetIntegrationServiceImpl latestInitialiser) {
        StaticTestDataInitialiser.init(latestInitialiser);
    }


    protected void init(BrokerProductAssetIntegrationService latestInitialiser) {
        StaticTestDataInitialiser.init(latestInitialiser);
    }

    protected void init(IndexAssetIntegrationService latestInitialiser) {
        StaticTestDataInitialiser.init(latestInitialiser);
    }

    protected void init(AalIndexIntegrationService latestInitialiser) {
        StaticTestDataInitialiser.init(latestInitialiser);
    }

    protected void init(CacheManagedAvaloqTransactionFeeIntegrationService latestInitialiser) {
        StaticTestDataInitialiser.init(latestInitialiser);
    }

    protected void init(CacheManagedAvaloqLicenseAdviserFeeIntegrationService latestInitialiser){
        StaticTestDataInitialiser.init(latestInitialiser);
    }
}