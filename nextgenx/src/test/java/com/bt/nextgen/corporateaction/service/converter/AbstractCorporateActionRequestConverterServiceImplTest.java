package com.bt.nextgen.corporateaction.service.converter;


import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.AbstractCorporateActionRequestConverterServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCorporateActionRequestConverterServiceImplTest {

    private AbstractCorporateActionRequestConverterServiceImpl converter;

    @Before
    public void setup() {
        converter = Mockito.mock(AbstractCorporateActionRequestConverterServiceImpl.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void testCreateElectionGroupsForDg_whenNoAccounts_thenNoGroupsCreated() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        List<CorporateActionAccount> accounts = new ArrayList<>();

        ImCorporateActionPortfolioModelDto portfolioDetails = mock(ImCorporateActionPortfolioModelDto.class);
        ImCorporateActionElectionDetailsDto electionDetails = mock(ImCorporateActionElectionDetailsDto.class);
        when(electionDetails.getPortfolioModels()).thenReturn(Arrays.asList(portfolioDetails));
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(context.getCorporateActionAccountList()).thenReturn(accounts);

        Collection<CorporateActionElectionGroup> groups = converter.createElectionGroupsForDg(context, electionDetails);

        Assert.assertNotNull(groups);
        Assert.assertTrue(groups.isEmpty());
    }

    @Test
    public void testCreateElectionGroupsForDg_whenCorrectData_groupsCreated() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        when(details.getOrderNumber()).thenReturn("1234");
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccount ftpAccount = mock(CorporateActionAccount.class);
        when(ftpAccount.getPositionId()).thenReturn("100");
        when(ftpAccount.getIpsId()).thenReturn("12345");
        when(ftpAccount.getContainerType()).thenReturn(null);

        CorporateActionAccount shadowAccount = mock(CorporateActionAccount.class);
        when(shadowAccount.getPositionId()).thenReturn("200");
        when(shadowAccount.getIpsId()).thenReturn("12345");
        when(shadowAccount.getContainerType()).thenReturn(ContainerType.SHADOW_MANAGED_PORTFOLIO);

        CorporateActionAccount mpAccount = mock(CorporateActionAccount.class);
        when(mpAccount.getPositionId()).thenReturn("300");
        when(mpAccount.getIpsId()).thenReturn("12345");
        when(mpAccount.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        List<CorporateActionAccount> accounts = new ArrayList<>();
        accounts.add(ftpAccount);
        accounts.add(shadowAccount);
        accounts.add(mpAccount);

        CorporateActionSelectedOptionDto primaryOption = mock(CorporateActionSelectedOptionDto.class);
        when(primaryOption.getOptionId()).thenReturn(1);
        CorporateActionSelectedOptionDto primaryOption2 = mock(CorporateActionSelectedOptionDto.class);
        when(primaryOption2.getOptionId()).thenReturn(2);

        CorporateActionSelectedOptionsDto selectedOptions = mock(CorporateActionSelectedOptionsDto.class);
        when(selectedOptions.getPrimarySelectedOption()).thenReturn(primaryOption);
        CorporateActionSelectedOptionsDto selectedOptions2 = mock(CorporateActionSelectedOptionsDto.class);
        when(selectedOptions2.getPrimarySelectedOption()).thenReturn(primaryOption2);

        CorporateActionAccountDetailsDto accountDetails = mock(CorporateActionAccountDetailsDto.class);
        when(accountDetails.getSelectedElections()).thenReturn(selectedOptions);
        when(accountDetails.getPositionId()).thenReturn("300");

        ImCorporateActionPortfolioModelDto portfolioDetails = mock(ImCorporateActionPortfolioModelDto.class);
        when(portfolioDetails.getSelectedElections()).thenReturn(selectedOptions2);
        when(portfolioDetails.getIpsId()).thenReturn("12345");

        ImCorporateActionElectionDetailsDto electionDetails = mock(ImCorporateActionElectionDetailsDto.class);
        when(electionDetails.getAccounts()).thenReturn(Arrays.asList(accountDetails));
        when(electionDetails.getPortfolioModels()).thenReturn(Arrays.asList(portfolioDetails));

        when(context.getCorporateActionAccountList()).thenReturn(accounts);

        Collection<CorporateActionElectionGroup> groups = converter.createElectionGroupsForDg(context, electionDetails);

        Assert.assertNotNull(groups);
        Assert.assertEquals(2, groups.size());
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
        when(context.getCorporateActionDetails()).thenReturn(details);
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
        assertEquals("100", electionGroup.getOptions().get(1).getValue());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get
                (CorporateActionConverterConstants.MAX_OPTIONS * 2).getKey());
        assertEquals("Y", electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_OPTIONS * 2).getValue());
    }

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
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(), electionGroup.getOptions().get
                (CorporateActionConverterConstants.MAX_OPTIONS * 2).getKey());
        assertEquals("Y", electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_OPTIONS * 2).getValue());
    }
}