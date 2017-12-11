package com.bt.nextgen.core.cache;

import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.avaloq.Constants;
import com.bt.nextgen.service.avaloq.asset.InterestRateCard;
import com.bt.nextgen.service.avaloq.asset.InterestRateCardImpl;
import com.bt.nextgen.service.avaloq.asset.aal.*;
import com.bt.nextgen.service.avaloq.broker.JobUserBroker;
import com.bt.nextgen.service.avaloq.code.StaticCodeKeyGetterImpl;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingTariff;
import com.bt.nextgen.service.avaloq.product.AplCacheServiceImpl;
import com.bt.nextgen.service.avaloq.transactionfee.AvaloqTransactionFee;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import static com.bt.nextgen.core.cache.CacheType.LICENSE_ADVISER_FEES;
import static com.bt.nextgen.core.cache.CacheType.TRANSACTION_FEES;
import static com.bt.nextgen.core.cache.DiskCacheFile.*;

/**
 * Created by Upul Doluweera on 5/03/2016.
 */
@Service
public class DiskCacheLoadingService {

    private static final Logger logger = LoggerFactory.getLogger(DiskCacheLoadingService.class);

    @Autowired
    private GenericCache cache;

    @Autowired
    private BrokerProductAssetIntegrationService brokerProductAssetService;

    @Autowired
    private AalIndexIntegrationService aalIndexIntegrationService;

    @Autowired
    private IndexAssetIntegrationService indexAssetIntegrationService;

    @Autowired
    private DiskSerializerService serializerService;

    @Autowired
    private FeatureTogglesService togglesService;

    public void loadCache() {

        boolean isEnabled = togglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.DISK_CACHE_SERIALIZATION);

        if (isEnabled) {

            boolean allCacheFilesExist = serializerService.isAllCacheFilesExist();

            if (!allCacheFilesExist) {
                logger.info("CACHE_FILE_LOAD_SKIP. Some cache files don't exist. Can't load the cache from files. Skipping");
                return;
            }

            boolean isLoaded = loadStaticCodes();

            if (isLoaded) {
                loadTermDeposits();
                loadBrokers();
                loadAssets();
                loadAPL();
                loadTransactionFees();
                loadBrokerProductAssets();
                loadAalIndexes();
                loadIndexAssets();
                loadLicenseAdviserFees();
                loadBankDate();
            } else {
                logger.info("Static codes hasn't been loaded from file. Other caches will not be loaded from disk");
            }
        }
    }

    private void loadLicenseAdviserFees() {
        try {
            String filePath = getAvaloqSpecificFilePath(LICENSE_ADVISER_FEES_CACHE_FILE);
            boolean fileExists = serializerService.isCacheFileExist(filePath);
            if (fileExists) {
                logger.info("Loading license adviser feed from the cache file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<FeeDgOngoingTariff> licenseFees = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (licenseFees != null) {
                    cache.putAll(licenseFees, LICENSE_ADVISER_FEES, new KeyGetter() {
                        @Override
                        public Object getKey(Object obj) {
                            return Integer.toString(((FeeDgOngoingTariff) obj).generateKey());
                        }
                    });
                    logger.info("{} LicenseFees successfully loaded from the file", licenseFees.size());
                } else {
                    logger.info("LicenseFees from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Error ", e);
        }
    }

    private void loadIndexAssets() {
        try {
            String filePath = getAvaloqSpecificFilePath(INDEX_ASSET_CACHE_FILE);
            boolean fileExists = serializerService.isCacheFileExist(filePath);
            if (fileExists) {
                logger.info("Loading index assets from the cache file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<IndexAsset> indexAssets = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (indexAssets != null) {
                    indexAssetIntegrationService.initCache(indexAssets);
                    logger.info("{} Index Assets successfully loaded from the file", indexAssets.size());
                } else {
                    logger.info("IndexAssets from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred when loading index assets from disk", e);
        }
    }

    private void loadAalIndexes() {
        try {
            String filePath = getAvaloqSpecificFilePath(AAL_LIST_INDEX_CACHE_FILE);
            boolean fileExists = serializerService.isCacheFileExist(filePath);
            if (fileExists) {
                logger.info("Loading aal indexes from the cache file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<AalIndex> aalIndexes = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (aalIndexes != null) {
                    aalIndexIntegrationService.initCache(aalIndexes);
                    logger.info("{} AalIndexes successfully loaded from the file", aalIndexes.size());
                } else {
                    logger.info("AalIndexes from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred when loading aal indexes from disk", e);
        }

    }

    private void loadBrokerProductAssets() {
        try {
            String filePath = getAvaloqSpecificFilePath(BROKER_PRODUCT_ASSETS_CACHE_FILE);
            boolean fileExists = serializerService.isCacheFileExist(filePath);
            if (fileExists) {
                logger.info("Loading broker product assets from the cache file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<BrokerProductAsset> brokerProductAssets = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (brokerProductAssets != null) {
                    brokerProductAssetService.initCache(brokerProductAssets);
                    logger.info("{} BrokerProductAssets successfully loaded from the file", brokerProductAssets.size());
                } else {
                    logger.info("BrokerProductAssets from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred when loading broker product assets from disk", e);
        }
    }

    private boolean loadStaticCodes() {

        boolean isLoaded = false;

        try {
            String filePath = getAvaloqSpecificFilePath(STATIC_CODES_CACHE_FILE);
            boolean staticCodeCacheFileExists = serializerService.isCacheFileExist(filePath);
            if (staticCodeCacheFileExists) {
                logger.info("Loading static codes from the cache file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<Code> staticCodes = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (staticCodes != null) {
                    cache.putAll(staticCodes, CacheType.STATIC_CODE_CACHE, new StaticCodeKeyGetterImpl(), true);
                    isLoaded = true;
                    logger.info("{} Static codes successfully loaded from the file", staticCodes.size());
                } else {
                    logger.info("Static codes from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred in static data loading from disk", e);
        }

        return isLoaded;
    }

    private void loadTermDeposits() {
        try {
            String filePath = getAvaloqSpecificFilePath(TERM_DEPO_CACHE_FILE);
            boolean termDepositFileExists = serializerService.isCacheFileExist(filePath);
            if (termDepositFileExists) {
                logger.info("Loading Term Deposits from file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<InterestRateCard> termDepoList = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (termDepoList != null) {
                    cache.putAll(termDepoList, CacheType.TERM_DEPOSIT_RATES_CACHE, new InterestRateCardImpl(), true);
                    logger.info("{} Term Deposits were loaded successfully", termDepoList.size());
                } else {
                    logger.info("Term Deposits from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred loading Term Deposits from disk", e);
        }
    }

    private void loadBrokers() {
        try {
            String filePath = getAvaloqSpecificFilePath(BROKER_CACHE_FILE);
            boolean brokerFileExists = serializerService.isCacheFileExist(filePath);
            if (brokerFileExists) {
                logger.info("Loading Brokers from file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<JobUserBroker> brokersList = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (brokersList != null) {
                    cache.putAll(brokersList, true);
                    cache.logCache(CacheType.JOB_USER_BROKER_CACHE, "BrokerCacheService", false);
                    logger.info("{} Brokers were loaded successfully", brokersList.size());
                } else {
                    logger.info("Brokers from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred loading brokers from disk", e);
        }
    }

    private void loadAssets() {
        try {
            String filePath = getAvaloqSpecificFilePath(ASSETS_CACHE_FILE);
            boolean assetsFileExists = serializerService.isCacheFileExist(filePath);
            if (assetsFileExists) {
                logger.info("Loading Assets from file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<ShareAsset> assetList = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (assetList != null) {
                    cache.putAll(assetList, CacheType.ASSET_DETAILS, new KeyGetter() {
                        @Override
                        public Object getKey(Object obj) {
                            Asset asset = (Asset) obj;
                            if (asset != null) {
                                return asset.getAssetId();
                            }
                            return null;
                        }
                    }, true);
                    logger.info("{} Assets were loading successfully.", assetList.size());
                } else {
                    logger.info("Assets from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred loading Assets from disk", e);
        }

    }

    private void loadAPL() {
        try {
            String filePath = getAvaloqSpecificFilePath(APL_CACHE_FILE);
            boolean aplExists = serializerService.isCacheFileExist(filePath);
            if (aplExists) {
                logger.info("Loading APL from the file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<AplCacheServiceImpl.DealerGroupAplProduct> dealerGroupAplProducts = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (dealerGroupAplProducts != null) {
                    cache.putAll(dealerGroupAplProducts, true);
                    logger.info("{} APL loaded successfully", dealerGroupAplProducts.size());
                } else {
                    logger.info("APL from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred loading APLs from disk", e);
        }

    }

    private void loadTransactionFees() {
        try {
            String filePath = getAvaloqSpecificFilePath(TRANSACTION_FEES_CACHE_FILE);
            boolean transactionFeesExists = serializerService.isCacheFileExist(filePath);
            if (transactionFeesExists) {
                logger.info("Loading Transaction Fees from file [{}]", filePath);
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(List.class);
                List<AvaloqTransactionFee> transactionFeeList = (List) diskCacheSerializer.readObjectFromFile(filePath);
                if (transactionFeeList != null) {
                    cache.putAll(transactionFeeList, TRANSACTION_FEES, new KeyGetter() {
                        @Override
                        public Object getKey(Object obj) {
                            return Integer.toString(((AvaloqTransactionFee) obj).generateKey());
                        }
                    });
                    logger.info("{} Transaction Fees were loaded successfully", transactionFeeList.size());
                } else {
                    logger.info("Transaction Fees from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred loading Transaction Fees from disk", e);
        }
    }

    /* Bank Data is not cached so Actual Service call happens here.*/
    private void loadBankDate() {
        try {
            String filePath = getAvaloqSpecificFilePath(BANK_DATE_CACHE_FILE);
            boolean fileExists = serializerService.isCacheFileExist(filePath);
            if (fileExists) {
                DiskCacheSerializer diskCacheSerializer = serializerService.createDiskSerializer(DateTime.class);
                DateTime bankDate = (DateTime) diskCacheSerializer.readObjectFromFile(filePath);
                if (bankDate != null) {
                    cache.put(bankDate, CacheType.BANK_DATE, new KeyGetter() {
                        @Override
                        public Object getKey(Object o) {
                            return Constants.BANKDATE;
                        }
                    });
                    logger.info("{} Bank date was successfully loaded from file", bankDate);
                } else {
                    logger.info("Bank date from disk is null");
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred when loading the bank date from disk", e);
        }
    }
}
