package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.api.version.service.VersionService;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.util.Properties;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static com.bt.nextgen.core.web.controller.HeartbeatControllerTest.setEnvironmentToDev;
import static com.bt.nextgen.core.web.controller.HeartbeatControllerTest.setEnvironmentToProd;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class VersionControllerTest
{
	@Test
	public void testShowVersion() throws Exception
	{
		final VersionController underTest = new VersionController();
		final String theVersion = underTest.showVersion();

		assertThat(theVersion, is(Properties.getString("version")));
		assertThat(theVersion, is(underTest.showSettingsVersion()));
	}

	@Test
    public void testShowVersionRedirectDeprecated() throws Exception
    {
        VersionController versionController = new VersionController();
        ModelAndView modelAndView = versionController.showVersionRedirect();
        assertThat(modelAndView.getViewName(), is("redirect:/public/api/nextgen/v1_0/version"));
    }

	@Test
	public void testShowVersionAdvancedCORS() throws Exception
	{
        setEnvironmentToDev();
		VersionController versionController = new VersionController();

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		when(req.getRemoteHost()).thenReturn("btfin.com");
		when(req.getHeader(any(String.class))).thenReturn("btfin.com");

		versionController.showVersionAdvancedCORS(req, response);

		writer.flush();
		String result = sw.toString();

		assertThat(result, is(Properties.getString("version")));
		assertThat(result, is(versionController.showSettingsVersion()));
	}

    @Test(expected = AccessDeniedException.class)
    public void testShowVersionAdvancedCORS_HiddenInProd() throws Exception
    {
        setEnvironmentToProd();
        VersionController versionController = new VersionController();

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        when(req.getRemoteHost()).thenReturn("btfin.com");
        when(req.getHeader(any(String.class))).thenReturn("btfin.com");

        versionController.showVersionAdvancedCORS(req, response);
        //Note expected exception above
        setEnvironmentToDev();
    }

	@Test
    public void testGetBootstrapParametersBasic() throws Exception
    {
        final VersionController underTest = new VersionController();
        final String bootstrapParameters = underTest.getBootstrapParameters();

        assertThat(bootstrapParameters, CoreMatchers.containsString("overrideKernelVersion"));
    }

    @Test
    public void testGetBootstrapParametersAllParams() throws Exception
    {
        final VersionController underTest = new VersionController();

        Map<String, String> environment = underTest.getEnvironmentAbstraction();
        environment.put("jarvis.overrideKernelVersion","1.23");
        environment.put("jarvis.overrideOWVersion","4.56");
        environment.put("jarvis.overrideNWVersion","7.89");

        final String bootstrapParameters = underTest.getBootstrapParameters();

        assertThat(bootstrapParameters, CoreMatchers.containsString("overrideKernelVersion"));
    }

    @Test
    public void testGetBootstrapParametersNoParams() throws Exception
    {
        final VersionController underTest = new VersionController();
        final String bootstrapParameters = underTest.getBootstrapParameters();

        Map<String, String> environment = underTest.getEnvironmentAbstraction();
        environment.put("jarvis.overrideKernelVersion",null);
        environment.put("jarvis.overrideOWVersion",null);
        environment.put("jarvis.overrideNWVersion",null);

        assertThat(bootstrapParameters, CoreMatchers.containsString("overrideKernelVersion"));
    }

    @Test
    public void testShowServerVersion() throws Exception
    {
        final VersionController underTest = new VersionController();
        Properties.all().setProperty("nextgen.version","server-2.10b4");
        String result = underTest.showServerVersion();
        assertThat(result, CoreMatchers.is("server-2.10b4"));
    }

    @Test
    public void testShowAvaloqVersion() {
        final VersionController underTest = new VersionController();
        VersionService versionService = Mockito.mock(VersionService.class);
        when(versionService.getAvaloqVersion()).thenReturn("ad5d2f489bb9204a77411ef651325636");
        underTest.setVersionService(versionService);
        String result = underTest.showAvaloqVersion();
        assertThat(result, CoreMatchers.is("ad5d2f489bb9204a77411ef651325636"));
    }

    @Test
    public void testShowAvaloqFullVersion() {
	    setEnvironmentToDev();
        final VersionController underTest = new VersionController();
        VersionService versionService = Mockito.mock(VersionService.class);
        when(versionService.getFullAvaloqVersion()).thenReturn("Avaloq Version ID:ad5d2f489bb9204a77411ef651325636");
        underTest.setVersionService(versionService);
        String result = underTest.showAvaloqFullVersion();
        assertThat(result, CoreMatchers.is("Avaloq Version ID:ad5d2f489bb9204a77411ef651325636"));

        HeartbeatControllerTest.setEnvironmentToProd();
        VersionService versionService2 = Mockito.mock(VersionService.class);
        when(versionService2.getAvaloqVersion()).thenReturn("ad5d2f489bb9204a77411ef651325636");
        underTest.setVersionService(versionService2);
        String result2 = underTest.showAvaloqFullVersion();
        assertThat(result2, CoreMatchers.is("ad5d2f489bb9204a77411ef651325636"));
    }

    @Test
    public void testRefreshAvaloqVersion() {
        final VersionController underTest = new VersionController();
        VersionService versionService = Mockito.mock(VersionService.class);
        underTest.setVersionService(versionService);
        String result = underTest.refreshAvaloqVersion();
        assertThat(result, CoreMatchers.is("OK"));
    }

    //the LoggingControllerTests were failing intermittently due to env being production.
    @After
    public void resetConfigAfterTests(){
        setEnvironmentToDev();
    }
}
