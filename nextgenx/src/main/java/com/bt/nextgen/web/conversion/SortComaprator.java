package com.bt.nextgen.web.conversion;

import com.bt.nextgen.addressbook.PayeeModel;

import java.util.Comparator;

public class SortComaprator implements Comparator
{
	public int compare(Object firstObj, Object secondObj)
	{
		PayeeModel firstPayeeModel = (PayeeModel)firstObj;
		PayeeModel secondPayeeModel = (PayeeModel)secondObj;
		int nameValue = (firstPayeeModel.getName().compareToIgnoreCase(secondPayeeModel.getName()));
		if (nameValue != 0)
		{
			int typeValue = ((firstPayeeModel.getPayeeType().toString().compareToIgnoreCase(secondPayeeModel.getPayeeType()
				.toString())));
			if (typeValue != 0)
				return typeValue;
		}
		return nameValue;

	}

}
