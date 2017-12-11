package com.bt.nextgen.api.termdeposit.controller;

import com.bt.nextgen.api.termdeposit.service.TermDepositCalculatorDtoService;
import com.bt.nextgen.api.termdeposit.service.TermDepositReportService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by M044020 on 3/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class TermDepositReportsControllerTest {

    @InjectMocks
    private TermDepositReportsController controller;

    @Mock
    private TermDepositCalculatorDtoService termDepositCalculatorDtoService;

    @Mock
    private TermDepositReportService depositReportService;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @Mock
    private CmsService cmsService;
    @Mock
    private FeatureToggles features;

    @Before
    public void setUp() throws Exception {
        when(cmsService.getContent(anyString())).thenReturn("St. George");
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(features);
        when(termDepositCalculatorDtoService.getTermDepositRatesAsCsv(anyString(), anyString(), anyString()))
                .thenReturn("Successfully generated.");
        when(depositReportService.getTermDepositRatesAsCsv(anyString(), anyString(), anyString(), anyString()))
                .thenReturn("Successfully generated.");
    }

    @Test
    public void getTDRatesWithoutToggle() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(false);
        ResponseEntity result = controller.getTDRates(null, null, null, null);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void getTDRatesWithToggle() throws Exception {
        when(features.getFeatureToggle(anyString())).thenReturn(true);
        ResponseEntity result = controller.getTDRates(null, null, null, null);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
    }
}