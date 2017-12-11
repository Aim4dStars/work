package com.bt.nextgen.service.avaloq.corporateaction;

import java.math.BigDecimal;

import com.avaloq.abs.bb.fld_def.NrFld;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Data;
import com.btfin.abs.trxservice.secevt2applydecsn.v1_0.Secevt2ApplyDecsnRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionElectionGroup;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqCorporateActionSubmitIntegrationServiceImplTest {
    @InjectMocks
    private AvaloqCorporateActionElectionIntegrationServiceImpl avaloqCorporateActionSubmitIntegrationService;

    @Mock
    private AvaloqGatewayHelperService webserviceClient;

    @Mock
    private CorporateActionElectionConverter electionConverter;

    private Secevt2ApplyDecsnRsp response;

    @Before
    public void setup() {
        response = new Secevt2ApplyDecsnRsp();

        Data data = new Data();
        NrFld numberField = new NrFld();
        numberField.setVal(new BigDecimal(0));

        data.setDocId(numberField);
        response.setData(data);

        Mockito.when(electionConverter.toSaveElectionGroupRequest(Mockito.any(CorporateActionElectionGroup.class))).thenReturn(null);

        Mockito.when(electionConverter.toSaveElectionResponse(Mockito.any(Secevt2ApplyDecsnRsp.class)))
               .thenReturn(
                       new CorporateActionElectionGroupImpl("0", new CorporateActionSelectedOptionDto(1, null, null, null), null, null));

        Mockito.when(electionConverter.toSaveElectionResponseForIm(Mockito.any(Secevt2ApplyDecsnRsp.class)))
               .thenReturn(
                       new CorporateActionElectionGroupImpl("0", new CorporateActionSelectedOptionDto(1, null, null, null), null, null));
    }

    @Test
    public void testSaveElectionGroup() {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), Mockito.eq(AvaloqOperation.SECEVT2_APPLY_DECSN_REQ),
                Mockito.any(ServiceErrors.class))).thenReturn(response);

        CorporateActionElectionGroup electionGroup = avaloqCorporateActionSubmitIntegrationService.submitElectionGroup(null, null);

        Assert.assertNotNull(electionGroup);
        Assert.assertEquals(electionGroup.getOrderNumber(), "0");

        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), Mockito.eq(AvaloqOperation.SECEVT2_APPLY_DECSN_REQ),
                Mockito.any(ServiceErrors.class))).thenReturn(Integer.valueOf(1));

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        electionGroup = avaloqCorporateActionSubmitIntegrationService.submitElectionGroup(null, serviceErrors);

        Assert.assertNull(electionGroup);
        Assert.assertNotNull(serviceErrors);
        Assert.assertEquals(true, serviceErrors.hasErrors());
    }

    @Test
    public void testSaveElectionGroupForIM() {
        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), Mockito.eq(AvaloqOperation.SECEVT2_APPLY_DECSN_REQ),
                Mockito.any(ServiceErrors.class))).thenReturn(response);

        CorporateActionElectionGroup electionGroup = avaloqCorporateActionSubmitIntegrationService.submitElectionGroupForIm(null, null);

        Assert.assertNotNull(electionGroup);
        Assert.assertEquals(electionGroup.getOrderNumber(), "0");

        Mockito.when(webserviceClient.sendToWebService(Mockito.anyObject(), Mockito.eq(AvaloqOperation.SECEVT2_APPLY_DECSN_REQ),
                Mockito.any(ServiceErrors.class))).thenReturn(Integer.valueOf(1));

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        electionGroup = avaloqCorporateActionSubmitIntegrationService.submitElectionGroupForIm(null, serviceErrors);

        Assert.assertNull(electionGroup);
        Assert.assertNotNull(serviceErrors);
        Assert.assertEquals(true, serviceErrors.hasErrors());
    }
}
