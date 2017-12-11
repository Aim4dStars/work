package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionSecurityExchangeType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;


/**
 * Converts Avaloq ID to CorporateActionSecurityExchangeType by looking up static codes.
 */
@Component
public class CorporateActionSecurityExchangeTypeConverter implements Converter<String, CorporateActionSecurityExchangeType> {
	@Autowired
	private StaticIntegrationService staticCodeService;

	/**
	 * Converts Avaloq ID to CorporateActionSecurityExchangeType by looking up static codes.
	 *
	 * @param source the Avaloq ID which will be used to lookup static code for its corresponding text description.
	 * @return CorporateActionSecurityExchangeType enum.  Null if no mapping is found.
	 */
	@Override
	public CorporateActionSecurityExchangeType convert(String source) {
		if (StringUtils.isNotEmpty(source)) {
			final Code code = staticCodeService.loadCode(CodeCategory.CA_SECURITY_EXCHANGE_TYPE, source, null);

			if (code != null) {
				return CorporateActionSecurityExchangeType.forId(code.getIntlId());
			}
		}

		return null;
	}
}
