package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.decorator.DeletableDecorator;
import com.bt.nextgen.api.statements.decorator.DocumentDtoCompressor;
import com.bt.nextgen.api.statements.decorator.DtoDecorator;
import com.bt.nextgen.api.statements.decorator.FinancialDocumentDecorator;
import com.bt.nextgen.api.statements.decorator.FinancialYearDecorator;
import com.bt.nextgen.api.statements.decorator.FundAdminDecorator;
import com.bt.nextgen.api.statements.decorator.KeyDecorator;
import com.bt.nextgen.api.statements.decorator.NameDecorator;
import com.bt.nextgen.api.statements.decorator.RoleDecorator;
import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.cmis.CmisDocumentImpl;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.cmis.constants.DocumentSubCategories;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.financialdocument.Document;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class is used to convert Dto objects(documentDto) to Domain Objects(document) and vice versa .
 */

@Component
@SuppressWarnings("squid:S1200")
public class DocumentDtoConverter {

    @Autowired
    private CmsService cmsService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private BrokerHelperService brokerService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    public List<DocumentDto> getDocumentDtoList(Collection<Document> documents, String encodedKey) {
        List<DocumentDto> documentDtoList = new ArrayList<DocumentDto>();
        for (Document document : documents) {
            DocumentDto documentDto = getDocumentDto(document, encodedKey);
            documentDtoList.add(documentDto);
        }
        return documentDtoList;
    }

    /**
     * This method converts the Document obtained from Documentum into DocumentDto for display in UI
     *
     * @param document
     * @param accountId
     * @return
     */
    //Suppressing  - Restricts the number of executable statements to a specified limit (default = 30).
    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck")
    public DocumentDto getDocumentDto(Document document, String accountId) {
        DocumentDto documentDto = new DocumentDto();
        DtoDecorator decorator = createDecorator(document, accountId, documentDto);

        documentDto = decorator.decorate();
        documentDto.setDocumentType(document.getDocumentType());
        documentDto.setUploadedDate(document.getUploadedDate());
        documentDto.setSize(document.getSize());
        documentDto.setStatus(StringUtils.isEmpty(document.getStatus()) ? DocumentConstants.DOCUMENT_STATUS_FINAL : document.getStatus());
        documentDto.setAudit(document.getAudit());
        documentDto.setFileType(document.getMimeType());
        documentDto.setChangeToken(document.getChangeToken());
        documentDto.setDocumentSubType(document.getDocumentSubType());
        documentDto.setDocumentTitleCode(document.getDocumentTitleCode());
        documentDto.setFileName(document.getFileName());

        documentDto.setBusinessArea(document.getPanoramaipBusinessArea());
        documentDto.setRelationshipId(document.getRelationshipId());
        documentDto.setRelationshipType(document.getPanoramaipRelationshipType());
        documentDto.setAbcOrderId(document.getOrderId());
        documentDto.setUploadedBy(document.getUploadedBy());
        documentDto.setSourceId(document.getSourceId());
        documentDto.setVisible(document.getVisibility() == null ? null : document.getVisibility().intValue());
        documentDto.setActivity(document.getActivity());
        documentDto.setAddedByName(document.getAddedByName());
        documentDto.setBatchId(document.getBatchId());
        documentDto.setExpiryDate(document.getExpiryDate());
        documentDto.setExternalId(document.getExternalId());
        documentDto.setDocumentSubType2(document.getDocumentSubType2());
        documentDto.setModelReportId(document.getModelReportId());
        documentDto.setStartDate(document.getStartDate());
        documentDto.setEndDate(document.getEndDate());
        documentDto.setDocumentTypeLabel(DocumentCategories.forCode(document.getDocumentType()).getDisplayName());
        documentDto.setSoftDeleted((!StringUtils.isEmpty(document.getDeleted()) && "Y".equalsIgnoreCase(document.getDeleted())) ? true : false);
        documentDto.setPermanent(!StringUtils.isEmpty(document.getPermanent()) && "Y".equalsIgnoreCase(document.getPermanent()) ? true : false);
        this.populateAuditFields(document, documentDto);
        documentDto.setLastModificationDate(document.getLastModificationDate());
        if (null != document.getDocumentSubType2())
            documentDto.setDocumentSubType2Label(DocumentSubCategories.forCode(document.getDocumentSubType2()).getDisplayName());
        return documentDto;
    }

    private DtoDecorator createDecorator(Document document, String accountId, DocumentDto documentDto) {
        final AccountKey accountKey;
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        WrapAccount wrapAccount = null;
        UserExperience userExperience = null;
        DtoDecorator decorator;
        if (profileService.isServiceOperator()) {
            decorator = new KeyDecorator(document.getDocumentKey().getId(), accountId, documentDto);
        } else {
            decorator = new KeyDecorator(document.getDocumentKey().getId(), getDecodedString(accountId), documentDto);
            accountKey = AccountKey.valueOf(getDecodedString(accountId));
            wrapAccount = accountService.loadWrapAccount(accountKey, serviceErrors);
            userExperience = brokerService.getUserExperience(wrapAccount, serviceErrors);
        }

        decorator = new NameDecorator(decorator, documentDto, document);
        decorator = new FinancialYearDecorator(decorator, documentDto, document);
        decorator = new RoleDecorator(decorator, documentDto, document);
        decorator = new FinancialDocumentDecorator(documentDto, document, decorator, wrapAccount, userExperience);
        decorator = new DeletableDecorator(documentDto, decorator, document, profileService.getActiveProfile().getJobRole());
        decorator = new FundAdminDecorator(cmsService, documentDto, document, decorator);
        return decorator;
    }

    public Document getDocumentToUpdate(DocumentDto documentDto) {
        CmisDocumentImpl document = new CmisDocumentImpl();
        com.bt.nextgen.service.integration.financialdocument.DocumentKey docKey = com.bt.nextgen.service.integration.financialdocument.DocumentKey.valueOf(
                getDecodedString(documentDto.getKey().getDocumentId()));
        document.setDocumentKey(docKey);
        document.setAudit(documentDto.getAudit());
        document.setStatus(documentDto.getStatus());
        document.setDocumentName(documentDto.getDocumentName());
        document.setFinancialYear(documentDto.getFinancialYear());
        document.setDocumentType(documentDto.getDocumentType());
        document.setChangeToken(documentDto.getChangeToken());

        document.setRelationshipId(documentDto.getRelationshipId());
        document.setOrderId(documentDto.getAbcOrderId());
        document.setSourceId(documentDto.getSourceId());
        document.setBatchId(documentDto.getBatchId());
        document.setModelReportId(documentDto.getModelReportId());
        document.setExternalId(documentDto.getExternalId());
        document.setDocumentSubType(documentDto.getDocumentSubType());
        document.setDocumentTitleCode(documentDto.getDocumentTitleCode());
        document.setPanoramaipRelationshipType(documentDto.getRelationshipType());
        document.setExpiryDate(documentDto.getExpiryDate());
        document.setStartDate(documentDto.getStartDate());
        document.setEndDate(documentDto.getEndDate());
        document.setDocumentSubType2(documentDto.getDocumentSubType2());
        document.setUpdatedByRole(VisibilityRoles.forRole(profileService.getActiveProfile().getJobRole()).getDescription());
        document.setUpdatedByID(profileService.getActiveProfile().getBankReferenceId());
        document.setUpdatedByName(profileService.getFullName());

        if (profileService.isServiceOperator()) {
            document = populateDeleteRestoreFields(document, documentDto.isSoftDeleted(), documentDto.isRestoredDeleted());
            if (StringUtils.isNotBlank(String.valueOf(documentDto.isPermanent()))) {
                document.setPermanent(documentDto.isPermanent() ? "Y" : "N");
            }

        }

        return document;
    }

    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck")
    public Document getDocument(DocumentDto documentDto) {
        final String accountNumber;
        if (!profileService.isServiceOperator() && "OFFAPR".equals(documentDto.getDocumentTitleCode())) {
            accountNumber = getDecodedString(documentDto.getRelationshipId());
        } else {
            accountNumber = getAccountNumber(documentDto.getKey(), new ServiceErrorsImpl());
        }
        CmisDocumentImpl document = new CmisDocumentImpl();
        if (documentDto.getKey() != null && documentDto.getKey().getDocumentId() != null) {
            com.bt.nextgen.service.integration.financialdocument.DocumentKey docKey =
                    com.bt.nextgen.service.integration.financialdocument.DocumentKey.valueOf(getDecodedString(documentDto.getKey().getDocumentId()));
            document.setDocumentKey(docKey);
        }
        document.setRelationshipId(accountNumber);
        document.setAudit(documentDto.getAudit() != null ? documentDto.getAudit() : Boolean.FALSE);
        document.setStatus(documentDto.getStatus());
        document.setDocumentName(documentDto.getDocumentName());
        document.setSize(documentDto.getSize());
        //Business required ServiceUI for source ID if document uploaded by service desk.
        if (profileService.isServiceOperator()) {
            document.setPanoramaipRelationshipType(documentDto.getRelationshipType());
            document.setSourceId(DocumentConstants.SERVICE_SOURCE_ID);
        } else {
            document.setPanoramaipRelationshipType(Constants.RELATIONSHIP_TYPE_ACCOUNT);
            document.setSourceId(DocumentConstants.SOURCE_ID);
        }
        document.setPanoramaipBusinessArea(DocumentConstants.BUSINESS_AREA_PANORAMA);
        document.setUploadedRole(VisibilityRoles.forRole(profileService.getActiveProfile().getJobRole()).getDescription());
        document.setUploadedBy(profileService.getActiveProfile().getBankReferenceId());
        if (documentDto.getFileType() != null) {
            document.setMimeType(documentDto.getFileType());
        }
        document.setFinancialYear(StringUtils.isEmpty(documentDto.getFinancialYear()) ? StringUtils.EMPTY : documentDto.getFinancialYear());
        document.setDocumentType(documentDto.getDocumentType());
        document.setData(documentDto.getDocumentBytes());
        document.setFileExtension(documentDto.getFileExtension() != null ? documentDto.getFileExtension().toUpperCase() : null);
        document.setAddedByName(profileService.getFullName());
        document.setChangeToken(documentDto.getChangeToken());
        document.setDocumentSubType(documentDto.getDocumentSubType());
        document.setFileName(documentDto.getFileName());
        document.setDocumentTitleCode(documentDto.getDocumentTitleCode());
        document.setDocumentSubType2(documentDto.getDocumentSubType2());
        document.setOrderId(documentDto.getAbcOrderId());
        document.setExternalId(documentDto.getExternalId());
        return document;
    }

    public DocumentDto loadDocumentZipped(List<DocumentDto> documents, ServiceErrors serviceErrors) {
        DocumentDtoCompressor compressor = new DocumentDtoCompressor(documents);
        DocumentDto dto = compressor.getDto();
        DocumentKey key = documents.listIterator().next().getKey();
        WrapAccount wrapAccount = getWrapAccount(key, serviceErrors);
        String zipFileName = compressor.getCreateName(wrapAccount.getAccountNumber(), wrapAccount.getAccountName());
        dto.setFileName(zipFileName);
        return dto;
    }

    public WrapAccount getWrapAccount(DocumentKey documentKey, ServiceErrors serviceErrors) {
        String encodedAcctId = documentKey.getAccountId();
        com.bt.nextgen.service.integration.account.AccountKey accountKey = AccountKey.valueOf(getDecodedString(encodedAcctId));
        return accountService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);

    }

    public String getAccountNumber(DocumentKey documentKey, ServiceErrors serviceErrors) {
        //TODO - will be changed with SMSF code
        if (profileService.isServiceOperator()) {
            return documentKey.getAccountId();
        } else {
            WrapAccount account = getWrapAccount(documentKey, serviceErrors);
            return account.getAccountNumber();
        }
    }

    public WrapAccount getAccount(String accountNumber, ServiceErrors serviceErrors) {
        return accountService.loadWrapAccountByAccountNumber(accountNumber, serviceErrors);
    }

    public List<DocumentDto> filteredDocuments(List<DocumentDto> documentDtoList, String queryString) {
        List<DocumentDto> filteredList = new ArrayList<>();
        for (DocumentDto documentDto : documentDtoList) {
            if (isTextExist(queryString, documentDto.getDocumentName())) {
                filteredList.add(documentDto);
            }
        }
        return filteredList;
    }

    private boolean isTextExist(String pattern, String value) {
        String patternWithLowerCase = pattern.toLowerCase();
        String valueWithLowerCase = value.toLowerCase();
        return valueWithLowerCase.contains(patternWithLowerCase);
    }

    public String getDecodedString(String inputValue) {
        return new EncodedString(inputValue).plainText();
    }

    public Document getMetaDataFieldsForDelete(DocumentKey key) {
        CmisDocumentImpl document = new CmisDocumentImpl();
        String documentId = new EncodedString(key.getDocumentId()).plainText();
        document.setDocumentKey(com.bt.nextgen.service.integration.financialdocument.DocumentKey.valueOf(documentId));
        document = populateDeleteRestoreFields(document, true, false);
        return document;
    }

    public Document getMetaDataFieldsForRestore(DocumentKey key) {

        CmisDocumentImpl document = new CmisDocumentImpl();
        String documentId = new EncodedString(key.getDocumentId()).plainText();
        document.setDocumentKey(com.bt.nextgen.service.integration.financialdocument.DocumentKey.valueOf(documentId));
        document = populateDeleteRestoreFields(document, false, true);

        return document;
    }

    private CmisDocumentImpl populateDeleteRestoreFields(CmisDocumentImpl document, boolean isDeleted, boolean isRestored) {
        if (isDeleted) {
            document.setDeletedByUserId(profileService.getActiveProfile().getBankReferenceId());
            document.setDeletedOn(new DateTime());
            document.setDeletedByRole(VisibilityRoles.forRole(profileService.getActiveProfile().getJobRole()).getDescription());
            document.setDeletedByName(profileService.getFullName());
            document.setDeleted("Y");

        } else if (isRestored) {

            document.setRestoredByUserId(profileService.getActiveProfile().getBankReferenceId());
            document.setRestoredOn(new DateTime());
            document.setRestoreByRole(VisibilityRoles.forRole(profileService.getActiveProfile().getJobRole()).getDescription());
            document.setRestoreByName(profileService.getFullName());
            document.setDeleted("");
        }

        return document;
    }

    private void populateAuditFields(Document document, DocumentDto documentDto) {
        documentDto.setUpdatedByID(document.getUpdatedByID());
        documentDto.setUpdatedByName(document.getUpdatedByName());
        documentDto.setUpdatedByRole(document.getUpdatedByRole());

        documentDto.setDeletedByUserId(document.getDeletedByUserId());
        documentDto.setDeletedByName(document.getDeletedByName());
        documentDto.setDeletedByRole(document.getDeletedByRole());
        documentDto.setDeletedOn(document.getDeletedOn());

        documentDto.setRestoredByUserId(document.getRestoredByUserId());
        documentDto.setRestoreByName(document.getRestoreByName());
        documentDto.setRestoreByRole(document.getRestoreByRole());
        documentDto.setRestoredOn(document.getRestoredOn());
    }
}