package com.bt.nextgen.service.avaloq.userinformation;

import static com.bt.nextgen.service.avaloq.AvaloqUtils.makeNotifyPasswordInformationUserRequest;

import java.util.ArrayList;
import java.util.List;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.group.customer.BankingCustomerIdentifier;
import com.bt.nextgen.service.integration.staticrole.StaticRoleIntegrationService;
import com.bt.nextgen.service.integration.userinformation.CacheManagedUserInformationService;
import com.bt.nextgen.service.integration.userinformation.ClientIdentifier;
import com.btfin.panorama.core.security.integration.userinformation.JobPermission;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userinformation.UserPasswordDetail;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.abs.trxservice.user.v1_0.UserReq;
import com.btfin.abs.trxservice.user.v1_0.UserRsp;

/**
 * This service is used for loading the initial user details which will be used for proceeding ahead in the application.
 */

@Service
@SuppressWarnings("squid:S1200")//(Single Responsibility Principle)
public class AvaloqUserInformationIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
	UserInformationIntegrationService
{
	private static final Logger logger = LoggerFactory.getLogger(AvaloqUserInformationIntegrationServiceImpl.class);

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	StaticRoleIntegrationService staticRoleService;

	@Autowired
	CacheManagedUserInformationService cacheService;

	@Autowired
	private AvaloqGatewayHelperService webserviceClient;

	/**
	 * This method return the generic user information including the role, loggedinpersonId, userId and the naturalpersonId
	 * This service is integrated against task template - BTFG$UI_USER.USER#USER_DET
	 * @param profileId
	 * @return UserInformation
	 */
	@Override
	public UserInformation loadUserInformation(JobProfile profileId, ServiceErrors serviceErrors)
	{
		UserInformationImpl userInformation = cacheService.getUserInformation(profileId, serviceErrors);
		logger.debug("loadUserInformation, updating functional roles for profile {}", profileId);
		List <FunctionalRole> functionalRoles = getFunctionalRoleList(userInformation.getUserRoles(), serviceErrors);
		userInformation.setFunctionalRoles(functionalRoles);
		return userInformation;
	}

	/**
	 * This method returns the logged in person
	 * This service is integrated against task template - BTFG$UI_USER.USER#USER_DET
	 * @param 
	 * @return ClientIdentifier
	 */
	@Override
	public ClientIdentifier getLoggedInPerson(JobProfile profileId, ServiceErrors serviceErrors)
	{
		return cacheService.getUserInformation(profileId, serviceErrors);
	}

	/**
	 * This method returns the Role of the user
	 * This service is integrated against task template - BTFG$UI_USER.USER#USER_DET
	 * @param profileId
	 * @return JobPermission
	 **/
	@Override
	public JobPermission getAvailableRoles(JobProfile profileId, ServiceErrors serviceErrors)
	{
		UserInformationImpl userInformation = cacheService.getUserInformation(profileId, serviceErrors);
		userInformation.setFunctionalRoles(getFunctionalRoleList(userInformation.getUserRoles(), serviceErrors));
		return userInformation;
	}

	/**
	 * This method returns the UserId which will further be used as a key to other services
	 * This service is integrated against task template - BTFG$UI_USER.USER#USER_DET
	 * @param profileId
	 * @return UserInformation
	 */
	@Override
	public UserInformation getUserIdentifier(JobProfile profileId, ServiceErrors serviceErrors)
	{
		return cacheService.getUserInformation(profileId, serviceErrors);
	}

	public List <FunctionalRole> getFunctionalRoleList(List <String> avaloqRoles, ServiceErrors serviceErrors)
	{
		List <FunctionalRole> functionalRole = new ArrayList <>();
		for (String avaloqRole : avaloqRoles)
		{
			List <FunctionalRole> roles = staticRoleService.loadFunctionalRoles(avaloqRole, serviceErrors);
			if (roles != null && !roles.isEmpty())
			{
				functionalRole.addAll(roles);
			}
			else
			{
				logger.warn("No functional roles can be retrieved for " + avaloqRole);
			}
		}

		logger.info("Number of Linked functional roles {}", functionalRole.size());
		return functionalRole;
	}

	/**
	 * This method updates in Avaloq and returns the UserPasswordDetail containing last password change date and time information. The Avaloq XSD 
	 * used is BTFG$COM.BTFIN.TRXSVC_USER_V1 and UserPasswordDetail interface as return reference type.
	 *  
	 */
	@Override
	public UserPasswordDetail notifyPasswordChange(final BankingCustomerIdentifier userIdentifier,
		final ServiceErrors serviceErrors)
	{
		logger.debug("Entered updateNotification Method");
		return new IntegrationSingleOperation <UserPasswordDetail>("notifyPasswordChangeInfo", serviceErrors)
		{
			@Override
			public UserPasswordDetail performOperation()
			{
				logger.info("Password Change information has been sent for {}", userProfileService.getGcmId());
				UserPasswordDetail userInformation = new UserPasswordDetailImpl(userIdentifier);
				UserReq userReq = makeNotifyPasswordInformationUserRequest(userProfileService.getGcmId());
				UserRsp ntfcnRsp = webserviceClient.sendToWebService(userReq, AvaloqOperation.USER_REQ, serviceErrors);
				String date = ntfcnRsp.getData().getLastPwdChg().getVal().toString();
				date = date.substring(0, date.indexOf("+"));
				DateTime lastPasswordChangeDate = DateTime.parse(date,
					DateTimeFormat.forPattern(Constants.AVALOQ_RESPONSE_DATE_FORMAT));
				userInformation.setLastPasswordChanged(lastPasswordChangeDate);
				logger.info("Password Change Date Information : {}", date);
				return userInformation;
			}
		}.run();
	}

}