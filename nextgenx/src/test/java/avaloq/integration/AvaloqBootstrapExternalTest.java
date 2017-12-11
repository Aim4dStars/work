package avaloq.integration;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;

/**
 * Test class to run all of the application level avaloq bootstraping interfaces to populate the initial service cache.
 */
public class AvaloqBootstrapExternalTest
{

	@Autowired
	StaticIntegrationService staticIntegrationService;


	@Test
	public void testLoadStaticCodes()
	{
		FailFastErrorsImpl errors = new FailFastErrorsImpl();
		staticIntegrationService.loadCode(CodeCategory.ADDR_CATEGORY, "123",errors);
	}


}
