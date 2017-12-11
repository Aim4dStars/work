package com.bt.nextgen.reports.account.common;

import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.PensionAccountDetail;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.apache.commons.lang.StringUtils;

/**
 * Utility class for account footer and header report data
 */
public class AccountUtil {

    /**
     * private constructor to hide the implicit public one - sonar check
     */
    private AccountUtil() {}

    /**
     * Set the accountStructure field for the super account
     * - Pension or Pension TTR based on the pensiontype
     *
     * @param wrapAccount
     *
     * @return
     */
    public static String getAccountTypeAndDescription(WrapAccountDetail wrapAccount) {
        String accountTypeDescription = wrapAccount.getAccountStructureType().name();
        if (wrapAccount instanceof PensionAccountDetail) {
            PensionAccountDetail pensionAccount = (PensionAccountDetail) wrapAccount;
            if (pensionAccount.getPensionType() != null) {
                switch (pensionAccount.getPensionType()) {
                    case TTR:
                        accountTypeDescription = PensionType.TTR.getLabel();
                        break;
                    case TTR_RETIR_PHASE:
                        accountTypeDescription = PensionType.TTR_RETIR_PHASE.getLabel();
                        break;
                    default:
                        accountTypeDescription = PensionType.STANDARD.getLabel();
                }
            }
        }
        if (accountTypeDescription.equals(AccountStructureType.SUPER.name())) {
            accountTypeDescription = StringUtils.capitalize(AccountStructureType.SUPER.name().toLowerCase());
        }
        return accountTypeDescription;
    }

}