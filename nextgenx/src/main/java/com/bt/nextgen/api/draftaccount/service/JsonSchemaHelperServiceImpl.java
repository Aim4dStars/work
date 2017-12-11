package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.model.JsonSchemaEnumsDto;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by M040398 on 23/08/2016.
 */
@Service
public class JsonSchemaHelperServiceImpl implements JsonSchemaHelperService {

    // create scanner and disable default filters (that is the 'false' argument)
    private final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);

    @Override
    public JsonSchemaEnumsDto getJsonSchemaEnums() throws ClassNotFoundException {
        JsonSchemaEnumsDto dto = new JsonSchemaEnumsDto();
        //filter to collect all Enum classes
        provider.addIncludeFilter(new AssignableTypeFilter(Enum.class));
        // get matching classes defined in the package
        final Set<BeanDefinition> classes = provider.findCandidateComponents(FormDataValidator.JAVA_PACKAGE_JSON_SCHEMAS);
        // this is how you can load the class type from BeanDefinition instance
        Class<?> clazz = null;
        for (BeanDefinition bean: classes) {
            clazz = Class.forName(bean.getBeanClassName());
            dto.addEnumValues(clazz.getSimpleName(), clazz.getEnumConstants());
        }
        return dto;
    }

}
