package com.bt.nextgen.service.avaloq.gateway;


import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.repository.RequestKey;
import com.bt.nextgen.core.repository.RequestRegister;
import com.bt.nextgen.service.avaloq.Template;

@TransactionConfiguration(defaultRollback = true)
public class AvaloqRequestRegisterIntegrationTest extends BaseSecureIntegrationTest
{
    @Autowired
    AvaloqRequestRegisterImpl requestRegister;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testCheckEntryForNoRequestEntry() throws Exception 
    {
    	AvaloqReportRequest request = new AvaloqReportRequest(Template.ADVISOR_PRODUCTS.getName());
        RequestKey key = new RequestKey(request.getTemplateName().toString(), request.getEventType().name());
    	RequestStatus requestStatus = requestRegister.checkRequestStatus(key, 0);
    	assertEquals(requestStatus, RequestStatus.GO);
    	
    	// Commented as it is useful to test in local
    	/*RequestKey key = new RequestKey(Template.ADVISOR_PRODUCTS.getName(), EventType.STARTUP.toString());
    	RequestRegister requestEntry = entityManager.find(RequestRegister.class, key);
    	System.out.println(requestEntry.getSentTime()+"::::::::"+requestEntry.getReceivedTime());
    	
    	RequestStatus requestStatus1 = requestRepository.checkEntry(request, 0);
    	assertEquals(requestStatus1, RequestStatus.GO);
    	RequestRegister requestEntry1 = entityManager.find(RequestRegister.class, key);
    	System.out.println(requestEntry1.getSentTime()+"::::::::"+requestEntry1.getReceivedTime());
    	requestEntry1.setReceivedTime(new Date());
    	entityManager.persist(requestEntry1);
    	RequestRegister requestEntry2 = entityManager.find(RequestRegister.class, key);
    	RequestStatus requestStatus2 = requestRepository.checkEntry(request, 0);
    	assertEquals(requestStatus2, RequestStatus.DONE);
    	System.out.println(requestEntry2.getSentTime()+"::::::::"+requestEntry2.getReceivedTime());*/
    }
   
    /**
     * Ignored because this test will hold the execution of thread for whole 10 minutes. Though useful for testing the code locally, so keeping it. 
     * @throws Exception
     */
    @Ignore
    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testCheckEntryForRequestEntryButNoRecievedTime() throws Exception 
    {
    	AvaloqReportRequest request = new AvaloqReportRequest(Template.ADVISOR_PRODUCTS.getName());
        RequestKey key = new RequestKey(request.getTemplateName().toString(), request.getEventType().name());
    	RequestStatus requestStatus = requestRegister.checkRequestStatus(key, 0);
    	assertEquals(requestStatus, RequestStatus.GO);
    	
    	//RequestKey key = new RequestKey(Template.ADVISOR_PRODUCTS.getName(), EventType.STARTUP.toString());
    	//RequestRegister requestEntry = entityManager.find(RequestRegister.class, key);
    	//System.out.println(requestEntry.getSentTime()+"::::::::"+requestEntry.getReceivedTime());
    	
    	RequestStatus requestStatus1 = requestRegister.checkRequestStatus(key, 0);
    	assertEquals(requestStatus1, RequestStatus.GO);
    }
    
    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void testCheckEntryForDoneStatus() throws Exception 
    {
    	AvaloqReportRequest request = new AvaloqReportRequest(Template.ADVISOR_PRODUCTS.getName());
        RequestKey key = new RequestKey(request.getTemplateName().toString(), request.getEventType().name());
    	RequestStatus requestStatus = requestRegister.checkRequestStatus(key, 0);
    	assertEquals(requestStatus, RequestStatus.GO);
    	
    	 key = new RequestKey(Template.ADVISOR_PRODUCTS.getName(), EventType.STARTUP.toString());
    	RequestRegister requestEntry = entityManager.find(RequestRegister.class, key);
    	requestEntry.setReceivedTime(new Date());
    	entityManager.persist(requestEntry);

    	RequestStatus requestStatus1 = requestRegister.checkRequestStatus(key, 0);
    	assertEquals(requestStatus1, RequestStatus.DONE);
    }
    

    
    @Test
    public void testFindWaitTime()
    {
    	RequestRegister requestEntry = new RequestRegister();
    	requestEntry.setSentTime(new Date(System.currentTimeMillis()-5*60*1000));
    	int waitTime = requestRegister.findWaitTime(requestEntry);
    	assertEquals(waitTime, 300000);
    }
    
    @Test
    public void testDoWait() throws ParseException
    {
    	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	Date date = formatter.parse("2015-02-03 11:00:06.466");
    	RequestRegister requestEntry = new RequestRegister();
    	requestEntry.setSentTime(date);
    	boolean waitTime = requestRegister.doWait(requestEntry);
    	assertEquals(waitTime, true);
    	requestEntry.setSentTime(new Date((System.currentTimeMillis()-60*9*1000)));
    	waitTime = requestRegister.doWait(requestEntry);
    	assertEquals(waitTime, false);
    }
    
    @Test
    public void testIsCleanupRequired()
    {
    	RequestRegister requestEntry = new RequestRegister();
    	requestEntry.setReceivedTime(new Date((System.currentTimeMillis()-60*60*1000)-1000));
    	boolean isCleanupRequired = requestRegister.isCleanupRequired(requestEntry);
    	assertEquals(isCleanupRequired, true);
    	requestEntry.setReceivedTime(new Date(System.currentTimeMillis()-5*60*1000));
    	boolean isCleanupRequired1 = requestRegister.isCleanupRequired(requestEntry);
    	assertEquals(isCleanupRequired1, false);
    }
}
