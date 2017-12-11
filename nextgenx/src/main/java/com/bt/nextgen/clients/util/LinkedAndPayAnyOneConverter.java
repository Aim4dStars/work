package com.bt.nextgen.clients.util;

import com.bt.nextgen.addressbook.web.model.PayAnyOneAndLinkedAccountModel;
import com.bt.nextgen.payments.repository.LinkedAccount;
import com.bt.nextgen.payments.repository.PayAnyonePayee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LinkedAndPayAnyOneConverter
{
	private static final Logger logger = LoggerFactory.getLogger(LinkedAndPayAnyOneConverter.class);

	public static List <PayAnyOneAndLinkedAccountModel> toModelForLinkedAccount(List <LinkedAccount> linkedAccountList)
	{
		logger.trace("Start Of Method :toModel");

		List <PayAnyOneAndLinkedAccountModel> modelObjList = null;
		modelObjList = new ArrayList <PayAnyOneAndLinkedAccountModel>();
		if ((linkedAccountList != null) && (linkedAccountList.size() > 0))
		{

			for (LinkedAccount account : linkedAccountList)
			{

				PayAnyOneAndLinkedAccountModel modelObj = null;
				modelObj = new PayAnyOneAndLinkedAccountModel();
				if ((account.getNickname()!= null ) || (!("".equals(account.getNickname()))))
					modelObj.setAccountNickName(account.getNickname());
				modelObj.setAccountName(account.getAccountName());
				modelObj.setAccountNumber(account.getAccountNumber());
				modelObj.setAccountType(account.getPayeeType().name());
				modelObj.setBsb(account.getBsb().getBsbCode());
				modelObjList.add(modelObj);
			}
		}
		logger.trace("End Of Method :toModel");
		return modelObjList;
	}

	public static List <PayAnyOneAndLinkedAccountModel> toModelForPayAnyOne(List <PayAnyonePayee> payAnyOneAccountList)
	{
		logger.trace("Start Of Method :toModel");
		List <PayAnyOneAndLinkedAccountModel> modelObjList = null;
		modelObjList = new ArrayList <PayAnyOneAndLinkedAccountModel>();
		if (payAnyOneAccountList != null)
		{	
			for (PayAnyonePayee account : payAnyOneAccountList)
			{
				PayAnyOneAndLinkedAccountModel modelObj = null;
				modelObj = new PayAnyOneAndLinkedAccountModel();
				if ((account.getNickname()!= null ) || (!("".equals(account.getNickname()))))
					modelObj.setAccountNickName(account.getNickname());
					modelObj.setAccountName(account.getName());
					modelObj.setAccountNumber(account.getAccountNumber());
					modelObj.setAccountType(account.getPayeeType().toString());
					modelObj.setBsb(account.getBsb().getBsbCode());

				modelObjList.add(modelObj);
			}
		}
		logger.trace("End Of Method :toModel");
		return modelObjList;
	}
}
