package com.bt.nextgen.web.controller.cash;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import com.bt.nextgen.payments.repository.LinkedAccount;
import com.bt.nextgen.payments.repository.PayAnyonePayee;
import com.bt.nextgen.payments.web.model.BpayBillerModel;
import com.bt.nextgen.web.controller.cash.util.PayeeControllerUtil;

public class PayeeControllerUtilTest
{

	private BpayBillerCodeRepository mockBpayBillerCodeRepository;

	@Before
	public void setup()
	{
		mockBpayBillerCodeRepository = Mockito.mock(BpayBillerCodeRepository.class);
	}

	@Test
	public void loadBillersAsDtoTest()
	{
		BpayBiller mockBpayBiller = Mockito.mock(BpayBiller.class);
		when(mockBpayBiller.getBillerCode()).thenReturn("code");
		when(mockBpayBiller.getBillerName()).thenReturn("name");

		when(mockBpayBillerCodeRepository.findByPartialBillerCode(Matchers.any(String.class))).thenReturn(Arrays.asList(mockBpayBiller));

		List <BpayBillerModel> bpayBillerModelList = PayeeControllerUtil.loadBillers(mockBpayBillerCodeRepository, "startsWith");
		assertThat(bpayBillerModelList.size(), is(1));
		BpayBillerModel model = bpayBillerModelList.get(0);
		assertThat(model.billerCode, is("code"));
		assertThat(model.billerName, is("name"));
	}

	@Test
	public void testlinkedAccountConverter()
	{
		PayeeModel payeeModel = new PayeeModel();
		payeeModel.setName("test");
		payeeModel.setReference("1234");
		payeeModel.setCode("123-321");
		payeeModel.setNickname("nick");
		payeeModel.setId("1");
		payeeModel.setPayeeType(PayeeType.PRIMARY_LINKED);
		LinkedAccount linkedAccount = PayeeControllerUtil.linkedAccountConverter(payeeModel, "12345678");
		assertNotNull(linkedAccount);
		assertEquals(PayeeType.PRIMARY_LINKED, linkedAccount.getPayeeType());

	}

	@Test
	public void testHasMoreThan4Secondaries()
	{
		Boolean status = false;
		List <LinkedAccount> linkedAccountList = new ArrayList <LinkedAccount>();
		LinkedAccount account1 = new LinkedAccount();
		account1.setPayeeType(PayeeType.SECONDARY_LINKED);
		linkedAccountList.add(account1);
		LinkedAccount account2 = new LinkedAccount();
		account2.setPayeeType(PayeeType.SECONDARY_LINKED);
		linkedAccountList.add(account2);
		LinkedAccount account3 = new LinkedAccount();
		account3.setPayeeType(PayeeType.SECONDARY_LINKED);
		linkedAccountList.add(account3);
		LinkedAccount account4 = new LinkedAccount();
		account4.setPayeeType(PayeeType.PRIMARY_LINKED);
		linkedAccountList.add(account4);
		status = PayeeControllerUtil.hasMoreThan4Secondaries(linkedAccountList);
		assertThat(status, is(false));

		//TODO will remove comment once logic for this will be uncommented(after avaloq integration)
		/*LinkedAccount account5 = new LinkedAccount();
		account5.setPayeeType(PayeeType.SECONDARY_LINKED);
		linkedAccountList.add(account5);
		status = PayeeControllerUtil.hasMoreThan4Secondaries(linkedAccountList);
		assertThat(status, is(true));

		linkedAccountList.remove(account3);
		status = PayeeControllerUtil.hasMoreThan4Secondaries(linkedAccountList);
		assertThat(status, is(false));*/
	}

	@Test
	public void testConvertModelToPayAnyonePayeeDomain()
	{
		PayeeModel payeeModel = new PayeeModel();
		payeeModel.setName("PayTest");
		payeeModel.setReference("12345");
		payeeModel.setCode("012-055");
		payeeModel.setNickname("nick");
		payeeModel.setId("2");
		PayAnyonePayee payAnyonePayee = PayeeControllerUtil.convertModelToDomain(payeeModel, "1234567");
		assertNotNull("Getting Null object", payAnyonePayee);
		assertEquals(PayeeType.PAY_ANYONE, payAnyonePayee.getPayeeType());

	}

	@Test
	public void testSortedPayeeList()
	{
		List <PayeeModel> payeeModelList = new ArrayList <PayeeModel>();
		PayeeModel payeeModel1 = new PayeeModel();
		payeeModel1.setPayeeType(PayeeType.SECONDARY_LINKED);
		payeeModel1.setName("test1");
		payeeModelList.add(payeeModel1);
		PayeeModel payeeModel2 = new PayeeModel();
		payeeModel2.setPayeeType(PayeeType.SECONDARY_LINKED);
		payeeModel2.setName("atest");
		payeeModelList.add(payeeModel2);
		PayeeModel payeeModel3 = new PayeeModel();
		payeeModel3.setPayeeType(PayeeType.SECONDARY_LINKED);
		payeeModel3.setName("utest");
		payeeModelList.add(payeeModel3);
		PayeeModel payeeModel4 = new PayeeModel();
		payeeModel4.setPayeeType(PayeeType.PRIMARY_LINKED);
		payeeModel4.setName("heltest");
		payeeModelList.add(payeeModel4);
		payeeModelList = PayeeControllerUtil.getSortedPayeeList(payeeModelList);
		assertEquals("heltest", payeeModelList.get(0).getName());
		assertEquals("atest", payeeModelList.get(1).getName());
		assertEquals("test1", payeeModelList.get(2).getName());
		assertEquals("utest", payeeModelList.get(3).getName());
	}
}
