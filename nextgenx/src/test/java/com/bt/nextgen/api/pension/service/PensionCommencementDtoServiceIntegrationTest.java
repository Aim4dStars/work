package com.bt.nextgen.api.pension.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Integration test class for {@link com.bt.nextgen.api.pension.service.PensionCommencementDtoService}
 * Created by L067218 on 15/09/2016.
 */
public class PensionCommencementDtoServiceIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private PensionCommencementDtoService pensionCommencementDtoService;

    PensionTrxnDto pensionDto = new PensionTrxnDto();

    @Test
    public void testSubmit() {

        pensionDto.setKey(new AccountKey("C2E32F24354886206E9FDF8E2280880615ADC6ACF3680ADC"));
        PensionTrxnDto pensionTrxnDto = pensionCommencementDtoService.submit(pensionDto, new ServiceErrorsImpl());

        assertThat("pensionTrxnDto", pensionTrxnDto.getTransactionStatus(), is(equalTo("saved")));
    }
}
