package com.bt.nextgen.api.allocation.model;

import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.core.api.model.KeyedDto;

/**
 * @deprecated use account.v2.mode.allocation
 */
@Deprecated
public interface AllocationDto extends KeyedDto <DatedAccountKey>
{
	public abstract DatedAccountKey getKey();
}
