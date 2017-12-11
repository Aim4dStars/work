package com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.common.xsd.v1.ProductArrangement;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.InvolvedParty;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.MaintainIPToIPRelationshipsResponse;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.maintainiptoiprelationships.v1.svc0257.MaintenanceAuditContext;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawDataImpl;
import com.bt.nextgen.serviceops.repository.GcmAuditRepository;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CustomerRawDataImpl.class })
public class GroupEsbIpToIpRelationManagementV1ImplTest {

    @InjectMocks
    private GroupEsbIpToIpRelationManagementV1Impl groupEsbIpToIpRelationManagementV1Impl;

    @Mock
    private MaintainIPToIPRelationshipsResponse response;

    @Mock
    private ServiceStatus serviceStatus;

    @Mock
    private ProductArrangement productArrangement;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private CustomerRawData customerRawData;

    @Mock
    private GcmAuditRepository gcmAuditRepository;

    @Mock
    private UserProfileService userProfileService;

    private void runCommonMockServices() throws Exception {
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
        when(userSamlService.getSamlToken()).thenReturn(samlToken);
        when(response.getServiceStatus()).thenReturn(serviceStatus);
        Mockito.when(userProfileService.getUserId()).thenReturn("201603884");
        doNothing().when(gcmAuditRepository).logAuditEntry("userId", "reqType", "message");
    }

    private StatusInfo getStatus(Level level) {
        StatusInfo status = new StatusInfo();
        status.setLevel(level);
        return status;
    }

    @Test
    public void testCreateArrangementAndRelationShip() throws Exception {
        runCommonMockServices();
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        MaintainIPToIPRelationshipsResponse res =
                new MaintainIPToIPRelationshipsResponse();
        ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
        res.setServiceStatus(serviceStatus);
        correlatedResponse.setResponseObject(res);

        PowerMockito.mockStatic(CustomerRawDataImpl.class);
        Mockito.when(CustomerRawDataImpl.getJson(res)).thenReturn("{aaaaa}");

        IpToIpRelationshipRequest req = new IpToIpRelationshipRequest();
        InvolvedParty involvedParty = new InvolvedParty();
        MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
        maintenanceAuditContext.setIsActive(true);
        involvedParty.setAuditContext(maintenanceAuditContext);
        req.setInvolvedParty(involvedParty);


        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.SUCCESS)));

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CustomerRawData customerRawData =
                groupEsbIpToIpRelationManagementV1Impl.maintainIpToIpRelationship(req, serviceErrors);
        assertThat(customerRawData.getRawResponse(), is("{aaaaa}"));
    }

    @Test
    public void testCreateArrangementAndRelationShipWithError() throws Exception {
        runCommonMockServices();
        when(serviceStatus.getStatusInfo()).thenReturn(Arrays.asList(getStatus(Level.ERROR)));
        IpToIpRelationshipRequest req = new IpToIpRelationshipRequest();
        InvolvedParty involvedParty = new InvolvedParty();
        MaintenanceAuditContext maintenanceAuditContext = new MaintenanceAuditContext();
        maintenanceAuditContext.setIsActive(true);
        involvedParty.setAuditContext(maintenanceAuditContext);
        req.setInvolvedParty(involvedParty);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        CorrelatedResponse correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), response);
        when(
                provider.sendWebServiceWithSecurityHeaderAndResponseCallback(any(SamlToken.class), anyString(),
                        anyObject(), any(ServiceErrors.class))).thenReturn(correlatedResponse);
        CustomerRawData customerRawData =
                groupEsbIpToIpRelationManagementV1Impl.maintainIpToIpRelationship(req, serviceErrors);
        Assert.assertEquals(null, customerRawData.getRawResponse());
    }
}
