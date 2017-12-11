package com.bt.nextgen.service.avaloq.account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import util.StaticCodeCategories;
import com.avaloq.abs.screen_rep.hira.btfg$ui_bp_bp.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.AccountType;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.btfin.panorama.service.integration.account.PersonRelation;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.domain.InvestorRole;

@RunWith(MockitoJUnitRunner.class)
public class WrapAccountDetailConverterTest
{
	@InjectMocks
	private WrapAccountDetailConverter converter = new WrapAccountDetailConverter();

	@Mock
	private StaticIntegrationService staticService;

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
				if (CodeCategory.ACCOUNT_STRUCTURE_TYPE.equals(args[0]) && "20611".equals(args[1]))
				{
					return new CodeImpl("20611", "btfg$indvl", "Individual");
				}
				if (CodeCategory.ACCOUNT_STRUCTURE_TYPE.equals(args[0]) && "20614".equals(args[1]))
				{
					return new CodeImpl("20614", "btfg$trust", "Trust");
				}
				if (CodeCategory.ACCOUNT_STRUCTURE_TYPE.equals(args[0]) && "20612".equals(args[1]))
				{
					return new CodeImpl("20612", "joint", "Joint");
				}
				else if (CodeCategory.CONTAINER_TYPE.equals(args[0]) && "7102".equals(args[1]))
				{
					return new CodeImpl("7102", "portf_dir", "BT Direct");
				}
				else if (CodeCategory.CONTAINER_TYPE.equals(args[0]) && "7181".equals(args[1]))
				{
					return new CodeImpl("7181", "aux_in", "AUX Bank positions customer");
				}
				else if (CodeCategory.CONTAINER_TYPE.equals(args[0]) && "7105".equals(args[1]))
				{
					return new CodeImpl("7105", "mp", "Managed Portfolio");
				}
				else if (CodeCategory.CURRENCY_TYPE.equals(args[0]) && "1009".equals(args[1]))
				{
					return new CodeImpl("1009", "aud", "AUD");
				}
				else if (CodeCategory.PERSON_RELATION_TYPE.equals(args[0]) && "1".equals(args[1]))
				{
                    return new CodeImpl("1", "acc_owner", "Account owner", "acc_owner");
				}
				else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5025".equals(args[1]))
				{
                    return new CodeImpl("5025", "SHAREHOLDER", "Shareholder", "btfg$sharehld");
                }
				else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5020".equals(args[1]))
				{
                    return new CodeImpl("5020", "DIRECTOR", "Director", "btfg$director");
				}
				else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5024".equals(args[1]))
				{
                    return new CodeImpl("5024", "BENEF", "Beneficiary", "btfg$benef");
                }
				else if (CodeCategory.PERSON_ASSOCIATION.equals(args[0]) && "5005".equals(args[1]))
				{
                    return new CodeImpl("5005", "TRUSTEE", "Trustee", "btfg$trustee");
				}
				else if (CodeCategory.ACCOUNT_TRANSACTION_PERMISSION.equals(args[0]) && "1".equals(args[1]))
				{
                    return new CodeImpl("1", "NO_TRX", "No Transaction", "no_trx");
				}
				else if (CodeCategory.ACCOUNT_TRANSACTION_PERMISSION.equals(args[0]) && "1124".equals(args[1]))
				{
                    return new CodeImpl("1124", "PAY_INPAY_LINK", "Payments & Deposits to Linked Accounts Only", "btfg$pay_link");
				}
				else if (CodeCategory.ACCOUNT_TRANSACTION_PERMISSION.equals(args[0]) && "1125".equals(args[1]))
				{
                    return new CodeImpl("1125", "PAY_INPAY_ALL", "Payments & Deposits to Anyone", "btfg$pay_all");
                }
				else if (CodeCategory.ACCOUNT_TRANSACTION_PERMISSION.equals(args[0]) && "1126".equals(args[1]))
				{
                    return new CodeImpl("1126", "BTFG$BP_ACC_MT", "BP Account Maintenance","btfg$bp_acc_mt");
				}
                else if (CodeCategory.ACCOUNT_STATUS.equals(args[0]) && "2750".equals(args[1]))
                {
                    return new CodeImpl("2750", "PEND_OPN", "BTFG$BP_STATUS", "pend_opn");
                }
				else
				{
					CodeImpl code = null;
					if (null != (code = StaticCodeCategories.retrieveCodeCategoryMap("" + args[0] + args[1])))
					{
						return code;
					}
					else
					{

						return new CodeImpl("Unknow", "Unknow", "Unknow");
					}
				}
			}
		});
	}

	@Test
	public void testToModelAccountOwner_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/Account_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <WrapAccountDetailImpl> result = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertFalse(serviceErrors.hasErrors());
		WrapAccountDetail wrapAccount = result.get(0);
		Assert.assertEquals("73330", wrapAccount.getAccountKey().getId());
		Assert.assertEquals(AccountStatus.PEND_OPN, wrapAccount.getAccountStatus());
		Assert.assertEquals("Adrian Demo Smith", wrapAccount.getAccountName());
		Assert.assertEquals(AccountType.WRAP, wrapAccount.getAccountType());
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		Assert.assertEquals("69825", wrapAccount.getAdviserPersonId().getId());
		Assert.assertEquals("2014-09-10", formatter.print(wrapAccount.getSignDate()));
		Assert.assertEquals("2014-09-10", formatter.print(wrapAccount.getOpenDate()));
		Assert.assertEquals("2014-09-15", formatter.print(wrapAccount.getClosureDate()));
		Assert.assertEquals(AccountStructureType.Individual, wrapAccount.getAccountStructureType());
		Assert.assertEquals(CGTLMethod.MIN_GAIN, wrapAccount.getcGTLMethod());

		Assert.assertEquals(4, wrapAccount.getSubAccounts().size());
		Assert.assertEquals(1, wrapAccount.getAccountOwners().size());
		Assert.assertEquals(true, wrapAccount.getAccountOwners().contains(ClientKey.valueOf("73328")));
		Assert.assertEquals(BrokerKey.valueOf("69816"), wrapAccount.getAdviserKey());
		Assert.assertEquals(1, wrapAccount.getAssociatedPersons().size());
		PersonRelation associatedPerson = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69825"));
		Assert.assertNull(associatedPerson);
		associatedPerson = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("73328"));

		Assert.assertEquals(true, associatedPerson.isApprover());
		//Assert.assertEquals(true, associatedPerson.getPermissions().contains(TransactionPermission.Payments_Deposits));
		Assert.assertEquals(true, associatedPerson.getPermissions().contains(TransactionPermission.Account_Maintenance));
		Assert.assertEquals("73328", wrapAccount.getPrimaryContactPersonId().getId());
		Assert.assertEquals(true, associatedPerson.getPersonRoles().contains(InvestorRole.Owner));
		Assert.assertEquals(1, wrapAccount.getLinkedAccounts().size());
		LinkedAccount linkedAccount = wrapAccount.getLinkedAccounts().get(0);
		Assert.assertEquals("123456789", linkedAccount.getAccountNumber());
		Assert.assertEquals("36081", linkedAccount.getBsb());
		//Assert.assertEquals("AUD", linkedAccount.getCurrencyId());
		Assert.assertEquals("Linked Account Name 01", linkedAccount.getName());
		Assert.assertEquals(0, new BigDecimal(3000).compareTo(linkedAccount.getLimit()));
		Assert.assertEquals("Linked Account Nickname 01", linkedAccount.getNickName());
		Assert.assertFalse(serviceErrors.hasErrors());
	}

	@Test
	public void testToModelAccountJoint_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/Account_Joint_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		List <WrapAccountDetailImpl> result = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertFalse(serviceErrors.hasErrors());
		WrapAccountDetail wrapAccount = result.get(0);
		Assert.assertEquals("33066", wrapAccount.getAccountKey().getId());
		Assert.assertEquals("Michael David Phillips, Katherine Ann Phillips", wrapAccount.getAccountName());
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		Assert.assertEquals("2014-04-24", formatter.print(wrapAccount.getOpenDate()));
		Assert.assertEquals("32606", wrapAccount.getAdviserPersonId().getId());
		Assert.assertEquals(BrokerKey.valueOf("65453"), wrapAccount.getAdviserKey());
		Assert.assertEquals("68445", wrapAccount.getProductKey().getId());
		//Assert.assertEquals("AUD", wrapAccount.getCurrencyId());
		Assert.assertEquals(AccountStructureType.Joint, wrapAccount.getAccountStructureType());
		Assert.assertEquals(CGTLMethod.MIN_GAIN, wrapAccount.getcGTLMethod());

		Assert.assertEquals(2, wrapAccount.getAccountOwners().size());

		Assert.assertEquals("33058", wrapAccount.getPrimaryContactPersonId().getId());
		Assert.assertTrue(wrapAccount.getAccountOwners().contains(ClientKey.valueOf("33058")));
		Assert.assertTrue(wrapAccount.getAccountOwners().contains(ClientKey.valueOf("33062")));

		Assert.assertEquals(3, wrapAccount.getAssociatedPersons().size());

		PersonRelation personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("33058"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Owner));
        //TODO -verify xml and code change for transaction permission
		//Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Account_Maintenance));
		//Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Payments_Deposits));
		Assert.assertTrue(personRelation.isApprover());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("33062"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Owner));
		//Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Account_Maintenance));
		//Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Payments_Deposits));
		Assert.assertTrue(personRelation.isApprover());

		Assert.assertEquals(2, wrapAccount.getSubAccounts().size());

		//linked accounts
		Assert.assertEquals(3, wrapAccount.getLinkedAccounts().size());
		LinkedAccount linkedAccount = wrapAccount.getLinkedAccounts().get(0);
		Assert.assertEquals("120463807", linkedAccount.getAccountNumber());
		Assert.assertEquals("182222", linkedAccount.getBsb());
		//Assert.assertEquals("AUD", linkedAccount.getCurrencyId());
		Assert.assertEquals("Michael & Philippa Phillips", linkedAccount.getName());
		Assert.assertTrue(linkedAccount.isPrimary());
		Assert.assertEquals("Michael & Pip Macq CMT", linkedAccount.getNickName());

		linkedAccount = wrapAccount.getLinkedAccounts().get(1);
		Assert.assertEquals("20299851", linkedAccount.getAccountNumber());
		Assert.assertEquals("32854", linkedAccount.getBsb());
		//Assert.assertEquals("AUD", linkedAccount.getCurrencyId());
		Assert.assertEquals("PHILIPPA GRANT", linkedAccount.getName());
		Assert.assertFalse(linkedAccount.isPrimary());
		Assert.assertEquals("Northwood Expense", linkedAccount.getNickName());

		Assert.assertEquals(5, wrapAccount.getRegPayees().size());
		BankAccount regPayee = wrapAccount.getRegPayees().get(0);
		Assert.assertEquals("32854", regPayee.getBsb());
		Assert.assertEquals("020299851", regPayee.getAccountNumber());
		Assert.assertEquals("Macquarie Mortgages", regPayee.getName());
		Assert.assertEquals("DAD", regPayee.getNickName());

		regPayee = wrapAccount.getRegPayees().get(1);
		Assert.assertEquals("32044", regPayee.getBsb());
		Assert.assertEquals("217988", regPayee.getAccountNumber());
		Assert.assertEquals("Conaught Pty Ltd", regPayee.getName());
		Assert.assertEquals("Peppers accom", regPayee.getNickName());

	}

	@Test
	public void testToModelAccountCompany_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/Account_Company_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		List <WrapAccountDetailImpl> result = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertFalse(serviceErrors.hasErrors());
		WrapAccountDetail wrapAccount = result.get(0);
		Assert.assertEquals("69612", wrapAccount.getAccountKey().getId());
		Assert.assertEquals("Demo Apple Pty Ltd", wrapAccount.getAccountName());
		Assert.assertEquals("120009162", wrapAccount.getAccountNumber());
		Assert.assertEquals("262786", wrapAccount.getBsb());
		Assert.assertEquals("220186", wrapAccount.getBillerCode());
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		Assert.assertEquals("2014-08-26", formatter.print(wrapAccount.getOpenDate()));
		Assert.assertEquals("66725", wrapAccount.getAdviserPersonId().getId());
		Assert.assertEquals(BrokerKey.valueOf("66716"), wrapAccount.getAdviserKey());
		Assert.assertEquals("65365", wrapAccount.getProductKey().getId());
		//Assert.assertEquals("AUD", wrapAccount.getCurrencyId());
		Assert.assertEquals(AccountStructureType.Company, wrapAccount.getAccountStructureType());
		Assert.assertEquals(CGTLMethod.MIN_GAIN, wrapAccount.getcGTLMethod());

		Assert.assertEquals(1, wrapAccount.getAccountOwners().size());

		Assert.assertEquals("69607", wrapAccount.getPrimaryContactPersonId().getId());
		Assert.assertTrue(wrapAccount.getAccountOwners().contains(ClientKey.valueOf("69609")));

		Assert.assertEquals(3, wrapAccount.getAssociatedPersons().size());

		PersonRelation personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69609"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Owner));
		Assert.assertTrue(CollectionUtils.isEmpty(personRelation.getPermissions()));
		Assert.assertFalse(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69607"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Director));
	//	Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Payments_Deposits));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertTrue(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69608"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Secretary));
		Assert.assertTrue(CollectionUtils.isEmpty(personRelation.getPermissions()));
		Assert.assertFalse(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		//linked accounts
		Assert.assertEquals(1, wrapAccount.getLinkedAccounts().size());
		LinkedAccount linkedAccount = wrapAccount.getLinkedAccounts().get(0);
		Assert.assertEquals("123456789", linkedAccount.getAccountNumber());
		Assert.assertEquals("36158", linkedAccount.getBsb());
		//Assert.assertEquals("AUD", linkedAccount.getCurrencyId());
		Assert.assertEquals("Linked Account Name 31", linkedAccount.getName());
		Assert.assertTrue(linkedAccount.isPrimary());
		Assert.assertEquals("Linked Account Nickname 31", linkedAccount.getNickName());

		Assert.assertEquals(2, wrapAccount.getSubAccounts().size());

	}

	@Test
	public void SMSF_Account_ValidResponse_ObjectCreated_NoServiceErrors_test() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/Account_SMSF_Individual_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		List <WrapAccountDetailImpl> result = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertFalse(serviceErrors.hasErrors());
		WrapAccountDetail wrapAccount = result.get(0);

		Assert.assertEquals("69396", wrapAccount.getAccountKey().getId());
		Assert.assertEquals("Demo Aisha Tan SMSF", wrapAccount.getAccountName());
		Assert.assertEquals("120009048", wrapAccount.getAccountNumber());
		Assert.assertEquals("262786", wrapAccount.getBsb());
		Assert.assertEquals("220186", wrapAccount.getBillerCode());
		Assert.assertEquals(CGTLMethod.MIN_GAIN, wrapAccount.getcGTLMethod());

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		Assert.assertEquals("2014-08-26", formatter.print(wrapAccount.getOpenDate()));

		Assert.assertEquals("66725", wrapAccount.getAdviserPersonId().getId());
		Assert.assertEquals(BrokerKey.valueOf("66716"), wrapAccount.getAdviserKey());

		Assert.assertEquals("65365", wrapAccount.getProductKey().getId());
		//Assert.assertEquals("AUD", wrapAccount.getCurrencyId());
		Assert.assertEquals(AccountStructureType.SMSF, wrapAccount.getAccountStructureType());

		Assert.assertEquals(1, wrapAccount.getAccountOwners().size());

		Assert.assertEquals("69389", wrapAccount.getPrimaryContactPersonId().getId());
		Assert.assertTrue(wrapAccount.getAccountOwners().contains(ClientKey.valueOf("69392")));

		Assert.assertEquals(4, wrapAccount.getAssociatedPersons().size());

		PersonRelation personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69392"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Owner));
		Assert.assertTrue(CollectionUtils.isEmpty(personRelation.getPermissions()));
		Assert.assertFalse(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69389"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Trustee));
		//Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Payments_Deposits));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertTrue(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69390"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Member));
		Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Account_Maintenance));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69391"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Member));
		Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Account_Maintenance));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		Assert.assertEquals(2, wrapAccount.getSubAccounts().size());

		//linked accounts
		Assert.assertEquals(1, wrapAccount.getLinkedAccounts().size());
		LinkedAccount linkedAccount = wrapAccount.getLinkedAccounts().get(0);
		Assert.assertEquals("123456789", linkedAccount.getAccountNumber());
		Assert.assertEquals("62111", linkedAccount.getBsb());
		//Assert.assertEquals("AUD", linkedAccount.getCurrencyId());
		Assert.assertEquals("Linked Account Name 50004", linkedAccount.getName());
		Assert.assertTrue(linkedAccount.isPrimary());
		Assert.assertEquals("Linked Account Nickname 50004", linkedAccount.getNickName());

	}

	@Test
	public void SMSF_Corporate_Account_ValidResponse_ObjectCreated_NoServiceErrors_test() throws Exception
	{

		Rep report = JaxbUtil.unmarshall("/webservices/response/Account_SMSF_Corporate_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		List <WrapAccountDetailImpl> result = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertFalse(serviceErrors.hasErrors());
		WrapAccountDetail wrapAccount = result.get(0);
		Assert.assertEquals("69776", wrapAccount.getAccountKey().getId());
		Assert.assertEquals("Demo Boson SMSF", wrapAccount.getAccountName());
		Assert.assertEquals("120009279", wrapAccount.getAccountNumber());
		Assert.assertEquals("262786", wrapAccount.getBsb());
		Assert.assertEquals("220186", wrapAccount.getBillerCode());
		Assert.assertEquals(CGTLMethod.MIN_GAIN, wrapAccount.getcGTLMethod());

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		Assert.assertEquals("2014-08-26", formatter.print(wrapAccount.getOpenDate()));

		Assert.assertEquals("66773", wrapAccount.getAdviserPersonId().getId());
		Assert.assertEquals(BrokerKey.valueOf("66767"), wrapAccount.getAdviserKey());

		Assert.assertEquals("65365", wrapAccount.getProductKey().getId());
		//Assert.assertEquals("AUD", wrapAccount.getCurrencyId());
		Assert.assertEquals(AccountStructureType.SMSF, wrapAccount.getAccountStructureType());

		Assert.assertEquals(1, wrapAccount.getAccountOwners().size());

		Assert.assertEquals("69770", wrapAccount.getPrimaryContactPersonId().getId());
		Assert.assertTrue(wrapAccount.getAccountOwners().contains(ClientKey.valueOf("69772")));

		Assert.assertEquals(5, wrapAccount.getAssociatedPersons().size());

		PersonRelation personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69772"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Owner));
		Assert.assertTrue(CollectionUtils.isEmpty(personRelation.getPermissions()));
		Assert.assertFalse(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69771"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Trustee));
		Assert.assertTrue(CollectionUtils.isEmpty(personRelation.getPermissions()));
		Assert.assertFalse(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69770"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Director));
		//Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Payments_Deposits));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertTrue(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69768"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Member));
		Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Account_Maintenance));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69769"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Member));
		Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Account_Maintenance));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		Assert.assertEquals(2, wrapAccount.getSubAccounts().size());

		//linked accounts
		Assert.assertEquals(1, wrapAccount.getLinkedAccounts().size());
		LinkedAccount linkedAccount = wrapAccount.getLinkedAccounts().get(0);
		Assert.assertEquals("123456789", linkedAccount.getAccountNumber());
		Assert.assertEquals("62111", linkedAccount.getBsb());
		//Assert.assertEquals("AUD", linkedAccount.getCurrencyId());
		Assert.assertEquals("Linked Account Name 50002", linkedAccount.getName());
		Assert.assertTrue(linkedAccount.isPrimary());
		Assert.assertEquals("Linked Account Nickname 50002", linkedAccount.getNickName());

	}

	@Test
	public void testToModelAccountTrust_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/Account_Trust_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		List <WrapAccountDetailImpl> result = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertFalse(serviceErrors.hasErrors());
		WrapAccountDetail wrapAccount = result.get(0);
		Assert.assertEquals("32565", wrapAccount.getAccountKey().getId());
		Assert.assertEquals("Furness Investment Trust", wrapAccount.getAccountName());
		Assert.assertEquals("120000831", wrapAccount.getAccountNumber());
		Assert.assertEquals("262786", wrapAccount.getBsb());
		Assert.assertEquals("220186", wrapAccount.getBillerCode());
		Assert.assertEquals(CGTLMethod.MIN_GAIN, wrapAccount.getcGTLMethod());

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		Assert.assertEquals("2014-04-22", formatter.print(wrapAccount.getSignDate()));
		Assert.assertEquals("2014-04-22", formatter.print(wrapAccount.getOpenDate()));
		Assert.assertEquals("29788", wrapAccount.getAdviserPersonId().getId());
		Assert.assertEquals(BrokerKey.valueOf("65022"), wrapAccount.getAdviserKey());
		Assert.assertEquals("68445", wrapAccount.getProductKey().getId());
		//Assert.assertEquals("AUD", wrapAccount.getCurrencyId());
		Assert.assertEquals(AccountStructureType.Trust, wrapAccount.getAccountStructureType());

		Assert.assertEquals("32561", wrapAccount.getPrimaryContactPersonId().getId());
		Assert.assertTrue(wrapAccount.getAccountOwners().contains(ClientKey.valueOf("32556")));
		Assert.assertEquals(1, wrapAccount.getAccountOwners().size());
		Set <ClientKey> set = wrapAccount.getAssociatedPersons().keySet();
		Assert.assertEquals(5, wrapAccount.getAssociatedPersons().size());

		PersonRelation personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("32561"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Beneficiary));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Director));
		Assert.assertTrue(personRelation.isApprover());
		//Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Payments_Deposits));
		Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Account_Maintenance));

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("32556"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Owner));
		Assert.assertEquals(null, personRelation.getPermissions());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("32558"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Trustee));
		Assert.assertEquals(null, personRelation.getPermissions());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("38191"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Beneficiary));
		Assert.assertFalse(personRelation.isApprover());
		Assert.assertEquals(null, personRelation.getPermissions());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("29788"));
		Assert.assertEquals(null, personRelation.getPersonRoles());
		Assert.assertTrue(personRelation.isAdviser());
		//Assert.assertEquals(true, personRelation.getPermissions().contains(TransactionPermission.Payments_Deposits));
		Assert.assertEquals(1, personRelation.getPermissions().size());

		Assert.assertEquals(1, personRelation.getPermissions().size());

		Assert.assertEquals(2, wrapAccount.getSubAccounts().size());

		//linked accounts
		Assert.assertEquals(1, wrapAccount.getLinkedAccounts().size());
		LinkedAccount linkedAccount = wrapAccount.getLinkedAccounts().get(0);
		Assert.assertEquals("10292846", linkedAccount.getAccountNumber());
		Assert.assertEquals("64178", linkedAccount.getBsb());
		//Assert.assertEquals("AUD", linkedAccount.getCurrencyId());
		Assert.assertEquals("Furness Investment Trust", linkedAccount.getName());
		Assert.assertTrue(linkedAccount.isPrimary());
		Assert.assertEquals(null, linkedAccount.getNickName());

	}

	@Test
	public void Trust_Corporate_Account_ValidResponse_ObjectCreated_NoServiceErrors_test() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/Account_Trust_Corporate_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		List <WrapAccountDetailImpl> result = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(1, result.size());
		Assert.assertFalse(serviceErrors.hasErrors());
		WrapAccountDetail wrapAccount = result.get(0);

		Assert.assertEquals("69802", wrapAccount.getAccountKey().getId());
		Assert.assertEquals("Demo Boral Employee Trust", wrapAccount.getAccountName());
		Assert.assertEquals("120009287", wrapAccount.getAccountNumber());
		Assert.assertEquals("262786", wrapAccount.getBsb());
		Assert.assertEquals("220186", wrapAccount.getBillerCode());
		Assert.assertEquals(CGTLMethod.MIN_GAIN, wrapAccount.getcGTLMethod());

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		Assert.assertEquals("2014-08-26", formatter.print(wrapAccount.getOpenDate()));

		Assert.assertEquals("66773", wrapAccount.getAdviserPersonId().getId());
		Assert.assertEquals(BrokerKey.valueOf("66767"), wrapAccount.getAdviserKey());

		Assert.assertEquals("65365", wrapAccount.getProductKey().getId());
		//Assert.assertEquals("AUD", wrapAccount.getCurrencyId());
		Assert.assertEquals(AccountStructureType.Trust, wrapAccount.getAccountStructureType());

		Assert.assertEquals(1, wrapAccount.getAccountOwners().size());

		Assert.assertEquals("69796", wrapAccount.getPrimaryContactPersonId().getId());
		Assert.assertTrue(wrapAccount.getAccountOwners().contains(ClientKey.valueOf("69798")));

		Assert.assertEquals(5, wrapAccount.getAssociatedPersons().size());

		PersonRelation personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69798"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Owner));
		Assert.assertTrue(CollectionUtils.isEmpty(personRelation.getPermissions()));
		Assert.assertFalse(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69797"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Trustee));
		Assert.assertTrue(CollectionUtils.isEmpty(personRelation.getPermissions()));
		Assert.assertFalse(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69796"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Director));
		//Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Payments_Deposits));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertTrue(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69794"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Beneficiary));
		Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Account_Maintenance));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		personRelation = wrapAccount.getAssociatedPersons().get(ClientKey.valueOf("69795"));
		Assert.assertTrue(personRelation.getPersonRoles().contains(InvestorRole.Beneficiary));
		Assert.assertTrue(personRelation.getPermissions().contains(TransactionPermission.Account_Maintenance));
		Assert.assertTrue(personRelation.isApprover());
		Assert.assertFalse(personRelation.isPrimaryContact());
		Assert.assertFalse(personRelation.isAdviser());

		Assert.assertEquals(2, wrapAccount.getSubAccounts().size());

		//linked accounts
		Assert.assertEquals(1, wrapAccount.getLinkedAccounts().size());
		LinkedAccount linkedAccount = wrapAccount.getLinkedAccounts().get(0);
		Assert.assertEquals("123456789", linkedAccount.getAccountNumber());
		Assert.assertEquals("62111", linkedAccount.getBsb());
		//Assert.assertEquals("AUD", linkedAccount.getCurrencyId());
		Assert.assertEquals("Linked Account Name 40002", linkedAccount.getName());
		Assert.assertTrue(linkedAccount.isPrimary());
		Assert.assertEquals("Linked Account Nickname 40002", linkedAccount.getNickName());

	}

}
