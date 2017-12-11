package com.bt.nextgen.core.web.controller;

import static com.bt.nextgen.core.web.controller.HeartbeatControllerTest.setEnvironmentToDev;
import static com.bt.nextgen.core.web.controller.HeartbeatControllerTest.setEnvironmentToProd;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.bt.nextgen.core.exception.AccessDeniedException;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.bt.nextgen.cms.service.CmsService;

import javax.servlet.ServletContext;

public class SettingsControllerTest
{
	private SettingsController controller = new SettingsController();

	@Test
	public void testReloadCMS_failure_isInternalServerError() throws Exception
	{
		CmsService mockService = Mockito.mock(CmsService.class);
		ReflectionTestUtils.setField(controller, "cmsService", mockService);

		Mockito.when(mockService.reLoadCmsContent()).thenReturn(CmsService.STATUS.FAILURE);

		assertThat(controller.reloadCMS().getStatusCode(), Is.is(HttpStatus.INTERNAL_SERVER_ERROR));
	}

	@Test
	public void testReloadCMS_success_OK() throws Exception
	{
		CmsService mockService = Mockito.mock(CmsService.class);
		ReflectionTestUtils.setField(controller, "cmsService", mockService);

		Mockito.when(mockService.reLoadCmsContent()).thenReturn(CmsService.STATUS.SUCCESS);

		assertThat(controller.reloadCMS().getStatusCode(), Is.is(HttpStatus.OK));
	}

	@Test
	public void testReloadProperties() throws Exception
	{
		assertThat(controller.reloadProperties().getStatusCode(), Is.is(HttpStatus.OK));

	}

    @Test(expected = AccessDeniedException.class)
    public void showEnvironmentVars_FailsInProduction() throws Exception {
        setEnvironmentToProd();
        // Will blow up on this next line
        controller.showEnvironment();
    }

    @Test
    public void showEnvironmentVars_ReturnsEnvVars() throws Exception {
        setEnvironmentToDev();
        mockServletContext();

        String result = controller.showEnvironment();
        //System.out.println(result);
        assertTrue(result.contains("JAVA"));
    }

    @Test(expected = AccessDeniedException.class)
    public void showEnvironmentProperties_FailsInProduction() throws Exception {
        setEnvironmentToProd();
        // Will blow up on this next line
        controller.showEnvironmentProperties();
    }

    @Test
    public void showEnvironmentProperties_ReturnsProperties() throws Exception {
        setEnvironmentToDev();
        mockServletContext();

        String result = controller.showEnvironmentProperties();
        //System.out.println(result);
        assertTrue(result.contains("java.library.path"));
    }

    @Test(expected = AccessDeniedException.class)
    public void showPanoramaProperties_FailsInProduction() throws Exception {
        setEnvironmentToProd();
        // Will blow up on this next line
        controller.showPanoramaProperties();
    }

    @Test
    public void showPanoramaProperties_ReturnsProperties() throws Exception {
        setEnvironmentToDev();
        mockServletContext();

        String result = controller.showPanoramaProperties();
        //System.out.println(result);
        assertTrue(result.contains("application-submission"));
    }

    //the LoggingControllerTests were failing intermittently due to env being production.
    @After
    public void resetConfigAfterTests(){
        setEnvironmentToDev();
    }

    private void mockServletContext() {
        ServletContext mockServletContext = Mockito.mock(ServletContext.class);
        when(mockServletContext.getContextPath()).thenReturn("/ng");
        ReflectionTestUtils.setField(controller, "context", mockServletContext);
    }
}
