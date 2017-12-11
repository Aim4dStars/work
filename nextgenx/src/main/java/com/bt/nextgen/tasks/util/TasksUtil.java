package com.bt.nextgen.tasks.util;

import com.bt.nextgen.core.web.model.AjaxResponse;

public class TasksUtil 
{

	public static AjaxResponse toModel(String clientId, Boolean isRegCodeSent) 
	{
		return new AjaxResponse(isRegCodeSent, clientId);
	}

}
