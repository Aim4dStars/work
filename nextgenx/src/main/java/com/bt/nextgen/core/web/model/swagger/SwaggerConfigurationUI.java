package com.bt.nextgen.core.web.model.swagger;

import java.util.List;

public class SwaggerConfigurationUI {

    private String validatorUrl;
    private String docExpansion;
    private String apisSorter;
    private String defaultModelRendering;
    private List<String> supportedSubmitMethods;
    private Boolean jsonEditor;
    private Boolean showRequestHeaders;

    public SwaggerConfigurationUI(String validatorUrl, String docExpansion, String apisSorter, String defaultModelRendering,
            List<String> supportedSubmitMethods, Boolean jsonEditor, Boolean showRequestHeaders) {
        this.validatorUrl = validatorUrl;
        this.docExpansion = docExpansion;
        this.apisSorter = apisSorter;
        this.defaultModelRendering = defaultModelRendering;
        this.supportedSubmitMethods = supportedSubmitMethods;
        this.jsonEditor = jsonEditor;
        this.showRequestHeaders = showRequestHeaders;
    }

    public String getValidatorUrl() {
        return validatorUrl;
    }

    public String getDocExpansion() {
        return docExpansion;
    }

    public String getApisSorter() {
        return apisSorter;
    }

    public String getDefaultModelRendering() {
        return defaultModelRendering;
    }

    public List<String> getSupportedSubmitMethods() {
        return supportedSubmitMethods;
    }

    public Boolean getJsonEditor() {
        return jsonEditor;
    }

    public Boolean getShowRequestHeaders() {
        return showRequestHeaders;
    }
}
