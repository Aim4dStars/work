package com.bt.nextgen.service.avaloq.broker;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;
import java.util.Map;

import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;

import com.bt.nextgen.service.integration.broker.BrokerUser;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.avaloq.abs.screen_rep.hira.btfg$ui_oe_struct_person_hira.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;

@RunWith(MockitoJUnitRunner.class)
public class UserConverterTest
{
	@InjectMocks
	private UserConverter userConverter = new UserConverter();

	@Mock
	private StaticIntegrationService staticService;

	@Mock
	private ProductIntegrationService productIntegrationService;

	private ServiceErrors serviceErrors = new FailFastErrorsImpl();

	@Before
	public void setup()
	{
		Mockito.when(staticService.loadCode(Mockito.any(CodeCategory.class),
			Mockito.anyString(),
			Mockito.any(ServiceErrors.class))).thenAnswer(new Answer <Object>()
		{
			public Object answer(InvocationOnMock invocation)
			{
				Object[] args = invocation.getArguments();
				if (CodeCategory.JOB_TYPE.equals(args[0]))
				{
					/*if ("660272".equals(args[1]))
					{
						return new CodeImpl("660272", "BTFG$AVSR", JobRole.ADVISER.getDescription());
					}*/
                    if("660272".equals(args[1])){

                        return new CodeImpl("660272", "BTFG$AVSR", JobRole.ADVISER.toString(),"BTFG$AVSR");
                    }
                   	else
					{
						return null;
					}
				}
				else
				{
					return null;
				}
			}
		});
	}

	@Test
	public void testToUserBroker() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/BrokerHierarchy_UT.xml", Rep.class);
		JobBrokerHolder jobBrokerHolder = userConverter.toUserBrokerMap(report, serviceErrors);

		Map <JobKey, BrokerUser> userMap = jobBrokerHolder.getJobMap();

		Assert.assertEquals(1353, userMap.size());
		Collection<BrokerUser> brokerUsers = jobBrokerHolder.getBrokerUsers(UserKey.valueOf("201631324"));
		assertThat(brokerUsers, is (notNullValue()));

		BrokerUserImpl brokerUser = (BrokerUserImpl)CollectionUtils.get(brokerUsers, 0);
		Assert.assertNotNull(brokerUser);
		Assert.assertEquals("Anne", brokerUser.getFirstName());
        Assert.assertEquals("C.", brokerUser.getMiddleName());
		Assert.assertEquals("Ramos.1", brokerUser.getLastName());
		Assert.assertEquals("201631324", brokerUser.getBankReferenceKey().getId());
		assertThat(brokerUser.getBankReferenceId(),is("201631324"));
        Assert.assertEquals(false, brokerUser.isRegisteredOnline());
		Assert.assertEquals(1, brokerUser.getRoles().size());
		BrokerRole role = brokerUser.getRoles().iterator().next();
		Assert.assertNotNull(role);
		Assert.assertEquals("93179", role.getKey().getId());
		Assert.assertEquals(JobRole.ADVISER, role.getRole());
	}


}
