package com.bt.nextgen.core.web.interceptor;

/**
 * This is the data that will be returned when an api is called and the server isn't ready
 */
public class StatusCheckResponse {

    private String status;
    private String humanUrl;
    private String message;

    public StatusCheckResponse(String status, String humanUrl, String message) {
        this.status = status;
        this.humanUrl = humanUrl;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getHumanUrl() {
        return humanUrl;
    }

    public String getMessage() {
        return message;
    }
}
