package com.bt.nextgen.smsf.service;

import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.model.AssetDto;
import com.bt.nextgen.api.smsf.service.AssetDetailsDtoService;
import com.bt.nextgen.api.smsf.service.AssetDetailsDtoServiceImpl;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetDetailsDtoServiceImplTest
{

    @InjectMocks
    private AssetDetailsDtoService assetDetailsDtoService = new AssetDetailsDtoServiceImpl();

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;



    private Map<String, Asset> getExternalAssets()
    {
        Map<String, Asset> results = new HashMap<>();


        AssetImpl asset1 = new AssetImpl();
        asset1.setAssetId("11");
        asset1.setAssetClassId(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        asset1.setAssetCode("CODE1ls");
        asset1.setAssetName("codename1-ls");
        asset1.setCluster(AssetType.AUSTRALIAN_LISTED_SECURITIES);
        results.put(asset1.getAssetId(),asset1);


        AssetImpl asset2 = new AssetImpl();
        asset2.setAssetId("22");
        asset2.setAssetClassId(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        asset2.setAssetCode("code-ls");
        asset2.setAssetName("codename-LS");
        asset2.setCluster(AssetType.AUSTRALIAN_LISTED_SECURITIES);
        results.put(asset2.getAssetId(),asset2);


        //MANAGED FUNDS - assettype - start

        AssetImpl asset3 = new AssetImpl();
        asset3.setAssetId("33");
        asset3.setAssetClassId(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        asset3.setAssetCode("RIM0032AU");
        asset3.setAssetName("Russell Global Opportunities Fund");
        asset3.setCluster(AssetType.MANAGED_FUND);
        results.put(asset3.getAssetId(),asset3);


        AssetImpl asset4 = new AssetImpl();
        asset4.setAssetId("44");
        asset4.setAssetClassId(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
        asset4.setAssetCode("RIM0038AU");
        asset4.setAssetName("Russell Emerging Markets Fund");
        asset4.setCluster(AssetType.MANAGED_FUND);
        results.put(asset4.getAssetId(),asset4);

        AssetImpl asset5 = new AssetImpl();
        asset5.setAssetId("55");
        asset5.setAssetClassId(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        asset5.setAssetCode("IML0005AU");
        asset5.setAssetName("Investors Mutual Equity Income Fund");
        asset5.setCluster(AssetType.MANAGED_FUND);
        results.put(asset5.getAssetId(),asset5);

        AssetImpl asset6 = new AssetImpl();
        asset6.setAssetId("66");
        asset6.setAssetClassId(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        asset6.setAssetCode("HOW0121AU");
        asset6.setAssetName("Challenger Wholesale Socially Responsive Share Fund");
        asset6.setCluster(AssetType.MANAGED_FUND);
        results.put(asset6.getAssetId(),asset6);

        AssetImpl asset7 = new AssetImpl();
        asset7.setAssetId("991");
        asset7.setAssetClassId(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        asset7.setAssetCode("WFT.STPLD");
        asset7.setAssetName("Westfield Trust (Stapled Security Underlying)");
        asset7.setCluster(AssetType.AUSTRALIAN_LISTED_SECURITIES);
        results.put(asset7.getAssetId(),asset7);


        //MANAGED FUNDS - assettype - Count=4 - END



        return results;
    }

    @Test
    public void assetTypeManagedFund_codenameTest()
    {
        when(assetIntegrationService.loadExternalAssets(any(ServiceErrorsImpl.class))).thenReturn(getExternalAssets());

        List<ApiSearchCriteria> criteria = Collections.singletonList(new ApiSearchCriteria(Attribute.ASSETTYPEINTLID,
                ApiSearchCriteria.SearchOperation.EQUALS,
                "mf",
                ApiSearchCriteria.OperationType.STRING));

        List<AssetDto> assetDtos = assetDetailsDtoService.search(criteria, new FailFastErrorsImpl());

        assertNotNull(assetDtos);
        assertFalse(assetDtos.isEmpty());

        Assert.assertEquals(4, assetDtos.size());

    }

    @Test
    public void testStapledSecuritiesAreBeingFilteredOut()
    {
        when(assetIntegrationService.loadExternalAssets(any(ServiceErrorsImpl.class))).thenReturn(getExternalAssets());

        List<ApiSearchCriteria> criteria = Collections.singletonList(new ApiSearchCriteria(Attribute.ASSETTYPEINTLID,
                ApiSearchCriteria.SearchOperation.EQUALS,
                "ls",
                ApiSearchCriteria.OperationType.STRING));

        List<AssetDto> assetDtos = assetDetailsDtoService.search(criteria, new FailFastErrorsImpl());

        assertNotNull(assetDtos);
        assertFalse(assetDtos.isEmpty());
        Assert.assertEquals(2, assetDtos.size());

        for (AssetDto dto : assetDtos)
        {
            assert(!dto.getAssetCode().contains(".STPLD"));
        }
    }


    @Test
    public void assetTypeListedSecurity_codenameTest()
    {
        when(assetIntegrationService.loadExternalAssets(any(ServiceErrorsImpl.class))).thenReturn(getExternalAssets());

        List<ApiSearchCriteria> criteria = Collections.singletonList(new ApiSearchCriteria(Attribute.ASSETTYPEINTLID,
                ApiSearchCriteria.SearchOperation.EQUALS,
                "ls",
                ApiSearchCriteria.OperationType.STRING));

        List<AssetDto> assetDtos = assetDetailsDtoService.search(criteria, new FailFastErrorsImpl());

        assertNotNull(assetDtos);
        assertFalse(assetDtos.isEmpty());

        Assert.assertEquals(2, assetDtos.size());

    }
}
