package com.bt.nextgen.api.adviser.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class AdviserSearchDto extends BaseDto implements KeyedDto <AdviserSearchDtoKey>
{
    private String fullName;
	private String firstName;
	private String lastName;
	private String practiceName;
	private String city;
	private String state;
	private String adviserPositionId;

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
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

	public String getPracticeName()
	{
		return practiceName;
	}

	public void setPracticeName(String practiceName)
	{
		this.practiceName = practiceName;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public void setAdviserPositionId(String adviserPositionId)
	{
		this.adviserPositionId = adviserPositionId;
	}

	public String getAdviserPositionId()
	{
		return adviserPositionId;
	}

	@Override
	public AdviserSearchDtoKey getKey()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
