package com.bt.nextgen.core.jms;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Tests {@link JmsDataHandlerCacheImpl}.
 * 
 * @author Albert Hirawan
 */
public class JmsDataHandlerCacheImplTest {
	private JmsObjectHandler jmsObjectHandler;
	
	
	@Before
	public void init() {
		jmsObjectHandler = Mockito.mock(JmsObjectHandler.class);
	}
	
	
	/**
	 * Register handler should not generate exception.
	 */
	@Test
	public void registerHandler() {
		final JmsDataHandlerCacheImpl jmsDataHandler = new JmsDataHandlerCacheImpl();

		jmsDataHandler.init();	// simulate spring post construct
		
		jmsDataHandler.registerHandler(null, null);
		jmsDataHandler.registerHandler(String.class, null);
		jmsDataHandler.registerHandler(null, jmsObjectHandler);
		jmsDataHandler.registerHandler(String.class, jmsObjectHandler);
	}

	
	@Test
	public void handleWithoutHandlers() {
		final JmsDataHandlerCacheImpl jmsDataHandler = new JmsDataHandlerCacheImpl();
		final Class<?> objectType = String.class;
		final String object = "test string";
		final String templateName = "template_name";
		final String requestId = "1234-abc-890";

		jmsDataHandler.init();	// simulate spring post construct
		
		// no registered handlers
		assertThat("no initialised handlers", jmsDataHandler.handle(object, objectType, templateName, requestId), equalTo(false));

		
		//////////////// badly initialised handler: null object type and/or null handler
		jmsDataHandler.registerHandler(null, null);
		assertThat("no initialised handlers: ", jmsDataHandler.handle(object, objectType,templateName, requestId), equalTo(false));

		jmsDataHandler.registerHandler(objectType, null);
		assertThat("no initialised handlers", jmsDataHandler.handle(object, objectType, templateName, requestId), equalTo(false));

		jmsDataHandler.registerHandler(null, jmsObjectHandler);
		assertThat("no initialised handlers", jmsDataHandler.handle(object, objectType, templateName, requestId), equalTo(false));
	}
	
	
	@Test
	public void handleReturningNotProcessed() {
		final JmsDataHandlerCacheImpl jmsDataHandler = new JmsDataHandlerCacheImpl();
		final Class<?> objectType = String.class;
		final String object = "test string";
		boolean handled = false;	// not processed
		String templateName = "";
		final String requestId = "1234-abc-890";

		jmsDataHandler.init();	// simulate spring post construct
		
		jmsDataHandler.registerHandler(objectType, jmsObjectHandler);
		when(jmsObjectHandler.handle(object, templateName, requestId)).thenReturn(handled);
		assertThat("valid handler returning " + handled, jmsDataHandler.handle(object, objectType, "", requestId), equalTo(handled));
		verify(jmsObjectHandler).handle(object, templateName, requestId);
		
	}
	
	
	@Test
	public void handleReturningProcessed() {
		final JmsDataHandlerCacheImpl jmsDataHandler = new JmsDataHandlerCacheImpl();
		final Class<?> objectType = Integer.class;
		final Integer object = 123;
		boolean handled = true;		// processed
		final String templateName = "template_name";
		boolean isPartial = false;
		final String requestId = "1234-abc-890";

		jmsDataHandler.init();	// simulate spring post construct

		jmsDataHandler.registerHandler(objectType, jmsObjectHandler);
		when(jmsObjectHandler.handle(object,templateName,requestId)).thenReturn(handled);
		assertThat("valid handler returning " + handled, jmsDataHandler.handle(object, objectType, templateName, requestId), equalTo(handled));
		verify(jmsObjectHandler).handle(object, templateName, requestId);
	}

	@Test
	//Test for assigning ExecutorService for each template request
	public void registerHandlerExecutorPerTemplate() {
		final JmsDataHandlerCacheImpl jmsDataHandler = new JmsDataHandlerCacheImpl();

		jmsDataHandler.init();

		jmsDataHandler.registerHandler(com.bt.nextgen.service.avaloq.code.StaticCodeHolder.class, jmsObjectHandler);
        jmsDataHandler.registerHandler(com.bt.nextgen.service.avaloq.asset.aal.BrokerProductAssetResponseImpl.class,
                jmsObjectHandler);

        ExecutorService executorService1 = jmsDataHandler
                .getExecutorService(com.bt.nextgen.service.avaloq.asset.aal.BrokerProductAssetResponseImpl.class);
        ExecutorService executorService2 = jmsDataHandler
                .getExecutorService(com.bt.nextgen.service.avaloq.asset.aal.BrokerProductAssetResponseImpl.class);
		ExecutorService executorService3 = jmsDataHandler.getExecutorService(com.bt.nextgen.service.avaloq.code.StaticCodeHolder.class);

		assertThat("Not returning same thread for same template ",executorService1, equalTo(executorService2));
		assertThat("Should return different threads ",executorService1, not(equalTo(executorService3)));
	}
}
