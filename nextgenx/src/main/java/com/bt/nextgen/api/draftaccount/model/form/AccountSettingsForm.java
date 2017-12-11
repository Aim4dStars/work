package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Should be using the v1 version of this class instead.
 */
@Deprecated
class AccountSettingsForm implements IAccountSettingsForm {

    private final Map<String, Object> map;

    public AccountSettingsForm(Map<String, Object> map) {
        if (map != null) {
            this.map = map;
        } else {
            this.map = new HashMap<>();
        }
    }

    private List<Map<String, Object>> getInvestorsAccountSettings(){
        boolean hasInvestors = this.map.containsKey("investorAccountSettings");
        if(hasInvestors){
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> investorAccountSettings = (List<Map<String, Object>>) this.map.get("investorAccountSettings");
            return new ArrayList<>(investorAccountSettings);
        }
        return new ArrayList<>();
    }

    @Override
    public PaymentAuthorityEnum getPaymentSettingForInvestor(int i){
        return PaymentAuthorityEnum.fromValue( (String) this.getInvestorsAccountSettings().get(i).get("paymentSetting"));
    }

    @Override
    public Boolean getApproverSettingForInvestor(int i){
        final String isApprover =  (String) this.getInvestorsAccountSettings().get(i).get("isApprover");
        return "true".equals(isApprover);
    }

    @Override
    public IOrganisationForm.OrganisationRole getRoleSettingForInvestor(int i){
        String role =  (String) this.getInvestorsAccountSettings().get(i).get("role");
        return IOrganisationForm.OrganisationRole.fromJson(role);
    }

    @Override
    public PaymentAuthorityEnum getProfessionalsPayment() {
        return PaymentAuthorityEnum.fromValue((String) map.get("professionalspayment"));
    }

    @Override
    public boolean isPrimaryContact(int index) {
        Integer primaryContact = getPrimaryContact();
        return primaryContact == null || primaryContact == index;
    }

    @Override
    public Integer getPrimaryContact() {
        if (map.containsKey("primarycontact")) {
            if (map.get("primarycontact") instanceof  String) { //old json (pre-schema)
                return Integer.parseInt((String) map.get("primarycontact"));
            } else {
                return (Integer) map.get("primarycontact"); //new json schema
            }
        }
        return null;
    }

    @Override
    public String getAdviserName() {
        return (String) map.get("adviserName");
    }

    /**
     * Used only by Unit Test class
     * @return
     */
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> getInvestorAccountSettings() {
        return (List<Map<String, Object>>) map.get("investorAccountSettings");
    }

    @Override
    public boolean hasSourceOfFunds() {
        return getSourceOfFunds() != null;
    }

    @Override
    public String getSourceOfFunds() {
        return (String) map.get("sourceoffunds");
    }

    @Override
    public String getAdditionalSourceOfFunds() {
        return (String) map.get("additionalsourceoffunds");
    }

    @Override
    public Boolean getPowerOfAttorney() {
        return (Boolean)map.get("powerofattorney");
    }

}
