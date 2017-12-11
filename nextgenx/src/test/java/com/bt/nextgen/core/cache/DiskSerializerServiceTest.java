package com.bt.nextgen.core.cache;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by M041926 on 22/02/2017.
 */
public class DiskSerializerServiceTest {

    private DiskSerializerService service = new DiskSerializerService();

    @Test
    public void isAllCacheFilesExist() throws Exception {
        assertFalse(service.isAllCacheFilesExist());
    }

}