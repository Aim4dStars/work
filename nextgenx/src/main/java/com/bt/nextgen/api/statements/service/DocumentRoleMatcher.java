package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;

/**
 *This class is used set the criteria on the type of document to be uploaded by a User.
 * It checks wheather a User (For ex:Adviser) has the access to upoad a document type
 */
public class DocumentRoleMatcher extends LambdaMatcher<DocumentDto> {

    private UserProfileService profileService;

    public DocumentRoleMatcher(UserProfileService profileService) {
        this.profileService = profileService;
    }

    public boolean isDocumentVisible(DocumentDto documentDto) {
        return matchesSafely(documentDto);
    }

    private boolean isVisibleByStatus(DocumentDto documentDto) {
        VisibilityRoles documentRole = VisibilityRoles.valueOf(documentDto.getUploadedRole().toUpperCase());
        VisibilityRoles uploadedRole = VisibilityRoles.forRole(profileService.getActiveProfile().getJobRole());
        return documentRole.equals(uploadedRole);
    }

    @Override
    protected boolean matchesSafely(DocumentDto documentDto) {
        if (!profileService.isServiceOperator() && DocumentConstants.DOCUMENT_STATUS_DRAFT.equals(documentDto.getStatus())) {
            return isVisibleByStatus(documentDto);
        }
        return true;
    }
}