package com.bt.nextgen.core.security;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum UIErrorCode
{
	API_LOAD_ERROR("500");

    private String UIErrorCode = "";

	private static final Map<String, UIErrorCode> errorMap = new HashMap<>();

    static
    {
        for (UIErrorCode code : EnumSet.allOf(UIErrorCode.class))
            errorMap.put(code.getUIErrorCode(), code);
    }

	public static UIErrorCode get(String tamOperationCode)
	{
		return errorMap.get(tamOperationCode);
	}

	UIErrorCode(String UIErrorCode)
	{
		this.UIErrorCode = UIErrorCode;
	}
	
	public String getUIErrorCode()
	{
		return UIErrorCode;
	}
}
