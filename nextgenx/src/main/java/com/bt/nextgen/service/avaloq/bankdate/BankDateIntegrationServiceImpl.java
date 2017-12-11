package com.bt.nextgen.service.avaloq.bankdate;


import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.bankdate.BankDateService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankDateIntegrationServiceImpl implements BankDateIntegrationService
{
	@Autowired
	private GenericCache cache;

	
	@Autowired
	private BankDateValueGetterImpl bankDateValueGetterImpl;
	
	@Autowired
	private AvaloqExecute avaloqExecute;
	
	private AvaloqReportRequest reportRequest;

	
	private DateTime bankdate;
	private DateTime bankDateTime;

	/**
	 * This method will fetch the Bank Date through Avaloq Service <b>BTFG$UI_BASE.ALL#SYSTEM_DET</b> 
	 * Value retrieved from the avaloq if entry not present in cache else fetch value from cache
	 * 
	 * @param serviceErrors- Errors
	 * @return Date - The DateTime object
	 */
	@Override
    public DateTime getBankDate(ServiceErrors serviceErrors)
	{
		bankdate = (DateTime)cache.get(CacheType.BANK_DATE, Constants.BANKDATE, bankDateValueGetterImpl);
		
		return bankdate;
	}


	/**
	 * This method will fetch the current TimeStamp through Avaloq Service <b>BTFG$UI_BASE.ALL#SYSTEM_DET</b> 
	 *  
	 * @param serviceErrors- Errors
	 * @return Date - The DateTime object
	 */
	@Override
	public DateTime getTime(ServiceErrors serviceErrors)
	{
		reportRequest = new AvaloqReportRequest(Template.BANK_DATE.getName()).asApplicationUser();
		
		BankDateService response = avaloqExecute.executeReportRequestToDomain(reportRequest,
		BankDateServiceImpl.class,
		serviceErrors);
	
		bankDateTime = ((BankDateServiceImpl)response).getCurrentTime();
		
		return bankDateTime;
	}

}
