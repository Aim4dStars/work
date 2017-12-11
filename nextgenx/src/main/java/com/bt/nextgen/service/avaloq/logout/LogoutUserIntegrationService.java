package com.bt.nextgen.service.avaloq.logout;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Interface for User Logout Avaloq Service
 */
@SuppressWarnings("squid:S00112")
public interface LogoutUserIntegrationService {
    UserLogoutDetail notifyUserLogout(ServiceErrors serviceErrors) throws Exception;
}
