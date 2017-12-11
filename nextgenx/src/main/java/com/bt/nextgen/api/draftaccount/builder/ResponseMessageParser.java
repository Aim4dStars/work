package com.bt.nextgen.api.draftaccount.builder;

import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorResponseType;

import java.util.List;

/**
 * Parser interface for querying response message objects, and extracting data from errors if they appear.
 * @param <P> Response message type.
 * @param <E> Error response type.
 */
public interface ResponseMessageParser<P, E extends ErrorResponseType> {

    /**
     * Whether or not the response received from the WebService was a success.s
     * @param response the response object.
     * @return whether it has a success status.
     */
    boolean isSuccessful(P response);

    /**
     * Extract the list of error response Objects from the (presumably erroneous) response.
     * @param response the response from which to extract the errors.
     * @return the list of error responses.
     */
    List<E> getErrorResponses(P response);
}
