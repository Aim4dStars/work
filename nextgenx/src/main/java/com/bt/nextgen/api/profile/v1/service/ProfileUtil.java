package com.bt.nextgen.api.profile.v1.service;

import com.bt.nextgen.api.userpreference.model.UserPreferenceEnum;
import com.bt.nextgen.core.repository.UserPreference;
import com.bt.nextgen.core.repository.UserPreferenceRepository;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by L075208 on 10/01/2017.
 */
@Service
@Transactional(value = "springJpaTransactionManager")
public class ProfileUtil {

    private static final Logger logger = getLogger(ProfileUtil.class);

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private UserCacheService userCacheService;

    /**
     * Get user's last selected profile id from preferences table.
     * <p/>
     * The preferred profile id is validated against the list of all available job profiles and only returned if it is open - otherwise
     * return the current job profile.
     *
     * @return profile id which active role will be switched to
     */
    public String getProfileId() {
        Profile effectiveProfile = profileService.getEffectiveProfile();
        final UserPreference defaultRole = userPreferenceRepository
                .find(effectiveProfile.getGcmId(), UserPreferenceEnum.DEFAULT_ROLE.getPreferenceKey());

        String jobProfileId = "";

        if (defaultRole != null) {
            try {
                jobProfileId = EncodedString.toPlainText(defaultRole.getValue());
            } catch (org.jasypt.exceptions.EncryptionOperationNotPossibleException e) {
                jobProfileId = "";
                logger.warn("Unable to decrypt prefer role {}", defaultRole.getValue(), e);
            }
            // Ensure that the user's last selected role is still available for selection.
            // Otherwise it could have been closed.
            for (JobProfile jobProfile : profileService.getAvailableProfiles()) {
                if (jobProfileId.equalsIgnoreCase(jobProfile.getProfileId())) {
                    return jobProfileId;
                }
            }
        }

        logger.info("Unable to find job profile {} for user {}. Reverting to job profile {}", jobProfileId, effectiveProfile.getGcmId(),
                effectiveProfile.getCurrentProfileId());
        return effectiveProfile.getCurrentProfileId();
    }

    /**
     * @return Cache key for the active profile
     */
    public String getActiveProfileCacheKey() {
        return userCacheService.getActiveProfileCacheKey();
    }
}
