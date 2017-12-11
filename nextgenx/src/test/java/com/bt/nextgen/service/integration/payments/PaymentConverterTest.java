package com.bt.nextgen.service.integration.payments;


import com.bt.nextgen.service.avaloq.payments.PaymentDetailsImpl;
import com.btfin.abs.trxservice.pay.v1_0.PayReq;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class PaymentConverterTest {

    @Mock
    private PaymentConverter paymentConverter;

    @Test
    public void toGenericPaymentRequestTest(){
        PaymentDetails paymentDetails = getPaymentDetails();
        PayReq payReq = paymentConverter.toGenericPaymentRequest(paymentDetails, new ServiceErrorsImpl());
        Assert.assertNotNull(payReq);
    }
    protected PaymentDetails getPaymentDetails() {
        PaymentDetails paymentDetails = new PaymentDetailsImpl();
        paymentDetails.setBusinessChannel("mobile");
        paymentDetails.setClientIp("10.0.0.1");
        paymentDetails.setAmount(BigDecimal.valueOf(123));
        return paymentDetails;
    }
}
