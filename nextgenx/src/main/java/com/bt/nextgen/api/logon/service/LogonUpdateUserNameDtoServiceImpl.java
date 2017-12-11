package com.bt.nextgen.api.logon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.logon.model.LogonUpdateUserNameDto;
import com.bt.nextgen.core.security.profile.UserNameChangeHolder;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.service.group.customer.CustomerUserNameManagementIntegrationService;

/**
 * Created by L070589 on 10/11/2014.
 */
@Service
@Transactional(value = "springJpaTransactionManager")
public class LogonUpdateUserNameDtoServiceImpl implements LogonUpdateUserNameDtoService {


    @Autowired
    CustomerUserNameManagementIntegrationService customerUserNameManagement;

    @Autowired
	UserNameChangeHolder userProfileService;

    private static final Logger logger = LoggerFactory.getLogger(LogonUpdateUserNameDtoServiceImpl.class);

    @Override
    public LogonUpdateUserNameDto update(LogonUpdateUserNameDto logonUserNameDto, ServiceErrors serviceErrors) {

        UserReset userReset = new UserReset();
        userReset.setCredentialId(logonUserNameDto.getKey().getCredentialId());
        userReset.setUserName(logonUserNameDto.getUserName());
        userReset.setNewUserName(logonUserNameDto.getNewUserName());
        LogonUpdateUserNameDto responseLogonUpdateUserNameDto= new LogonUpdateUserNameDto();

        try {
            CustomerCredentialManagementInformation customerCredentialManagementInformation = customerUserNameManagement.updateUsername(userReset, serviceErrors);
            if(!serviceErrors.hasErrors()) {
                if (customerCredentialManagementInformation.getServiceLevel().equals("SUCCESS")) {
                    responseLogonUpdateUserNameDto.setUpdateFlag(true);
                    userProfileService.setNewUserNameProvidedByUserForChange(logonUserNameDto.getNewUserName());
                    logger.debug("Update Status is {}", customerCredentialManagementInformation.getServiceLevel());
                } else {
                    responseLogonUpdateUserNameDto.setUpdateFlag(false);
                }
            }
        } catch (Exception ex) {
            logger.error("Error updating username: ", ex);
        }

        return responseLogonUpdateUserNameDto;
    }
}
