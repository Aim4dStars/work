package com.bt.nextgen.api.modelportfolio.v2.util.rebalance;

import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.btfin.panorama.service.integration.account.WrapAccount;

public class ModelAccountTypeHelper {
    private static final String SUPER_DESCRIPTION = "Superannuation";
    private static final String PENSION_DESCRIPTION = "Pension";
    private static final String PENSION_TTR_DESCRIPTION = "Pension (TTR)";

    private ModelAccountTypeHelper() {
        // private constructor
    }

    public static String getAccountTypeDescription(WrapAccount account) {
        String accountType = account.getAccountStructureType().name();

        if (AccountStructureType.SUPER.equals(account.getAccountStructureType())) {
            accountType = SUPER_DESCRIPTION;

            if (account instanceof PensionAccountDetailImpl) {
                if (PensionType.TTR.equals(((PensionAccountDetailImpl) account).getPensionType())) {
                    accountType = PENSION_TTR_DESCRIPTION;
                } else {
                    accountType = PENSION_DESCRIPTION;
                }
            }
        }

        return accountType;
    }

    public static String getModelAccountType(String modelAccountTypeId) {
        if (modelAccountTypeId != null && ModelType.forId(modelAccountTypeId) != null) {
            return ModelType.forId(modelAccountTypeId).getDisplayValue();
        }
        return "";
    }
}
