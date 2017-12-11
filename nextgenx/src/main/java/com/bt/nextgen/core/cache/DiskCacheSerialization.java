package com.bt.nextgen.core.cache;

import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.asset.InterestRateCard;
import com.bt.nextgen.service.avaloq.asset.aal.AalIndex;
import com.bt.nextgen.service.avaloq.asset.aal.BrokerProductAsset;
import com.bt.nextgen.service.avaloq.asset.aal.IndexAsset;
import com.bt.nextgen.service.avaloq.broker.JobUserBroker;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingTariff;
import com.bt.nextgen.service.avaloq.product.AplCacheServiceImpl;
import com.bt.nextgen.service.avaloq.transactionfee.AvaloqTransactionFee;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.cache.DiskCacheFile.*;

/**
 * Created by Upul Doluweera on 4/03/2016.
 */
@Service
public class DiskCacheSerialization {

    private static final Logger logger = LoggerFactory.getLogger(DiskCacheSerialization.class);

    @Autowired
    private GenericCache cache;

    @Autowired
    private DiskSerializerService serializerFactory;

    @Autowired
    private FeatureTogglesService togglesService;

    public void persistCacheOnDisk() {

        boolean isEnabled = togglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle(FeatureToggles.DISK_CACHE_SERIALIZATION);

        if (isEnabled) {
            logger.info("Serializing eh cache to the disk..");

            createCacheDirectory();

            serializeStaticCodesIntoDisk(getAvaloqSpecificFilePath(STATIC_CODES_CACHE_FILE));
            serializeTermDepositsRatesIntoDisk(getAvaloqSpecificFilePath(TERM_DEPO_CACHE_FILE));
            serializeBrokersIntoDisk(getAvaloqSpecificFilePath(BROKER_CACHE_FILE));
            serializeAssetsIntoDisk(getAvaloqSpecificFilePath(ASSETS_CACHE_FILE));
            serializeAplIntoDisk(getAvaloqSpecificFilePath(APL_CACHE_FILE));
            serializeTransactionFeesIntoDisk(getAvaloqSpecificFilePath(TRANSACTION_FEES_CACHE_FILE));
            serializeBrokerProductAssetsIntoDisk(getAvaloqSpecificFilePath(BROKER_PRODUCT_ASSETS_CACHE_FILE));
            serializeAalIndexesIntoDisk(getAvaloqSpecificFilePath(AAL_LIST_INDEX_CACHE_FILE));
            serializeIndexAssetsIntoDisk(getAvaloqSpecificFilePath(INDEX_ASSET_CACHE_FILE));
            serializeLicenseAdviserFeesIntoDisk(getAvaloqSpecificFilePath(LICENSE_ADVISER_FEES_CACHE_FILE));
            serializeBankDateIntoDisk(getAvaloqSpecificFilePath(BANK_DATE_CACHE_FILE));

            logger.info("Serialization of ehcache to disk was successful...");
        } else {
            logger.info("Serialization usage feature is not enabled.");
        }
    }

    private void createCacheDirectory() {
        String cacheFilesPath = Properties.getString("disk.cache.env.path") + "/";

        logger.info("Cache file location is {}", cacheFilesPath);

        File cacheFileLocation = new File(cacheFilesPath);
        if (!cacheFileLocation.exists()) {
            logger.info("Cache file location {} doesn't exist. Going to create one.", cacheFilesPath);
            cacheFileLocation.mkdirs();
        }
    }

    private void serializeTransactionFeesIntoDisk(String filePath) {

        logger.info("Serializing Transaction Fees into {}.", filePath);

        Map<String, AvaloqTransactionFee> transactionFeesMap = (Map<String, AvaloqTransactionFee>) cache.getAll(CacheType.TRANSACTION_FEES);
        Collection<AvaloqTransactionFee> transactionFeeCollection = transactionFeesMap.values();
        List<AvaloqTransactionFee> transactionFeeList = new ArrayList<>(transactionFeeCollection);

        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, transactionFeeList);
        } catch (Exception e) {
            logger.error("Error occurred while serializing Transaction Fees", e);
        }
        logger.info("Serialization of {} Transaction fees were successful.", transactionFeeList.size());
    }

    private void serializeAplIntoDisk(String filePath) {

        logger.info("Serializing APL cache into {} ", filePath);
        Map<String, AplCacheServiceImpl.DealerGroupAplProduct> aplCacheMap = (Map<String, AplCacheServiceImpl.DealerGroupAplProduct>) cache.getAll(CacheType.ADVISER_PRODUCT_LIST_CACHE);
        ArrayList<AplCacheServiceImpl.DealerGroupAplProduct> dealerGroupAplProductArrayList = new ArrayList<>(aplCacheMap.values());

        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, dealerGroupAplProductArrayList);
        } catch (Exception e) {
            logger.error("Error occurred while serializing APL cache", e);
        }
        logger.info("Serialization of {} APLs were successful", dealerGroupAplProductArrayList.size());
    }



    private void serializeAssetsIntoDisk(String filePath) {

        logger.info("Serializing Assets Cache into {}", filePath);
        Map<String, Asset> assetsCacheMap = (Map<String, Asset>) cache.getAll(CacheType.ASSET_DETAILS);
        ArrayList<Asset> assetArrayList = new ArrayList<>(assetsCacheMap.values());

        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, assetArrayList);
        } catch (Exception e) {
            logger.error("Error occurred while serializing Assets", e);
        }
        logger.info("Serialization of {} Assets were successful", assetArrayList.size());
    }

    private void serializeTermDepositsRatesIntoDisk(String filePath) {

        logger.info("Serializing TermDepositsRates cache into {}", filePath);
        Map<String, InterestRateCard> termDepositCacheMap = (Map<String, InterestRateCard>) cache.getAll(CacheType.TERM_DEPOSIT_RATES_CACHE);
        List<InterestRateCard> interestRateCardList = new ArrayList<>(termDepositCacheMap.values());

        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, interestRateCardList);
        } catch (Exception e) {
            logger.error("Error occurred when serializing term deposit rates to disk", e);
        }
        logger.info("Serialization of {} TermDepositsRates were successful...", interestRateCardList.size());
    }

    private void serializeBrokersIntoDisk(String filePath) {

        logger.info("Serializing broker cache into {}", filePath);
        Map<String, JobUserBroker> jobUserBrokerMap = (Map<String, JobUserBroker>)
                cache.getAll(CacheType.JOB_USER_BROKER_CACHE);
        ArrayList<JobUserBroker> brokersList = new ArrayList<>(jobUserBrokerMap.values());

        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, brokersList);
        } catch (Exception e) {
            logger.error("Error occurred in serializing brokers.", e);
        }
        logger.info("serialization of {} JobUserBrokers were successful.", brokersList.size());
    }

    private void serializeStaticCodesIntoDisk(String filePath) {

        logger.info("Serializing static code cache into {}", filePath);
        Map<String, Code> staticCodeCache = (Map<String, Code>) cache.getAll(CacheType.STATIC_CODE_CACHE);
        ArrayList<Code> staticCodesList = new ArrayList<>(staticCodeCache.values());
        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, staticCodesList);
        } catch (Exception e) {
            logger.error("Error occurred in serializing static codes", e);
        }
        logger.info("Serialization of {} static codes were successful.", staticCodesList.size());
    }

    private void serializeBrokerProductAssetsIntoDisk(String filePath) {
        logger.info("Serializing broker product assets cache into {}", filePath);

        Map<String, BrokerProductAsset> brokerProductAssetCache = (Map<String, BrokerProductAsset>) cache.getAll(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE);
        ArrayList<BrokerProductAsset> brokerProductAssets = new ArrayList<>(brokerProductAssetCache.values());
        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, brokerProductAssets);
        } catch (Exception e) {
            logger.error("Error occurred in serializing broker product assets", e);
        }

        logger.info("Serialization of {} broker product assets were successful.", brokerProductAssets.size());
    }

    private void serializeAalIndexesIntoDisk(String filePath) {
        logger.info("Serializing aal indexes cache into {}", filePath);
        Map<String, AalIndex> aalIndexesCache = (Map<String, AalIndex>) cache.getAll(CacheType.AVAILABLE_ASSET_LIST_INDEX_CACHE);
        ArrayList<AalIndex> aalIndexes = new ArrayList<>(aalIndexesCache.values());
        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, aalIndexes);
        } catch (Exception e) {
            logger.error("Error occurred in serializing aal indexes", e);
        }

        logger.info("Serialization of {} aal indexes were successful.", aalIndexes.size());
    }

    private void serializeIndexAssetsIntoDisk(String filePath) {
        logger.info("Serializing index assets cache into {}", filePath);
        Map<String, IndexAsset> indexAssetsCache = (Map<String, IndexAsset>) cache.getAll(CacheType.INDEX_ASSET_CACHE);
        ArrayList<IndexAsset> indexAssets = new ArrayList<>(indexAssetsCache.values());
        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, indexAssets);
        } catch (Exception e) {
            logger.error("Error occurred in serializing index assets", e);
        }

        logger.info("Serialization of {} index assets were successful.", indexAssets.size());
    }

    private void serializeLicenseAdviserFeesIntoDisk(String filePath) {
        logger.info("Serializing license adviser fees cache into {}", filePath);
        Map<String, FeeDgOngoingTariff> licenseAdviserFeesCache = (Map<String, FeeDgOngoingTariff>) cache.getAll(CacheType.LICENSE_ADVISER_FEES);
        ArrayList<FeeDgOngoingTariff> licenseAdviserFees = new ArrayList<>(licenseAdviserFeesCache.values());
        try {
            DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(List.class);
            diskCacheSerializer.writeObjectToFile(filePath, licenseAdviserFees);
        } catch (Exception e) {
            logger.error("Error occurred in serializing license adviser fees", e);
        }

        logger.info("Serialization of {} license adviser fees were successful.", licenseAdviserFees.size());
    }

    private void serializeBankDateIntoDisk(String filePath) {

        logger.info("Serializing bank date cache into {}", filePath);

        DateTime bankDate = (DateTime) cache.get(CacheType.BANK_DATE, Constants.BANKDATE, new ValueGetter() {
            @Override
            public Object getValue(Object o) {
                return null;
            }
        });

        if (bankDate != null) {
            try {
                DiskCacheSerializer diskCacheSerializer = serializerFactory.createDiskSerializer(DateTime.class);
                diskCacheSerializer.writeObjectToFile(filePath, bankDate);
                logger.info("Serialization of {} bank date were successful.", bankDate);
            } catch (Exception e) {
                logger.error("Error occurred in serializing bank date", e);
            }
        } else {
            logger.error("Bank date from cache is null");
        }
    }
}
