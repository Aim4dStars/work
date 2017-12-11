package com.bt.nextgen.verifylinkedaccount;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.avaloq.linkedaccountverification.LinkedAccountVerificationImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.verifylinkedaccount.VerifyLinkedAccountIntegrationService;
import com.bt.nextgen.service.integration.verifylinkedaccount.VerifyLinkedAccountStatus;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertEquals;

/**
 * Created by l078480 on 21/08/2017.
 */

public class VerifyLinkedIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    VerifyLinkedAccountIntegrationService verifyLinkedAccountIntegrationService;

    @SecureTestContext
    @Test
    public void test_verifyLinkedAccount() throws Exception {

        LinkedAccountVerificationImpl verificationImpl =new LinkedAccountVerificationImpl();
        verificationImpl.setAccountNumber("1234356");
        verificationImpl.setBsb("062032");
        verificationImpl.setVerificationCode("1234567");
        verificationImpl.setAccountKey(AccountKey.valueOf("123456"));
        VerifyLinkedAccountStatus verifyStatus= verifyLinkedAccountIntegrationService.getVerifyLinkedAccount(verificationImpl,new ServiceErrorsImpl());
        assertEquals("1",verifyStatus.getLinkedAccountStatus());

    }

    @SecureTestContext
    @Test
    public void test_generateCodeForLinkedAccount() throws Exception {

        LinkedAccountVerificationImpl verificationImpl =new LinkedAccountVerificationImpl();
        verificationImpl.setAccountNumber("1234356");
        verificationImpl.setBsb("062032");
        verificationImpl.setVerificationCode("1234567");
        verificationImpl.setAccountKey(AccountKey.valueOf("123456"));
        VerifyLinkedAccountStatus verifyStatus= verifyLinkedAccountIntegrationService.generateCodeForLinkedAccount(verificationImpl,new ServiceErrorsImpl());
        assertEquals("1",verifyStatus.getLinkedAccountStatus());

    }
}
