package com.bt.nextgen.service.avaloq.logout;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * This service is used to notify avaloq about logged out user
 */

@Service
public class UserLogoutDetailImpl implements UserLogoutDetail {

    private String secUser;
    private DateTime loginTime;
    private DateTime lastActionTime;


    @Override
    public String getSecUser() {
        return secUser;
    }

    @Override
    public DateTime getLoginTime() {
        return loginTime;
    }

    @Override
    public DateTime getLastActionTime() {
        return lastActionTime;
    }

    @Override
    public void setSecUser(String secUser) {
        this.secUser = secUser;
    }

    @Override
    public void setLoginTime(DateTime loginTime) {
        this.loginTime = loginTime;
    }

    @Override
    public void setLastActionTime(DateTime lastActionTime) {
        this.lastActionTime = lastActionTime;
    }
}