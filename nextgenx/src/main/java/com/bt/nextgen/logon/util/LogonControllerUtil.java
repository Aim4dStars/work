package com.bt.nextgen.logon.util;

import com.bt.nextgen.clientadmin.web.util.InvestorControllerUtil;
import com.bt.nextgen.core.web.model.PhoneModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LogonControllerUtil
{
	private static final Logger logger = LoggerFactory.getLogger(LogonControllerUtil.class);

	public static List <PhoneModel> getMobileList(List <PhoneModel> phoneModelList)
	{
		return getPhoneModelListByType(phoneModelList, Attribute.MOBILE);
	}

	public static List <PhoneModel> getLandLineList(List <PhoneModel> phoneModelList)
	{
		return getPhoneModelListByType(phoneModelList, Attribute.LANDLINE);
	}

	public static List <PhoneModel> getPhoneModelListByType(List <PhoneModel> phoneModelList, String phoneType)
	{
		logger.debug("Inside getPhoneModelListByType()");
		List <PhoneModel> phoneModelListByType = null;
		if (phoneModelList != null)
		{
			for (PhoneModel phoneModel : phoneModelList)
			{
				if (phoneModel.getType().equalsIgnoreCase(phoneType))
				{
					if (phoneModelListByType == null)
					{
						phoneModelListByType = new ArrayList <PhoneModel>();
					}
					phoneModelListByType.add(phoneModel);
				}
			}
		}

		if (phoneModelListByType != null)
		{
			phoneModelListByType = InvestorControllerUtil.sortedPhoneList(phoneModelListByType);
		}

		return phoneModelListByType;
	}

}
