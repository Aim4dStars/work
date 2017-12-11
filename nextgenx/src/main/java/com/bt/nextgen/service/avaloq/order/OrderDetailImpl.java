package com.bt.nextgen.service.avaloq.order;

import java.math.BigDecimal;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.order.OrderDetail;

@ServiceBean(xpath = "detail")
public class OrderDetailImpl implements OrderDetail
{
	@ServiceElement(xpath = "key/val")
	private String key;

	@ServiceElement(xpath = "val/val")
	private BigDecimal value;

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public BigDecimal getValue()
	{
		return value;
	}

	public void setValue(BigDecimal value)
	{
		this.value = value;
	}
}
