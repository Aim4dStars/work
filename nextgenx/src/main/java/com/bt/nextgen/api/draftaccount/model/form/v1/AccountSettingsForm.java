package com.bt.nextgen.api.draftaccount.model.form.v1;

import org.apache.commons.collections.CollectionUtils;

import com.bt.nextgen.api.draftaccount.model.form.IAccountSettingsForm;
import com.bt.nextgen.api.draftaccount.model.form.IOrganisationForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.accountsettings.AccountSettings;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.BooleanTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;

/**
 * IAccountSettingsForm implementation for JSON schemas v1
 *
 * Created by m040398 on 14/03/2016.
 */
final class AccountSettingsForm implements IAccountSettingsForm {

    private final AccountSettings settings;

    public AccountSettingsForm(AccountSettings settings) {
        this.settings = settings;
    }

    @Override
    public PaymentAuthorityEnum getPaymentSettingForInvestor(int i) {
        return CollectionUtils.isNotEmpty(settings.getInvestorAccountSettings())
            ? settings.getInvestorAccountSettings().get(i).getPaymentSetting() : null;
    }

    @Override
    public Boolean getApproverSettingForInvestor(int i) {
        return CollectionUtils.isNotEmpty(settings.getInvestorAccountSettings())
            ? BooleanTypeEnum.TRUE.equals(settings.getInvestorAccountSettings().get(i).getIsApprover()) : false;
    }

    @Override
    public IOrganisationForm.OrganisationRole getRoleSettingForInvestor(int i) {
        return CollectionUtils.isNotEmpty(settings.getInvestorAccountSettings())
            ? IOrganisationForm.OrganisationRole.fromJson(settings.getInvestorAccountSettings().get(i).getRole().value()) : null;
    }

    @Override
    public PaymentAuthorityEnum getProfessionalsPayment() {
        return settings.getProfessionalspayment();
    }

    @Override
    public boolean isPrimaryContact(int index) {
        Integer primaryContact = getPrimaryContact();
        return primaryContact == null || primaryContact == index;
    }

    @Override
    public Integer getPrimaryContact() {
        return settings.getPrimarycontact();
    }

    @Override
    public String getAdviserName() {
        return settings.getAdviserName();
    }

    @Override
    public boolean hasSourceOfFunds() {
        return getSourceOfFunds() != null;
    }

    @Override
    public String getSourceOfFunds() {
        return settings.getSourceoffunds();
    }

    @Override
    public String getAdditionalSourceOfFunds() {
        return settings.getAdditionalsourceoffunds();
    }

    @Override
    public Boolean getPowerOfAttorney() {
        return settings.getPowerofattorney();
    }
}
