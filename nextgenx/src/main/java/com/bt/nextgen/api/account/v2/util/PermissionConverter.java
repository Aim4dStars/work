package com.bt.nextgen.api.account.v2.util;

import com.bt.nextgen.api.account.v2.model.AccountPaymentPermission;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;

import java.util.Collection;
import java.util.Collections;

/**
 * The Class PermissionConverter.
 */
@Deprecated
public class PermissionConverter {

    /** The permissions. */
    private final Collection<TransactionPermission> permissions;

    /** The is adviser. */
    private final boolean isAdviser;

    /** The account permission. */
    private AccountPaymentPermission accountPermission;

    /**
     * Instantiates a new permission converter.
     *
     * @param permission
     *            the permission
     * @param isAdviser
     *            the is adviser
     */
    public PermissionConverter(TransactionPermission permission, boolean isAdviser) {
        this(Collections.singleton(permission), isAdviser);
    }

    /**
     * Instantiates a new permission converter.
     *
     * @param permissions
     *            the permissions
     * @param isAdviser
     *            the is adviser
     */
    public PermissionConverter(Collection<TransactionPermission> permissions, boolean isAdviser) {
        this.permissions = permissions;
        this.isAdviser = isAdviser;
    }

    /**
     * Converts avaloq TransactionPermission permission to UI permissions.
     *
     * @return the account permission
     */
    public AccountPaymentPermission getAccountPermission() {
        for (TransactionPermission permission : permissions) {
            if (TransactionPermission.Company_Registration != permission) {
                switch (permission) {
                    case Account_Maintenance:
                        if (accountPermission == null) {
                            accountPermission = isAdviser ? AccountPaymentPermission.NA : AccountPaymentPermission.NO_PAYMENTS;
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
            accountPermission = isAdviser ? AccountPaymentPermission.NA : AccountPaymentPermission.NO_PAYMENTS;
        }

        return accountPermission;
    }
}
