package com.bt.nextgen.service.integration.userinformation;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.integration.staticrole.StaticRoleIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;

/**
 * This is a caching service for the User_Auth avaloq service.
 */

@Service
public class CacheManagedUserInformationService  extends AbstractAvaloqIntegrationService 
{
	private static final Logger logger = LoggerFactory.getLogger(CacheManagedUserInformationService.class);

    @Autowired
    private AvaloqReportService avaloqService;

	@Autowired
	private StaticRoleIntegrationService functionalRoleService;

	@Autowired
	private Validator validator;
	
	/**
	 * This method returns the complete user information which will further be categorised into various interfaces. 
	 * This is a cached service and the implementation of BTFG$UI_USER.USER_AUTH avaloq service  
	 * @param 
	 * @return UserInformation
	 */
	//@Cacheable(key = "#root.target.getAvaloqId()", value = "com.bt.nextgen.service.avaloq.userinformation.AvaloqUserInformationService.user")
	public UserInformationImpl getUserInformation(JobProfile profile, ServiceErrors serviceErrors)
	{
		try
		{
			//TODO : logger.info("Loading user information for profileId {}", profile.getJobProfileId());
            AvaloqRequest avaloqReportRequest= new AvaloqReportRequestImpl(UserInformationEnumTemplate.USER_INFORMATION);
           UserInformationImpl userInformation = avaloqService.executeReportRequestToDomain(avaloqReportRequest, UserInformationImpl.class, serviceErrors);

			return userInformation;
		}
		catch(Exception e){
			logger.error("Exception in loading UserInformation ", e);
			return new UserInformationImpl();
		}
	}
}
