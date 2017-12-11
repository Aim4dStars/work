package com.bt.nextgen.api.draftaccount.model.form;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AccountSettingsFormTest {


    @Test
     public void getInvestorPayments_WhenIndividualAccountType_ThenReturnAListWithOneInvestorPayment() throws Exception {
        Map investorSetting = new HashMap<>();
        investorSetting.put("paymentSetting", "nopayments");
        investorSetting.put("isApprover", "true");

        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        investorsSettingsList.add(investorSetting);

        Map accountSettings = new HashMap<>();
        accountSettings.put("investorAccountSettings", investorsSettingsList);


        AccountSettingsForm form = new AccountSettingsForm(accountSettings);

        List<Map<String, Object>> investorAccountSettings = form.getInvestorAccountSettings();
        assertThat(investorAccountSettings, hasSize(1));
        assertThat(form.getPaymentSettingForInvestor(0), is(PaymentAuthorityEnum.NOPAYMENTS));
        assertThat(form.getApproverSettingForInvestor(0), is(true));
    }

    @Test
    public void getInvestorPayments_WhenJointAccountType_ThenReturnAListWithAllInvestorsPayments() throws Exception {
        Map investorSetting = new HashMap<>();
        investorSetting.put("paymentSetting", "nopayments");
        investorSetting.put("isApprover", "false");

        Map investorSetting2 = new HashMap<>();
        investorSetting2.put("paymentSetting", "linkedaccountsonly");
        investorSetting2.put("isApprover", "true");

        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        investorsSettingsList.add(investorSetting);
        investorsSettingsList.add(investorSetting2);

        Map accountSettings = new HashMap<>();
        accountSettings.put("investorAccountSettings", investorsSettingsList);

        AccountSettingsForm form = new AccountSettingsForm(accountSettings);

        List<Map<String, Object>> investorAccountSettings = form.getInvestorAccountSettings();
        assertThat(investorAccountSettings, hasSize(2));
        assertThat(form.getPaymentSettingForInvestor(0), is(PaymentAuthorityEnum.NOPAYMENTS));
        assertThat(form.getApproverSettingForInvestor(0), is(false));

        assertThat(form.getPaymentSettingForInvestor(1), is(PaymentAuthorityEnum.LINKEDACCOUNTSONLY));
        assertThat(form.getApproverSettingForInvestor(1), is(true));
    }

    @Test
    public void isPrimaryContact_WhenIndexIsTheSameAsThePrimaryContactInAccountSettings_ThenReturnTrue() throws Exception {
        Map accountSettings = new HashMap<>();
        accountSettings.put("primarycontact", "0");

        AccountSettingsForm form = new AccountSettingsForm(accountSettings);

        assertThat(form.isPrimaryContact(0), is(true));
    }

    @Test
     public void getRoleSettingForInvestor_WhenRoleDirector() {
        AccountSettingsForm form = getAccountSettingsFormWithRole("director");
        assertEquals(IOrganisationForm.OrganisationRole.DIRECTOR, form.getRoleSettingForInvestor(0));
    }

    @Test
    public void getRoleSettingForInvestor_WhenRoleSecretary() {
        AccountSettingsForm form = getAccountSettingsFormWithRole("secretary");
        assertEquals(IOrganisationForm.OrganisationRole.SECRETARY, form.getRoleSettingForInvestor(0));
    }

    @Test
    public void getRoleSettingForInvestor_WhenRoleSignatory() {
        AccountSettingsForm form = getAccountSettingsFormWithRole("signatory");
        assertEquals(IOrganisationForm.OrganisationRole.SIGNATORY, form.getRoleSettingForInvestor(0));
    }

    private AccountSettingsForm getAccountSettingsFormWithRole(String role) {
        Map accountSettings = new HashMap<>();
        Map investorsSetting = new HashMap<String, Object>();
        investorsSetting.put("role", role);
        List investorsSettingsList = new ArrayList<Map<String, Object>>();
        investorsSettingsList.add(investorsSetting);
        accountSettings.put("investorAccountSettings", investorsSettingsList);

        return new AccountSettingsForm(accountSettings);
    }

}
