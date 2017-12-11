package com.bt.nextgen.core.cache;

import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by M041926 on 22/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiskCacheSerializationTest {

    @Mock
    private GenericCache cache;

    @Mock
    private DiskSerializerService serializerService;

    @Mock
    private DiskCacheSerializer serializer;

    @Mock
    private FeatureTogglesService togglesService;

    @Mock
    private FeatureToggles featureToggles;

    @InjectMocks
    private DiskCacheSerialization diskCacheSerialization;

    @Before
    public void setup() {
        when(serializerService.createDiskSerializer(any(Class.class))).thenReturn(serializer);
        when(serializerService.isAllCacheFilesExist()).thenReturn(true);
        when(featureToggles.getFeatureToggle(FeatureToggles.DISK_CACHE_SERIALIZATION)).thenReturn(true);
        when(togglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
    }

    @Test
    public void testPersistCacheOnDisk() throws Exception {
        diskCacheSerialization.persistCacheOnDisk();
        verify(serializer, times(10)).writeObjectToFile(anyString(), Matchers.anyObject());
    }
}