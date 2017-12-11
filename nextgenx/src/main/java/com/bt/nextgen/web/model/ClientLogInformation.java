package com.bt.nextgen.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.codehaus.jackson.annotate.JsonWriteNullProperties;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * For details. See <a href="http://dwgps0026/twiki/bin/view/NextGen/CoreSinglePageAppLogging">CoreSinglePageAppLogging</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientLogInformation {

    @NotNull
    private String clientType;
    @NotNull
    private String clientVersion;
    private String originatingSystem;
    private List<PerformanceLog> performanceLogs;
    private List<ErrorLog> errorLogs;

    public String getClientType() {
        return clientType;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public List<PerformanceLog> getPerformanceLogs() {
        return performanceLogs;
    }

    public List<ErrorLog> getErrorLogs() {
        return errorLogs;
    }

    public String getOriginatingSystem() {
        return originatingSystem;
    }

    public void setOriginatingSystem(String originatingSystem) {
        this.originatingSystem = originatingSystem;
    }
}
