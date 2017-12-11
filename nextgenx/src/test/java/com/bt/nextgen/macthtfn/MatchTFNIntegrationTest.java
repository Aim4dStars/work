package com.bt.nextgen.macthtfn;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.matchtfn.AvaloqMatchTFNIntegrationServiceImpl;
import com.btfin.panorama.service.client.error.ServiceErrorImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.core.Is.is;

/**
 * Created by l078480 on 27/06/2017.
 */
public class MatchTFNIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    AvaloqMatchTFNIntegrationServiceImpl matchTFNIntegrationService;

    private ServiceErrors serviceErrors = new com.btfin.panorama.service.exception.ServiceErrorsImpl();

    @Test
    public void test_doesNotMatchValidTFN() throws Exception {
        Boolean matchedResult = (matchTFNIntegrationService.doMatchTFN("44444","778188488",serviceErrors));
        Assert.assertThat(matchedResult, is(false));
    }

    @Test
    public void test_matchesValidTFN() throws Exception {
        Boolean matchedResult = (matchTFNIntegrationService.doMatchTFN("185415","648188480",serviceErrors));
        Assert.assertThat(matchedResult, is(true));
    }

    @Test
    public void test_invalidTFNParameter() throws Exception {
        Boolean matchedResult = (matchTFNIntegrationService.doMatchTFN("185415","",serviceErrors));
        Assert.assertThat(serviceErrors.hasErrors(),is(true));
        Assert.assertThat(serviceErrors.getErrorList().iterator().next().getMessage(),is("Invalid Tax File Number parameter provided"));
        Assert.assertThat(matchedResult,is(false));
    }

    @Test
    public void test_matchTFNWithServiceErrors() throws Exception {
        serviceErrors.addError(new ServiceErrorImpl("Error in matchTFNService"));
        Boolean matchedResult = (matchTFNIntegrationService.doMatchTFN("185415","648188480",serviceErrors));
        Assert.assertThat(matchedResult, is(false));

    }

}
