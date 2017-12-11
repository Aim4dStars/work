package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts Avaloq integer ID to CorporateActionType enum.
 * <p/>
 * This class is used by the Spring XML converter.
 */
@Component
public class CorporateActionTypeConverter implements Converter<String, CorporateActionType> {
	@Autowired
	private StaticIntegrationService staticCodeService;

	/**
	 * Converts Avaloq offer type ID to CorporateActionType enum.
	 * Used by XML mapper to map to individual objects.
	 *
	 * @param source Avaloq offer type ID
	 * @return CorporateActionType enum.  Null if no mapping is found.
	 */
	@Override
	public CorporateActionType convert(String source) {
		final Code code = staticCodeService.loadCode(CodeCategory.CA_TYPE, source, null);

		return CorporateActionType.forId(code.getIntlId());
	}
}
