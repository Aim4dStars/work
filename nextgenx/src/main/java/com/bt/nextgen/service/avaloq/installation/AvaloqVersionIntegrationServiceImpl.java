package com.bt.nextgen.service.avaloq.installation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.bt.nextgen.service.AvaloqGatewayWebService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.installation.impl.AvaloqInstallationInformationImpl;
import com.bt.nextgen.service.avaloq.installation.request.AvaloqSystemInformationTemplate;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;

@Component
public class AvaloqVersionIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements AvaloqVersionIntegrationService
{

	private final static String SINGLETON_KEY = "singleton";

	private final Logger logger = LoggerFactory.getLogger(AvaloqVersionIntegrationServiceImpl.class);

	@Autowired
	AvaloqGatewayWebService avaloqGateway;

	@Cacheable(key = "#root.target.getSingletonKey()", value = "com.bt.nextgen.service.avaloq.installation.AvaloqVersionIntegrationService.avaloqInstallationInformation")
	@Override public AvaloqInstallationInformation getAvaloqInstallInformation(ServiceErrors errors)
	{
		AvaloqRequest request = new AvaloqReportRequestImpl(AvaloqSystemInformationTemplate.AVALOQ_INSTALLATION_DETAILS);
		request = request.asApplicationUser();

		AvaloqInstallationInformation installationInformation = getResponse(errors,AvaloqInstallationInformationImpl.class,request);

		return installationInformation;
	}


	@CacheEvict(key = "#root.target.getSingletonKey()", value = "com.bt.nextgen.service.avaloq.installation.AvaloqVersionIntegrationService.avaloqInstallationInformation")
	@Override public void refreshAvaloqVersion()
	{
		logger.info("Cache com.bt.nextgen.service.avaloq.installation.AvaloqVersionIntegrationService.avaloqInstallationInformation has been evicted, the next request will load directly from ABS");
		return;
	}

	public String getSingletonKey()
	{
		return SINGLETON_KEY;
	}



}
