package com.bt.nextgen.api.statements.service.permission;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.permission.DocumentPermissionService;
import com.bt.nextgen.api.statements.service.DocumentDtoConverter;
import com.bt.nextgen.api.statements.service.DocumentDtoService;
import com.bt.nextgen.api.statements.service.UserDocumentDtoConverter;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.financialdocument.Criteria;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.joda.time.DateTime;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentPermissionServiceTest {

    @InjectMocks
    private DocumentPermissionService permissionService;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private UserProfileService profileService;

    @Mock
    private DocumentIntegrationService documentIntegrationService;

    @Mock
    private DocumentDtoConverter documentDtoConverter;

    @Mock
    private UserDocumentDtoConverter userDocumentDtoConverter;

    @Mock
    private DocumentDtoService documentDtoService;

    List<FunctionalRole> functionalRoles = new ArrayList<>();

    @Before
    public void setUp() {
        functionalRoles.add(FunctionalRole.View_Document_library);
        functionalRoles.add(FunctionalRole.Upload_Document);
        functionalRoles.add(FunctionalRole.Maintain_document_attributes);
        functionalRoles.add(FunctionalRole.Update_document_Audit);
        functionalRoles.add(FunctionalRole.Delete_Document);

        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        AccountKey key = AccountKey.valueOf("1234");
        wrapAccount.setAccountKey(key);
        wrapAccount.setAccountNumber("12569845");

        Collection<Document> documents = new ArrayList<>();
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setStatus("Final");
        document.setAudit(Boolean.FALSE);
        document.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        document.setRelationshipId("12569845");
        documents.add(document);

        DocumentKey documentKey = new DocumentKey();
        documentKey.setAccountId("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        DocumentDto documentDto = new DocumentDto();
        documentDto.setStatus("Final");
        documentDto.setAudit(Boolean.FALSE);
        documentDto.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        documentDto.setKey(documentKey);

        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(AccountKey.class), any(ServiceErrors.class))).thenReturn(wrapAccount);
        when(profileService.isServiceOperator()).thenReturn(false);
        when(documentIntegrationService.getDocuments(any(Criteria.class))).thenReturn(documents);
        when(documentDtoService.find(any(DocumentKey.class), any(ServiceErrors.class))).thenReturn(documentDto);
        when(documentDtoConverter.getDocumentDto(any(Document.class), anyString())).thenReturn(documentDto);
        when(documentDtoConverter.getAccount(anyString(), any(ServiceErrors.class))).thenReturn(wrapAccount);
        setProfile();
    }

    @Test
    public void testHasAccountPermission() {
        /*Check for happy path*/
        boolean hasAccess = permissionService.hasAccountViewPermission("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        Assert.assertTrue(hasAccess);

        /*Check if audit FR not present*/
        functionalRoles.remove(FunctionalRole.View_Document_library);
        setProfile();
        hasAccess = permissionService.hasAccountViewPermission("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        Assert.assertFalse(hasAccess);
    }

    @Test
    public void testHasUpdatePermission() {
        DocumentKey key = new DocumentKey();
        key.setAccountId("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        key.setDocumentId("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        DocumentDto dto = new DocumentDto();
        dto.setKey(key);
        dto.setAudit(Boolean.TRUE);

        /*Check for happy path*/
        boolean hasAccess = permissionService.hasUpdatePermission(dto);
        Assert.assertTrue(hasAccess);

        Collection<Document> documents = new ArrayList<>();
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setStatus("Final");
        document.setAudit(Boolean.FALSE);
        document.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        document.setRelationshipId("12569846");//Account number changed
        documents.add(document);
        when(documentIntegrationService.getDocuments((Criteria) Matchers.anyObject())).thenReturn(documents);

        /*Check if document accessibility fails*/
        when(documentDtoConverter.getAccount(anyString(), any(ServiceErrors.class))).thenReturn(null);
        hasAccess = permissionService.hasUpdatePermission(dto);
        Assert.assertFalse(hasAccess);

        /*Check if account accessibility fails*/
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(AccountKey.valueOf("123"), new WrapAccountImpl());
        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        hasAccess = permissionService.hasUpdatePermission(dto);
        Assert.assertFalse(hasAccess);

        /*Check if audit Audit FR not present*/
        functionalRoles.remove(FunctionalRole.Update_document_Audit);
        setProfile();
        hasAccess = permissionService.hasUpdatePermission(dto);
        Assert.assertFalse(hasAccess);

        /*Check if audit Update FR not present*/
        functionalRoles.remove(FunctionalRole.Maintain_document_attributes);
        setProfile();
        hasAccess = permissionService.hasUpdatePermission(dto);
        Assert.assertFalse(hasAccess);
    }

    @Test
    public void testHasUploadPermission() {
        DocumentKey key = new DocumentKey();
        key.setAccountId("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        DocumentDto dto = new DocumentDto();
        dto.setKey(key);
        dto.setAudit(Boolean.TRUE);
        /*Check for happy path*/
        boolean hasAccess = permissionService.hasUploadPermission(dto);
        Assert.assertTrue(hasAccess);

        /*Check if audit Audit FR not present
        //TODO: Uncomment once check included
        functionalRoles.remove(FunctionalRole.Update_document_Audit);
        setProfile();
        hasAccess = permissionService.hasUploadPermission(dto);
        Assert.assertTrue(hasAccess);*/

        /*Check if audit Upload FR not present*/
        functionalRoles.remove(FunctionalRole.Upload_Document);
        setProfile();
        hasAccess = permissionService.hasUploadPermission(dto);
        Assert.assertFalse(hasAccess);

        /*Check if account accessibility fails*/
        functionalRoles.add(FunctionalRole.Update_document_Audit);
        functionalRoles.add(FunctionalRole.Upload_Document);
        setProfile();
        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(AccountKey.class),any(ServiceErrors.class))).thenReturn(null);
        hasAccess = permissionService.hasUploadPermission(dto);
        Assert.assertFalse(hasAccess);
    }

    @Test
    public void testHasUploadNewPermission() {
        DocumentKey key = new DocumentKey();
        key.setAccountId("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        key.setDocumentId("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        DocumentDto dto = new DocumentDto();
        dto.setKey(key);
        dto.setAudit(Boolean.TRUE);

        /*Check for happy path*/
        boolean hasAccess = permissionService.hasUploadNewPermission(dto);
        Assert.assertTrue(hasAccess);

        Collection<Document> documents = new ArrayList<>();
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setStatus("Final");
        document.setAudit(Boolean.FALSE);
        document.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        document.setRelationshipId("12569846");//Account number changed
        documents.add(document);
        when(documentIntegrationService.getDocuments((Criteria) Matchers.anyObject())).thenReturn(documents);

        /*Check if document accessibility fails*/
        when(documentDtoConverter.getAccount(anyString(), any(ServiceErrors.class))).thenReturn(null);
        hasAccess = permissionService.hasUploadNewPermission(dto);
        Assert.assertFalse(hasAccess);

        /*Check if audit Audit FR not present
        //TODO: Uncomment once check included
        functionalRoles.remove(FunctionalRole.Update_document_Audit);
        setProfile();
        hasAccess = permissionService.hasUploadNewPermission(dto);
        Assert.assertFalse(hasAccess);*/

        /*Check if audit Upload FR not present*/
        functionalRoles.remove(FunctionalRole.Upload_Document);
        setProfile();
        hasAccess = permissionService.hasUploadNewPermission(dto);
        Assert.assertFalse(hasAccess);

        /*Check if account accessibility fails*/
        functionalRoles.add(FunctionalRole.Update_document_Audit);
        functionalRoles.add(FunctionalRole.Upload_Document);
        setProfile();
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(AccountKey.valueOf("123"), new WrapAccountImpl());
        when(accountIntegrationService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        hasAccess = permissionService.hasUploadNewPermission(dto);
        Assert.assertFalse(hasAccess);
    }



    @Test
    public void testIsDocumentAccessible() {
        DocumentKey key = new DocumentKey();
        key.setDocumentId("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        boolean hasAccess = permissionService.isDocumentAccessible(key);
        Assert.assertTrue(hasAccess);
        Collection<Document> documents = new ArrayList<>();
        CmisDocumentImpl document = new CmisDocumentImpl();
        document.setStatus("Final");
        document.setAudit(Boolean.FALSE);
        document.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        document.setRelationshipId("12569846");//Account number changed
        documents.add(document);
        when(documentIntegrationService.getDocuments((Criteria) Matchers.anyObject())).thenReturn(documents);
        /*Check if document accessibility fails*/
        when(documentDtoConverter.getAccount(anyString(), any(ServiceErrors.class))).thenReturn(null);
        hasAccess = permissionService.isDocumentAccessible(key);
        Assert.assertFalse(hasAccess);
    }

    @Test
    public void when_only_auditflagUpdated_Or_nothingToUpdate() {
        setProfile();
        DocumentDto dto = new DocumentDto();
        dto.setAudit(true);
        dto.setKey(new DocumentKey());
        dto.setChangeToken("3");
        permissionService.updateAndAuditFunctionalRoleCheck(dto);
        Mockito.verify(profileService, Mockito.times(2)).getActiveProfile();
    }

    @Test
    public void when_only_auditflagUpdated_Or_auditAndOtherProeprtyUdpated() {
        setProfile();
        DocumentDto dto = new DocumentDto();
        dto.setKey(new DocumentKey());
        dto.setFinancialYear("1111");
        dto.setAudit(true);
        Assert.assertTrue(permissionService.updateAndAuditFunctionalRoleCheck(dto));
        Mockito.verify(profileService, Mockito.times(2)).getActiveProfile();

        dto = new DocumentDto();
        dto.setKey(new DocumentKey());
        dto.setFinancialYear("1111");
        Assert.assertTrue(permissionService.updateAndAuditFunctionalRoleCheck(dto));
        Mockito.verify(profileService, Mockito.times(3)).getActiveProfile();
    }


    @Test
    public void when_only_auditflagUpdated_Or_auditAndOtherProeprtyUdpated_withNoAuditPermission() {
        setProfile();
        functionalRoles.remove(FunctionalRole.Update_document_Audit);
        DocumentDto dto = new DocumentDto();
        dto.setKey(new DocumentKey());
        dto.setFinancialYear("1111");
        dto.setAudit(true);
        Assert.assertFalse(permissionService.updateAndAuditFunctionalRoleCheck(dto));
        Mockito.verify(profileService, Mockito.times(1)).getActiveProfile();

    }

    @Test
    public void when_only_auditflagUpdated_Or_auditAndOtherProeprtyUdpated_withNoUpdatePermission() {
        setProfile();
        functionalRoles.remove(FunctionalRole.Maintain_document_attributes);
        DocumentDto dto = new DocumentDto();
        dto.setKey(new DocumentKey());
        dto.setFinancialYear("1111");
        Assert.assertFalse(permissionService.updateAndAuditFunctionalRoleCheck(dto));
        Mockito.verify(profileService, Mockito.times(1)).getActiveProfile();

    }

    @Test
    public void when_only_auditflagUpdated_Or_auditAndOtherProeprtyUdpated_withAuditWitoutUpdatePermission() {
        setProfile();
        functionalRoles.remove(FunctionalRole.Maintain_document_attributes);
        DocumentDto dto = new DocumentDto();
        dto.setKey(new DocumentKey());
        dto.setAudit(true);
        dto.setFinancialYear("1111");
        Assert.assertFalse(permissionService.updateAndAuditFunctionalRoleCheck(dto));
        Mockito.verify(profileService, Mockito.times(2)).getActiveProfile();

    }

    private void setProfile() {
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.ACCOUNTANT);
        UserInformationImpl userInformation = new UserInformationImpl();
        userInformation.setFunctionalRoles(functionalRoles);
        UserProfileAdapterImpl profileAdapter = new UserProfileAdapterImpl(userInformation, jobProfile);
        when(profileService.getActiveProfile()).thenReturn(profileAdapter);
    }

    private void setProfileAsAdviser()
    {
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.ADVISER);
        UserInformationImpl userInformation = new UserInformationImpl();
        userInformation.setFunctionalRoles(functionalRoles);
        UserProfileAdapterImpl profileAdapter = new UserProfileAdapterImpl(userInformation, jobProfile);
        when(profileService.getActiveProfile()).thenReturn(profileAdapter);
    }

    public UserProfile getProfile(JobProfile jobProfile) {
        return new UserProfileAdapterImpl(null, jobProfile);
    }

    @Test
    public void testIsDocumentUserAccessible() {

        String documentId = "402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF";
        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTMENT_MANAGER);
        jobProfile.setPersonJobId("12569846");
        when(profileService.getActiveProfile()).thenReturn(getProfile(jobProfile));
        when(profileService.getPositionId()).thenReturn("12569846");
        Collection<Document> documents = new ArrayList<>();
        CmisDocumentImpl document = new CmisDocumentImpl();

        document.setStatus("Final");
        document.setAudit(Boolean.FALSE);
        document.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        document.setRelationshipId("12569846");
        documents.add(document);

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
        when(documentIntegrationService.getDocuments((Criteria) Matchers.anyObject())).thenReturn(documents);
        when(userDocumentDtoConverter.getDocumentDto(any(Document.class))).thenReturn(documentDto1);
        boolean hasAccess = permissionService.isDocumentUserAccessible(documentId);
        Assert.assertTrue(hasAccess);
        document.setRelationshipId("1269846");

        hasAccess = permissionService.isDocumentUserAccessible(documentId);
        Assert.assertFalse(hasAccess);
    }

    @Test
    public void testUserPositionViewPermission(){

        JobProfileImpl jobProfile = new JobProfileImpl();
        jobProfile.setJobRole(JobRole.INVESTMENT_MANAGER);
        jobProfile.setPersonJobId("12569846");
        when(profileService.getPositionId()).thenReturn("12569846");
        when(profileService.getActiveProfile()).thenReturn(getProfile(jobProfile));
        Assert.assertTrue(permissionService.hasUserPositionViewPermission("12569846"));
        Assert.assertFalse(permissionService.hasUserPositionViewPermission("12345"));

    }


}