package com.bt.nextgen.service.avaloq.account;

import java.math.BigDecimal;
import java.util.Map;

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
import com.avaloq.abs.screen_rep.hira.btfg$ui_custr_list_bp_person_cont.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class WrapAccountConverterTest
{
	@InjectMocks
	private WrapAccountConverter accountConverter = new WrapAccountConverter();

	@Mock
	private StaticIntegrationService staticService;

	@Mock
	private ProductIntegrationService productIntegrationService;

	ServiceErrors serviceErrors = new FailFastErrorsImpl();

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
				if (CodeCategory.PERSON_TYPE.equals(args[0]) && "120".equals(args[1]))
				{
					return new CodeImpl("120", "person", Constants.NATURAL_PERSON);
				}
				else if (CodeCategory.PERSON_TYPE.equals(args[0]) && "121".equals(args[1]))
				{
					return new CodeImpl("121", "person", Constants.LEGAL_ENTITY);
				}
				else if (CodeCategory.ACCOUNT_STRUCTURE_TYPE.equals(args[0]) && "20611".equals(args[1]))
				{
					return new CodeImpl("20611", "btfg$indvl", "Individual");
				}
				else if (CodeCategory.COUNTRY.equals(args[0]) && "2061".equals(args[1]))
				{
					return new CodeImpl("2061", "country", "Australia");
				}
				else if (CodeCategory.STATES.equals(args[0]) && "5004".equals(args[1]))
				{
					return new CodeImpl("2061", "state", "New South Wales");
				}
                else if(CodeCategory.ACCOUNT_STATUS.equals(args[0]))
                {
                    return null;
                }
				else
				{
					return "Unknown";
				}
			}
		});
	}

	@Test
	public void testToAccount() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/BTFG$UI_CUSTR_LIST.BP_PERSON_CONT_UT.xml", Rep.class);

		Map <AccountKey, WrapAccountImpl> accountMap = accountConverter.toAccountMap(report, serviceErrors);

		Assert.assertEquals(1, accountMap.size());

		WrapAccountImpl account = accountMap.values().iterator().next();
		Assert.assertEquals("45403", account.getAccountKey().getId());
		Assert.assertEquals("Adrian Demo Smith", account.getAccountName());
		Assert.assertEquals("43303", account.getProductKey().getId());
		Assert.assertEquals(BigDecimal.valueOf(50023093.72), account.getAvailableCash());
		Assert.assertEquals(BigDecimal.valueOf(100677640.44), account.getPortfolioValue());
		Assert.assertEquals(AccountStructureType.Individual, account.getAccountStructureType());
		Assert.assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
		Assert.assertNotNull(account.getAdviser());
	}
}
