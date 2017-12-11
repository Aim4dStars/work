package com.bt.nextgen.payments.repository;

import java.util.Collection;

public interface BpayBillerCodeRepository
{
	/**
	 * Supports finding a biller by biller code
	 *
	 * @param partialBillerCode this can be a partial or full. When partial it is assumed to be a 'starts with' search
	 * @return
	 */
	Collection <BpayBiller> findByPartialBillerCode(final String partialBillerCode);

	BpayBiller load(String billerCode);

	Collection <BpayBiller> loadAllBillers();
}
