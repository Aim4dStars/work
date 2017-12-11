package com.bt.nextgen.service.avaloq.broker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;

@Service
public class AnnotatedBrokerIntegrationServiceImpl
{

	@Autowired
	private AvaloqExecute avaloqExecute;


	public BrokerHolderAnnotationImpl loadBrokers()
	{
        BrokerHolderAnnotationImpl holder=null;
        try {
             holder = avaloqExecute.executeReportRequestToDomain(
                    new AvaloqReportRequest(Template.BROKER_HIERARCHY.getName()).asApplicationUser(),
                    PartialInvalidationBrokerHolderImpl.class, new FailFastErrorsImpl());
        }
        catch(Exception e)
        {

        }


        return holder;
	}


}
