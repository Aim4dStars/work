package com.bt.nextgen.service.avaloq.wrapaccount;

import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

import static org.springframework.util.ObjectUtils.nullSafeEquals;
import static org.springframework.util.ObjectUtils.nullSafeHashCode;

public class WrapAccountIdentifierImpl implements WrapAccountIdentifier
{
	private String bpId;

	@Override
	public String getAccountIdentifier()
	{
		return bpId;
	}

	@Override
	public void setBpId(String bpId)
	{
		this.bpId = bpId;
	}

	@Override
	public int hashCode()
	{
		return nullSafeHashCode(getAccountIdentifier());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj == null)
		{
			return false;
		}
		else
		{
			return nullSafeEquals(getAccountIdentifier(), ((WrapAccountIdentifierImpl) obj).getAccountIdentifier());
		}
	}
}
