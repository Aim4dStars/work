package com.bt.nextgen.api.transaction.util;

import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import org.apache.struts.mock.MockHttpSession;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * Created by L067218 on 16/12/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionUtilTest {

    @InjectMocks
    private TransactionUtil transactionUtil;

    private Collection<CashTransactionHistoryDto> createCashTransactionHistory() {
        TransactionHistoryImpl transaction1 = new TransactionHistoryImpl();
        transaction1.setDocId("12345");
        transaction1.setAmount(new BigDecimal("157.00"));
        transaction1.setMetaType("abcd");
        CashTransactionHistoryDto dto1 = new CashTransactionHistoryDto(transaction1, "unmarked");

        TransactionHistoryImpl transaction2 = new TransactionHistoryImpl();
        transaction2.setDocId("12346");
        transaction2.setAmount(new BigDecimal("7000.00"));
        transaction2.setMetaType("abcd");
        CashTransactionHistoryDto dto2 = new CashTransactionHistoryDto(transaction2, "unmarked");

        TransactionHistoryImpl transaction3 = new TransactionHistoryImpl();
        transaction3.setDocId("12347");
        transaction3.setAmount(new BigDecimal("15000.00"));
        transaction3.setMetaType("abcd");
        CashTransactionHistoryDto dto3 = new CashTransactionHistoryDto(transaction3, "unmarked");

        ArrayList<CashTransactionHistoryDto> dtoList = new ArrayList<>();
        dtoList.add(dto1);
        dtoList.add(dto2);
        dtoList.add(dto3);

        return dtoList;
    }

    private PaymentDto createPayment() {
        PaymentDto dto1 = new PaymentDto();
        dto1.setReceiptId("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");
        dto1.setAmount(new BigDecimal("157.00"));

        return dto1;
    }

    @Test
    public void ensureCashTransactionHistoryPersistedToSession() throws Exception {
        List<CashTransactionHistoryDto> dtoList = (ArrayList<CashTransactionHistoryDto>) createCashTransactionHistory();
        MockHttpSession mockHttpSession = new MockHttpSession();
        transactionUtil.updateCashDepositAmountsToSession(dtoList, mockHttpSession);
        ConcurrentHashMap<String, BigDecimal> dtoHashmap = (ConcurrentHashMap<String, BigDecimal>) mockHttpSession
                .getAttribute("transactionhistory");
        assertNotNull(dtoHashmap);
        assertEquals(3, dtoHashmap.size());
        assertEquals(new BigDecimal("157.00"), dtoHashmap.get("12345"));
        assertEquals(new BigDecimal("7000.00"), dtoHashmap.get("12346"));
        assertEquals(new BigDecimal("15000.00"), dtoHashmap.get("12347"));

    }

    @Test
    public void ensurePaymentsPersistedToSession() throws Exception {
        PaymentDto paymentDto = createPayment();
        MockHttpSession mockHttpSession = new MockHttpSession();
        transactionUtil.updatePaymentsAmountsToSessionAfterSubmit(paymentDto, mockHttpSession);
        ConcurrentHashMap<String, BigDecimal> dtoHashmap = (ConcurrentHashMap<String, BigDecimal>) mockHttpSession
                .getAttribute("transactionhistory");
        assertNotNull(dtoHashmap);
        assertEquals(1, dtoHashmap.size());
        assertEquals(new BigDecimal("157.00"),
                dtoHashmap.get(EncodedString.toPlainText("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0")));

    }
}
