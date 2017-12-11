package com.bt.nextgen.logout.service;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Logout Service Interface for user Logout
 */
public interface LogoutService {

    void notifyLogoutUser(ServiceErrors serviceErrors);

}
