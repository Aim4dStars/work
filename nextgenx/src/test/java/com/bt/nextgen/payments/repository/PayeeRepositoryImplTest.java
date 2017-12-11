package com.bt.nextgen.payments.repository;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.isA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.payments.domain.PayeeType;


@RunWith(MockitoJUnitRunner.class)
public class PayeeRepositoryImplTest {
	
	@InjectMocks
	private PayeeRepository payeeRepository=new PayeeRepositoryImpl();
	
	@Mock
	private EntityManager entityManager;


	@Mock
	private CriteriaBuilder criteriaBuilder;
	
	@Mock
	private CriteriaQuery<Payee> criteriaQuery;
	
	@Mock
	private CriteriaQuery<Payee> select;
	
	@Mock
    Root<BpayPayee> bpayeeRoot;

	@Mock
    Root<PayAnyonePayee> payAnyonePayeeRoot;
	@Mock
    Root<LinkedAccount> linkedAccountRoot;
	@Mock
	Join<Object, Object> biller;
	
	@Mock
	TypedQuery<Payee> typedQuery;
	
	
	
	@Test
	public void testLoad() throws IOException
	{
		Payee payee=Mockito.mock(Payee.class);
		Payee payee1=new BpayPayee();
		Mockito.when(entityManager.find(isA(Class.class),anyObject())).thenReturn(payee);
		payee=payeeRepository.load(234l);
		assertThat(payee,IsNull.notNullValue());
		
	}
	
	@Test
	public void testLoadAll() throws IOException
	{
		
		 Query query = Mockito.mock(Query.class);
		 Payee payee=Mockito.mock(Payee.class);
		 payee.setCashAccountId("1234");
		 
		 List<Payee> payeeList=new ArrayList<Payee>();
		 payeeList.add(payee);
		
		 Mockito.when(entityManager.createQuery(anyString())).thenReturn(query);
		 Mockito.when(query.getResultList()).thenReturn(payeeList);
		 payeeList=payeeRepository.loadAll("1234");
		 assertThat(payeeList.size(), Is.is(1));
	}
	
	@Test
	public void testFindOfBayPayee() throws IOException
	{
				
		Bsb bsb =new Bsb();
		bsb.setBsbCode("bsb");
		
		BpayBiller bpayBiller=new BpayBiller();
		bpayBiller.setBillerCode("345");
		
		BpayPayee bpayPayee=new BpayPayee();
		bpayPayee.setCashAccountId("123");
		bpayPayee.setBiller(bpayBiller);
		bpayPayee.setCustomerReference("Reference");
		
		List<Payee> pay=new ArrayList<Payee>();
		
		Payee p=new BpayPayee();
		p.setCashAccountId("12");
		p.setNickname("Taylor");
		pay.add(p);
		
			
		Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		
		Mockito.when(criteriaBuilder.createQuery(Payee.class)).thenReturn(criteriaQuery);
		Mockito.when(criteriaQuery.from(BpayPayee.class)).thenReturn(bpayeeRoot);
				
		bpayeeRoot=criteriaQuery.from(BpayPayee.class);
		
		Mockito.when(bpayeeRoot.join(anyString())).thenReturn(biller);
		
		select=criteriaBuilder.createQuery(Payee.class);
		
		Mockito.when(criteriaQuery.select(bpayeeRoot)).thenReturn(select);
		Mockito.when(entityManager.createQuery(select)).thenReturn(typedQuery);
		
		Mockito.when(typedQuery.getResultList()).thenReturn(pay);
		
		Payee payee =payeeRepository.find(bpayPayee);
		assertThat(payee.getNickname(),containsString("Taylor"));
		assertThat(payee.getCashAccountId(),containsString("12"));
		
	}
	
	
	@Test
	public void testFindOfBayPayeeNull() throws IOException
	{
				
		Bsb bsb =new Bsb();
		bsb.setBsbCode("bsb");
		
		BpayBiller bpayBiller=new BpayBiller();
		bpayBiller.setBillerCode("345");
		
		BpayPayee bpayPayee=new BpayPayee();
		bpayPayee.setCashAccountId("123");
		bpayPayee.setBiller(bpayBiller);
		bpayPayee.setCustomerReference("Reference");
			
		Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		
		Mockito.when(criteriaBuilder.createQuery(Payee.class)).thenReturn(criteriaQuery);
		Mockito.when(criteriaQuery.from(BpayPayee.class)).thenReturn(bpayeeRoot);
				
		bpayeeRoot=criteriaQuery.from(BpayPayee.class);
		
		Mockito.when(bpayeeRoot.join(anyString())).thenReturn(biller);
		
		select=criteriaBuilder.createQuery(Payee.class);
		
		Mockito.when(criteriaQuery.select(bpayeeRoot)).thenReturn(select);
		Mockito.when(entityManager.createQuery(select)).thenReturn(typedQuery);
		
		Payee payee =payeeRepository.find(bpayPayee);
		assertThat(payee,IsNull.nullValue());
	}
	
	@Test
	public void testFindOfPayAnyonePayee() throws IOException
	{
				
		Bsb bsb =new Bsb();
		bsb.setBsbCode("bsb");
		
		BpayBiller bpayBiller=new BpayBiller();
		bpayBiller.setBillerCode("345");
		
		PayAnyonePayee payAnyonePayee=new PayAnyonePayee();
		payAnyonePayee.setAccountNumber("456");
		payAnyonePayee.setBsb(bsb);
		payAnyonePayee.setCashAccountId("345");
		
		List<Payee> pay=new ArrayList<Payee>();
		
		Payee p=new BpayPayee();
		p.setCashAccountId("1245");
		p.setNickname("Martin");
		pay.add(p);
		
			
		Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		
		Mockito.when(criteriaBuilder.createQuery(Payee.class)).thenReturn(criteriaQuery);
		Mockito.when(criteriaQuery.from(PayAnyonePayee.class)).thenReturn(payAnyonePayeeRoot);
				
		payAnyonePayeeRoot=criteriaQuery.from(PayAnyonePayee.class);
		
		Mockito.when(payAnyonePayeeRoot.join(anyString())).thenReturn(biller);
		
		select=criteriaBuilder.createQuery(Payee.class);
		
		Mockito.when(criteriaQuery.select(payAnyonePayeeRoot)).thenReturn(select);
		Mockito.when(entityManager.createQuery(select)).thenReturn(typedQuery);
		
		Mockito.when(typedQuery.getResultList()).thenReturn(pay);
		
		Payee payee =payeeRepository.find(payAnyonePayee);
		assertThat(payee.getNickname(),containsString("Martin"));
		assertThat(payee.getCashAccountId(),containsString("1245"));
		
	}
	
	
	
	
	@Test
	public void testFindOfPayAnyonePayeeNull() throws IOException
	{
				
		Bsb bsb =new Bsb();
		bsb.setBsbCode("bsb");
		
		BpayBiller bpayBiller=new BpayBiller();
		bpayBiller.setBillerCode("345");
		
		
		PayAnyonePayee payAnyonePayee=new PayAnyonePayee();
		payAnyonePayee.setAccountNumber("456");
		payAnyonePayee.setBsb(bsb);
		payAnyonePayee.setCashAccountId("345");
			
		Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		
		Mockito.when(criteriaBuilder.createQuery(Payee.class)).thenReturn(criteriaQuery);
		Mockito.when(criteriaQuery.from(PayAnyonePayee.class)).thenReturn(payAnyonePayeeRoot);
				
		payAnyonePayeeRoot=criteriaQuery.from(PayAnyonePayee.class);
		
		Mockito.when(payAnyonePayeeRoot.join(anyString())).thenReturn(biller);
		
		select=criteriaBuilder.createQuery(Payee.class);
		
		Mockito.when(criteriaQuery.select(payAnyonePayeeRoot)).thenReturn(select);
		Mockito.when(entityManager.createQuery(select)).thenReturn(typedQuery);
		
		Payee payee =payeeRepository.find(payAnyonePayee);
		assertThat(payee,IsNull.nullValue());
		
	}
	
	
	
	@Test
	public void testElseLinkedAccount() throws IOException
	{
				
		Bsb bsb =new Bsb();
		bsb.setBsbCode("bsb");
		
		BpayBiller bpayBiller=new BpayBiller();
		bpayBiller.setBillerCode("345");
		
		LinkedAccount linkedAccount=new LinkedAccount();
		linkedAccount.setAccountNumber("456");
		linkedAccount.setBsb(bsb);
		linkedAccount.setCashAccountId("345");
		
		List<Payee> pay=new ArrayList<Payee>();
		
		Payee p=new BpayPayee();
		p.setCashAccountId("7855");
		p.setId(43423);
		pay.add(p);
		
			
		Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		
		Mockito.when(criteriaBuilder.createQuery(Payee.class)).thenReturn(criteriaQuery);
		Mockito.when(criteriaQuery.from(LinkedAccount.class)).thenReturn(linkedAccountRoot);
				
		linkedAccountRoot=criteriaQuery.from(LinkedAccount.class);
		
		Mockito.when(linkedAccountRoot.join(anyString())).thenReturn(biller);
		
		select=criteriaBuilder.createQuery(Payee.class);
		
		Mockito.when(criteriaQuery.select(linkedAccountRoot)).thenReturn(select);
		Mockito.when(entityManager.createQuery(select)).thenReturn(typedQuery);
		
		Mockito.when(typedQuery.getResultList()).thenReturn(pay);
		
		Payee payee =payeeRepository.find(linkedAccount);
		assertThat(payee.getId(),Is.is(43423l));
		assertThat(payee.getCashAccountId(),containsString("7855"));
	
	}
	
	@Test
	public void testElseLinkedAccountNull() throws IOException
	{
				
		Bsb bsb =new Bsb();
		bsb.setBsbCode("bsb");
		
		BpayBiller bpayBiller=new BpayBiller();
		bpayBiller.setBillerCode("345");
		
		LinkedAccount linkedAccount=new LinkedAccount();
		linkedAccount.setAccountNumber("456");
		linkedAccount.setBsb(bsb);
		linkedAccount.setCashAccountId("345");
		
		
			
		Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		
		Mockito.when(criteriaBuilder.createQuery(Payee.class)).thenReturn(criteriaQuery);
		Mockito.when(criteriaQuery.from(LinkedAccount.class)).thenReturn(linkedAccountRoot);
				
		linkedAccountRoot=criteriaQuery.from(LinkedAccount.class);
		
		Mockito.when(linkedAccountRoot.join(anyString())).thenReturn(biller);
		
		select=criteriaBuilder.createQuery(Payee.class);
		
		Mockito.when(criteriaQuery.select(linkedAccountRoot)).thenReturn(select);
		Mockito.when(entityManager.createQuery(select)).thenReturn(typedQuery);
				
		Payee payee =payeeRepository.find(linkedAccount);
		
		assertThat(payee,IsNull.nullValue());
	
	}
	
	@Test
	public void testLoadAllPayanyone() throws IOException
	{
		
		 Query query = Mockito.mock(Query.class);
		 PayAnyonePayee payAnyonePayee=Mockito.mock(PayAnyonePayee.class);
		 payAnyonePayee.setCashAccountId("1234");
		 payAnyonePayee.setPayeeType(PayeeType.PAY_ANYONE);
		 
		 List<PayAnyonePayee> payeeList=new ArrayList<PayAnyonePayee>();
		 payeeList.add(payAnyonePayee);
		
		 Mockito.when(entityManager.createQuery(anyString())).thenReturn(query);
		 Mockito.when(query.getResultList()).thenReturn(payeeList);
			
		 
		 payeeList=payeeRepository.loadAllPayanyone("1234");
		 assertThat(payeeList.size(), Is.is(1));
		
	}
	@Test
	public void testFindPayanyone() throws IOException
	{
		
		 
		 	PayAnyonePayee payAnyonePayeeObj=new PayAnyonePayee();
		 
		 	Bsb bsb =new Bsb();
			bsb.setBsbCode("bsb");
			
		
			
			PayAnyonePayee payAnyonePayee=new PayAnyonePayee();
			payAnyonePayee.setAccountNumber("456");
			payAnyonePayee.setBsb(bsb);
			payAnyonePayee.setCashAccountId("345");
		
			
		
			Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
			Mockito.when(criteriaBuilder.createQuery(Payee.class)).thenReturn(criteriaQuery);
			Mockito.when(criteriaQuery.from(PayAnyonePayee.class)).thenReturn(payAnyonePayeeRoot);
			
			select=criteriaBuilder.createQuery(Payee.class);
			
			Mockito.when(criteriaQuery.select(payAnyonePayeeRoot)).thenReturn(select);
			Mockito.when(entityManager.createQuery(select)).thenReturn(typedQuery);
			Mockito.when(typedQuery.getSingleResult()).thenReturn(payAnyonePayee);
			
			
		 
		 payAnyonePayeeObj=payeeRepository.findPayanyone(bsb, "1234");
		 assertThat(payAnyonePayeeObj.getAccountNumber(),containsString("456"));
		 assertThat(payAnyonePayeeObj.getCashAccountId(),containsString("345"));
		
	}
	
	@Test
	public void testDeletePayAnyone() throws IOException
	{
		PayAnyonePayee payAnyonePayee=new PayAnyonePayee();
		payAnyonePayee.setAccountNumber("456");
		payAnyonePayee.setCashAccountId("345");
		
		Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		Mockito.when(criteriaBuilder.createQuery(Payee.class)).thenReturn(criteriaQuery);
		Mockito.when(criteriaQuery.from(PayAnyonePayee.class)).thenReturn(payAnyonePayeeRoot);
		select=criteriaBuilder.createQuery(Payee.class);
		
		Mockito.when(criteriaQuery.select(payAnyonePayeeRoot)).thenReturn(select);
		Mockito.when(entityManager.createQuery(select)).thenReturn(typedQuery);
		Mockito.when(typedQuery.getSingleResult()).thenReturn(payAnyonePayee);
	
		
		Mockito.when(payeeRepository.findPayanyone("456","566")).thenReturn(payAnyonePayee);
		
		int count=payeeRepository.deletePayAnyone(payAnyonePayee);
		assertEquals(true, (count != 0));
		
	}
	@Test
	public void testUpdatePayanyone() throws IOException
	{
		
		PayAnyonePayee payAnyonePayee=new PayAnyonePayee();
		payAnyonePayee.setAccountNumber("456");
		payAnyonePayee.setCashAccountId("345");
		
		Mockito.when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
		Mockito.when(criteriaBuilder.createQuery(Payee.class)).thenReturn(criteriaQuery);
		Mockito.when(criteriaQuery.from(PayAnyonePayee.class)).thenReturn(payAnyonePayeeRoot);
		select=criteriaBuilder.createQuery(Payee.class);
		
		Mockito.when(criteriaQuery.select(payAnyonePayeeRoot)).thenReturn(select);
		Mockito.when(entityManager.createQuery(select)).thenReturn(typedQuery);
		Mockito.when(typedQuery.getSingleResult()).thenReturn(payAnyonePayee);
	
		
		Mockito.when(payeeRepository.findPayanyone("456","566")).thenReturn(payAnyonePayee);
		int updateCount=payeeRepository.updatePayanyone(payAnyonePayee);
		assertEquals(true, (updateCount != 0));
	
	}
	
	@Test
	public void testUpdate() throws IOException
	{
		Payee payee=Mockito.mock(Payee.class);
		Long payeeId=new Long(34);
		Object someObject=new Object();
		Mockito.when(entityManager.find(isA(Class.class),anyObject())).thenReturn(payee);
		payee=payeeRepository.update(456l,"Taylor");
		assertThat(payee,IsNull.notNullValue());
	
	}
	
	@Test
	public void testLoadAllLinkedAccount() throws IOException
	{
		
		 Query query = Mockito.mock(Query.class);
		 List<LinkedAccount> linkedAccountList=new ArrayList<LinkedAccount>();
		 
		 LinkedAccount linkedAccount=new LinkedAccount();
		 linkedAccount.setCashAccountId("4556");
		 linkedAccount.setPayeeType(PayeeType.PRIMARY_LINKED);
		 linkedAccount.setPayeeType(PayeeType.SECONDARY_LINKED); 
		 linkedAccountList.add(linkedAccount);
		 Mockito.when(entityManager.createQuery(anyString())).thenReturn(query);
		 Mockito.when(query.getResultList()).thenReturn(linkedAccountList);
		 
		 List<LinkedAccount> list=payeeRepository.loadAllLinkedAccount("1234");
		 assertThat(list.size(), Is.is(1));
	}
	
	@Test
	public void testUpdateLinkedAccountType() throws IOException
	{
		 Query query = Mockito.mock(Query.class);
		 Payee payee=Mockito.mock(Payee.class);
		 Mockito.when(entityManager.createQuery(anyString())).thenReturn(query);
		 Mockito.when(query.executeUpdate()).thenReturn(1);
		 Mockito.when(entityManager.find(isA(Class.class),anyObject())).thenReturn(payee);
		 payee=payeeRepository.updateLinkedAccountType("23", "66");
		 assertThat(payee,IsNull.notNullValue());
	}
	
	
}

