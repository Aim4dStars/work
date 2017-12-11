package com.bt.nextgen.core.security.profile;

import com.btfin.panorama.service.integration.broker.Broker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by L062329 on 18/08/2015.
 */
public class FindDealerGroup {

    private static final Logger logger = LoggerFactory.getLogger(FindDealerGroup.class);

    private UserProfileServiceSpringImpl profileService = null;

    public FindDealerGroup(UserProfileServiceSpringImpl profileService) {
        this.profileService = profileService;
    }

    /* *********************************************************
    * Convenience methods from UserProfileService interface implementation
	***********************************************************/
    public Broker getDealerGroupBroker() {
        com.btfin.panorama.core.security.profile.UserProfile userProfile = profileService.getActiveProfile();
        isValidProfile(userProfile);
        switch (userProfile.getJobRole()) {
            case ADVISER:
            case ASSISTANT:
            case DEALER_GROUP_MANAGER:
            case PRACTICE_MANAGER:
            case PARAPLANNER:
                return profileService.getDealerGroupForIntermediary();
            case INVESTOR:
                return profileService.getDealerGroupForInvestor();
            default:
                logger.warn("This user does not have a dealer group");
        }
        return null;
    }

    private boolean isValidProfile(com.btfin.panorama.core.security.profile.UserProfile userProfile) {
        if (profileService.isLoggedIn() && userProfile == null || userProfile.getJobRole() == null) {
            throw new IllegalStateException("Could not load the status of the current logged in user");
        }
        return false;
    }

    public String getDealerGroupBrandSilo(){
        com.btfin.panorama.core.security.profile.UserProfile userProfile = profileService.getActiveProfile();
        isValidProfile(userProfile);
        switch (userProfile.getJobRole()) {
            case ADVISER:
            case ASSISTANT:
            case DEALER_GROUP_MANAGER:
            case PRACTICE_MANAGER:
            case PARAPLANNER:
                return profileService.getBrandSiloForIntermediary();
            case INVESTOR:
                return profileService.getBrandSiloForInvestor();
            default:
                logger.warn("This user does not have a dealer group");
        }
        return null;
    }
}
