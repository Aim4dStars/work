package com.bt.nextgen.service.avaloq.history;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.history.CashRateComponent;
import com.bt.nextgen.service.integration.history.InterestDate;

@ServiceBean(xpath = "base_contri|marg_contri|spec_contri", type = ServiceBeanType.CONCRETE)
public class CashRateComponentImpl implements CashRateComponent {

	@ServiceElement(xpath = "base_contri/contri_id/val|marg_contri/contri_id/val|spec_contri/contri_id/val")
	private String cashRateComponentId;
	@ServiceElement(xpath = "base_contri/contri_id/annot/displ_text|marg_contri/contri_id/annot/displ_text|spec_contri/contri_id/annot/displ_text")
	private String cashRateComponentName;
	@ServiceElementList(xpath = "base_contri|marg_contri|spec_contri", type = InterestDateImpl.class)
	private List<InterestDate> interestDates;
	
	public String getCashRateComponentId() {
		return cashRateComponentId;
	}
	public void setCashRateComponentId(String cashRateComponentId) {
		this.cashRateComponentId = cashRateComponentId;
	}
	public String getCashRateComponentName() {
		return cashRateComponentName;
	}
	public void setCashRateComponentName(String cashRateComponentName) {
		this.cashRateComponentName = cashRateComponentName;
	}
	public List<InterestDate> getInterestDates() {
		return interestDates;
	}
	public void setInterestDates(List<InterestDate> interestDates) {
		this.interestDates = interestDates;
	}
	public BigDecimal getSummatedRate()
	{
		BigDecimal toatlRate = new BigDecimal(0.0);
		for (InterestDate interestDate : getInterestDates())
		{
			toatlRate = toatlRate.add(interestDate.getInterestRate());
		}
		return toatlRate;
	}
}
