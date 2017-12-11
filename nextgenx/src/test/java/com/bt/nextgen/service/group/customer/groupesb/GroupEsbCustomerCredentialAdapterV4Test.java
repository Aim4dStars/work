package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.RetrieveChannelAccessCredentialResponse;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserAlternateAliasCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserCredentialDocument;
import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v4.svc0311.UserNameAliasCredentialDocument;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class GroupEsbCustomerCredentialAdapterV4Test {


    private RetrieveChannelAccessCredentialResponse retrieveChannelAccessCredentialErrorResponseV4
            = Mockito.mock(RetrieveChannelAccessCredentialResponse.class, Mockito.RETURNS_DEEP_STUBS);

    @Spy
    private List<StatusInfo> statusInfoList = new ArrayList<>();

    ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class, Mockito.RETURNS_DEEP_STUBS);

    private List<UserCredentialDocument> userCredentialV4 = new ArrayList<>();

    @Before
    public void setUp() throws Exception {


        StatusInfo statusInfo = new StatusInfo();

        statusInfo.setCode("0");
        statusInfo.setLevel(Level.SUCCESS);
        statusInfoList.add(statusInfo);

        //V4 Response
        UserCredentialDocument userCredentialDocumentV4 = new UserCredentialDocument();
        UserAlternateAliasCredentialDocument userAlternateAliasCredentialDocument = new UserAlternateAliasCredentialDocument();
        userAlternateAliasCredentialDocument.setUserId("fake-userid-for-test");
        UserNameAliasCredentialDocument userNameAliasCredentialDocument = new UserNameAliasCredentialDocument();
        userNameAliasCredentialDocument.setUserId("fake-userid-for-test");
        userNameAliasCredentialDocument.setUserAlias("Username123");
        userNameAliasCredentialDocument.setHasAlternateUserNameAlias(userAlternateAliasCredentialDocument);
        userCredentialDocumentV4.setUserName(userNameAliasCredentialDocument);
        userCredentialV4.add(userCredentialDocumentV4);


    }

    @Test
    public void getCustomerCredentials_V4() {


        when(retrieveChannelAccessCredentialErrorResponseV4.getUserCredential()).thenReturn(userCredentialV4);
        when(retrieveChannelAccessCredentialErrorResponseV4.getServiceStatus()).thenReturn(serviceStatus);
        doReturn(statusInfoList).when(serviceStatus).getStatusInfo();
        when(retrieveChannelAccessCredentialErrorResponseV4.getServiceStatus().getStatusInfo()).thenReturn(statusInfoList);
        doReturn(statusInfoList.get(0)).when(statusInfoList).get(0);

        GroupEsbCustomerCredentialAdapter groupEsbCustomerCredentialAdapter =
                new GroupEsbCustomerCredentialAdapter(retrieveChannelAccessCredentialErrorResponseV4, "201601609", new ServiceErrorsImpl());
        assertEquals("201601609", groupEsbCustomerCredentialAdapter.getBankReferenceId());
    }

  }
