package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountPaymentPermission;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by L062329 on 23/12/2014.
 */
public class PermissionConverter {
    private final Collection<TransactionPermission> permissions;
    private final boolean isAdviser;
    private AccountPaymentPermission accountPermission;

    public PermissionConverter(TransactionPermission permission, boolean isAdviser) {
        this.permissions= new ArrayList<>();
        this.permissions.add(permission);
        this.isAdviser = isAdviser;
    }

    public PermissionConverter(Collection<TransactionPermission> permissions, boolean isAdviser) {
        this.permissions= permissions;
        this.isAdviser = isAdviser;
    }


    /**
     * Converts avaloq TransactionPermission permission to UI permissions
     * @return
     */
    public AccountPaymentPermission getAccountPermission() {
        for (TransactionPermission permission : permissions ) {
            if (TransactionPermission.Company_Registration != permission) {
                switch (permission) {
                    case Account_Maintenance:
                        if (accountPermission == null) {
                            if (!isAdviser) {
                                accountPermission = AccountPaymentPermission.NA;

                            } else {
                                accountPermission = AccountPaymentPermission.NO_PAYMENTS;
                            }
                        }
                        break;
                    case Payments_Deposits_To_Linked_Accounts:
                        accountPermission = AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_LINKED_ACCOUNTS;
                        break;
                    case Payments_Deposits:
                        accountPermission = AccountPaymentPermission.PAYMENTS_DEPOSITS_TO_ALL;
                        break;
                    default:
                        // haven't found a mappable permission so continue to the next one
                }
            }
        }

        if (accountPermission == null) {
            if (!isAdviser) {
                accountPermission = AccountPaymentPermission.NA;

            } else {
                accountPermission = AccountPaymentPermission.NO_PAYMENTS;
            }
        }

        return accountPermission;
    }
}
