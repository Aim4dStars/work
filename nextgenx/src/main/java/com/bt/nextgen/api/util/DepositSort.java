package com.bt.nextgen.api.util;

import com.bt.nextgen.api.account.v1.model.DepositDto;

import com.bt.nextgen.addressbook.PayeeModel;

import java.util.Comparator;

/**
 * Created by L069679 on 2/11/2014.
 */
public class DepositSort implements Comparator
{
    public int compare(Object firstObj, Object secondObj)
    {
        DepositDto firstDepositModel = (DepositDto)firstObj;
        DepositDto secondPayeeModel = (DepositDto)secondObj;
        if(null!= firstDepositModel.getFromPayDto().getNickname() && null!= secondPayeeModel.getFromPayDto().getNickname()) {
            int nameValue = (firstDepositModel.getFromPayDto().getNickname().compareToIgnoreCase(secondPayeeModel.getFromPayDto().getNickname()));
            return nameValue;
        } else {
            int nameValue = (firstDepositModel.getFromPayDto().getAccountName().compareToIgnoreCase(secondPayeeModel.getFromPayDto().getAccountName()));
            return nameValue;

        }


    }

}
