package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.util.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.Properties;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.bt.nextgen.core.api.UriMappingConstants.NEXTGEN_MODULE_VERSION;

@Controller
public class SettingsController
{
	private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

	@Autowired
	private CmsService cmsService;

    @Autowired
    private ServletContext context;

    /**
	 * This method will reload the contents of Cms.
	 *
	 * @return
	 */
	@RequestMapping(value = "/secure/page/admin/reloadcms", method = RequestMethod.GET)
	public ResponseEntity reloadCMS()
	{
		logger.info("Reload of cms requested...");
		CmsService.STATUS status = cmsService.reLoadCmsContent();
		switch (status)
		{
			case SUCCESS:
				logger.info("Reload of cms requested... SUCCESS");
				return new ResponseEntity(HttpStatus.OK);

			default:
				logger.warn("Reload of cms requested... error");
				return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method will reload the contents properties files.
	 *
	 * @return
	 */
	@RequestMapping(value = "/secure/page/admin/reloadproperties", method = RequestMethod.GET)
	public ResponseEntity reloadProperties()
	{
		logger.info("Reload of properties requested...");
		Properties.reload();
		return new ResponseEntity(HttpStatus.OK);
	}

    @RequestMapping(value = {"/public/properties/environment/variables",NEXTGEN_MODULE_VERSION+"/environment/variables"
    }, method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String showEnvironment()
    {
        if(Environment.isProduction()){
            throw new AccessDeniedException("You are not authorised");
        }
        String app = context.getContextPath();

        String result = "";

        result += "<h3>Operating System Environment Variables</h3>";
        result += "<p style=\"font-style: italic;\">What are the memory settings and what other values exist in the OS Environment context?</p>";
        result += "<p><a href='"+app+NEXTGEN_MODULE_VERSION+"/dashboard/dashboard"+"'>Back to the Dashboard dashboard</a>";

        result += "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";

        Map<String, String> env = System.getenv();
        Set<String> keys =  new TreeSet(env.keySet());
        for (String envName :keys) {
            result += envName + "=" +env.get(envName) + "\n";
        }
        result += "</pre>";
        return result;
    }

    @RequestMapping(value = {"/public/properties/environment",NEXTGEN_MODULE_VERSION+"/properties/environment"
    }, method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String showEnvironmentProperties()
    {
        if(Environment.isProduction()){
            throw new AccessDeniedException("You are not authorised");
        }
        String app = context.getContextPath();

        String result = "";

        result += "<h3>Java Properties</h3>";
        result += "<p style=\"font-style: italic;\">What are the values in System.util.Properties? (not in the Panorama Properties object)</p>";
        result += "<p><a href='"+app+NEXTGEN_MODULE_VERSION+"/dashboard/dashboard"+"'>Back to the Dashboard dashboard</a>";

        result += "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";

        java.util.Properties properties = System.getProperties();
        Set<String> keys =  new TreeSet(properties.keySet());
        for (String property :keys) {
            result += property + "=" +properties.get(property) + "\n";
        }
        result += "</pre>";
        return result;
    }

    @RequestMapping(value = {"/public/properties/settings",NEXTGEN_MODULE_VERSION+"/properties/settings"
    }, method = RequestMethod.GET, produces = "text/html")
    @ResponseBody
    public String showPanoramaProperties()
    {
        if(Environment.isProduction()){
            throw new AccessDeniedException("You are not authorised");
        }
        String app = context.getContextPath();

        String result = "";

        result += "<h3>Panorama Properties</h3>";
        result += "<p style=\"font-style: italic;\">How did the values from my properties file(s) get loaded up at runtime?</p>";
        result += "<p><a href='"+app+NEXTGEN_MODULE_VERSION+"/dashboard/dashboard"+"'>Back to the Dashboard dashboard</a>";

        result += "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">";

        java.util.Properties properties = Properties.all();
        Set<String> keys =  new TreeSet(properties.keySet());
        for (String property :keys) {
            result += property + "=" +properties.get(property) + "\n";
        }
        result += "</pre>";
        return result;
    }

}
