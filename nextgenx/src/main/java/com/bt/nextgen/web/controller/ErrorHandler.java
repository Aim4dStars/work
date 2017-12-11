package com.bt.nextgen.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.util.View;

/**
 * This is not used anywhere, other than its own test cases.
 *
 * This is deprecated and is scheduled for deletion, please use @GlobalExceptionHandler
 */
@Controller
@Deprecated
public class ErrorHandler
{
	@Autowired
	private UserProfileService profileService;

	@RequestMapping(value = "/error/page/{errorNumber}")
	public String handle(@PathVariable("errorNumber") String errorNumber)
	{
		switch (errorNumber)
		{
			case "unsupportedBrowser":
				return "redirect:/public/static/page/unsupportedbrowsers.html";
			  
			case "403":
			case "404":
				if(isUnauthorised())
				{
					return "redirect:/public/page/logon";
				}
				return View.ERROR_404;

			case "500":
			default:
				if (isUnauthorised())
				{
					return "redirect:/public/static/page/maintenance.html";
				}
				return View.ERROR_500;


		}
	}

	private boolean isUnauthorised()
	{
		return !(profileService.isLoggedIn());

	}
}
