package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transaction.SavedPayment;
import com.bt.nextgen.service.avaloq.transaction.TransactionFrequency;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.movemoney.SavedPaymentIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by L067218 on 9/02/2017.
 */
public class SavedPaymentIntegrationServiceTest extends BaseSecureIntegrationTest {

    private static final String ACCOUNT_NUMBER = "830676";

    private static final Logger logger = LoggerFactory.getLogger(SavedPaymentIntegrationServiceTest.class);

    @Autowired
    SavedPaymentIntegrationService savedPaymentIntegrationService;

    ServiceErrors serviceErrors = new ServiceErrorsImpl();

    @SecureTestContext
    @Test
    public void testLoadSavedPensionPayments() throws Exception
    {
        List<String> codeList = new ArrayList<>();
        //Add codes to this list
        codeList.add("pay.pay#super_pens_oneoff");
        codeList.add("pay.pay#super_opn_lmpsm");
        codeList.add("pay.stord_new_super_pens");
        codeList.add("pay.pay.sa_stord_mdf");

        List<SavedPayment> savedPayments = savedPaymentIntegrationService.loadSavedPensionPayments(ACCOUNT_NUMBER, codeList, serviceErrors);
        assertNotNull(savedPayments);
        assertEquals(1, savedPayments.size());
        assertEquals("854360", savedPayments.get(0).getStordPos());
        assertEquals("123456789", savedPayments.get(0).getPayeeAccount());
        assertEquals("036081", savedPayments.get(0).getPayeeBsb());
        assertEquals("854130", savedPayments.get(0).getPayer());
        assertEquals("2", savedPayments.get(0).getTransSeqNo());
        assertEquals(TransactionFrequency.Yearly, savedPayments.get(0).getFrequency());
        assertEquals(new BigDecimal(300), savedPayments.get(0).getAmount());
        assertEquals(new DateTime(DateTime.parse("2017-06-10")), savedPayments.get(0).getFirstDate());
        assertEquals(PensionPaymentType.MINIMUM_AMOUNT, savedPayments.get(0).getPensionPaymentType());
        assertEquals("Pension payment", savedPayments.get(0).getOrderType());
        assertEquals("Not Submitted", savedPayments.get(0).getTransactionStatus());



    }
}
