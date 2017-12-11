package com.bt.nextgen.core.repository;


public interface UserPreferenceRepository {
    public UserPreference find(String userId, String preferenceId);

    public UserPreference save(UserPreference userPreference);
}
