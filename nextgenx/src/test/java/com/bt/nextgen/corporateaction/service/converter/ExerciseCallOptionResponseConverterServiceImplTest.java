package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.service.converter.ExerciseCallOptionResponseConverterServiceImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExerciseCallOptionResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private ExerciseCallOptionResponseConverterServiceImpl exerciseCallOptionResponseConverterService;

    @Before
    public void setup() {
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenStrikePriceOptionIsNotAvailable_thenDoNotSetThePrice() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        when(corporateActionDetails.getOptions()).thenReturn(new ArrayList<CorporateActionOption>());
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        params = exerciseCallOptionResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertNull(params.getCorporateActionPrice());
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenStrikePriceOptionIsAvailable_thenSetThePrice() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption = createOptionMock(CorporateActionOptionKey.STRIKE_PRICE.getCode(), BigDecimal.TEN);
        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        params = exerciseCallOptionResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCorporateActionPrice()) == 0);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenAccountIsNotSubmitted_thenDoNotModifyTheAccountBalance() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.NOT_SUBMITTED);
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = exerciseCallOptionResponseConverterService.setCorporateActionAccountDetailsDtoParams(context,
                corporateActionAccount, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void
    testSetCorporateActionAccountDetailsDtoParams_whenAccountIsSubmittedButNoStrikePrice_thenDoNotModifyTheAccountBalance() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = exerciseCallOptionResponseConverterService
                .setCorporateActionAccountDetailsDtoParams(context, corporateActionAccount, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void
    testSetCorporateActionAccountDetailsDtoParams_whenAccountIsSubmittedAndHasStrikePriceAndIsPartialExercise_thenModifyTheAccountBalanceToBeOriginal() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption = createOptionMock(CorporateActionOptionKey.STRIKE_PRICE.getCode(), BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);
        CorporateActionOption corporateActionAccountOption =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), BigDecimal.TEN);

        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption));

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = exerciseCallOptionResponseConverterService
                .setCorporateActionAccountDetailsDtoParams(context, corporateActionAccount, params);

        assertTrue(BigDecimal.valueOf(110.0).compareTo(params.getCash()) == 0);
    }

    @Test
    public void
    testSetCorporateActionAccountDetailsDtoParams_whenAccountIsSubmittedAndHasStrikePriceAndIsPartialExerciseButNoQuantityOrZero_thenDoNotModifyTheAccountBalance() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption = createOptionMock(CorporateActionOptionKey.STRIKE_PRICE.getCode(), BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(corporateActionAccount.getDecisions()).thenReturn(new ArrayList<CorporateActionOption>());

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = exerciseCallOptionResponseConverterService
                .setCorporateActionAccountDetailsDtoParams(context, corporateActionAccount, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);

        CorporateActionOption corporateActionAccountOption = mock(CorporateActionOption.class);
        when(corporateActionAccountOption.getKey()).thenReturn(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode());
        when(corporateActionAccountOption.getValue()).thenReturn("0.0");
        when(corporateActionAccountOption.getBigDecimalValue()).thenReturn(BigDecimal.ZERO);
        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption));

        params.setCash(BigDecimal.TEN);
        params = exerciseCallOptionResponseConverterService
                .setCorporateActionAccountDetailsDtoParams(context, corporateActionAccount, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void
    testSetCorporateActionAccountDetailsDtoParams_whenAccountIsSubmittedAndHasStrikePriceAndIsFullExerciseWithOversubscribe_thenModifyTheAccountBalanceToBeOriginal() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption = createOptionMock(CorporateActionOptionKey.STRIKE_PRICE.getCode(), BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);
        CorporateActionOption corporateActionAccountOption =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode(), "Y");
        CorporateActionOption corporateActionAccountOptionQty =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(), BigDecimal.valueOf(5.0));

        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(corporateActionAccount.getDecisions())
                .thenReturn(Arrays.asList(corporateActionAccountOption, corporateActionAccountOptionQty));

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);
        params.setHolding(5);

        params = exerciseCallOptionResponseConverterService
                .setCorporateActionAccountDetailsDtoParams(context, corporateActionAccount, params);

        assertTrue(BigDecimal.valueOf(110.0).compareTo(params.getCash()) == 0);
    }

    @Test
    public void
    testSetCorporateActionAccountDetailsDtoParams_whenAccountIsSubmittedAndHasStrikePriceAndIsFullExercise_thenModifyTheAccountBalanceToBeOriginal() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption = createOptionMock(CorporateActionOptionKey.STRIKE_PRICE.getCode(), BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);
        CorporateActionOption corporateActionAccountOption =
                createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode(), "Y");

        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption));

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);
        params.setHolding(5);

        params = exerciseCallOptionResponseConverterService
                .setCorporateActionAccountDetailsDtoParams(context, corporateActionAccount, params);

        assertTrue(BigDecimal.valueOf(60.0).compareTo(params.getCash()) == 0);
    }

    @Test
    public void
    testSetCorporateActionAccountDetailsDtoParams_whenAccountIsSubmittedAndHasStrikePriceButHasNoOptionSelected_thenDoNotModifyTheAccountBalance() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption = createOptionMock(CorporateActionOptionKey.STRIKE_PRICE.getCode(), BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(corporateActionAccount.getDecisions()).thenReturn(new ArrayList<CorporateActionOption>());

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);
        params.setHolding(5);

        params = exerciseCallOptionResponseConverterService
                .setCorporateActionAccountDetailsDtoParams(context, corporateActionAccount, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);

        CorporateActionOption corporateActionAccountOption = createOptionMock(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode(), "");
        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption));

        params.setCash(BigDecimal.TEN);
        params = exerciseCallOptionResponseConverterService
                .setCorporateActionAccountDetailsDtoParams(context, corporateActionAccount, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void
    testSetCorporateActionAccountDetailsDtoParams_whenAccountIsSubmittedAndHasStrikePriceAndLapseExercise_thenDoNotModifyTheAccountBalance() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionOption corporateActionOption = createOptionMock(CorporateActionOptionKey.STRIKE_PRICE.getCode(), BigDecimal.TEN);

        when(corporateActionDetails.getOptions()).thenReturn(Arrays.asList(corporateActionOption));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(corporateActionAccount.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(corporateActionAccount.getDecisions()).thenReturn(null);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = exerciseCallOptionResponseConverterService
                .setCorporateActionAccountDetailsDtoParams(context, corporateActionAccount, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }
}