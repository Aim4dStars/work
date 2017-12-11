package com.bt.nextgen.service.avaloq.gateway;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.when;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotificationImpl;
import com.bt.nextgen.core.repository.PartialInvalidationRequestKey;
import com.bt.nextgen.core.repository.PartialInvalidationRequestRegister;
import com.bt.nextgen.core.repository.PartialInvalidationRequestRegisterRepository;
import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import net.sf.saxon.type.Converter;

@Ignore
public class AvaloqPartialInvalidationRequestRegisterImplTest
{

	@InjectMocks
	AvaloqPartialInvalidationRequestRegisterImpl underTest;


	@Mock
	PartialInvalidationRequestRegisterRepository repo;

	InvalidationNotification invalidation;

	com.bt.nextgen.service.request.AvaloqRequest request;
	PartialInvalidationRequestKey key;
	PartialInvalidationRequestRegister registerReq;

	@Before public void setUp() throws Exception
	{
		when(repo.updateRequestEntry(any(PartialInvalidationRequestRegister.class))).thenThrow(new RuntimeException("I should not be called"));

		request = new com.bt.nextgen.service.request.AvaloqReportRequestImpl(
			new AvaloqTemplate()
			{
				@Override public String getTemplateName()
				{
					return "TEST";
				}

				@Override public List<AvaloqParameter> getValidParamters()
				{
					return null;
				}
			});

		final DateTime now = DateTime.now();
		final DateTime twoSecondsAgo = now.minusSeconds(2);


		invalidation = new InvalidationNotification(){

			@Override public String getTemplateName()
			{
				return "TEST";
			}

			@Override public String getScope()
			{
				return null;
			}

			@Override public String getParamName()
			{
				return null;
			}

			@Override public List<String> getParamValList()
			{
				return null;
			}

			@Override public DateTime getCreateDate()
			{
				return twoSecondsAgo;
			}

			@Override public String getParamValType()
			{
				return null;
			}

			@Override public String getCacheName()
			{
				return null;
			}
		};

		key = new PartialInvalidationRequestKey(invalidation.getTemplateName().toString(), EventType.CACHE_INVALIDATION.name(), "1066", invalidation.getCreateDate().toDate());

		registerReq = new PartialInvalidationRequestRegister();
		registerReq.setRequestKey(key);


	}


	@Test public void testCheckRegisterForPartialInvalidationRequest() throws Exception
	{


		//underTest.checkRegisterForPartialInvalidationRequest(request, invalidation);


	}

	@Test public void testUpdateRegisterWithCorrelationId() throws Exception
	{

	}

	@Test public void testCheckStatusForPartialInvalidationRequest() throws Exception
	{

	}

	@Test public void testCreateNewEntry() throws Exception
	{

	}



	/*@Test public void testAnalyseRequestAction() throws Exception
	{
		RequestStatus tmp = underTest.analyseRequestAction(registerReq, invalidation, key);
		assertThat(tmp, is(notNull()));
		assertThat(tmp,is(RequestStatus.DONE));


	}*/

	@Test public void testDoWait() throws Exception
	{

	}
}