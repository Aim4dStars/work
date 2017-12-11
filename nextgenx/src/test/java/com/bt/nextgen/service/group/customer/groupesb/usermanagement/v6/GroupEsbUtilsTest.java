package com.bt.nextgen.service.group.customer.groupesb.usermanagement.v6;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ActionCode;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v6.svc0310.ModifyChannelAccessCredentialRequest;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertThat;

/**
 * Created by L075208 on 20/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupEsbUtilsTest {

   @Test
    public void testCreateRequest(){

       ModifyChannelAccessCredentialRequest request = GroupEsbUtils.createRequest("PPID","CredentialID", ActionCode.MODIFY_EAM_EXTENDED_ATTRIBUTES);
       assertThat(request.getUserCredential().get(0).getChannel().getChannelType(), Is.is("ONL"));
       assertThat(request.getUserCredential().get(0).getCredentialIdentifier().getCredentialId(), Is.is("CredentialID"));
       assertThat(request.getUserCredential().get(0).getSourceSystem(), Is.is("EAM"));
       assertThat(request.getRequestedAction(), Is.is(ActionCode.MODIFY_EAM_EXTENDED_ATTRIBUTES));

       assertThat(request.getUserCredential().get(1).getChannel().getChannelType(), Is.is("ONL"));
       assertThat(request.getUserCredential().get(1).getUserName().getUserId(),Is.is("PPID"));
       assertThat(request.getUserCredential().get(1).getSourceSystem(), Is.is("WRAP"));
       assertThat(request.getRequestedAction(), Is.is(ActionCode.MODIFY_EAM_EXTENDED_ATTRIBUTES));

   }
}
