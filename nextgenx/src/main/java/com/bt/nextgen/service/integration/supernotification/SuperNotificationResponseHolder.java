package com.bt.nextgen.service.integration.supernotification;

import com.bt.nextgen.service.btesb.base.model.EsbError;

public interface SuperNotificationResponseHolder {

    /**
     * Gets the status from the super match response
     */
    String getStatus();

    /**
     * Gets the error from the super match response if any
     */
    EsbError getError();
}
