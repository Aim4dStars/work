package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.web.model.swagger.SwaggerConfigurationSecurity;
import com.bt.nextgen.core.web.model.swagger.SwaggerConfigurationUI;
import com.bt.nextgen.core.web.model.swagger.SwaggerResource;
import com.bt.nextgen.util.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * This controller exists because Springfox has hardcoded resource endpoints which get redirected to the login page in our test
 * environments and thus are inaccessible. Duplicates of these resources are manually provided here.
 */
@Controller
public class SwaggerController {

    // Manually provide <host>/ng/swagger-resources/configuration/ui
    @RequestMapping(value = { "/public/static/documentation/swagger-resources/configuration/ui" }, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public SwaggerConfigurationUI provideUIConfig() {
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }
        List<String> methods = Arrays.asList("get", "post", "put", "delete", "patch");
        return new SwaggerConfigurationUI(null, "none", "alpha", "schema", methods, Boolean.FALSE, Boolean.TRUE);
    }

    // Manually provide <host>/ng/swagger-resources/configuration/security
    @RequestMapping(value = { "/public/static/documentation/swagger-resources/configuration/security" }, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public SwaggerConfigurationSecurity provideSecurityConfig() {
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }
        return new SwaggerConfigurationSecurity("header", ":", "api_key");
    }

    // Manually provide <host>/ng/swagger-resources/
    @RequestMapping(value = { "/public/static/documentation/swagger-resources" }, method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<SwaggerResource> provideResources() {
        if (Environment.isProduction()) {
            throw new AccessDeniedException("You are not authorised");
        }
        SwaggerResource filteredApiGroup = new SwaggerResource("filteredApi", "?group=filteredApi", "2.0");
        SwaggerResource fullApiGroup = new SwaggerResource("fullApi", "?group=fullApi", "2.0");
        return Arrays.asList(filteredApiGroup, fullApiGroup);
    }
}
