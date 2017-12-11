package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.config.WebConfig;
import com.bt.nextgen.core.exception.AccessDeniedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static com.bt.nextgen.core.web.controller.HeartbeatControllerTest.setEnvironmentToDev;
import static com.bt.nextgen.core.web.controller.HeartbeatControllerTest.setEnvironmentToProd;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { WebConfig.class })
public class APIReportControllerTest {

    @Autowired
    private APIReportController apiReportController;

    @Test
    public void test_that_Show_returns_a_String() {
        String result = apiReportController.show();
        assertThat("Not null check", result != null);
        assertThat("Zero length check", result.length() > 0);
        assertThat("Contains row data", result.contains("API Path"));
    }

    @Test(expected = AccessDeniedException.class)
    public void test_that_show_errors_in_prod() {
        setEnvironmentToProd();
        apiReportController.show();
        setEnvironmentToDev();
    }
}
