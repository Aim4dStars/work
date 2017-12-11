package com.bt.nextgen.core.repository;

public interface UserRepository
{
	public User loadUser(String username);
	
	public User update(User user);

	public User newUser(String username);
}
