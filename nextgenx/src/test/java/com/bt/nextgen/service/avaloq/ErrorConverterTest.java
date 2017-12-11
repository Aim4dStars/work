package com.bt.nextgen.service.avaloq;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.validation.ValidationError;
import com.btfin.abs.trxservice.stex.v1_0.StexRsp;

@RunWith(MockitoJUnitRunner.class)
public class ErrorConverterTest {

    @InjectMocks
    ErrorConverter errorConverter = new ErrorConverter();

    @Mock
    private CmsService cmsService;

    @Test
    public void testToOrderCancelStexResponse_whenErrors() throws Exception {
        StexRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderCancelStexResponseError_UT.xml", StexRsp.class);
        Mockito.when(cmsService.getContent(Mockito.anyString())).thenReturn("Error");
        try {
            List<ValidationError> validations = errorConverter.processErrorList(rsp.getRsp().getExec().getErrList());
            Assert.assertNotNull(validations);
            Assert.assertEquals(2, validations.size());

        } catch (Exception e) {
        }
    }
}
