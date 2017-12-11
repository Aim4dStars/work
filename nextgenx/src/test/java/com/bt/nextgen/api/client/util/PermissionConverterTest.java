package com.bt.nextgen.api.client.util;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.api.account.v1.model.AccountPaymentPermission;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;

public class PermissionConverterTest {


    private List<TransactionPermission> transactionPermissionList;

    @Before
    public void setUp() throws Exception {
        transactionPermissionList = new ArrayList<>();
        transactionPermissionList.add(TransactionPermission.Account_Maintenance);
        transactionPermissionList.add(TransactionPermission.No_Transaction);
        transactionPermissionList.add(TransactionPermission.Company_Registration);
    }

    @Test
    public void testGetAccountPermission() throws Exception {
        //TODO: more test cases need to be written for all the permutation combination
        PermissionConverter permissionConverter = new PermissionConverter(transactionPermissionList, true);
        Assert.assertThat(permissionConverter.getAccountPermission(), Is.is(AccountPaymentPermission.NO_PAYMENTS));

        transactionPermissionList = new ArrayList<>();
        transactionPermissionList.add(TransactionPermission.No_Transaction);
        transactionPermissionList.add(TransactionPermission.Account_Maintenance);
        permissionConverter = new PermissionConverter(transactionPermissionList, true);
        Assert.assertThat(permissionConverter.getAccountPermission(), Is.is(AccountPaymentPermission.NO_PAYMENTS));

        transactionPermissionList = new ArrayList<>();
        transactionPermissionList.add(TransactionPermission.No_Transaction);
        transactionPermissionList.add(TransactionPermission.Payments_Deposits_To_Linked_Accounts);
        permissionConverter = new PermissionConverter(transactionPermissionList, true);
        Assert.assertThat(permissionConverter.getAccountPermission(), Is.is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS));

        permissionConverter = new PermissionConverter(transactionPermissionList, false);
        Assert.assertThat(permissionConverter.getAccountPermission(), Is.is(AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS));

    }
}