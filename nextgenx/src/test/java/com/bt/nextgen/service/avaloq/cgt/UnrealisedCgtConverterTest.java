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

import com.avaloq.abs.screen_rep.hira.btfg$ui_cgt_unreal_bp_cont_assetgrp_asset.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.cgt.CgtBaseData;
import com.bt.nextgen.service.integration.cgt.InvestmentCgt;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

/**
 * @author L072463
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class UnrealisedCgtConverterTest {

    @InjectMocks
    UnrealisedCgtConverter cgtConverter = new UnrealisedCgtConverter();

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // Create dummy asset
        AssetImpl mp = new AssetImpl();
        mp.setAssetId("94339");
        mp.setAssetName("MP 1");
        mp.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mp.setAssetCode("11111");

        AssetImpl mfund = new AssetImpl();
        mfund.setAssetId("93659");
        mfund.setAssetName("Test Fund");
        mfund.setAssetType(AssetType.MANAGED_FUND);
        mfund.setAssetCode("APIR999");

        AssetImpl share = new AssetImpl();
        share.setAssetId("92924");
        share.setAssetName("Share X");
        share.setAssetType(AssetType.SHARE);
        share.setAssetCode("ASXQ");

        AssetImpl nonStapled = new AssetImpl();
        nonStapled.setAssetId("110752");
        nonStapled.setAssetName("Not a Stapled Security");
        nonStapled.setAssetType(AssetType.SHARE);
        nonStapled.setAssetCode("NUNS");

        AssetImpl stapled = new AssetImpl();
        stapled.setAssetId("111412");
        stapled.setAssetName("Stapled Security");
        stapled.setAssetType(AssetType.SHARE);
        stapled.setAssetCode("STAP");

        AssetImpl stapled2 = new AssetImpl();
        stapled.setAssetId("111413");
        stapled.setAssetName("Stapled Security2");
        stapled.setAssetType(AssetType.SHARE);
        stapled.setAssetCode("STAP");

        HashMap<String, Asset> assetMap = new HashMap<>();
        assetMap.put("94339", mp);
        assetMap.put("92924", share);
        assetMap.put("92928", share);
        assetMap.put("93026", share);
        assetMap.put("110752", nonStapled);
        assetMap.put("111412", stapled);
        assetMap.put("111413", stapled2);

        assetMap.put("93743", mfund);
        assetMap.put("93692", mfund);
        assetMap.put("93101", mfund);
        assetMap.put("93688", mfund);

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
    public void testToModel_whenSuppliedWithEmptyResponse_thenEmptyObjectCreatedWithNoServiceErrors() {
        Rep report = JaxbUtil.unmarshall("/webservices/response/UnrealisedCgtDetailsEmpty_UT.xml", Rep.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<InvestmentCgt> result = cgtConverter.toModel(report, serviceErrors);

        assertTrue(!serviceErrors.hasErrors());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testToModel_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() {
        Rep report = JaxbUtil.unmarshall("/webservices/response/UnrealisedCgtDetails_UT.xml", Rep.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<InvestmentCgt> result = cgtConverter.toModel(report, serviceErrors);

        assertTrue(!serviceErrors.hasErrors());
        assertNotNull(result);
        assertEquals(6, result.size());

        // Test non-stapled share holding remains separate
        InvestmentCgt share = result.get(0);
        assertEquals(Boolean.FALSE, share.getIsStapledSecurity());
        assertEquals(1, share.getCgtData().size());
        assertEquals("NUNS", share.getInvestment().getAssetCode());

        CgtBaseData shareData = share.getCgtData().get(0);
        assertEquals(new DateTime("2015-03-30"), shareData.getTaxDate());
        assertTrue(shareData.getHoldingPeriod().doubleValue() == 154d);
        assertTrue(shareData.getQuantity().doubleValue() == 6d);
        assertTrue(shareData.getNetProceed().doubleValue() == 25.08d);
        assertTrue(shareData.getTaxCost().doubleValue() == 25.8d);
        assertTrue(shareData.getTaxCostBase().doubleValue() == 25.8d);
        assertNull(shareData.getTaxGain());
        assertTrue(shareData.getTaxReducedCostBase().doubleValue() == 25.8d);
        assertTrue(shareData.getPriceDate().equals(new DateTime("2015-08-14")));

        // Test stapled security details get grouped under parent and lots are aggregated
        InvestmentCgt stapled = result.get(4);
        assertEquals(Boolean.TRUE, stapled.getIsStapledSecurity());
        assertEquals(2, stapled.getCgtData().size());
        assertEquals("STAP", stapled.getInvestment().getAssetCode());

        CgtBaseData stapledData1 = stapled.getCgtData().get(0);
        assertEquals(new DateTime("2015-03-17"), stapledData1.getTaxDate());
        assertTrue(stapledData1.getHoldingPeriod().doubleValue() == 167d);
        assertTrue(stapledData1.getQuantity().doubleValue() == 511d);
        assertTrue(stapledData1.getNetProceed().doubleValue() == 2906.57d);
        assertTrue(stapledData1.getTaxCost().doubleValue() == 2832.08d);
        assertTrue(stapledData1.getTaxCostBase().doubleValue() == 2832.08d);
        assertTrue(stapledData1.getTaxGain().doubleValue() == 74.49d);
        assertTrue(stapledData1.getTaxReducedCostBase().doubleValue() == 2832.08d);
        // Take latest price date from aggregated lots
        assertTrue(stapledData1.getPriceDate().equals(new DateTime("2015-01-01")));

        CgtBaseData stapledData2 = stapled.getCgtData().get(1);
        assertEquals(new DateTime("2015-03-30"), stapledData2.getTaxDate());
        assertTrue(stapledData2.getHoldingPeriod().doubleValue() == 154d);
        assertTrue(stapledData2.getQuantity().doubleValue() == 37d);
        assertTrue(stapledData2.getNetProceed().doubleValue() == 210.46d);
        assertTrue(stapledData2.getTaxCost().doubleValue() == 207.04d);
        assertTrue(stapledData2.getTaxCostBase().doubleValue() == 207.04d);
        assertTrue(stapledData2.getTaxGain().doubleValue() == 3.42d);
        assertTrue(stapledData2.getTaxReducedCostBase().doubleValue() == 207.04d);
        assertTrue(stapledData2.getPriceDate().equals(new DateTime("2015-01-01")));

        // MF holding
        InvestmentCgt invCgt = result.get(1);
        assertTrue(invCgt.getCgtData().size() == 1);
        CgtBaseData data = invCgt.getCgtData().get(0);
        assertTrue(data.getHoldingPeriod().doubleValue() == 71d);
        assertTrue(data.getQuantity().doubleValue() == 14825d);
        assertTrue(data.getNetProceed().doubleValue() == 0d);
        assertTrue(data.getTaxCost().doubleValue() == 13046d);
        assertTrue(data.getTaxCostBase().doubleValue() == 13046d);
        assertTrue(data.getTaxGain().doubleValue() == -13046d);
        assertTrue(data.getTaxReducedCostBase().doubleValue() == 13046d);

        // Test price date. Value is read from asset-head.
        DateTime priceDate = new DateTime(2014, 12, 21, 0, 0);
        assertTrue(data.getPriceDate().equals(priceDate));

        Asset invAsset = invCgt.getInvestment();
        assertTrue(invAsset != null);
        assertTrue(invAsset.getAssetCode().equals("APIR999"));

        // MP holding
        ManagedPortfolioCgtImpl mpCgt = (ManagedPortfolioCgtImpl) result.get(5);
        assertEquals(mpCgt.getInvestmentCgtList().size(), 5);
        assertEquals(mpCgt.getCgtData().size(), 5);
    }

    @Test
    public void testToModel_whenSuppliedWithValidResponse_thenStapledSecuritiesAreAggregatedCorrectly() {
        Rep report = JaxbUtil.unmarshall("/webservices/response/UnrealisedCgtDetailsStapled_UT.xml", Rep.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<InvestmentCgt> resultList = cgtConverter.toModel(report, serviceErrors);
        assertEquals(1, resultList.size());
        InvestmentCgt result = resultList.iterator().next();
        assertNull(result.getChildInvestment());
        assertEquals("110752", result.getInvestment().getAssetId());
        assertEquals(4, result.getCgtData().size());

        assertEquals(new DateTime("2015-03-17"), result.getCgtData().get(0).getTaxDate());
        assertEquals(BigDecimal.valueOf(250), result.getCgtData().get(0).getHoldingPeriod());
        assertEquals(BigDecimal.valueOf(642), result.getCgtData().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(1816.86), result.getCgtData().get(0).getNetProceed());
        assertEquals(BigDecimal.valueOf(2080.16), result.getCgtData().get(0).getTaxCost());
        assertEquals(BigDecimal.valueOf(2080.16), result.getCgtData().get(0).getTaxCostBase());
        assertEquals(BigDecimal.valueOf(2080.16), result.getCgtData().get(0).getTaxReducedCostBase());
        assertEquals(null, result.getCgtData().get(0).getTaxIndexedCostBase());
        assertEquals(BigDecimal.valueOf(-26330, 2), result.getCgtData().get(0).getTaxGain());
        assertEquals(BigDecimal.valueOf(-26330, 2), result.getCgtData().get(0).getCostBaseGain());

        assertEquals(new DateTime("2015-03-30"), result.getCgtData().get(1).getTaxDate());
        assertEquals(BigDecimal.valueOf(237), result.getCgtData().get(1).getHoldingPeriod());
        assertEquals(BigDecimal.valueOf(82), result.getCgtData().get(1).getQuantity());
        assertEquals(BigDecimal.valueOf(232.06), result.getCgtData().get(1).getNetProceed());
        assertEquals(BigDecimal.valueOf(151.27), result.getCgtData().get(1).getTaxCost());
        assertEquals(BigDecimal.valueOf(151.27), result.getCgtData().get(1).getTaxCostBase());
        assertEquals(BigDecimal.valueOf(151.27), result.getCgtData().get(1).getTaxReducedCostBase());
        assertEquals(null, result.getCgtData().get(1).getTaxIndexedCostBase());
        assertEquals(BigDecimal.valueOf(80.79), result.getCgtData().get(1).getTaxGain());
        assertEquals(BigDecimal.valueOf(80.79), result.getCgtData().get(1).getCostBaseGain());

        assertEquals(new DateTime("2015-05-10"), result.getCgtData().get(2).getTaxDate());
        assertEquals(BigDecimal.valueOf(187), result.getCgtData().get(2).getHoldingPeriod());
        assertEquals(BigDecimal.valueOf(35), result.getCgtData().get(2).getQuantity());
        assertEquals(BigDecimal.valueOf(99.05), result.getCgtData().get(2).getNetProceed());
        assertEquals(BigDecimal.valueOf(102.94), result.getCgtData().get(2).getTaxCost());
        assertEquals(BigDecimal.valueOf(102.94), result.getCgtData().get(2).getTaxCostBase());
        assertEquals(BigDecimal.valueOf(102.94), result.getCgtData().get(2).getTaxReducedCostBase());
        assertEquals(null, result.getCgtData().get(2).getTaxIndexedCostBase());
        assertEquals(BigDecimal.valueOf(-3.89), result.getCgtData().get(2).getTaxGain());
        assertEquals(BigDecimal.valueOf(-3.89), result.getCgtData().get(2).getCostBaseGain());

        assertEquals(new DateTime("2015-05-19"), result.getCgtData().get(3).getTaxDate());
        assertEquals(BigDecimal.valueOf(187), result.getCgtData().get(3).getHoldingPeriod());
        assertEquals(BigDecimal.valueOf(35), result.getCgtData().get(3).getQuantity());
        assertEquals(BigDecimal.valueOf(99.05), result.getCgtData().get(3).getNetProceed());
        assertEquals(BigDecimal.valueOf(110.71), result.getCgtData().get(3).getTaxCost());
        assertEquals(BigDecimal.valueOf(110.71), result.getCgtData().get(3).getTaxCostBase());
        assertEquals(BigDecimal.valueOf(110.71), result.getCgtData().get(3).getTaxReducedCostBase());
        assertEquals(null, result.getCgtData().get(3).getTaxIndexedCostBase());
        assertEquals(BigDecimal.valueOf(-11.66), result.getCgtData().get(3).getTaxGain());
        assertEquals(BigDecimal.valueOf(-11.66), result.getCgtData().get(3).getCostBaseGain());
    }
}
