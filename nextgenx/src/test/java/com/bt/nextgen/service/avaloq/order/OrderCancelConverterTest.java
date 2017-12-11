package com.bt.nextgen.service.avaloq.order;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.btfin.abs.err.v1_0.ErrList;
import com.btfin.abs.trxservice.canceldoc.v1_0.CancelDocReq;
import com.btfin.abs.trxservice.canceldoc.v1_0.CancelDocRsp;

@RunWith(MockitoJUnitRunner.class)
public class OrderCancelConverterTest {
    @InjectMocks
    OrderCancelConverter orderCancelConverter = new OrderCancelConverter();

    @Mock
    private ErrorConverter errorConverter;

    private List<ValidationError> validations = new ArrayList<ValidationError>();

    @Before
    public void setup() {
        Mockito.when(errorConverter.processErrorList(Mockito.any(ErrList.class))).thenReturn(validations);
    }

    @Test
    public void testToOrderCancelRequest_whenSuppliedOrderId_thenCancelReqMatches() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        BigInteger orderId = BigInteger.valueOf(112345L);
        BigInteger lastTranSeqId = BigInteger.valueOf(3L);
        CancelDocReq request = (CancelDocReq) orderCancelConverter.toOrderCancelRequest(orderId, lastTranSeqId, serviceErrors);
        Assert.assertEquals(orderId, BigInteger.valueOf(request.getData().getDoc().getVal().intValue()));
        Assert.assertNotNull(request.getReq().getExec());
        Assert.assertEquals(Constants.DO, request.getReq().getExec().getAction().getGenericAction());
    }

    @Test
    public void testToOrderCancelResponse_whenSuppliedWithValidResponse_thenNoServiceErrors() throws Exception {
        CancelDocRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderCancelResponse_UT.xml", CancelDocRsp.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        orderCancelConverter.processCancelResponse(rsp);
        Assert.assertFalse(serviceErrors.hasErrors());
    }

    @Test
    public void testToOrderCancelResponse_whenSuppliedWithError_thenError() throws Exception {
        CancelDocRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderCancelResponseError_UT.xml", CancelDocRsp.class);
        validations.add(new ValidationError("btfg$chk_avl_cash_tra", null, "The client has not enough cash for trading !",
                ErrorType.WARNING));
        try {
            orderCancelConverter.processCancelResponse(rsp);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

}
