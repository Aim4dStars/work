package com.bt.nextgen.service.group.customer.groupesb.usermanagement.v3;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.Channel;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.LifeCycleStatus;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ModifyChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.ObjectFactory;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.UserCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v3.svc0310.UserNameAliasCredentialDocument;
import au.com.westpac.gn.common.xsd.identifiers.v1.CredentialIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.group.customer.CustomerUsernameUpdateRequest;
import com.bt.nextgen.service.group.customer.ServiceConstants;

public final class GroupEsbUtils {
    private static final Logger logger = LoggerFactory.getLogger(GroupEsbUtils.class);

    private GroupEsbUtils() {}

    public static ModifyChannelAccessCredentialRequest createModifyChannelAccessCredentialRequest(CustomerUsernameUpdateRequest usernameUpdateRequest, boolean isUpdate) {
        ObjectFactory of = new ObjectFactory();
        ModifyChannelAccessCredentialRequest request = of.createModifyChannelAccessCredentialRequest();

        if (isUpdate) {
            request.setRequestedAction(ActionCode.MODIFY_USER_ALIAS);
        }

        UserCredentialDocument userCredential = new UserCredentialDocument();

        Channel channel = new Channel();
        channel.setChannelType(Properties.get(ServiceConstants.EAM_CREDENTIAL_CHANNEL));
        userCredential.setChannel(channel);

        UserNameAliasCredentialDocument userNameAlias = new UserNameAliasCredentialDocument();
        userNameAlias.setUserAlias(usernameUpdateRequest.getNewUserName());
        userCredential.setUserName(userNameAlias);

        CredentialIdentifier regIdentifier = new CredentialIdentifier();
        regIdentifier.setCredentialId(usernameUpdateRequest.getCredentialId());
        userCredential.setCredentialIdentifier(regIdentifier);
        userCredential.setSourceSystem(Properties.get(ServiceConstants.EAM_CREDENTIAL_SOURCE));

        request.setUserCredential(userCredential);

        return request;
    }

    public static ModifyChannelAccessCredentialRequest createRequest(String credentialId, ActionCode actionCode, boolean isResetPassword) {
        UserCredentialDocument userCredential = new UserCredentialDocument();
        Channel channel = new Channel();
        channel.setChannelType(Properties.get(ServiceConstants.EAM_CREDENTIAL_CHANNEL));
        userCredential.setChannel(channel);
        userCredential.setSourceSystem(Properties.get(ServiceConstants.EAM_CREDENTIAL_SOURCE));
        CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
        credentialIdentifier.setCredentialId(credentialId);
        userCredential.setLifecycleStatusReason(ServiceConstants.LIFECYCLE_STATUS_REASON);
        userCredential.setCredentialIdentifier(credentialIdentifier);

        ObjectFactory of = new ObjectFactory();
        ModifyChannelAccessCredentialRequest request = of.createModifyChannelAccessCredentialRequest();
        request.setRequestedAction(actionCode);
        request.setUserCredential(userCredential);

        switch (actionCode) {
            case REINSTATE_CREDENTIAL:
                setResetPasswordRequestParams(request, isResetPassword);
                break;
            case LOCK_CREDENTIAL:
                setLockCredentialParams(request);
                break;
            default:
                //do nothing
                break;
        }
        logger.info("GroupEsbCustomerUserNameManagementImpl.createRequest(): Returning request created for action: {}", actionCode);
        return request;
    }

    private static void setLockCredentialParams(ModifyChannelAccessCredentialRequest request) {
        LifeCycleStatus lifeCycleStatus = new LifeCycleStatus();
        lifeCycleStatus.setStatus(ServiceConstants.BLOCK_STATUS_CODE);
        request.getUserCredential().setLifecycleStatus(lifeCycleStatus);
    }

    private static void setResetPasswordRequestParams(ModifyChannelAccessCredentialRequest request, boolean isResetPassword) {
        if (isResetPassword) {
            request.setResetPassword(Boolean.TRUE);
            request.setPasswordDeliveryMethod(ServiceConstants.VOICE);
        }
    }
}
