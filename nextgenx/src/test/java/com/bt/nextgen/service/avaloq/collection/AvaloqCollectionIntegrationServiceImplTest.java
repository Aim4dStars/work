package com.bt.nextgen.service.avaloq.collection;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.collection.Collection;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AvaloqCollectionIntegrationServiceImplTest extends BaseSecureIntegrationTest {

    @Autowired
    AvaloqCollectionIntegrationServiceImpl collectionService;

    private ServiceErrors serviceErrors = new ServiceErrorsImpl();

    @Test
    public void testLoadAssetsForCollectionSuccess() {
        List<String> assetIds = collectionService.loadAssetsForCollection("AL.OE.AVSR.PILOT.PROD", serviceErrors);
        assertFalse(serviceErrors.hasErrors());
        assertEquals(assetIds.size(), 4);
        assertEquals(assetIds.get(0), "382630");
    }

    @Test
    public void testLoadCollectionAssetsMapSuccess() {
        Map<String, Collection> assetMap = collectionService.loadCollectionAssetsMap(
                Arrays.asList("AL.OE.AVSR.PILOT.PROD", "AL.OE.AVSR.PILOT.IDX"), serviceErrors);

        assertFalse(serviceErrors.hasErrors());
        assertEquals(assetMap.size(), 2);
        assertEquals(assetMap.get("AL.OE.AVSR.PILOT.IDX").getCollectionId(), "5678");
        assertEquals(assetMap.get("AL.OE.AVSR.PILOT.IDX").getCollectionSymId(), "AL.OE.AVSR.PILOT.IDX");
        assertEquals(assetMap.get("AL.OE.AVSR.PILOT.IDX").getAssetIds().size(), 58);
    }

    @Test
    public void testLoadAssetsForCollectionFailure() {
        List<String> assetIds = collectionService.loadAssetsForCollection("PILOT", serviceErrors);
        assertEquals(assetIds.size(), 0);
    }

}
