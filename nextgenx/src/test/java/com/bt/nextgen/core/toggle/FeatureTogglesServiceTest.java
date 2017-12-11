package com.bt.nextgen.core.toggle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;

/**
 * Tests for the feature toggles service.
 */
public class FeatureTogglesServiceTest {

    private FeatureTogglesService service;

    private ServiceErrors errors;

    @Before
    public void initService() {
        service = new FeatureTogglesServiceImpl();
        errors = new FailFastErrorsImpl();
    }

    @Test
    public void togglesMatchEnvironmentProperties() {
        final boolean performanceChartV2 = Properties.getSafeBoolean("feature.performanceChartV2");
        final FeatureToggles toggles = service.findOne(errors);
        assertEquals(performanceChartV2, toggles.getFeatureToggle("performanceChartV2"));
    }

    @Test
    public void togglesAreCachedAfterInitialCall() {
        final FeatureToggles toggles = service.findOne(errors);
        assertSame(toggles, service.findOne(errors));
    }
}
