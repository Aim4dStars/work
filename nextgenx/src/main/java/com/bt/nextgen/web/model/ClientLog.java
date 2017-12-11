package com.bt.nextgen.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientLog {
    private String logType;
    private String location;

    public String getLogType() {
        return logType;
    }

    public String getLocation() {
        return location;
    }
}
