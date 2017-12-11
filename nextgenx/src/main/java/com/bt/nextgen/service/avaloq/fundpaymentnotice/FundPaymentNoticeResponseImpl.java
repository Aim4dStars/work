/**
 * 
 */
package com.bt.nextgen.service.avaloq.fundpaymentnotice;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNotice;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNoticeResponse;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class FundPaymentNoticeResponseImpl extends AvaloqBaseResponseImpl implements FundPaymentNoticeResponse
{

	@ServiceElementList(xpath = "//data/doc_list/doc", type = FundPaymentNoticeImpl.class)
	private List <FundPaymentNotice> fundPaymentNoticeList;

	@Override
	public List <FundPaymentNotice> getFundPaymentNotice()
	{
		return fundPaymentNoticeList;
	}

}
