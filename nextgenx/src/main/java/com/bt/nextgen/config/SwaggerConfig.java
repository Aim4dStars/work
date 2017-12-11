package com.bt.nextgen.config;

import com.bt.nextgen.util.Environment;
import com.google.common.base.Predicate;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Configuration
@PropertySource({ "classpath:/com/bt/nextgen/config/swagger.properties" })
@EnableSwagger2
@ComponentScan(basePackages = "com.bt.nextgen.api")
public class SwaggerConfig {

    @Bean(name = "swaggerFilteredApi")
    public Docket api() throws IOException {
        List<String> validApis = getValidApis();

        return new Docket(DocumentationType.SWAGGER_2)
            .enable(Environment.notProduction())
            .apiInfo(getFilteredApiInfo())
            .groupName("filteredApi")
            .directModelSubstitute(DateTime.class, String.class)
            .globalResponseMessage(RequestMethod.GET, getError500Details())
            .globalResponseMessage(RequestMethod.POST, getError500Details())
            .globalResponseMessage(RequestMethod.DELETE, getError500Details())
            .select()
                .apis(validateApis(validApis))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean(name = "swaggerFullApi")
    public Docket everyApi() throws IOException {
        return new Docket(DocumentationType.SWAGGER_2)
            .enable(Environment.notProduction())
            .apiInfo(getFullApiInfo())
            .groupName("fullApi")
            .directModelSubstitute(DateTime.class, String.class)
            .globalResponseMessage(RequestMethod.GET, getError500Details())
            .globalResponseMessage(RequestMethod.POST, getError500Details())
            .globalResponseMessage(RequestMethod.DELETE, getError500Details())
            .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getFilteredApiInfo() {
        return new ApiInfo("Panorama API Documentation",
                "All APIs correctly set up to use a UriConfig.properties file will be displayed here", null, null,
                (Contact) null, null, null);
    }

    private ApiInfo getFullApiInfo() {
        return new ApiInfo("Panorama API Documentation", "All APIs will be displayed here", null, null, (Contact) null, null,
                null);
    }

    private List<ResponseMessage> getError500Details() {
        return Arrays.asList(new ResponseMessageBuilder().code(500).message("Internal server error")
                .responseModel(new ModelRef("Error")).build());
    }

    private List<String> getValidApis() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:com/bt/nextgen/api/**/UriConfig.properties");

        List<String> propertyList = new ArrayList<>();
        if (resources != null) {
            for (Resource resource : resources) {
                Properties properties = new Properties();
                properties.load(resource.getInputStream());
                propertyList.addAll(Arrays.asList(properties.keySet().toArray(new String[0])));
            }
        }
        return propertyList;
    }

    private Predicate<RequestHandler> validateApis(final List<String> validApis) {
        return new Predicate<RequestHandler>() {
            @Override
            public boolean apply(RequestHandler input) {
                return valid(input, validApis);
            }
        };
    }

    private boolean valid(RequestHandler input, List<String> validApis) {
        RequestMapping annotation = AnnotationUtils.findAnnotation(input.getHandlerMethod().getMethod(), RequestMapping.class);
        if (annotation != null) {
            String[] annotationValue = (String[]) AnnotationUtils.getValue(annotation, "value");

            if (annotationValue.length == 1 && annotationValue[0] != null) {
                String value = removeFormatting(annotationValue[0]);
                return validApis.contains(value);
            }
        }
        return false;
    }

    private String removeFormatting(String value) {
        // Convert "${uri}" to "uri" if necessary
        if ("${".equals(value.substring(0, 2))) {
            return value.substring(2, value.length() - 1);
        }
        return value;
    }
}