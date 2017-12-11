package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBuyBackAccountElectionsDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPriceOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.converter.BuyBackResponseConverterServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElectionKey;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountKey;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipation;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuyBackResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private BuyBackResponseConverterServiceImpl buyBackResponseConverterService;

    @Before
    public void setup() {
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereAreNoDecision_thenReturnNull() {
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(corporateActionAccount.getDecisions()).thenReturn(null);

        CorporateActionAccountElectionsDto accountElectionsDto = buyBackResponseConverterService.toSubmittedAccountElectionsDto(null,
                corporateActionAccount);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsADecision_thenReturnAPopulatedAccountElectionsDto() {
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        CorporateActionOption corporateActionAccountOption1 =
                createOptionMock(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode(), BigDecimal.TEN);
        CorporateActionOption corporateActionAccountOption2 =
                createOptionMock(CorporateActionDecisionKey.OPTION_QUANTITY.getCode(1), BigDecimal.TEN);
        CorporateActionOption corporateActionAccountOption3 =
                createOptionMock(CorporateActionDecisionKey.MINIMUM_PRICE_DECISION.getCode(), BigDecimal.ONE);

        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption1,
                corporateActionAccountOption2, corporateActionAccountOption3));

        CorporateActionAccountElectionsDto accountElectionsDto = buyBackResponseConverterService.toSubmittedAccountElectionsDto(null,
                corporateActionAccount);

        assertNotNull(accountElectionsDto);
        assertTrue(accountElectionsDto instanceof CorporateActionBuyBackAccountElectionsDtoImpl);
        CorporateActionBuyBackAccountElectionsDtoImpl buyBackAccountElectionsDto =
                (CorporateActionBuyBackAccountElectionsDtoImpl) accountElectionsDto;

        assertEquals((Integer) 1, buyBackAccountElectionsDto.getMinimumPriceId());
        assertEquals(2, buyBackAccountElectionsDto.getOptions().size());
        assertEquals((Integer) CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID,
                buyBackAccountElectionsDto.getOptions().get(0).getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(buyBackAccountElectionsDto.getOptions().get(0).getUnits()) == 0);
        assertEquals((Integer) 1, buyBackAccountElectionsDto.getOptions().get(1).getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(buyBackAccountElectionsDto.getOptions().get(1).getUnits()) == 0);
    }

    @Test
    public void
    testToSubmittedAccountElectionsDto_whenThereIsADecisionButNoFinalTenderQuantityOrNegativeQuantity_thenReturnNullAccountElectionDto() {
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        CorporateActionOption corporateActionAccountOption1 =
                createOptionMock(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode(), (BigDecimal) null);
        CorporateActionOption corporateActionAccountOption2 =
                createOptionMock(CorporateActionDecisionKey.OPTION_QUANTITY.getCode(1), new BigDecimal("-10.0"));
        CorporateActionOption corporateActionAccountOption3 =
                createOptionMock(CorporateActionDecisionKey.MINIMUM_PRICE_DECISION.getCode(), BigDecimal.ONE);

        when(corporateActionAccount.getDecisions())
                .thenReturn(Arrays.asList(corporateActionAccountOption1, corporateActionAccountOption2, corporateActionAccountOption3));

        CorporateActionAccountElectionsDto accountElectionsDto =
                buyBackResponseConverterService.toSubmittedAccountElectionsDto(null, corporateActionAccount);

        assertNull(accountElectionsDto);

        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption2, corporateActionAccountOption3));

        accountElectionsDto = buyBackResponseConverterService.toSubmittedAccountElectionsDto(null, corporateActionAccount);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsADecisionButHasNoValueInOptions_thenReturnNullAccountElectionDto() {
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        CorporateActionOption corporateActionAccountOption2 =
                createOptionMock(CorporateActionDecisionKey.OPTION_QUANTITY.getCode(1), (BigDecimal) null);
        CorporateActionOption corporateActionAccountOption3 =
                createOptionMock(CorporateActionDecisionKey.MINIMUM_PRICE_DECISION.getCode(), (String) null);

        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption2, corporateActionAccountOption3));

        CorporateActionAccountElectionsDto accountElectionsDto = buyBackResponseConverterService.toSubmittedAccountElectionsDto(null,
                corporateActionAccount);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsADecisionButNotTheDecisionsExpected_thenReturnNullAccountElectionDto() {
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        CorporateActionOption corporateActionAccountOption =
                createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), BigDecimal.TEN);

        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption));

        CorporateActionAccountElectionsDto accountElectionsDto = buyBackResponseConverterService.toSubmittedAccountElectionsDto(null,
                corporateActionAccount);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsADecisionButNoOptions_thenReturnNullAccountElectionDto() {
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(corporateActionAccount.getDecisions()).thenReturn(new ArrayList<CorporateActionOption>());

        CorporateActionAccountElectionsDto accountElectionsDto = buyBackResponseConverterService.toSubmittedAccountElectionsDto(null,
                corporateActionAccount);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedParticipation_thenThereShouldNoBeElectionDto() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);

        when(savedDetails.getSavedParticipation()).thenReturn(null);

        assertNull(buyBackResponseConverterService.toSavedAccountElectionsDto(null, null, savedDetails));
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsASavedParticipation_thenThereElectionDtoShouldBePopulatedAccordingly() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccountElection savedAccountElection = mock(CorporateActionSavedAccountElection.class);
        CorporateActionSavedAccountElectionKey savedAccountElectionKey = mock(CorporateActionSavedAccountElectionKey.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);

        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getMinimumPriceId()).thenReturn(1);
        when(savedAccount.getAccountElections()).thenReturn(Arrays.asList(savedAccountElection));

        when(savedAccountElectionKey.getOptionId()).thenReturn(1);
        when(savedAccountElection.getKey()).thenReturn(savedAccountElectionKey);
        when(savedAccountElection.getUnits()).thenReturn(BigDecimal.TEN);

        CorporateActionAccountElectionsDto accountElectionsDto =
                buyBackResponseConverterService.toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNotNull(accountElectionsDto);
        assertTrue(accountElectionsDto instanceof CorporateActionBuyBackAccountElectionsDtoImpl);
        CorporateActionBuyBackAccountElectionsDtoImpl buyBackAccountElectionsDto =
                (CorporateActionBuyBackAccountElectionsDtoImpl) accountElectionsDto;

        assertEquals((Integer) 1, buyBackAccountElectionsDto.getMinimumPriceId());
        assertEquals(1, buyBackAccountElectionsDto.getOptions().size());
        assertEquals((Integer) 1, buyBackAccountElectionsDto.getOptions().get(0).getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(buyBackAccountElectionsDto.getOptions().get(0).getUnits()) == 0);
    }


    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedSavedParticipationAccount_thenThereShouldNoBeElectionDto() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccountElection savedAccountElection = mock(CorporateActionSavedAccountElection.class);
        CorporateActionSavedAccountElectionKey savedAccountElectionKey = mock(CorporateActionSavedAccountElectionKey.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);

        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getMinimumPriceId()).thenReturn(1);
        when(savedAccount.getAccountElections()).thenReturn(Arrays.asList(savedAccountElection));

        when(savedAccountElectionKey.getOptionId()).thenReturn(1);
        when(savedAccountElection.getKey()).thenReturn(savedAccountElectionKey);
        when(savedAccountElection.getUnits()).thenReturn(BigDecimal.TEN);

        CorporateActionAccountElectionsDto accountElectionsDto =
                buyBackResponseConverterService.toSavedAccountElectionsDto(null, "1", savedDetails);

        assertNull(accountElectionsDto);

        when(savedAccount.getAccountElections()).thenReturn(new ArrayList<CorporateActionSavedAccountElection>());

        accountElectionsDto = buyBackResponseConverterService.toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToCorporateActionPriceOptionDtos_whenThereIsMinPriceOption_thenPopulatePriceOptionDtos() {
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption = createOptionMock(CorporateActionOptionKey.OPTION_MINIMUM_PRICE.getCode(1),
                BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));

        List<CorporateActionPriceOptionDto> priceOptionDtos =
                buyBackResponseConverterService.toCorporateActionPriceOptionDtos(corporateActionDetails, 1);

        assertNotNull(priceOptionDtos);
        assertEquals(2, priceOptionDtos.size());
        assertEquals((Integer) CorporateActionConverterConstants.NONE_MINIMUM_PRICE_ID, priceOptionDtos.get(0).getId());
        assertEquals("None", priceOptionDtos.get(0).getTitle());
        assertTrue(priceOptionDtos.get(0).getIsDefault());

        assertEquals((Integer) 2, priceOptionDtos.get(1).getId());
        assertEquals("$10", priceOptionDtos.get(1).getTitle());
        assertFalse(priceOptionDtos.get(1).getIsDefault());

        priceOptionDtos = buyBackResponseConverterService.toCorporateActionPriceOptionDtos(corporateActionDetails, 2);

        assertFalse(priceOptionDtos.get(0).getIsDefault());
        assertTrue(priceOptionDtos.get(1).getIsDefault());

    }

    @Test
    public void testToCorporateActionPriceOptionDtos_whenThereIsMinPriceOptionButNoValue_thenPopulatePriceOptionDtosWithJustDefaultOption
            () {
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption =
                createOptionMock(CorporateActionOptionKey.OPTION_MINIMUM_PRICE.getCode(1), (BigDecimal) null);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));

        List<CorporateActionPriceOptionDto> priceOptionDtos =
                buyBackResponseConverterService.toCorporateActionPriceOptionDtos(corporateActionDetails, 1);

        assertNotNull(priceOptionDtos);
        assertEquals(1, priceOptionDtos.size());
    }

    @Test
    public void testToElectionOptionDtos_whenThereAreValues_thenPopulateListOfOptionDtos() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption1 =
                createOptionMock(CorporateActionOptionKey.FINAL_TENDER_PRICE.getCode(), CorporateActionConverterConstants.OPTION_VALUE_YES);
        CorporateActionOption corporateActionOption2 = createOptionMock(CorporateActionOptionKey.OPTION.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionOption3 =
                createOptionMock(CorporateActionOptionKey.PRICE_AS_PERCENT.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionDecisionOption1 =
                createOptionMock(CorporateActionDecisionKey.FINAL_TENDER_PERCENT.getCode(), new BigDecimal("100.0"));
        CorporateActionOption corporateActionDecisionOption2 =
                createOptionMock(CorporateActionDecisionKey.OPTION_PERCENT.getCode(1), new BigDecimal("100.0"));

        when(corporateActionDetails.getOptions())
                .thenReturn(Arrays.asList(corporateActionOption1, corporateActionOption2, corporateActionOption3));
        when(corporateActionDetails.getDecisions())
                .thenReturn(Arrays.asList(corporateActionDecisionOption1, corporateActionDecisionOption2));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        List<CorporateActionOptionDto> optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(3, optionDtos.size());

        assertEquals((Integer) CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("As a Final Price Tender", optionDtos.get(0).getSummary());
        assertTrue(optionDtos.get(0).getIsDefault());

        assertEquals((Integer) 1, optionDtos.get(1).getId());
        assertEquals("Option B", optionDtos.get(1).getTitle());
        assertEquals("Tender at 10% discount", optionDtos.get(1).getSummary());
        assertFalse(optionDtos.get(1).getIsDefault());

        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, optionDtos.get(2).getId());
        assertEquals("Option C", optionDtos.get(2).getTitle());
        assertEquals("Take no action", optionDtos.get(2).getSummary());
        assertFalse(optionDtos.get(2).getIsDefault());
    }

    @Test
    public void testToElectionOptionDtos_whenThereAreValuesButNotTenderPriceDecisionDefault_thenTenderPriceMustNotBeTheDefaultOption() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption1 =
                createOptionMock(CorporateActionOptionKey.FINAL_TENDER_PRICE.getCode(), CorporateActionConverterConstants.OPTION_VALUE_YES);
        CorporateActionOption corporateActionOption2 = createOptionMock(CorporateActionOptionKey.OPTION.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionOption3 =
                createOptionMock(CorporateActionOptionKey.PRICE_AS_PERCENT.getCode(1), "10.0", BigDecimal.TEN);

        when(corporateActionDetails.getOptions())
                .thenReturn(Arrays.asList(corporateActionOption1, corporateActionOption2, corporateActionOption3));
        when(corporateActionDetails.getDecisions()).thenReturn(new ArrayList<CorporateActionOption>());
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        List<CorporateActionOptionDto> optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(3, optionDtos.size());

        assertEquals((Integer) CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("As a Final Price Tender", optionDtos.get(0).getSummary());
        assertFalse(optionDtos.get(0).getIsDefault());
        assertFalse(optionDtos.get(1).getIsDefault());
        assertTrue(optionDtos.get(2).getIsDefault());
    }

    @Test
    public void testToElectionOptionDtos_whenNot100Percent_thenTenderOptionIsFalse() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption1 =
                createOptionMock(CorporateActionOptionKey.FINAL_TENDER_PRICE.getCode(), CorporateActionConverterConstants.OPTION_VALUE_YES);
        CorporateActionOption corporateActionOption2 = createOptionMock(CorporateActionOptionKey.OPTION.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionOption3 =
                createOptionMock(CorporateActionOptionKey.PRICE_AS_PERCENT.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionDecisionOption1 =
                createOptionMock(CorporateActionDecisionKey.FINAL_TENDER_PERCENT.getCode(), new BigDecimal("50.0"));
        CorporateActionOption corporateActionDecisionOption2 =
                createOptionMock(CorporateActionDecisionKey.OPTION_PERCENT.getCode(1), new BigDecimal("100.0"));

        when(corporateActionDetails.getOptions())
                .thenReturn(Arrays.asList(corporateActionOption1, corporateActionOption2, corporateActionOption3));
        when(corporateActionDetails.getDecisions())
                .thenReturn(Arrays.asList(corporateActionDecisionOption1, corporateActionDecisionOption2));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        List<CorporateActionOptionDto> optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(3, optionDtos.size());

        assertEquals((Integer) CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID, optionDtos.get(0).getId());
        assertFalse(optionDtos.get(0).getIsDefault());
        assertTrue(optionDtos.get(1).getIsDefault());

        when(corporateActionDecisionOption1.hasValue()).thenReturn(Boolean.FALSE);
        optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);
        assertFalse(optionDtos.get(0).getIsDefault());
        assertTrue(optionDtos.get(1).getIsDefault());
    }

    @Test
    public void testToElectionOptionDtos_whenThereIsNoTenderPrice_thenPopulateListOfOptionDtosAccordingly() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption1 =
                createOptionMock(CorporateActionOptionKey.FINAL_TENDER_PRICE.getCode(), CorporateActionConverterConstants.OPTION_VALUE_NO);
        CorporateActionOption corporateActionOption2 = createOptionMock(CorporateActionOptionKey.OPTION.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionOption3 =
                createOptionMock(CorporateActionOptionKey.PRICE_AS_PERCENT.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionDecisionOption1 =
                createOptionMock(CorporateActionDecisionKey.FINAL_TENDER_PERCENT.getCode(), new BigDecimal("100.0"));
        CorporateActionOption corporateActionDecisionOption2 =
                createOptionMock(CorporateActionDecisionKey.OPTION_PERCENT.getCode(1), new BigDecimal("100.0"));
        when(corporateActionDetails.getOptions())
                .thenReturn(Arrays.asList(corporateActionOption1, corporateActionOption2, corporateActionOption3));
        when(corporateActionDetails.getDecisions())
                .thenReturn(Arrays.asList(corporateActionDecisionOption1, corporateActionDecisionOption2));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        List<CorporateActionOptionDto> optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(2, optionDtos.size());

        assertEquals((Integer) 1, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("Tender at 10% discount", optionDtos.get(0).getSummary());

        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, optionDtos.get(1).getId());
        assertEquals("Option B", optionDtos.get(1).getTitle());
        assertEquals("Take no action", optionDtos.get(1).getSummary());

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption2, corporateActionOption3));

        CorporateActionOption corporateActionDecisionOption3 =
                createOptionMock(CorporateActionDecisionKey.OPTION_QUANTITY.getCode(1), new BigDecimal("100.0"));
        when(corporateActionDetails.getDecisions())
                .thenReturn(Arrays.asList(corporateActionDecisionOption2, corporateActionDecisionOption3));

        optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertEquals(2, optionDtos.size());
        assertEquals((Integer) 1, optionDtos.get(0).getId());

        when(corporateActionDecisionOption2.hasValue()).thenReturn(false);

        optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertEquals(2, optionDtos.size());
        assertEquals((Integer) 1, optionDtos.get(0).getId());
    }

    @Test
    public void testToElectionOptionDtos_whenThereAreNoDecisions_thenPopulateListOfOptionDtosAccordingly() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption1 =
                createOptionMock(CorporateActionOptionKey.FINAL_TENDER_PRICE.getCode(), CorporateActionConverterConstants.OPTION_VALUE_NO);
        CorporateActionOption corporateActionOption2 = createOptionMock(CorporateActionOptionKey.OPTION.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionOption3 =
                createOptionMock(CorporateActionOptionKey.PRICE_AS_PERCENT.getCode(1), "10.0", BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption1, corporateActionOption2,
                corporateActionOption3));
        when(corporateActionDetails.getDecisions()).thenReturn(null);
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        List<CorporateActionOptionDto> optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(2, optionDtos.size());
    }

    @Test
    public void testToElectionOptionDtos_whenThereIsNoOption_thenPopulateListOfOptionDtosAccordingly() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption1 =
                createOptionMock(CorporateActionOptionKey.FINAL_TENDER_PRICE.getCode(), CorporateActionConverterConstants.OPTION_VALUE_NO);
        CorporateActionOption corporateActionOption2 =
                createOptionMock(CorporateActionOptionKey.PRICE_AS_PERCENT.getCode(1), "10.0", BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption1, corporateActionOption2));
        when(corporateActionDetails.getDecisions()).thenReturn(null);
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        List<CorporateActionOptionDto> optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(1, optionDtos.size());
    }

    @Test
    public void testToElectionOptionDtos_whenThereIsFinalTenderQuantity_thenPopulateListOfOptionDtos() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption1 =
                createOptionMock(CorporateActionOptionKey.FINAL_TENDER_PRICE.getCode(), CorporateActionConverterConstants.OPTION_VALUE_YES);
        CorporateActionOption corporateActionOption2 = createOptionMock(CorporateActionOptionKey.OPTION.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionOption3 =
                createOptionMock(CorporateActionOptionKey.PRICE_AS_PERCENT.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionDecisionOption1 =
                createOptionMock(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode(), new BigDecimal("100.0"));
        CorporateActionOption corporateActionDecisionOption2 =
                createOptionMock(CorporateActionDecisionKey.OPTION_PERCENT.getCode(1), new BigDecimal("100.0"));

        when(corporateActionDetails.getOptions())
                .thenReturn(Arrays.asList(corporateActionOption1, corporateActionOption2, corporateActionOption3));
        when(corporateActionDetails.getDecisions())
                .thenReturn(Arrays.asList(corporateActionDecisionOption1, corporateActionDecisionOption2));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        List<CorporateActionOptionDto> optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(3, optionDtos.size());
    }

    @Test
    public void testToElectionOptionDtos_whenThereIsNoFinalTenderQuantity_thenFinalTenderShouldNotBeTheDefaultOption() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption1 =
                createOptionMock(CorporateActionOptionKey.FINAL_TENDER_PRICE.getCode(), CorporateActionConverterConstants.OPTION_VALUE_YES);
        CorporateActionOption corporateActionOption2 = createOptionMock(CorporateActionOptionKey.OPTION.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionOption3 =
                createOptionMock(CorporateActionOptionKey.PRICE_AS_PERCENT.getCode(1), "10.0", BigDecimal.TEN);
        CorporateActionOption corporateActionDecisionOption1 =
                createOptionMock(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode(), new BigDecimal("0"));
        CorporateActionOption corporateActionDecisionOption2 =
                createOptionMock(CorporateActionDecisionKey.OPTION_PERCENT.getCode(1), new BigDecimal("50.0"));

        when(corporateActionDetails.getOptions())
                .thenReturn(Arrays.asList(corporateActionOption1, corporateActionOption2, corporateActionOption3));
        when(corporateActionDetails.getDecisions())
                .thenReturn(Arrays.asList(corporateActionDecisionOption1, corporateActionDecisionOption2));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        List<CorporateActionOptionDto> optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(3, optionDtos.size());

        assertFalse(optionDtos.get(0).getIsDefault());
        assertFalse(optionDtos.get(1).getIsDefault());
        assertTrue(optionDtos.get(2).getIsDefault());

        when(corporateActionDecisionOption1.hasValue()).thenReturn(Boolean.FALSE);
        optionDtos = buyBackResponseConverterService.toElectionOptionDtos(context, null);
        assertFalse(optionDtos.get(0).getIsDefault());
        assertFalse(optionDtos.get(1).getIsDefault());
        assertTrue(optionDtos.get(2).getIsDefault());
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenThereAreMinimumPrices_thenEnsureMinimumPricesAreSet() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption =
                createOptionMock(CorporateActionOptionKey.OPTION_MINIMUM_PRICE.getCode(1), BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        params = buyBackResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertFalse(params.getMinPrices().isEmpty());
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenThereIsPayDateAndItIsAfterToday_thenNullOutTransactionDetails() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        DateTime now = new DateTime();

        params.setTransactionNumber(0);
        params.setTransactionDescription("Transaction");

        when(corporateActionDetails.getPayDate()).thenReturn(now.plusDays(1));

        params = buyBackResponseConverterService.setCorporateActionAccountDetailsDtoParams(context, null, params);

        assertNull(params.getTransactionNumber());
        assertNull(params.getTransactionDescription());
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenTherePayDateIsNotTodayOrIsNull_thenDoNotModifyTransactionDetails() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        DateTime now = new DateTime();

        params.setTransactionNumber(0);
        params.setTransactionDescription("Transaction");

        when(corporateActionDetails.getPayDate()).thenReturn(now.minusDays(1));

        params = buyBackResponseConverterService.setCorporateActionAccountDetailsDtoParams(context, null, params);

        assertNotNull(params.getTransactionNumber());
        assertNotNull(params.getTransactionDescription());

        when(corporateActionDetails.getPayDate()).thenReturn(null);

        params = buyBackResponseConverterService.setCorporateActionAccountDetailsDtoParams(context, null, params);

        assertNotNull(params.getTransactionNumber());
        assertNotNull(params.getTransactionDescription());
    }
}