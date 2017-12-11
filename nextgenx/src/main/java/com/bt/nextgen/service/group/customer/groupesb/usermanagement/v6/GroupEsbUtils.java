package com.bt.nextgen.service.group.customer.groupesb.usermanagement.v6;

/**
 * Created by L075208 on 19/07/2016.
 */
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.Channel;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.LifeCycleStatus;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ModifyChannelAccessCredentialRequest;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ObjectFactory;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.UserCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.UserNameAliasCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserAlternateAliasCredentialDocument;
import au.com.westpac.gn.common.xsd.identifiers.v1.ApplicationServerIdentifier;
import au.com.westpac.gn.common.xsd.identifiers.v1.CredentialIdentifier;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.group.customer.ServiceConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class GroupEsbUtils {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbUtils.class);

    private GroupEsbUtils(){}

    public static ModifyChannelAccessCredentialRequest createRequest(String PPID , String credentialId , ActionCode actionCode){
        UserCredentialDocument userCredentialDocument = new UserCredentialDocument();
        Channel channel = new Channel();
        channel .setChannelType(Properties.get(ServiceConstants.EAM_CREDENTIAL_CHANNEL));
        userCredentialDocument.setChannel(channel);
        userCredentialDocument.setSourceSystem(Properties.get(ServiceConstants.EAM_CREDENTIAL_SOURCE));
        CredentialIdentifier credentialIdentifier = new CredentialIdentifier();
        credentialIdentifier.setCredentialId(credentialId);
        userCredentialDocument.setCredentialIdentifier(credentialIdentifier);
        ObjectFactory of = new ObjectFactory();
        ModifyChannelAccessCredentialRequest request = of.createModifyChannelAccessCredentialRequest();
        request.setRequestedAction(actionCode);
        request.getUserCredential().add(userCredentialDocument);

        UserCredentialDocument userCredentialDocument1 = new UserCredentialDocument();
        channel = new Channel();
        channel.setChannelType(Properties.get(ServiceConstants.EAM_CREDENTIAL_CHANNEL));
        userCredentialDocument1.setChannel(channel);
        userCredentialDocument1.setSourceSystem(Properties.get(ServiceConstants.WRAP_CREDENTIAL_SOURCE));
        UserNameAliasCredentialDocument userNameAliasCredentialDocument = new UserNameAliasCredentialDocument();
        userNameAliasCredentialDocument.setUserId(PPID);
        userCredentialDocument1.setUserName(userNameAliasCredentialDocument);
        request.getUserCredential().add(userCredentialDocument1);

       return  request;
    }
}
