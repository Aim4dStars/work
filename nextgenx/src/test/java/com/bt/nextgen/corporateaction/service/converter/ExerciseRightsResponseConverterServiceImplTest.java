package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionExerciseRightsAccountElectionDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.converter.ExerciseRightsResponseConverterServiceImpl;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElectionKey;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountKey;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipation;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionExerciseRightsType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExerciseRightsResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private ExerciseRightsResponseConverterServiceImpl rightsExerciseResponseConverterServiceImpl;

    @Before
    public void setup() {
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenItIsOneHundredPercentExercise_thenReturnFullExerciseAccountOption() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), new BigDecimal("100.0")));
        options.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(), BigDecimal.TEN));

        when(account.getDecisions()).thenReturn(options);
        CorporateActionAccountElectionsDto electionDto = rightsExerciseResponseConverterServiceImpl.toSubmittedAccountElectionsDto(null,
                account);

        assertNotNull(electionDto);
        assertEquals((Integer) CorporateActionExerciseRightsType.FULL.getId(), electionDto.getPrimaryAccountElection().getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(((CorporateActionExerciseRightsAccountElectionDtoImpl) electionDto.getPrimaryAccountElection
                ()).getOversubscribe()) == 0);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenItIsNotOneHundredPercentExercise_thenReturnPartialExerciseAccountOption() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        List<CorporateActionOption> options = new ArrayList<>();
        List<CorporateActionOption> accountOptions = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.ONE));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), BigDecimal.valueOf(2.0)));
        accountOptions.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), (BigDecimal) null));
        accountOptions.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), BigDecimal.TEN));

        when(details.getOptions()).thenReturn(options);
        when(account.getDecisions()).thenReturn(accountOptions);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountElectionsDto electionDto =
                rightsExerciseResponseConverterServiceImpl.toSubmittedAccountElectionsDto(context, account);

        assertNotNull(electionDto);
        assertEquals((Integer) CorporateActionExerciseRightsType.PARTIAL.getId(), electionDto.getPrimaryAccountElection().getOptionId());
        assertTrue(BigDecimal.valueOf(20.0).compareTo(electionDto.getPrimaryAccountElection().getUnits()) == 0);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsRightsExerciseQuantity_thenReturnPartialExerciseAccountOption() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        List<CorporateActionOption> accountOptions = new ArrayList<>();
        accountOptions.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), BigDecimal.TEN));

        List<CorporateActionOption> options = new ArrayList<>();
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.ONE));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), BigDecimal.valueOf(2.0)));

        when(account.getDecisions()).thenReturn(accountOptions);
        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountElectionsDto electionDto = rightsExerciseResponseConverterServiceImpl.toSubmittedAccountElectionsDto(context,
                account);

        assertNotNull(electionDto);
        assertEquals((Integer) CorporateActionExerciseRightsType.PARTIAL.getId(), electionDto.getPrimaryAccountElection().getOptionId());
        assertTrue(BigDecimal.valueOf(20).compareTo(electionDto.getPrimaryAccountElection().getUnits()) == 0);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsNoPercentageOrQuantity_thenReturnLapsedExerciseAccountOption() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(account.getDecisions()).thenReturn(new ArrayList<CorporateActionOption>());
        CorporateActionAccountElectionsDto electionDto = rightsExerciseResponseConverterServiceImpl.toSubmittedAccountElectionsDto(null,
                account);

        assertNotNull(electionDto);
        assertEquals((Integer) CorporateActionExerciseRightsType.LAPSE.getId(), electionDto.getPrimaryAccountElection().getOptionId());
    }


    @Test
    public void testToSubmittedAccountElectionsDto_whenNoDecisions_thenReturnLapsedExerciseAccountOption() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(account.getDecisions()).thenReturn(null);
        CorporateActionAccountElectionsDto electionDto = rightsExerciseResponseConverterServiceImpl.toSubmittedAccountElectionsDto(null,
                account);

        assertNotNull(electionDto);
        assertEquals((Integer) CorporateActionExerciseRightsType.LAPSE.getId(), electionDto.getPrimaryAccountElection().getOptionId());
    }

    @Test
    public void
    testToSubmittedAccountElectionsDto_whenPartialExerciseButOldStockOrNewStockIsInvalid_thenReturnPartialExerciseAccountOptionWithCorrectUnits() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        List<CorporateActionOption> options = new ArrayList<>();
        List<CorporateActionOption> accountOptions = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), (BigDecimal) null));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), (BigDecimal) null));
        accountOptions.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), (BigDecimal) null));
        accountOptions.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), BigDecimal.TEN));

        when(details.getOptions()).thenReturn(options);
        when(account.getDecisions()).thenReturn(accountOptions);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountElectionsDto electionDto =
                rightsExerciseResponseConverterServiceImpl.toSubmittedAccountElectionsDto(context, account);

        assertNotNull(electionDto);
        assertEquals((Integer) CorporateActionExerciseRightsType.PARTIAL.getId(), electionDto.getPrimaryAccountElection().getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(electionDto.getPrimaryAccountElection().getUnits()) == 0);

        options.clear();
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.ZERO));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), (BigDecimal) null));
        electionDto = rightsExerciseResponseConverterServiceImpl.toSubmittedAccountElectionsDto(context, account);

        assertTrue(BigDecimal.TEN.compareTo(electionDto.getPrimaryAccountElection().getUnits()) == 0);

        options.clear();
        electionDto = rightsExerciseResponseConverterServiceImpl.toSubmittedAccountElectionsDto(context, account);

        assertTrue(BigDecimal.TEN.compareTo(electionDto.getPrimaryAccountElection().getUnits()) == 0);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedDetails_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);

        when(savedDetails.getSavedParticipation()).thenReturn(null);

        assertNull(rightsExerciseResponseConverterServiceImpl.toSavedAccountElectionsDto(null, null, savedDetails));
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
        when(savedAccountElection.getUnits()).thenReturn(BigDecimal.TEN);
        when(savedAccount.getAccountElections()).thenReturn(Arrays.asList(savedAccountElection));
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                rightsExerciseResponseConverterServiceImpl.toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNotNull(electionsDto);
        assertNotNull(electionsDto.getPrimaryAccountElection());
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(electionsDto.getPrimaryAccountElection().getUnits()) == 0);
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

        CorporateActionAccountElectionsDto electionsDto = rightsExerciseResponseConverterServiceImpl.toSavedAccountElectionsDto(null,
                "0", savedDetails);

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

        CorporateActionAccountElectionsDto electionsDto = rightsExerciseResponseConverterServiceImpl.toSavedAccountElectionsDto(null,
                "1", savedDetails);

        assertNull(electionsDto);
    }

    @Test
    public void testToElectionOptionDtos_whenNotDealerGroupOrInvestmentManager_thenThereShouldBeThreeOptionsAvailable() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionOption option = createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), new BigDecimal
                ("100.0"));
        when(details.getDecisions()).thenReturn(Arrays.asList(option));
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<CorporateActionOptionDto> optionDtos = rightsExerciseResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertEquals(3, optionDtos.size());

        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("Lapse", optionDtos.get(0).getSummary());
        assertFalse(optionDtos.get(0).getIsDefault());

        assertEquals("Option B", optionDtos.get(1).getTitle());
        assertEquals("Partial exercise", optionDtos.get(1).getSummary());
        assertFalse(optionDtos.get(1).getIsDefault());

        assertEquals("Option C", optionDtos.get(2).getTitle());
        assertEquals("Full exercise", optionDtos.get(2).getSummary());
        assertTrue(optionDtos.get(2).getIsDefault());

        option = createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), BigDecimal.TEN);
        when(details.getDecisions()).thenReturn(Arrays.asList(option));

        optionDtos = rightsExerciseResponseConverterServiceImpl.toElectionOptionDtos(context, null);
        assertTrue(optionDtos.get(1).getIsDefault());

        when(details.getDecisions()).thenReturn(null);

        optionDtos = rightsExerciseResponseConverterServiceImpl.toElectionOptionDtos(context, null);
        assertTrue(optionDtos.get(0).getIsDefault());
    }

    @Test
    public void testToElectionOptionDtos_whenIsDealerGroupOrInvestmentManager_thenThereShouldBeTwoOptionsAvailable() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionOption option =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), new BigDecimal("100.0"));
        when(details.getDecisions()).thenReturn(Arrays.asList(option));
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(context.isDealerGroupOrInvestmentManager()).thenReturn(true);

        List<CorporateActionOptionDto> optionDtos = rightsExerciseResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertEquals(2, optionDtos.size());

        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("Lapse", optionDtos.get(0).getSummary());
        assertFalse(optionDtos.get(0).getIsDefault());

        assertEquals("Option B", optionDtos.get(1).getTitle());
        assertEquals("Full exercise", optionDtos.get(1).getSummary());
        assertTrue(optionDtos.get(1).getIsDefault());
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenOversubscribeAndHasPrice_thenPopulateParam() {
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        List<CorporateActionOption> options = new ArrayList<>();
        options.add(createOptionMock(CorporateActionOptionKey.OVERSUBSCRIBE.getCode(), "Y"));
        options.add(createOptionMock(CorporateActionOptionKey.PRICE.getCode(), "10.0"));

        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertTrue(params.getOversubscribe());
        assertTrue(BigDecimal.TEN.compareTo(params.getCorporateActionPrice()) == 0);
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenOversubscribeAndHasMaxPercentOrMaxQuantity_thenPopulateParamAccordingly() {
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        List<CorporateActionOption> options = new ArrayList<>();
        options.add(createOptionMock(CorporateActionOptionKey.OVERSUBSCRIBE.getCode(), "Y"));
        options.add(createOptionMock(CorporateActionOptionKey.MAX_OVERSUBSCRIBE_PERCENT.getCode(), "10.0", BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.MAX_OVERSUBSCRIBE_QUANTITY.getCode(), "80.0", BigDecimal.valueOf(80.0)));

        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertTrue(params.getOversubscribe());
        assertNotNull(params.getOversubscription());
        assertNotNull(params.getOversubscription().toString());
        assertTrue(BigDecimal.TEN.compareTo(params.getOversubscription().getMaximumPercent()) == 0);
        assertTrue(BigDecimal.valueOf(80.0).compareTo(params.getOversubscription().getMaximumQuantity()) == 0);
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenNotOversubscribeAndNoPrice_thenPopulateParam() {
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        List<CorporateActionOption> options = new ArrayList<>();
        options.add(createOptionMock(CorporateActionOptionKey.OVERSUBSCRIBE.getCode(), "N"));

        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertFalse(params.getOversubscribe());
        assertNull(params.getCorporateActionPrice());

        when(details.getOptions()).thenReturn(new ArrayList<CorporateActionOption>());

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertFalse(params.getOversubscribe());
        assertNull(params.getCorporateActionPrice());
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenNotSubmitted_thenDoNotModifyCash() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.NOT_SUBMITTED);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);
        params.setHolding(10);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenSubmittedAndHasPrice_thenModifyCash() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        CorporateActionOption priceOption = createOptionMock(CorporateActionOptionKey.PRICE.getCode(), BigDecimal.TEN);
        CorporateActionOption partialExerciseOption =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), BigDecimal.TEN);
        CorporateActionOption fullExerciseOption =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), BigDecimal.valueOf(100.0));
        CorporateActionOption oversubscribeOption =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(), BigDecimal.valueOf(5.0));

        when(details.getOptions()).thenReturn(Arrays.asList(priceOption));
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(context.getCorporateActionDetails()).thenReturn(details);

        // Partial exercise test
        when(account.getDecisions()).thenReturn(Arrays.asList(partialExerciseOption));

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setHolding(10);
        params.setCash(BigDecimal.TEN);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);
        assertTrue(BigDecimal.valueOf(110.0).compareTo(params.getCash()) == 0);

        // Full exercise test
        when(account.getDecisions()).thenReturn(Arrays.asList(fullExerciseOption));

        params.setCash(BigDecimal.TEN);
        params.setHolding(5);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);
        assertTrue(BigDecimal.valueOf(60.0).compareTo(params.getCash()) == 0);

        // Full exercise with oversubscribe test
        when(account.getDecisions()).thenReturn(Arrays.asList(fullExerciseOption, oversubscribeOption));

        params.setCash(BigDecimal.TEN);
        params.setHolding(5);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);
        assertTrue(BigDecimal.valueOf(110.0).compareTo(params.getCash()) == 0);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenSubmittedAndHasPriceButHasNoValidPercentOrQuantity_thenDoNotModifyCash() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        CorporateActionOption priceOption = createOptionMock(CorporateActionOptionKey.PRICE.getCode(), BigDecimal.TEN);
        CorporateActionOption invalidPercentOption =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), BigDecimal.valueOf(-1.0));
        CorporateActionOption invalidQuantityOption =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), BigDecimal.valueOf(-1.0));

        when(details.getOptions()).thenReturn(Arrays.asList(priceOption));
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);
        params.setHolding(10);

        when(account.getDecisions()).thenReturn(Arrays.asList(invalidPercentOption));
        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);
        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);

        when(account.getDecisions()).thenReturn(Arrays.asList(invalidQuantityOption));
        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);
        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenSubmittedAndLapsed_thenDoNotModifyCash() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.PRICE.getCode(), BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), BigDecimal.ONE));

        when(details.getOptions()).thenReturn(options);
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);

        when(context.getCorporateActionDetails()).thenReturn(details);

        when(account.getDecisions()).thenReturn(null);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);
        params.setHolding(10);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);
        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
        assertEquals((Integer) 1, params.getHolding());
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenSubmittedAndHasNoPrice_thenDoNotModifyCash() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), BigDecimal.ONE));

        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setHolding(10);
        params.setCash(BigDecimal.TEN);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);
        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
        assertEquals((Integer) 1, params.getHolding());
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenThereAreCorporateActionDetails_thenApplicableRationMustBeSet() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.TEN));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), BigDecimal.ONE));

        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertNotNull(params.getApplicableRatio());
        assertTrue(BigDecimal.TEN.compareTo(params.getApplicableRatio().getOldStock()) == 0);
        assertTrue(BigDecimal.ONE.compareTo(params.getApplicableRatio().getNewStock()) == 0);
        assertTrue(BigDecimal.valueOf(0.1).compareTo(params.getApplicableRatio().getRatio()) == 0);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenThereIsRatio_thenApplyRatioToHolding() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        List<CorporateActionOption> options = new ArrayList<>();
        List<CorporateActionOption> accountOptions = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.ONE));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), BigDecimal.valueOf(2.0)));
        accountOptions.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), (BigDecimal) null));
        accountOptions.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), BigDecimal.TEN));

        when(details.getOptions()).thenReturn(options);
        when(account.getDecisions()).thenReturn(accountOptions);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setHolding(10);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);

        assertEquals((Integer) 10, params.getOriginalHolding());
        assertEquals((Integer) 20, params.getHolding());
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenThereIsAnInvalidNewStockValue_thenDoNotApplyRatioToHolding() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        List<CorporateActionOption> options = new ArrayList<>();
        List<CorporateActionOption> accountOptions = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.ONE));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), BigDecimal.valueOf(-2.0)));
        accountOptions.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), (BigDecimal) null));
        accountOptions.add(createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), BigDecimal.TEN));

        when(details.getOptions()).thenReturn(options);
        when(account.getDecisions()).thenReturn(accountOptions);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setHolding(10);

        params = rightsExerciseResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);

        assertNull(params.getOriginalHolding());
        assertEquals((Integer) 10, params.getHolding());
    }
}