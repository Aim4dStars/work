package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionsDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.api.corporateaction.v1.service.converter.NonProRataPriorityOfferRequestConverterServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionNonProRataPriorityOfferType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NonProRataPriorityOfferRequestConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private NonProRataPriorityOfferRequestConverterServiceImpl nonProRataPriorityOfferRequestConverterService;

    @Mock
    private StaticIntegrationService staticCodeService;

    @Before
    public void setup() {
        Code noActionCode = mock(Code.class);

        when(noActionCode.getCodeId()).thenReturn("8");
        when(noActionCode.getUserId()).thenReturn(CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_VALUE.toLowerCase());

        when(staticCodeService.loadCodeByUserId(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn
                (noActionCode);
    }

    @Test
    public void testCreateElectionGroups_whenTakeUpElection_thenPopulateCorporateActionElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(CorporateActionNonProRataPriorityOfferType
                .TAKE_UP.getId(), BigDecimal.TEN, null);

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(accountDetailsDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto.getPositionId()).thenReturn("0");
        when(details.getOrderNumber()).thenReturn("0");

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(details.getOrderNumber()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups =
                nonProRataPriorityOfferRequestConverterService.createElectionGroups(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertEquals(electionGroup.getOrderNumber(), "0");
        assertFalse(electionGroup.getPositions().isEmpty());
        assertEquals("0", electionGroup.getPositions().get(0).getId());

        assertNotNull(electionGroup.getOptions());
        assertEquals(2, electionGroup.getOptions().size());

        assertEquals(CorporateActionDecisionKey.SUBSCRIBED_QUANTITY.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("10", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), electionGroup.getOptions().get(1).getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());
    }

    @Test
    public void testCreateElectionGroups_whenLapsedElection_thenPopulateCorporateActionElectionGroup() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(CorporateActionNonProRataPriorityOfferType
                .LAPSE.getId(), null, null);

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto));
        when(accountDetailsDto.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(accountDetailsDto.getPositionId()).thenReturn("0");
        when(details.getOrderNumber()).thenReturn("0");

        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(details.getOrderNumber()).thenReturn("0");
        when(context.getCorporateActionDetails()).thenReturn(details);

        Collection<CorporateActionElectionGroup> electionGroups =
                nonProRataPriorityOfferRequestConverterService.createElectionGroups(context, electionDetailsDto);

        assertFalse(electionGroups.isEmpty());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();

        assertEquals(electionGroup.getOrderNumber(), "0");
        assertFalse(electionGroup.getPositions().isEmpty());
        assertEquals("0", electionGroup.getPositions().get(0).getId());

        assertNotNull(electionGroup.getOptions());
        assertEquals(2, electionGroup.getOptions().size());

        assertEquals(CorporateActionDecisionKey.SUBSCRIBED_QUANTITY.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("", electionGroup.getOptions().get(0).getValue());
        assertEquals(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), electionGroup.getOptions().get(1).getKey());
        assertEquals("8", electionGroup.getOptions().get(1).getValue());

        // No avaloq code
        when(staticCodeService.loadCodeByUserId(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn(null);

        electionGroups = nonProRataPriorityOfferRequestConverterService.createElectionGroups(context, electionDetailsDto);

        electionGroup = electionGroups.iterator().next();

        assertEquals(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), electionGroup.getOptions().get(1).getKey());
        assertEquals("", electionGroup.getOptions().get(1).getValue());
    }
}
