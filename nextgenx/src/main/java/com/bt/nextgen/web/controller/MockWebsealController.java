package com.bt.nextgen.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bt.nextgen.core.repository.UserRepository;

/**
 * This class will support our authentication/authorisation in environments without webseal
 */
@Controller
public class MockWebsealController
{
	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/public/page/logonFailure")
	public String logonFailed()
	{
		return LogonController.REDIRECT_LOGON + "?TAM_OP=auth_failure";
	}

	@RequestMapping("/public/page/logoutSuccess")
	public String logoutSuccess()
	{   
		return LogonController.REDIRECT_LOGON + "?TAM_OP=logout";
	}

	@RequestMapping("/public/page/accessDenied")
	public String accessDenied()
	{
		return LogonController.REDIRECT_LOGON + "?TAM_OP=auth_failure";
	}


	@RequestMapping(value = LogonController.LOGON, method = RequestMethod.GET)
	public String requestLogon()
	{
		return LogonController.REDIRECT_LOGON + "?TAM_OP=login";
	}

	@RequestMapping(value = "/security/doLogon", method = RequestMethod.POST)
	public String doLogon()
	{
		return HomePageController.REDIRECT_HOMEPAGE;
	}

    //TODO : Temp FIX !!! Need to remove, keepin this just for the demo to product
   /* @RequestMapping("/public/page/blockedLogon")
    public String blockedLogon()
    {
        return LogonController.REDIRECT_LOGON + "?TAM_OP=blocked";
    }*/
}
