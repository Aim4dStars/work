package com.bt.nextgen.core.cache;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.PersonRequest;
import com.bt.nextgen.service.avaloq.PortfolioRequest;

/**
 * from Spring 4.0.
 */
public class SimpleKeyGenerator implements KeyGenerator
{
	private static final Logger logger = LoggerFactory.getLogger(SimpleKeyGenerator.class);
	
	@Autowired
	private UserProfileService userProfileService;
	
	@SuppressWarnings("serial")
	public static final class SimpleKey implements Serializable
	{

		public static final SimpleKey EMPTY = new SimpleKey();

		private final Object[] params;

		/**
		 * Create a new {@link SimpleKey} instance.
		 * 
		 * @param elements
		 *            the elements of the key
		 */
		public SimpleKey(Object... elements)
		{
			Assert.notNull(elements, "Elements must not be null");
			this.params = new Object[elements.length];
			System.arraycopy(elements, 0, this.params, 0, elements.length);
		}

		@Override
		public boolean equals(Object obj)
		{
			return (this == obj || (obj instanceof SimpleKey && Arrays
					.deepEquals(this.params, ((SimpleKey) obj).params)));
		}

		@Override
		public int hashCode()
		{
			return Arrays.deepHashCode(this.params);
		}

		@Override
		public String toString()
		{
			return "SimpleKey ["
					+ StringUtils.arrayToCommaDelimitedString(this.params)
					+ "]";
		}

	}

	@Override
	public Object generate(Object target, Method method, Object... params)
	{
		String key = userProfileService.getGcmId();
		for(int i=0; i<params.length; i++)
		{
			if(params[i] instanceof ServiceErrorsImpl)
			{
				params = ArrayUtils.remove(params, i);
			}
		}
		if (params.length == 0 || params[0] instanceof String)
		{
			logger.info("SimpleKeyGenerator.generate(): Key having gcm is : {} ", key);
			return key;
		}
		if(params[0] instanceof PortfolioRequest)
		{
			PortfolioRequest portfolioRequest = (PortfolioRequest)params[0];
			key = key + portfolioRequest.getPortfolioId();
			logger.info("SimpleKeyGenerator.generate(): Key having gcm & portfolio is : {} ", key);
			return key;
		}
		else if(params[0] instanceof PersonRequest)
		{
			PersonRequest personRequest = (PersonRequest)params[0];
			if(org.apache.commons.lang.StringUtils.isNotEmpty(personRequest.getPersonId()))
			{
				key = key + personRequest.getPersonId();
			}
			else if(personRequest.getClientIdList() != null && personRequest.getClientIdList().size()>0)
			{
				key = key + personRequest.getClientIdList().get(0);
			}
			logger.info("SimpleKeyGenerator.generate(): Key having gcm & clientId is : {} ", key);
			return key;
		}
		
		if (params.length == 1)
		{
			Object param = params[0];
			if (param != null && !param.getClass().isArray())
			{
				return param;
			}
		}
		return new SimpleKey(params);
	}

}
