package com.bt.nextgen.corporateaction.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.model.ImCorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionCommonService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionPersistenceDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.ElectionResults;
import com.bt.nextgen.api.corporateaction.v1.service.ImCorporateActionElectionDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.ImCorporateActionValidationConverter;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionRequestConverterService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetailsResponse;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionValidationError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImCorporateActionElectionDtoServiceImplTest {
    @InjectMocks
    private ImCorporateActionElectionDtoServiceImpl imCorporateActionElectionDtoServiceImpl;

    @Mock
    private CorporateActionIntegrationService corporateActionService;

    @Mock
    private CorporateActionPersistenceDtoServiceImpl corporateActionPersistenceDtoService;

    @Mock
    private CorporateActionElectionIntegrationService corporateActionElectionService;

    @Mock
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Mock
    private ImCorporateActionValidationConverter corporateActionValidationConverter;

    @Mock
    private CorporateActionCommonService corporateActionCommonService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ImCorporateActionElectionDetailsDto electionDetailsDto;

    @Mock
    private CorporateActionRequestConverterService requestConverterService;

    @Mock
    private CorporateActionDetailsResponse corporateActionDetailsResponse;

    @Mock
    private CorporateActionDetails corporateActionDetails;

    @Before
    public void setup() throws Exception {
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.PUBLIC_OFFER);

        when(corporateActionDetailsResponse.getCorporateActionDetailsList()).thenReturn(Arrays.asList(corporateActionDetails));

        when(corporateActionService.loadCorporateActionDetails(anyString(), any(ServiceErrors.class))).thenReturn(
                corporateActionDetailsResponse);

        when(corporateActionValidationConverter.getCmsText(anyString())).thenReturn("Error text");

        when(userProfileService.getPositionId()).thenReturn("0");
        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

        when(electionDetailsDto.getKey()).thenReturn(new CorporateActionDtoKey("0"));
    }

    @Test
    public void testSubmit_whenValidateOptionsChanged_thenReturnOptionsChangedStatus() {
        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.FALSE);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.OPTIONS_CHANGED, dto.getStatus());
    }

    @Test
    public void testSubmit_whenValidateOptionsHasNotChangedAndThereAreNoSubmissionErrors_thenReturnSuccessStatus() {
        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        when(electionGroup.getValidationErrors()).thenReturn(null);

        when(corporateActionValidationConverter.createElectionResults()).thenReturn(new ElectionResults());
        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(new ServiceErrorsImpl());

        when(requestConverterService
                .createElectionGroupsForIm(any(CorporateActionContext.class), any(ImCorporateActionElectionDetailsDto.class)))
                .thenReturn(Arrays.asList(electionGroup));

        when(corporateActionValidationConverter
                .validateOptions(any(CorporateActionElectionDetailsBaseDto.class), any(CorporateActionContext.class),
                        any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(corporateActionElectionService.submitElectionGroupForIm(any(CorporateActionElectionGroup.class), any(ServiceErrors.class)))
                .thenReturn(electionGroup);

        when(corporateActionConverterFactory.getRequestConverterService(
                any(CorporateActionDetails.class))).thenReturn(requestConverterService);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, dto.getStatus());
        assertTrue(dto.getTotalCount() == 1);
        assertTrue(dto.getSuccessCount() == 1);
    }

    @Test
    public void testSubmit_whenValidateOptionsHasNotChangedAndThereIsSubmissionError_thenReturnErrorStatus() {
        CorporateActionValidationError validationError = mock(CorporateActionValidationError.class);
        when(validationError.getPositionId()).thenReturn("0");
        when(validationError.getErrorCode()).thenReturn("ERR");
        when(validationError.getErrorMessage()).thenReturn("Error message");

        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        when(electionGroup.getValidationErrors()).thenReturn(Arrays.asList(validationError));

        // Not really a great test
        ElectionResults electionResults = mock(ElectionResults.class);
        when(electionResults.isSubmitErrors()).thenReturn(true);
        when(electionResults.isCompleteFailure()).thenReturn(true);
        when(electionResults.getPortfolioModelCount()).thenReturn(1);
        when(corporateActionValidationConverter.createElectionResults()).thenReturn(electionResults);

        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(new ServiceErrorsImpl());

        when(requestConverterService.createElectionGroupsForIm(any(CorporateActionContext.class),
                any(ImCorporateActionElectionDetailsDto.class))).thenReturn(Arrays.asList(electionGroup));

        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(corporateActionValidationConverter.hasManagedPortfolioValidationError(any(CorporateActionElectionGroup.class), anyList()))
                .thenReturn(Boolean.TRUE);

        when(corporateActionElectionService.submitElectionGroupForIm(any(CorporateActionElectionGroup.class), any(ServiceErrors.class)))
                .thenReturn(electionGroup);

        when(corporateActionConverterFactory.getRequestConverterService(any(CorporateActionDetails.class)))
                .thenReturn(requestConverterService);

        when(corporateActionValidationConverter.isCompleteSubmissionFailure(any(CorporateActionElectionGroup.class), anyList()))
                .thenReturn(Boolean.TRUE);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.ERROR, dto.getStatus());
        assertTrue(dto.getTotalCount() == 1);
        assertTrue(dto.getSuccessCount() == 0);
    }

    @Test
    public void testSubmit_whenValidateOptionsHasNotChangedAndThereIsOnlyOneSubmissionError_thenReturnWarningStatus() {
        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        when(electionGroup.getValidationErrors()).thenReturn(Arrays.asList(mock(CorporateActionValidationError.class)));
        CorporateActionElectionGroup electionGroup2 = mock(CorporateActionElectionGroup.class);
        when(electionGroup2.getValidationErrors()).thenReturn(null);

        ElectionResults electionResults = mock(ElectionResults.class);
        when(electionResults.isSubmitErrors()).thenReturn(true);
        when(electionResults.isCompleteFailure()).thenReturn(false);
        when(electionResults.getPortfolioModelCount()).thenReturn(2);
        when(electionResults.getSuccessCount()).thenReturn(1);
        when(corporateActionValidationConverter.createElectionResults()).thenReturn(electionResults);

        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(new ServiceErrorsImpl());

        when(requestConverterService.createElectionGroupsForIm(any(CorporateActionContext.class),
                any(ImCorporateActionElectionDetailsDto.class))).thenReturn(Arrays.asList(electionGroup, electionGroup2));

        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(corporateActionValidationConverter.hasManagedPortfolioValidationError(any(CorporateActionElectionGroup.class), anyList()))
                .thenReturn(Boolean.TRUE);

        when(corporateActionElectionService.submitElectionGroupForIm(any(CorporateActionElectionGroup.class), any(ServiceErrors.class)))
                .thenReturn(electionGroup).thenReturn(electionGroup2);

        when(corporateActionConverterFactory.getRequestConverterService(any(CorporateActionDetails.class)))
                .thenReturn(requestConverterService);

        when(corporateActionValidationConverter.isCompleteSubmissionFailure(any(CorporateActionElectionGroup.class), anyList()))
                .thenReturn(Boolean.FALSE);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.WARNING, dto.getStatus());
        assertTrue(dto.getTotalCount() == 2);
        assertTrue(dto.getSuccessCount() == 1);
    }

    @Test
    public void testSubmit_whenValidateOptionsHasNotChangedAndThereIsAServiceError_thenReturnErrorStatus() {
        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);

        ElectionResults electionResults = mock(ElectionResults.class);
        when(electionResults.isSubmitErrors()).thenReturn(true);
        when(electionResults.isCompleteFailure()).thenReturn(true);
        when(electionResults.isSystemFailure()).thenReturn(true);
        when(corporateActionValidationConverter.createElectionResults()).thenReturn(electionResults);

        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        when(serviceErrors.hasErrors()).thenReturn(true);
        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(serviceErrors);

        when(requestConverterService.createElectionGroupsForIm(any(CorporateActionContext.class),
                any(ImCorporateActionElectionDetailsDto.class))).thenReturn(Arrays.asList(electionGroup));

        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(corporateActionElectionService.submitElectionGroupForIm(any(CorporateActionElectionGroup.class), any(ServiceErrors.class)))
                .thenReturn(electionGroup);

        when(corporateActionConverterFactory.getRequestConverterService(any(CorporateActionDetails.class)))
                .thenReturn(requestConverterService);

        when(corporateActionValidationConverter.isCompleteSubmissionFailure(any(CorporateActionElectionGroup.class), anyList()))
                .thenReturn(Boolean.FALSE);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.ERROR, dto.getStatus());
    }

    @Test
    public void testSubmit_whenValidateOptionsHasNotChangedAndThereAreNoDealerGroupSubmissionErrors_thenReturnSuccessStatus() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        CorporateActionElectionGroup electionGroup = mockDgElectionGroup();
        List<CorporateActionAccount> accounts = mockDgAccountsList();

        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(new ServiceErrorsImpl());

        when(corporateActionService.loadCorporateActionAccountsDetailsForIm(anyString(), anyString(), any(ServiceErrors.class))).thenReturn(
                accounts);

        when(requestConverterService.createElectionGroupsForDg(any(CorporateActionContext.class),
                any(ImCorporateActionElectionDetailsDto.class))).thenReturn(Arrays.asList(electionGroup));

        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class),
                any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(corporateActionElectionService.submitElectionGroupForIm(any(CorporateActionElectionGroup.class),
                any(ServiceErrors.class))).thenReturn(electionGroup);

        when(corporateActionConverterFactory.getRequestConverterService(any(CorporateActionDetails.class)))
                .thenReturn(requestConverterService);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, dto.getStatus());
        assertTrue(dto.getTotalCount() == 1);
        assertTrue(dto.getSuccessCount() == 1);
    }

    @Test
    public void testDgSubmit_whenValidateOptionsChanged_thenReturnOptionsChangedStatus() {
        when(userProfileService.isDealerGroup()).thenReturn(true);

        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.FALSE);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.OPTIONS_CHANGED, dto.getStatus());
    }

    @Test
    public void testSubmit_whenValidateOptionsHasNotChangedAndThereAreNoPortfolioManagerSubmissionErrors_thenReturnSuccessStatus() {
        when(userProfileService.isPortfolioManager()).thenReturn(true);

        CorporateActionElectionGroup electionGroup = mockDgElectionGroup();
        List<CorporateActionAccount> accounts = mockDgAccountsList();

        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(new ServiceErrorsImpl());

        when(corporateActionService.loadCorporateActionAccountsDetailsForIm(anyString(), anyString(), any(ServiceErrors.class)))
                .thenReturn(accounts);

        when(
                requestConverterService.createElectionGroupsForDg(any(CorporateActionContext.class),
                        any(ImCorporateActionElectionDetailsDto.class))).thenReturn(Arrays.asList(electionGroup));

        when(
                corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                        any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(
                corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                        any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(
                corporateActionElectionService.submitElectionGroupForIm(any(CorporateActionElectionGroup.class),
                        any(ServiceErrors.class))).thenReturn(electionGroup);

        when(corporateActionConverterFactory.getRequestConverterService(any(CorporateActionDetails.class))).thenReturn(
                requestConverterService);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, dto.getStatus());
        assertTrue(dto.getTotalCount() == 1);
        assertTrue(dto.getSuccessCount() == 1);
    }

    @Test
    public void testPmSubmit_whenValidateOptionsChanged_thenReturnOptionsChangedStatus() {
        when(userProfileService.isPortfolioManager()).thenReturn(true);

        when(
                corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                        any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.FALSE);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.OPTIONS_CHANGED, dto.getStatus());
    }

    @Test
    public void testDgSubmit_whenValidateOptionsHasNotChangedAndThereAreNoSubmissionErrors_thenReturnSuccessStatus() {
        when(userProfileService.isDealerGroup()).thenReturn(true);
        CorporateActionElectionGroup electionGroup = mockDgElectionGroup();
        List<CorporateActionAccount> accounts = mockDgAccountsList();

        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(new ServiceErrorsImpl());

        when(corporateActionService.loadCorporateActionAccountsDetailsForIm(anyString(), anyString(), any(ServiceErrors.class)))
                .thenReturn(accounts);

        when(requestConverterService.createElectionGroupsForDg(any(CorporateActionContext.class),
                any(ImCorporateActionElectionDetailsDto.class))).thenReturn(Arrays.asList(electionGroup));

        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(corporateActionElectionService.submitElectionGroupForIm(any(CorporateActionElectionGroup.class),
                any(ServiceErrors.class))).thenReturn(electionGroup);

        when(corporateActionConverterFactory.getRequestConverterService(any(CorporateActionDetails.class)))
                .thenReturn(requestConverterService);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.SUCCESS, dto.getStatus());
        assertTrue(dto.getTotalCount() == 1);
        assertTrue(dto.getSuccessCount() == 1);
    }

    @Test
    public void testDgSubmit_whenValidateOptionsHasNotChangedAndThereIsSubmissionError_thenReturnErrorStatus() {
        when(userProfileService.isDealerGroup()).thenReturn(true);
        CorporateActionElectionGroup electionGroup = mockDgElectionGroup();
        List<CorporateActionAccount> accounts = mockDgAccountsList();

        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(new ServiceErrorsImpl());

        when(corporateActionService.loadCorporateActionAccountsDetailsForIm(anyString(), anyString(), any(ServiceErrors.class)))
                .thenReturn(accounts);

        when(requestConverterService
                .createElectionGroupsForDg(any(CorporateActionContext.class), any(ImCorporateActionElectionDetailsDto.class)))
                .thenReturn(Arrays.asList(electionGroup));

        CorporateActionValidationError validationError = mock(CorporateActionValidationError.class);
        when(validationError.getPositionId()).thenReturn("300");
        when(validationError.getErrorCode()).thenReturn("ERR");
        when(validationError.getErrorMessage()).thenReturn("Error message");

        when(electionGroup.getValidationErrors()).thenReturn(Collections.singletonList(validationError));

        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(corporateActionValidationConverter.hasManagedPortfolioValidationError(any(CorporateActionElectionGroup.class),
                anyList())).thenReturn(Boolean.TRUE);

        when(corporateActionElectionService.submitElectionGroupForIm(any(CorporateActionElectionGroup.class), any(ServiceErrors.class)))
                .thenReturn(electionGroup);

        when(corporateActionConverterFactory.getRequestConverterService(any(CorporateActionDetails.class)))
                .thenReturn(requestConverterService);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.ERROR, dto.getStatus());
        assertTrue(dto.getTotalCount() == 1);
        assertTrue(dto.getSuccessCount() == 0);
    }

    @Test
    public void testDgSubmit_whenValidateOptionsHasNotChangedAndThereIsASystemError_thenReturnErrorStatus() {
        when(userProfileService.isDealerGroup()).thenReturn(true);
        CorporateActionElectionGroup electionGroup = mockDgElectionGroup();
        List<CorporateActionAccount> accounts = mockDgAccountsList();

        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        when(serviceErrors.hasErrors()).thenReturn(true);

        List<ServiceError> serviceErrorList = new ArrayList<>();
        serviceErrorList.add(new ServiceErrorImpl());
        when(serviceErrors.getErrorList()).thenReturn(serviceErrorList);
        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(serviceErrors);

        when(corporateActionService.loadCorporateActionAccountsDetailsForIm(anyString(), anyString(), any(ServiceErrors.class))).thenReturn(
                accounts);

        when(requestConverterService
                .createElectionGroupsForDg(any(CorporateActionContext.class), any(ImCorporateActionElectionDetailsDto.class)))
                .thenReturn(Arrays.asList(electionGroup));

        CorporateActionValidationError validationError = mock(CorporateActionValidationError.class);
        when(validationError.getPositionId()).thenReturn("300");
        when(validationError.getErrorCode()).thenReturn("ERR");
        when(validationError.getErrorMessage()).thenReturn("Error message");

        when(electionGroup.getValidationErrors()).thenReturn(Collections.singletonList(validationError));

        when(corporateActionValidationConverter.validateOptions(any(CorporateActionElectionDetailsBaseDto.class),
                any(CorporateActionContext.class), any(ServiceErrorsImpl.class))).thenReturn(Boolean.TRUE);

        when(corporateActionValidationConverter.hasManagedPortfolioValidationError(any(CorporateActionElectionGroup.class),
                anyList())).thenReturn(Boolean.TRUE);

        when(corporateActionElectionService.submitElectionGroupForIm(any(CorporateActionElectionGroup.class), any(ServiceErrors.class)))
                .thenReturn(electionGroup);

        when(corporateActionConverterFactory.getRequestConverterService(any(CorporateActionDetails.class)))
                .thenReturn(requestConverterService);

        CorporateActionElectionDetailsBaseDto dto = imCorporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.ERROR, dto.getStatus());
        assertTrue(dto.getTotalCount() == 1);
    }

    private CorporateActionElectionGroup mockDgElectionGroup() {
        CorporateActionPosition ftpPosition = Mockito.mock(CorporateActionPosition.class);
        when(ftpPosition.getId()).thenReturn("100");
        when(ftpPosition.getContainerType()).thenReturn(null);

        CorporateActionPosition shadowPosition = mock(CorporateActionPosition.class);
        when(shadowPosition.getId()).thenReturn("200");
        when(shadowPosition.getContainerType()).thenReturn(ContainerType.SHADOW_MANAGED_PORTFOLIO);

        CorporateActionPosition mpPosition = mock(CorporateActionPosition.class);
        when(mpPosition.getId()).thenReturn("300");
        when(mpPosition.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        List<CorporateActionPosition> positions = new ArrayList<>();
        positions.add(ftpPosition);
        positions.add(shadowPosition);
        positions.add(mpPosition);

        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        when(electionGroup.getValidationErrors()).thenReturn(null);
        when(electionGroup.getPositions()).thenReturn(positions);

        return electionGroup;
    }

    private List<CorporateActionAccount> mockDgAccountsList() {
        CorporateActionAccount ftpAccount = mock(CorporateActionAccount.class);
        when(ftpAccount.getPositionId()).thenReturn("100");
        when(ftpAccount.getContainerType()).thenReturn(null);

        CorporateActionAccount shadowAccount = mock(CorporateActionAccount.class);
        when(ftpAccount.getPositionId()).thenReturn("200");
        when(ftpAccount.getContainerType()).thenReturn(ContainerType.SHADOW_MANAGED_PORTFOLIO);

        CorporateActionAccount mpAccount = mock(CorporateActionAccount.class);
        when(ftpAccount.getPositionId()).thenReturn("300");
        when(ftpAccount.getContainerType()).thenReturn(ContainerType.MANAGED_PORTFOLIO);

        List<CorporateActionAccount> accounts = new ArrayList<>();
        accounts.add(ftpAccount);
        accounts.add(shadowAccount);
        accounts.add(mpAccount);

        return accounts;
    }
}
