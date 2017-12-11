package com.bt.nextgen.api.draftaccount.builder.v3;


import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.service.integration.account.CashManagementAccountType;
import ns.btfin_com.product.common.investmentaccount.v2_0.AccountPropertyListType;
import ns.btfin_com.product.common.investmentaccount.v2_0.AccountPropertyType;

import java.util.List;

/**
 * This class has been created to build PIE and POA
 * elements for CMA feature. Each of these elements is index of
 * {@code List<AccountPropertyType>}
 */
public class AccountPropertiesTypeBuilder {

    private static final String YES_STR = "yes";
    private static final String NO_STR = "no";

    private AccountPropertiesTypeBuilder() {
    }

    /**
     * This identifies the account type and accordingly builds the
     * {@code AccountPropertyListType} object with {@code AccountPropertyType} elements.
     * Each element has possible pair of keys - {@code CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY} and/or
     * {@code CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY} and values
     * of {@code "yes"} or {@code "no"} depending on account type.
     *
     * @param form instance of type {@link IClientApplicationForm} containing application details is required
     * @return it returns {@link AccountPropertyListType} object
     */
    public static AccountPropertyListType getAccountPropertyListType(IClientApplicationForm form) {
        Boolean poa = null;
        Boolean pie = null;

        if (!form.isDirectAccount()) {
            poa = form.getAccountSettings() != null ? form.getAccountSettings().getPowerOfAttorney() : null;
        }

        if (IClientApplicationForm.AccountType.COMPANY == form.getAccountType()) {
            pie = form.getCompanyDetails().getPersonalInvestmentEntity();
        } else if ((IClientApplicationForm.AccountType.INDIVIDUAL_TRUST == form.getAccountType()
            || IClientApplicationForm.AccountType.CORPORATE_TRUST == form.getAccountType())
            && form.hasTrust()) {
            pie = form.getTrust().getPersonalInvestmentEntity();
        }

        return createAccountPropertyListTypeObj(pie, poa);
    }

    private static AccountPropertyListType createAccountPropertyListTypeObj(Boolean pieValue, Boolean poaValue) {
        AccountPropertyListType accountPropertyListTypeObj = null;
        if (pieValue != null || poaValue != null) {
            accountPropertyListTypeObj = new AccountPropertyListType();
            final List<AccountPropertyType> accountPropTypeList = accountPropertyListTypeObj.getAccountProperty();
            if (pieValue != null) {
                final String pieValueStr = pieValue.booleanValue() ? YES_STR : NO_STR;
                accountPropTypeList.add(
                    getAccountPropertyType(
                        CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY, pieValueStr));
            }

            if (poaValue != null) {
                final String poaValueStr = poaValue.booleanValue() ? YES_STR : NO_STR;
                accountPropTypeList.add(
                    getAccountPropertyType(
                        CashManagementAccountType.POWER_OF_ATTORNEY, poaValueStr));
            }
        }
        return accountPropertyListTypeObj;
    }

    private static AccountPropertyType getAccountPropertyType(CashManagementAccountType name, String value) {
        final AccountPropertyType accountPropType = new AccountPropertyType();
        accountPropType.setName(name.toString());
        accountPropType.setValue(value);
        return accountPropType;
    }
}
