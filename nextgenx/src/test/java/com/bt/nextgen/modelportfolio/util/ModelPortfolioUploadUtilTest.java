package com.bt.nextgen.modelportfolio.util;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioAssetAllocationDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioUploadDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioUploadUtilTest
{
	@InjectMocks
	private final ModelPortfolioUploadUtilImpl modelUtil = new ModelPortfolioUploadUtilImpl();

	@Mock
    private CmsService cmsService;

	@Mock
    private AssetIntegrationService assetService;

    private MockMultipartFile file;
    private MockMultipartFile emptyFile;
    private MockMultipartFile invalidFileType;
    private String[] commentary;
    private String[] cashRow;
    private String[] assetNoAllocation;
    private String[] assetNoTradePercent;
    private Map<String, Asset> assetMap = new HashMap<>();
    private List<Asset> assetList = new ArrayList<>();

    private String percentage;
    private String percentageWithSign;
    private BigDecimal defaultVal;

	@Before
	public void setup() throws Exception
	{
		InputStream input = getClass().getResourceAsStream("/csv/testUploadFile.csv");
		file = new MockMultipartFile("testUploadFile", "testUploadFile.csv", MediaType.TEXT_PLAIN_VALUE, input);

		InputStream emptyInput = getClass().getResourceAsStream("/csv/emptyUploadFile.csv");
		emptyFile = new MockMultipartFile("testUploadFile", "testUploadFile.csv", MediaType.TEXT_PLAIN_VALUE, emptyInput);
		emptyInput = getClass().getResourceAsStream("/csv/emptyUploadFile.csv");
		invalidFileType = new MockMultipartFile("testUploadFile", "testUploadFile.csv", MediaType.TEXT_XML_VALUE, emptyInput);

		commentary = new String[4];
		commentary[0] = "Commentary";
		commentary[1] = "Lorem ipsum";
		commentary[2] = "dolor sit amet";
		commentary[3] = "consectetur adipiscing elit";

        cashRow = new String[3];
        cashRow[0] = "Model Cash";
        cashRow[1] = "3.00%";
        cashRow[2] = "-";

		assetNoAllocation = new String[3];
		assetNoAllocation[0] = "AGK";
		assetNoAllocation[1] = "";
		assetNoAllocation[2] = "0.50%";

		assetNoTradePercent = new String[3];
		assetNoTradePercent[0] = "ANZ";
		assetNoTradePercent[1] = "8.39%";
		assetNoTradePercent[2] = "";

		percentage = "3.15";
		percentageWithSign = "4.56%";
		defaultVal = new BigDecimal("9.54");

        mockAssets();
	}

    private void mockAssets() {
        String[] assetCodes = { "AGK", "ANZ", "BBG", "CBA", "CWN", "DDL", "NAB", "SHL", "SKI", "TAH", "TLS", "UGL", "WES",
                "MACC.MP.AUD" };
        for(String assetCode: assetCodes) {
            Asset asset;
            asset = Mockito.mock(Asset.class);
            when(asset.getAssetCode()).thenReturn(assetCode);
            assetList.add(asset);
            assetMap.put(asset.getAssetCode(), asset);            
        }
        when(assetService.loadAssetsForAssetCodes(Mockito.anyList(), Mockito.any(ServiceErrors.class))).thenReturn(assetList);
        
        
        Map <String, Asset> cashAssetMap = new HashMap <> ();
        AssetImpl asset = Mockito.mock(AssetImpl.class);
        when(asset.getAssetCode()).thenReturn(null);
        when(asset.getMoneyAccountType()).thenReturn("Managed Portfolio");
        cashAssetMap.put("MACC.MP.AUD", asset);
        when(assetService.loadAssetsForCriteria(Mockito.anyList(), Mockito.anyString(), Mockito.anyCollection(),
                        Mockito.any(ServiceErrors.class))).thenReturn(cashAssetMap);
    }

    @Test
	public void testParseFile_WhenInvalidFileType_thenBadRequestException()
	{
		try
		{
			when(cmsService.getContent(anyString())).thenReturn("Err.IP-0119");
			modelUtil.parseFile("1234", invalidFileType);
			Assert.fail();
		}
		catch (BadRequestException e)
		{
			Assert.assertEquals("Err.IP-0119", e.getMessage());
		}
	}

	@Test
	public void testParseFile_WhenEmptyFile_thenBadRequestException()
	{
		try
		{
			when(cmsService.getContent(anyString())).thenReturn("Err.IP-0164");
			modelUtil.parseFile("1234", emptyFile);
			Assert.fail();
		}
		catch (BadRequestException e)
		{
			Assert.assertEquals("Err.IP-0164", e.getMessage());
		}
	}

	@Test
	public void testParseFile_WhenValidFile_thenObjectReturned()
	{
		ModelPortfolioUploadDto modelUpload = modelUtil.parseFile("1234", file);
		Assert.assertEquals("EQR_CONC_CORE_EQ", modelUpload.getModelCode());
		Assert.assertEquals("EQR Concentrated Core Equities", modelUpload.getModelName());
		Assert.assertNotEquals("", modelUpload.getCommentary());
		Assert.assertEquals(14, modelUpload.getAssetAllocations().size());
	}

	@Test
	public void testGetCommentary_whenValue_thenValueMatches()
	{
		String commentaryStr = modelUtil.getCommentary(commentary);
		Assert.assertEquals("Lorem ipsum,dolor sit amet,consectetur adipiscing elit", commentaryStr);
	}

	@Test
	public void testGetAssetAllocation_whenCashAsset_thenCodeMapped()
	{

        ModelPortfolioAssetAllocationDto allocation = modelUtil.getAssetAllocation(cashRow, assetMap);
		Assert.assertEquals("MACC.MP.AUD", allocation.getAssetCode());
		Assert.assertEquals(new BigDecimal("3.00"), allocation.getAssetAllocation());
		Assert.assertNull(allocation.getTradePercent());
	}

	@Test
	public void testGetAssetAllocation_whenNoAllocation_thenZero()
	{
        ModelPortfolioAssetAllocationDto allocation = modelUtil.getAssetAllocation(assetNoAllocation, assetMap);
		Assert.assertEquals("AGK", allocation.getAssetCode());
		Assert.assertEquals(BigDecimal.ZERO, allocation.getAssetAllocation());
		Assert.assertEquals(new BigDecimal("0.50"), allocation.getTradePercent());
	}

	@Test
	public void testGetAssetAllocation_whenNoTradePercent_thenNull()
	{
        ModelPortfolioAssetAllocationDto allocation = modelUtil.getAssetAllocation(assetNoTradePercent, assetMap);
		Assert.assertEquals("ANZ", allocation.getAssetCode());
		Assert.assertEquals(new BigDecimal("8.39"), allocation.getAssetAllocation());
		Assert.assertNull(allocation.getTradePercent());
	}

	@Test
	public void testGetPercentage_whenValue_thenResultMatches()
	{
		BigDecimal percent = modelUtil.getPercentage(percentage, defaultVal);
		Assert.assertEquals(new BigDecimal(percentage), percent);
	}

	@Test
	public void testGetPercentage_whenValueWithSign_thenResultMatches()
	{
		BigDecimal percent = modelUtil.getPercentage(percentageWithSign, defaultVal);
		Assert.assertEquals(new BigDecimal("4.56"), percent);
	}

	@Test
	public void testGetPercentage_whenNoValue_thenDefault()
	{
		BigDecimal percent = modelUtil.getPercentage("", defaultVal);
		Assert.assertEquals(defaultVal, percent);
	}

    @Test
    public void testUploadFile_NoCashAssetFound() {
        Map<String, Asset> cashAssetMap = new HashMap<>();
        AssetImpl asset = Mockito.mock(AssetImpl.class);
        when(asset.getAssetCode()).thenReturn(null);
        when(asset.getMoneyAccountType()).thenReturn("Other");
        cashAssetMap.put("MACC.MP.AUD", asset);
        when(
                assetService.loadAssetsForCriteria(Mockito.anyList(), Mockito.anyString(), Mockito.anyCollection(),
                        Mockito.any(ServiceErrors.class))).thenReturn(cashAssetMap);

        ModelPortfolioUploadDto modelUpload = modelUtil.parseFile("1234", file);
        Assert.assertEquals("EQR_CONC_CORE_EQ", modelUpload.getModelCode());
        Assert.assertEquals("EQR Concentrated Core Equities", modelUpload.getModelName());
        Assert.assertNotEquals("", modelUpload.getCommentary());
        Assert.assertEquals(14, modelUpload.getAssetAllocations().size());
    }
}
