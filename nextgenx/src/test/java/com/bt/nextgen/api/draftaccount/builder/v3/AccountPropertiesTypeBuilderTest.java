package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.AbstractJsonReaderTest;
import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.v1.ClientApplicationFormFactoryV1;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AccountTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.DirectAccountTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.TrustTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.trust.TrustDetails;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.service.integration.account.CashManagementAccountType;
import com.fasterxml.jackson.databind.ObjectMapper;
import ns.btfin_com.product.common.investmentaccount.v2_0.AccountPropertyListType;
import ns.btfin_com.product.common.investmentaccount.v2_0.AccountPropertyType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountPropertiesTypeBuilderTest extends AbstractJsonReaderTest {

    private static final String JSON_MOCK_PACKAGE = "com/bt/nextgen/api/draftaccount/model/";
    private static final String V3_JSON_SCHEMA_MOCK_PACKAGE = "com/bt/nextgen/api/draftaccount/builder/v3_JsonSchema/";

    @Before
    public void setup() {
    }

    private String getAccountPropertyTypeValue(List<AccountPropertyType> acctPropTypeList, String pieOrPoa) {
        String value = null;
        for (AccountPropertyType acctPropTypeObj : acctPropTypeList) {
            if (pieOrPoa.equals(acctPropTypeObj.getName())) {
                value = acctPropTypeObj.getValue();
            }
        }
        return value;
    }

    private IClientApplicationForm getClientApplicationForm(String jsonFilePath) throws IOException {
        ObjectMapper jsonObjectMapper = new JsonObjectMapper();
        Object formData = jsonObjectMapper.readValue(readJsonStringFromFile(jsonFilePath), OnboardingApplicationFormData.class);
        return ClientApplicationFormFactoryV1.getNewClientApplicationForm((OnboardingApplicationFormData) formData);
    }

    @Test
    public void accountPropertiesTypeForDirectInvestor() throws IOException {
        ObjectMapper jsonObjectMapper = new JsonObjectMapper();
        String jsonRequest = readJsonStringFromFile(V3_JSON_SCHEMA_MOCK_PACKAGE + "directIndividual_crsData.json");
        DirectClientApplicationFormData directClientApplicationFormData = jsonObjectMapper.readValue(jsonRequest, DirectClientApplicationFormData.class);
        directClientApplicationFormData.setAccountType(DirectAccountTypeEnum.INDIVIDUAL);

        IClientApplicationForm form = ClientApplicationFormFactoryV1.getNewDirectClientApplicationForm(directClientApplicationFormData);

        final AccountPropertyListType accountPropertiesTypeObj = AccountPropertiesTypeBuilder.getAccountPropertyListType(form);
        assertNull(accountPropertiesTypeObj);
    }

    @Test
    public void accountPropertiesTypeForCompany() throws IOException {
        IClientApplicationForm form = getClientApplicationForm(JSON_MOCK_PACKAGE + "company_cma.json");

        final AccountPropertyListType accountPropertiesTypeObj = AccountPropertiesTypeBuilder.getAccountPropertyListType(form);
        assertNotNull(accountPropertiesTypeObj);
        assertNotNull(accountPropertiesTypeObj.getAccountProperty().get(0));
        assertNotNull(accountPropertiesTypeObj.getAccountProperty().get(1));
        assertEquals(
            getAccountPropertyTypeValue(accountPropertiesTypeObj.getAccountProperty(),
                CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY.toString()),
            "yes");
        assertEquals(
            getAccountPropertyTypeValue(accountPropertiesTypeObj.getAccountProperty(),
                CashManagementAccountType.POWER_OF_ATTORNEY.toString()),
            "yes");
    }

    @Test
    public void accountPropertiesTypeForIndividualOrJointOrIndSMSFOrCorpSMSF() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        String acctSettingsJsonStr = " {\"accountsettings\": {\"professionalspayment\": \"linkedaccountsonly\","
            + "	\"investorAccountSettings\": [{"
            + "		\"paymentSetting\": \"nopayments\","
            + "		\"hasRoles\": false,"
            + "		\"hasApprovers\": false"
            + "	}],"
            + "	\"primarycontact\": 0,"
            + "	\"adviserName\": \"Edward Revis\","
            + "	\"adviserLocation\": \"New South Wales\","
            + "	\"sourceoffunds\": \"Business income/earnings\","
            + "	\"powerofattorney\": false"
            + "}}";

        OnboardingApplicationFormData formData = mapper.readValue(acctSettingsJsonStr, OnboardingApplicationFormData.class);
        formData.setAccountType(AccountTypeEnum.INDIVIDUAL);

        IClientApplicationForm form = ClientApplicationFormFactoryV1.getNewClientApplicationForm(formData);

        final AccountPropertyListType accountPropertiesTypeObj = AccountPropertiesTypeBuilder.getAccountPropertyListType(form);
        assertNotNull(accountPropertiesTypeObj);
        assertNotNull(accountPropertiesTypeObj.getAccountProperty().get(0));
        assertNull(getAccountPropertyTypeValue(accountPropertiesTypeObj.getAccountProperty(),
            CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY.toString()));
        assertEquals(
            getAccountPropertyTypeValue(accountPropertiesTypeObj.getAccountProperty(),
                CashManagementAccountType.POWER_OF_ATTORNEY.toString()),
            "no");
    }

    @Test
    public void accountPropertiesTypeForTrust_GovtSuperOrRegisteredMIS() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        String acctSettingsJsonStr = " {\"accountsettings\": {\"professionalspayment\": \"linkedaccountsonly\","
            + "	\"investorAccountSettings\": [{"
            + "		\"paymentSetting\": \"nopayments\","
            + "		\"hasRoles\": false,"
            + "		\"hasApprovers\": false"
            + "	}],"
            + "	\"primarycontact\": 0,"
            + "	\"adviserName\": \"Edward Revis\","
            + "	\"adviserLocation\": \"New South Wales\","
            + "	\"sourceoffunds\": \"Business income/earnings\","
            + "	\"powerofattorney\": false"
            + "}}";

        OnboardingApplicationFormData formData = mapper.readValue(acctSettingsJsonStr, OnboardingApplicationFormData.class);
        TrustTypeEnum trustType = TrustTypeEnum.GOVSUPER;
        formData.setAccountType(AccountTypeEnum.INDIVIDUAL_TRUST);
        formData.setTrustType(trustType);
        TrustDetails trustDetails = new TrustDetails();
        trustDetails.setTrusttype(trustType);

        formData.setTrustdetails(trustDetails);

        IClientApplicationForm form = ClientApplicationFormFactoryV1.getNewClientApplicationForm(formData);

        final AccountPropertyListType accountPropertiesTypeObj = AccountPropertiesTypeBuilder.getAccountPropertyListType(form);
        assertNotNull(accountPropertiesTypeObj);
        assertNotNull(accountPropertiesTypeObj.getAccountProperty().get(0));
        assertNull(getAccountPropertyTypeValue(accountPropertiesTypeObj.getAccountProperty(),
            CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY.toString()));
        assertEquals(
            getAccountPropertyTypeValue(accountPropertiesTypeObj.getAccountProperty(),
                CashManagementAccountType.POWER_OF_ATTORNEY.toString()),
            "no");
    }

    @Test
    public void accountPropertiesTypeForTrust_FamilyOrRegulatedOrOther() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        String acctSettingsJsonStr = " {\"accountsettings\": {\"professionalspayment\": \"linkedaccountsonly\","
            + "	\"investorAccountSettings\": [{"
            + "		\"paymentSetting\": \"nopayments\","
            + "		\"hasRoles\": false,"
            + "		\"hasApprovers\": false"
            + "	}],"
            + "	\"primarycontact\": 0,"
            + "	\"adviserName\": \"Edward Revis\","
            + "	\"adviserLocation\": \"New South Wales\","
            + "	\"sourceoffunds\": \"Business income/earnings\","
            + "	\"powerofattorney\": false"
            + "}}";

        OnboardingApplicationFormData formData = mapper.readValue(acctSettingsJsonStr, OnboardingApplicationFormData.class);
        formData.setAccountType(AccountTypeEnum.INDIVIDUAL_TRUST);
        formData.setTrustType(TrustTypeEnum.FAMILY);
        TrustDetails trustDetails = new TrustDetails();
        trustDetails.setPersonalinvestmententity(Boolean.TRUE);
        trustDetails.setTrusttype(formData.getTrustType());

        formData.setTrustdetails(trustDetails);

        IClientApplicationForm form = ClientApplicationFormFactoryV1.getNewClientApplicationForm(formData);

        final AccountPropertyListType accountPropertiesTypeObj = AccountPropertiesTypeBuilder.getAccountPropertyListType(form);
        assertNotNull(accountPropertiesTypeObj);
        assertNotNull(accountPropertiesTypeObj.getAccountProperty().get(0));
        assertNotNull(accountPropertiesTypeObj.getAccountProperty().get(1));
        assertEquals(
            getAccountPropertyTypeValue(accountPropertiesTypeObj.getAccountProperty(),
                CashManagementAccountType.PERSONAL_INVESTMENT_ENTITY.toString()),
            "yes");
        assertEquals(
            getAccountPropertyTypeValue(accountPropertiesTypeObj.getAccountProperty(),
                CashManagementAccountType.POWER_OF_ATTORNEY.toString()),
            "no");
    }

    @Test
    public void accountPropertiesTypeForDefaultCase(){
        IClientApplicationForm form = Mockito.mock(IClientApplicationForm.class);
        when(form.getAccountType()).thenReturn(IClientApplicationForm.AccountType.UNKNOWN);
        when(form.isDirectAccount()).thenReturn(Boolean.FALSE);
        when(form.getAccountSettings()).thenReturn(null);
        final AccountPropertyListType accountPropertiesTypeObj = AccountPropertiesTypeBuilder.getAccountPropertyListType(form);
        assertNull(accountPropertiesTypeObj);

    }

}