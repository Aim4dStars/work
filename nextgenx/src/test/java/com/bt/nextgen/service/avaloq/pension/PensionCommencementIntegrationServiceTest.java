package com.bt.nextgen.service.avaloq.pension;

import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


/**
 * Tests {@link com.bt.nextgen.service.avaloq.pension.PensionCommencementIntegrationServiceImpl}.
 */
public class PensionCommencementIntegrationServiceTest extends BaseSecureIntegrationTest {
    private static final String ACCOUNT_NUMBER = "400002374";


    @Autowired
    private PensionCommencementIntegrationService service;


    @SecureTestContext
    @Test
    public void commencePension() throws Exception {
        final PensionTrxnDto pensionDto = service.commencePension(ACCOUNT_NUMBER);

        assertThat("commencePension status", pensionDto.getTransactionStatus(), equalTo("saved"));
    }

    @SecureTestContext
    @Test
    public void isPensionCommencementPending() throws Exception {
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        final boolean commencementPending = service.isPensionCommencementPending(ACCOUNT_NUMBER, serviceErrors);

        assertThat("service errors", serviceErrors.hasErrors(), equalTo(false));
        assertThat("commencementPending", commencementPending, equalTo(true));
    }
}
