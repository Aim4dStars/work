package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts Avaloq ID to CorporateActionStatus by looking up static codes.
 */
@Component
public class CorporateActionStatusConverter implements Converter<String, CorporateActionStatus> {
	@Autowired
	private StaticIntegrationService staticCodeService;

	/**
	 * Converts Avaloq ID to CorporateActionStatus by looking up static codes.
	 *
	 * @param source the Avaloq ID which will be used to lookup static code for its corresponding text description.
	 * @return CorporateActionStatus enum.  Null if no mapping is found.
	 */
	@Override
	public CorporateActionStatus convert(String source) {
		// Must not be null
		final Code code = staticCodeService.loadCode(CodeCategory.ORDER_STATUS, source, null);

		return CorporateActionStatus.forId(code.getIntlId());
	}
}
