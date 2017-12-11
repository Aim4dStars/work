package com.bt.nextgen.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.bt.nextgen.core.cache.DiskCacheFile.getAvaloqSpecificFilePath;

/**
 * Created by M041926 on 22/02/2017.
 */
@Component
public class DiskSerializerService {

    private static final Logger logger = LoggerFactory.getLogger(DiskSerializerService.class);

    public DiskCacheSerializer createDiskSerializer(Class<?> type) {
        return new DiskCacheSerializer(type);
    }

    public boolean isAllCacheFilesExist() {
        boolean allCacheFilesExist = true;

        for (DiskCacheFile cacheFile : DiskCacheFile.values()) {
            String cacheFilePath = getAvaloqSpecificFilePath(cacheFile);
            File file = new File(cacheFilePath);
            if (!file.exists()) {
                logger.info("CACHE_FILE_NOT_FOUND. {} doesn't exist", cacheFilePath);
                allCacheFilesExist = false;
            }
        }

        return allCacheFilesExist;
    }

    public boolean isCacheFileExist(String path) {
        return new File(path).isFile();
    }
}
