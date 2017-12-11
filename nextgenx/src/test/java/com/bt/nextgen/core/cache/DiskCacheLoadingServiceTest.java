package com.bt.nextgen.core.cache;

import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.aal.AalIndexIntegrationService;
import com.bt.nextgen.service.avaloq.asset.aal.BrokerProductAssetIntegrationService;
import com.bt.nextgen.service.avaloq.asset.aal.IndexAssetIntegrationService;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

/**
 * Created by M041926 on 22/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DiskCacheLoadingServiceTest {

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

    @Mock
    private BrokerProductAssetIntegrationService brokerProductAssetService;

    @Mock
    private AalIndexIntegrationService aalIndexIntegrationService;

    @Mock
    private IndexAssetIntegrationService indexAssetIntegrationService;

    @InjectMocks
    private DiskCacheLoadingService diskCacheLoadingService;

    @Before
    public void setup() throws Exception {
        when(serializer.readObjectFromFile(argThat(new TypeSafeMatcher<String>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("BANK_DATE_CACHE.bin");
            }

            @Override
            protected boolean matchesSafely(String item) {
                return item.contains("BANK_DATE_CACHE.bin");
            }
        }))).thenReturn(new DateTime());

        when(serializer.readObjectFromFile(argThat(new TypeSafeMatcher<String>() {
            @Override
            protected boolean matchesSafely(String item) {
                return !item.contains("BANK_DATE_CACHE.bin");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("not BANK_DATE_CACHE.bin");
            }

        }))).thenReturn(Collections.singletonList(new Object()));

        when(serializerService.createDiskSerializer(any(Class.class))).thenReturn(serializer);
        when(featureToggles.getFeatureToggle(FeatureToggles.DISK_CACHE_SERIALIZATION)).thenReturn(true);
        when(serializerService.isAllCacheFilesExist()).thenReturn(true);
        when(serializerService.isCacheFileExist(anyString())).thenReturn(true);
        when(togglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
    }

    @Test
    public void testLoadCache() throws Exception {
        diskCacheLoadingService.loadCache();
        verify(serializer, times(11)).readObjectFromFile(anyString());
    }
}