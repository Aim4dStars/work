package com.bt.nextgen.api.profile.v1.service;

import com.bt.nextgen.api.profile.v1.model.ProfileDetailsDto;
import com.bt.nextgen.core.api.dto.FindOneDtoService;

public interface ProfileDetailsDtoService extends FindOneDtoService<ProfileDetailsDto> {

    /**
     * For use in {@code @PreAuthorize} annotations.
     *
     * @return whether the currently logged in user is a Westpac adviser.
     */
    boolean isWestpacAdviser();

    /**
     * For use in {@code @PreAuthorize} annotations.
     *
     * @return whether the currently logged in adviser belongs to a Westpac-branded dealer group.
     */
    boolean isWestpacBrandedAdviser();

    /**
     * For use in {@code @PreAuthorize} annotations.
     * @return whether the currently logged in adviser is registered for offline approvals (of client applications).
     */
    boolean isOfflineApproval();

    /**
     * Clears the profile cache
     */
    void clearProfileCache();
}
