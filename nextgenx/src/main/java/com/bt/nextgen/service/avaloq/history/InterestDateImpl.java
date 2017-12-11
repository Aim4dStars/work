package com.bt.nextgen.service.avaloq.history;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.history.InterestDate;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;

@ServiceBean(xpath = "base_contri|marg_contri|spec_contri", type = ServiceBeanType.CONCRETE)
public class InterestDateImpl implements InterestDate {
	
	@ServiceElement(xpath = "eff_date/val", converter = DateTimeTypeConverter.class)
	private DateTime effectiveDate;
	@ServiceElement(xpath = "intr/val")
	private BigDecimal interestRate;
	
	public DateTime getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(DateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public BigDecimal getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}
	//@Override
	public int compareTo(InterestDate o) {
		return getEffectiveDate().compareTo(o.getEffectiveDate());
	}

}
