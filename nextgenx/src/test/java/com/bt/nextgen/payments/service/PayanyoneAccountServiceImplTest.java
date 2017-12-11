package com.bt.nextgen.payments.service;


import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.hamcrest.Matchers.containsString;
import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.PayAnyonePayee;
import com.bt.nextgen.payments.repository.Payee;
import com.bt.nextgen.payments.repository.PayeeRepository;
import com.bt.nextgen.test.MockAuthentication;

@RunWith(MockitoJUnitRunner.class)
public class PayanyoneAccountServiceImplTest extends MockAuthentication
{
	@InjectMocks
	private PayanyoneAccountService payanyoneAccountService=new PayanyoneAccountServiceImpl();
	@Mock
	private WebServiceProvider provider;
	@Mock
	private PayeeRepository payeeRepository;
	@Mock
	private Payee payee;
	
	final static String SUCCESS = "SUCCESS";
	final static String FAIL = "FAIL";
	
	
	@Test
	public void testLoadCheck()
	{
		String cashAccountId="123";
		String Pname="PAY_ANYONE";
		final PayAnyonePayee payanyone=new PayAnyonePayee();
		payanyone.setCashAccountId("123");
		
		
		Mockito.doAnswer(new Answer <List<PayAnyonePayee>>() {
			@Override
			public List<PayAnyonePayee> answer(InvocationOnMock invocation) throws Throwable {
				List<PayAnyonePayee> payanyoneList = new ArrayList<PayAnyonePayee>();
				payanyoneList.add(payanyone);
				return payanyoneList;
			}
			
		}).when(payeeRepository).loadAllPayanyone(anyString());
		
		List<PayAnyonePayee> result = payanyoneAccountService.load(cashAccountId);
		assertThat(cashAccountId,containsString(result.get(0).getCashAccountId()) );
		assertThat(Pname,containsString(result.get(0).getPayeeType().name()) );
	}
	
	
	@Test
	public void testAddCheckSUCCESS()
	{
		final PayAnyonePayee payanyone=new PayAnyonePayee();
		Bsb bsb =new Bsb();
		bsb.setBsbCode("bsb");
		payanyone.setBsb(bsb);
		payanyone.setAccountNumber("accountNumber");
		Mockito.when(payeeRepository.findPayanyone(anyString(), anyString())).thenReturn(null);
		String result = payanyoneAccountService.add(payanyone);
		assertThat(result, Is.is(SUCCESS));
	}
	
	
	@Test
	public void testAddCheckFAIL()
	{
		final PayAnyonePayee payanyone=new PayAnyonePayee();
		Bsb bsb =new Bsb();
		bsb.setBsbCode("bsb");
		payanyone.setBsb(bsb);
		payanyone.setAccountNumber("accountNumber");
		
		Mockito.doAnswer(new Answer<Payee>() {
			@Override
			public Payee answer(InvocationOnMock invocation) throws Throwable {
				return payanyone;
			}
			
		}).when(payeeRepository).findPayanyone(isA(Bsb.class), anyString());
		
		String result = payanyoneAccountService.add(payanyone);
		assertThat(result, Is.is(FAIL));
	}
	
	@Test
	public void testDeleteCheckSUCCESS()
	{
		final PayAnyonePayee payanyone=new PayAnyonePayee();
		payanyone.setCashAccountId("1234");
		payanyone.setAccountNumber("accountNumber");
		
		Mockito.doAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				return 1;
			}
			
		}).when(payeeRepository).deletePayAnyone(isA(PayAnyonePayee.class));
		
		String result = payanyoneAccountService.delete(payanyone);
		assertThat(result, Is.is(SUCCESS));
	}
	
	
	@Test
	public void testDeleteCheckFAIL()
	{
		final PayAnyonePayee payanyone=new PayAnyonePayee();
		payanyone.setCashAccountId("1234");
		payanyone.setAccountNumber("accountNumber");
		
		Mockito.doAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				return 0;
			}
			
		}).when(payeeRepository).deletePayAnyone(isA(PayAnyonePayee.class));
		
		String result = payanyoneAccountService.delete(payanyone);
		assertThat(result, Is.is(FAIL));
	}
	
	@Test
	public void testUpdateCheckSUCCESS()
	{
		final PayAnyonePayee payanyone=new PayAnyonePayee();
		payanyone.setAccountNumber("123");
		payanyone.setNickname("Test");
		
		Mockito.doAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				return 1;
			}
			
		}).when(payeeRepository).updatePayanyone(isA(PayAnyonePayee.class));
		
		String result = payanyoneAccountService.update(payanyone);
		assertThat(result, Is.is(SUCCESS));
	}
	@Test
	public void testUpdateCheckFAIL()
	{
		final PayAnyonePayee payanyone=new PayAnyonePayee();
		payanyone.setAccountNumber("123");
		payanyone.setNickname("Test");
		
		Mockito.doAnswer(new Answer<Integer>() {
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				return 0;
			}
			
		}).when(payeeRepository).updatePayanyone(isA(PayAnyonePayee.class));
		
		String result = payanyoneAccountService.update(payanyone);
		assertThat(result, Is.is(FAIL));
	}
}
