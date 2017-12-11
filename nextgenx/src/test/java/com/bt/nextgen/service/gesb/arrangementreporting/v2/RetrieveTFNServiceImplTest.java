package com.bt.nextgen.service.gesb.arrangementreporting.v2;

import au.com.westpac.gn.arrangementreporting.services.arrangementreporting.xsd.retrieveiptaxregistration.v2.svc0610.RetrieveDemandDepositArrangementDetailsResponse;
import au.com.westpac.gn.common.xsd.identifiers.v1.RegistrationIdentifier;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.CorrelationIdWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Created by M040398 (Florin.Adochiei@btfinancialgroup.com) on 15/06/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class RetrieveTFNServiceImplTest {

    @InjectMocks
    private RetrieveTFNServiceImpl retrieveTFNService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private BankingAuthorityService userSamlService;

    private static final String TFN = "876543210";
    private RetrieveDemandDepositArrangementDetailsResponse res;
    private ServiceStatus serviceStatus = Mockito.mock(ServiceStatus.class);
    private CorrelatedResponse correlatedResponse;
    private static final String CIS_KEY = "12456543215";

    @Before
    public void init(){
        res = new RetrieveDemandDepositArrangementDetailsResponse();
        RegistrationIdentifier rid = new RegistrationIdentifier();
        rid.setRegistrationNumber(TFN);
        res.setRegistrationIdentifier(rid);
        res.setServiceStatus(serviceStatus);
        correlatedResponse = new CorrelatedResponse(new CorrelationIdWrapper(), res);

        when(userSamlService.getSamlToken()).thenReturn(new SamlToken(SamlUtil.loadSaml()));
        when(provider
                .sendWebServiceWithSecurityHeaderAndResponseCallback(
                        any(SamlToken.class), anyString(), anyObject(), any(ServiceErrors.class)
                )
        ).thenReturn(correlatedResponse);

    }

    @Test
    public void test_getTFN_when_TFN_exists() {
        when(serviceStatus.getStatusInfo()).thenReturn(getStatusInfo(Level.SUCCESS));
        assertThat(retrieveTFNService.getTFN(CIS_KEY, new ServiceErrorsImpl()), is(TFN));
    }

    @Test
    public void test_getTFN_when_TFN_does_not_exist() {
        when(serviceStatus.getStatusInfo()).thenReturn(getStatusInfo(Level.ERROR));
        assertNull(retrieveTFNService.getTFN(CIS_KEY, new ServiceErrorsImpl()));
    }

    private List<StatusInfo> getStatusInfo(Level level) {
        List<StatusInfo> statusInfo = new ArrayList<StatusInfo>();
        StatusInfo status = new StatusInfo();
        status.setLevel(level);
        statusInfo.add(status);
        return statusInfo;
    }


}
