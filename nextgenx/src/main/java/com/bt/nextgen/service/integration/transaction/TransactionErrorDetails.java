package com.bt.nextgen.service.integration.transaction;


public interface TransactionErrorDetails {

    /**
     * Whether or not an error occurred
     * 
     * @return
     */
    public boolean isErrorResponse();

    /**
     * The error message if one exists
     * 
     * @return
     */
    public String getErrorMessage();
}
