package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.permission.DocumentRequestManager;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.integration.financialdocument.Criteria;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.DocumentKey;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;

/**
 * Created by L081361 on 2/12/2015.
 */
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(DocumentRequestManager.class)
public class UserDocumentDtoServiceImplTest {

    @InjectMocks
    private UserDocumentDtoServiceImpl userDocumentDtoService;

    @Mock
    private DocumentIntegrationService documentIntegrationService;

    @Mock
    private UserDocumentDtoConverter userDocumentDtoConverter;

    @Mock
    private UserProfileService userProfileService;

    List<Document> documents = new ArrayList<>();
    List<DocumentDto> documentDtos = new ArrayList<>();

    @Before
    public void setup(){

        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        CmisDocumentImpl cmisDocument1 = new CmisDocumentImpl();
        cmisDocument1.setDocumentKey(DocumentKey.valueOf("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0"));
        cmisDocument1.setDocumentName("IMRCTI_Doc.pdf");
        cmisDocument1.setStatus("Draft");
        cmisDocument1.setSize(BigInteger.valueOf(123456));
        cmisDocument1.setDocumentType("STM");
        cmisDocument1.setMimeType("application/pdf");
        cmisDocument1.setUploadedRole("INVESTMENT_MANAGER");
        cmisDocument1.setUploadedDate(new DateTime());
        cmisDocument1.setStartDate("17/08/2015 20:27:05");
        cmisDocument1.setEndDate("20/08/2015 20:27:05");
        cmisDocument1.setDocumentTitleCode("IMRCTI");
        cmisDocument1.setFileName("imrcti");
        cmisDocument1.setFileExtension("PDF");
        documents.add(cmisDocument1);

        DocumentDto documentDto1 = new DocumentDto();
        com.bt.nextgen.api.statements.model.DocumentKey key = new com.bt.nextgen.api.statements.model.DocumentKey();
        key.setDocumentId(EncodedString.fromPlainText("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0").toString());
        documentDto1.setKey(key);
        documentDto1.setDocumentName("IMRCTI_Doc.pdf");
        documentDto1.setSize(BigInteger.valueOf(123456));
        documentDto1.setDocumentType("Statement");
        documentDto1.setFileType("application/pdf");
        documentDto1.setStatus("Draft");
        documentDto1.setUploadedRole("INVESTMENT_MANAGER");
        documentDto1.setFileExtension("PDF");
        documentDto1.setFileName("imrcti");
        documentDto1.setUploadedDate(new DateTime());
        documentDtos.add(documentDto1);
        Mockito.when(documentIntegrationService.getParentFolderId()).thenReturn("12345");
    }

    public UserProfile getProfile(JobProfile jobProfile) {
        return new UserProfileAdapterImpl(null, jobProfile);
    }

    @Test
    public void testSearch(){
        Mockito.when(documentIntegrationService.getDocuments((Criteria) Matchers.anyObject())).thenReturn(documents);
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTMENT_MANAGER);
        jobProfile.setPersonJobId("123456");
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(getProfile(jobProfile));
        Mockito.when(userProfileService.getPositionId()).thenReturn("123");
        Mockito.when(userDocumentDtoConverter.getDocumentDtoList((Collection<Document>) Matchers.anyObject())).thenReturn(documentDtos);

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("documentTitleCode", ApiSearchCriteria.SearchOperation.EQUALS,"IMRCTI"));
        List<DocumentDto> listOfDocuments = userDocumentDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(1, listOfDocuments.size());
    }

    @Test
    public void testLoadDocuments(){
        List<String> documentIds = new ArrayList<>();
        documentIds.add("B674E4A9DF84F2E696890506ECA9F63065206D9150EAD0E0");
        try{
            CmisDocumentImpl document = (CmisDocumentImpl)documents.get(0);
            document.setData(new byte[12]);
            DocumentDto dto1 = new DocumentDto();

            Mockito.when(documentIntegrationService.getDocumentData( any(com.bt.nextgen.service.integration.financialdocument.DocumentKey.class))).thenReturn(document);
            Mockito.when(documentIntegrationService.getDocuments(any(Criteria.class))).thenReturn(documents);
            Mockito.when(documentIntegrationService.getParentFolderId()).thenReturn("123654");
            Mockito.when(userDocumentDtoConverter.getDocumentDto(any(Document.class))).thenReturn(documentDtos.get(0));
//            PowerMockito.mockStatic(DocumentRequestManager.class);
//            when(DocumentRequestManager.getDocument(any(com.bt.nextgen.api.statements.model.DocumentKey.class))).thenReturn(null);

            JobProfileImpl jobProfile = new JobProfileImpl();
            jobProfile.setJobRole(JobRole.INVESTMENT_MANAGER);
            Mockito.when(userProfileService.getActiveProfile()).thenReturn(getProfile(jobProfile));
            com.bt.nextgen.api.statements.model.DocumentKey key = new com.bt.nextgen.api.statements.model.DocumentKey();
            key.setDocumentId("B674E4A9DF84F2E696890506ECA9F63065206D9150EAD0E0");
            System.out.println("userDocumentDtoService : : "+userDocumentDtoService);
//            DocumentDto dto = userDocumentDtoService.loadDocument(key);
//            Assert.assertEquals(dto.getDocumentName(),"IMRCTI_Doc.pdf");
//            Assert.assertEquals(dto.getSize(),12);
        }
        catch(IOException e){

        }
    }

}
