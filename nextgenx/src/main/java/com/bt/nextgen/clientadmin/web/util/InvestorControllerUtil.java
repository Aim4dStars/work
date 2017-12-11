package com.bt.nextgen.clientadmin.web.util;

import com.bt.nextgen.core.web.model.EmailModel;
import com.bt.nextgen.core.web.model.PhoneModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InvestorControllerUtil
{

	private static final Logger logger = LoggerFactory.getLogger(InvestorControllerUtil.class);

	/**********
	 * 
	 * @param newPassword
	 * @param confirmPassword
	 * @return true : if repeated password is same as new password else false
	 */
	public static boolean checkRepeatedPassword(String newPassword, String confirmPassword)
	{
		logger.debug("Inside Method : checkRepeatedPassword()");
		if (newPassword.equals(confirmPassword))
		{
			return true;
		}
		return false;
	}

	public static List <EmailModel> sortedEmailList(List <EmailModel> emailList)
	{

		Collections.sort(emailList, new Comparator <EmailModel>()
		{
			@Override
			public int compare(EmailModel o1, EmailModel o2)
			{
				boolean b1 = o1.isPrimary();
				boolean b2 = o2.isPrimary();
				if (b1 == b2)
				{
					return 0;
				}
				// either b1 is true or b2   
				// if true goes after false switch the -1 and 1   
				return (b1 ? -1 : 1);
			}
		});

		return emailList;

	}

	public static List <PhoneModel> sortedPhoneList(List <PhoneModel> phoneList)
	{

		Collections.sort(phoneList, new Comparator <PhoneModel>()
		{
			@Override
			public int compare(PhoneModel o1, PhoneModel o2)
			{
				boolean b1 = o1.isPrimary();
				boolean b2 = o2.isPrimary();
				if (b1 == b2)
				{
					return 0;
				}
				// either b1 is true or b2   
				// if true goes after false switch the -1 and 1   
				return (b1 ? -1 : 1);
			}
		});

		return phoneList;

	}
}
