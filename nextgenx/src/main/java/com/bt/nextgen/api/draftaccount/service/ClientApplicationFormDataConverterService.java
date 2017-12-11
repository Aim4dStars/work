package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.util.GenderMapperUtil;
import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.apache.commons.lang.StringUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.api.draftaccount.FormDataConstants.*;

@Service
@Transactional
@Deprecated
public class ClientApplicationFormDataConverterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationFormDataConverterService.class);

    @Autowired
    private UserProfileService userProfileService;

    public ClientApplicationFormDataConverterService() {
        // used by spring for instantiation
    }

    // used only for ProcessInvestorApplicationRequestMsgTypeBuilderV3Test since its structured differently
    public ClientApplicationFormDataConverterService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Deprecated
    public Object convertFormDataForDirect(Map<String, Object> formData) {
        Map<String, Object> convertedFormData = new HashMap<>();
        convertedFormData.put(FIELD_INVESTORS, getInvestors(formData));
        convertedFormData.put(FIELD_LINKEDACCOUNTS, getLinkedBankAccountDetails((Map<String, Object>) formData.get("linkedaccount")));
        convertedFormData.put(FIELD_INVESTMENTOPTIONS, formData.get("investmentoptions"));
        convertedFormData.put(FIELD_ACCOUNT_TYPE, "individual");
        convertedFormData.put(FIELD_APPLICATION_ORIGIN, FormDataConstants.VALUE_APPLICATION_ORIGIN_DIRECT);
        convertedFormData.put(FIELD_ADVICE_TYPE, "NoAdvice");
        convertedFormData.put(FIELD_ACCOUNTSETTINGS, getAccountSettingDetails());
        convertedFormData.put(FIELD_FEES, getFees());
        return convertedFormData;
    }

    private Map<String, Object> getFees() {
        Map<String, Object> fees = new HashMap<>();
        fees.put(FIELD_ESTAMOUNT, "0.00");
        return fees;
    }

    private List<Map<String, Object>> getInvestors(Map<String, Object> submissionData) {
        Map<String, Object> individualDetails = (Map<String, Object>) submissionData.get("individualDetails");
        Map<String, Object> investorDetails = (Map<String, Object>) submissionData.get("investordetails");

        Map<String, Object> investor = new HashMap<>();
        setInvestorKeys(submissionData, individualDetails, investor);
        if (investorDetails != null) {
            investor.putAll(investorDetails);
            String mobileNumber = getMobileNumber((String) investorDetails.get("encodedMobile"));
            investor.put(FIELD_MOBILE, getContactDetail(mobileNumber));
            investor.put(FIELD_EMAIL, getContactDetail((String) investorDetails.get(FIELD_EMAIL)));
        }

        investor.put(FIELD_TITLE, individualDetails.get(FIELD_TITLE));
        investor.put(FIELD_FIRSTNAME, individualDetails.get("firstName"));
        investor.put(FIELD_LASTNAME, individualDetails.get("lastName"));
        investor.put(FIELD_PREFERREDNAME, submissionData.get("preferredName"));
        investor.put(FIELD_DATEOFBIRTH, individualDetails.get("dateOfBirth"));
        investor.put(FIELD_ID_VERIFIED, individualDetails.get(FIELD_ID_VERIFIED));

        investor.put(FIELD_GENDER, GenderMapperUtil.getGenderFromGCMGenderCode((String) individualDetails.get(FIELD_GENDER)));
        Map<String, Object> address = getAddress(submissionData);
        investor.put(FIELD_RESADDRESS, address);
        investor.put(FIELD_POSTALADDRESS, address);

        investor.put(FIELD_PREFERREDCONTACT, "mobile");
        List<Map<String, Object>> investors = new ArrayList<>();
        investors.add(investor);

        return investors;
    }

    private void setInvestorKeys(Map<String, Object> submissionData, Map<String, Object> individualDetails, Map<String, Object> investor) {
        investor.put(FIELD_CIS_ID, submissionData.get(FIELD_CIS_ID));
        investor.put(FIELD_USER_NAME, individualDetails.get(FIELD_USER_NAME));
        if (StringUtils.isNotEmpty((String) submissionData.get(FIELD_CLIENT_ID))) {
            Map<String, Object> clientKey = new HashMap<>();
            clientKey.put(FIELD_CLIENT_ID, submissionData.get(FIELD_CLIENT_ID));
            investor.put(FIELD_KEY, clientKey);
            // assuming this is an existing panorama client since it has client Id
            investor.put(FormDataConstants.FIELD_GCM_ID, userProfileService.getGcmId());
        }
    }

    private String getMobileNumber(String mobileNumber) {
        try {
            return EncodedString.toPlainText(mobileNumber);

        } catch (EncryptionOperationNotPossibleException e) {
            LOGGER.error("The following number could not be decrypted: {}. Error: {}", mobileNumber, e);
            return "";
        }
    }

    private Map<String, Object> getAddress(Map<String, Object> submissionData) {
        Map<String, Object> address = (Map<String, Object>) submissionData.get("address");
        address.put(FIELD_COMPONENTISED, address.get("standardAddressFormat"));
        address.put(FIELD_PIN, address.get("postcode"));
        return address;
    }

    private Map<String, Object> getContactDetail(String value) {
        Map<String, Object> contactDetails = new HashMap<>();
        contactDetails.put("value", value);
        return contactDetails;
    }

    private Map<String, Object> getAccountSettingDetails() {
        Map<String, Object> accountSettings = new HashMap<>();

        List<Map<String, Object>> investorsAccountSettings = new ArrayList<>();
        Map<String, Object> investorsAccountSetting = new HashMap<>();
        investorsAccountSetting.put(FIELD_PAYMENT_SETTING, PaymentAuthorityEnum.ALLPAYMENTS.value());
        investorsAccountSettings.add(investorsAccountSetting);

        accountSettings.put(FIELD_INVESTOR_ACCOUNT_SETTINGS, investorsAccountSettings);
        accountSettings.put(FIELD_PROFESSIONALSPAYMENT, PaymentAuthorityEnum.NOPAYMENTS.value());
        return accountSettings;
    }

    private Map<String, Object> getLinkedBankAccountDetails(Map<String, Object> linkedBankAccountDetails) {
        Map<String, Object> linkedAccount = new HashMap<>();
        linkedAccount.put(FIELD_PRIMARY_LINKED_ACCOUNT, linkedBankAccountDetails);
        return linkedAccount;
    }
}

