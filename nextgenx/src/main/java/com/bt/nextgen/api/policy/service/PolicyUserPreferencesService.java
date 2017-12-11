package com.bt.nextgen.api.policy.service;

import com.bt.nextgen.api.policy.util.PolicyUtil;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDto;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDtoKey;
import com.bt.nextgen.api.userpreference.model.UserPreferenceEnum;
import com.bt.nextgen.core.repository.UserPreference;
import com.bt.nextgen.core.repository.UserPreferenceKey;
import com.bt.nextgen.core.repository.UserPreferenceRepository;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PolicyUserPreferencesService {

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerIntegrationService;

    public void saveLastAccessedFNumber(String fNUmber) {
        UserPreferenceDto userPreferenceDto = PolicyUtil.createDtoToSavePreferences(fNUmber, UserPreferenceEnum.LAST_ACCESSED_FNUMBER);
        updateUserPreference(userPreferenceDto);
    }

    public void saveLastAccessedAdviser(String brokerId) {
        UserPreferenceDto userPreferenceDto = PolicyUtil.createDtoToSavePreferences(brokerId, UserPreferenceEnum.LAST_ACCESSED_ADVISER);
        updateUserPreference(userPreferenceDto);
    }

    public String findLastAccessedFNumber() {
        UserPreferenceDtoKey userPreferenceDtoKey = PolicyUtil.getUserPreferenceKey(UserPreferenceEnum.LAST_ACCESSED_FNUMBER);
        return find(userPreferenceDtoKey);
    }

    public String findLastAccessedAdviser() {
        UserPreferenceDtoKey userPreferenceDtoKey = PolicyUtil.getUserPreferenceKey(UserPreferenceEnum.LAST_ACCESSED_ADVISER);
        return find(userPreferenceDtoKey);
    }

    private void updateUserPreference(UserPreferenceDto userPreferenceDto) {
        String userId = userProfileService.getActiveProfile().getBankReferenceId();
        UserPreference preference = new UserPreference();
        if (userPreferenceDto.getKey() != null && StringUtils.isNotBlank(userId)
                && StringUtils.isNotBlank(userPreferenceDto.getValue())) {
            preference.setKey(new UserPreferenceKey(userId, UserPreferenceEnum.fromString(userPreferenceDto.getKey()
                    .getPreferenceId()).getPreferenceKey()));
            preference.setValue(userPreferenceDto.getValue());
            userPreferenceRepository.save(preference);
        }
    }

    public String find(UserPreferenceDtoKey key) {
        String userIdentifier = userProfileService.getActiveProfile().getBankReferenceId();
        UserPreference preference = userPreferenceRepository.find(userIdentifier, UserPreferenceEnum.fromString(key.getPreferenceId())
                .getPreferenceKey());
        return preference!=null?preference.getValue():null;
    }
}
