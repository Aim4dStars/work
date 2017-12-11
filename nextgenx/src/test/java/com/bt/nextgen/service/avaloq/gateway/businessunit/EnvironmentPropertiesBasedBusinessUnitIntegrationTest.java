package com.bt.nextgen.service.avaloq.gateway.businessunit;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.avaloq.BusinessUnitProvider;
import com.bt.nextgen.service.avaloq.BusinessUnitProviderImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class EnvironmentPropertiesBasedBusinessUnitIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	private BusinessUnitProvider provider;

	@Before
	public void setUp() throws Exception
	{}

	@Test
	public void testGetBusinessUnit()
	{
		assertEquals(Properties.get(BusinessUnitProviderImpl.BTFG_BUSINESS_UNIT), provider.getBusinessUnit());
	}
}
