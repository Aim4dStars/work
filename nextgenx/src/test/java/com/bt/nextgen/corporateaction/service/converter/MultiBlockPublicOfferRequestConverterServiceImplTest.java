package com.bt.nextgen.corporateaction.service.converter;


import java.math.BigDecimal;
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
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MultiBlockPublicOfferRequestConverterServiceImpl;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MultiBlockPublicOfferRequestConverterServiceImplTest {
    @InjectMocks
    private MultiBlockPublicOfferRequestConverterServiceImpl converter;

    @Test
    public void testCreateElectionGroups_whenThereAreElectionDetailsAndHasTakeOverLimit_thenReturnElectionGroupsWithQuantityPopulated() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto1 = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto2 = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = mock(CorporateActionSelectedOptionDto.class);

        when(accountDetailsDto1.getPositionId()).thenReturn("0");
        when(selectedOptionDto.getOptionId()).thenReturn(1);
        when(selectedOptionDto.getUnits()).thenReturn(null);
        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(accountDetailsDto1.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto2.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto1, accountDetailsDto2));
        when(details.getTakeoverLimit()).thenReturn(BigDecimal.TEN);
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups = converter.createElectionGroups(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();
        assertNotNull(electionGroup.getOptions());

        assertEquals(CorporateActionDecisionKey.QUANTITY.getCode(1), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.PERCENT.getCode(1), electionGroup.getOptions().get(1).getKey());
        assertEquals("10", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get
                (CorporateActionConverterConstants.MAX_OPTIONS * 2).getKey());
        assertEquals("Y", electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_OPTIONS * 2).getValue());

        when(selectedOptionDto.getUnits()).thenReturn(BigDecimal.TEN);
        electionGroups = converter.createElectionGroups(context, electionDetailsDto);
        electionGroup = electionGroups.iterator().next();
        assertEquals("10", electionGroup.getOptions().get(1).getValue());
    }

    @Test
    public void testCreateElectionGroupsForIm_whenThereArePortfolioModels_thenReturnPopulatedElectionGroups() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        ImCorporateActionElectionDetailsDto electionDetailsDto = mock(ImCorporateActionElectionDetailsDto.class);
        ImCorporateActionPortfolioModelDto portfolioModelDto = mock(ImCorporateActionPortfolioModelDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = mock(CorporateActionSelectedOptionDto.class);

        when(account.getIpsId()).thenReturn("0");
        when(account.getPositionId()).thenReturn("0");
        when(account.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);
        when(portfolioModelDto.getIpsId()).thenReturn("0");
        when(selectedOptionDto.getOptionId()).thenReturn(1);
        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getPortfolioModels()).thenReturn(Arrays.asList(portfolioModelDto));
        when(portfolioModelDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(details.getTakeoverLimit()).thenReturn(BigDecimal.TEN);
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(account.getEligibleQuantity()).thenReturn(BigDecimal.valueOf(200));
        when(account.getAvailableQuantity()).thenReturn(BigDecimal.valueOf(200));
        when(context.getCorporateActionAccountList()).thenReturn(Arrays.asList(account));

        List<CorporateActionElectionGroup> electionGroups =
                (List<CorporateActionElectionGroup>) converter.createElectionGroupsForIm(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());
        assertNotNull(electionGroups.get(0).getOptions());
        CorporateActionElectionGroup electionGroup = electionGroups.get(0);
        assertEquals(CorporateActionDecisionKey.QUANTITY.getCode(1), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.PERCENT.getCode(1), electionGroup.getOptions().get(1).getKey());
        assertEquals("10", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get
                (CorporateActionConverterConstants.MAX_OPTIONS * 2).getKey());
        assertEquals("Y", electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_OPTIONS * 2).getValue());
    }

    @Test
    public void
    testCreateElectionGroups_whenThereAreElectionDetailsAndNoTakeOverLimitOrIsZeroOrOneHundred_thenReturnElectionGroupsWithPercentPopulated() {
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
        when(details.getTakeoverLimit()).thenReturn(null);
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
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get
                (CorporateActionConverterConstants.MAX_OPTIONS * 2).getKey());
        assertEquals("Y", electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_OPTIONS * 2).getValue());

        when(details.getTakeoverLimit()).thenReturn(BigDecimal.ZERO);

        electionGroups = converter.createElectionGroups(context, electionDetailsDto);

        electionGroup = electionGroups.iterator().next();
        assertEquals(CorporateActionDecisionKey.QUANTITY.getCode(1), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.PERCENT.getCode(1), electionGroup.getOptions().get(1).getKey());
        assertEquals("100", electionGroup.getOptions().get(1).getValue());

        when(details.getTakeoverLimit()).thenReturn(CorporateActionConverterConstants.DECIMAL_ONE_HUNDRED);

        electionGroups = converter.createElectionGroups(context, electionDetailsDto);

        electionGroup = electionGroups.iterator().next();
        assertEquals(CorporateActionDecisionKey.QUANTITY.getCode(1), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.PERCENT.getCode(1), electionGroup.getOptions().get(1).getKey());
        assertEquals("100", electionGroup.getOptions().get(1).getValue());
    }
}