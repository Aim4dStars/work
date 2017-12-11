package com.bt.nextgen.service.avaloq.corporateaction;

import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts Avaloq offer type ID to CorporateActionOfferType enum.
 * <p/>
 * Used by XML mapper.
 */
@Component
public class CorporateActionOfferTypeConverter implements Converter<String, CorporateActionOfferType> {
	@Autowired
	private StaticIntegrationService staticCodeService;

	/**
	 * Converts Avaloq offer type ID to CorporateActionOfferType enum.
	 * <p/>
	 * Used by XML mapper to map to individual objects.
	 *
	 * @param source Avaloq offer type ID
	 * @return CorporateActionOfferType enum.  Null if source is empty/null or no mapping is found.
	 */
	@Override
	public CorporateActionOfferType convert(String source) {
		// Accept only non-null source, which is most likely the case if the action type was non multi-block.
		if (StringUtils.isNotEmpty(source)) {
			final Code code = staticCodeService.loadCode(CodeCategory.CA_OFFER_TYPE, source, null);

			return CorporateActionOfferType.forId(code.getIntlId());
		}

		return null;
	}
}
