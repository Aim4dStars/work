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

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionMultiBlockAccountElectionDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MultiBlockResponseConverterServiceImpl;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElectionKey;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountKey;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipation;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MultiBlockResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private MultiBlockResponseConverterServiceImpl multiBlockResponseConverterService;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Before
    public void setup() {
        Asset asset = mock(Asset.class);
        when(asset.getAssetCode()).thenReturn("BHP");

        when(assetIntegrationService.loadAsset(anyString(), any(ServiceErrors.class))).thenReturn(asset);
    }

    @Test
    public void testToElectionOptionDtos_whenThereAreValidElectionOptions_thenPopulateAvailableOptions() {
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

        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "100.0", new BigDecimal("100.0"));

        when(details.getOptions()).thenReturn(options);
        when(details.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<CorporateActionOptionDto> optionDtos = multiBlockResponseConverterService.toElectionOptionDtos(context, null);

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
    public void testToElectionOptionDtos_whenThereAreInvalidElectionOptions_thenPopulateAvailableOptions() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        List<CorporateActionOption> options = new ArrayList<>();

        // Below combos should not produce an electable option
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(1), "1"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(1), "2"));

        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(2), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(2), "1"));

        options.add(createOptionMock(CorporateActionOptionKey.OFFERED_PRICE.getCode(3), "10.0", BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(3), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(3), "1"));

        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(4), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(4), "1"));

        options.add(createOptionMock(CorporateActionOptionKey.OFFERED_PRICE.getCode(5), "10.0", BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.ASSET_ID.getCode(5), "0"));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(5), "1"));

        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(6), "1"));

        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(7), "1"));

        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "100.0", new BigDecimal("100.0"));

        when(details.getOptions()).thenReturn(options);
        when(details.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<CorporateActionOptionDto> optionDtos = multiBlockResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(0, optionDtos.size());
//        assertEquals("Do not participate", optionDtos.get(0).getSummary());
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

        List<CorporateActionOptionDto> optionDtos = multiBlockResponseConverterService.toElectionOptionDtos(context, null);

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

        List<CorporateActionOptionDto> optionDtos = multiBlockResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(1, optionDtos.size());

        assertEquals((Integer) 1, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("$10 per share", optionDtos.get(0).getSummary());
        assertFalse(optionDtos.get(0).getIsDefault());
        assertFalse(optionDtos.get(0).getIsNoAction());
    }


    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsNoSelectedOption_thenReturnNull() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        assertNull(multiBlockResponseConverterService.toSubmittedAccountElectionsDto(null, account));
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsSelectedOption_thenReturnAnElectionDto() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "50.0", BigDecimal.valueOf(50.0));

        when(account.getDecisions()).thenReturn(Arrays.asList(decision));

        CorporateActionAccountElectionsDto electionsDto = multiBlockResponseConverterService.toSubmittedAccountElectionsDto(null, account);

        assertNotNull(electionsDto);
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());

        assertTrue(electionsDto.getPrimaryAccountElection() instanceof CorporateActionMultiBlockAccountElectionDtoImpl);
        CorporateActionMultiBlockAccountElectionDtoImpl multiBlockAccountElectionDto =
                (CorporateActionMultiBlockAccountElectionDtoImpl) electionsDto.getPrimaryAccountElection();
        assertTrue(BigDecimal.valueOf(50.0).compareTo(multiBlockAccountElectionDto.getPercent()) == 0);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsSelectedOptionWithInvalidPercentValue_thenReturnAnElectionDto() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), null, null);

        when(account.getDecisions()).thenReturn(Arrays.asList(decision));

        CorporateActionAccountElectionsDto electionsDto = multiBlockResponseConverterService.toSubmittedAccountElectionsDto(null, account);
        assertNull(electionsDto);

        decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "-1", BigDecimal.valueOf(-1));

        when(account.getDecisions()).thenReturn(Arrays.asList(decision));
        electionsDto = multiBlockResponseConverterService.toSubmittedAccountElectionsDto(null, account);
        assertNull(electionsDto);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedDetails_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);

        when(savedDetails.getSavedParticipation()).thenReturn(null);

        assertNull(multiBlockResponseConverterService.toSavedAccountElectionsDto(null, null, savedDetails));
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsSavedAccountWithElection_thenReturnPopulatedElectionDto() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);
        CorporateActionSavedAccountElection savedAccountElection = mock(CorporateActionSavedAccountElection.class);
        CorporateActionSavedAccountElectionKey savedAccountElectionKey = mock(CorporateActionSavedAccountElectionKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccountElectionKey.getOptionId()).thenReturn(1);
        when(savedAccountElection.getKey()).thenReturn(savedAccountElectionKey);
        when(savedAccountElection.getPercent()).thenReturn(BigDecimal.TEN);
        when(savedAccount.getAccountElections()).thenReturn(Arrays.asList(savedAccountElection));
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockResponseConverterService.toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNotNull(electionsDto);
        assertNotNull(electionsDto.getPrimaryAccountElection());
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());

        assertTrue(electionsDto.getPrimaryAccountElection() instanceof CorporateActionMultiBlockAccountElectionDtoImpl);
        CorporateActionMultiBlockAccountElectionDtoImpl multiBlockAccountElectionDto =
                (CorporateActionMultiBlockAccountElectionDtoImpl) electionsDto.getPrimaryAccountElection();

        assertTrue(BigDecimal.TEN.compareTo(multiBlockAccountElectionDto.getPercent()) == 0);
        assertNull(multiBlockAccountElectionDto.getUnits());
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsSavedAccountWithNoElections_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getAccountElections()).thenReturn(new ArrayList<CorporateActionSavedAccountElection>());
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockResponseConverterService.toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNull(electionsDto);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedAccount_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getAccountElections()).thenReturn(new ArrayList<CorporateActionSavedAccountElection>());
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockResponseConverterService.toSavedAccountElectionsDto(null, "1", savedDetails);

        assertNull(electionsDto);
    }

    @Test
    public void testToSummaryList_whenThereIsNoSummary_thenReturnEmptyList() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        when(details.getSummary()).thenReturn(null);
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> summaryLines = multiBlockResponseConverterService.toSummaryList(context, null);

        assertTrue(summaryLines.isEmpty());

        when(details.getSummary()).thenReturn("");

        summaryLines = multiBlockResponseConverterService.toSummaryList(context, null);

        assertTrue(summaryLines.isEmpty());
    }

    @Test
    public void testToSummaryList_whenThereIsSummary_thenPopulatedList() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        when(details.getSummary()).thenReturn("Hello|World");
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<String> summaryLines = multiBlockResponseConverterService.toSummaryList(context, null);

        assertFalse(summaryLines.isEmpty());
        assertEquals(2, summaryLines.size());
        assertEquals("Hello", summaryLines.get(0));
        assertEquals("World", summaryLines.get(1));
    }

    @Test
    public void testSetCorporateActionDetailsDtoParam() {
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();
        CorporateActionOptionDto optionDto = mock(CorporateActionOptionDto.class);
        params.setOptions(Arrays.asList(optionDto));

        when(optionDto.getIsDefault()).thenReturn(Boolean.TRUE);

        params = multiBlockResponseConverterService.setCorporateActionDetailsDtoParams(null, params);

        assertNull(params.getErrorMessage());

        when(optionDto.getIsDefault()).thenReturn(Boolean.FALSE);

        params = multiBlockResponseConverterService.setCorporateActionDetailsDtoParams(null, params);

        assertNotNull(params.getErrorMessage());
    }
}