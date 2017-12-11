package com.bt.nextgen.api.fees.model;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class RCTInvoicesDto extends BaseDto implements KeyedDto <DateRangeKey> {

	private DateRangeKey key;
	
	public RCTInvoicesDto(DateTime startDate, DateTime endDate) {
		this.key = new DateRangeKey(startDate, endDate);
	}
	
    public String getName() {
        return "Recipient Created Tax Invoice";
    }
    
	@Override
	public DateRangeKey getKey() {
		return this.key;
	}    
}
