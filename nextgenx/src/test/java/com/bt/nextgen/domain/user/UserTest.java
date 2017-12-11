package com.bt.nextgen.domain.user;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.core.repository.User;

public class UserTest
{
	User user;

	@Before
	public void setUp()
	{
		user = new User("forlinr");
	}

	@After
	public void tearDown()
	{

	}

	@Test
	public void testFormattedUsername() throws Exception
	{
		assertEquals(user.getFormattedUsername(), "Forlinr");
	}
}
