package com.bt.nextgen.api.logon.service;

import com.bt.nextgen.api.logon.model.LogonUpdatePasswordDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.gateway.AvaloqException;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerPasswordManagementIntegrationService;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by L070589 on 10/11/2014.
 */
@Service
@Transactional(value = "springJpaTransactionManager")
public class LogonUpdatePasswordDtoServiceImpl implements LogonUpdatePasswordDtoService {


    private static final Logger logger = LoggerFactory.getLogger(LogonUpdatePasswordDtoServiceImpl.class);
    @Autowired
    CustomerPasswordManagementIntegrationService customerPasswordManagement;
    @Autowired
    private UserProfileService profileService;
    @Autowired
    private UserInformationIntegrationService userInformationIntegrationService;


    @Override
    public LogonUpdatePasswordDto update(LogonUpdatePasswordDto logonUpdatePasswordDto, ServiceErrors serviceErrors) {

        UserReset userReset = new UserReset();
        userReset.setCredentialId(logonUpdatePasswordDto.getKey().getCredentialId());
        userReset.setPassword(logonUpdatePasswordDto.getCurrentPassword());
        userReset.setConfirmPassword(logonUpdatePasswordDto.getNewPassword());
        userReset.setHalgm(logonUpdatePasswordDto.getHalgm());
        userReset.setRequestedAction(ServiceConstants.UPDATE_PASSWORD);
        LogonUpdatePasswordDto responseLogonUpdatePasswordDto= new LogonUpdatePasswordDto();

        try {
            CustomerCredentialManagementInformation customerCredentialManagementInformation = customerPasswordManagement.updatePassword(userReset, serviceErrors);
            if (customerCredentialManagementInformation.getServiceLevel().equalsIgnoreCase(Attribute.SUCCESS_MESSAGE))
            {
                responseLogonUpdatePasswordDto.setUpdateFlag(true);
                logger.info("LogonServiceImpl.updatePassword() : Successfully returning service status.");
                try
                {
                    userInformationIntegrationService.notifyPasswordChange(profileService.getActiveProfile(),serviceErrors);
                } catch (AvaloqException e) {
                    logger.error("Getting error response from avaloq while sending password update information for {}", profileService.getGcmId());
                }
            } else {
                responseLogonUpdatePasswordDto.setUpdateFlag(false);
                logger.info("LogonServiceImpl.updatePassword() : Error, returning service status.");
            }
        } catch (Exception ex) {
            logger.error("Error updating password for {}",profileService.getGcmId(), ex);
        }
        return responseLogonUpdatePasswordDto;
    }
}
