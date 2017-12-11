package com.bt.nextgen.web.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerformanceLog extends ClientLog {
    private String intiatorType;
    private Long duration;
    private String name;
    private Long redirectDuration;
    private Long dnsDuration;
    private Long connectDuration;
    private Long sslDuration;
    private Long timeToFirstByte;
    private Long downloadDuration;
    private Long durationFromLogin;
    private String previousLocation;

    public String getIntiatorType() {
        return intiatorType;
    }

    public String getName() {
        return name;
    }

    public Long getDuration() {
        return duration;
    }

    public Long getRedirectDuration() {
        return redirectDuration;
    }

    public Long getDnsDuration() {
        return dnsDuration;
    }

    public Long getConnectDuration() {
        return connectDuration;
    }

    public Long getSslDuration() {
        return sslDuration;
    }

    public Long getTimeToFirstByte() {
        return timeToFirstByte;
    }

    public Long getDownloadDuration() {
        return downloadDuration;
    }

    public Long getDurationFromLogin() {
        return durationFromLogin;
    }

    public String getPreviousLocation() {
        return previousLocation;
    }
}