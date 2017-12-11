package com.bt.nextgen.service.group.customer.groupesb.usermanagement.v5;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.Channel;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.LifeCycleStatus;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ObjectFactory;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.UserCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.UserNameAliasCredentialDocument;
import au.com.westpac.gn.common.xsd.identifiers.v1.ApplicationServerIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.CredentialIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.group.customer.CustomerUsernameUpdateRequest;
import com.bt.nextgen.service.group.customer.ServiceConstants;

public final class GroupEsbUtils {
    private static final Logger logger = LoggerFactory.getLogger(GroupEsbUtils.class);

    private GroupEsbUtils(){}

    public static ModifyChannelAccessCredentialRequest createModifyChannelAccessCredentialRequest(CustomerUsernameUpdateRequest usernameUpdateRequest, boolean isUpdate) {
        ObjectFactory of = new ObjectFactory();
        ModifyChannelAccessCredentialRequest request = of.createModifyChannelAccessCredentialRequest();

        if (isUpdate) {
            request.setRequestedAction(ActionCode.MODIFY_USER_ALIAS);
        }

        UserCredentialDocument userCredential = new UserCredentialDocument();

        Channel channeltype = new Channel();
        channeltype.setChannelType(Properties.get(ServiceConstants.EAM_CREDENTIAL_CHANNEL));
        userCredential.setChannel(channeltype);

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

    public static ModifyChannelAccessCredentialRequest createRequest(String credentialId, ActionCode actionCode) {
        UserCredentialDocument userCredential = new UserCredentialDocument();
        Channel channel = new Channel();
        channel.setChannelType(Properties.get(ServiceConstants.EAM_CREDENTIAL_CHANNEL));
        userCredential.setChannel(channel);
        userCredential.setSourceSystem(Properties.get(ServiceConstants.EAM_CREDENTIAL_SOURCE));
        CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
        credentialIdentifier.setCredentialId(credentialId);
        userCredential.setCredentialIdentifier(credentialIdentifier);
        ObjectFactory of = new ObjectFactory();
        ModifyChannelAccessCredentialRequest request = of.createModifyChannelAccessCredentialRequest();
        request.setRequestedAction(actionCode);
        request.setUserCredential(userCredential);
        LifeCycleStatus lifeCycleStatus = new LifeCycleStatus();
        lifeCycleStatus.setLifecycleStatusReason(ServiceConstants.LIFECYCLE_STATUS_REASON);

        switch (actionCode) {
            case LOCK_CREDENTIAL:
                lifeCycleStatus.setStatus(ServiceConstants.BLOCK_STATUS_CODE);
                logger.info("GroupEsbCustomerUserNameManagementImpl.createRequest(): Returning request created for block user.");
                break;
            case REINSTATE_CREDENTIAL:
            case REFRESH_CREDENTIAL:
            default:
                lifeCycleStatus.setStatus(ServiceConstants.ACTIVE_STATUS_CODE);
                logger.info("GroupEsbCustomerUserNameManagementImpl.createRequest(): Returning request created for {}.", actionCode);
                break;
        }
        request.getUserCredential().setLifecycleStatus(lifeCycleStatus);
        return request;
    }

    public static ModifyChannelAccessCredentialRequest createRequest(String credentialId, ActionCode actionCode, boolean isResetPassword) {
        ModifyChannelAccessCredentialRequest request = createRequest(credentialId, actionCode);
        if (isResetPassword) {
            request.setResetPassword(Boolean.TRUE);
            request.setPasswordDeliveryMethod(ServiceConstants.VOICE);
        }
        return request;
    }

    /**
     * For refresh credential service, we need to set the westpac customer/Z number for the user
     * @param credentialId
     * @param actionCode
     * @param westpacCustomerNumber
     * @param websealAppServerId
     * @return
     */
    public static ModifyChannelAccessCredentialRequest createRequest(String credentialId, ActionCode actionCode, String westpacCustomerNumber, String websealAppServerId) {
        if (StringUtils.isBlank(westpacCustomerNumber)) {
            throw new IllegalArgumentException("Westpac customer number must not be null.");
        }

        ModifyChannelAccessCredentialRequest request = createRequest(credentialId, actionCode);
        UserNameAliasCredentialDocument userNameDocument = new UserNameAliasCredentialDocument();
        userNameDocument.setUserId(westpacCustomerNumber);
        request.getUserCredential().setUserName(userNameDocument);
        ApplicationServerIdentifier applicationServerIdentifier = new ApplicationServerIdentifier();
        applicationServerIdentifier.setApplicationServerId(websealAppServerId);
        request.setWebsealApplicationServerIdentifier(applicationServerIdentifier);
        return request;
    }    
}
