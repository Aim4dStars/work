package com.bt.nextgen.service.avaloq.bankdate;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.core.cache.ValueGetter;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.integration.bankdate.BankDateService;

@Service
public class BankDateValueGetterImpl implements ValueGetter
{

	private static final Logger logger = LoggerFactory.getLogger(BankDateIntegrationServiceImpl.class);
	
	@Autowired
	private AvaloqExecute avaloqExecute;
	
	private DateTime bankdate;
	private AvaloqReportRequest reportRequest;
	
	@Override
	public Object getValue(Object key)
	{
		logger.info("Inside getValue method");
		
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
        reportRequest = new AvaloqReportRequest(Template.BANK_DATE.getName()).asApplicationUser();

		BankDateService response = avaloqExecute.executeReportRequestToDomain(reportRequest,
			BankDateServiceImpl.class,
			serviceErrors);
		
		bankdate = ((BankDateServiceImpl)response).getBankDate();
		
		return bankdate;
	}

}
