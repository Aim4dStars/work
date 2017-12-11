package com.bt.nextgen.service.avaloq;

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserCacheService
{

	@Resource(name = "userDetailsService")
    public AvaloqBankingAuthorityService userProfileService;

	private final static Logger logger = LoggerFactory.getLogger(UserCacheService.class);


	public String getActiveProfileCacheKey()
	{
		if (userProfileService != null && userProfileService.getActiveJobProfile()!=null ) {
			logger.info("Cache Key Prefix : {}", userProfileService.getActiveJobProfile().getProfileId());
			return userProfileService.getActiveJobProfile().getProfileId();

		} else {
			logger.warn("Caching user related value with no prefix");
			return "";
		}
	}
}