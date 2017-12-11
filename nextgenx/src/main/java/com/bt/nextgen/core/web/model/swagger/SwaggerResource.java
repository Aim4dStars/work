package com.bt.nextgen.core.web.model.swagger;

public class SwaggerResource {

    private String name;
    private String location;
    private String swaggerVersion;

    public SwaggerResource(String name, String location, String swaggerVersion) {
        this.name = name;
        this.location = location;
        this.swaggerVersion = swaggerVersion;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getSwaggerVersion() {
        return swaggerVersion;
    }
}
