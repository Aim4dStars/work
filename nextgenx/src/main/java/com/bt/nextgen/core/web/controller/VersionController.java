package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.api.version.service.VersionService;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.interceptor.Spring3CorsFilter;
import com.bt.nextgen.util.Environment;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.bt.nextgen.core.api.UriMappingConstants.NEXTGEN_MODULE_VERSION;
import static com.bt.nextgen.core.util.SETTINGS.SETTINGS_VERSION;

@Controller
public class VersionController
{
	//private final Logger logger = LoggerFactory.getLogger(getClass());

	private Map<String,String> environmentAbstraction = new HashMap<String,String>();

	@Autowired
	VersionService versionService;

	@RequestMapping(value = {
			"/version", "/public/version", NEXTGEN_MODULE_VERSION + "/version",
	}, method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	@Spring3CorsFilter
	public String showVersion()
	{
		return showSettingsVersion();
	}

	@RequestMapping(value = "/public/version2", method = RequestMethod.GET)
	public ModelAndView showVersionRedirect(){
		return new ModelAndView("redirect:" + NEXTGEN_MODULE_VERSION + "/version");
	}

	@SuppressFBWarnings("HRS_REQUEST_PARAMETER_TO_HTTP_HEADER")
	@RequestMapping(value = "/public/version3", method = RequestMethod.GET, produces = "text/plain") @ResponseBody
	public void showVersionAdvancedCORS(HttpServletRequest req, HttpServletResponse response) throws IOException {

		if(Environment.isProduction()){
			throw new AccessDeniedException("You are not authorised");
		}

		String origin = req.getHeader("Origin");
		// need to do origin.toString() to avoid findbugs error about response splitting
		response.addHeader("Access-Control-Allow-Origin", origin.toString());

		// Test CORS putting in domain passed in
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");

		String result = showSettingsVersion();
		response.getWriter().write(result);
	}

	@RequestMapping(value = {
			"/public/version/server"
	}, method = RequestMethod.GET, produces = "text/plain") @ResponseBody
	@Spring3CorsFilter
	public String showServerVersion()
	{
		return Properties.getString("nextgen.version");
	}

	@RequestMapping(value = {
			"/public/version/avaloq"
	}, method = RequestMethod.GET, produces = "text/plain") @ResponseBody
	@Spring3CorsFilter
	public String showAvaloqVersion()
	{
		return versionService.getAvaloqVersion();
	}

	@RequestMapping(value = {
			"/public/version/avaloqFull"
	}, method = RequestMethod.GET, produces = "text/plain") @ResponseBody
	@Spring3CorsFilter
	public String showAvaloqFullVersion()
	{
		if(Environment.isProduction())
			return versionService.getAvaloqVersion();
		else
			return versionService.getFullAvaloqVersion();

	}

	@RequestMapping(value = {
			"/public/version/avaloq/refresh"
	}, method = RequestMethod.GET, produces = "text/plain") @ResponseBody
	public String refreshAvaloqVersion()
	{
		versionService.refreshAvaloqVersion();
		return "OK";
	}

	@RequestMapping(value = {
			"/version/settings", "/public/version/settings"
	}, method = RequestMethod.GET, produces = "text/plain") @ResponseBody
	@Spring3CorsFilter
	public String showSettingsVersion()
	{
		return SETTINGS_VERSION.value();
	}

	/**
	 * This function is used to pass any specific server side bootstrap parameters to the UI
	 * @return a text formatted as 'application/javascript'
	 */
	@RequestMapping(value = {
			"/public/bootstrap/parameters.js"
	}, method = RequestMethod.GET, produces = "application/javascript") @ResponseBody
	public String getBootstrapParameters()
	{
		String overrideKernelVersion = "";
		String overrideOWVersion = "";
		String overrideNWVersion = "";
		if (getLocalEnv("jarvis.overrideKernelVersion") != null) {
			overrideKernelVersion = getLocalEnv("jarvis.overrideKernelVersion").trim();
		}
		if (getLocalEnv("jarvis.overrideOWVersion") != null) {
			overrideOWVersion = getLocalEnv("jarvis.overrideOWVersion").trim();
		}
		if (getLocalEnv("jarvis.overrideNWVersion") != null) {
			overrideNWVersion = getLocalEnv("jarvis.overrideNWVersion").trim();
		}
		return "window.org.bt.bootstrapParams = {\"formalEnvironmentName\":\"" + Properties.getString("barista.environment.label") + "\"," +
				"\"overrideKernelVersion\":\"" + overrideKernelVersion + "\"," +
				"\"overrideOWVersion\":\"" + overrideOWVersion  + "\"," +
				"\"overrideNWVersion\":\"" + overrideNWVersion + "\"};";
	}

	public Map<String,String> getEnvironmentAbstraction() {
		return environmentAbstraction;
	}

	/**
	 * Provide a way to modify environment variables for testing
	 * @param propertyName
	 * @return
	 */
	private String getLocalEnv(String propertyName) {
		if (environmentAbstraction.get(propertyName) != null) {
			return environmentAbstraction.get(propertyName);
		}
		return System.getenv().get(propertyName);
	}

	/**
	 * For testing
	 * @param versionService
	 */
	public void setVersionService(VersionService versionService) {
		this.versionService = versionService;
	}
}