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
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MultiBlockCapitalCallResponseConverterServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MultiBlockCapitalCallResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private MultiBlockCapitalCallResponseConverterServiceImpl multiBlockCapitalCallResponseConverterServiceImpl;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Before
    public void setup() {
        Asset asset = mock(Asset.class);
        when(asset.getAssetCode()).thenReturn("BHP");

        when(assetIntegrationService.loadAsset(anyString(), any(ServiceErrors.class))).thenReturn(asset);
    }

    @Test
    public void testToElectionOptionDtos_whenThereAreElectionOptions_thenPopulateAvailableOptions() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.OFFERED_PRICE.getCode(1), "10.0", BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(2), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(2), "1"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(2), "2"));
        options.add(createOptionMock(CorporateActionOptionKey.OFFERED_PRICE.getCode(3), "10.0", BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(3), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(3), "1"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(3), "2"));
        options.add(createOptionMock(CorporateActionOptionKey.TITLE.getCode(4), "Title"));

        // Below combos should not produce an electable option
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(5), "1"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(5), "2"));

        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(6), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(6), "1"));

        options.add(createOptionMock(CorporateActionOptionKey.OFFERED_PRICE.getCode(7), "10.0", BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(7), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(7), "1"));

        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(8), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(8), "1"));

        options.add(createOptionMock(CorporateActionOptionKey.OFFERED_PRICE.getCode(9), "10.0", BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(9), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(9), "1"));

        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(10), "1"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(11), "2"));

        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "100.0", new BigDecimal("100.0"));

        when(details.getOptions()).thenReturn(options);
        when(details.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<CorporateActionOptionDto> optionDtos = multiBlockCapitalCallResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(4, optionDtos.size());

        assertEquals((Integer) 1, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("$10 per share", optionDtos.get(0).getSummary());
        assertTrue(optionDtos.get(0).getIsDefault());
        assertFalse(optionDtos.get(0).getIsNoAction());

        assertEquals((Integer) 2, optionDtos.get(1).getId());
        assertEquals("Option B", optionDtos.get(1).getTitle());
        assertEquals("1:2 BHP shares", optionDtos.get(1).getSummary());
        assertFalse(optionDtos.get(1).getIsDefault());
        assertFalse(optionDtos.get(1).getIsNoAction());

        assertEquals((Integer) 3, optionDtos.get(2).getId());
        assertEquals("Option C", optionDtos.get(2).getTitle());
        assertEquals("$10 per share & 1:2 BHP shares", optionDtos.get(2).getSummary());
        assertFalse(optionDtos.get(2).getIsDefault());
        assertFalse(optionDtos.get(2).getIsNoAction());

        assertEquals((Integer) 4, optionDtos.get(3).getId());
        assertEquals("Option D", optionDtos.get(3).getTitle());
        assertEquals("Do not participate", optionDtos.get(3).getSummary());
        assertFalse(optionDtos.get(3).getIsDefault());
        assertTrue(optionDtos.get(3).getIsNoAction());
    }

    @Test
    public void testToElectionOptionDtos_whenThereAreElectionOptionsButNoDefaultOptionForNoAction_thenSetTheNoActionToDefault() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.TITLE.getCode(1), "Title"));

        when(details.getOptions()).thenReturn(options);
        when(details.getDecisions()).thenReturn(null);
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<CorporateActionOptionDto> optionDtos = multiBlockCapitalCallResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(1, optionDtos.size());

        assertEquals((Integer) 1, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("Do not participate", optionDtos.get(0).getSummary());
        assertTrue(optionDtos.get(0).getIsDefault());
        assertTrue(optionDtos.get(0).getIsNoAction());
    }

    @Test
    public void testToElectionOptionDtos_whenThereAreElectionOptionsButNoDefaultOption_thenSetDoNothingToElectionOption() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.OFFERED_PRICE.getCode(1), "10.0", BigDecimal.TEN));

        when(details.getOptions()).thenReturn(options);
        when(details.getDecisions()).thenReturn(null);
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<CorporateActionOptionDto> optionDtos = multiBlockCapitalCallResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(2, optionDtos.size());

        assertEquals((Integer) 1, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("$10 per share", optionDtos.get(0).getSummary());
        assertFalse(optionDtos.get(0).getIsDefault());
        assertFalse(optionDtos.get(0).getIsNoAction());

        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, optionDtos.get(1).getId());
        assertEquals("Option B", optionDtos.get(1).getTitle());
        assertEquals("Do not participate", optionDtos.get(1).getSummary());
        assertTrue(optionDtos.get(1).getIsDefault());
        assertTrue(optionDtos.get(1).getIsNoAction());
    }


    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsSelectedOption_thenReturnAnElectionDto() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "100.0", new BigDecimal("100.0"));

        when(account.getDecisions()).thenReturn(Arrays.asList(decision));
        CorporateActionAccountElectionsDto electionsDto = multiBlockCapitalCallResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(null, account);
        assertNotNull(electionsDto);
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenElectionNotSubmitted_thenDoNotModifyCash() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.NOT_SUBMITTED);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = multiBlockCapitalCallResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(null, account, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenElectionIsSubmitted_thenModifyCashAccordingly() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        CorporateActionOption priceOption = createOptionMock(CorporateActionOptionKey.OFFERED_PRICE.getCode(1), BigDecimal.TEN);
        CorporateActionOption electionOption = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), BigDecimal.valueOf(100.0));

        when(details.getOptions()).thenReturn(Arrays.asList(priceOption));
        when(account.getDecisions()).thenReturn(Arrays.asList(electionOption));
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);
        params.setHolding(5);

        params = multiBlockCapitalCallResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);
        assertTrue(BigDecimal.valueOf(60.0).compareTo(params.getCash()) == 0);
    }


    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenElectionIsSubmittedButNoPriceOption_thenDoNotModifyCash() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        CorporateActionOption electionOption = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), BigDecimal.valueOf(100.0));

        when(details.getOptions()).thenReturn(new ArrayList<CorporateActionOption>());
        when(account.getDecisions()).thenReturn(Arrays.asList(electionOption));
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = multiBlockCapitalCallResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);
        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }
}