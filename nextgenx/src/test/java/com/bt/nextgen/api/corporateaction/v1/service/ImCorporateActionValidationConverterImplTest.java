package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionValidationStatus;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionValidationError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImCorporateActionValidationConverterImplTest {
    @InjectMocks
    private ImCorporateActionValidationConverterImpl validationConverterImpl;

    @Mock
    private CmsService cmsService;

    @Mock
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Mock
    private CorporateActionResponseConverterService responseConverterService;

    @Mock
    private CorporateActionPosition corporateActionPosition1;

    @Mock
    private CorporateActionPosition corporateActionPosition2;

    @Mock
    private CorporateActionPosition corporateActionPosition3;

    @Mock
    private ImCorporateActionElectionDetailsDto electionDetailsDto;

    @Mock
    private CorporateActionAccount corporateActionAccount1;

    @Mock
    private CorporateActionAccount corporateActionAccount2;

    @Mock
    private CorporateActionAccount shadowPortfolioAccount;

    @Mock
    private CorporateActionElectionGroup electionGroup;

    @Mock
    private CorporateActionOptionDto corporateActionOptionDto1;

    @Mock
    private CorporateActionOptionDto corporateActionOptionDto2;

    @Before
    public void setup() {
        when(corporateActionPosition1.getId()).thenReturn("0");
        when(corporateActionPosition1.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        when(corporateActionPosition2.getId()).thenReturn("1");
        when(corporateActionPosition2.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        when(corporateActionPosition3.getId()).thenReturn("2");
        when(corporateActionPosition3.getContainerType()).thenReturn(ContainerType.DIRECT);

        when(electionDetailsDto.getKey()).thenReturn(new CorporateActionDtoKey("0"));

        when(corporateActionAccount1.getPositionId()).thenReturn("0");
        when(corporateActionAccount1.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);
        when(corporateActionAccount2.getPositionId()).thenReturn("1");
        when(corporateActionAccount2.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        when(shadowPortfolioAccount.getPositionId()).thenReturn("100");
        when(shadowPortfolioAccount.getContainerType()).thenReturn(ContainerType.SHADOW_MANAGED_PORTFOLIO);

        when(electionGroup.getValidationErrors()).thenReturn(null);
        when(electionGroup.getIpsId()).thenReturn("0");

        when(corporateActionOptionDto1.getTitle()).thenReturn("Option A");
        when(corporateActionOptionDto1.getSummary()).thenReturn("Summary 1");

        when(corporateActionOptionDto2.getTitle()).thenReturn("Option B");
        when(corporateActionOptionDto2.getSummary()).thenReturn("Summary 2");

        when(cmsService.getContent(anyString())).thenReturn("Cms text");
        when(cmsService.getDynamicContent(anyString(), any(String[].class))).thenReturn("Dynamic cms text");

        when(corporateActionConverterFactory.getResponseConverterService(any(CorporateActionDetails.class)))
                .thenReturn(responseConverterService);
    }

    @Test
    public void testToElectionResultDtoList_WhenThereIsNoValidationErrors_thenReturnAnEmptyElectionResultDtoLists() {
        List<ImCorporateActionElectionResultDto> resultDtos =
                validationConverterImpl.toElectionResultDtoList(mock(CorporateActionElectionGroup.class), electionDetailsDto, null, null);

        assertNotNull(resultDtos);
        assertEquals(0, resultDtos.size());
    }

    @Test
    public void testToElectionResultDtoList_WhenThereIsNoValidationErrors_thenListOfDtosShouldBeReturned() {
        when(electionGroup.getPositions())
                .thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2, corporateActionPosition3));

        List<ImCorporateActionElectionResultDto> resultDtos = validationConverterImpl.toElectionResultDtoList(electionGroup,
                electionDetailsDto, Arrays.asList(shadowPortfolioAccount, corporateActionAccount1, corporateActionAccount2),
                new ArrayList<CorporateActionValidationError>());

        assertNotNull(resultDtos);
        assertEquals(1, resultDtos.size());
        assertEquals("0", resultDtos.get(0).getPortfolioModelId());
        assertEquals(CorporateActionValidationStatus.SUCCESS, resultDtos.get(0).getStatus());
        assertNull(resultDtos.get(0).getErrorMessages());
    }

    @Test
    public void
    testToElectionResultDtoList_whenThereIsValidationErrorButNotACompleteFailure_thenListOfDtosShouldBeReturnedWithWarningStatus() {
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2));

        CorporateActionValidationError corporateActionValidationError = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError.getPositionId()).thenReturn("0");
        when(corporateActionValidationError.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        List<ImCorporateActionElectionResultDto> resultDtos = validationConverterImpl.toElectionResultDtoList(electionGroup,
                electionDetailsDto, Arrays.asList(shadowPortfolioAccount, corporateActionAccount1, corporateActionAccount2),
                Arrays.asList(corporateActionValidationError));

        assertNotNull(resultDtos);
        assertEquals(1, resultDtos.size());
        assertEquals("0", resultDtos.get(0).getPortfolioModelId());
        assertEquals(CorporateActionValidationStatus.WARNING, resultDtos.get(0).getStatus());
        assertNotNull(resultDtos.get(0).getErrorMessages());
    }

    @Test
    public void testToElectionResultDtoList_whenThereIsValidationErrorOnNonManagedPortfolio_thenListOfDtosShouldBeReturnedWithoutError() {
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2));

        CorporateActionValidationError corporateActionValidationError = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError.getPositionId()).thenReturn("100");
        when(corporateActionValidationError.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        List<ImCorporateActionElectionResultDto> resultDtos = validationConverterImpl.toElectionResultDtoList(electionGroup,
                electionDetailsDto, Arrays.asList(shadowPortfolioAccount, corporateActionAccount1, corporateActionAccount2),
                Arrays.asList(corporateActionValidationError));

        assertNotNull(resultDtos);
        assertEquals(1, resultDtos.size());
        assertEquals("0", resultDtos.get(0).getPortfolioModelId());
        assertEquals(CorporateActionValidationStatus.SUCCESS, resultDtos.get(0).getStatus());
        assertNull(resultDtos.get(0).getErrorMessages());
    }

    @Test
    public void testToElectionResultDtoList_whenThereIsValidationErrorAndACompleteFailure_thenListOfDtosShouldBeReturnedWithErrorStatus() {
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2));

        CorporateActionValidationError corporateActionValidationError1 = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError1.getPositionId()).thenReturn("0");
        when(corporateActionValidationError1.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        CorporateActionValidationError corporateActionValidationError2 = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError2.getPositionId()).thenReturn("1");
        when(corporateActionValidationError2.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        List<ImCorporateActionElectionResultDto> resultDtos = validationConverterImpl.toElectionResultDtoList(electionGroup,
                electionDetailsDto, Arrays.asList(shadowPortfolioAccount, corporateActionAccount1, corporateActionAccount2),
                Arrays.asList(corporateActionValidationError1, corporateActionValidationError2));

        assertNotNull(resultDtos);
        assertEquals(1, resultDtos.size());
        assertEquals("0", resultDtos.get(0).getPortfolioModelId());
        assertEquals(CorporateActionValidationStatus.ERROR, resultDtos.get(0).getStatus());
        assertNotNull(resultDtos.get(0).getErrorMessages());
        assertEquals(1, resultDtos.get(0).getErrorMessages().size());
        assertEquals("Dynamic cms text", resultDtos.get(0).getErrorMessages().get(0));
    }

    @Test
    public void
    testToElectionResultDtoList_whenThereIsValidationErrorAndACompleteFailureAndTwoTypeOfErrors_thenListOfDtosShouldBeReturnedWithErrorStatusAndThereAreTwoErrorMessages() {
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2));

        CorporateActionValidationError corporateActionValidationError1 = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError1.getPositionId()).thenReturn("0");
        when(corporateActionValidationError1.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        CorporateActionValidationError corporateActionValidationError2 = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError2.getPositionId()).thenReturn("1");
        when(corporateActionValidationError2.getErrorCode()).thenReturn("pay_bal_chk_fail");

        List<ImCorporateActionElectionResultDto> resultDtos = validationConverterImpl.toElectionResultDtoList(electionGroup,
                electionDetailsDto, Arrays.asList(shadowPortfolioAccount, corporateActionAccount1, corporateActionAccount2),
                Arrays.asList(corporateActionValidationError1, corporateActionValidationError2));

        assertNotNull(resultDtos);
        assertEquals(1, resultDtos.size());
        assertEquals("0", resultDtos.get(0).getPortfolioModelId());
        assertEquals(CorporateActionValidationStatus.ERROR, resultDtos.get(0).getStatus());
        assertNotNull(resultDtos.get(0).getErrorMessages());
        assertEquals(2, resultDtos.get(0).getErrorMessages().size());
    }

    @Test
    public void testHasManagedPortfolioValidationError_whenThereIsAnErrorOfTypeManagedPortfolio_thenReturnTrue() {
        when(electionGroup.getPositions())
                .thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2, corporateActionPosition3));

        CorporateActionValidationError corporateActionValidationError = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError.getPositionId()).thenReturn("0");
        when(corporateActionValidationError.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        boolean hasValidationError =
                validationConverterImpl.hasManagedPortfolioValidationError(electionGroup, Arrays.asList(corporateActionValidationError));

        assertTrue(hasValidationError);
    }

    @Test
    public void testHasManagedPortfolioValidationError_whenThereIsAnErrorButNotOfTypeManagedPortfolio_thenReturnFalse() {
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2));

        CorporateActionValidationError corporateActionValidationError = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError.getPositionId()).thenReturn("100");
        when(corporateActionValidationError.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        boolean hasValidationError =
                validationConverterImpl.hasManagedPortfolioValidationError(electionGroup, Arrays.asList(corporateActionValidationError));

        assertFalse(hasValidationError);
    }

    @Test
    public void testIsCompleteSubmissionFailure_whenAllPositionsSubmissionFailed_thenReturnTrue() {
        when(electionGroup.getPositions())
                .thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2, corporateActionPosition3));

        CorporateActionValidationError corporateActionValidationError1 = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError1.getPositionId()).thenReturn("0");
        when(corporateActionValidationError1.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        CorporateActionValidationError corporateActionValidationError2 = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError2.getPositionId()).thenReturn("1");
        when(corporateActionValidationError2.getErrorCode()).thenReturn("pay_bal_chk_fail");

        boolean isCompleteSubmissionFailure = validationConverterImpl.isCompleteSubmissionFailure(electionGroup,
                Arrays.asList(corporateActionValidationError1, corporateActionValidationError2));

        assertTrue(isCompleteSubmissionFailure);
    }

    @Test
    public void testIsCompleteSubmissionFailure_whenOnlyOnePositionSubmissionFailed_thenReturnFalse() {
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2));

        CorporateActionValidationError corporateActionValidationError1 = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError1.getPositionId()).thenReturn("0");
        when(corporateActionValidationError1.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        boolean isCompleteSubmissionFailure = validationConverterImpl.isCompleteSubmissionFailure(electionGroup,
                Arrays.asList(corporateActionValidationError1));

        assertFalse(isCompleteSubmissionFailure);
    }

    @Test
    public void testValidateOptions_whenThereOptionsButNoAvaloqOptions_thenReturnFalse() {
        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto1));

        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class))).thenReturn(null);

        // Null test
        boolean isValidOptions = validationConverterImpl.validateOptions(electionDetailsDto, mock(CorporateActionContext.class), null);

        assertFalse(isValidOptions);

        // Empty test
        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CorporateActionOptionDto>(0));

        isValidOptions = validationConverterImpl.validateOptions(electionDetailsDto, mock(CorporateActionContext.class), null);

        assertFalse(isValidOptions);
    }

    @Test
    public void testValidateOptions_whenThereAreOptionsButDifferentSizeToAvaloqOptions_thenReturnFalse() {
        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto1));

        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(corporateActionOptionDto1, corporateActionOptionDto2));

        boolean isValidOptions = validationConverterImpl.validateOptions(electionDetailsDto, mock(CorporateActionContext.class), null);

        assertFalse(isValidOptions);
    }

    @Test
    public void testValidateOptions_whenOptionAvailableDoesNotMatchAvaloqOption_thenReturnFalse() {
        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto1));

        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(corporateActionOptionDto2));

        boolean isValidOptions = validationConverterImpl.validateOptions(electionDetailsDto, mock(CorporateActionContext.class), null);

        assertFalse(isValidOptions);
    }

    @Test
    public void testValidateOptions_whenOptionAvailableMatchesAvaloqOption_thenReturnTrue() {
        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto1));

        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(corporateActionOptionDto1));

        boolean isValidOptions = validationConverterImpl.validateOptions(electionDetailsDto, mock(CorporateActionContext.class), null);

        assertTrue(isValidOptions);
    }

    @Test
    public void testToElectionResultDtoListForDg_WhenThereIsNoValidationErrors_thenListSuccessResultDtosForMPsOnly() {
        CorporateActionElectionGroup electionGroup = mockDgElectionGroup();
        List<CorporateActionAccount> accounts = mockDgAccountsList();
        ImCorporateActionElectionDetailsDto electionDetailsDto = mockDgElectionDetailsDto();

        List<CorporateActionElectionResultDto> resultDtos = validationConverterImpl.toElectionResultDtoListForDg(electionGroup,
                electionDetailsDto, accounts, null);

        assertNotNull(resultDtos);
        assertEquals(1, resultDtos.size());
        assertEquals("1200", resultDtos.get(0).getAccountId());
        assertEquals(CorporateActionValidationStatus.SUCCESS, resultDtos.get(0).getStatus());
    }

    @Test
    public void testToElectionResultDtoListForDg_WhenThereAreValidationErrors_thenListErrorResultDtosForMPsOnly() {
        CorporateActionElectionGroup electionGroup = mockDgElectionGroup();
        List<CorporateActionAccount> accounts = mockDgAccountsList();
        ImCorporateActionElectionDetailsDto electionDetailsDto = mockDgElectionDetailsDto();

        CorporateActionValidationError error = mock(CorporateActionValidationError.class);
        when(error.getPositionId()).thenReturn("300");
        when(error.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        CorporateActionValidationError shadowError = mock(CorporateActionValidationError.class);
        when(shadowError.getPositionId()).thenReturn("200");
        when(shadowError.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        List<CorporateActionElectionResultDto> resultDtos = validationConverterImpl.toElectionResultDtoListForDg(electionGroup,
                electionDetailsDto, accounts, Arrays.asList(error, shadowError));

        assertNotNull(resultDtos);
        assertEquals(1, resultDtos.size());
        assertEquals("1200", resultDtos.get(0).getAccountId());
        assertEquals(CorporateActionValidationStatus.ERROR, resultDtos.get(0).getStatus());
    }

    @Test
    public void testHandleServiceErrors() {
        ElectionResults electionResults = new ElectionResults();

        validationConverterImpl.handleServiceErrors(null, null, electionResults);

        electionResults.setCompleteFailure(false);
        electionResults.setSystemFailure(false);
        assertTrue(electionResults.isSubmitErrors());
        assertFalse(electionResults.isCompleteFailure());
        assertFalse(electionResults.isSystemFailure());
        assertEquals(0, electionResults.getSuccessCount());
    }

    @Test
    public void testHandleValidationErrors() {
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1, corporateActionPosition2));

        CorporateActionValidationError corporateActionValidationError1 = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError1.getPositionId()).thenReturn("0");
        when(corporateActionValidationError1.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        CorporateActionValidationError corporateActionValidationError2 = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError2.getPositionId()).thenReturn("1");
        when(corporateActionValidationError2.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        List<CorporateActionAccount> accounts = mockDgAccountsList();

        ElectionResults electionResults = new ElectionResults();
        validationConverterImpl.handleValidationErrors(electionGroup, electionDetailsDto, accounts,
                Arrays.asList(corporateActionValidationError1), electionResults);

        electionResults.incrementSuccessCount();
        assertTrue(electionResults.isSubmitErrors());
        assertFalse(electionResults.isCompleteFailure());
        assertEquals(1, electionResults.getSuccessCount());

        electionResults = new ElectionResults();
        validationConverterImpl.handleValidationErrors(electionGroup, electionDetailsDto, accounts,
                Arrays.asList(corporateActionValidationError1, corporateActionValidationError2), electionResults);

        assertTrue(electionResults.isSubmitErrors());
        assertTrue(electionResults.isCompleteFailure());
        assertEquals(0, electionResults.getSuccessCount());
        assertFalse(electionResults.isSystemFailure());
    }

    @Test
    public void testCreateElectionResults() {
        assertNotNull(validationConverterImpl.createElectionResults());
    }

    @Test
    public void testCreateServiceErrors() {
        assertNotNull(validationConverterImpl.createServiceErrors());
    }

    @Test
    public void testGetCmsText() {
        assertEquals("Cms text", validationConverterImpl.getCmsText("XXX", null));
        assertEquals("Dynamic cms text", validationConverterImpl.getCmsText("XXX", "X"));
    }

    private CorporateActionElectionGroup mockDgElectionGroup() {
        CorporateActionPosition ftpPosition = Mockito.mock(CorporateActionPosition.class);
        Mockito.when(ftpPosition.getId()).thenReturn("100");
        Mockito.when(ftpPosition.getContainerType()).thenReturn(null);

        CorporateActionPosition shadowPosition = Mockito.mock(CorporateActionPosition.class);
        Mockito.when(shadowPosition.getId()).thenReturn("200");
        Mockito.when(shadowPosition.getContainerType()).thenReturn(ContainerType.SHADOW_MANAGED_PORTFOLIO);

        CorporateActionPosition mpPosition = Mockito.mock(CorporateActionPosition.class);
        Mockito.when(mpPosition.getId()).thenReturn("300");
        Mockito.when(mpPosition.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        List<CorporateActionPosition> positions = new ArrayList<>();
        positions.add(ftpPosition);
        positions.add(shadowPosition);
        positions.add(mpPosition);

        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        when(electionGroup.getPositions()).thenReturn(positions);

        return electionGroup;
    }

    private List<CorporateActionAccount> mockDgAccountsList() {
        CorporateActionAccount ftpAccount = mock(CorporateActionAccount.class);
        when(ftpAccount.getPositionId()).thenReturn("100");
        when(ftpAccount.getContainerType()).thenReturn(null);

        CorporateActionAccount shadowAccount = mock(CorporateActionAccount.class);
        when(shadowAccount.getPositionId()).thenReturn("200");
        when(shadowAccount.getContainerType()).thenReturn(ContainerType.SHADOW_MANAGED_PORTFOLIO);

        CorporateActionAccount mpAccount = mock(CorporateActionAccount.class);
        when(mpAccount.getPositionId()).thenReturn("300");
        when(mpAccount.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        List<CorporateActionAccount> accounts = new ArrayList<>();
        accounts.add(ftpAccount);
        accounts.add(shadowAccount);
        accounts.add(mpAccount);

        return accounts;
    }

    private ImCorporateActionElectionDetailsDto mockDgElectionDetailsDto() {
        CorporateActionAccountDetailsDto accountDetails = mock(CorporateActionAccountDetailsDto.class);
        when(accountDetails.getAccountId()).thenReturn("1200");
        when(accountDetails.getPositionId()).thenReturn("300");

        ImCorporateActionElectionDetailsDto electionDetails = mock(ImCorporateActionElectionDetailsDto.class);
        when(electionDetails.getAccounts()).thenReturn(Collections.singletonList(accountDetails));

        return electionDetails;
    }
}
