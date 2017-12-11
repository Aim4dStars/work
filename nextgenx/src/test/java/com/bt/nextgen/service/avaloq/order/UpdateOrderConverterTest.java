package com.bt.nextgen.service.avaloq.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.ErrorConverter;
import com.bt.nextgen.service.integration.order.ExpiryMethod;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.order.PriceType;
import com.btfin.abs.trxservice.stex.v1_0.StexReq;
import com.btfin.abs.trxservice.stex.v1_0.StexRsp;

@RunWith(MockitoJUnitRunner.class)
public class UpdateOrderConverterTest {

    @InjectMocks
    UpdateOrderConverter updateOrderConverter;

    @InjectMocks
    private ErrorConverter errorConverter;

    @Before
    public void setup() {

    }

    @Test
    public void testToUpdateOrderRequest_forCancellation_thenStexReqMatches() throws Exception {
        BigInteger orderId = BigInteger.valueOf(112345L);
        BigInteger lastTranSeqId = BigInteger.valueOf(3L);
        OrderImpl order = new OrderImpl();
        order.setOrderId(orderId.toString());
        order.setLastTranSeqId(lastTranSeqId.toString());
        order.setStatus(OrderStatus.CANCELLED);
        StexReq request = updateOrderConverter.toUpdateOrderRequest(order);
        assertEquals(orderId, BigInteger.valueOf(request.getData().getDoc().getVal().intValue()));
        assertNotNull(request.getReq().getExec());
        assertEquals(Constants.DO, request.getReq().getExec().getAction().getGenericAction());
    }

    @Test
    public void testProcessUpdateResponse_ForInProgressLSOrders_whenSuppliedWithValidResponse_thenNoServiceErrors()
            throws Exception {
        StexRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderCancelStexResponse_UT.xml", StexRsp.class);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ValidationError> validationErrors = updateOrderConverter.toValidationErrors(rsp);
        assertTrue(validationErrors.isEmpty());

    }

    @Test
    public void testProcessUpdateResponse_whenSuppliedWithError_thenError() throws Exception {
        StexRsp rsp = JaxbUtil.unmarshall("/webservices/response/OrderCancelStexResponseError_UT.xml", StexRsp.class);
        try {
            List<ValidationError> validationErrors = updateOrderConverter.toValidationErrors(rsp);
            fail("Exception should be thrown");
        } catch (Exception e) {

        }

    }

    @Test
    public void testToUpdateOrderRequest_forAmendment_thenStexReqMatches() throws Exception {
        BigInteger orderId = BigInteger.valueOf(112345L);
        BigInteger lastTranSeqId = BigInteger.valueOf(3L);
        OrderImpl order = new OrderImpl();
        order.setOrderId(orderId.toString());
        order.setLastTranSeqId(lastTranSeqId.toString());
        order.setStatus(OrderStatus.IN_PROGRESS);
        order.setLimitPrice(new BigDecimal("2.435"));
        order.setExpiryType(ExpiryMethod.GTC);
        order.setPriceType(PriceType.LIMIT);
        order.setOriginalQuantity(new BigDecimal("453"));
        StexReq request = updateOrderConverter.toUpdateOrderRequest(order);
        assertEquals(orderId, BigInteger.valueOf(request.getData().getDoc().getVal().intValue()));
        assertNotNull(request.getReq().getExec());
        assertEquals(Constants.DO, request.getReq().getExec().getAction().getGenericAction());
        assertEquals(request.getData().getLimit().getVal(), order.getLimitPrice());
        assertEquals(request.getData().getExecType().getExtlVal().getVal(), order.getPriceType().getIntlId());
        assertEquals(request.getData().getExpirType().getExtlVal().getVal(), order.getExpiryType().getIntlId());
        assertEquals(request.getData().getQty().getVal(), order.getOriginalQuantity());
    }

}
