package com.bt.nextgen.service.avaloq.logout;

import com.avaloq.abs.bb.fld_def.DateTimeFld;
import com.avaloq.abs.bb.fld_def.IdFld;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.abs.trxservice.reguser.v1_0.Data;
import com.btfin.abs.trxservice.reguser.v1_0.RegUserReq;
import com.btfin.abs.trxservice.reguser.v1_0.RegUserRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LogoutUserIntegrationServiceImplTest {

    @InjectMocks
    private LogoutUserIntegrationService logoutUserIntegrationService = new LogoutUserIntegrationServiceImpl();

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Mock
    private AvaloqBankingAuthorityService userSamlService;

    @Mock
    private RegUserRsp regUserRsp;

    @Mock
    private Data data;

    @Mock
    private IdFld idFld;

    @Mock
    private DateTimeFld lastActionDateTimeFld;

    @Mock
    private DateTimeFld lastLoginDateTimeFld;

    private DatatypeFactory factory;

    @Before
    public void setup() throws Exception {
        when(webserviceClient.sendToWebService(any(RegUserReq.class), any(AvaloqOperation.class), any(ServiceErrors.class))).thenReturn(regUserRsp);
        when(regUserRsp.getData()).thenReturn(data);
        when(data.getSecUser()).thenReturn(idFld);
        factory = DatatypeFactory.newInstance();
    }

    @Test
    public void testNotifyUserLogoutWithoutSaml() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        UserLogoutDetail userLogoutDetail = logoutUserIntegrationService.notifyUserLogout(serviceErrors);
        assertThat(userLogoutDetail, is(nullValue()));
    }

    @Test
    public void testNotifyUserLogoutWithSamlAndNullValue() throws Exception {
        when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        UserLogoutDetail userLogoutDetail = logoutUserIntegrationService.notifyUserLogout(serviceErrors);
        assertThat(userLogoutDetail, is(notNullValue()));
        assertThat(userLogoutDetail.getSecUser(), is(nullValue()));
        assertThat(userLogoutDetail.getLoginTime(), is(nullValue()));
        assertThat(userLogoutDetail.getLastActionTime(), is(nullValue()));
    }

    @Test
    public void testNotifyUserLogoutWithSamlAndValue() throws Exception {
        XMLGregorianCalendar lastLoginTime = factory.newXMLGregorianCalendar(2017, 01, 01, 12, 10, 12, DatatypeConstants.FIELD_UNDEFINED, 10);
        XMLGregorianCalendar lastActionTime = factory.newXMLGregorianCalendar(2017, 01, 01, 12, 15, 22, DatatypeConstants.FIELD_UNDEFINED, 10);
        when(data.getLoginTime()).thenReturn(lastLoginDateTimeFld);
        when(data.getLastActionTime()).thenReturn(lastActionDateTimeFld);
        when(lastLoginDateTimeFld.getVal()).thenReturn(lastLoginTime);
        when(lastActionDateTimeFld.getVal()).thenReturn(lastActionTime);
        when(userSamlService.getSamlToken()).thenReturn(new SamlToken(""));
        when(idFld.getVal()).thenReturn("TestUser");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        UserLogoutDetail userLogoutDetail = logoutUserIntegrationService.notifyUserLogout(serviceErrors);
        assertThat(userLogoutDetail, is(notNullValue()));
        assertThat(userLogoutDetail.getSecUser(), is("TestUser"));
        assertThat(userLogoutDetail.getLoginTime().toLocalDateTime().toString(), is("2017-01-01T12:10:12.000"));
        assertThat(userLogoutDetail.getLastActionTime().toLocalDateTime().toString(), is("2017-01-01T12:15:22.000"));
    }

}
