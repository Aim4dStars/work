package com.bt.nextgen.api.statements.service.validator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.service.DocumentDtoConverter;
import com.bt.nextgen.api.statements.service.DocumentDtoService;
import com.bt.nextgen.api.statements.validation.DocumentDtoValidator;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.financialdocument.DocumentIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentDtoValidatorTest {

    @InjectMocks
    DocumentDtoValidator documentDtoValidator;

    @Mock
    UserProfileService profileService;

    @Mock
    DocumentDtoConverter documentDtoConverter;


    @Mock
    DocumentDtoService documentDtoService;

    @Mock
    DocumentIntegrationService documentIntegrationService;

    DocumentDto documentDto = null;

    DocumentKey documentKey=new DocumentKey();

    AccountKey accountKey;

    @Mock
    AccountIntegrationService accountIntegrationService;
    ServiceErrors serviceErrors;

    @Before
    public void setUp(){
        documentDto = new DocumentDto();
        documentKey.setDocumentId("121321323");
        documentKey.setAccountId("3dwe3343");
        documentDto.setKey(documentKey);
        documentDto.setStatus("Draft");
        documentDto.setDocumentType("ADVICE");
    }

    @Test
    public void testDocumentVisibilityValidation(){
        setProfile(JobRole.ADVISER);
        boolean result = documentDtoValidator.checkForDocumentCategory(documentDto);
        Assert.assertTrue(result);

        documentDto.getKey().setDocumentId(null);
        result = documentDtoValidator.checkForDocumentCategory(documentDto);
        Assert.assertTrue(result);

        setProfile(JobRole.ACCOUNTANT);
        result = documentDtoValidator.checkForDocumentCategory(documentDto);
        Assert.assertFalse(result);
    }

    @Test
    public void testDocumentSubTypeValidation(){
        documentDto.setDocumentType("INV");
        documentDto.setDocumentSubType("Corporate Actions");
        documentDto.setDocumentName("test.pdf");
        Boolean result= documentDtoValidator.checkForDocumentSubType(documentDto);
        Assert.assertTrue(result);

        documentDto.setDocumentType("SMSF");
        documentDto.setDocumentSubType("Corporate Actions");
        documentDto.setDocumentName("test.pdf");
        result= documentDtoValidator.checkForDocumentSubType(documentDto);
        Assert.assertFalse(result);

        documentDto.setDocumentType("SMSF");
        documentDto.setDocumentSubType("Company");
        documentDto.setDocumentName("test.pdf");
        result= documentDtoValidator.checkForDocumentSubType(documentDto);
        Assert.assertTrue(result);

        documentDto.setDocumentType("INV");
        documentDto.setDocumentSubType("Company");
        documentDto.setDocumentName("test.pdf");
        result= documentDtoValidator.checkForDocumentSubType(documentDto);
        Assert.assertFalse(result);
    }

    @Test
    public void testStatementTypeValidation(){
        Mockito.when(profileService.isServiceOperator()).thenReturn(true);
        documentDto.setDocumentType("STM");

        documentDto.setDocumentTitleCode("test");
        Boolean result=documentDtoValidator.checkForStatementTypeDocuments(documentDto);
        Assert.assertTrue(result);

        documentDto.setDocumentTitleCode(null);
        result=documentDtoValidator.checkForStatementTypeDocuments(documentDto);
        Assert.assertFalse(result);

        documentDto.setDocumentTitleCode("");
        result=documentDtoValidator.checkForStatementTypeDocuments(documentDto);
        Assert.assertFalse(result);
    }

    @Test
    public void testCheckForFileType(){
        documentDto.setDocumentName("test.pdf");
        boolean result=documentDtoValidator.checkForFileType(documentDto);
        Assert.assertTrue(result);

        documentDto.setDocumentName("test.random");
        result=documentDtoValidator.checkForFileType(documentDto);
        Assert.assertFalse(result);
    }

    @Test
    public void testCheckForNullValues(){
        documentDto.setDocumentName("test.pdf");
        boolean result = documentDtoValidator.checkForNullValues(documentDto, new ArrayList<DomainApiErrorDto>());
        Assert.assertTrue(result);

        documentDto.setDocumentName(null);
        result = documentDtoValidator.checkForNullValues(documentDto, new ArrayList<DomainApiErrorDto>());
        Assert.assertFalse(result);
    }

    private void setProfile(JobRole jobRole) {
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(jobRole);
        UserInformationImpl userInformation = new UserInformationImpl();
        UserProfileAdapterImpl profileAdapter = new UserProfileAdapterImpl(userInformation, jobProfile);
        Mockito.when(profileService.getActiveProfile()).thenReturn(profileAdapter);
    }

    @Test
    public void testCheckForDuplicateFileName()
    {
        //Upload new document - if document not present
        List<DomainApiErrorDto> errorList = new ArrayList<>();
        Mockito.when(documentDtoService.getDocumentsForDuplicateNameCheck((DocumentDto)Matchers.anyObject(), (ServiceErrors)Matchers.anyObject())).thenReturn(Collections.EMPTY_LIST);
        documentDtoValidator.checkForDuplicateFileName(documentDto, errorList);
        Assert.assertTrue(errorList.isEmpty());

        //Upload new version - if retrived document is previous version
        DocumentDto dto = new DocumentDto();
        DocumentKey key = new DocumentKey();
       /* key.setDocumentId("121321323");
        dto.setKey(key);
        Mockito.when(documentDtoService.getDocumentsForDuplicateNameCheck((DocumentDto)Matchers.anyObject(), (ServiceErrors)Matchers.anyObject())).thenReturn(Collections.singletonList(dto));
        documentDtoValidator.checkForDuplicateFileName(documentDto, errorList);
        Assert.assertTrue(errorList.isEmpty());*/

        //Upload new version - if retrived document is different
        key.setDocumentId("12132132");
        dto.setKey(key);
        Mockito.when(documentDtoService.getDocumentsForDuplicateNameCheck((DocumentDto)Matchers.anyObject(), (ServiceErrors)Matchers.anyObject())).thenReturn(Collections.singletonList(dto));
        documentDtoValidator.checkForDuplicateFileName(documentDto, errorList);
        Assert.assertFalse(errorList.isEmpty());

        //Upload new document - if document present
        documentDto.getKey().setDocumentId(null);
        documentDtoValidator.checkForDuplicateFileName(documentDto, errorList);
        Assert.assertFalse(errorList.isEmpty());

        //Upload new version - if one of the document from retrived documents is different
        DocumentDto dto1 = new DocumentDto();
        DocumentKey key1 = new DocumentKey();
        key1.setDocumentId("121321323");
        dto1.setKey(key1);
        List<DocumentDto> documents = new ArrayList<>();
        documents.add(dto);
        documents.add(dto1);
        Mockito.when(documentDtoService.getDocumentsForDuplicateNameCheck((DocumentDto)Matchers.anyObject(), (ServiceErrors)Matchers.anyObject())).thenReturn(documents);
        documentDtoValidator.checkForDuplicateFileName(documentDto, errorList);
        Assert.assertFalse(errorList.isEmpty());
    }
}
