package com.bt.nextgen.api.verifylinkedaccountstatus;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.verifylinkedaccount.model.LinkedAccountDetailsDto;
import com.bt.nextgen.api.verifylinkedaccount.service.VerifyLinkedDtoServiceImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.linkedaccountverification.LinkedAccountVerificationImpl;
import com.bt.nextgen.service.avaloq.linkedaccountverification.VerifyLinkedAccountStatusImpl;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.verifylinkedaccount.VerifyLinkedAccountIntegrationService;
import com.bt.nextgen.service.integration.verifylinkedaccount.VerifyLinkedAccountStatus;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by l078480 on 22/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class VerifyLinkedDtoServiceImplTest {

    @InjectMocks
    VerifyLinkedDtoServiceImpl verifyLinkedDtoService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    @Mock
    private VerifyLinkedAccountIntegrationService verifyLinkedAccountIntegrationService;


    private CodeImpl statusCode;
    private LinkedAccountDetailsDto verifyCode;
    private LinkedAccountDetailsDto generateCode;
    private VerifyLinkedAccountStatus verifyLinkedAccountStatus;


    @Before
    public void setUp() {

        verifyCode = new LinkedAccountDetailsDto();
        verifyCode.setVerificationAction("verifyCode");
        verifyCode.setAccountNumber("123456");
        verifyCode.setBsb("062032");
        verifyCode.setKey(new AccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0"));
        verifyCode.setVerificationCode("123456");

        generateCode = new LinkedAccountDetailsDto();
        generateCode.setVerificationAction("generateCode");
        generateCode.setAccountNumber("123456");
        generateCode.setBsb("062032");
        generateCode.setKey(new AccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0"));

        statusCode = new CodeImpl("1", "Verified","vfy");
        statusCode.setIntlId("vfy");
        statusCode.addField("can_vfy_code","-");

        verifyLinkedAccountStatus =new VerifyLinkedAccountStatusImpl();
        verifyLinkedAccountStatus.setLinkedAccountStatus("1");



    }

    @Test
    public void test_VerifyCode () throws Exception {

        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.LINKED_ACCOUNT_STATUS),
                Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(statusCode);
        Mockito.when(verifyLinkedAccountIntegrationService.getVerifyLinkedAccount(Mockito.any(LinkedAccountVerificationImpl.class),Mockito.any(ServiceErrors.class))).thenReturn(verifyLinkedAccountStatus);
        LinkedAccountDetailsDto linkedAccountDetailsDto =  verifyLinkedDtoService.submit(verifyCode,new FailFastErrorsImpl());
        assertThat(linkedAccountDetailsDto, is(notNullValue()));
        assertEquals("Verified", linkedAccountDetailsDto.getLinkedAccountStatus().getLinkedAccountStatus().getDescription());



    }

    @Test
    public void test_generateCode () throws Exception {

        Mockito.when(staticIntegrationService.loadCode(Mockito.eq(CodeCategory.LINKED_ACCOUNT_STATUS),
                Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenReturn(statusCode);
        Mockito.when(verifyLinkedAccountIntegrationService.generateCodeForLinkedAccount(Mockito.any(LinkedAccountVerificationImpl.class),Mockito.any(ServiceErrors.class))).thenReturn(verifyLinkedAccountStatus);
        LinkedAccountDetailsDto linkedAccountDetailsDto =  verifyLinkedDtoService.submit(generateCode,new FailFastErrorsImpl());
        assertThat(linkedAccountDetailsDto, is(notNullValue()));
        assertEquals("Verified", linkedAccountDetailsDto.getLinkedAccountStatus().getLinkedAccountStatus().getDescription());



    }


}
