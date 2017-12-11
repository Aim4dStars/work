package com.bt.nextgen.core.exception;

import com.bt.nextgen.service.ServiceError;
import org.slf4j.helpers.MessageFormatter;

/**
 * Created by m035652 on 6/05/14.
 * This exception is thrown by a ControllerService when data is found to be
 * incorrectly setup in Avaloq.
 *
 * It should contain as many details about the problem as possible so that the Service
 * Operators can fix any issues which exist with this user in smart client
 *
 *  This error will be handled by the generic
 */
public class UserConfigurationException extends Exception
{
    private static final String TEMPLATE_ERROR_MESSAGE = " There was a problem adding values to message";

    private String gcmId;

    private String username;

    private ServiceError error;

    public UserConfigurationException(ServiceError error, String gcmId, String username)
    {
        super(error.getReason());
        this.error = error;
        this.gcmId = gcmId;
        this.username = username;
    }

    public String getGcmId() {
        return gcmId;
    }

    public String getUsername() {
        return username;
    }

    public ServiceError getError() {
        return error;
    }
}
