package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionPortfolioModelDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.BuyBackRequestConverterServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BuyBackRequestConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private BuyBackRequestConverterServiceImpl buyBackRequestConverterServiceImpl;

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
                buyBackRequestConverterServiceImpl.createElectionGroups(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());

        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertEquals(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 3, electionGroup.getOptions().size());
        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(),
                electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 2).getKey());
        assertEquals("Y", electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 2).getValue());
    }

    @Test
    public void testCreateElectionGroupsForIm_whenElectionDetailsArePopulated_thenReturnAPopulatedElectionGroupObject() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        ImCorporateActionElectionDetailsDto electionDetailsDto = mock(ImCorporateActionElectionDetailsDto.class);
        ImCorporateActionPortfolioModelDto portfolioModelDto = mock(ImCorporateActionPortfolioModelDto.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(details.getOrderNumber()).thenReturn("0");
        when(account.getIpsId()).thenReturn("0");
        when(portfolioModelDto.getIpsId()).thenReturn("0");
        when(electionDetailsDto.getPortfolioModels()).thenReturn(Arrays.asList(portfolioModelDto));
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(context.getCorporateActionAccountList()).thenReturn(Arrays.asList(account));

        List<CorporateActionSelectedOptionDto> optionDtoList = new ArrayList<>();

        optionDtoList.add(createSelectedOptionDtoMock(1, BigDecimal.TEN, null));
        optionDtoList.add(createSelectedOptionDtoMock(CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID, BigDecimal.ONE,
                null));

        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        when(selectedOptionsDto.getOptions()).thenReturn(optionDtoList);
        when(selectedOptionsDto.getMinimumPriceId()).thenReturn(2);
        when(portfolioModelDto.getSelectedElections()).thenReturn(selectedOptionsDto);

        Collection<CorporateActionElectionGroup> electionGroups =
                buyBackRequestConverterServiceImpl.createElectionGroupsForIm(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());

        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();
        assertEquals("0", electionGroup.getOrderNumber());

        assertEquals(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 3, electionGroup.getOptions().size());

        assertEquals(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("1", electionGroup.getOptions().get(0).getValue());

        assertEquals(CorporateActionDecisionKey.OPTION_QUANTITY.getCode(1), electionGroup.getOptions().get(1).getKey());
        assertEquals("10", electionGroup.getOptions().get(1).getValue());

        assertEquals(CorporateActionDecisionKey.OPTION_QUANTITY.getCode(2), electionGroup.getOptions().get(2).getKey());
        assertEquals("", electionGroup.getOptions().get(2).getValue());

        assertEquals(CorporateActionDecisionKey.MINIMUM_PRICE_DECISION.getCode(),
                electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 1).getKey());
        assertEquals("2", electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 1).getValue());

        assertEquals(CorporateActionDecisionKey.BLOCK_ON_DECISION.getCode(),
                electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 2).getKey());
        assertEquals("Y", electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 2).getValue());

        optionDtoList.clear();
        optionDtoList.add(createSelectedOptionDtoMock(1, BigDecimal.TEN, null));
        optionDtoList.add(createSelectedOptionDtoMock(CorporateActionConverterConstants.OPTION_FINAL_TENDER_PRICE_ID, null, null));

        electionGroups = buyBackRequestConverterServiceImpl.createElectionGroupsForIm(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());

        electionGroup = electionGroups.iterator().next();
        assertEquals(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
    }

    @Test
    public void
    testCreateElectionGroupsForIm_whenElectionDetailsArePopulatedButWithoutFinalTenderPriceOrUnitsOrMinimumPrice_thenPopulateElectionGroupAppropriately() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        ImCorporateActionElectionDetailsDto electionDetailsDto = mock(ImCorporateActionElectionDetailsDto.class);
        ImCorporateActionPortfolioModelDto portfolioModelDto = mock(ImCorporateActionPortfolioModelDto.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(details.getOrderNumber()).thenReturn("0");
        when(account.getIpsId()).thenReturn("0");
        when(portfolioModelDto.getIpsId()).thenReturn("0");
        when(electionDetailsDto.getPortfolioModels()).thenReturn(Arrays.asList(portfolioModelDto));
        when(context.getCorporateActionDetails()).thenReturn(details);
        when(context.getCorporateActionAccountList()).thenReturn(Arrays.asList(account));

        List<CorporateActionSelectedOptionDto> optionDtoList = new ArrayList<>();

        optionDtoList.add(createSelectedOptionDtoMock(1, null, null));

        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        when(selectedOptionsDto.getOptions()).thenReturn(optionDtoList);
        when(selectedOptionsDto.getMinimumPriceId()).thenReturn(null);
        when(portfolioModelDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(optionDtoList.get(0));

        Collection<CorporateActionElectionGroup> electionGroups =
                buyBackRequestConverterServiceImpl.createElectionGroupsForIm(context, electionDetailsDto);

        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();
        assertEquals(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 3, electionGroup.getOptions().size());

        assertEquals(CorporateActionDecisionKey.FINAL_TENDER_QUANTITY.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());

        assertEquals(CorporateActionDecisionKey.OPTION_QUANTITY.getCode(1), electionGroup.getOptions().get(1).getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());

        assertEquals(CorporateActionDecisionKey.MINIMUM_PRICE_DECISION.getCode(),
                electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 1).getKey());
        assertEquals(Integer.toString(CorporateActionConverterConstants.NONE_MINIMUM_PRICE_ID),
                electionGroup.getOptions().get(CorporateActionConverterConstants.MAX_BUY_BACK_OPTIONS + 1).getValue());
    }
}