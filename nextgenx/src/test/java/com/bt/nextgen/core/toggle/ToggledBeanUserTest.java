package com.bt.nextgen.core.toggle;

import com.bt.nextgen.core.toggle.beans.SampleService;
import com.bt.nextgen.core.toggle.beans.ToggleOffSampleService;
import com.bt.nextgen.core.toggle.beans.ToggleOnSampleService;
import com.bt.nextgen.core.toggle.beans.ToggledSampleService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.bt.nextgen.core.toggle.beans.ToggledSampleService.TOGGLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for the {@code ToggledBeanUser} base class. Uses an ultra-slim Spring Context to ensure that this part of
 * the functionality works as well.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ToggledBeanUserTest.Config.class)
public class ToggledBeanUserTest {

    @Autowired
    private FeatureTogglesService togglesService;

    @Autowired
    private SampleService service;

    private boolean toggle;

    @Before
    public void initToggle() {
        final FeatureToggles toggles = togglesService.findOne(new FailFastErrorsImpl());
        toggle = toggles.getFeatureToggle(TOGGLE_NAME);
    }

    @Test
    public void correctServiceImplementationIsToggled() {
        assertTrue(service instanceof ToggledSampleService);
        final String actualBeanName = service.getBeanName();
        final String expectedBeanName = toggle ? ToggleOnSampleService.BEAN_NAME : ToggleOffSampleService.BEAN_NAME;
        assertEquals(expectedBeanName, actualBeanName);
    }

    @Configuration
    @ComponentScan("com.bt.nextgen.core.toggle.beans")
    public static class Config {

        @Bean
        public FeatureTogglesService featureTogglesService() {
            return new FeatureTogglesServiceImpl();
        }
    }
}
