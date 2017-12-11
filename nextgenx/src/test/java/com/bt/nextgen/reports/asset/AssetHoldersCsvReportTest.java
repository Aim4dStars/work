package com.bt.nextgen.reports.asset;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.asset.model.AssetHoldersDto;
import com.bt.nextgen.api.asset.service.AssetHoldersDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class AssetHoldersCsvReportTest {

    @InjectMocks
    AssetHoldersCsvReport assetHoldersCsvReport;

    @Mock
    private AssetHoldersDtoService assetHoldersDtoService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private ContentDtoService contentService;

    private List<AssetHoldersDto> assetHoldersDtoList;
    private Asset asset;
    private Map<String, Object> params = new HashMap<>();
    private Map<String, Object> dataCollections = new HashMap<>();

    @Before
    public void setup() {
        assetHoldersDtoList = createAssetHoldersList();
        asset = createAsset();
        ContentDto contentDto = Mockito.mock(ContentDto.class);
        Mockito.when(contentDto.getContent()).thenReturn("DS-IP-0054");

        Mockito.when(assetHoldersDtoService.search(anyList(), any(ServiceErrors.class))).thenReturn(assetHoldersDtoList);
        Mockito.when(assetIntegrationService.loadAsset(anyString(), any(ServiceErrors.class))).thenReturn(asset);
        Mockito.when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);
    }

    private Asset createAsset() {
        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetId()).thenReturn("121153");
        Mockito.when(asset.getAssetName()).thenReturn("ANZ banking");
        Mockito.when(asset.getAssetCode()).thenReturn("ANZ");
        return asset;
    }

    private List<AssetHoldersDto> createAssetHoldersList() {
        AssetHoldersDto assetHoldersDto = Mockito.mock(AssetHoldersDto.class);
        AccountDto accountDto = Mockito.mock(AccountDto.class);

        Mockito.when(accountDto.getAccountName()).thenReturn("Account name");
        Mockito.when(accountDto.getAdviserName()).thenReturn("Adviser name");
        Mockito.when(accountDto.getProduct()).thenReturn("Product name");
        Mockito.when(accountDto.getAccountTypeDescription()).thenReturn("Trust");

        Mockito.when(assetHoldersDto.getAssetPrice()).thenReturn(BigDecimal.valueOf(21.30));
        Mockito.when(assetHoldersDto.getPriceDate()).thenReturn(new DateTime("2016-10-10"));
        Mockito.when(assetHoldersDto.getAccount()).thenReturn(accountDto);

        return asList(assetHoldersDto);
    }

    @Test
    public void testGetAssetHolders() {
        params.put("asset-id", "121153");

        List<AssetHoldersDto> result = assetHoldersCsvReport.getAssetHolders(params, dataCollections);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).getAssetPrice(), BigDecimal.valueOf(21.30));
        Assert.assertEquals(result.get(0).getPriceDate(), new DateTime("2016-10-10"));

        Assert.assertNotNull(result.get(0).getAccount());
        Assert.assertEquals(result.get(0).getAccount().getAccountName(), "Account name");
        Assert.assertEquals(result.get(0).getAccount().getAdviserName(), "Adviser name");
        Assert.assertEquals(result.get(0).getAccount().getProduct(), "Product name");
        Assert.assertEquals(result.get(0).getAccount().getAccountTypeDescription(), "Trust");
    }

    @Test
    public void testGetAssetHolders_forInvalidAsset() {
        params.put("asset-id", "121153");
        Mockito.when(assetIntegrationService.loadAsset(anyString(), any(ServiceErrors.class))).thenReturn(null);

        List<AssetHoldersDto> result = assetHoldersCsvReport.getAssetHolders(params, dataCollections);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 0);
    }

    @Test
    public void testReportName() {
        String reportName = assetHoldersCsvReport.getReportName(params);
        Assert.assertNotNull(reportName);
        Assert.assertEquals("Client holdings report", reportName);
    }

    @Test
    public void testDisclaimer() {
        String disclaimer = assetHoldersCsvReport.getDisclaimer(params);
        Assert.assertNotNull(disclaimer);
        Assert.assertEquals("DS-IP-0054", disclaimer);
    }
}
