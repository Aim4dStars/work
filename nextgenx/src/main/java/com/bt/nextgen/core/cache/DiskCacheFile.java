package com.bt.nextgen.core.cache;

import com.bt.nextgen.core.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Upul Doluweera on 7/03/2016.
 */
public enum DiskCacheFile {

    STATIC_CODES_CACHE_FILE("STATIC_CODE_CACHE.bin"),
    BROKER_CACHE_FILE("BROKER_CACHE.bin"),
    TERM_DEPO_CACHE_FILE("TERM_DEPO_CACHE.bin"),
    ASSETS_CACHE_FILE("ASSETS_CACHE.bin"),
    BROKER_PRODUCT_ASSETS_CACHE_FILE("AVAILABLE_ASSETS_CACHE.bin"),
    APL_CACHE_FILE("APL_CACHE.bin"),
    TRANSACTION_FEES_CACHE_FILE("TRANACTION_FEES_CACHE.bin"),
    AAL_LIST_INDEX_CACHE_FILE("AAL_LIST_INDEX_CACHE.bin"),
    INDEX_ASSET_CACHE_FILE("INDEX_ASSET_CACHE.bin"),
    LICENSE_ADVISER_FEES_CACHE_FILE("LICENSE_ADVISER_FEES_CACHE.bin"),
    BANK_DATE_CACHE_FILE("BANK_DATE_CACHE.bin");

    private static final Logger logger = LoggerFactory.getLogger(DiskCacheFile.class);

    DiskCacheFile(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public java.lang.String getFileName() {
        return fileName;
    }

    public static String getAvaloqSpecificFilePath(DiskCacheFile diskCacheFile) {
        String cacheFilePath = Properties.getString("disk.cache.env.path") + "/" + diskCacheFile.getFileName();
        logger.info("File Path Location {}", cacheFilePath);
        return cacheFilePath;
    }
}
