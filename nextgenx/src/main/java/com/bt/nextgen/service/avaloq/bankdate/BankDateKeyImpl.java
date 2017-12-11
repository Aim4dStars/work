package com.bt.nextgen.service.avaloq.bankdate;

import com.bt.nextgen.core.cache.KeyGetter;
import com.btfin.panorama.core.security.avaloq.Constants;

public class BankDateKeyImpl implements KeyGetter
{

	@Override
	public Object getKey(Object obj)
	{
		return Constants.BANKDATE.toString();
	}

}
