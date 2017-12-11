package com.bt.nextgen.core.web.controller;

import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.preparer.ViewPreparerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bt.nextgen.core.security.UserRole;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.model.PersonInterface;
import com.bt.nextgen.core.web.model.ServiceOperator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.integration.userinformation.JobPermission;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.web.controller.cash.util.Attribute;

/**
 * This Object will add in any common information (Global elements) to the model, such as user or reference data.
 * <p>
 * This should only be used for Service Operator pages, so is being refactored to reflect this.
 * Investor and Adviser pages should fall under the Single Page Application model -- (and do not utilise Tiles)
 * </p>
 */
@Component
public class GlobalElementPreparer extends ViewPreparerSupport {
    private static final Logger logger = LoggerFactory.getLogger(GlobalElementPreparer.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserInformationIntegrationService userInformationIntegrationService;

    @Override
    public void execute(TilesRequestContext tilesContext, AttributeContext attributeContext) {
        // this must always work, otherwise we'll have issues on error pages (ie http.500)
        // this crude way will ensure that this always returns.
        try {
            logger.debug("execute{}");
            ServiceErrors errors = new ServiceErrorsImpl();
            addUserInformation(tilesContext.getRequestScope(), errors);
            addEmulationData(tilesContext.getRequestScope());
        } catch (Exception e) {
            logger.warn("There was a problem loading the global elements will have mixed results", e);
        }
    }

    private void addUserInformation(Map<String, Object> mapModel, ServiceErrors errors) {
        PersonInterface person;

        person = new ServiceOperator();
        String displayName = "";

        if (!StringUtils.isEmpty(userProfileService.getUserId())) {
            displayName = userProfileService.getUserId();
        }

        person.setUserName(displayName);

        mapModel.put(Attribute.DEALER_GROUP, "");
        mapModel.put(Attribute.PERSON_MODEL, person);
        mapModel.put(Attribute.IS_DEALERGROUP, false);
        mapModel.put(Attribute.TRUSTEE_APPROVAL_ACCESS, hasUserRole(errors, UserRole.TRUSTEE_BASIC, UserRole.TRUSTEE_READ_ONLY));
        mapModel.put(Attribute.IRG_APPROVAL_ACCESS, hasUserRole(errors, UserRole.IRG_BASIC, UserRole.IRG_READ_ONLY));
    }

    private void addEmulationData(Map<String, Object> mapModel) {
        mapModel.put(Attribute.IS_EMULATING, userProfileService.isEmulating());
    }

    private boolean hasUserRole(ServiceErrors serviceErrors, UserRole... userRoles) {
        List<JobProfile> jobProfileList = userProfileService.getAvailableProfiles();

        if (jobProfileList != null) {
            for (JobProfile jobProfile : jobProfileList) {
                JobPermission jobPermission = userInformationIntegrationService.getAvailableRoles(jobProfile, serviceErrors);

                if (jobPermission.getUserRoles() != null) {
                    for (String roleName : jobPermission.getUserRoles()) {
                        UserRole userRole = UserRole.forAvaloqRole(roleName);
                        for (UserRole role : userRoles) {
                            if (userRole.equals(role)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
