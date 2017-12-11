package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;

import com.bt.nextgen.core.service.ErrorConstants;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.group.customer.CustomerCredentialManagementInformation;
import com.bt.nextgen.web.validator.ValidationErrorCode;

@SuppressWarnings({"squid:S1200", "squid:S00116", "findbugs:SS_SHOULD_BE_STATIC", "squid:RedundantThrowsDeclarationCheck", "squid:S00112", "squid:S1155", "squid:S1168", "squid:S128"})
public class GroupEsbCustomerCredentialManagementAdapter implements CustomerCredentialManagementInformation {
    private au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse modifyChannelAccessCredentialResponseV3;
    private ModifyChannelAccessCredentialResponse modifyChannelAccessCredentialResponseV5;

    private static final String SVC_310_V5_ENABLED = "svc.310.v5.enabled";

    public GroupEsbCustomerCredentialManagementAdapter() {
        //default constructor method
    }

    public GroupEsbCustomerCredentialManagementAdapter(ModifyChannelAccessCredentialResponse response) throws RuntimeException {
        if (response == null) {
            throw new RuntimeException("The ModifyChannelAccessCredentialResponse's Response was null");
        }

        this.modifyChannelAccessCredentialResponseV5 = response;
    }

    public GroupEsbCustomerCredentialManagementAdapter(au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialResponse response) throws RuntimeException {
        if (response == null) {
            throw new RuntimeException("The ModifyChannelAccessCredentialResponse's Response was null");
        }

        this.modifyChannelAccessCredentialResponseV3 = response;
    }

    @Override
    public String getServiceLevel() {
        if (Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
            return modifyChannelAccessCredentialResponseV5.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
        } else {
            return modifyChannelAccessCredentialResponseV3.getServiceStatus().getStatusInfo().get(0).getLevel().toString();
        }

    }

    @Override
    public String getServiceStatusErrorCode() {
        if (Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
            return modifyChannelAccessCredentialResponseV5.getServiceStatus().getStatusInfo().get(0).getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode();
        } else {
            return modifyChannelAccessCredentialResponseV3.getServiceStatus().getStatusInfo().get(0).getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode();
        }
    }

    @Override
    public String getServiceStatusErrorDesc() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getServiceStatus() {
        if (Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
            return modifyChannelAccessCredentialResponseV5.getServiceStatus().toString();
        } else {
            return modifyChannelAccessCredentialResponseV3.getServiceStatus().toString();
        }
    }

    /**
     * Method to parse password service response status in case of error and returns the error key to fetch the value corresponds to that
     * key from cms.
     *
     * @param status ServiceStatus
     * @return String
     */
    @Override
    public String getServiceNegativeResponse() {
        ServiceStatus status = null;
        if (Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
            status = modifyChannelAccessCredentialResponseV5.getServiceStatus();
        } else {
            status = modifyChannelAccessCredentialResponseV3.getServiceStatus();
        }
        if (status != null && status.getStatusInfo() != null && status.getStatusInfo().size() > 0) {
            Level level = status.getStatusInfo().get(0).getLevel();
            switch (level) {
                case ERROR:
                    return getErrorValidationCode(status);
                case WARNING:
                case INFORMATION:
                default:
                    return ValidationErrorCode.FAILED_FORGET_PASSWORD;
            }
        }
        return ValidationErrorCode.FAILED_FORGET_PASSWORD;
    }

    private String getErrorValidationCode(ServiceStatus status) {
        switch (status.getStatusInfo().get(0).getStatusDetail().get(0).getProviderErrorDetail().get(0).getProviderErrorCode()) {
            case ErrorConstants.AUTHENTICATE_USER_FAULT:
                return ValidationErrorCode.INVALID_CURRENT_PASSWORD;

            case ErrorConstants.PWD_POLICY_IN_HISTORY_FAULT:
                return ValidationErrorCode.PASSWORD_NOT_UNIQUE;

            default:
                return ValidationErrorCode.FAILED_FORGET_PASSWORD;
        }
    }

    @Override
    public String getNewPassword() {
        if (Properties.getSafeBoolean(SVC_310_V5_ENABLED)) {
            return modifyChannelAccessCredentialResponseV5.getNewPassword().getPassword();
        } else {
            return modifyChannelAccessCredentialResponseV3.getNewPassword().getPassword();
        }
    }
}
