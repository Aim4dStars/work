package com.bt.nextgen.core.repository;


import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.avaloq.Template;

@TransactionConfiguration(defaultRollback = true)
public class RequestRegisterRepositoryImplTest extends BaseSecureIntegrationTest 
{
    @Autowired
    RequestRegisterRepositoryImpl requestRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testFindRequestEntry() throws Exception 
    {
    	RequestKey requestKey = new RequestKey(Template.STATIC_CODES.toString(), "STARTUP");
    	RequestRegister requestRegister = requestRepository.findRequestEntry(requestKey);
    	Assert.assertNull(requestRegister);
    }
   
    /**
     * Ignored because this test will hold the execution of thread for whole 10 minutes. Though useful for testing the code locally, so keeping it. 
     * @throws Exception
     */
    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testUpdateRequestEntry() throws Exception 
    {
    	RequestKey requestKey = new RequestKey(Template.STATIC_CODES.toString(), "STARTUP");
    	
    	RequestRegister entity = new RequestRegister();
		entity.setRequestKey(requestKey);
		entity.setServerName("");
		entity.setSentTime(new Date());
		requestRepository.updateRequestEntry(entity);
    	
    	RequestRegister requestRegister = requestRepository.findRequestEntry(requestKey);
    	Assert.assertNotNull(requestRegister);
    }
    
    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testCheckEntryForDoneStatus() throws Exception 
    {
    	RequestKey requestKey = new RequestKey(Template.STATIC_CODES.toString(), "STARTUP");
    	RequestRegister entity = new RequestRegister();
		entity.setRequestKey(requestKey);
		entity.setServerName("");
		entity.setSentTime(new Date());
		requestRepository.updateRequestEntry(entity);
		
    	RequestRegister requestRegister = requestRepository.findRequestEntry(requestKey);
    	Assert.assertNotNull(requestRegister);
    	requestRepository.removeEntry(requestRegister);
    	//RequestRegister requestRegister1 = requestRepository.findRequestEntry(requestKey);
    	//Assert.assertNull(requestRegister1);
    }
}
