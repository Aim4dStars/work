package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentKey;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * Created by L062605 on 3/08/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentDtoConverterTest {

    @InjectMocks
    private DocumentDtoConverter documentDtoConverter;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private CmsService cmsService;

    @Mock
    private BrokerHelperService brokerService;

    List<Document> documents = new ArrayList<>();
    List<DocumentDto> documentDtos = new ArrayList<>();

    @Before
    public void setup() {
        DateTimeFormatter format = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        CmisDocumentImpl cmisDocument1 = new CmisDocumentImpl();
        cmisDocument1.setDocumentKey(DocumentKey.valueOf("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0"));
        cmisDocument1.setDocumentName("Document1");
        cmisDocument1.setSize(BigInteger.valueOf(123456));
        cmisDocument1.setDocumentType("STM");
        cmisDocument1.setAccountKey(AccountKey.valueOf("1234"));
        cmisDocument1.setFinancialYear("2013/2014");
        cmisDocument1.setMimeType("application/pdf");
        cmisDocument1.setStatus("Draft");
        cmisDocument1.setUploadedRole("Adviser");
        cmisDocument1.setUploadedDate(new DateTime());
        cmisDocument1.setStartDate("2015-08-17T20:27:05.000+11:00");
        cmisDocument1.setEndDate("2015-08-20T20:27:05.000+11:00");
        cmisDocument1.setDocumentTitleCode("STMANN");
        documents.add(cmisDocument1);

        CmisDocumentImpl cmisDocument2 = new CmisDocumentImpl();
        cmisDocument2.setDocumentKey(DocumentKey.valueOf("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E1"));
        cmisDocument2.setDocumentName("Document2");
        cmisDocument2.setSize(BigInteger.valueOf(123465));
        cmisDocument2.setDocumentType("CORRO");
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
        key.setAccountId("123");
        documentDto1.setKey(key);
        documentDto1.setDocumentName("Document1");
        documentDto1.setSize(BigInteger.valueOf(123456));
        documentDto1.setDocumentType("STM");
        documentDto1.setFinancialYear("2013/2014");
        documentDto1.setFileType("application/pdf");
        documentDto1.setStatus("Draft");
        documentDto1.setUploadedRole("Adviser");
        documentDto1.setUploadedDate(new DateTime());
        documentDtos.add(documentDto1);

        DocumentDto documentDto2 = new DocumentDto();
        com.bt.nextgen.api.statements.model.DocumentKey key1 = new com.bt.nextgen.api.statements.model.DocumentKey();
        key1.setDocumentId(EncodedString.fromPlainText("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E1").toString());
        documentDto2.setKey(key);
        documentDto2.setDocumentName("Document2");
        documentDto2.setSize(BigInteger.valueOf(123456));
        documentDto2.setDocumentType("CORRO");
        documentDto2.setFinancialYear("2012/2013");
        documentDto2.setFileType("application/txt");
        documentDto2.setStatus("Draft");
        documentDto2.setUploadedRole("Adviser");
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
        documentDto3.setUploadedRole("Adviser");
        documentDto3.setUploadedDate(new DateTime());
        documentDtos.add(documentDto3);

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        AccountKey accountKey = AccountKey.valueOf("123456");
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountNumber("123456");
        accountMap.put(accountKey, wrapAccount);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) Matchers.anyObject()))
                .thenReturn(accountMap);
        when(brokerService.getUserExperience(Mockito.any(WrapAccount.class), Mockito.any(ServiceErrors.class))).thenReturn(UserExperience.ADVISED);
    }

    @Test
    public void testGetDocumentDtoList() {
        UserProfile userProfile = getProfile();
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        List<DocumentDto> dtoList = documentDtoConverter.getDocumentDtoList(documents, "448FD7E09FAA45458DAA43B9B9B954BA0B7C0BC56FBD8A59");
        Assert.assertNotNull(dtoList);
        Assert.assertEquals(documents.size(), dtoList.size());
        DocumentDto dto = dtoList.get(0);
        Document document = documents.get(0);
        Assert.assertEquals("Annual investment statement (17 Aug 2015 - 20 Aug 2015).pdf", dto.getDocumentName());
        Assert.assertEquals("2015/2016", dto.getFinancialYear());
        Assert.assertEquals(VisibilityRoles.ADVISER.getDescription(), dto.getUploadedRole());
        Assert.assertEquals(document.getMimeType(), dto.getFileType());
        Assert.assertEquals(document.getSize(), dto.getSize());
    }

    @Test
    public void testDeletablePermission()
    {
        List<Document> documents_for_delete = new ArrayList<>();
        CmisDocumentImpl cmisDocument1=new CmisDocumentImpl();
        cmisDocument1.setDocumentKey(DocumentKey.valueOf("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0"));
        cmisDocument1.setDocumentName("Document1");
        cmisDocument1.setSize(BigInteger.valueOf(123456));
        cmisDocument1.setDocumentType("TAX");
        cmisDocument1.setAccountKey(AccountKey.valueOf("1234"));
        cmisDocument1.setFinancialYear("2013/2014");
        cmisDocument1.setMimeType("application/pdf");
        cmisDocument1.setStatus("Draft");
        cmisDocument1.setUploadedRole("Accountant");
        cmisDocument1.setUploadedDate(new DateTime());
        cmisDocument1.setDocumentTitleCode("STMANN");
        documents_for_delete.add(cmisDocument1);

        UserProfile userProfile = getProfile();
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        List<DocumentDto> dtoList = documentDtoConverter.getDocumentDtoList(documents_for_delete, "448FD7E09FAA45458DAA43B9B9B954BA0B7C0BC56FBD8A59");
        Assert.assertTrue(dtoList.get(0).isDeletable());

        CmisDocumentImpl cmisDocument2=new CmisDocumentImpl();

        cmisDocument2.setDocumentKey(DocumentKey.valueOf("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0"));
        cmisDocument2.setDocumentName("Document1");
        cmisDocument2.setSize(BigInteger.valueOf(123456));
        cmisDocument2.setDocumentType("TAX");
        cmisDocument2.setAccountKey(AccountKey.valueOf("1234"));
        cmisDocument2.setFinancialYear("2013/2014");
        cmisDocument2.setMimeType("application/pdf");
        cmisDocument2.setStatus("Draft");
        cmisDocument2.setUploadedRole("Adviser");
        cmisDocument2.setUploadedDate(new DateTime());
        cmisDocument2.setDocumentTitleCode("STMANN");
        documents_for_delete.add(cmisDocument2);
        dtoList = documentDtoConverter.getDocumentDtoList(documents_for_delete, "448FD7E09FAA45458DAA43B9B9B954BA0B7C0BC56FBD8A59");

        Assert.assertFalse(dtoList.get(1).isDeletable());


    }

    @Test
    public void testGetDocument() {
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountNumber("123456");
        UserProfile userProfile = getProfile();
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        Mockito.when(accountIntegrationService.loadWrapAccountWithoutContainers(Matchers.any(AccountKey.class),
                Matchers.any(ServiceErrors.class))).thenReturn(wrapAccount);
        DocumentDto documentDto = new DocumentDto();
        com.bt.nextgen.api.statements.model.DocumentKey key = new com.bt.nextgen.api.statements.model.DocumentKey();
        key.setDocumentId(EncodedString.fromPlainText("idd_AF1B3E96-C563-4ECB-AB60-327456BD19E0").toString());
        key.setAccountId(EncodedString.fromPlainText("12345").toString());
        documentDto.setKey(key);
        documentDto.setDocumentName("Document1");
        documentDto.setSize(BigInteger.valueOf(123456));
        documentDto.setDocumentType("Statement");
        documentDto.setFinancialYear("2013/2014");
        documentDto.setFileType("application/pdf");
        documentDto.setStatus("Draft");
        documentDto.setUploadedRole("Adviser");
        documentDto.setUploadedDate(new DateTime());

        Document document = documentDtoConverter.getDocument(documentDto);
        Assert.assertNotNull(document);
        Assert.assertEquals(documentDto.getDocumentName(), document.getDocumentName());
        Assert.assertEquals(documentDto.getFileType(), document.getMimeType());
        Assert.assertEquals(documentDto.getStatus(), document.getStatus());

        Mockito.when(userProfileService.isServiceOperator()).thenReturn(true);
        WrapAccountDetailImpl impl = new WrapAccountDetailImpl();
        impl.setAccountNumber("123456");
        Mockito.when(accountIntegrationService.loadWrapAccountDetail(Matchers.any(AccountKey.class), Matchers.any(ServiceErrors.class))).thenReturn(impl);
        document = documentDtoConverter.getDocument(documentDto);
        Assert.assertNotNull(document);
        Assert.assertEquals(documentDto.getDocumentName(), document.getDocumentName());
        Assert.assertEquals(documentDto.getFileType(), document.getMimeType());
        Assert.assertEquals(documentDto.getStatus(), document.getStatus());
    }

    @Test
    public void testFilteredDocuments(){
        List<DocumentDto> dtoList = documentDtoConverter.filteredDocuments(documentDtos, "document");
        Assert.assertEquals(3, dtoList.size());
    }

    private UserProfile getProfile() {
        UserProfile userProfile = Mockito.mock(UserProfile.class);
        when(userProfile.getBankReferenceId()).thenReturn("2016351254");
        when(userProfile.getJobRole()).thenReturn(JobRole.ACCOUNTANT);
        return userProfile;
    }
}
