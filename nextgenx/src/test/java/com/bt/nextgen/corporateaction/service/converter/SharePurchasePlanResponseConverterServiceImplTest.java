package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterConstants;
import com.bt.nextgen.api.corporateaction.v1.service.converter.SharePurchasePlanResponseConverterServiceImpl;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElectionKey;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountKey;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipation;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SharePurchasePlanResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private SharePurchasePlanResponseConverterServiceImpl sharePurchasePlanResponseConverterServiceImpl;

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
    public void testToSubmittedAccountElectionsDto_whenThereAreAccountDecisions_thenPopulateCorporateActionAccountElectionsDtoAccordingly
            () {
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        List<CorporateActionOption> decisions = new ArrayList<>();

        decisions.add(createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), "1"));
        when(account.getDecisions()).thenReturn(decisions);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class)))
                .thenReturn(subscribedCode);

        CorporateActionAccountElectionsDto accountElectionsDto =
                sharePurchasePlanResponseConverterServiceImpl.toSubmittedAccountElectionsDto(null, account);

        assertNotNull(accountElectionsDto);
        assertEquals((Integer) 1, accountElectionsDto.getPrimaryAccountElection().getOptionId());
    }

    @Test
    public void
    testToSubmittedAccountElectionsDto_whenThereAreAccountDecisionsButNoStaticCodeOrUserId_thenPopulateCorporateActionAccountElectionsDtoAccordingly
            () {
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        List<CorporateActionOption> decisions = new ArrayList<>();

        decisions.add(createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), "1"));
        when(account.getDecisions()).thenReturn(decisions);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn(null);

        CorporateActionAccountElectionsDto accountElectionsDto = sharePurchasePlanResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(null, account);

        assertNotNull(accountElectionsDto);
        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID,
                accountElectionsDto.getPrimaryAccountElection().getOptionId());

        Code code = mock(Code.class);
        when(code.getUserId()).thenReturn(null);
        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn(code);

        accountElectionsDto = sharePurchasePlanResponseConverterServiceImpl.toSubmittedAccountElectionsDto(null, account);

        assertNotNull(accountElectionsDto);
        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID,
                accountElectionsDto.getPrimaryAccountElection().getOptionId());
    }

    @Test
    public void
    testToSubmittedAccountElectionsDto_whenThereAreAccountDecisionsAndItIsTakeNoAction_thenPopulateCorporateActionAccountElectionsDtoAsTakeNoAction() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        List<CorporateActionOption> decisions = new ArrayList<>();

        decisions.add(createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), "8"));
        when(account.getDecisions()).thenReturn(decisions);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn(noActionCode);

        CorporateActionAccountElectionsDto accountElectionsDto = sharePurchasePlanResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(null, account);

        assertNotNull(accountElectionsDto);
        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID,
                accountElectionsDto.getPrimaryAccountElection().getOptionId());
    }

    @Test
    public void
    testToSubmittedAccountElectionsDto_whenThereAreNoAccountDecisions_thenPopulateCorporateActionAccountElectionsDtoAsTakeNoAction() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        List<CorporateActionOption> decisions = new ArrayList<>();

        when(account.getDecisions()).thenReturn(decisions);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn(noActionCode);

        CorporateActionAccountElectionsDto accountElectionsDto = sharePurchasePlanResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(null, account);

        assertNotNull(accountElectionsDto);
        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID,
                accountElectionsDto.getPrimaryAccountElection().getOptionId());
    }

    @Test
    public void testToElectionOptionDtos_whenThereIsSubscribedAmount_thenReturnPopulatedCorporateActionOptionsDto() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionOption option = createOptionMock(CorporateActionOptionKey.SUBSCRIPTION_AMOUNT.getCode(1), "1000.0");
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), "1");

        when(details.getOptions()).thenReturn(Arrays.asList(option));
        when(details.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class)))
                .thenReturn(subscribedCode);

        List<CorporateActionOptionDto> optionDtos =
                sharePurchasePlanResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(2, optionDtos.size());

        assertEquals((Integer) 1, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("Apply for $1,000", optionDtos.get(0).getSummary());
        assertTrue(optionDtos.get(0).getIsDefault());
        assertFalse(optionDtos.get(0).getIsNoAction());

        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, optionDtos.get(1).getId());
        assertEquals("Option B", optionDtos.get(1).getTitle());
        assertEquals("Take no action", optionDtos.get(1).getSummary());
        assertFalse(optionDtos.get(1).getIsDefault());
        assertTrue(optionDtos.get(1).getIsNoAction());
    }

    @Test
    public void testToElectionOptionDtos_whenThereIsNoSubscribedAmount_thenReturnPopulatedCorporateActionOptionsDto() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionOption option = createOptionMock(CorporateActionOptionKey.SUBSCRIPTION_AMOUNT.getCode(1), (String) null);
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), (String) null);

        when(details.getOptions()).thenReturn(Arrays.asList(option));
        when(details.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn(noActionCode);

        List<CorporateActionOptionDto> optionDtos =
                sharePurchasePlanResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(1, optionDtos.size());

        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("Take no action", optionDtos.get(0).getSummary());
        assertTrue(optionDtos.get(0).getIsDefault());
        assertTrue(optionDtos.get(0).getIsNoAction());
    }

    @Test
    public void testToElectionOptionDtos_whenThereIsNoUserIdOrCode_thenChooseTakeNoActionAsDefault() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionOption option = createOptionMock(CorporateActionOptionKey.SUBSCRIPTION_AMOUNT.getCode(1), (String) null);
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), (String) null);

        when(details.getOptions()).thenReturn(Arrays.asList(option));
        when(details.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        Code code = mock(Code.class);
        when(code.getCodeId()).thenReturn("1");
        when(code.getUserId()).thenReturn(null);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn(code);

        List<CorporateActionOptionDto> optionDtos =
                sharePurchasePlanResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(1, optionDtos.size());

        assertEquals((Integer) CorporateActionConverterConstants.OPTION_TAKE_NO_ACTION_ID, optionDtos.get(0).getId());
        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("Take no action", optionDtos.get(0).getSummary());
        assertTrue(optionDtos.get(0).getIsDefault());
        assertTrue(optionDtos.get(0).getIsNoAction());

        decision = createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), "1");
        when(details.getDecisions()).thenReturn(Arrays.asList(decision));
        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class))).thenReturn(null);

        optionDtos = sharePurchasePlanResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertNotNull(optionDtos);
        assertEquals(1, optionDtos.size());
        assertTrue(optionDtos.get(0).getIsDefault());
        assertTrue(optionDtos.get(0).getIsNoAction());
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenNotSubmittedAccountStatus_thenDoNotModifyCash() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        CorporateActionOption option =
                createOptionMock(CorporateActionOptionKey.SUBSCRIPTION_AMOUNT.getCode(1), BigDecimal.valueOf(1000.0));

        when(details.getOptions()).thenReturn(Arrays.asList(option));
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.NOT_SUBMITTED);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = sharePurchasePlanResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedDetails_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);

        when(savedDetails.getSavedParticipation()).thenReturn(null);

        assertNull(sharePurchasePlanResponseConverterServiceImpl.toSavedAccountElectionsDto(null, null, savedDetails));
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsSavedAccountWithElection_thenReturnPopulatedElectionDto() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);
        CorporateActionSavedAccountElection savedAccountElection = mock(CorporateActionSavedAccountElection.class);
        CorporateActionSavedAccountElectionKey savedAccountElectionKey = mock(CorporateActionSavedAccountElectionKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccountElectionKey.getOptionId()).thenReturn(1);
        when(savedAccountElection.getKey()).thenReturn(savedAccountElectionKey);
        when(savedAccount.getAccountElections()).thenReturn(Arrays.asList(savedAccountElection));
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                sharePurchasePlanResponseConverterServiceImpl.toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNotNull(electionsDto);
        assertNotNull(electionsDto.getPrimaryAccountElection());
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsSavedAccountWithNoElections_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getAccountElections()).thenReturn(new ArrayList<CorporateActionSavedAccountElection>());
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                sharePurchasePlanResponseConverterServiceImpl.toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNull(electionsDto);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedAccount_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getAccountElections()).thenReturn(new ArrayList<CorporateActionSavedAccountElection>());
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                sharePurchasePlanResponseConverterServiceImpl.toSavedAccountElectionsDto(null, "1", savedDetails);

        assertNull(electionsDto);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenSubmittedAccountStatus_thenRevertCashBackToPreSubmission() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        CorporateActionOption option =
                createOptionMock(CorporateActionOptionKey.SUBSCRIPTION_AMOUNT.getCode(1), BigDecimal.valueOf(1000.0));
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), "1");

        when(details.getOptions()).thenReturn(Arrays.asList(option));
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(account.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class)))
                .thenReturn(subscribedCode);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = sharePurchasePlanResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);

        assertTrue(BigDecimal.valueOf(1010.0).compareTo(params.getCash()) == 0);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenSubmittedAccountStatusButNoSubscriptionAmount_thenDoNotModifyCash() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(), "1");

        when(details.getOptions()).thenReturn(new ArrayList<CorporateActionOption>());
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(account.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class)))
                .thenReturn(subscribedCode);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = sharePurchasePlanResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void testSetCorporateActionAccountDetailsDtoParams_whenSubmittedAccountStatusButNoDecisions_thenDoNotModifyCash() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(details.getOptions()).thenReturn(new ArrayList<CorporateActionOption>());
        when(account.getElectionStatus()).thenReturn(CorporateActionAccountParticipationStatus.SUBMITTED);
        when(account.getDecisions()).thenReturn(null);
        when(context.getCorporateActionDetails()).thenReturn(details);

        when(staticCodeService.loadCode(any(CodeCategoryInterface.class), anyString(), any(ServiceErrors.class)))
                .thenReturn(subscribedCode);

        CorporateActionAccountDetailsDtoParams params = new CorporateActionAccountDetailsDtoParams();
        params.setCash(BigDecimal.TEN);

        params = sharePurchasePlanResponseConverterServiceImpl.setCorporateActionAccountDetailsDtoParams(context, account, params);

        assertTrue(BigDecimal.TEN.compareTo(params.getCash()) == 0);
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenThereIsNoDefaultOption_thenSetAnErrorMessage() {
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        CorporateActionOptionDto optionDto = mock(CorporateActionOptionDto.class);
        when(optionDto.getIsDefault()).thenReturn(false);

        params.setOptions(Arrays.asList(optionDto));

        params = sharePurchasePlanResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(null, params);

        assertNotNull(params.getErrorMessage());
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenThereIsDefaultOption_thenDoNotSetAnErrorMessage() {
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        CorporateActionOptionDto optionDto = mock(CorporateActionOptionDto.class);
        when(optionDto.getIsDefault()).thenReturn(true);

        params.setOptions(Arrays.asList(optionDto));

        params = sharePurchasePlanResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(null, params);

        assertNull(params.getErrorMessage());
    }
}