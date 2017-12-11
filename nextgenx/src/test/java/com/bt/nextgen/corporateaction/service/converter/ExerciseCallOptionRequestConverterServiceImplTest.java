package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.ExerciseCallOptionRequestConverterServiceImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionExerciseRightsType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExerciseCallOptionRequestConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private ExerciseCallOptionRequestConverterServiceImpl exerciseCallOptionRequestConverterService;

    @Test
    public void testCreateElectionGroups_whenFullExerciseElection_thenPopulateElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(CorporateActionExerciseRightsType.FULL.getId(),
                null, BigDecimal.ONE);

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(accountDetailsDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto.getPositionId()).thenReturn("0");
        when(details.getOrderNumber()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups =
                exerciseCallOptionRequestConverterService.createElectionGroups(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertEquals(electionGroup.getOrderNumber(), "0");
        assertFalse(electionGroup.getPositions().isEmpty());
        assertEquals("0", electionGroup.getPositions().get(0).getId());

        assertNotNull(electionGroup.getOptions());
        assertEquals(5, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_NO_ACTION.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("N", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(1).getKey());
        assertEquals("100", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertEquals("", electionGroup.getOptions().get(2).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode(), electionGroup.getOptions().get(3).getKey());
        assertEquals("Y", electionGroup.getOptions().get(3).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(4).getKey());
        assertEquals("Y", electionGroup.getOptions().get(4).getValue());
    }

    @Test
    public void testCreateElectionGroups_whenPartialExerciseElection_thenPopulateElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(CorporateActionExerciseRightsType.PARTIAL.getId
                (), BigDecimal.TEN, null);

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(accountDetailsDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto.getPositionId()).thenReturn("0");
        when(details.getOrderNumber()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups =
                exerciseCallOptionRequestConverterService.createElectionGroups(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertNotNull(electionGroup.getOptions());
        assertEquals(5, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_NO_ACTION.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("N", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(1)
                                                                                                .getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertEquals("10", electionGroup.getOptions().get(2).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode(), electionGroup.getOptions().get(3)
                                                                                            .getKey());
        assertEquals("N", electionGroup.getOptions().get(3).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(4).getKey());
        assertEquals("Y", electionGroup.getOptions().get(4).getValue());
    }

    @Test
    public void testCreateElectionGroups_whenLapsedExerciseElection_thenPopulateElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(CorporateActionExerciseRightsType.LAPSE.getId(),
                null, null);

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(accountDetailsDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto.getPositionId()).thenReturn("0");
        when(details.getOrderNumber()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups =
                exerciseCallOptionRequestConverterService.createElectionGroups(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertNotNull(electionGroup.getOptions());
        assertEquals(5, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_NO_ACTION.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("Y", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(1)
                                                                                                .getKey());
        assertEquals("0", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertEquals("", electionGroup.getOptions().get(2).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_ALL.getCode(), electionGroup.getOptions().get(3)
                                                                                            .getKey());
        assertEquals("N", electionGroup.getOptions().get(3).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(4).getKey());
        assertEquals("Y", electionGroup.getOptions().get(4).getValue());
    }
}