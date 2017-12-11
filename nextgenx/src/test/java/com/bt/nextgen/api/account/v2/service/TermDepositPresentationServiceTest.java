package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.termdeposit.service.TermDepositCalculatorUtils;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TermDepositPresentationServiceTest {

    @InjectMocks
    TermDepositPresentationServiceImpl termDepositPresentationService;

    @Mock
    AssetIntegrationService assetIntegrationService;

    @Mock
    @Qualifier("avaloqAccountIntegrationService")
    AccountIntegrationService accountIntegrationService;

    @Mock
    CmsService cmsService;

    @Mock
    FeatureTogglesService featureTogglesService;

    @Mock
    FeatureToggles featureToggles;

    @Mock
    TermDepositCalculatorUtils termDepositCalculatorUtils;

    private AccountKey accountKey;
    private final String assetId = "20169";

    @Before
    public void setup() {

        accountKey = AccountKey.valueOf("accountKey");

        TermDepositAssetImpl termDepositAsset = new TermDepositAssetImpl();
        termDepositAsset.setAssetId(assetId);
        termDepositAsset.setAssetName("BT");
        termDepositAsset.setGenericAssetId(assetId);
        termDepositAsset.setBrand("80000064");

        Mockito.when(assetIntegrationService.loadAsset(Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(
                termDepositAsset);

        TermDepositAssetDetailImpl termDepositAssetDetail = new TermDepositAssetDetailImpl();
        termDepositAssetDetail.setTerm(new Term("6M"));
        termDepositAssetDetail.setPaymentFrequency(PaymentFrequency.AT_MATURITY);

        Map<String, TermDepositAssetDetail> termDepositAssetMap = new HashMap<String, TermDepositAssetDetail>();
        termDepositAssetMap.put(assetId, termDepositAssetDetail);

        Mockito.when(
                assetIntegrationService.loadTermDepositRates(Mockito.any(BrokerKey.class), Mockito.any(DateTime.class),
                        Mockito.anyList(),
                        Mockito.any(ServiceErrors.class))).thenReturn(termDepositAssetMap);


        Mockito.when(cmsService.getContent(Mockito.any(String.class))).thenReturn("BT");

        // Mock account adviserId
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAdviserPositionId(BrokerKey.valueOf("12345"));

        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(account);
        Mockito.when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
        Mockito.when(termDepositCalculatorUtils.getBankDate()).thenReturn(new DateTime());
    }

    @Test
    public void testGetTermDepositPresentation() {
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(false);
        TermDepositPresentation termDepositPresentation = termDepositPresentationService.getTermDepositPresentation(accountKey,
                assetId, new ServiceErrorsImpl());

        assertNotNull(termDepositPresentation);
        assertEquals("BT", termDepositPresentation.getBrandClass());
        assertEquals("BT Term Deposit", termDepositPresentation.getBrandName());
        assertEquals("6 months", termDepositPresentation.getTerm());
        assertEquals(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase(), termDepositPresentation.getPaymentFrequency());

    }

    @Test
    public void testTermDepositPresentationV2(){


        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        TermDepositInterestRate termDepositAssetDetail = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withTerm(new Term("6M")).withPaymentFrequency(PaymentFrequency.AT_MATURITY).buildTermDepositRate();
        Mockito.when(
                assetIntegrationService.loadTermDepositRates(Mockito.any(TermDepositAssetRateSearchKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(new ArrayList<>(Collections.singleton(termDepositAssetDetail)));

        TermDepositPresentation termDepositPresentation = termDepositPresentationService.getTermDepositPresentation(accountKey,
                assetId, new ServiceErrorsImpl());
        assertNotNull(termDepositPresentation);
        assertEquals("BT", termDepositPresentation.getBrandClass());
        assertEquals("BT Term Deposit", termDepositPresentation.getBrandName());
        assertEquals("6 months", termDepositPresentation.getTerm());
        assertEquals(PaymentFrequency.AT_MATURITY.getDisplayName().toLowerCase(), termDepositPresentation.getPaymentFrequency());



    }

    @Test
    public void testTermDepositPresentationV2_withNoAssets(){

        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        WrapAccountImpl account = new WrapAccountImpl();
        account.setAdviserPositionId(null);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class)))
                .thenReturn(account);
        TermDepositInterestRate termDepositAssetDetail = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withTerm(new Term("6M")).withPaymentFrequency(PaymentFrequency.AT_MATURITY).buildTermDepositRate();
        Mockito.when(
                assetIntegrationService.loadTermDepositRates(Mockito.any(TermDepositAssetRateSearchKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(new ArrayList<>(Collections.singleton(termDepositAssetDetail)));
        TermDepositPresentation termDepositPresentation = termDepositPresentationService.getTermDepositPresentation(accountKey,
                assetId, new ServiceErrorsImpl());
        assertEquals(termDepositPresentation.getPaymentFrequency(),"");
        assertEquals(termDepositPresentation.getTerm(),"");

    }
}
