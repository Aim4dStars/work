package com.bt.nextgen.corporateaction.service.converter;

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
import com.bt.nextgen.api.corporateaction.v1.service.converter.SharePurchasePlanRequestConverterServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SharePurchasePlanRequestConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private SharePurchasePlanRequestConverterServiceImpl sharePurchasePlanRequestConverterServiceImpl;

    @Mock
    private StaticIntegrationService staticCodeService;

    @Mock
    private Code noActionCode;

    @Mock
    private Code subscribedCode;

    @Before
    public void setup() {
        when(noActionCode.getCodeId()).thenReturn("8");
        when(noActionCode.getUserId()).thenReturn(CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_VALUE.toLowerCase());

        when(subscribedCode.getCodeId()).thenReturn("1");
        when(subscribedCode.getUserId()).thenReturn(CorporateActionConverterConstants.OPTION_PREFIX + "1");
    }

    @Test
    public void testCreateElectionGroups_whenThereAreElectionDetails_thenReturnPopulatedElectionGroups() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto1 = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(1, null, null);

        when(accountDetailsDto1.getPositionId()).thenReturn("0");
        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(accountDetailsDto1.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto1));
        when(context.getCorporateActionDetails()).thenReturn(details);

        when(staticCodeService.loadCodeByUserId(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class)))
                .thenReturn(subscribedCode);
        Collection<CorporateActionElectionGroup> electionGroups =
                sharePurchasePlanRequestConverterServiceImpl.createElectionGroups(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();
        assertNotNull(electionGroup.getOptions());

        assertEquals(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("1", electionGroup.getOptions().get(0).getValue());
    }

    @Test
    public void testCreateElectionGroups_whenThereAreElectionDetailsButTakeNoAction_thenReturnPopulatedElectionGroups() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto1 = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = createSelectedOptionDtoMock(CorporateActionConverterConstants
                .OPTION_TAKE_NO_ACTION_ID, null, null);

        when(accountDetailsDto1.getPositionId()).thenReturn("0");
        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(accountDetailsDto1.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto1));
        when(context.getCorporateActionDetails()).thenReturn(details);

        when(staticCodeService.loadCodeByUserId(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class)))
                .thenReturn(noActionCode);
        Collection<CorporateActionElectionGroup> electionGroups =
                sharePurchasePlanRequestConverterServiceImpl.createElectionGroups(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();
        assertNotNull(electionGroup.getOptions());

        assertEquals(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), electionGroup.getOptions().get(0).getKey());
        assertEquals("8", electionGroup.getOptions().get(0).getValue());
    }

    @Test
    public void testCreateElectionGroups_whenThereAreElectionDetailsButNoStaticCode_thenReturnElectionGroupsWithNoOptions() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionElectionDetailsDto electionDetailsDto = mock(CorporateActionElectionDetailsDto.class);
        CorporateActionAccountDetailsDto accountDetailsDto1 = mock(CorporateActionAccountDetailsDto.class);
        CorporateActionSelectedOptionsDto selectedOptionsDto = mock(CorporateActionSelectedOptionsDto.class);
        CorporateActionSelectedOptionDto selectedOptionDto = mock(CorporateActionSelectedOptionDto.class);

        when(accountDetailsDto1.getPositionId()).thenReturn("0");
        when(selectedOptionDto.getOptionId()).thenReturn(1);
        when(selectedOptionsDto.getPrimarySelectedOption()).thenReturn(selectedOptionDto);
        when(accountDetailsDto1.getSelectedElections()).thenReturn(selectedOptionsDto);
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(accountDetailsDto1));
        when(context.getCorporateActionDetails()).thenReturn(details);

        when(staticCodeService.loadCodeByUserId(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn(null);
        Collection<CorporateActionElectionGroup> electionGroups =
                sharePurchasePlanRequestConverterServiceImpl.createElectionGroups(context, electionDetailsDto);

        assertNotNull(electionGroups);
        assertEquals(1, electionGroups.size());
        CorporateActionElectionGroup electionGroup = electionGroups.iterator().next();
        assertTrue(electionGroup.getOptions().isEmpty());
    }
}