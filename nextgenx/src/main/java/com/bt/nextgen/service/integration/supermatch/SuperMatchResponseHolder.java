package com.bt.nextgen.service.integration.supermatch;

import com.bt.nextgen.service.btesb.base.model.EsbError;

import java.util.List;

/**
 * Interface for the Super match response
 */
public interface SuperMatchResponseHolder {

    /**
     * Gets the super match response from the search
     */
    List<SuperMatchDetails> getSuperMatchDetails();

    /**
     * Gets the status from the super match response
     */
    String getStatus();

    /**
     * Gets the error from the super match response if any
     */
    EsbError getError();
}
