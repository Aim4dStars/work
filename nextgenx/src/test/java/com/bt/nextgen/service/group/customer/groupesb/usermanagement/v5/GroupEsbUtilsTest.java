package com.bt.nextgen.service.group.customer.groupesb.usermanagement.v5;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialRequest;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.core.web.model.User;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GroupEsbUtilsTest {
    private User usernameUpdateRequest;

    @Before
    public void setUp() throws Exception {
        usernameUpdateRequest = new User();
        usernameUpdateRequest.setUserName("userName");
        usernameUpdateRequest.setNewUserName("newUserName");
        usernameUpdateRequest.setCredentialId("321");
    }

    @Test
    public void testCreateModifyChannelAccessCredentialRequest() {
        ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createModifyChannelAccessCredentialRequest(usernameUpdateRequest, true);
        assertThat(request.getUserCredential().getUserName().getUserAlias(), Is.is("newUserName"));
        assertThat(request.getUserCredential().getChannel().getChannelType(), Is.is("ONL"));
        assertThat(request.getUserCredential().getCredentialIdentifier().getCredentialId(), Is.is("321"));
        assertThat(request.getUserCredential().getSourceSystem(), Is.is("EAM"));
        assertThat(request.getRequestedAction(), Is.is(ActionCode.MODIFY_USER_ALIAS));
    }

    @Test
    public void testCreateRequestToBlockUser() {
        ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createRequest("321", ActionCode.LOCK_CREDENTIAL, false);
        assertThat(request.getUserCredential().getChannel().getChannelType(), Is.is("ONL"));
        assertThat(request.getUserCredential().getSourceSystem(), Is.is("EAM"));
        assertThat(request.getUserCredential().getCredentialIdentifier().getCredentialId(), Is.is("321"));
        assertThat(request.getUserCredential().getLifecycleStatus().getLifecycleStatusReason(), Is.is("User requested action"));
        assertThat(request.getRequestedAction(), Is.is(ActionCode.LOCK_CREDENTIAL));
        assertThat(request.getUserCredential().getLifecycleStatus().getStatus(), Is.is("LCKD_P_AC"));
    }

    @Test
    public void testCreateRequestToUnblockUser() {
        ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createRequest("321", ActionCode.REINSTATE_CREDENTIAL, true);
        assertThat(request.getUserCredential().getChannel().getChannelType(), Is.is("ONL"));
        assertThat(request.getUserCredential().getSourceSystem(), Is.is("EAM"));
        assertThat(request.getUserCredential().getCredentialIdentifier().getCredentialId(), Is.is("321"));
        assertThat(request.getUserCredential().getLifecycleStatus().getLifecycleStatusReason(), Is.is("User requested action"));
        assertThat(request.getRequestedAction(), Is.is(ActionCode.REINSTATE_CREDENTIAL));
        assertThat(request.getPasswordDeliveryMethod(), Is.is("VOICE"));
        assertThat(request.isResetPassword(), Is.is(true));
    }

    @Test
    public void testCreateRequestToUnblockUserWithoutResettingPassword() {
        ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createRequest("321", ActionCode.REINSTATE_CREDENTIAL, false);
        assertThat(request.getUserCredential().getChannel().getChannelType(), Is.is("ONL"));
        assertThat(request.getUserCredential().getSourceSystem(), Is.is("EAM"));
        assertThat(request.getUserCredential().getCredentialIdentifier().getCredentialId(), Is.is("321"));
        assertThat(request.getUserCredential().getLifecycleStatus().getLifecycleStatusReason(), Is.is("User requested action"));
        assertThat(request.getRequestedAction(), Is.is(ActionCode.REINSTATE_CREDENTIAL));
    }

    @Test
    public void testCreateRequestToRefreshCredential() {
        ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createRequest("321", ActionCode.REFRESH_CREDENTIAL, "12345678", "websealAppServerId");
        assertThat(request.getUserCredential().getChannel().getChannelType(), Is.is("ONL"));
        assertThat(request.getUserCredential().getSourceSystem(), Is.is("EAM"));
        assertThat(request.getUserCredential().getCredentialIdentifier().getCredentialId(), Is.is("321"));
        assertThat(request.getUserCredential().getLifecycleStatus().getLifecycleStatusReason(), Is.is("User requested action"));
        assertThat(request.getUserCredential().getUserName().getUserId(), Is.is("12345678"));
        assertThat(request.getRequestedAction(), Is.is(ActionCode.REFRESH_CREDENTIAL));
        assertThat(request.getWebsealApplicationServerIdentifier().getApplicationServerId(), is("websealAppServerId"));
        assertThat(request.getUserCredential().getLifecycleStatus().getStatus(), Is.is("ACTV"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRequestToRefreshCredential_failed() {
        GroupEsbUtils.createRequest("321", ActionCode.REFRESH_CREDENTIAL, null, "websealAppServerId");
    }
}