package com.bt.nextgen.api.userpreference.service;

import com.bt.nextgen.api.userpreference.model.UserPreferenceDto;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDtoKey;
import com.bt.nextgen.api.userpreference.model.UserPreferenceEnum;
import com.bt.nextgen.api.userpreference.model.UserTypeEnum;
import com.bt.nextgen.core.repository.UserPreference;
import com.bt.nextgen.core.repository.UserPreferenceKey;
import com.bt.nextgen.core.repository.UserPreferenceRepository;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This service retrieves and updates a user's saved preferences.
 */
@Service
public class UserPreferenceDtoServiceImpl implements UserPreferenceDtoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferenceDtoServiceImpl.class);

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private UserProfileService profileService;

    /**
     * Search for the user preference details. A user id is required to search the repository.
     *
     * @param key           user id and preference id to search for
     * @param serviceErrors serviceErrors
     */
    @Override public List<UserPreferenceDto> search(UserPreferenceDtoKey key, ServiceErrors serviceErrors) {
        List<UserPreferenceDto> preferenceList = new ArrayList<>();
        String userId = getUserId(key.getUserType());
        if (StringUtils.isNotBlank(userId)) {
            preferenceList = getUserPreferenceList(key, userId);
        }
        return preferenceList;
    }

    /**
     * Multiple preferences can be retrieved with a comma delimited string.
     *
     * @param key    contains the user type and preference value to find
     * @param userId
     */
    private List<UserPreferenceDto> getUserPreferenceList(UserPreferenceDtoKey key, String userId) {
        List<UserPreferenceDto> preferenceList = new ArrayList<>();
        String[] keyList = key.getPreferenceId().split(",");
        for (String keyStr : keyList) {
            UserPreference preference = userPreferenceRepository.find(userId, UserPreferenceEnum.fromString(keyStr)
                .getPreferenceKey());
            if (preference != null) {
                preferenceList.add(new UserPreferenceDto(key.getUserType(),
                    preference.getKey().getPreferenceId(), preference.getValue()));
            }
        }
        return preferenceList;
    }

    /**
     * Get the user id depending on the user type
     *
     * @param userTypeStr
     */
    private String getUserId(String userTypeStr) {
        UserTypeEnum userType = UserTypeEnum.fromString(userTypeStr);
        if (userType == UserTypeEnum.USER) {
            return profileService.getActiveProfile().getBankReferenceId();
        } else if (userType == UserTypeEnum.JOB) {
            return profileService.getActiveProfile().getJob().getId();
        }
        LOGGER.error("Could not determine id for this user.");
        return null;
    }

    /**
     * One preference is updated per call
     *
     * @param updateDetails preference key and value to update for this user
     * @param serviceErrors serviceErrors
     */
    @Override public UserPreferenceDto update(UserPreferenceDto updateDetails, ServiceErrors serviceErrors) {
        UserPreference preference = new UserPreference();
        if (updateDetails.getKey() != null && StringUtils.isNotBlank(updateDetails.getKey().getUserType())
            && StringUtils.isNotBlank(updateDetails.getValue())) {
            String userId = getUserId(updateDetails.getKey().getUserType());
            if (StringUtils.isNotBlank(userId)) {
                preference.setKey(new UserPreferenceKey(userId, UserPreferenceEnum.fromString(updateDetails.getKey()
                    .getPreferenceId()).getPreferenceKey()));
                preference.setValue(updateDetails.getValue());
                preference = userPreferenceRepository.save(preference);
                return new UserPreferenceDto(updateDetails.getKey().getUserType(),
                    preference.getKey().getPreferenceId(), preference.getValue());
            }
        }
        return null;
    }

    @Override public UserPreferenceDto find(UserPreferenceDtoKey key, ServiceErrors serviceErrors) {
        return null;
    }
}
