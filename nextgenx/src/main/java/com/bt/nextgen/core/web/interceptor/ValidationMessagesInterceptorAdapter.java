package com.bt.nextgen.core.web.interceptor;

import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.bt.nextgen.cms.service.CmsEntryJaxb;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.cms.service.CmsServiceXmlResourceImpl;
import com.bt.nextgen.config.ApplicationContextProvider;

import net.sf.cglib.beans.BeanGenerator;

public class ValidationMessagesInterceptorAdapter extends HandlerInterceptorAdapter
{
	private static final Logger logger = LoggerFactory.getLogger(ValidationMessagesInterceptorAdapter.class);
	

	/**
	 * This bean is constructed using cglib to represents all of the messages
	 * in the validationMessages.properties file.  It does this so that the jsp can
	 * use the ${errors.err00005} format to access the error messages.
	 */
	private static Object bean;

	{
		loadErrorMessages();
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
		ModelAndView modelAndView) throws Exception
	{
		
		if  (request.getRequestURI().contains(
		"/page/reloadcms") )
		{
			loadErrorMessages();
		}
        if (modelAndView != null && modelAndView.getModelMap() != null && request.getRequestURI() != null && (request.getRequestURI().contains(
			"/secure/page/") || request.getRequestURI().contains("/logon") || request.getRequestURI().contains("/secure/") || request.getRequestURI().contains("/public/page/")))
		{
			modelAndView.getModelMap().addAttribute("errors", bean);
		}

	}
	
	/**
	 * Loads the error codes from cms and creates a dynamic field for each error code in CGLIB bean.
	 */
	//TODO the following method will change with actual implementation of cms.
	public synchronized void loadErrorMessages(){
		final BeanGenerator beanGenerator = new BeanGenerator();
		try
		{
			CmsService cmsService=ApplicationContextProvider.getApplicationContext().getBean(CmsService.class);
			logger.info("Loading CMS error messages");
			//Get the error codes from cms.
			Set<Entry<String, CmsEntryJaxb>> cmsEntrySet= ((CmsServiceXmlResourceImpl)cmsService).getErrorCodeEntrySet();
			
			for(Entry<String, CmsEntryJaxb> entry :cmsEntrySet)
			{
				String key = entry.getKey();

				beanGenerator.addProperty(key, String.class);
			}
			bean = beanGenerator.create();
			for(Entry<String, CmsEntryJaxb> entry :cmsEntrySet)
			{
				String key = entry.getKey();
				String cmsEntryValue = entry.getValue().getValue();
				PropertyUtils.setProperty(bean, key, cmsEntryValue);
			}
			logger.info("Loaded {} error messages from the CMS",cmsEntrySet.size());
		}
		 catch (Exception e)
		{
			logger.error("Exception caught while loading errors", e);
		}
	}	
}
