/**
 * 
 */
package com.bt.nextgen.service.avaloq.cgt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.avaloq.abs.screen_rep.hira.btfg$ui_cgt_real_bp_cont_assetgrp_asset.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.cgt.InvestmentCgt;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

/**
 * @author L072463
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CgtConverterTest {

    @InjectMocks
    CgtConverter cgtConverter = new CgtConverter();

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        AssetImpl asset = new AssetImpl();
        asset.setAssetId("21123");
        asset.setAssetName("BHP BHP Billiton");
        asset.setAssetType(AssetType.SHARE);
        asset.setAssetCode("BHP");

        AssetImpl stapled = new AssetImpl();
        stapled.setAssetId("111419");
        stapled.setAssetName("Stapled Security");
        stapled.setAssetType(AssetType.SHARE);
        stapled.setAssetCode("STAP");

        AssetImpl stapled2 = new AssetImpl();
        stapled.setAssetId("111420");
        stapled.setAssetName("Stapled Security2");
        stapled.setAssetType(AssetType.SHARE);
        stapled.setAssetCode("STAP");

        AssetImpl mfund = new AssetImpl();
        mfund.setAssetId("93659");
        mfund.setAssetName("Test Fund");
        mfund.setAssetType(AssetType.MANAGED_FUND);
        mfund.setAssetCode("APIR999");

        HashMap<String, Asset> assetMap = new HashMap<>();
        assetMap.put("21123", asset);
        assetMap.put("54715", asset);
        assetMap.put("53428", asset);
        assetMap.put("54715", asset);
        assetMap.put("111419", stapled);
        assetMap.put("111420", stapled2);
        assetMap.put("93659", mfund);

        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assetMap);

        Mockito.when(
                staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                if ("1".equals(args[1])) {
                    CodeImpl c = new CodeImpl("1", "VALID", "valid");
                    c.setIntlId("valid");
                    return c;
                } else if ("0".equals(args[1])) {
                    CodeImpl c = new CodeImpl("0", "PRE85", "Grandfathered");
                    c.setIntlId("pre85");
                    return c;
                } else if ("2".equals(args[1])) {
                    CodeImpl c = new CodeImpl("2", "insufficient", "Insufficient RFP");
                    c.setIntlId("insufficient");
                    return c;
                } else if ("3".equals(args[1])) {
                    CodeImpl c = new CodeImpl("3", "ESTIM_VAL", "RFP with estimated values");
                    c.setIntlId("estim_val");
                    return c;
                } else if ("4".equals(args[1])) {
                    CodeImpl c = new CodeImpl("4", "CORRUPT", "Corrupt");
                    c.setIntlId("corrupt");
                    return c;
                } else if ("5".equals(args[1])) {
                    CodeImpl c = new CodeImpl("5", "EXTL_VAL", "RFP with values from external sources");
                    c.setIntlId("extl_val");
                    return c;
                } else if ("99".equals(args[1])) {
                    CodeImpl c = new CodeImpl("1", "DLV_OUT", "Delivery out");
                    c.setIntlId("dlv_out");
                    return c;
                } else {
                    return null;
                }
            }
        });
    }

    @Test
    public void testWhenResponseIsEmpty() {
        Rep report = JaxbUtil.unmarshall("/webservices/response/RealisedCgtDetailsEmpty_UT.xml", Rep.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<InvestmentCgt> result = cgtConverter.toModel(report, serviceErrors);

        assertTrue(!serviceErrors.hasErrors());
        assertTrue(result.isEmpty());
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.service.avaloq.cgt.CgtConverter#toModel(com.avaloq.abs.screen_rep.hira.btfg$ui_cgt_real_bp_cont_assetgrp_asset.Rep, com.bt.nextgen.service.ServiceErrors)}
     * .
     */
    @Test
    public void testToModel() {
        Rep report = JaxbUtil.unmarshall("/webservices/response/RealisedCgtDetails_UT.xml", Rep.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<InvestmentCgt> result = cgtConverter.toModel(report, serviceErrors);

        assertNotNull(result);
        assertEquals(3, result.size());

        // ManagedPortfolio cgt data
        ManagedPortfolioCgtImpl mpCgt = (ManagedPortfolioCgtImpl) result.get(0);
        assertEquals(mpCgt.getInvestmentCgtList().size(), 4);
        assertEquals(mpCgt.getCgtData().size(), 8);

        // InvestmentAsset cgt data
        InvestmentCgt invCgt = result.get(1);
        Asset invAsset = invCgt.getInvestment();
        assertEquals("93659", invAsset.getAssetId());
        assertEquals("APIR999", invAsset.getAssetCode());
        assertEquals(4, invCgt.getCgtData().size());
        assertTrue(1500d == invCgt.getCgtData().get(0).getNetProceed().doubleValue());
        assertTrue(1482.95d == invCgt.getCgtData().get(0).getTaxCost().doubleValue());
        
     // InvestmentAsset cgt data
        InvestmentCgt invCgt1 = result.get(2);
        assertEquals(BigDecimal.ZERO, invCgt1.getCgtData().get(0).getNetProceed());
        
    }

    @Test
    public void testToModel_whenSuppliedWithValidResponse_thenStapledSecuritiesAreAggregatedCorrectly() {
        Rep report = JaxbUtil.unmarshall("/webservices/response/RealisedCgtDetailsStapled_UT.xml", Rep.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<InvestmentCgt> resultList = cgtConverter.toModel(report, serviceErrors);
        assertEquals(1, resultList.size());
        InvestmentCgt result = resultList.iterator().next();
        assertNull(result.getChildInvestment());
        assertEquals("21123", result.getInvestment().getAssetId());
        assertEquals(2, result.getCgtData().size());

        assertEquals(new DateTime("2014-12-22"), result.getCgtData().get(0).getTaxDate());
        assertEquals(new DateTime("2014-12-22"), result.getCgtData().get(0).getTaxDate());
        assertEquals(new DateTime("2014-12-23"), result.getCgtData().get(1).getTaxDate());
        assertEquals(BigDecimal.valueOf(0), result.getCgtData().get(0).getHoldingPeriod());
        assertEquals(BigDecimal.valueOf(7761.3635), result.getCgtData().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(13673.34), result.getCgtData().get(0).getNetProceed());
        assertEquals(BigDecimal.valueOf(1562500, 2), result.getCgtData().get(0).getTaxCost());
        assertEquals(BigDecimal.valueOf(1562500, 2), result.getCgtData().get(0).getTaxCostBase());
        assertEquals(BigDecimal.valueOf(1562500, 2), result.getCgtData().get(0).getTaxReducedCostBase());
        assertEquals(null, result.getCgtData().get(0).getTaxIndexedCostBase());
        assertEquals(BigDecimal.valueOf(-1951.66), result.getCgtData().get(0).getTaxGain());
        assertEquals(BigDecimal.valueOf(-1951.66), result.getCgtData().get(0).getCostBaseGain());
    }
}
