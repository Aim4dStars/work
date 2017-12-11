package com.bt.nextgen.api.statements.controller;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoMapImpl;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.service.ClientApplicationDtoService;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.permission.DocumentPermissionService;
import com.bt.nextgen.api.statements.service.DocumentUploadDtoService;
import com.bt.nextgen.api.statements.validation.DocumentDtoValidator;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.persistence.NoResultException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bt.nextgen.core.type.DateFormatType.DATEFORMAT_UPLOAD_OFFLINE;
import static com.bt.nextgen.core.type.DateUtil.toFormattedDate;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by L070354 on 18/08/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentUploadControllerTest {

    @InjectMocks
    private DocumentUploadController documentUploadController;

    @Mock
    private DocumentUploadDtoService documentUploadDtoService;

    @Mock
    private ClientApplicationDtoService clientApplicationDtoService;

    @Mock
    private DocumentDtoValidator validator;

    @Mock
    private DocumentPermissionService permissionService;

    @Mock
    private UserProfileService profileService;


    DocumentDto documentDto;
    DocumentDto offlineDocumentDto;
    MockMultipartFile file;
    DocumentKey documentKey;



    @Before
    public void setup() throws Exception {
        InputStream input = getClass().getResourceAsStream("/csv/testUploadFile.csv");
        file = new MockMultipartFile("testUploadFile", "testUploadFile.csv", MediaType.TEXT_PLAIN_VALUE, input);

        documentDto = new DocumentDto();
        documentKey = new DocumentKey();
        documentKey.setDocumentId("DOC_ID");
        documentDto.setDocumentName("DOC_NAME.pdf");
        documentDto.setKey(documentKey);

        offlineDocumentDto =  new DocumentDto();
        offlineDocumentDto.setKey(mock(DocumentKey.class));
        offlineDocumentDto.setDocumentName("OFFLINE_DOC_NAME.pdf");
        offlineDocumentDto.setUpdatedByID("7777");
    }

    @Test(expected=AccessDeniedException.class)
    public void testUploadModel_WithoutPermission() {
        when(permissionService.hasUploadNewPermission(any(DocumentDto.class))).thenReturn(false);

        ApiResponse response = documentUploadController.uploadModel(documentDto, file);
        verify(validator, never()).validate(any(DocumentDto.class));
    }

    @Test
    public void testUploadModel_WithPermission() {
        when(permissionService.hasUploadNewPermission(any(DocumentDto.class))).thenReturn(true);
        when(documentUploadDtoService.uploadNewVersion(documentDto)).thenReturn(documentDto);

        ApiResponse response = documentUploadController.uploadModel(documentDto, file);
        verify(validator).validate(documentDto);
        verify(documentUploadDtoService).uploadNewVersion(documentDto);
        assertThat(((DocumentDto)response.getData()).getDocumentName(), is("DOC_NAME.pdf"));
    }

    @Test
        public void testUploadOffline_with_databaseupdated_fine() {
        when(profileService.getFullName()).thenReturn("ADVISER_NAME");
        when(documentUploadDtoService.upload(offlineDocumentDto)).thenReturn(offlineDocumentDto);
        when(clientApplicationDtoService.find(any(ClientApplicationKey.class), any(ServiceErrorsImpl.class))).thenReturn(new ClientApplicationDtoMapImpl());

        ApiResponse response = documentUploadController.uploadOfflineModel(offlineDocumentDto, file);
        verify(validator).validate(offlineDocumentDto);
        verify(documentUploadDtoService).upload(offlineDocumentDto);
        assertThat(((DocumentDto)response.getData()).getDocumentName(), is("Offline Approval Document " + toFormattedDate(new Date(), DATEFORMAT_UPLOAD_OFFLINE) +".pdf"));
    }

    @Test(expected = NoResultException.class)
    public void testUploadOffline_WhenErrorInUpdating(){
        when(profileService.getFullName()).thenReturn("ADVISER_NAME");
        when(documentUploadDtoService.upload(offlineDocumentDto)).thenReturn(offlineDocumentDto);
        when(clientApplicationDtoService.find(any(ClientApplicationKey.class), any(ServiceErrorsImpl.class))).thenReturn(new ClientApplicationDtoMapImpl());
        when(clientApplicationDtoService.update(any(ClientApplicationDto.class), any(ServiceErrorsImpl.class))).thenThrow(NoResultException.class);

        ApiResponse response = documentUploadController.uploadOfflineModel(offlineDocumentDto, file);
        verify(validator).validate(offlineDocumentDto);
        verify(documentUploadDtoService).upload(offlineDocumentDto);
    }

    @Test
    public void testUpload_whenValidationsFail() {

        List<DomainApiErrorDto> errorList = new ArrayList<>();
        errorList.add(new DomainApiErrorDto(null, null, "The File Extension is out of the scope "));
        errorList.add(new DomainApiErrorDto(null, null, "User uploading invalid document"));
        offlineDocumentDto.setWarnings(errorList);

        when(permissionService.hasUploadNewPermission(any(DocumentDto.class))).thenReturn(true);
        when(profileService.getFullName()).thenReturn("ADVISER_NAME");
        when(clientApplicationDtoService.find(any(ClientApplicationKey.class), any(ServiceErrorsImpl.class))).thenReturn(new ClientApplicationDtoMapImpl());

        ApiResponse response = documentUploadController.uploadOfflineModel(offlineDocumentDto, file);
        verify(validator).validate(offlineDocumentDto);
        verify(documentUploadDtoService, never()).upload(any(DocumentDto.class));
        verify(documentUploadDtoService, never()).uploadNewVersion(any(DocumentDto.class));
        assertThat(((DocumentDto)response.getData()).getWarnings().size(), is(2));

    }
}
