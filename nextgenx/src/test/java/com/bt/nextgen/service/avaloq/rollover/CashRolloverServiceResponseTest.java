package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;

@RunWith(MockitoJUnitRunner.class)
public class CashRolloverServiceResponseTest {

    @InjectMocks
    DefaultResponseExtractor<RolloverDetailsImpl> defaultResponseExtractor;

    @Test
    public void testRolloverInResponse_errorResponse() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource("/webservices/response/rollover/RolloverInWithError.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(RolloverDetailsImpl.class);
        RolloverDetailsImpl response = defaultResponseExtractor.extractData(content);
        Assert.assertTrue(response.getWarnings() != null);
        Assert.assertEquals(1, response.getWarnings().size());
        TransactionValidation err = response.getWarnings().get(0);
        Assert.assertEquals("10666", err.getErrorId());
        Assert.assertEquals("Rollover USI missing.", err.getErrorMessage());
    }

    @Test
    public void testRolloverInResponse_fatalErrorResponse() throws Exception {
        final ClassPathResource responseResource = new ClassPathResource(
                "/webservices/response/rollover/RolloverInFatalErrorResponse.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(responseResource.getInputStream()));

        defaultResponseExtractor = new DefaultResponseExtractor<>(RolloverDetailsImpl.class);
        RolloverDetailsImpl response = defaultResponseExtractor.extractData(content);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getErrorMessage());
    }
}
