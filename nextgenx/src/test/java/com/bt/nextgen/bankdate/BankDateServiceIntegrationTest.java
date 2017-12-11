package com.bt.nextgen.bankdate;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
 
@SecureTestContext(username="adviser", jobRole = "ADVISER" , customerId = "297129090", jobId="",  profileId = "")
public class BankDateServiceIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	BankDateIntegrationService bankdateservice;

    @Autowired
    ParsingContext context;

    private DateTime bankDate;
    private DateTime currentTime;

    @Autowired
    private GenericCache cache;

	@Test
	@SecureTestContext
	public void testBankDate() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

        bankDate = bankdateservice.getBankDate(serviceErrors);
		assertNotNull(bankDate);
		
			
		//TimeUnit.SECONDS.sleep(70);		
		//bankDate = bankdateservice.getBankDate(serviceErrors);
		//assertNotNull(bankDate);
		
		
        currentTime = bankdateservice.getTime(serviceErrors);
		assertNotNull(currentTime);
		

		assertThat(bankDate, is(new DateTime("2015-03-07T00:00:00.000+11:00")));


	}

    
    @SecureTestContext(username="explode", customerId = "201601388")
    @Test
    public void testErrorBankDate() throws Exception
    {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        currentTime = bankdateservice.getTime(serviceErrors);
        assertThat(serviceErrors.hasErrors(), Is.is(true));
    }
	
}
