package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoKey;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionElectionResultDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionValidationStatus;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionValidationConverterImplTest {
    @Mock
    protected CmsService cmsService;
    @InjectMocks
    private CorporateActionValidationConverterImpl validationConverterImpl;
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
    private CorporateActionElectionDetailsDto electionDetailsDto;

    @Mock
    private CorporateActionAccount corporateActionAccount1;

    @Mock
    private CorporateActionAccount corporateActionAccount2;

    @Mock
    private CorporateActionAccountDetailsDto corporateActionAccountDetailsDto1;

    @Mock
    private CorporateActionElectionGroup electionGroup;

    @Mock
    private CorporateActionOptionDto corporateActionOptionDto1;

    @Mock
    private CorporateActionOptionDto corporateActionOptionDto2;

    @Before
    public void setup() {
        when(corporateActionPosition1.getId()).thenReturn("0");
        when(corporateActionPosition1.getContainerType()).thenReturn(ContainerType.DIRECT);

        when(corporateActionPosition2.getId()).thenReturn("1");
        when(corporateActionPosition2.getContainerType()).thenReturn(ContainerType.DIRECT);

        when(electionDetailsDto.getKey()).thenReturn(new CorporateActionDtoKey("0"));

        when(corporateActionAccount1.getPositionId()).thenReturn("0");
        when(corporateActionAccount1.getContainerType()).thenReturn(ContainerType.DIRECT);
        when(corporateActionAccount2.getPositionId()).thenReturn("1");
        when(corporateActionAccount2.getContainerType()).thenReturn(ContainerType.DIRECT);

        when(corporateActionAccountDetailsDto1.getAccountId()).thenReturn("0");
        when(corporateActionAccountDetailsDto1.getPositionId()).thenReturn("0");

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
    public void testToElectionResultDtoList_whenThereAreNoPositions_thenReturnEmptyList() {
        CorporateActionElectionGroup electionGroup = mock(CorporateActionElectionGroup.class);
        when(electionGroup.getPositions()).thenReturn(new ArrayList<CorporateActionPosition>());

        List<CorporateActionElectionResultDto> result = validationConverterImpl.toElectionResultDtoList(electionGroup, null, null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testToElectionResultDtoList_whenTherePositionsAndNoValidationErrors_thenReturnDtoWithSuccessStatus() {
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(corporateActionAccountDetailsDto1));
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1));

        List<CorporateActionElectionResultDto> result =
                validationConverterImpl.toElectionResultDtoList(electionGroup, electionDetailsDto, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CorporateActionValidationStatus.SUCCESS, result.get(0).getStatus());
    }

    @Test
    public void testToElectionResultDtoList_whenTherePositionsAndHasAnEmptyValidationError_thenReturnDtoWithSuccessStatus() {
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(corporateActionAccountDetailsDto1));
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1));

        List<CorporateActionElectionResultDto> result = validationConverterImpl
                .toElectionResultDtoList(electionGroup, electionDetailsDto, new ArrayList<CorporateActionValidationError>());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CorporateActionValidationStatus.SUCCESS, result.get(0).getStatus());
    }

    @Test
    public void testToElectionResultDtoList_whenTherePositionsAndHasAValidationError_thenReturnDtoWithErrorStatus() {
        when(electionDetailsDto.getAccounts()).thenReturn(Arrays.asList(corporateActionAccountDetailsDto1));
        when(electionGroup.getPositions()).thenReturn(Arrays.asList(corporateActionPosition1));

        CorporateActionValidationError corporateActionValidationError = mock(CorporateActionValidationError.class);
        when(corporateActionValidationError.getPositionId()).thenReturn("0");
        when(corporateActionValidationError.getErrorCode()).thenReturn("btfg$valid_decsn_qty");

        List<CorporateActionElectionResultDto> result = validationConverterImpl
                .toElectionResultDtoList(electionGroup, electionDetailsDto, Arrays.asList(corporateActionValidationError));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CorporateActionValidationStatus.ERROR, result.get(0).getStatus());
    }

    @Test
    public void testGetCmsText() {
        assertEquals("Cms text", validationConverterImpl.getCmsText("XXX", null));
        assertEquals("Dynamic cms text", validationConverterImpl.getCmsText("XXX", "X"));
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
    public void testCreateElectionResults() {
        assertNotNull(validationConverterImpl.createElectionResults());
    }

    @Test
    public void testCreateServiceErrors() {
        assertNotNull(validationConverterImpl.createServiceErrors());
    }
}
