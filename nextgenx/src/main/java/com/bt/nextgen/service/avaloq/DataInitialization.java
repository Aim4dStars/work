package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.CacheManagedAssetIntegrationService;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositRateIntegrationService;
import com.bt.nextgen.service.avaloq.asset.aal.AalIndexIntegrationService;
import com.bt.nextgen.service.avaloq.asset.aal.BrokerProductAssetIntegrationService;
import com.bt.nextgen.service.avaloq.asset.aal.IndexAssetIntegrationService;
import com.bt.nextgen.service.avaloq.broker.CacheManagedUserBrokerIntegrationService;
import com.bt.nextgen.service.avaloq.code.CacheManagedStaticCodeIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.installation.AvaloqInstallationInformation;
import com.bt.nextgen.service.avaloq.installation.AvaloqVersionIntegrationService;
import com.bt.nextgen.service.avaloq.licenseadviserfee.CacheManagedAvaloqLicenseAdviserFeeIntegrationService;
import com.bt.nextgen.service.avaloq.paginated.CacheManagedPaginatedBrokerIntegrationService;
import com.bt.nextgen.service.avaloq.product.CacheAvaloqProductIntegrationService;
import com.bt.nextgen.service.avaloq.transactionfee.CacheManagedAvaloqTransactionFeeIntegrationService;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * This class defines data that needs to be loaded as part of the data initialization after server startup which is required for
 * the application to work.
 *
 * @author L054821
 */
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck", "squid:S1166"})
@Component
public class DataInitialization {


    @Autowired
    private CacheManagedStaticCodeIntegrationServiceImpl staticCodeIntegrationService;

    @Autowired
    private CacheManagedUserBrokerIntegrationService brokerIntegrationService;

    @Autowired
    private CacheManagedPaginatedBrokerIntegrationService paginatedBrokerIntegrationService;

    @Autowired
    private FeatureTogglesService togglesService;

    @Autowired
    private CacheManagedAssetIntegrationService assetIntegrationService;

    @Autowired
    private BrokerProductAssetIntegrationService brokerProductAssetService;

    @Autowired
    private IndexAssetIntegrationService indexAssetIntegrationService;

    @Autowired
    private AalIndexIntegrationService aalIndexIntegrationService;

    @Autowired
    private BankDateIntegrationService bankdateservice;

    @Autowired
    private CacheAvaloqProductIntegrationService cacheProductService;

    @Autowired
    private CacheManagedTermDepositRateIntegrationService termDepositRatesService;

    @Autowired
    private CacheManagedTermDepositAssetRateIntegrationService rateIntegrationService;

    @Autowired
    private CacheManagedAvaloqTransactionFeeIntegrationService transactionFeeService;

    @Autowired
    private CacheManagedAvaloqLicenseAdviserFeeIntegrationService licenseAdviserFeeService;

    @Autowired
    private AvaloqVersionIntegrationService installationService;

    @Autowired
    private CacheTimer cacheTimer;

    @Autowired
    private ChessSponsorIntegrationService chessSponsorIntegrationService;


    private static Logger logger = LoggerFactory.getLogger(DataInitialization.class);

    public static final String STATIC_CODE_REQUEST_CALLED = "StaticRequestCalled";

    public static final String STATIC_CODE_REQUEST_TIME = "StaticRequestTime";

    public static final String ADVISER_PRODUCT_LIST_REQUEST_CALLED = "APLRequestCalled";

    public static final String ADVISER_PRODUCT_LIST_REQUEST_TIME = "APLRequestTime";

    public static final String BROKER_REQUEST_CALLED = "BrokerRequestCalled";

    public static final String BROKER_REQUEST_TIME = "BrokerRequestTime";

    public static final String AVAILABLE_ASSET_BROKER_REQUEST_CALLED = "AABrokerRequestCalled";

    public static final String AVAILABLE_ASSET_BROKER_REQUEST_TIME = "AABrokerRequestTime";

    public static final String AVAILABLE_ASSET_ISSUER_REQUEST_CALLED = "AAIssuerRequestCalled";

    public static final String AVAILABLE_ASSET_ISSUER_REQUEST_TIME = "AAIssuerRequestTime";

    public static final String AVAILABLE_ASSET_INDEX_REQUEST_CALLED = "AAIndexRequestCalled";

    public static final String AVAILABLE_ASSET_INDEX_REQUEST_TIME = "AAIndexRequestTime";

    public static final String INDEX_ASSET_REQUEST_CALLED = "IndexAssetRequestCalled";

    public static final String INDEX_ASSET_REQUEST_TIME = "IndexAssetRequestTime";

    public static final String TERM_DEPOSIT_ASSET_REQUEST_CALLED = "TDAssetRequestCalled";

    public static final String TERM_DEPOSIT_ASSET_REQUEST_TIME = "TDAssetRequestTime";

    public static final String TERM_DEPOSIT_PRODUCT_REQUEST_CALLED = "TDProductRequestCalled";

    public static final String TERM_DEPOSIT_PRODUCT_REQUEST_TIME = "TDProductRequestTime";

    public static final String PAGINATED_BROKER_REQUEST_CALLED = "PaginatedBrokerRequestCalled";

    public static final String PAGINATED_BROKER_REQUEST_TIME = "PaginatedBrokerRequestTime";

    @Async
    public void loadAllCaches() {
        loadAllStaticCodes();
        loadDataCaches();
    }

    public void loadDataCaches() {
        loadTermDepositAssetRates();
        loadTermDepositProductRates();
        loadApl();

        loadBrokers();

        loadAssets();

        loadAALv2();

        loadTransactionFees();
        loadLicenseAdviserFees();
        loadBankDate();
        loadChessParameters();
    }

    public void loadAALv2() {
        loadBrokerProductAssets();
        loadAalIndexes();
        loadIndexAssets();
    }

    public void loadBrokers() {
        try {
            logger.info("Loading paginated broker data");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            System.getProperties().setProperty(PAGINATED_BROKER_REQUEST_CALLED, "true");
            System.getProperties().put(PAGINATED_BROKER_REQUEST_TIME, new DateTime());
            paginatedBrokerIntegrationService.populatePaginatedBrokerCache(serviceErrors);
            logger.info("Paginated broker reference data request placed successfully");
        } catch (Exception err) {
            logger.error("Failed to load paginated broker data, initial login will fail", err);
            throw err;
        }
    }

    public void loadAvaloqVersionInformation() {
        try {
            logger.info("Loading avaloq version information");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            AvaloqInstallationInformation avaloqInformation = installationService.getAvaloqInstallInformation(serviceErrors);
            logger.info("Avaloq Version is {}", avaloqInformation.getInstallationUid());

        } catch (Exception err) {
            logger.error("Failed to load avaloq version. trying a second time", err);
            try {
                ServiceErrors serviceErrors = new FailFastErrorsImpl();
                AvaloqInstallationInformation avaloqInformation = installationService.getAvaloqInstallInformation(serviceErrors);
                logger.info("Avaloq Version is {}", avaloqInformation.getInstallationUid());
            } catch (Exception err1) {
                logger.error("Failed on secondary load of avaloq version with error", err1);
            }
        }
    }

    public void loadAllStaticCodes() {
        try {
            logger.info("Loading static reference data");
            cacheTimer.startTimer();
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            System.getProperties().setProperty(STATIC_CODE_REQUEST_CALLED, "true");
            System.getProperties().put(STATIC_CODE_REQUEST_TIME, new DateTime());
            staticCodeIntegrationService.populateCache(serviceErrors);
            logger.info("Cash Static reference data request placed successfully");

        } catch (Exception err) {
            logger.error("Failed to load all static data, initial login will fail", err);
            throw err;
        }

    }

    public void loadChunkedBrokers() {
        try {
            logger.info("Loading broker reference data");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            System.getProperties().setProperty(BROKER_REQUEST_CALLED, "true");
            System.getProperties().put(BROKER_REQUEST_TIME, new DateTime());
            brokerIntegrationService.populatebrokerCache(serviceErrors);
            logger.info("Broker reference data request placed successfully");
        } catch (Exception err) {
            logger.error("Failed to load all Broker data, initial login will fail", err);
            throw err;
        }

    }

    public void loadPartialBrokerUpdate(InvalidationNotification invalidationNotification) {
        try {
            logger.info("Loading partial broker reference data");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            brokerIntegrationService.populatePartialBrokerCache(invalidationNotification, serviceErrors);
            logger.info("Broker partial update reference data request placed successfully");
        } catch (Exception err) {
            logger.error("Failed to update partial Broker data", err);
            throw err;
        }

    }

    public void loadGeneralAssets() {
        try {
            logger.info("Loading Asset Information reference data");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            assetIntegrationService.populateAssetCache(serviceErrors);
            if (serviceErrors.hasErrors()) {
                logger.info("Asset list failed to load");
            } else {
                logger.info("Asset list loaded successfully");
            }
        } catch (Exception err) {
            logger.error("Failed to load all Asset data", err);
            throw err;
        }
    }

    public void loadAssets() {
        try {
            logger.info("Loading Asset Information reference data");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            assetIntegrationService.populateAssetCache(serviceErrors);
            logger.info("Asset list loaded successfully");
        } catch (Exception err) {
            logger.error("Failed to load all Asset data", err);
            throw err;
        }
    }

    public void loadApl() {
        try {
            logger.info("Requesting APL");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            System.getProperties().setProperty(ADVISER_PRODUCT_LIST_REQUEST_CALLED, "true");
            System.getProperties().put(ADVISER_PRODUCT_LIST_REQUEST_TIME, new DateTime());
            cacheProductService.initializeAplCache(serviceErrors);
            logger.info("APL requested successfully");

        } catch (Exception err) {
            logger.error("Failed to request APL data", err);
            throw err;
        }

    }

     public void loadTermDepositAssetRates(){
        logger.info("Loading Term Deposits rates");
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        try{
            System.getProperties().setProperty(TERM_DEPOSIT_ASSET_REQUEST_CALLED, "true");
            System.getProperties().put(TERM_DEPOSIT_ASSET_REQUEST_TIME, new DateTime());
            rateIntegrationService.clearCache();
            rateIntegrationService.loadTermDepositAssetsToCache(serviceErrors);
            logger.info("Term Deposits Asset rates loaded successfully");
        }catch(Exception err){
            logger.error("Failed to load Term Deposits Asset rates.", err);
            throw err;
        }
    }

    public void loadTermDepositProductRates(){
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        try{
            System.getProperties().setProperty(TERM_DEPOSIT_PRODUCT_REQUEST_CALLED, "true");
            System.getProperties().put(TERM_DEPOSIT_PRODUCT_REQUEST_TIME, new DateTime());
            rateIntegrationService.clearCache();
            rateIntegrationService.loadTermDepositBaseProductListToCache(serviceErrors);
            logger.info("Term Deposits Product rates loaded successfully");
        }catch(Exception err){
            logger.error("Failed to load Term Deposits Product rates.", err);
            throw err;
        }

    }


    public void loadBankDate() {
        try {
            logger.info("Loading Bank Date");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            bankdateservice.getBankDate(serviceErrors);
            logger.info("Bank date loaded successfully");
        } catch (Exception err) {
            logger.error("Failed to load Bank Date, initial login will fail", err);
            throw err;
        }

    }

    public void loadChessParameters(){
        try{
            logger.info("Loading ChessParameter");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            chessSponsorIntegrationService.getChessSponsorData(serviceErrors);
            logger.info("Chess Parameter loaded Sucessfully");
        }catch(Exception err){
            logger.error("Failed to load Chess Parameter ", err);
            throw err;
        }
    }

    public void loadTransactionFees() {
        try {
            logger.info("Loading transaction fees.");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            transactionFeeService.initCache(serviceErrors);
            logger.info("Transaction fees loaded successfully.");
        } catch (Exception err) {
            logger.error("Failed to load all transaction fees.", err);
            throw err;
        }

    }


    public void loadLicenseAdviserFees() {
        try {
            logger.info("Loading License Adviser fees.");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            licenseAdviserFeeService.initCache(serviceErrors);
            logger.info("License Adviser fees loaded successfully.");
        } catch (Exception err) {
            logger.error("Failed to load all License Adviser fees.", err);
            throw err;
        }

    }

    public void loadBrokerProductAssets() {
        try {
            logger.info("Loading brokerProductAssets.");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            System.getProperties().setProperty(AVAILABLE_ASSET_BROKER_REQUEST_CALLED, "true");
            System.getProperties().put(AVAILABLE_ASSET_BROKER_REQUEST_TIME, new DateTime());
            brokerProductAssetService.initCache(serviceErrors);
            logger.info("brokerProductAssets loaded successfully.");
        } catch (Exception err) {
            logger.error("Failed to load all brokerProductAssets.", err);
            throw err;
        }
    }

    public void loadAalIndexes() {
        try {
            logger.info("Loading aalIndexes.");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            System.getProperties().setProperty(AVAILABLE_ASSET_INDEX_REQUEST_CALLED, "true");
            System.getProperties().put(AVAILABLE_ASSET_INDEX_REQUEST_TIME, new DateTime());
            aalIndexIntegrationService.initCache(serviceErrors);
            logger.info("aalIndexes loaded successfully.");
        } catch (Exception err) {
            logger.error("Failed to load all aalIndexes.", err);
            throw err;
        }
    }

    public void loadIndexAssets() {
        try {
            logger.info("Loading indexAssets.");
            ServiceErrors serviceErrors = new FailFastErrorsImpl();
            System.getProperties().setProperty(INDEX_ASSET_REQUEST_CALLED, "true");
            System.getProperties().put(INDEX_ASSET_REQUEST_TIME, new DateTime());
            indexAssetIntegrationService.initCache(serviceErrors);
            logger.info("indexAssets loaded successfully.");
        } catch (Exception err) {
            logger.error("Failed to load all indexAssets.", err);
            throw err;
        }
    }


}
