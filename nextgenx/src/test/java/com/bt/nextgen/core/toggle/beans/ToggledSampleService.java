package com.bt.nextgen.core.toggle.beans;

import com.bt.nextgen.core.toggle.ToggledBeanUser;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Test subclass for the ToggledBeanUser class.
 */
@Component
@Primary
public class ToggledSampleService extends ToggledBeanUser<SampleService> implements SampleService {

    public static final String TOGGLE_NAME = "sampleFeature";

    public ToggledSampleService() {
        super(SampleService.class, TOGGLE_NAME, ToggleOnSampleService.BEAN_NAME, ToggleOffSampleService.BEAN_NAME);
    }

    @Override
    public void setBeanName(String name) {
        // Ignore
    }

    @Override
    public String getBeanName() {
        return getBean().getBeanName();
    }
}
