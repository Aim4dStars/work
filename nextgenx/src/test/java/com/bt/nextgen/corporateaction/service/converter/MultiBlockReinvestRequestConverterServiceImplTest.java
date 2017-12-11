package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MultiBlockReinvestRequestConverterServiceImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MultiBlockReinvestRequestConverterServiceImplTest {
    @InjectMocks
    private MultiBlockReinvestRequestConverterServiceImpl multiBlockReinvestRequestConverterServiceImpl;

    @Before
    public void setup() {
    }

    @Test
    public void testCreateElectionGroups_whenElectionDetailsArePopulated_thenReturnAPopulatedElectionGroupObject() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto1 = mock(CorporateActionSelectedOptionDto.class);

        when(details.getOrderNumber()).thenReturn("0");
        when(selectedOptionDto1.getOptionId()).thenReturn(1);
        when(selectedOptionDto1.getUnits()).thenReturn(BigDecimal.TEN);
        when(selectedOptionsDto.getOptions()).thenReturn(Arrays.asList(selectedOptionDto1));
        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto1);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(accountDetailsDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto.getPositionId()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups =
                multiBlockReinvestRequestConverterServiceImpl.createElectionGroups(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());

        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();
        assertNotNull(electionGroup.getOptions());
        assertEquals(CorporateActionConverterConstants.MAX_OPTIONS * 2 + 1, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.QUANTITY.getCode(1), electionGroup.getOptions().get(0).getKey());
        assertEquals("10", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.PERCENT.getCode(1), electionGroup.getOptions().get(1).getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());

        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get(14).getKey());
        assertEquals("Y", electionGroup.getOptions().get(14).getValue());
    }

    @Test
    public void testCreateElectionGroups_whenElectionDetailsArePopulatedWithPercentage_thenReturnAPopulatedElectionGroupObject() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto1 = mock(CorporateActionSelectedOptionDto.class);

        when(details.getOrderNumber()).thenReturn("0");
        when(selectedOptionDto1.getOptionId()).thenReturn(1);
        when(selectedOptionDto1.getUnits()).thenReturn(null);
        when(selectedOptionsDto.getOptions()).thenReturn(Arrays.asList(selectedOptionDto1));
        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto1);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(accountDetailsDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto.getPositionId()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(context.isDealerGroupOrInvestmentManager()).thenReturn(true);

        Collection<CorporateActionElectionGroup> electionGroups =
                multiBlockReinvestRequestConverterServiceImpl.createElectionGroups(context, electionDetailsDto);

        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertEquals(CorporateActionDecisionKey.QUANTITY.getCode(1), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.PERCENT.getCode(1), electionGroup.getOptions().get(1).getKey());
        assertEquals("100", electionGroup.getOptions().get(1).getValue());
    }
}
