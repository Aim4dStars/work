package com.bt.nextgen.core.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository("userRepository")
public class UserRepositoryImpl implements UserRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional(value = "springJpaTransactionManager")
	public User update(User user)
	{
	  logger.info("start update User  {}",user.getUsername());
	  entityManager.merge(user);
	  entityManager.flush();
	  return user;
	}

	@Override public User newUser(String username)
	{
		return new User(username);
	}

	@Override
	public User loadUser(String username) {
		logger.info("start loadUser  {}",username);
		User user= entityManager.find(User.class, username);
		if (user != null)
		{
			logger.info("loaded user with username {} and id:{}",username,user.getId() );
		}
		else
		{
			logger.info("user not found {}", username);
		}
		return user;
	}
	
}
