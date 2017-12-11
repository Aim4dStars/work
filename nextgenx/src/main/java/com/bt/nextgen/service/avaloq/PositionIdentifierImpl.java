package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.integration.PositionIdentifier;

public class PositionIdentifierImpl implements PositionIdentifier
{
	private String positionId;

    public PositionIdentifierImpl(String positionId)
    {
        setPositionId(positionId);
    }

    public PositionIdentifierImpl()
    {

    }
    
	@Override
	public String getPositionId()
	{
		return positionId;
	}

	@Override
	public void setPositionId(String positionId)
	{
		this.positionId = positionId;

	}

}
