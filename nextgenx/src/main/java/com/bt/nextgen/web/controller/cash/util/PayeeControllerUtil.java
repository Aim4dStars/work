package com.bt.nextgen.web.controller.cash.util;

import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.repository.*;
import com.bt.nextgen.payments.web.model.BpayBillerModel;

import java.util.*;

public class PayeeControllerUtil
{
	public static List <BpayBillerModel> loadBillers(BpayBillerCodeRepository bpayBillerCodeRepository, String startsWith)
	{
		Collection <BpayBiller> billers = bpayBillerCodeRepository.findByPartialBillerCode(startsWith);
		List <BpayBillerModel> billerDtos = new ArrayList <BpayBillerModel>();
		for (BpayBiller biller : billers)
		{
			billerDtos.add(new BpayBillerModel(biller.getBillerCode(), biller.getBillerName()));
		}
		return billerDtos;
	}

	//TODO: Now hard coded in JSP,It will be implemented when CMS will be integrated.
	/*public static ArrayList <Object> loadPaymentCategories()
	 {
	     Map <String, ArrayList <String>> categoryMap = new HashMap <String, ArrayList <String>>();
	     ArrayList <String> subcatlist1 = new ArrayList <String>();
	     ArrayList <String> subcatlist2 = new ArrayList <String>();

	     subcatlist1.add("Pension payment");
	     subcatlist1.add("Pers tax deductible");
	     subcatlist1.add("Personal undeducted");
	     subcatlist1.add("Contribution split");
	     subcatlist1.add("Rollover");
	     subcatlist1.add("Other");
	     categoryMap.put("Super payment", subcatlist1);

	     subcatlist2.add("Shares");
	     subcatlist2.add("Managed Investment");
	     subcatlist2.add("Bond/Fixed interest");
	     subcatlist2.add("Property");
	     categoryMap.put("Asset purchase", subcatlist2);

	     ArrayList <Object> categoryListNotes = new ArrayList <Object>();

	     categoryListNotes.add(categoryMap);
	     categoryListNotes.add("Sample Notes");

	     return categoryListNotes;
	 }*/

	/**
	 * *************************************************************************************************************************************
	 * Convert PayeeModel into LinkedAccount Domain
	 * *************************************************************************************************************************************
	 */
	public static LinkedAccount linkedAccountConverter(PayeeModel payeeModel, String cashAccountId)
	{
		LinkedAccount linkedAccount = new LinkedAccount();
		linkedAccount.setAccountName(payeeModel.getName());
		linkedAccount.setAccountNumber(payeeModel.getReference());

		linkedAccount.setNickname(payeeModel.getNickname());
		Bsb bsb = new Bsb();
		if (payeeModel.getCode() != null && !payeeModel.getCode().equals(Attribute.EMPTY_STRING))
		{
			bsb.setBsbCode(payeeModel.getCode());
			bsb.setBsbCode(payeeModel.getCode().replaceAll("[- ]?", ""));
		}
		linkedAccount.setBsb(bsb);
		if (payeeModel.getPayeeType() != null)
		{
			linkedAccount.setPayeeType(payeeModel.getPayeeType().equals(PayeeType.SECONDARY_LINKED)
				? PayeeType.SECONDARY_LINKED
				: PayeeType.PRIMARY_LINKED);
		}
		linkedAccount.setCashAccountId(cashAccountId);
		//linkedAccount.setId(payeeModel.getId());

		return linkedAccount;
	}

	/**
	 * *************************************************************************************************************************************
	 * Check Number of Linked Account Corresponding to an Investor & Set the Flag for maximum Accounts
	 * *************************************************************************************************************************************
	 */
	public static boolean hasMoreThan4Secondaries(List <LinkedAccount> linkedAccountList)
	{
		int numberOfSecondaries = 0;

		for (LinkedAccount linkedAccount : linkedAccountList)
		{
			if (linkedAccount.getPayeeType().equals(PayeeType.SECONDARY_LINKED))
			{
				numberOfSecondaries++;
			}
		}
		/*if (numberOfSecondaries >= 4)
		{
			return true;
		}*/
		return false;

	}

	public static PayAnyonePayee convertModelToDomain(PayeeModel payeeModel, String cashAccount)
	{
		PayAnyonePayee payAnyOnePayee = new PayAnyonePayee();
		payAnyOnePayee.setName(payeeModel.getName());
		payAnyOnePayee.setAccountNumber(payeeModel.getReference());
		Bsb bsb = new Bsb();
		if (payeeModel.getCode() != null && !payeeModel.getCode().equals(Attribute.EMPTY_STRING))
		{
			bsb.setBsbCode(payeeModel.getCode());
			bsb.setBsbCode(payeeModel.getCode().replaceAll("[- ]?", ""));
		}
		payAnyOnePayee.setBsb(bsb);
		payAnyOnePayee.setNickname(payeeModel.getNickname());
		payAnyOnePayee.setPayeeType(PayeeType.PAY_ANYONE);
		payAnyOnePayee.setCashAccountId(cashAccount);
		//payAnyOnePayee.setId(payeeModel.getId());
		return payAnyOnePayee;
	}

	public static List <PayeeModel> getSortedPayeeList(List <PayeeModel> payeeList)
	{
		PayeeModel payeeNomineeModel = null;
		for (PayeeModel payeeModel : payeeList)
		{
			if (payeeModel.getPayeeType().equals(PayeeType.PRIMARY_LINKED))
			{
				payeeNomineeModel = payeeModel;
				break;

			}
		}
		payeeList.remove(payeeNomineeModel);

		Collections.sort(payeeList, new Comparator <PayeeModel>()
		{
			@Override
			public int compare(PayeeModel o1, PayeeModel o2)
			{
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		if (payeeNomineeModel != null)
		{
			payeeList.add(0, payeeNomineeModel);
		}

		return payeeList;

	}
}
