package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.financialdocument.Criteria;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentIntegrationService;
import com.bt.nextgen.service.integration.financialdocument.DocumentKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Created by L062605 on 31/07/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentDtoServiceImplTest {

    @InjectMocks
    private DocumentDtoServiceImpl documentDtoService;

    @Mock
    private DocumentIntegrationService documentIntegrationService;

    @Mock
    private DocumentDtoConverter documentDtoConverter;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserProfile userProfile;

    List<Document> documents = new ArrayList<>();
    List<DocumentDto> documentDtos = new ArrayList<>();

    @Before
    public void setup() {
        when(userProfile.getBankReferenceId()).thenReturn("2016351254");
        when(userProfile.getJobRole()).thenReturn(JobRole.ACCOUNTANT);

        CmisDocumentImpl cmisDocument1 = new CmisDocumentImpl();
        cmisDocument1.setDocumentKey(DocumentKey.valueOf("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0"));
        cmisDocument1.setDocumentName("Document1");
        cmisDocument1.setSize(BigInteger.valueOf(123456));
        cmisDocument1.setDocumentType("Statement");
        cmisDocument1.setAccountKey(AccountKey.valueOf("1234"));
        cmisDocument1.setFinancialYear("2013/2014");
        cmisDocument1.setMimeType("application/pdf");
        cmisDocument1.setStatus("Draft");
        cmisDocument1.setUploadedRole("Adviser");
        cmisDocument1.setUploadedDate(new DateTime());
        documents.add(cmisDocument1);

        CmisDocumentImpl cmisDocument2 = new CmisDocumentImpl();
        cmisDocument2.setDocumentKey(DocumentKey.valueOf("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E1"));
        cmisDocument2.setDocumentName("Document2");
        cmisDocument2.setSize(BigInteger.valueOf(123465));
        cmisDocument2.setDocumentType("Company");
        cmisDocument2.setAccountKey(AccountKey.valueOf("1234"));
        cmisDocument2.setFinancialYear("2012/2013");
        cmisDocument2.setMimeType("application/txt");
        cmisDocument2.setStatus("Draft");
        cmisDocument2.setUploadedRole("Adviser");
        cmisDocument2.setUploadedDate(new DateTime());
        documents.add(cmisDocument2);

        CmisDocumentImpl cmisDocument3 = new CmisDocumentImpl();
        cmisDocument3.setDocumentKey(DocumentKey.valueOf("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E2"));
        cmisDocument3.setDocumentName("Document3");
        cmisDocument3.setSize(BigInteger.valueOf(123476));
        cmisDocument3.setDocumentType("SMSF");
        cmisDocument3.setAccountKey(AccountKey.valueOf("1234"));
        cmisDocument3.setFinancialYear("2011/2012");
        cmisDocument3.setMimeType("image/jpeg");
        cmisDocument3.setStatus("Final");
        cmisDocument3.setUploadedRole("Adviser");
        cmisDocument3.setUploadedDate(new DateTime());
        documents.add(cmisDocument3);

        DocumentDto documentDto1 = new DocumentDto();
        com.bt.nextgen.api.statements.model.DocumentKey key = new com.bt.nextgen.api.statements.model.DocumentKey();
        key.setDocumentId(EncodedString.fromPlainText("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0").toString());
        documentDto1.setKey(key);
        documentDto1.setDocumentName("Document1");
        documentDto1.setSize(BigInteger.valueOf(123456));
        documentDto1.setDocumentType("Statement");
        documentDto1.setFinancialYear("2013/2014");
        documentDto1.setFileType("application/pdf");
        documentDto1.setStatus("Draft");
        documentDto1.setUploadedRole("Accountant");
        documentDto1.setUploadedDate(new DateTime());
        documentDtos.add(documentDto1);

        DocumentDto documentDto2 = new DocumentDto();
        com.bt.nextgen.api.statements.model.DocumentKey key1 = new com.bt.nextgen.api.statements.model.DocumentKey();
        key1.setDocumentId(EncodedString.fromPlainText("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E1").toString());
        documentDto2.setKey(key);
        documentDto2.setDocumentName("Document2");
        documentDto2.setSize(BigInteger.valueOf(123456));
        documentDto2.setDocumentType("Company");
        documentDto2.setFinancialYear("2012/2013");
        documentDto2.setFileType("application/txt");
        documentDto2.setStatus("Draft");
        documentDto2.setUploadedRole("ADVISER");
        documentDto2.setUploadedDate(new DateTime());
        documentDtos.add(documentDto2);

        DocumentDto documentDto3 = new DocumentDto();
        com.bt.nextgen.api.statements.model.DocumentKey key2 = new com.bt.nextgen.api.statements.model.DocumentKey();
        key2.setDocumentId(EncodedString.fromPlainText("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E2").toString());
        documentDto3.setKey(key);
        documentDto3.setDocumentName("Document3");
        documentDto3.setSize(BigInteger.valueOf(23476));
        documentDto3.setDocumentType("SMSF");
        documentDto3.setFinancialYear("2011/2012");
        documentDto3.setFileType("image/jpeg");
        documentDto3.setStatus("Final");
        documentDto3.setUploadedRole("ADVISER");
        documentDto3.setUploadedDate(new DateTime());
        documentDtos.add(documentDto3);
    }

    @Test
    public void testSearch() {
        Mockito.when(documentIntegrationService.getDocuments((Criteria) Matchers.anyObject())).thenReturn(documents);
        Mockito.when(userProfileService.isAccountant()).thenReturn(true);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        Mockito.when(documentDtoConverter.getAccountNumber((com.bt.nextgen.api.statements.model.DocumentKey) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn("123245");
        Mockito.when(documentDtoConverter.getDocumentDtoList((Collection<Document>) Matchers.anyObject(), Matchers.anyString())).thenReturn(documentDtos);
        com.bt.nextgen.api.statements.model.DocumentKey key = new com.bt.nextgen.api.statements.model.DocumentKey();
        key.setAccountId("448FD7E09FAA45458DAA43B9B9B954BA0B7C0BC56FBD8A59");
        List<DocumentDto> listOfDocuments = documentDtoService.search(key, new ServiceErrorsImpl());
        Assert.assertEquals(2, listOfDocuments.size());
    }

    @Ignore
    public void testLoadDocuments() {
        List<String> documentIds = new ArrayList<>();
        documentIds.add("B674E4A9DF84F2E696890506ECA9F63065206D9150EAD0E0");
        documentIds.add("8A9C3D55732DA44476B74C8F8507A6F1B12422C337D95ED3");
        documentIds.add("CED01F880DDBE5F2088A2F60E9BED083D6D4419942EF0FE5");
        documentIds.add("9FF78396B5DF091465DDBBA8F8CA6656178C57BD6936D251");
        try {
            CmisDocumentImpl document = (CmisDocumentImpl) documents.get(0);
            document.setData(new byte[12]);
            Mockito.when(documentIntegrationService.getDocumentData((com.bt.nextgen.service.integration.financialdocument.DocumentKey) Matchers.anyObject())).thenReturn(document);
            Mockito.when(documentIntegrationService.getDocuments((Criteria) Matchers.anyObject())).thenReturn(documents);
            Mockito.when(documentIntegrationService.getParentFolderId()).thenReturn("123654");
            Mockito.when(documentDtoConverter.getAccountNumber((com.bt.nextgen.api.statements.model.DocumentKey) Matchers.anyObject(), (ServiceErrors) Matchers.anyObject())).thenReturn("123245");
            Mockito.when(documentDtoConverter.getDocumentDto((Document) Matchers.anyObject(), Matchers.anyString())).thenReturn(documentDtos.get(0));
            Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
            com.bt.nextgen.api.statements.model.DocumentKey key = new com.bt.nextgen.api.statements.model.DocumentKey();
            key.setAccountId("448FD7E09FAA45458DAA43B9B9B954BA0B7C0BC56FBD8A59");
//            byte[] listOfDocuments = documentDtoService.loadDocuments(documentIds);
//            Assert.assertNotNull(listOfDocuments.length);
        }
        catch (IOException e) {

        }
    }

    @Test
    public void testSoftDeleteDocument() {
        com.bt.nextgen.api.statements.model.DocumentKey key = new com.bt.nextgen.api.statements.model.DocumentKey();
        key.setDocumentId("B674E4A9DF84F2E696890506ECA9F63065206D9150EAD0E0");
        key.setAccountId("dfdkf");
        DocumentDto dto = new DocumentDto();
        dto.setAudit(true);
        CmisDocumentImpl document = new CmisDocumentImpl();
        Collection<Document> documents = new ArrayList<>();
        documents.add(document);
        com.bt.nextgen.api.statements.model.DocumentKey key2 = new com.bt.nextgen.api.statements.model.DocumentKey();
        key2.setDocumentId("B674E4A9DF84F2E696890506ECA9F63065206D9150EAD0E0");
        Mockito.when(documentIntegrationService.getDocuments((Criteria) Matchers.anyObject())).thenReturn(documents);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        Mockito.when(documentIntegrationService.getParentFolderId()).thenReturn("123654");
        Mockito.when(documentDtoConverter.getDocumentDto((Document) Matchers.anyObject(), Matchers.anyString())).thenReturn(dto);
        boolean result = documentDtoService.softDeleteDocument(key);
        Assert.assertFalse(result);
        Mockito.when(documentDtoConverter.getDocumentDto((Document) Matchers.anyObject(), Matchers.anyString())).thenReturn(null);
        result = documentDtoService.softDeleteDocument(key);
        Assert.assertTrue(result);

    }
}