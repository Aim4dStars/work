package com.bt.nextgen.api.registration.service;


import com.bt.nextgen.api.registration.model.UserRoleDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.registration.repository.UserRoleTermsAndConditionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UserRoleDtoServiceImpl implements UserRoleDtoService
{
	private static final Logger logger = LoggerFactory.getLogger(UserRoleDtoServiceImpl.class);

	@Autowired
	private UserRoleTermsAndConditionsRepository userRoleTncRepository;

	@Autowired
	private UserProfileService userProfileService;

	@Override
	public UserRoleDto submit(UserRoleDto userRoleTncDto, ServiceErrors serviceErrors)
	{
		userRoleTncDto.setGcmId(userProfileService.getGcmId());

		try
		{
			userRoleTncRepository.save(UserRoleConverter.toUserRoleTermsAndConditions(userRoleTncDto));
		}
		catch (DataAccessException dae)
		{
			serviceErrors.addError(new ServiceErrorImpl("Unable to save terms and conditions acceptance"));
			logger.error("Unable to save terms and conditions acceptance to database", dae);
		}

		userRoleTncDto.setTransactionStatus(true);
		return userRoleTncDto;
	}
}