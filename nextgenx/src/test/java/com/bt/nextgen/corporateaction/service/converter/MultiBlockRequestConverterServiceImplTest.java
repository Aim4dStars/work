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
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MultiBlockRequestConverterServiceImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MultiBlockRequestConverterServiceImplTest {
    @InjectMocks
    private MultiBlockRequestConverterServiceImpl converter;

    @Test
    public void testCreateElectionGroups_whenThereAreElectionDetails_thenReturnPopulatedElectionGroups() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto1 = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto2 = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = mock(CorporateActionSelectedOptionDto.class);

        when(accountDetailsDto1.getPositionId()).thenReturn("0");
        when(selectedOptionDto.getOptionId()).thenReturn(1);
        when(selectedOptionDto.getPercent()).thenReturn(null);
        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(accountDetailsDto1.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto2.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto1, accountDetailsDto2));
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups = converter.createElectionGroups(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();
        assertNotNull(electionGroup.getOptions());

        assertEquals(CorporateActionDecisionKey.QUANTITY.getCode(1), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.PERCENT.getCode(1), electionGroup.getOptions().get(1).getKey());
        assertEquals("100", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(),
                electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_OPTIONS * 2).getKey());
        assertEquals("Y", electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_OPTIONS * 2).getValue());

        when(selectedOptionDto.getPercent()).thenReturn(BigDecimal.TEN);
        electionGroups = converter.createElectionGroups(context, electionDetailsDto);
        electionGroup = electionGroups.iterator().next();
        assertEquals("10", electionGroup.getOptions().get(1).getValue());
    }
}