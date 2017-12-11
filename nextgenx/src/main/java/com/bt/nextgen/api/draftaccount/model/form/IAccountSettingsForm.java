package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;

/**
 * Created by m040398 on 14/03/2016.
 */
public interface IAccountSettingsForm {

    public PaymentAuthorityEnum getPaymentSettingForInvestor(int i);
    public Boolean getApproverSettingForInvestor(int i);
    public IOrganisationForm.OrganisationRole getRoleSettingForInvestor(int i);
    public PaymentAuthorityEnum getProfessionalsPayment();
    public boolean isPrimaryContact(int index);
    public Integer getPrimaryContact();
    public String getAdviserName();
    public boolean hasSourceOfFunds();
    public String getSourceOfFunds();
    public String getAdditionalSourceOfFunds();
    public Boolean getPowerOfAttorney();

}
