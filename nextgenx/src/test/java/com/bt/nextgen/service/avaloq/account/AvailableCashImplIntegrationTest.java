package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class AvailableCashImplIntegrationTest extends BaseSecureIntegrationTest {
    @Autowired
    private Validator validator;

    @Before
    public void setup() {
    }

    @Test
    public void testValidation_whenAvailableCashIsNull_thenServiceErrors() {
        AvailableCashImpl cash = new AvailableCashImpl();
        cash.setAccountKey(AccountKey.valueOf("1234"));
        cash.setTotalPendingSells(BigDecimal.valueOf(2.0));
        cash.setPendingSells(BigDecimal.valueOf(1.0));
        cash.setQueuedBuys(BigDecimal.valueOf(1.0));
        cash.setPendingBuys(BigDecimal.valueOf(1.0));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(cash, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("availableCash may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenPendingTotalSellsIsNull_thenServiceErrors() {
        AvailableCashImpl cash = new AvailableCashImpl();
        cash.setAccountKey(AccountKey.valueOf("1234"));
        cash.setAvailableCash(BigDecimal.valueOf(1.0));
        cash.setQueuedBuys(BigDecimal.valueOf(1.0));
        cash.setPendingBuys(BigDecimal.valueOf(1.0));
        cash.setPendingSells(BigDecimal.valueOf(1.0));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(cash, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("totalPendingSells may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenPendingSellsIsNull_thenServiceErrors() {
        AvailableCashImpl cash = new AvailableCashImpl();
        cash.setAccountKey(AccountKey.valueOf("1234"));
        cash.setAvailableCash(BigDecimal.valueOf(1.0));
        cash.setQueuedBuys(BigDecimal.valueOf(1.0));
        cash.setPendingBuys(BigDecimal.valueOf(1.0));
        cash.setTotalPendingSells(BigDecimal.valueOf(2.0));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(cash, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("pendingSells may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenQueuedBuysIsNull_thenServiceErrors() {
        AvailableCashImpl cash = new AvailableCashImpl();
        cash.setAccountKey(AccountKey.valueOf("1234"));
        cash.setAvailableCash(BigDecimal.valueOf(1.0));
        cash.setPendingSells(BigDecimal.valueOf(1.0));
        cash.setPendingBuys(BigDecimal.valueOf(1.0));
        cash.setTotalPendingSells(BigDecimal.valueOf(2.0));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(cash, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("queuedBuys may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Ignore
    @Test
    public void testValidation_whenPendingBuysIsNull_thenServiceErrors() {
        AvailableCashImpl cash = new AvailableCashImpl();
        cash.setAccountKey(AccountKey.valueOf("1234"));
        cash.setAvailableCash(BigDecimal.valueOf(1.0));
        cash.setTotalPendingSells(BigDecimal.valueOf(2.0));
        cash.setPendingSells(BigDecimal.valueOf(1.0));
        cash.setQueuedBuys(BigDecimal.valueOf(1.0));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(cash, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("pendingBuys may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenAccountIdIsNull_thenServiceErrors() {
        AvailableCashImpl cash = new AvailableCashImpl();
        cash.setAvailableCash(BigDecimal.valueOf(1.0));
        cash.setTotalPendingSells(BigDecimal.valueOf(2.0));
        cash.setPendingSells(BigDecimal.valueOf(1.0));
        cash.setQueuedBuys(BigDecimal.valueOf(1.0));
        cash.setPendingBuys(BigDecimal.valueOf(1.0));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(cash, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("accountKey may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenObjectIsNull_thenServiceErrors() {
        AvailableCashImpl cash = null;
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(cash, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenValid_thenNoServiceError() {
        AvailableCashImpl cash = new AvailableCashImpl();
        cash.setAccountKey(AccountKey.valueOf("1234"));
        cash.setAvailableCash(BigDecimal.valueOf(1.0));
        cash.setTotalPendingSells(BigDecimal.valueOf(2.0));
        cash.setPendingSells(BigDecimal.valueOf(1.0));
        cash.setQueuedBuys(BigDecimal.valueOf(1.0));
        cash.setPendingBuys(BigDecimal.valueOf(1.0));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(cash, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
    }

}
