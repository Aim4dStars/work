package com.bt.nextgen.service.avaloq;

import javax.annotation.Resource;

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUserCachedAvaloqIntegrationService extends AbstractAvaloqIntegrationService {

	@Resource(name = "userDetailsService")
    public AvaloqBankingAuthorityService userProfileService;

	private final static Logger logger = LoggerFactory.getLogger(AbstractUserCachedAvaloqIntegrationService.class);

	/**
	 *
	 * @deprecated This method is badly named as this ID is the banking customer master Id
	 * @return The banking customer master ID
	 */
	@Deprecated
    public String getAvaloqId() {
       return getUserBankingId();
    }

	public String getUserBankingId() {
		if (userProfileService == null ||userProfileService.getSamlToken()==null ) {
			return "";
		} else  {
            SamlToken samlToken=(SamlToken)userProfileService.getSamlToken();
			return samlToken.getBankReferenceId();
		}
	}

    public String getJobProfileId() {
        if (userProfileService == null || userProfileService.getActiveJobProfile()==null) {
            return "";
        }
        else {
			return userProfileService.getActiveJobProfile().getProfileId();
		}

    }

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
