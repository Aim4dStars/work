package com.bt.nextgen.api.transactionhistory.model;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class SmsfMembersDto extends BaseDto implements KeyedDto <AccountKey>
{
	private AccountKey key;
	private String personId;
	private String firstName;
	private String lastName;
	private String dateOfBirth;


	public SmsfMembersDto(AccountKey key, String personId, String firstName, String lastName)
	{
		this.key = key;
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public SmsfMembersDto()
	{

	}

	@Override
	public AccountKey getKey()
	{
		return key;

	}

	public void setKey(AccountKey key)
	{
		this.key = key;
	}

	public String getPersonId()
	{
		return personId;
	}

	public void setPersonId(String personId)
	{
		this.personId = personId;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}
}
