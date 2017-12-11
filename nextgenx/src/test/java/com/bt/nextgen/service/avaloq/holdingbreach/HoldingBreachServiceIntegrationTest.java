package com.bt.nextgen.service.avaloq.holdingbreach;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreach;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachAsset;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachIntegrationService;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachSummary;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HoldingBreachServiceIntegrationTest extends BaseSecureIntegrationTest {
    @Autowired
    private HoldingBreachIntegrationService holdingBreachService;

    private ServiceErrors serviceErrors;

    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
    }

    @Test
    public void testLoadHolding() {
        HoldingBreachSummary summary = holdingBreachService.loadHoldingBreaches(serviceErrors);
        assertNotNull(summary.getReportDate());
        assertEquals(3, summary.getHoldingBreaches().size());
        HoldingBreach breach = summary.getHoldingBreaches().get(0);
        assertEquals("64114", breach.getAccountId());
        assertEquals(new BigDecimal("368248.19"), breach.getValuationAmount());
        assertEquals(11, breach.getBreachAssets().size());
        HoldingBreachAsset breachAsset = breach.getBreachAssets().get(0);
        assertEquals("110294", breachAsset.getAssetId());
        assertEquals(new BigDecimal("111.45086924412686373525478533"), breachAsset.getBreachAmount());
        assertEquals(new BigDecimal("2"), breachAsset.getHoldingLimitPercent());
        assertEquals(new BigDecimal("11468.94"), breachAsset.getMarketValue());
        assertEquals(new BigDecimal("3.1145086924412686373525478533"), breachAsset.getPortfolioPercent());
    }
}
