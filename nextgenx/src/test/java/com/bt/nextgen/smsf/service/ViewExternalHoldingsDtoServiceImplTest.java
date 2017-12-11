package com.bt.nextgen.smsf.service;


import com.bt.nextgen.api.smsf.model.*;
import com.bt.nextgen.api.smsf.service.ViewExternalHoldingsDtoService;
import com.bt.nextgen.api.smsf.service.ViewExternalHoldingsDtoServiceImpl;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.externalasset.service.ExternalAssetIntegrationService;
import com.bt.nextgen.service.integration.externalasset.model.OnPlatformExternalAssetImpl;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ViewExternalHoldingsDtoServiceImplTest
{
    @Mock
    ExternalAssetIntegrationService externalAssetIntegrationService;

    @InjectMocks
    ViewExternalHoldingsDtoService viewExternalHoldingsDtoService = new ViewExternalHoldingsDtoServiceImpl();


    @Before
    public void init()
    {
        Mockito.when(externalAssetIntegrationService.getExternalAssets(
                Mockito.any(List.class), Mockito.any(DateTime.class))).thenReturn(getAssetHoldings());
    }


    @Test
    public void getSingleAssetHoldings()
    {
        ApiSearchCriteria bpIdCriteria = new ApiSearchCriteria("account_id", ApiSearchCriteria.SearchOperation.EQUALS, "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");
        List<ApiSearchCriteria> searchCriteria = new ArrayList<>();
        searchCriteria.add(bpIdCriteria);

        List<ExternalAssetHoldingsValuationDto> holdingsDtoList = viewExternalHoldingsDtoService.search(searchCriteria, new ServiceErrorsImpl());
        assertEquals(1, holdingsDtoList.size());

        ExternalAssetHoldingsValuationDto holdingsDto = holdingsDtoList.get(0);
        assertEquals(1, holdingsDto.getValuationByAssetClass().size());

        ExternalAssetClassValuationDto classValuation = holdingsDto.getValuationByAssetClass().get(0);
        assertEquals(AssetClass.AUSTRALIAN_LISTED_SECURITIES.getCode(), classValuation.getAssetClass());
        assertEquals(new BigDecimal(1).setScale(3), classValuation.getPercentageOfPortfolio());
        assertEquals(new BigDecimal(32000), classValuation.getTotalMarketValue());
        assertEquals(1, classValuation.getAssetList().size());

        ExternalAssetDto extAssetDto = classValuation.getAssetList().get(0);
        assertEquals("BHP", extAssetDto.getAssetName());
        assertEquals("ETRADE", extAssetDto.getSource());
        assertEquals("32000", extAssetDto.getMarketValue());
    }


    private AssetHoldings getAssetHoldings()
    {
        ExternalAsset listedSecurityAsset = new OnPlatformExternalAssetImpl();
        listedSecurityAsset.setAssetName("BHP");
        listedSecurityAsset.setPositionCode("BHP");
        listedSecurityAsset.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        listedSecurityAsset.setSource("ETRADE");
        listedSecurityAsset.setPositionCode("BHP");
        listedSecurityAsset.setMarketValue(new BigDecimal(32000));
        listedSecurityAsset.setValueDate(new DateTime());
        listedSecurityAsset.setPositionIdentifier(new PositionIdentifierImpl("12345"));
        listedSecurityAsset.setQuantity(new BigDecimal(150));
        listedSecurityAsset.setAssetType(AssetType.AUSTRALIAN_LISTED_SECURITIES);

        AssetClassValuation valuation = new AssetClassValuationImpl(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
        List<ExternalAsset> assetList = new ArrayList<>();
        assetList.add(listedSecurityAsset);
        valuation.setAssets(assetList);

        AssetHoldings holdings = new AssetHoldingsImpl();
        List<AssetClassValuation> assetClassValuations = new ArrayList<>();
        assetClassValuations.add(valuation);
        holdings.setAssetClassValuations(assetClassValuations);

        return holdings;
    }
}
