package com.bt.nextgen.service.avaloq.fundpaymentnotice;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.fundpaymentnotice.DistributionDetails;

@ServiceBean(xpath = "//distr")
public class DistributionDetailsImpl implements DistributionDetails
{

	@ServiceElement(xpath = "distr_compo_id/val", converter = DistributionComponentConverter.class)
	String distributionComponent;

	@ServiceElement(xpath = "amount/val")
	String distributionComponentAmount;

	@Override
	public String getDistributionComponent()
	{

		return distributionComponent;
	}

	@Override
	public String getDistributionComponentAmount()
	{
		return distributionComponentAmount;
	}

}
