package com.bt.nextgen.service.group.customer;

import org.springframework.xml.transform.StringResult;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Created by l063220 on 05/05/14.
 */

public interface CustomerTokenIntegrationService 
{
	String getCustomerSAMLToken(CustomerTokenRequest customerTokenRequest, ServiceErrors errors);
}
