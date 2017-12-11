package com.bt.nextgen.core.jms;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.jms.JmsIntegrationServiceResponseImpl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JmsResponseIntegrationTest extends BaseSecureIntegrationTest{

	private Logger logger = LoggerFactory.getLogger(JmsResponseIntegrationTest.class);

	@Autowired
	AvaloqExecute avaloqExecute;
	
	@Test
	@SecureTestContext
	public void testJmsResponse() {

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		
		JmsIntegrationServiceResponseImpl result = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(
				Template.STATIC_CODES.getName()).forJMSResponse()
				.compressResult().asApplicationUser(), JmsIntegrationServiceResponseImpl.class,
				serviceErrors);
		
		assertNotNull(result);
		assertNotNull(result.getResponseMsgIdList());
		assertEquals(98, result.getResponseMsgIdList().size());
	}

    @Test
    
    @SecureTestContext(username="explode", jobRole = "adviser" , customerId = "201601934", profileId="971", jobId = "")
    public void testJmsErrorResponse()
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        JmsIntegrationServiceResponseImpl result = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(
                        Template.STATIC_CODES.getName()).forJMSResponse()
                        .compressResult().asApplicationUser(), JmsIntegrationServiceResponseImpl.class,
                serviceErrors);

        assertThat(result, is(notNullValue()));
        assertThat(result.getErrorList(), is(notNullValue()));
        assertThat(result.getErrorList().get(0).getType().toString(),is("fa"));
        assertThat(result.getErrorList().get(0).getReason().toString(),is(""));
        assertThat(result.getErrorList().get(1).getType().toString(),is("fa"));
        assertThat(result.getErrorList().get(1).getReason().toString(),is("Fatal error occurred: "));
    }

    @Test(expected = Exception.class)
    @SecureTestContext(username="iccendsystemexplode", jobRole = "adviser" , customerId = "201601934", profileId="971", jobId = "")
    public void testJmsIccExplodeErrorResponse()
    {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();

        JmsIntegrationServiceResponseImpl result = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(
                        Template.STATIC_CODES.getName()).forJMSResponse()
                        .compressResult().asApplicationUser(), JmsIntegrationServiceResponseImpl.class,
                serviceErrors);
        System.out.println("result = " + result);

    }

}
