package com.bt.nextgen.repository.user;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserRepository;

public class UserRepositoryIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private UserRepository userRepository;

	@Test
	public void testUserRepositoryUpdateAndLoad() throws Exception
	{
        User user=new User("adviser");
		user.setFirstTimeLoggedIn(false);
        userRepository.update(user);

        user=userRepository.loadUser("adviser");

        assertEquals(user.isFirstTimeLoggedIn(),false);
		user.setFirstTimeLoggedIn(true);
        userRepository.update(user);

        user=userRepository.loadUser("adviser");

        assertEquals(user.isFirstTimeLoggedIn(),true);
	}
	
}
