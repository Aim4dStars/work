package com.bt.nextgen.api.payments.service;

import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.api.account.v1.model.PayeeDto;
import com.bt.nextgen.api.util.PaymentUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.domain.PaymentMethod;
import com.bt.nextgen.payments.web.model.PaymentInterface;
import com.bt.nextgen.payments.web.model.PaymentModel;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.payments.RecurringPaymentDetails;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by L078480 on 20/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentUtilTest {


    @Test
    public void testpopulateRecurringPaymentDetails()
    {

        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        moneyAccountIdentifier.setMoneyAccountId("1234567");

        PaymentInterface payment = new PaymentModel();
        payment.setPaymentId("OTY90867HJU");
        payment.setPaymentMethod(PaymentMethod.Method.DIRECT_DEBIT.name());
        payment.setAmount("$12.00");
        payment.setDescription("Test Payment");
        payment.setMaccId("1002927871");

        PayeeModel from = new PayeeModel();
        from.setReference("1001");
        from.setCode("262-786");
        from.setName("Adrian Demo Smith");
        from.setPayeeType(PayeeType.PAY_ANYONE);
        from.setPayeeType(PayeeType.PAY_ANYONE);
        payment.setFrom(from);

        PayeeModel to = new PayeeModel();
        to.setReference("");
        to.setCode("12006");
        to.setName("linkedAcc");
        to.setPayeeType(PayeeType.LINKED);
        to.setDescription("Test Payment");
        payment.setTo(to);

        payment.setDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");
        com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto = new com.bt.nextgen.api.account.v1.model.PaymentDto();
        com.bt.nextgen.api.account.v1.model.AccountKey key = new com.bt.nextgen.api.account.v1.model.AccountKey("36846");
        paymentDto.setKey(key);

        PayeeDto fromDto = new PayeeDto();

        fromDto.setCode("262-786");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());

        paymentDto.setFromPayDto(fromDto);

        PayeeDto toDto = new PayeeDto();

        toDto.setCode("12006");
        toDto.setAccountKey(EncodedString.fromPlainText("234234234234").toString());
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(PayeeType.LINKED.name());

        paymentDto.setToPayteeDto(toDto);
        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setEndRepeat("setEndDate");
        paymentDto.setIsRecurring(true);
        paymentDto.setFrequency("Monthly");
        paymentDto.setTransactionDate("12/06/2016");
        Date date = null;



        RecurringPaymentDetails recurringPaymentDetails = PaymentUtil.populateRecurringPaymentDetails(moneyAccountIdentifier,paymentDto);
        assertNotNull(recurringPaymentDetails);
        assertEquals("Test Payment", recurringPaymentDetails.getBenefeciaryInfo());
        assertEquals("linkedAcc", recurringPaymentDetails.getPayeeName());


    }

    @Test
    public void testpopulatePaymentDetails()
    {

        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        moneyAccountIdentifier.setMoneyAccountId("1234567");

        PaymentInterface payment = new PaymentModel();
        payment.setPaymentId("OTY90867HJU");
        payment.setPaymentMethod(PaymentMethod.Method.DIRECT_DEBIT.name());
        payment.setAmount("$12.00");
        payment.setDescription("Test Payment");
        payment.setMaccId("1002927871");

        PayeeModel from = new PayeeModel();
        from.setReference("1001");
        from.setCode("262-786");
        from.setName("Adrian Demo Smith");
        from.setPayeeType(PayeeType.PAY_ANYONE);
        from.setPayeeType(PayeeType.PAY_ANYONE);
        payment.setFrom(from);

        PayeeModel to = new PayeeModel();
        to.setReference("");
        to.setCode("12006");
        to.setName("linkedAcc");
        to.setPayeeType(PayeeType.LINKED);
        to.setDescription("Test Payment");
        payment.setTo(to);

        payment.setDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");
        com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto = new com.bt.nextgen.api.account.v1.model.PaymentDto();
        com.bt.nextgen.api.account.v1.model.AccountKey key = new com.bt.nextgen.api.account.v1.model.AccountKey("36846");
        paymentDto.setKey(key);

        PayeeDto fromDto = new PayeeDto();

        fromDto.setCode("262-786");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());

        paymentDto.setFromPayDto(fromDto);

        PayeeDto toDto = new PayeeDto();

        toDto.setCode("12006");
        toDto.setAccountKey(EncodedString.fromPlainText("234234234234").toString());
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(PayeeType.LINKED.name());

        paymentDto.setToPayteeDto(toDto);
        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setEndRepeat("setEndDate");
        paymentDto.setIsRecurring(true);
        paymentDto.setFrequency("Monthly");
        paymentDto.setTransactionDate("12/06/2016");
        paymentDto.setBusinessChannel("mobile");
        paymentDto.setClientIp("0.0.0.1");
        Date date = null;



        RecurringPaymentDetails recurringPaymentDetails = PaymentUtil.populateRecurringPaymentDetails(moneyAccountIdentifier,paymentDto);
        assertNotNull(recurringPaymentDetails);
        assertEquals("Test Payment", recurringPaymentDetails.getBenefeciaryInfo());
        assertEquals("linkedAcc", recurringPaymentDetails.getPayeeName());
        assertEquals("mobile", recurringPaymentDetails.getBusinessChannel());
        assertEquals("0.0.0.1", recurringPaymentDetails.getClientIp());
    }

    @Test
    public void populatePaymentDetailsTest() {
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        moneyAccountIdentifier.setMoneyAccountId("1234567");

        PaymentInterface payment = new PaymentModel();
        payment.setPaymentId("OTY90867HJU");
        payment.setPaymentMethod(PaymentMethod.Method.DIRECT_DEBIT.name());
        payment.setAmount("$12.00");
        payment.setDescription("Test Payment");
        payment.setMaccId("1002927871");

        PayeeModel from = new PayeeModel();
        from.setReference("1001");
        from.setCode("262-786");
        from.setName("Adrian Demo Smith");
        from.setPayeeType(PayeeType.PAY_ANYONE);
        from.setPayeeType(PayeeType.PAY_ANYONE);
        payment.setFrom(from);

        PayeeModel to = new PayeeModel();
        to.setReference("");
        to.setCode("12006");
        to.setName("linkedAcc");
        to.setPayeeType(PayeeType.LINKED);
        to.setDescription("Test Payment");
        payment.setTo(to);

        payment.setDate("Tue Sep 02 2014 00:00:00 GMT+1000 (AUS Eastern Standard Time)");
        com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto = new com.bt.nextgen.api.account.v1.model.PaymentDto();
        com.bt.nextgen.api.account.v1.model.AccountKey key = new com.bt.nextgen.api.account.v1.model.AccountKey("36846");
        paymentDto.setKey(key);

        PayeeDto fromDto = new PayeeDto();

        fromDto.setCode("262-786");
        fromDto.setAccountName("Adrian Demo Smith");
        fromDto.setPayeeType(PayeeType.PAY_ANYONE.name());
        paymentDto.setClientIp("0.0.0.1");
        paymentDto.setBusinessChannel("mobile");
        paymentDto.setFromPayDto(fromDto);

        PayeeDto toDto = new PayeeDto();

        toDto.setCode("12006");
        toDto.setAccountKey(EncodedString.fromPlainText("234234234234").toString());
        toDto.setAccountName("linkedAcc");
        toDto.setPayeeType(PayeeType.LINKED.name());

        paymentDto.setToPayteeDto(toDto);
        paymentDto.setAmount(new BigDecimal(12));
        paymentDto.setDescription("Test Payment");
        paymentDto.setEndRepeat("setEndDate");
        paymentDto.setTransactionDate("12/06/2016");
        Date date = null;

        com.bt.nextgen.service.integration.payments.PaymentDetails paymentDetails = PaymentUtil.populatePaymentDetails(moneyAccountIdentifier, paymentDto);
        assertNotNull(paymentDetails);
        assertEquals("0.0.0.1", paymentDetails.getClientIp());
        assertEquals("mobile", paymentDetails.getBusinessChannel());
    }
}
