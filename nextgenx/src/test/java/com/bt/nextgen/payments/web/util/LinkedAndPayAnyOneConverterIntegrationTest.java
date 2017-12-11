package com.bt.nextgen.payments.web.util;

import com.bt.nextgen.addressbook.web.model.PayAnyOneAndLinkedAccountModel;
import com.bt.nextgen.clients.util.LinkedAndPayAnyOneConverter;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.LinkedAccount;
import com.bt.nextgen.payments.repository.PayAnyonePayee;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class LinkedAndPayAnyOneConverterIntegrationTest extends BaseSecureIntegrationTest
{

@Test
public void testToModelForLinkedAccount()
{
	List <LinkedAccount> linkedAccountList = new ArrayList<LinkedAccount>();
	LinkedAccount linkedAccount= new LinkedAccount();
	linkedAccount.setAccountName("Testing 1");
	linkedAccount.setAccountNumber("123456789");
	Bsb bsb = new Bsb();
	bsb.setBsbCode("012055");
	linkedAccount.setBsb(bsb);
	linkedAccount.setCashAccountId("12345");
	linkedAccount.setNickname("NickTest");
	linkedAccount.setPayeeType(PayeeType.SECONDARY_LINKED);
	linkedAccountList.add(linkedAccount);
	List <PayAnyOneAndLinkedAccountModel> PayAnyOneAndLinkedAccountModelList =LinkedAndPayAnyOneConverter.toModelForLinkedAccount(linkedAccountList);
	assertNotNull("PayAnyOneAndLinkedAccountModelList should not be null", PayAnyOneAndLinkedAccountModelList);
 }

@Test
public void testToModelForPayAnyAccount()
{
	List <PayAnyonePayee> payAnyOneList= new ArrayList<PayAnyonePayee>();
	PayAnyonePayee payAnyonePayee=new PayAnyonePayee();
	payAnyonePayee.setAccountNumber("369852147");
	Bsb bsb = new Bsb();
	bsb.setBsbCode("012055");
	payAnyonePayee.setBsb(bsb);
	payAnyonePayee.setCashAccountId("123");
	payAnyonePayee.setDescription("Nothing");
	payAnyonePayee.setName("Test Name");
	payAnyonePayee.setNickname("Pay Any One");
	payAnyonePayee.setPayeeType(PayeeType.PAY_ANYONE);
	payAnyOneList.add(payAnyonePayee);
	List <PayAnyOneAndLinkedAccountModel> PayAnyOneAndLinkedAccountModelList =LinkedAndPayAnyOneConverter.toModelForPayAnyOne(payAnyOneList);
	assertNotNull("PayAnyOneAndLinkedAccountModelList should not be null", PayAnyOneAndLinkedAccountModelList);
 }
}
