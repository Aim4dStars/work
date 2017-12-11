package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WrapAccountImplIntegrationTest extends BaseSecureIntegrationTest {
    @Autowired
    private Validator validator;

    @Before
    public void setup() {
    }

    @Test
    public void testValidation_whenAccountIdIsNull_thenServiceErrors() {
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountName("test");
        wrapAccount.setAdviserPersonId(ClientKey.valueOf("5678"));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(wrapAccount, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("accountKey may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenAccountNameIsNull_thenServiceErrors() {
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(AccountKey.valueOf("1234"));
        wrapAccount.setAdviserPersonId(ClientKey.valueOf("5678"));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(wrapAccount, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("accountName may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenAdviserPersonIdIsNull_thenServiceErrors() {
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(AccountKey.valueOf("1234"));
        wrapAccount.setAccountName("test");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(wrapAccount, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("adviserPersonId may not be null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenObjectIsNull_thenServiceErrors() {
        WrapAccountImpl wrapAccount = null;
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(wrapAccount, serviceErrors);
        Assert.assertTrue(serviceErrors.hasErrors());
        Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
    }

    @Test
    public void testValidation_whenValid_thenNoServiceError() {
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        wrapAccount.setAccountKey(AccountKey.valueOf("1234"));
        wrapAccount.setAccountName("test");
        wrapAccount.setAdviserPersonId(ClientKey.valueOf("5678"));
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        validator.validate(wrapAccount, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
    }
}
