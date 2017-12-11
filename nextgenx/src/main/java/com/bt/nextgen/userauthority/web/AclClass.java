package com.bt.nextgen.userauthority.web;

public enum AclClass 
{
	MOVE_MONEY("1"),
	TERMDEPOSIT("1"),
	ADDRESSBOOK("3");
	
	long id;


	AclClass(String id)
	{
		this.id = Long.parseLong(id);
	}


	public long getId() {
		return id;
	}
}
