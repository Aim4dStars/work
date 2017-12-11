package com.bt.nextgen.service.avaloq.corporateaction;

import com.btfin.panorama.core.conversion.CodeCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;

/**
 * Converts Avaloq ID to CorporateActionElectionStatus by looking up static codes.
 */
@Component
public class CorporateActionAccountParticipationStatusConverter implements Converter<String,CorporateActionAccountParticipationStatus> {
	@Autowired
	private StaticIntegrationService staticCodeService;

	/**
	 * Converts Avaloq ID to CorporateActionStatus by looking up static codes.
	 *
	 * @param source the Avaloq ID which will be used to lookup static code for its corresponding text description.
	 * @return CorporateActionElectionStatus enum.  Null if no mapping is found.
	 */
	@Override
	public CorporateActionAccountParticipationStatus convert(String source) {
		// Must not be null
		final Code code = staticCodeService.loadCode(CodeCategory.CA_ELECT_STATUS, source, null); 

		return CorporateActionAccountParticipationStatus.forId(code.getIntlId());
	}
}
