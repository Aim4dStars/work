package com.bt.nextgen.service.integration.externalassets;

import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.constants.PropertyType;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerKey;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.externalasset.model.OffPlatformExternalAssetImpl;
import com.bt.nextgen.service.integration.externalasset.model.OnPlatformExternalAssetImpl;
import com.bt.nextgen.service.integration.externalasset.service.ExternalAssetIntegrationService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class ExternalAssetIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	ExternalAssetIntegrationService externalAssetIntegrationService;

	@Test
	public void submitNewCommercialProperty()
	{
		TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"),
			ContainerKey.valueOf("54321"),
			createNewSingleExternalAssetForProperty(),
			new DateTime());
		assertEquals("saved", status.getStatus());
	}

	@Test
    public void submitNewOnAustralianListedSecurity()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createNewOnPlatformListedSecurity(), new DateTime());
        assertEquals("saved", status.getStatus());
    }

	@Test
    public void submitNewOnManagedFund()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createNewOnPlatformManagedFund(), new DateTime());
        assertEquals("saved", status.getStatus());
    }

	@Test
    public void submitNewOffListedSecurity()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createNewOffPlatformListedSecurity(), new DateTime());
        assertEquals("saved", status.getStatus());
    }
	
	

	@Test
    public void submitNewOffManagedFund()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createNewOffPlatformManagedFund(), new DateTime());
        assertEquals("saved", status.getStatus());
    }

	@Test
    public void submitNewOffInternationalListedSecurity()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createOffPlatformInternationalListedSecurity(), new DateTime());
        assertEquals("saved", status.getStatus());
    }
	
	

	@Test
    public void submitNewOffManagedPortfolio()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createOffPlatformManagedPortfolio(), new DateTime());
        assertEquals("saved", status.getStatus());
    }

	@Test
    public void submitNewOffDirectProperty()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createOffPlatformDirectProperty(), new DateTime());
        assertEquals("saved", status.getStatus());
    }

	@Test
    public void submitNewOffOther()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createOffPlatformOther(), new DateTime());
        assertEquals("saved", status.getStatus());
    }

	@Test
    public void submitNewOffCash()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createOffPlatformCash(), new DateTime());
        assertEquals("saved", status.getStatus());
    }

	@Test
    public void submitNewOffTermDeposit()
    {
        TransactionStatus status = externalAssetIntegrationService.saveOrUpdateExternalAssets(AccountKey.valueOf("12345"), ContainerKey.valueOf("54321"), createOffPlatformTermDeposit(), new DateTime());
        assertEquals("saved", status.getStatus());
    }

	@Test
	public void getExternalAssets()
	{
		List <AccountKey> accountIdList = new ArrayList <>();
		accountIdList.add(AccountKey.valueOf("12345"));

		//ExternalAssetResponseHolder externalAssetResponseHolder = externalAssetIntegrationService.getExternalAssets(accountIdList, new DateTime());
		// assertEquals(5, externalAssetResponseHolder.getExternalAssetList().size());
	}

	private List <ExternalAsset> createNewSingleExternalAssetForProperty()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OffPlatformExternalAssetImpl asset1 = new OffPlatformExternalAssetImpl();
		asset1.setPositionName("8 Jordan Street, Chatswood, Sydney 2000");
		asset1.setPositionCode("PRP");
		asset1.setAssetType(AssetType.DIRECT_PROPERTY);
		asset1.setAssetClass(AssetClass.AUSTRALIAN_REAL_ESTATE);
		asset1.setPropertyType(PropertyType.COMMERCIAL_PROPERTY);
		asset1.setValueDate(new DateTime());
		asset1.setMarketValue(new BigDecimal(500000));
		asset1.setMaturityDate(new DateTime());
		asset1.setQuantity(new BigDecimal(1));
		asset1.setSource("MANUAL");
		//asset1.setAssetKey(AssetKey.valueOf("12345"));

		externalAssetList.add(asset1);

		return externalAssetList;
	}

	private List <ExternalAsset> createNewOnPlatformListedSecurity()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OnPlatformExternalAssetImpl asset1 = new OnPlatformExternalAssetImpl();
		AssetKey assetKey = AssetKey.valueOf("123456");
		asset1.setAssetKey(assetKey);
		asset1.setSource("COMMSEC");
		//asset1.setAssetKey(AssetKey.valueOf("234324"));
		asset1.setQuantity(new BigDecimal(20000));

		externalAssetList.add(asset1);

		return externalAssetList;
	}

	private List <ExternalAsset> createNewOnPlatformManagedFund()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OnPlatformExternalAssetImpl asset1 = new OnPlatformExternalAssetImpl();
		AssetKey assetKey = AssetKey.valueOf("123456");
		asset1.setAssetKey(assetKey);
		asset1.setSource("PRIME");
		asset1.setQuantity(new BigDecimal(1250));

		externalAssetList.add(asset1);

		return externalAssetList;
	}

	private List <ExternalAsset> createNewOffPlatformListedSecurity()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OffPlatformExternalAssetImpl asset1 = new OffPlatformExternalAssetImpl();
		asset1.setAssetType(AssetType.AUSTRALIAN_LISTED_SECURITIES);
		asset1.setAssetClass(AssetClass.AUSTRALIAN_LISTED_SECURITIES);
		asset1.setSource("ETRADE");
		asset1.setPositionName("Belfast Resources Limited");
		asset1.setPositionCode("BRL");
		asset1.setQuantity(new BigDecimal(1500));
		asset1.setMarketValue(new BigDecimal("245000"));
		asset1.setValueDate(new DateTime());

		externalAssetList.add(asset1);

		return externalAssetList;
	}
	
	

	private List <ExternalAsset> createNewOffPlatformManagedFund()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OffPlatformExternalAssetImpl asset1 = new OffPlatformExternalAssetImpl();
		asset1.setAssetType(AssetType.MANAGED_FUND);
		asset1.setAssetClass(AssetClass.CASH);
		asset1.setSource("ETRADE");
		asset1.setPositionName("Goldman Sachs Long Term Income Fund");
		asset1.setPositionCode("GSL999EE");
		asset1.setQuantity(new BigDecimal(1500));
		asset1.setMarketValue(new BigDecimal("32055"));
		asset1.setValueDate(new DateTime());

		externalAssetList.add(asset1);

		return externalAssetList;
	}

	private List <ExternalAsset> createOffPlatformInternationalListedSecurity()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OffPlatformExternalAssetImpl asset1 = new OffPlatformExternalAssetImpl();
		asset1.setAssetType(AssetType.INTERNATIONAL_LISTED_SECURITIES);
		asset1.setAssetClass(AssetClass.INTERNATIONAL_LISTED_SECURITIES);
		asset1.setSource("ETRADE");
		asset1.setPositionName("APPLE");
		asset1.setPositionCode("AAPL");
		asset1.setQuantity(new BigDecimal(1500));
		asset1.setMarketValue(new BigDecimal("32055"));
		asset1.setValueDate(new DateTime());

		externalAssetList.add(asset1);

		return externalAssetList;
	}
	
	

	private List <ExternalAsset> createOffPlatformManagedPortfolio()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OffPlatformExternalAssetImpl asset1 = new OffPlatformExternalAssetImpl();
		asset1.setAssetType(AssetType.MANAGED_PORTFOLIO);
		asset1.setAssetClass(AssetClass.CASH);
		asset1.setSource("cash");
		asset1.setPositionName("Hedge fund");
		asset1.setPositionCode("GSL9999EE");
		asset1.setMarketValue(new BigDecimal("55000"));
		asset1.setValueDate(new DateTime());

		externalAssetList.add(asset1);

		return externalAssetList;
	}

	private List <ExternalAsset> createOffPlatformDirectProperty()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OffPlatformExternalAssetImpl asset1 = new OffPlatformExternalAssetImpl();
		asset1.setAssetType(AssetType.DIRECT_PROPERTY);
		asset1.setAssetClass(AssetClass.AUSTRALIAN_REAL_ESTATE);
		asset1.setPositionName("151 Clarence St Sydney 2000");
		asset1.setMarketValue(new BigDecimal("550000"));
		asset1.setValueDate(new DateTime());
		asset1.setPropertyType(PropertyType.COMMERCIAL_PROPERTY);

		externalAssetList.add(asset1);

		return externalAssetList;
	}

	private List <ExternalAsset> createOffPlatformOther()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OffPlatformExternalAssetImpl asset1 = new OffPlatformExternalAssetImpl();
		asset1.setAssetType(AssetType.OTHER_ASSET);
		asset1.setAssetClass(AssetClass.OTHER_ASSET);
		asset1.setPositionName("my car");
		asset1.setQuantity(new BigDecimal("2"));
		asset1.setMarketValue(new BigDecimal("150000"));
		asset1.setValueDate(new DateTime());
		asset1.setSource("car asset");

		externalAssetList.add(asset1);

		return externalAssetList;
	}

	private List <ExternalAsset> createOffPlatformCash()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OffPlatformExternalAssetImpl asset1 = new OffPlatformExternalAssetImpl();
		asset1.setAssetType(AssetType.CASH);
		asset1.setAssetClass(AssetClass.CASH);
		asset1.setPositionName("ANZ checking account");
		asset1.setQuantity(new BigDecimal("1")); //default quantity
		asset1.setMarketValue(new BigDecimal("150050"));
		asset1.setValueDate(new DateTime());
		asset1.setSource("ANZ bank account");

		externalAssetList.add(asset1);

		return externalAssetList;
	}

	private List <ExternalAsset> createOffPlatformTermDeposit()
	{
		ArrayList <ExternalAsset> externalAssetList = new ArrayList();

		OffPlatformExternalAssetImpl asset1 = new OffPlatformExternalAssetImpl();
		asset1.setAssetType(AssetType.TERM_DEPOSIT);
		asset1.setAssetClass(AssetClass.CASH);
		asset1.setPositionName("Term Deposit 3 months");
		asset1.setQuantity(new BigDecimal("1")); //default quantity
		asset1.setMarketValue(new BigDecimal("150050"));
		asset1.setValueDate(new DateTime());
		asset1.setSource("WESTPAC term deposit");
		DateTime date = new DateTime();
		date = date.plusMonths(3);
		asset1.setMaturityDate(date);

		externalAssetList.add(asset1);

		return externalAssetList;
	}

}
