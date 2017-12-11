package com.bt.nextgen.service.avaloq.history;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.history.CashRateComponent;
import com.bt.nextgen.service.integration.history.CashRateHistoryService;
import com.bt.nextgen.service.integration.history.CashReport;
import com.bt.nextgen.service.integration.history.InterestDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This test is based on the stubbed response from xml named BTFG$UI_INTR_RATE.ASSET#HIST.xml
 * Any change in the xml may lead to fail this test. Please change accordingly if you wish to change the xml. 
 * 
 */
public class CashRateHistoryServiceIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	CashRateHistoryService cashRateHistoryService;

	@Test
	@SecureTestContext(customerId = "227182001", profileId="8426")
	//@SecureTestContext
	public void loadProductsTest()
	{
		Collection<AssetKey> assetIds= new ArrayList<>();
		assetIds.add(AssetKey.valueOf("10757"));
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Collection<CashReport> reports = cashRateHistoryService.loadCashRateHistory(assetIds, serviceErrors);
		assertNotNull(reports);
		
		for(CashReport rate : reports)
		{
			if(rate.getAssetKey() != null)
			assertNotNull(rate.getAssetKey().getId());
			assertNotNull(rate.getAssetName());
			assertNotNull(rate.getComponentTypeId());
			assertEquals(rate.getBaseRate(), BigDecimal.valueOf(2.5));
			assertEquals(rate.getCurrentRate(), BigDecimal.valueOf(2.08));
			//assertNotNull(rate.getComponentTypeName());
			
			for(InterestDate interestDate : rate.getInterestRates())
			{
				assertNotNull(interestDate.getEffectiveDate());
				assertNotNull(interestDate.getInterestRate());
			}
			
			CashRateComponent contributionImpl1 = rate.getBaseCashRateComponent();
			{
				assertNotNull(contributionImpl1.getCashRateComponentId());
				//assertNotNull(contributionImpl1.getCashRateComponentName());
				for(InterestDate interestDate : contributionImpl1.getInterestDates())
				{
					assertNotNull(interestDate.getEffectiveDate());
					assertNotNull(interestDate.getInterestRate());
				}
			}
			
			CashRateComponent contributionImpl = rate.getMarginCashRateComponent();
			{
				assertNotNull(contributionImpl.getCashRateComponentId());
				//assertNotNull(contributionImpl.getCashRateComponentName());
				for(InterestDate interestDate : contributionImpl.getInterestDates())
				{
					assertNotNull(interestDate.getEffectiveDate());
					assertNotNull(interestDate.getInterestRate());
				}
				assertEquals(contributionImpl.getSummatedRate(), BigDecimal.valueOf(-0.42));
			}
			
			if(rate.getSpecialCashRateComponent() != null )
			{
				CashRateComponent contributionImpl3 = rate.getSpecialCashRateComponent();
				{
					assertNull(contributionImpl3.getCashRateComponentId());
					assertNull(contributionImpl3.getCashRateComponentName());
					if(contributionImpl3.getInterestDates() != null)
					{
						for(InterestDate interestDate : contributionImpl3.getInterestDates())
						{
							assertNull(interestDate.getEffectiveDate());
							assertNull(interestDate.getInterestRate());
						}
					}
					
				}
			}
			
		}
	}

}
