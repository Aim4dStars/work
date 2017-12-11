package com.bt.nextgen.core.web.model.swagger;

public class SwaggerConfigurationSecurity {

    private String apiKeyVehicle;
    private String scopeSeparator;
    private String apiKeyName;

    public SwaggerConfigurationSecurity(String apiKeyVehicle, String scopeSeparator, String apiKeyName) {
        this.apiKeyVehicle = apiKeyVehicle;
        this.scopeSeparator = scopeSeparator;
        this.apiKeyName = apiKeyName;
    }

    public String getApiKeyVehicle() {
        return apiKeyVehicle;
    }

    public String getScopeSeparator() {
        return scopeSeparator;
    }

    public String getApiKeyName() {
        return apiKeyName;
    }
}
