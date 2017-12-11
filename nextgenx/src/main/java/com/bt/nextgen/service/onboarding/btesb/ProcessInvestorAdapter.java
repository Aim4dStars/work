package com.bt.nextgen.service.onboarding.btesb;

import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ProvisionOnlineAccessResponseMsgType;

public class ProcessInvestorAdapter extends ProvisionOnlineAccessResponseAdapter
{
	/**
	 * Constructor.
	 * @param jaxbResponse response object.
	 */
	public ProcessInvestorAdapter(ProvisionOnlineAccessResponseMsgType jaxbResponse)
	{
		super(jaxbResponse);
	}
}