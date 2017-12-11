package com.bt.nextgen.service.avaloq.broker;

import com.avaloq.abs.screen_rep.hira.btfg$ui_oe_struct_person_hira.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
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

import java.math.BigDecimal;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class BrokerConverterTest
{

	@InjectMocks
	private BrokerConverter brokerConverter = new BrokerConverter();

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
				if (CodeCategory.PERSON_CLASS.equals(args[0]))
				{
					if ("660230".equals(args[1]))
					{
						return new CodeImpl("660230", "AVSR_POS", BrokerType.ADVISER.getDescription(),"btfg$avsr_pos");
					}
					else if ("660229".equals(args[1]))
					{
						return new CodeImpl("660229", "OFFICE", BrokerType.OFFICE.getDescription(),"btfg$office");
					}
					else if ("660228".equals(args[1]))
					{
						return new CodeImpl("660228", "PRACTICE", BrokerType.PRACTICE.getDescription(),"btfg$practice");
					}
					else if ("660227".equals(args[1]))
					{
						return new CodeImpl("660227", "nDG", BrokerType.DEALER.getDescription(),"btfg$dg");
					}
					else if ("660226".equals(args[1]))
					{
						return new CodeImpl("660226", "CE", BrokerType.OPERATOR.getDescription(),"btfg$ce");
					}
					else if ("660308".equals(args[1]))
					{
						return new CodeImpl("660308", "BTFG$INVST_MGR", BrokerType.INVESTMENT_MANAGER.getDescription(),"btfg$invst_mgr");
					}
					else
					{
						return null;
					}
				}
                else if (CodeCategory.ADDR_CATEGORY.equals(args[0]) && "1".equals(args[1]))
                {
                    return new CodeImpl("1", "POSTAL", "Postal Address", "postal");
                }
                else if (CodeCategory.ADDR_CATEGORY.equals(args[0]) && "3".equals(args[1]))
                {
                    return new CodeImpl("3", "ELECTRONICAL", "Electronical Address", "electronical");
                }
                else if (CodeCategory.ADDR_KIND.equals(args[0]) && "1000".equals(args[1]))
                {
                    return new CodeImpl("1000", "btfg$pref", "Preferred", "btfg$pref");
                }
                else if (CodeCategory.ADDR_MEDIUM.equals(args[0])) {
                    if ("1000".equals(args[1])) {
                        return new CodeImpl("1000", "btfg$pers_phone", "Personal telephone", "btfg$pers_phone");
                    }
                    else if ("1001".equals(args[1])) {
                        return new CodeImpl("1001", "BTFG$MOBILE_PRI", "Mobile Phone - Primary", "btfg$mobile_pri");
                    }
                    else if ("1002".equals(args[1])) {
                        return new CodeImpl("1002", "BTFG$MOBILE_SEC", "Mobile Phone - Secondary", "btfg$mobile_sec");
                    }
                    else if ("1003".equals(args[1])) {
                        return new CodeImpl("1002", "BTFG$BUSI_PHONE", "Business Telephone", "btfg$busi_phone");
                    }
                    else if ("1004".equals(args[1])) {
                        return new CodeImpl("1004", "BTFG$EMAIL_PRI", "Email Address - Primary", "btfg$email_pri");
                    }
                    else if ("1005".equals(args[1])) {
                        return new CodeImpl("1005", "BTFG$EMAIL_SEC", "Email Address - Secondary", "btfg$email_sec");
                    }
                    else if ("6".equals(args[1])) {
                        return new CodeImpl("6", "postal", "postal", "postal");
                    }
                    else {
                        return null;
                    }
                }
                else if (CodeCategory.COUNTRY.equals(args[0]) && "2061".equals(args[1]))
                {
                    return new CodeImpl("2061", "country", "Australia");
                }
                else if (CodeCategory.STATES.equals(args[0]))
                {
                    if ("5002".equals(args[1]))
                    {
                        return new CodeImpl("5002", "QLD", "Queensland");
                    }
                    else if ("5001".equals(args[1]))
                    {
                        return new CodeImpl("5001", "VIC", "Victoria");
                    }
                    else if ("5004".equals(args[1]))
                    {
                        return new CodeImpl("5004", "NSW", "New South Wales");
                    }
                    else if ("5003".equals(args[1]))
                    {
                        return new CodeImpl("5003", "WA", "Western Australia");
                    }
                    else if ("5007".equals(args[1]))
                    {
                        return new CodeImpl("5007", "ACT", "Australia Capital Territory");
                    }
                    else if ("5008".equals(args[1]))
                    {
                        return new CodeImpl("5008", "TAS", "Tasmania");
                    }
                    else if ("5005".equals(args[1]))
                    {
                        return new CodeImpl("5005", "NT", "Northern Territory");
                    }
                    else if ("5006".equals(args[1]))
                    {
                        return new CodeImpl("5006", "SA", "South Australia");
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
	public void testToBrokerHierarchy() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_oe_struct_person_hira.Rep report = JaxbUtil.unmarshall("/webservices/response/BrokerHierarchy_UT.xml",
			com.avaloq.abs.screen_rep.hira.btfg$ui_oe_struct_person_hira.Rep.class);

		Map <BrokerKey, Broker> brokerMap = brokerConverter.toBrokerMap(report, serviceErrors);
		checkAdviserHierarchy(brokerMap);
		checkOfficeHierarchy(brokerMap);
		checkPracticeHierarchy(brokerMap);
		checkDealerHierarchy(brokerMap);
		checkOperatorHierarchy(brokerMap);
		checkIMHierarchy(brokerMap);
		checkOtherHierarchy(brokerMap);
	}

	@Test
	public void testToBrokerDetails() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/BrokerHierarchy_UT.xml", Rep.class);

		Map <BrokerKey, Broker> brokerMap = brokerConverter.toBrokerMap(report, serviceErrors);
		BrokerImpl broker =(BrokerImpl) brokerMap.get(BrokerKey.valueOf("67946"));
		Assert.assertEquals(BigDecimal.valueOf(0), broker.getFua());
		Assert.assertEquals(Integer.valueOf(0), broker.getNumberOfAccounts());

	}

	private void checkAdviserHierarchy(Map <BrokerKey, Broker> brokerMap)
	{
		int adviserCount = 0;
		for (Broker broker : brokerMap.values())
		{

			if (broker.getBrokerType() == BrokerType.ADVISER)
			{
				adviserCount++;
				BrokerImpl office =(BrokerImpl)brokerMap.get(broker.getOfficeKey());
				BrokerImpl practice = (BrokerImpl)brokerMap.get(broker.getPracticeKey());
				BrokerImpl dealer =(BrokerImpl) brokerMap.get(broker.getDealerKey());
				BrokerImpl operator = (BrokerImpl)brokerMap.get(broker.getOperatorKey());
				Assert.assertTrue(office == null || office.getBrokerType() == BrokerType.OFFICE);
				Assert.assertTrue(practice == null || practice.getBrokerType() == BrokerType.PRACTICE);
				Assert.assertTrue(dealer == null || BrokerType.DEALER == dealer.getBrokerType());
				Assert.assertTrue(operator == null || BrokerType.OPERATOR == operator.getBrokerType());
			}
		}
		Assert.assertEquals(1176, adviserCount);
	}

	private void checkOfficeHierarchy(Map <BrokerKey, Broker> brokerMap)
	{
		int officeCount = 0;
		for (Broker broker : brokerMap.values())
		{

			if (broker.getBrokerType() == BrokerType.OFFICE)
			{
				officeCount++;
				BrokerImpl office =(BrokerImpl) brokerMap.get(broker.getOfficeKey());
				BrokerImpl practice =(BrokerImpl) brokerMap.get(broker.getPracticeKey());
				BrokerImpl dealer = (BrokerImpl)brokerMap.get(broker.getDealerKey());
				BrokerImpl operator = (BrokerImpl)brokerMap.get(broker.getOperatorKey());
				Assert.assertTrue(office == null || office.getBrokerType() == BrokerType.OFFICE);
				Assert.assertTrue(practice == null || practice.getBrokerType() == BrokerType.PRACTICE);
				Assert.assertTrue(dealer == null || BrokerType.DEALER == dealer.getBrokerType());
				Assert.assertTrue(operator == null || BrokerType.OPERATOR == operator.getBrokerType());
			}
		}
		Assert.assertEquals(1, officeCount);
	}

	private void checkPracticeHierarchy(Map <BrokerKey, Broker> brokerMap)
	{
		int practiceCount = 0;
		for (Broker broker : brokerMap.values())
		{

			if (broker.getBrokerType() == BrokerType.PRACTICE)
			{
				practiceCount++;
				BrokerImpl practice =(BrokerImpl) brokerMap.get(broker.getPracticeKey());
				BrokerImpl dealer = (BrokerImpl)brokerMap.get(broker.getDealerKey());
				BrokerImpl operator = (BrokerImpl)brokerMap.get(broker.getOperatorKey());
				Assert.assertNull(broker.getOfficeKey());
				Assert.assertTrue(practice == null || practice.getBrokerType() == BrokerType.PRACTICE);
				Assert.assertTrue(dealer == null || BrokerType.DEALER == dealer.getBrokerType());
				Assert.assertTrue(operator == null || BrokerType.OPERATOR == operator.getBrokerType());
			}
		}
		Assert.assertEquals(2, practiceCount);
	}

	private void checkDealerHierarchy(Map <BrokerKey, Broker> brokerMap)
	{
		int dealerCount = 0;
		for (Broker broker : brokerMap.values())
		{

			if (broker.getBrokerType() == BrokerType.DEALER)
			{
				dealerCount++;
				Assert.assertNull(broker.getOfficeKey());
				Assert.assertNull(broker.getPracticeKey());
				BrokerImpl dealer = (BrokerImpl)brokerMap.get(broker.getDealerKey());
				BrokerImpl operator = (BrokerImpl)brokerMap.get(broker.getOperatorKey());
				Assert.assertTrue(dealer == null || BrokerType.DEALER == dealer.getBrokerType());
				Assert.assertTrue(operator == null || BrokerType.OPERATOR == operator.getBrokerType());
			}
		}
		Assert.assertEquals(168, dealerCount);
	}

	private void checkOperatorHierarchy(Map <BrokerKey, Broker> brokerMap)
	{
		int operatorCount = 0;
		for (Broker broker : brokerMap.values())
		{

			if (broker.getBrokerType() == BrokerType.OPERATOR)
			{
				operatorCount++;
				Assert.assertNull(broker.getOfficeKey());
				Assert.assertNull(broker.getPracticeKey());
				Assert.assertNull(broker.getDealerKey());
				Broker operator = (BrokerImpl)brokerMap.get(broker.getOperatorKey());
				Assert.assertTrue(operator == null || BrokerType.OPERATOR == operator.getBrokerType());

			}
		}
		Assert.assertEquals(0, operatorCount);
	}

	private void checkIMHierarchy(Map <BrokerKey, Broker> brokerMap)
	{
		int imCount = 0;
		for (Broker broker : brokerMap.values())
		{

			if (broker.getBrokerType() == BrokerType.INVESTMENT_MANAGER)
			{
				imCount++;
				Assert.assertNull(broker.getOfficeKey());
				Assert.assertNull(broker.getPracticeKey());
				Assert.assertNull(broker.getDealerKey());
				Assert.assertNull(broker.getOperatorKey());
			}
		}
		Assert.assertEquals(2, imCount);
	}

	private void checkOtherHierarchy(Map <BrokerKey, Broker> brokerMap)
	{
		int otherCount = 0;
		for (Broker broker : brokerMap.values())
		{

			if (broker.getBrokerType() == BrokerType.OTHER)
			{
				otherCount++;
				Assert.assertNull(broker.getOfficeKey());
				Assert.assertNull(broker.getPracticeKey());
				Assert.assertNull(broker.getDealerKey());
				Assert.assertNull(broker.getOperatorKey());
			}
		}
		Assert.assertEquals(29, otherCount);
	}
}
