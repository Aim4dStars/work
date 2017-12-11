package com.bt.nextgen.api.statements.validation;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.SupportedDocumentType;
import com.bt.nextgen.api.statements.service.DocumentDtoService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Custom validation for Document Upload and upload new version. This implementationis required due to filenet, can not
 * enforce the business rules - given filenet has multiple system uploading files in it and every system has it's own
 * business rules or limitations eg. (HP which generates files from xml data provided by avaloq and uploads it in filnet)
 */
@Component
public class DocumentDtoValidator {

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private DocumentDtoService documentDtoService;

    public void validate(DocumentDto documentDto) {
        List<DomainApiErrorDto> errorList = new ArrayList<>();
        if (checkForNullValues(documentDto, errorList)) {
            if (!checkForFileType(documentDto)) {
                errorList.add(new DomainApiErrorDto(null, null, "The File Extension is out of the scope "));
            }
            if (!checkForStatementTypeDocuments(documentDto)) {
                errorList.add(new DomainApiErrorDto(null, null, "Title Code is mandatory for Statement type"));
            }
            if (!checkForDocumentSubType(documentDto)) {
                errorList.add(new DomainApiErrorDto(null, null, "There is mismatch between Document Type and Document Sub Type"));
            }
            if (!checkForDocumentCategory(documentDto)) {
                errorList.add(new DomainApiErrorDto(null, null, "User uploading invalid document"));
            }
            checkForDuplicateFileName(documentDto, errorList);
        }
        documentDto.setWarnings(errorList);
    }

    /**
     * Method checks for following conditions:
     * 1) Logged in user Role has the access to DocumentCategories specified for that  Role
     * For service operator this check is not required.
     * @param documentDto
     * @return false if logged in user has access to DocumentCategory else true
     */
    public boolean checkForDocumentCategory(DocumentDto documentDto) {
        if (!profileService.isServiceOperator() || (documentDto.getKey() == null || documentDto.getKey().getDocumentId() == null)) {
            VisibilityRoles role = VisibilityRoles.forRole(profileService.getActiveProfile().getJobRole());
            List<String> accessibleCategories = role.getSupportedCategories();
            DocumentCategories documentCategory = DocumentCategories.forCode(documentDto.getDocumentType());
            return accessibleCategories.contains(documentCategory.getCode());
        }
        return true;
    }
    /**
     * Method checks for following conditions:
     * 1) Wheather the DocumentSubType contained in the object belongs to it's parent DocumentType Or Not
     * 2)For Ex: We cannot have document subtype as "Company" For "INVESTMENTS" DocType
     * @param documentDto
     * @return false if DocumentSubType is of its Parent DocumentType only else true
     */
    public boolean checkForDocumentSubType(DocumentDto documentDto) {
        if (StringUtil.isNotNullorEmpty(documentDto.getDocumentSubType())) {
            DocumentCategories documentCategories = DocumentCategories.forCode(documentDto.getDocumentType());
            return documentCategories != null && documentCategories.getSubCatogories() != null &&
                    documentCategories.getSubCatogories().contains(documentDto.getDocumentSubType());
        }
        return true;
    }

    /**
     * Method checks for following conditions:
     * 1) Wheather the Statement Type Documents has DocumentTitleCode or Not
     * @param documentDto
     * @return false if DocumentTitleCode value is null else true
     */
    public boolean checkForStatementTypeDocuments(DocumentDto documentDto) {
        return !DocumentCategories.STATEMENTS.getCode().equals(documentDto.getDocumentType()) ||
                (profileService.isServiceOperator() &&
                        StringUtil.isNotNullorEmpty(documentDto.getDocumentTitleCode()));
    }
    /**
     * Method checks for following conditions:
     * 1) Wheather the FileType of the document equals predefined set of FileTypes mentioned in SupportedDocumentTypeFile
     * @param documentDto
     * @return false if the DocumentFiletype is not present in supportedDocumentType file  else true
     */
    public boolean checkForFileType(DocumentDto documentDto) {
        SupportedDocumentType fileExtension = SupportedDocumentType.getFileExtension(documentDto.getDocumentName());
        for (SupportedDocumentType documentType : SupportedDocumentType.values()) {
            if (documentType.equals(fileExtension) && !SupportedDocumentType.DEFAULT.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method checks for following conditions:
     * 1) Queries the FileNet For the Document of the same Name and if the duplicate name exists in the same Document Category
     * and the existed  document is not the previous version of the current document then the method throws the error otherwise not
     * @param dto,errorList

     */
    public void checkForDuplicateFileName(DocumentDto dto, List<DomainApiErrorDto> errorList) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        if (dto.getDocumentType() != null) {
            List<DocumentDto> documents = documentDtoService.getDocumentsForDuplicateNameCheck(dto, serviceErrors);
            if (!documents.isEmpty()) {
                if (dto.getKey().getDocumentId() != null) {
                    errorList.add(new DomainApiErrorDto(null, null, "Err.IP-0477"));
                } else {
                    errorList.add(new DomainApiErrorDto(null, null, "Err.IP-0460"));
                }
            }
        }
    }
    /**
     * Method checks for following conditions:
     * 1) checks for any null values in the object . if any null values present then it throws error
     * @param dto,errorList
     * @return false if any null values present in the input object  else true
     */
    public boolean checkForNullValues(DocumentDto dto, List<DomainApiErrorDto> errorList) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<DocumentDto>> constraintViolations = validator.validate(dto);
        for (ConstraintViolation<DocumentDto> violation : constraintViolations) {
            errorList.add(new DomainApiErrorDto(null, null, violation.getPropertyPath() + " " + violation.getMessage()));
        }
        return CollectionUtils.isEmpty(errorList);
    }
}
