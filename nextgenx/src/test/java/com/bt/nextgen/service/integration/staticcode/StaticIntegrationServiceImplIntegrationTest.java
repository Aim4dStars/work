package com.bt.nextgen.service.integration.staticcode;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StaticIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest{

	@Autowired
	StaticIntegrationService staticCode;
	
	private static String JMS_ENABLED_PROPERTY = "jms.sending.enabled";
	private Boolean jmsEnabled = null;

	@Test
	public void testStaticCodeService() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		
		if(!isJmsEnabled()){
			Code code = staticCode.loadCode(CodeCategory.ORDER_TYPE, "1110", serviceErrors);

			assertNotNull(code);
			assertEquals("pay_bpay", code.getIntlId());
			assertEquals("BPAY_PAY", code.getUserId());
		}
	
	}

	
	private Boolean isJmsEnabled()
	{
		if(jmsEnabled!=null)
			return jmsEnabled;
		else
			jmsEnabled = Properties.getSafeBoolean(JMS_ENABLED_PROPERTY);

		return jmsEnabled;
	}
	
}
