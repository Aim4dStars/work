package com.bt.nextgen.api.profile.service;

import com.bt.nextgen.api.profile.model.ProfileDetailsUpdateDto;
import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserRepository;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service updates the whats new viewed status for
 * the logged in user.
 *
 */
@Deprecated
@Service
public class ProfileDetailsUpdateDtoServiceImpl implements ProfileDetailsUpdateDtoService
{
    private static final Logger logger = LoggerFactory.getLogger(ProfileDetailsDtoServiceImpl.class);

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ProfileDetailsUpdateDto update(ProfileDetailsUpdateDto updateDto, ServiceErrors serviceErrors)
    {
        logger.info("Updating user's what's new viewed status.");
        boolean status = false;
        String gcmId = profileService.getActiveProfile().getBankReferenceId();
        User user = userRepository.loadUser(gcmId);
        if (user == null)
        {
            user = userRepository.newUser(gcmId);
        }

        if (updateDto.getKey().equalsIgnoreCase("read"))
        {
            user.setWhatsNewVersion(Properties.getString("version"));
            status = true;
        }

        userRepository.update(user);
        return new ProfileDetailsUpdateDto(status ? "Success" : "Failed");
    }
}