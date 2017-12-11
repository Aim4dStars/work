package com.bt.nextgen.logout.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.logout.LogoutUserIntegrationService;
import com.bt.nextgen.service.avaloq.logout.UserLogoutDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is responsible to logout the user from Avaloq
 */
@Service
@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.IllegalCatchCheck")
public class LogoutServiceImpl implements LogoutService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutServiceImpl.class);

    @Autowired
    private LogoutUserIntegrationService logoutUserIntegrationService;

    /**
     * Notify Avaloq about logged out User
     *
     * @param serviceErrors
     */

    @Override
    public void notifyLogoutUser(ServiceErrors serviceErrors) {
        try {
            UserLogoutDetail userLogoutDetail = logoutUserIntegrationService.notifyUserLogout(serviceErrors);
            if (userLogoutDetail == null) {
                LOGGER.info("Logout Request to Avaloq is not send as there is no user login.");
                return;
            }
            LOGGER.info(" sent Logout Request to Avaloq for User:{}. [Login Time:{}, Last Action Time:{}]",
                    userLogoutDetail.getSecUser(), userLogoutDetail.getLoginTime(), userLogoutDetail.getLastActionTime());
        } catch (Exception ex) {
            LOGGER.error("Exception occurred while user trying to logout", ex);
        }

    }

}
