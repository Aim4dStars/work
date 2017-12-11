package com.bt.nextgen.service.group.customer.groupesb;

import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

/*import org.mockito.Mockito;
import org.mockito.Spy;*/


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Properties.class })
public class GroupEsbCustomerCredentialAdapterV3Test {


   private au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.
            svc0311.RetrieveChannelAccessCredentialResponse retrieveChannelAccessCredentialErrorResponseV3 =
            Mockito.mock(au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.
                    svc0311.RetrieveChannelAccessCredentialResponse.class, Mockito.RETURNS_DEEP_STUBS);
    @Spy
    private List<StatusInfo> statusInfoList = new ArrayList<>();

    ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class, Mockito.RETURNS_DEEP_STUBS);

    private List<au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.
            retrievechannelaccesscredential.v3.svc0311.UserCredentialDocument> userCredentialV3 = new ArrayList<>();

    @Before
    public void setUp() throws Exception {


        StatusInfo statusInfo = new StatusInfo();

        statusInfo.setCode("0");
        statusInfo.setLevel(Level.SUCCESS);
        statusInfoList.add(statusInfo);

        //V3 Response
        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.
                retrievechannelaccesscredential.v3.svc0311.UserCredentialDocument userCredentialDocumentV3 =
                new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.
                        retrievechannelaccesscredential.v3.svc0311.UserCredentialDocument();
        au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.
                retrievechannelaccesscredential.v3.svc0311.UserNameAliasCredentialDocument userNameAliasCredentialDocumentV3
                = new au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.retrievechannelaccesscredential.v3.svc0311.UserNameAliasCredentialDocument();
        userNameAliasCredentialDocumentV3.setUserId("fake-userid-for-test");
        userNameAliasCredentialDocumentV3.setUserAlias("Username123");
        userCredentialDocumentV3.setUserName(userNameAliasCredentialDocumentV3);
        userCredentialV3.add(userCredentialDocumentV3);

        PowerMockito.mockStatic(Properties.class);
        when(Properties.getSafeBoolean("svc.311.v4.enabled")).thenReturn(false);
        when(Properties.getSafeBoolean("gesb-retrieve-credentials.webservice.filestub")).thenReturn(true);
    }

   @Test
   public void getCustomerCredentials_V3() {
        when(retrieveChannelAccessCredentialErrorResponseV3.getUserCredential()).thenReturn(userCredentialV3);
        when(retrieveChannelAccessCredentialErrorResponseV3.getServiceStatus()).thenReturn(serviceStatus);
        doReturn(statusInfoList).when(serviceStatus).getStatusInfo();
        when(retrieveChannelAccessCredentialErrorResponseV3.getServiceStatus().getStatusInfo()).thenReturn(statusInfoList);
        doReturn(statusInfoList.get(0)).when(statusInfoList).get(0);

       GroupEsbCustomerCredentialAdapter groupEsbCustomerCredentialAdapter =
                new GroupEsbCustomerCredentialAdapter(retrieveChannelAccessCredentialErrorResponseV3, "201601609", new ServiceErrorsImpl());
        assertEquals("201601609", groupEsbCustomerCredentialAdapter.getBankReferenceId());
    }

}
