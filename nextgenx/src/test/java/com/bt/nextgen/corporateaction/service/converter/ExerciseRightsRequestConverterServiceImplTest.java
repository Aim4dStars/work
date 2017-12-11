package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.ExerciseRightsRequestConverterServiceImpl;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionExerciseRightsType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ExerciseRightsRequestConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private ExerciseRightsRequestConverterServiceImpl rightsExerciseRequestConverterService;

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
                rightsExerciseRequestConverterService.createElectionGroups(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertEquals(electionGroup.getOrderNumber(), "0");
        assertFalse(electionGroup.getPositions().isEmpty());
        assertEquals("0", electionGroup.getPositions().get(0).getId());

        assertNotNull(electionGroup.getOptions());
        assertEquals(4, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(1).getKey());
        assertEquals("100", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(),
                electionGroup.getOptions().get(2).getKey());
        assertEquals("1", electionGroup.getOptions().get(2).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(3).getKey());
        assertEquals("Y", electionGroup.getOptions().get(3).getValue());

        // Oversubscribe null
        when(selectedOptionDto.getOversubscribe()).thenReturn(null);

        electionGroups = rightsExerciseRequestConverterService.createElectionGroups(context, electionDetailsDto);

        electionGroup = electionGroups.iterator().next();
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(),
                electionGroup.getOptions().get(2).getKey());
        assertEquals("", electionGroup.getOptions().get(2).getValue());

        // Oversubscribe zero
        when(selectedOptionDto.getOversubscribe()).thenReturn(BigDecimal.ZERO);

        electionGroups = rightsExerciseRequestConverterService.createElectionGroups(context, electionDetailsDto);

        electionGroup = electionGroups.iterator().next();
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(), electionGroup.getOptions().get(2)
                                                                                                               .getKey());
        assertEquals("", electionGroup.getOptions().get(2).getValue());
    }

    @Test
    public void testCreateElectionGroups_whenPartialExerciseElection_thenPopulateElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto =
                createSelectedOptionDtoMock(CorporateActionExerciseRightsType.PARTIAL.getId(), BigDecimal.TEN, null);

        List<CorporateActionOption> options = new ArrayList<>();
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.ONE));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), BigDecimal.valueOf(2.0)));

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(accountDetailsDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto.getPositionId()).thenReturn("0");
        when(details.getOrderNumber()).thenReturn("0");
        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups =
                rightsExerciseRequestConverterService.createElectionGroups(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertNotNull(electionGroup.getOptions());
        assertEquals(4, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(),
                electionGroup.getOptions().get(1).getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertTrue(BigDecimal.valueOf(5.0).compareTo(new BigDecimal(electionGroup.getOptions().get(2).getValue())) == 0);
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(3).getKey());
        assertEquals("Y", electionGroup.getOptions().get(3).getValue());
    }

    @Test
    public void testCreateElectionGroups_whenPartialExerciseElectionButNoApplicableRatio_thenPopulateElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto =
                createSelectedOptionDtoMock(CorporateActionExerciseRightsType.PARTIAL.getId(), BigDecimal.TEN, null);

        List<CorporateActionOption> options = new ArrayList<>();
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), (BigDecimal) null));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), (BigDecimal) null));

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(accountDetailsDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto.getPositionId()).thenReturn("0");
        when(details.getOrderNumber()).thenReturn("0");
        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups =
                rightsExerciseRequestConverterService.createElectionGroups(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertNotNull(electionGroup.getOptions());
        assertEquals(4, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(),
                electionGroup.getOptions().get(1).getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertTrue(BigDecimal.TEN.compareTo(new BigDecimal(electionGroup.getOptions().get(2).getValue())) == 0);
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(3).getKey());
        assertEquals("Y", electionGroup.getOptions().get(3).getValue());

        // Old stock is zero
        options.clear();
        options.add(createOptionMock(CorporateActionOptionKey.OLD_STOCK_HELD.getCode(), BigDecimal.ZERO));
        options.add(createOptionMock(CorporateActionOptionKey.NEW_STOCK_ALLOCATED.getCode(), (BigDecimal) null));

        electionGroups = rightsExerciseRequestConverterService.createElectionGroups(context, electionDetailsDto);

        electionGroup = electionGroups.iterator().next();

        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertTrue(BigDecimal.TEN.compareTo(new BigDecimal(electionGroup.getOptions().get(2).getValue())) == 0);

        // Empty options
        when(details.getOptions()).thenReturn(new ArrayList<CorporateActionOption>());

        electionGroups = rightsExerciseRequestConverterService.createElectionGroups(context, electionDetailsDto);

        electionGroup = electionGroups.iterator().next();

        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertTrue(BigDecimal.TEN.compareTo(new BigDecimal(electionGroup.getOptions().get(2).getValue())) == 0);
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
                rightsExerciseRequestConverterService.createElectionGroups(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertNotNull(electionGroup.getOptions());
        assertEquals(4, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("0", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(),
                electionGroup.getOptions().get(1).getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertEquals("", electionGroup.getOptions().get(2).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(3).getKey());
        assertEquals("Y", electionGroup.getOptions().get(3).getValue());
    }

    @Test
    public void testCreateElectionGroupsForIm_whenFullExerciseElection_thenPopulateElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        ImCorporateActionElectionDetailsDto electionDetailsDto = mock(ImCorporateActionElectionDetailsDto.class);
        ImCorporateActionPortfolioModelDto portfolioModelDto = mock(ImCorporateActionPortfolioModelDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(CorporateActionExerciseRightsType.FULL.getId(),
                null, BigDecimal.ONE);

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getPortfolioModels()).thenReturn(Arrays.asList(portfolioModelDto));
        when(portfolioModelDto.getIpsId()).thenReturn("0");
        when(portfolioModelDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(account.getPositionId()).thenReturn("0");
        when(account.getIpsId()).thenReturn("0");
        when(account.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);
        when(details.getOrderNumber()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(context.getCorporateActionAccountList()).thenReturn(Arrays.asList(account));

        Collection<CorporateActionElectionGroup> electionGroups =
                rightsExerciseRequestConverterService.createElectionGroupsForIm(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertEquals(electionGroup.getOrderNumber(), "0");
        assertFalse(electionGroup.getPositions().isEmpty());
        assertEquals("0", electionGroup.getPositions().get(0).getId());

        assertNotNull(electionGroup.getOptions());
        assertEquals(4, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(1).getKey());
        assertEquals("100", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(),
                electionGroup.getOptions().get(2).getKey());
        assertEquals("1", electionGroup.getOptions().get(2).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(3).getKey());
        assertEquals("Y", electionGroup.getOptions().get(3).getValue());
    }

    @Test
    public void testCreateElectionGroupsForIm_whenPartialExerciseElection_thenPopulateElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        ImCorporateActionElectionDetailsDto electionDetailsDto = mock(ImCorporateActionElectionDetailsDto.class);
        ImCorporateActionPortfolioModelDto portfolioModelDto = mock(ImCorporateActionPortfolioModelDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(CorporateActionExerciseRightsType.PARTIAL.getId
                (), BigDecimal.TEN, null);

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getPortfolioModels()).thenReturn(Arrays.asList(portfolioModelDto));
        when(portfolioModelDto.getIpsId()).thenReturn("0");
        when(portfolioModelDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(account.getPositionId()).thenReturn("0");
        when(account.getIpsId()).thenReturn("0");
        when(account.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);
        when(details.getOrderNumber()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(context.getCorporateActionAccountList()).thenReturn(Arrays.asList(account));

        Collection<CorporateActionElectionGroup> electionGroups =
                rightsExerciseRequestConverterService.createElectionGroupsForIm(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertNotNull(electionGroup.getOptions());
        assertEquals(4, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(),
                electionGroup.getOptions().get(1).getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertTrue(BigDecimal.valueOf(10).compareTo(new BigDecimal(electionGroup.getOptions().get(2).getValue())) == 0);
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(3).getKey());
        assertEquals("Y", electionGroup.getOptions().get(3).getValue());
    }

    @Test
    public void testCreateElectionGroupsForIm_whenLapsedExerciseElection_thenPopulateElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        ImCorporateActionElectionDetailsDto electionDetailsDto = mock(ImCorporateActionElectionDetailsDto.class);
        ImCorporateActionPortfolioModelDto portfolioModelDto = mock(ImCorporateActionPortfolioModelDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(CorporateActionExerciseRightsType.LAPSE.getId(),
                null, null);

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getPortfolioModels()).thenReturn(Arrays.asList(portfolioModelDto));
        when(portfolioModelDto.getIpsId()).thenReturn("0");
        when(portfolioModelDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(account.getPositionId()).thenReturn("0");
        when(account.getIpsId()).thenReturn("0");
        when(account.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);
        when(details.getOrderNumber()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(context.getCorporateActionAccountList()).thenReturn(Arrays.asList(account));

        Collection<CorporateActionElectionGroup> electionGroups =
                rightsExerciseRequestConverterService.createElectionGroupsForIm(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertNotNull(electionGroup.getOptions());
        assertEquals(4, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_PERCENT.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("0", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_OVERSUBSCRIBE_QUANTITY.getCode(),
                electionGroup.getOptions().get(1).getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.EXERCISE_RIGHTS_QUANTITY.getCode(), electionGroup.getOptions().get(2).getKey());
        assertEquals("", electionGroup.getOptions().get(2).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(3).getKey());
        assertEquals("Y", electionGroup.getOptions().get(3).getValue());
    }
}