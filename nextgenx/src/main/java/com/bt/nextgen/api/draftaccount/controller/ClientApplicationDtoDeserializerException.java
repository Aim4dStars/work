package com.bt.nextgen.api.draftaccount.controller;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Custom JSON deserializer exception for ClientApplicationDto type
 */
public class ClientApplicationDtoDeserializerException extends JsonProcessingException {

    protected ClientApplicationDtoDeserializerException(String msg) {
        super(msg);
    }

    protected ClientApplicationDtoDeserializerException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}