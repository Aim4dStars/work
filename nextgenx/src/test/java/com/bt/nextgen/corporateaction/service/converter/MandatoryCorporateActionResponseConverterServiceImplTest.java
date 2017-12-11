package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MandatoryCorporateActionResponseConverterServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionSecurityExchangeTypeConverter;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MandatoryCorporateActionResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private MandatoryCorporateActionResponseConverterServiceImpl mandatoryCorporateActionResponseConverterService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private CorporateActionSecurityExchangeTypeConverter corporateActionSecurityExchangeConverter;

    private List<CorporateActionOption> options = new ArrayList<>();

    @Before
    public void setup() {
        AssetImpl asset = new AssetImpl();
        asset.setAssetCode("BHP");
        when(assetIntegrationService.loadAsset(anyString(), any(ServiceErrors.class))).thenReturn(asset);
        when(corporateActionSecurityExchangeConverter.convert(anyString())).thenReturn(CorporateActionSecurityExchangeType
                .SECURITY_EXCHANGE);

        options.add(createOptionMock(CorporateActionOptionKey.OFFERED_PRICE.getCode(), "1000"));
        options.add(createOptionMock(CorporateActionOptionKey.PRICE.getCode(), "2000"));
        options.add(createOptionMock(CorporateActionOptionKey.REVENUE_PER_PRICE.getCode(), "100"));
        options.add(createOptionMock(CorporateActionOptionKey.PRICE_FACTOR.getCode(), "10"));
        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(), "1"));
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), "1"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), "2"));
        options.add(createOptionMock(CorporateActionOptionKey.SECURITY_EXCHANGE.getCode(), "1"));
        options.add(createOptionMock(CorporateActionOptionKey.REDEMPTION_PRICE.getCode(), "10"));
        options.add(createOptionMock(CorporateActionOptionKey.FINAL_BUY_BACK_PRICE.getCode(), "10"));
    }

    @Test
    public void testToSummaryList_whenThereAreNoOptions_thenReturnAnEmptyList() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        when(details.getOptions()).thenReturn(null);
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> texts = mandatoryCorporateActionResponseConverterService.toSummaryList(context, null);

        assertTrue(texts.isEmpty());

        when(details.getOptions()).thenReturn(new ArrayList<CorporateActionOption>());
        texts = mandatoryCorporateActionResponseConverterService.toSummaryList(context, null);

        assertTrue(texts.isEmpty());
    }

    @Test
    public void testToSummaryList_whenThereIsNoSummaryTemplate_thenReturnAnEmptyList() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        when(details.getCorporateActionType()).thenReturn(CorporateActionType.STAPLED_SECURITY_EVENT);
        when(details.getOptions()).thenReturn(null);
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> texts = mandatoryCorporateActionResponseConverterService.toSummaryList(context, null);

        assertTrue(texts.isEmpty());
    }

    @Test
    public void testToSummaryList_whenThereIsNoOptions_thenReturnAnEmptyList() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionOption option = createOptionMock(CorporateActionOptionKey.MINIMUM_QUANTITY.getCode(), "1");

        when(details.getCorporateActionType()).thenReturn(CorporateActionType.STAPLED_SECURITY_EVENT);
        when(details.getOptions()).thenReturn(Arrays.asList(option));
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> texts = mandatoryCorporateActionResponseConverterService.toSummaryList(context, null);

        assertTrue(texts.isEmpty());
    }

    @Test
    public void testToSummaryList_whenMandatoryGroupIsPurchaseOfferExecution_thenReplaceWithBindValues() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        when(details.getCorporateActionType()).thenReturn(CorporateActionType.PURCHASE_OFFER_EXECUTION);
        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> texts = mandatoryCorporateActionResponseConverterService.toSummaryList(context, null);

        assertNotNull(texts);
        assertTrue(texts.size() == 1);
        assertEquals("BHP Purchase Offer Execution $2,000.00", texts.get(0));

    }

    @Test
    public void testToSummaryList_whenMandatoryGroupIsMergerWithCashPaymentAndFraction_thenReplaceWithBindValues() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        when(details.getCorporateActionType()).thenReturn(CorporateActionType.MERGER_WITH_CASH_PAYMENT_AND_FRACTION);
        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> texts = mandatoryCorporateActionResponseConverterService.toSummaryList(context, null);

        assertNotNull(texts);
        assertTrue(texts.size() == 1);
        assertEquals("BHP 1 is now BHP 2 and $1,000.00 per unit", texts.get(0));
    }

    @Test
    public void testToSummaryList_whenMandatoryGroupIsBuyBack_thenReplaceWithBindValues() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        when(details.getCorporateActionType()).thenReturn(CorporateActionType.BUY_BACK_MANDATORY);
        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> texts = mandatoryCorporateActionResponseConverterService.toSummaryList(context, null);

        assertNotNull(texts);
        assertTrue(texts.size() == 1);
        assertEquals("BHP, Off Market Buy Back, $10.00", texts.get(0));
    }

    @Test
    public void testToSummaryList_whenMandatoryGroupWhenThereAreNonRelatedOptions_thenDoNotReplaceBindParams() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionOption option = createOptionMock(CorporateActionOptionKey.QUANTITY.getCode(), "10");
        when(details.getCorporateActionType()).thenReturn(CorporateActionType.PURCHASE_OFFER_EXECUTION);

        when(details.getOptions()).thenReturn(Arrays.asList(option));
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> texts = mandatoryCorporateActionResponseConverterService.toSummaryList(context, null);

        assertNotNull(texts);
        assertTrue(texts.size() == 1);
        assertEquals("BHP Purchase Offer Execution {price_amount}", texts.get(0));
    }

    @Test
    public void testToSummaryList_whenMandatoryGroupIsPurchaseOfferExecutionButNoStaticCode_thenDoNotReplaceWithBindParams() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.QUANTITY.getCode(), "10"));
        options.add(createOptionMock(CorporateActionOptionKey.SECURITY_EXCHANGE.getCode(), CorporateActionSecurityExchangeType.CONVERSION
                .getId()));

        when(details.getCorporateActionType()).thenReturn(CorporateActionType.PURCHASE_OFFER_EXECUTION);
        when(corporateActionSecurityExchangeConverter.convert(anyString())).thenReturn(null);
        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> texts = mandatoryCorporateActionResponseConverterService.toSummaryList(context, null);

        assertNotNull(texts);
        assertTrue(texts.size() == 1);
        assertEquals("BHP Purchase Offer Execution {price_amount}", texts.get(0));
    }

    @Test
    public void testToElectionOptionDtos_shouldAnBeEmptyList() {
        List<CorporateActionOptionDto> optionDtos = mandatoryCorporateActionResponseConverterService.toElectionOptionDtos(null, null);

        assertNotNull(optionDtos);
        assertTrue(optionDtos.isEmpty());
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenSecurityExchangeIsSetInOption_thenReturnCorrespondingCorporateActionType() {
        when(corporateActionSecurityExchangeConverter.convert(anyString())).thenReturn(CorporateActionSecurityExchangeType.CONVERSION);

        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        CorporateActionOption option = createOptionMock(CorporateActionOptionKey.SECURITY_EXCHANGE.getCode(),
                CorporateActionSecurityExchangeType.CONVERSION.getId());

        when(details.getOptions()).thenReturn(Arrays.asList(option));
        when(context.getCorporateActionDetails()).thenReturn(details);

        mandatoryCorporateActionResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertNotNull(params.getCorporateActionType());
        assertEquals(CorporateActionType.SECURITY_EXCHANGE_CONVERSION.getCode(), params.getCorporateActionType());

        when(corporateActionSecurityExchangeConverter.convert(anyString())).thenReturn(CorporateActionSecurityExchangeType.REINVESTMENT);

        option = createOptionMock(CorporateActionOptionKey.SECURITY_EXCHANGE.getCode(), CorporateActionSecurityExchangeType.REINVESTMENT
                .getId());

        when(details.getOptions()).thenReturn(Arrays.asList(option));

        mandatoryCorporateActionResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertNotNull(params.getCorporateActionType());
        assertEquals(CorporateActionType.SECURITY_EXCHANGE_REINVESTMENT.getCode(), params.getCorporateActionType());
    }

    @Test
    public void
    testSetCorporateActionDetailsDtoParams_whenSecurityExchangeIsSetInOptionButNoConversion_thenReturnCorrespondingCorporateActionType() {
        when(corporateActionSecurityExchangeConverter.convert(anyString())).thenReturn(null);

        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        CorporateActionOption option = createOptionMock(CorporateActionOptionKey.SECURITY_EXCHANGE.getCode(), (String) null);

        when(details.getOptions()).thenReturn(Arrays.asList(option));
        when(context.getCorporateActionDetails()).thenReturn(details);

        mandatoryCorporateActionResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertNull(params.getCorporateActionType());
    }

    @Test
    public void
    testSetCorporateActionDetailsDtoParams_whenThereNoSecurityExchangeIsSetInOption_thenReturnCorrespondingCorporateActionType() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        when(details.getOptions()).thenReturn(new ArrayList<CorporateActionOption>());
        when(context.getCorporateActionDetails()).thenReturn(details);
        params.setCorporateActionType(CorporateActionType.STAPLED_SECURITY_EVENT.getCode());

        mandatoryCorporateActionResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertEquals(CorporateActionType.STAPLED_SECURITY_EVENT.getCode(), params.getCorporateActionType());
    }

    @Test
    public void testToSubmittedAccountElectionsDto_shouldBeNull() {
        CorporateActionAccountElectionsDto electionsDto = mandatoryCorporateActionResponseConverterService.toSubmittedAccountElectionsDto
                (null, null);

        assertNull(electionsDto);
    }

    @Test
    public void testToSavedAccountElectionsDto_shouldBeNull() {
        CorporateActionAccountElectionsDto electionsDto =
                mandatoryCorporateActionResponseConverterService.toSavedAccountElectionsDto(null, null, null);

        assertNull(electionsDto);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_shouldNotBeModified() {
        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = mandatoryCorporateActionResponseConverterService.setCorporateActionAccountDetailsDtoParams(null, null, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }
}