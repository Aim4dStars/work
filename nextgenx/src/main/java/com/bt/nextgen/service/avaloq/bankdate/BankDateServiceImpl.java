package com.bt.nextgen.service.avaloq.bankdate;


import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.bankdate.BankDateService;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class BankDateServiceImpl extends AvaloqBaseResponseImpl implements BankDateService
{

	@ServiceElement(xpath = "//report_foot/bank_date/val", converter = DateTimeTypeConverter.class)
	private DateTime bankDate;
	
	@ServiceElement(xpath = "//metadata/current_of_time/val", converter = DateTimeTypeConverter.class)
	private DateTime currentTime;

	@Override
	public DateTime getBankDate()
	{
        return bankDate;
	}

	@Override
	public DateTime getCurrentTime()
	{
        return currentTime;
	}
	
	
	
}
