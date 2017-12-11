package com.bt.nextgen.service.avaloq.logout;

import org.joda.time.DateTime;

/**
 * Interface for User Logout Avaloq Service
 */
public interface UserLogoutDetail {
    String  getSecUser();
    DateTime getLoginTime();
    DateTime getLastActionTime();

    void setSecUser(String user);
    void setLoginTime(DateTime loginTime);
    void setLastActionTime(DateTime lastActiontime);
}
