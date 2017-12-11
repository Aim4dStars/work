package com.bt.nextgen.corporateaction.service;

import java.util.ArrayList;
import java.util.Arrays;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionResponseCode;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionCommonService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionElectionDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionPersistenceDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionValidationConverter;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactory;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionRequestConverterService;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetailsResponse;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionPosition;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionValidationError;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CorporateActionElectionDtoServiceImplTest {
    @InjectMocks
    private CorporateActionElectionDtoServiceImpl corporateActionElectionDtoServiceImpl;

    @Mock
    private CorporateActionIntegrationService corporateActionService;

    @Mock
    private CorporateActionElectionIntegrationService corporateActionElectionService;

    @Mock
    private CorporateActionPersistenceDtoServiceImpl corporateActionPersistenceDtoService;

    @Mock
    private CorporateActionConverterFactory corporateActionConverterFactory;

    @Mock
    private CorporateActionValidationConverter corporateActionValidationConverter;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private CorporateActionElectionDetailsDto electionDetailsDto;

    @Mock
    private CorporateActionRequestConverterService requestConverterService;

    @Mock
    private CorporateActionResponseConverterService responseConverterService;

    @Mock
    private CorporateActionCommonService corporateActionCommonService;

    @Mock
    private CorporateActionDetailsResponse corporateActionDetailsResponse;

    @Mock
    private CorporateActionDetails corporateActionDetails;

    @Mock
    private CorporateActionOptionDto corporateActionOptionDto1;

    @Mock
    private CorporateActionOptionDto corporateActionOptionDto2;

    @Mock
    private CorporateActionOptionDto corporateActionOptionDto3;

    @Mock
    private CorporateActionElectionResultDto corporateActionElectionResultDto;

    @Mock
    private CorporateActionPosition corporateActionPosition;

    @Before
    public void setup() throws Exception {
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.PUBLIC_OFFER);

        when(corporateActionOptionDto1.getTitle()).thenReturn("Option A");
        when(corporateActionOptionDto1.getSummary()).thenReturn("Summary 1");

        when(corporateActionOptionDto2.getTitle()).thenReturn("Option B");
        when(corporateActionOptionDto2.getSummary()).thenReturn("Summary 2");

        when(corporateActionOptionDto3.getTitle()).thenReturn("Option C");
        when(corporateActionOptionDto3.getSummary()).thenReturn("Summary 3");

        when(corporateActionPosition.getId()).thenReturn("0");

        when(corporateActionDetailsResponse.getCorporateActionDetailsList()).thenReturn(Arrays.asList(corporateActionDetails));

        when(corporateActionService.loadCorporateActionDetails(anyString(), any(ServiceErrors.class))).thenReturn(
                corporateActionDetailsResponse);

        when(corporateActionValidationConverter.getCmsText(anyString())).thenReturn("Error text");

        when(corporateActionValidationConverter.createElectionResults()).thenReturn(new ArrayList<CorporateActionElectionResultDto>());
        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(new ServiceErrorsImpl());

        when(userProfileService.getPositionId()).thenReturn("0");
        when(userProfileService.isInvestor()).thenReturn(false);

        when(electionDetailsDto.getKey()).thenReturn(new CorporateActionDtoKey("0"));

        when(corporateActionConverterFactory.getResponseConverterService(any(CorporateActionDetails.class)))
                .thenReturn(responseConverterService);

        when(corporateActionConverterFactory.getRequestConverterService(any(CorporateActionDetails.class)))
                .thenReturn(requestConverterService);

        when(corporateActionCommonService.getUserProfileService()).thenReturn(userProfileService);

    }

    @Test
    public void testSubmit_whenValidateOptionsChanged_thenReturnOptionsChangedStatus() {
        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(corporateActionOptionDto1));

        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto2));

        CorporateActionElectionDetailsBaseDto dto = corporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        verify(corporateActionPersistenceDtoService).deleteDraftParticipation(anyString());
        verify(corporateActionPersistenceDtoService).deleteExpiredDraftParticipations();
        assertEquals(CorporateActionResponseCode.OPTIONS_CHANGED, dto.getStatus());
    }

    @Test
    public void testSubmit_whenThereOptionsButNoAvaloqOptions_thenReturnOptionsChangedStatus() {
        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class))).thenReturn(null);

        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto2));

        // Null test
        CorporateActionElectionDetailsBaseDto dto = corporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.OPTIONS_CHANGED, dto.getStatus());

        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(new ArrayList<CorporateActionOptionDto>(0));

        // Empty test
        dto = corporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.OPTIONS_CHANGED, dto.getStatus());
    }

    @Test
    public void testSubmit_whenThereOptionsAreOfDifferentSizeToAvaloqOptions_thenReturnOptionsChangedStatus() {
        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(corporateActionOptionDto1, corporateActionOptionDto2));

        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto2));

        CorporateActionElectionDetailsBaseDto dto = corporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.OPTIONS_CHANGED, dto.getStatus());
    }

    @Test
    public void testSubmit_whenValidateOptionsHasNotChangedAndThereAreNoSubmissionErrors_thenReturnSuccessStatus() {
        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        when(electionGroup.getValidationErrors()).thenReturn(null);
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition));

        when(requestConverterService.createElectionGroups(any(CorporateActionContext.class),
                any(CorporateActionElectionDetailsDto.class))).thenReturn(Arrays.asList(electionGroup));

        when(corporateActionElectionService.submitElectionGroup(any(CorporateActionElectionGroup.class), any(ServiceErrors.class)))
                .thenReturn(electionGroup);

        when(corporateActionValidationConverter
                .toElectionResultDtoList(any(CorporateActionElectionGroup.class), any(CorporateActionElectionDetailsDto.class), anyList()))
                .thenReturn(Arrays.asList(corporateActionElectionResultDto));

        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(corporateActionOptionDto1));

        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto1));

        CorporateActionElectionDetailsBaseDto dto = corporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        verify(corporateActionPersistenceDtoService)
                .deleteSuccessfulDraftAccountElections(any(CorporateActionElectionDetailsDto.class), anyList());

        assertEquals(CorporateActionResponseCode.SUCCESS, dto.getStatus());
        assertEquals((Integer) 1, dto.getTotalCount());
        assertEquals((Integer) 1, dto.getSuccessCount());
    }

    @Test
    public void testSubmit_whenValidateOptionsHasNotChangedAndThereAreSubmissionErrors_thenReturnErrorStatus() {
        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        when(electionGroup.getValidationErrors()).thenReturn(Arrays.asList(mock(CorporateActionValidationError.class)));
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition));

        when(requestConverterService.createElectionGroups(any(CorporateActionContext.class),
                any(CorporateActionElectionDetailsDto.class))).thenReturn(Arrays.asList(electionGroup));

        when(corporateActionElectionService.submitElectionGroup(any(CorporateActionElectionGroup.class), any(ServiceErrors.class)))
                .thenReturn(electionGroup);

        when(corporateActionValidationConverter
                .toElectionResultDtoList(any(CorporateActionElectionGroup.class), any(CorporateActionElectionDetailsDto.class), anyList()))
                .thenReturn(Arrays.asList(corporateActionElectionResultDto));

        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(corporateActionOptionDto1));

        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto1));

        CorporateActionElectionDetailsBaseDto dto = corporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        verify(corporateActionPersistenceDtoService)
                .deleteSuccessfulDraftAccountElections(any(CorporateActionElectionDetailsDto.class), anyList());

        assertEquals(CorporateActionResponseCode.ERROR, dto.getStatus());
        assertEquals((Integer) 1, dto.getTotalCount());
        assertEquals((Integer) 0, dto.getSuccessCount());
    }

    @Test
    public void testSubmit_whenValidateOptionsHasNotChangedAndThereIsAServiceError_thenReturnErrorStatus() {
        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        when(electionGroup.getValidationErrors()).thenReturn(Arrays.asList(mock(CorporateActionValidationError.class)));
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition));

        when(requestConverterService.createElectionGroups(any(CorporateActionContext.class),
                any(CorporateActionElectionDetailsDto.class))).thenReturn(Arrays.asList(electionGroup));

        when(corporateActionElectionService.submitElectionGroup(any(CorporateActionElectionGroup.class), any(ServiceErrors.class)))
                .thenReturn(electionGroup);

        when(corporateActionValidationConverter
                .toElectionResultDtoList(any(CorporateActionElectionGroup.class), any(CorporateActionElectionDetailsDto.class), anyList()))
                .thenReturn(Arrays.asList(corporateActionElectionResultDto));

        when(responseConverterService.toElectionOptionDtos(any(CorporateActionContext.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(corporateActionOptionDto1));

        ServiceErrors serviceErrors = mock(ServiceErrors.class);
        when(serviceErrors.hasErrors()).thenReturn(true);
        when(corporateActionValidationConverter.createServiceErrors()).thenReturn(serviceErrors);

        when(electionDetailsDto.getOptions()).thenReturn(Arrays.asList(corporateActionOptionDto1));

        CorporateActionElectionDetailsBaseDto dto = corporateActionElectionDtoServiceImpl.submit(electionDetailsDto, null);

        assertEquals(CorporateActionResponseCode.ERROR, dto.getStatus());
    }
}
