package com.bt.nextgen.reports.account.fees.schedule;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeesScheduleAuthorisationFormTest {
    @InjectMocks
    private FeeScheduleAuthorisationForm feeScheduleAuthorisationForm;

    @Mock
    ContentDtoService contentDtoService;

    @Mock
    CmsService cmsService;

    @Mock
    OptionsService optionService;

    @Spy
    private ObjectMapper mapper = new JsonObjectMapper();

    @Before
    public void setup() {
    }

    @Test
    public void testFeeScheduleAuthorisationForm_whenNoFees_thenEmptyList() {
        Map<String, Object> feeScheduleParams;
        feeScheduleParams = new HashMap<String, Object>();
        feeScheduleParams.put("feeScheduleTransactionDto", "{\"onGoingFees\":{},\"licenseeFees\":{},\"contributionFees\":[]}");
        FeeScheduleAuthorisationData result = (FeeScheduleAuthorisationData) feeScheduleAuthorisationForm
                .getData(feeScheduleParams, null).get(0);
        assertTrue(result.getLicenseeFees().isEmpty());
        assertTrue(result.getOngoingFees().isEmpty());
        assertTrue(result.getOneoffContributionFees().isEmpty());
        assertTrue(result.getRegularContributionFees().isEmpty());
    }

    @Test
    public void testFeeScheduleAuthorisationForm_whenNoSlidingFee_thenFeeTiers() {
        Map<String, Object> feeScheduleParams;
        feeScheduleParams = new HashMap<String, Object>();
        feeScheduleParams.put("feeScheduleTransactionDto",
                "{\"onGoingFees\":{},\"licenseeFees\":{\"slidingScaleFee\":{\"managedFund\":false,\"managedPortfolio\":false,\"cash\":false,\"termDeposit\":true,\"share\":true,\"slidingScaleFeeTier\":[{\"lowerBound\":0,\"upperBound\":1000,\"percentage\":0.1,\"type\":\"SlidingScaleFeeTier\"},{\"lowerBound\":1000,\"upperBound\":null,\"percentage\":0.2,\"type\":\"SlidingScaleFeeTier\"}],\"minimumFee\":null,\"maximumFee\":null,\"label\":\"Sliding scale fee component\",\"name\":\"licenseeadvicefeeslidingscalefee\",\"spclDiscount\":null,\"assetCount\":2,\"type\":\"SlidingScaleFee\"}}}");
        FeeScheduleAuthorisationData result = (FeeScheduleAuthorisationData) feeScheduleAuthorisationForm
                .getData(feeScheduleParams, null).get(0);
        assertFalse(result.getLicenseeFees().isEmpty());
        SlidingFeeComponentData component = (SlidingFeeComponentData) result.getLicenseeFees().get(0);
        List<String> assetTypes = component.getAssetTypes();
        assertEquals(2, assetTypes.size());
        assertEquals("Listed securities", assetTypes.get(0));
        assertEquals("Term deposits", assetTypes.get(1));
        List<ImmutablePair<String, String>> tiers = (List<ImmutablePair<String, String>>) component.getChildren();
        assertEquals(2, tiers.size());
        assertEquals("$0 - $1,000", tiers.get(0).getKey());
        assertEquals("0.10%", tiers.get(0).getValue());
        assertEquals("$1,000 and above", tiers.get(1).getKey());
        assertEquals("0.20%", tiers.get(1).getValue());
    }

    @Test
    public void testFeeScheduleAuthorisationForm_whenFlatFee_thenDollarFee() {
        Map<String, Object> feeScheduleParams;
        feeScheduleParams = new HashMap<String, Object>();
        feeScheduleParams.put("feeScheduleTransactionDto",
                "{\"onGoingFees\":{\"dollarFee\":{\"cpiindex\":true,\"amount\":1000,\"date\":\"Aug 2017\",\"name\":\"ongoingadvicefeedollarfee\",\"label\":\"Dollar fee component\",\"type\":\"DollarFee\"}},\"licenseeFees\":{}}");
        FeeScheduleAuthorisationData result = (FeeScheduleAuthorisationData) feeScheduleAuthorisationForm
                .getData(feeScheduleParams, null).get(0);
        assertFalse(result.getOngoingFees().isEmpty());
        DollarFeeComponentData component = (DollarFeeComponentData) result.getOngoingFees().get(0);
        assertTrue(component.getAssetTypes().isEmpty());
        assertEquals("$1,000.00", component.getFeeAmount());
        assertEquals(true, component.getCpiIndexed());
        assertEquals("Aug 2017", component.getCpiDate());
    }

    @Test
    public void testFeeScheduleAuthorisationForm_whenAssetTypeFee_thenPercentFee() {
        Map<String, Object> feeScheduleParams;
        feeScheduleParams = new HashMap<String, Object>();
        feeScheduleParams.put("feeScheduleTransactionDto",
                "{\"onGoingFees\":{},\"licenseeFees\":{\"percentageFee\":{\"name\":\"licenseeadvicefeepercentagefee\",\"label\":\"Percentage fee component\",\"managedFund\":0.2,\"managedPortfolio\":0.3,\"cash\":0.5,\"termDeposit\":0.4,\"share\":0.1,\"minimumFee\":null,\"maximumFee\":null,\"listAssets\":[],\"tailored\":false,\"type\":\"PercentageFee\"}}}");
        FeeScheduleAuthorisationData result = (FeeScheduleAuthorisationData) feeScheduleAuthorisationForm
                .getData(feeScheduleParams, null).get(0);
        assertFalse(result.getLicenseeFees().isEmpty());
        PercentFeeComponentData component = (PercentFeeComponentData) result.getLicenseeFees().get(0);
        List<ImmutablePair<String, String>> tiers = (List<ImmutablePair<String, String>>) component.getChildren();
        assertTrue(component.getAssetTypes().isEmpty());
        assertEquals("Listed securities", tiers.get(0).getKey());
        assertEquals("0.10%", tiers.get(0).getValue());
        assertEquals("Managed funds", tiers.get(1).getKey());
        assertEquals("0.20%", tiers.get(1).getValue());
        assertEquals("Managed portfolios", tiers.get(2).getKey());
        assertEquals("0.30%", tiers.get(2).getValue());
        assertEquals("Term deposits", tiers.get(3).getKey());
        assertEquals("0.40%", tiers.get(3).getValue());
        assertEquals("Cash", tiers.get(4).getKey());
        assertEquals("0.50%", tiers.get(4).getValue());
    }

    @Test
    public void testFeeScheduleAuthorisationForm_whenPortfolioPercentFee_thenPercentFee() {
        Map<String, Object> feeScheduleParams;
        feeScheduleParams = new HashMap<String, Object>();
        feeScheduleParams.put("feeScheduleTransactionDto",
                "{\"portfolioFees\":[{\"apirCode\":null,\"code\":\"TESTTMP01\",\"investmentName\":\"TestTMP01\",\"ipsId\":\"733145\",\"subaccountId\":\"C6AF549B4A8533F227FBC078E130188D9E7DE2B04E2BFD49\",\"label\":\"Percentage fee component\",\"percentage\":4,\"type\":\"PERCENTAGE_FEE\"}]}");
        FeeScheduleAuthorisationData result = (FeeScheduleAuthorisationData) feeScheduleAuthorisationForm
                .getData(feeScheduleParams, null).get(0);
        assertTrue(result.getHasPortfolioFees());
        assertFalse(result.getPortfolioPercentFees().isEmpty());
        PortfolioFeeComponentData component = (PortfolioFeeComponentData) result.getPortfolioPercentFees().get(0);
        assertEquals("Percentage fee component", component.getName());
        assertEquals("<b>TESTTMP01 &#183 </b> TestTMP01", component.getKey());
        assertEquals("4.00%", component.getValue());
    }

    @Test
    public void testFeeScheduleAuthorisationForm_whenPortfolioSlidingFee_thenSlidingFee() {
        Map<String, Object> feeScheduleParams;
        feeScheduleParams = new HashMap<String, Object>();
        feeScheduleParams.put("feeScheduleTransactionDto",
                "{\"onGoingFees\":{},\"licenseeFees\":{},\"portfolioFees\":[{\"slidingScaleFeeTier\":[{\"lowerBound\":0,\"upperBound\":1,\"percentage\":2,\"type\":\"SlidingScaleFeeTier\"},{\"lowerBound\":1,\"upperBound\":\"\",\"percentage\":3,\"type\":\"SlidingScaleFeeTier\"}],\"apirCode\":null,\"code\":\"TESTTMP01\",\"investmentName\":\"TestTMP01\",\"ipsId\":\"733145\",\"subaccountId\":\"C9C4E7BC53B8FBBD128D8DBD97F30BEF2E583DA1CE069A3C\",\"type\":\"SLIDING_SCALE_FEE\",\"label\":\"Sliding scale fee component\"}]}");
        FeeScheduleAuthorisationData result = (FeeScheduleAuthorisationData) feeScheduleAuthorisationForm
                .getData(feeScheduleParams, null).get(0);
        assertTrue(result.getHasPortfolioFees());
        assertFalse(result.getPortfolioSlidingFees().isEmpty());
        PortfolioFeeComponentData component = (PortfolioFeeComponentData) result.getPortfolioSlidingFees().get(0);
        assertEquals("Sliding scale fee component", component.getName());
        assertEquals("<b>TESTTMP01 &#183 </b> TestTMP01", component.getAsset());

        List<ImmutablePair<String, String>> tiers = (List<ImmutablePair<String, String>>) component.getChildren();
        assertEquals(2, tiers.size());
        assertEquals("$0 - $1", tiers.get(0).getKey());
        assertEquals("2.00%", tiers.get(0).getValue());
        assertEquals("$1 and above", tiers.get(1).getKey());
        assertEquals("3.00%", tiers.get(1).getValue());
    }

    @Test
    public void testGetBrandedAuthorisationText_returnsContent() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "F7E0C70611EC5EA3D1DBB9929819D7BE30F44533189E3362");
        when(cmsService.getContent(Mockito.anyString())).thenReturn("branded authorisationText");
        when(optionService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(true);
        String authText = feeScheduleAuthorisationForm.getAuthorisationText(params);
        assertEquals("branded authorisationText", authText);
    }

    @Test
    public void testGetNonBrandedAuthorisationText_returnsContent() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "F7E0C70611EC5EA3D1DBB9929819D7BE30F44533189E3362");
        when(cmsService.getContent(Mockito.anyString())).thenReturn("authorisationText");
        when(optionService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(false);
        String authText = feeScheduleAuthorisationForm.getAuthorisationText(params);
        assertEquals("authorisationText", authText);
    }

    @Test
    public void testGetBrandedHeaderText_returnsContent() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "F7E0C70611EC5EA3D1DBB9929819D7BE30F44533189E3362");
        when(cmsService.getContent(Mockito.anyString())).thenReturn("branded header Text");
        when(optionService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(true);
        String headerText = feeScheduleAuthorisationForm.getHeaderDisclaimer(params);
        assertEquals("branded header Text", headerText);
    }

    @Test
    public void testGetHeaderText_returnsContent() {
        Map<String, Object> params = new HashMap<>();
        params.put("account-id", "F7E0C70611EC5EA3D1DBB9929819D7BE30F44533189E3362");
        when(cmsService.getContent(Mockito.anyString())).thenReturn("header Text");
        when(optionService.hasFeature(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(false);
        String headerText = feeScheduleAuthorisationForm.getHeaderDisclaimer(params);
        assertEquals("header Text", headerText);
    }

    @Test
    public void testFeeScheduleAuthorisationForm_whenContributionPercentFee_thenFlatPercentFee() {
        Map<String, Object> feeScheduleParams = new HashMap<String, Object>();
        feeScheduleParams.put("feeScheduleTransactionDto",
                "{\"contributionFees\":[{\"rate\":0.1,\"label\":\"Percentage fee component\",\"name\":\"employercontribution\",\"type\":\"FlatPercentageFee\",\"contributionType\":\"Employer contribution\"},{\"rate\":0.1,\"label\":\"Percentage fee component\",\"name\":\"oneoffdeposit\",\"type\":\"FlatPercentageFee\",\"contributionType\":\"oneoff deposit\"},{\"rate\":0.2,\"label\":\"Percentage fee component\",\"name\":\"oneoffpersonalcontribution\",\"type\":\"FlatPercentageFee\",\"contributionType\":\"Oneoff personal contribution\"},{\"rate\":0.2,\"label\":\"Percentage fee component\",\"name\":\"oneoffspousecontribution\",\"type\":\"FlatPercentageFee\",\"contributionType\":\"Oneoff spouse contribution\"},{\"rate\":0.3,\"label\":\"Percentage fee component\",\"name\":\"regularspousecontribution\",\"type\":\"FlatPercentageFee\",\"contributionType\":\"Regular spouse contribution\"}]}");
        FeeScheduleAuthorisationData result = (FeeScheduleAuthorisationData) feeScheduleAuthorisationForm
                .getData(feeScheduleParams, null).get(0);
        assertTrue(result.getHasContributionFees());
        assertEquals(4, result.getOneoffContributionFees().size());
        assertEquals(1, result.getRegularContributionFees().size());

        FlatPercentFeeComponentData component = (FlatPercentFeeComponentData) result.getOneoffContributionFees().get(0);
        List<ImmutablePair<String, String>> fee = (List<ImmutablePair<String, String>>) component.getChildren();
        assertEquals(1, fee.size());
        assertEquals("Employer contribution", fee.get(0).getKey());
        assertEquals("0.10%", fee.get(0).getValue());
    }

    @Test
    public void testFeeScheduleAuthorisationForm_whenContributionFeeTypeEmpty_thenReturnedEmpty() {
        Map<String, Object> feeScheduleParams = new HashMap<String, Object>();
        feeScheduleParams.put("feeScheduleTransactionDto",
                "{\"contributionFees\":[{\"rate\":0.1,\"label\":\"Percentage fee component\",\"name\":\"employercontribution\",\"contributionType\":\"Employer contribution\"}]}");
        FeeScheduleAuthorisationData result = (FeeScheduleAuthorisationData) feeScheduleAuthorisationForm
                .getData(feeScheduleParams, null).get(0);
        assertFalse(result.getHasContributionFees());
    }

    @Test
    public void testFeeScheduleAuthorisationForm_whenContributionFeeTypeDollar_thenReturnedEmpty() {
        Map<String, Object> feeScheduleParams = new HashMap<String, Object>();
        feeScheduleParams.put("feeScheduleTransactionDto",
                "{\"contributionFees\":[{\"amount\":100,\"label\":\"Dollar fee component\",\"name\":\"employercontribution\",\"type\":\"DollarFee\",\"contributionType\":\"Employer contribution\"}]}");
        FeeScheduleAuthorisationData result = (FeeScheduleAuthorisationData) feeScheduleAuthorisationForm
                .getData(feeScheduleParams, null).get(0);
        assertFalse(result.getHasContributionFees());
    }

}
