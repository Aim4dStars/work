package com.bt.nextgen.api.statements.permission;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.api.statements.service.DocumentDtoConverter;
import com.bt.nextgen.api.statements.service.DocumentRoleMatcher;
import com.bt.nextgen.api.statements.service.FilterBuilder;
import com.bt.nextgen.api.statements.service.UserDocumentDtoConverter;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.bt.nextgen.service.integration.financialdocument.DocumentIntegrationService;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * This class checks the permission for document library. This extra implementation is required because data level permissions
 * are not present in backend (Filenet) system as compare to Avaloq.
 */
@Component("docPermissionService")
public class DocumentPermissionService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentPermissionService.class);

    @Autowired
    private UserProfileService profileService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private DocumentIntegrationService documentIntegrationService;

    @Autowired
    private DocumentDtoConverter documentDtoConverter;

    @Autowired
    private UserDocumentDtoConverter userDocumentDtoConverter;

    /**
     * Method checks for following conditions:
     * 1) Logged in user has account access.
     * 2) Logged in user has FR for View documents ($FR_DOCU_VIEW)
     * For service operator this check is not required.
     *
     * @param accountId
     * @return true if logged in user has access to account else false
     */
    public boolean hasAccountViewPermission(String accountId) {
        if (accountId != null) {
            AccountKey accountKey = AccountKey.valueOf(new EncodedString(accountId).plainText());
            WrapAccount account = accountService.loadWrapAccountWithoutContainers(accountKey, new FailFastErrorsImpl());
            return account != null && hasFunctionalRole(FunctionalRole.View_Document_library);
        } else {
            return false;
        }
    }

    /**
     * Method checks for following conditions:
     * 1) Logged in user has account access.
     * 2) Logged in user can view the document (Viability rules applied) @See DocumentRoleMatcher.
     * 3) Logged in user has FR for update metadata ($FR_DOCU_MAINT)
     * 4) If audit flag is part of the request check permission against audit flag update FR ($FR_DOCU_AUD_FLAG)
     * The above check is not done if logged in person is service operator who is not emulating, when emulating they are not allowed to
     * upload new version of the document.
     *
     * @param dto
     * @return true if person has update access else false.
     */
    public boolean hasUpdatePermission(DocumentDto dto) {
        if (!profileService.isEmulating()) {
            if (!profileService.isServiceOperator()) {
                if (isDocumentAccessible(dto.getKey())) {
                    return updateAndAuditFunctionalRoleCheck(dto);
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Method checks for following conditions:
     * 1) Logged in user has account access.
     * 2) Logged in user has FR for upload ($FR_DOCU_UPLD)
     * The above check is not done if logged in person is service operator who is not emulating, when emulating they are not allowed to
     * upload document and return false.
     *
     * @param dto
     * @return true if person has update access else false.
     */
    public boolean hasUploadPermission(DocumentDto dto) {
        if (!profileService.isEmulating()) {
            if (!profileService.isServiceOperator()) {
                return hasAccountViewPermission(dto.getKey().getAccountId())
                        && hasFunctionalRole(FunctionalRole.Upload_Document);
            }
            return true;
        }
        return false;
    }

    /**
     * Method checks for following conditions:
     * 1) Logged in user has account access.
     * 2) Logged in user has FR for upload new version ($FR_DOCU_UPLD)
     * 3) Logged in user can view the document (Viability rules applied) @See DocumentRoleMatcher.
     * The above check is not done if logged in person is service operator who is not emulating, when emulating they are not allowed to
     * upload new version of the document.
     *
     * @param dto
     * @return true if person has update access else false.
     */
    public boolean hasUploadNewPermission(DocumentDto dto) {
        if (!profileService.isEmulating()) {
            if (!profileService.isServiceOperator()) {
                return  hasFunctionalRole(FunctionalRole.Upload_Document)
                        && isDocumentAccessible(dto.getKey());
            }
            return true;
        }
        return false;
    }


    /**
     * Method checks for following conditions:
     * 1) Logged in user has account access.
     * 2) Logged in user has FR for update audit flag
     * 3) Logged in user can view the document (Viability rules applied) @See DocumentRoleMatcher.
     * The above check is not done if logged in person is service operator who is not emulating, when emulating they are not allowed to
     * upload new version of the document.
     *
     * @param key
     * @return true if person has update access else false.
     */
    public boolean isDocumentAccessible(DocumentKey key) {
        Document document = getDocumentInfo(key);
        if (document != null) {
            String accountNumber = document.getRelationshipId();
            WrapAccount account = getAccount(accountNumber);
            if (account != null && hasAccountViewPermission(EncodedString.fromPlainText(account.getAccountKey().getId()).toString())) {
                key.setAccountId(EncodedString.fromPlainText(account.getAccountKey().getId()).toString());
                return isDocumentVisible(document, key);
            }
        }
        return false;
    }

    /**
     * Method checks for following conditions:
     * 1) Logged in user has account access.
     * 2) Logged in user has FR for update audit flag
     * 3) Logged in user can view the document (Viability rules applied) @See DocumentRoleMatcher.
     * The above check is not done if logged in person is service operator who is not emulating, when emulating they are not allowed to
     * upload new version of the document.
     *
     * @param documentIds
     * @return true if person has update access else false.
     */
    public boolean isDocumentAccessible(String... documentIds) {
        FilterBuilder filterUtil = new FilterBuilder(profileService, documentIntegrationService.getParentFolderId());
        Collection<Document> documents = documentIntegrationService.getDocuments(filterUtil.createCriteriaForDocumentInfo(documentIds));
        for (Document document : documents) {
            if (document != null) {
                String accountNumber = document.getRelationshipId();
                WrapAccount account = getAccount(accountNumber);
                if (account != null && hasAccountViewPermission(EncodedString.fromPlainText(account.getAccountKey().getId()).toString())) {
                    DocumentKey key = new DocumentKey();
                    key.setDocumentId(EncodedString.fromPlainText(document.getDocumentKey().getId()).plainText());
                    key.setAccountId(EncodedString.fromPlainText(account.getAccountKey().getId()).toString());
                    if (!isDocumentVisible(document, key)) {
                        break;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private Document getDocumentInfo(DocumentKey key) {
        Document document = null;
        FilterBuilder filterUtil = new FilterBuilder(profileService, documentIntegrationService.getParentFolderId());
        Collection<Document> documentList = documentIntegrationService.getDocuments(filterUtil.createCriteriaForDocumentInfo(key));
        if (!documentList.isEmpty()) {
            document = documentList.iterator().next();
        }
        return document;
    }

    private WrapAccount getAccount(String accountNumber) {
        return documentDtoConverter.getAccount(accountNumber,new ServiceErrorsImpl());
    }

    private boolean hasFunctionalRole(FunctionalRole functionalRoles) {
        return profileService.getActiveProfile().getFunctionalRoles().contains(functionalRoles);
    }

    private boolean isDocumentVisible(Document document, DocumentKey key) {
        DocumentDto dto = documentDtoConverter.getDocumentDto(document, key.getAccountId());
        DocumentRoleMatcher documentVisibilityUtil = new DocumentRoleMatcher(profileService);
        DocumentRequestManager.addDocument(dto);
        return documentVisibilityUtil.isDocumentVisible(dto);
    }


    /**
     * Method checks the bean properties for FR against update and updateAudit flag.
     *
     * @param dto
     * @return
     */
    public boolean updateAndAuditFunctionalRoleCheck(DocumentDto dto) {
        try {
            Map<String, String> properties = PropertyUtils.describe(dto);
            Set<Map.Entry<String, String>> entries = properties.entrySet();
            if (dto.getAudit() != null && !hasFunctionalRole(FunctionalRole.Update_document_Audit)) {
                return false;
            }
            for (Map.Entry<String, String> entry : entries) {
                if (!ignoreProperty(dto, entry)) {
                    return hasFunctionalRole(FunctionalRole.Maintain_document_attributes);
                }
            }
            return true;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Error while permission check." + e);
            ServiceErrors serviceErrors = new ServiceErrorsImpl();
            serviceErrors.addError(new ServiceErrorImpl(e.getMessage()));
            throw new ServiceException(ApiVersion.CURRENT_VERSION, serviceErrors);
        }
    }

    private boolean ignoreProperty(DocumentDto dto, Map.Entry<String, String> entry) {
        if (PropertyUtils.isWriteable(dto, entry.getKey()) && entry.getValue() != null) {
            return "key".equals(entry.getKey()) || "changeToken".equals(entry.getKey()) || "audit".equals(entry.getKey());
        }
        return true;
    }

    public boolean hasDeletePermission(DocumentKey key) {
        if(!profileService.isEmulating()&&!profileService.isServiceOperator()) {
                return hasFunctionalRole(FunctionalRole.Delete_Document)
                        &&isDocumentDeletable(key);
        }
        return false;
    }

    private boolean isDocumentDeletable(DocumentKey key) {
        if(isDocumentAccessible(key.getDocumentId())) {
            DocumentDto dto = DocumentRequestManager.getDocument(key.getDocumentId());
            if(dto != null) {
                return dto.isDeletable();
            }
        }
        return false;
    }

    public boolean hasUserPositionViewPermission(String userPositionId) {

        String positionId =profileService.getPositionId();

        if (userPositionId != null && userPositionId.equals(positionId)) {
            return true;
        }
       return false;
    }

    private boolean isDocumentUserVisible(Document document) {
        DocumentDto dto = userDocumentDtoConverter.getDocumentDto(document);
        DocumentRoleMatcher documentVisibilityUtil = new DocumentRoleMatcher(profileService);
        DocumentRequestManager.addDocument(dto);
        return documentVisibilityUtil.isDocumentVisible(dto);
    }



    public boolean isDocumentUserAccessible(String... documentIds) {

        FilterBuilder filterUtil = new FilterBuilder(profileService, documentIntegrationService.getParentFolderId());
        Collection<Document> documents = documentIntegrationService.getDocuments(filterUtil.createCriteriaForUserDocumentInfo(documentIds));
        for (Document document : documents) {
            if (document != null) {
                String userPositionId = document.getRelationshipId();

                if (userPositionId != null && hasUserPositionViewPermission(userPositionId)) {
                     if (!isDocumentUserVisible(document)) {
                        break;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

}
