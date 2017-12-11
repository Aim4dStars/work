package com.bt.nextgen.api.corporateaction.v1.service.converter;

import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionHelper;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionRequestConverter;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionGroup;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class CorporateActionConverterFactoryImpl implements CorporateActionConverterFactory, ApplicationContextAware {
	private static final String CA_PREFIX = "CA_";
	private static final String SUFFIX_RESPONSE = "_RESPONSE";
	private static final String SUFFIX_REQUEST = "_REQUEST";
	private static final String MANDATORY_CA = CA_PREFIX + "MANDATORY";

	@Autowired
	private CorporateActionHelper corporateActionHelper;

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public CorporateActionResponseConverterService getResponseConverterService(CorporateActionDetails corporateActionDetails) {
		return (CorporateActionResponseConverterService) getCorporateActionBean(corporateActionHelper.getEffectiveCorporateActionType(
						corporateActionDetails).getType(), corporateActionDetails.getCorporateActionOfferType(),
				SUFFIX_RESPONSE, CorporateActionResponseConverter.class);
	}

	@Override
	public CorporateActionRequestConverterService getRequestConverterService(CorporateActionDetails corporateActionDetails) {
		return (CorporateActionRequestConverterService) getCorporateActionBean(corporateActionHelper.getEffectiveCorporateActionType(
						corporateActionDetails).getType(), corporateActionDetails.getCorporateActionOfferType(),
				SUFFIX_REQUEST, CorporateActionRequestConverter.class);
	}

	public Object getCorporateActionBean(CorporateActionType corporateActionType,
										 CorporateActionOfferType corporateActionOfferType, String suffix,
										 Class corporateActionConverterClass) {

		Map<String, Object> convertersMap = applicationContext.getBeansWithAnnotation(corporateActionConverterClass);

		String offerType = corporateActionOfferType != null ? "_" + corporateActionOfferType.getCode() : "";

		String beanName = CA_PREFIX + corporateActionType.getCode() + offerType + suffix;

		if (!convertersMap.containsKey(beanName)) {
			// Default to multi-block if voluntary
			beanName = (CorporateActionGroup.VOLUNTARY.equals(corporateActionType.getGroup()) ?
					CA_PREFIX + CorporateActionType.MULTI_BLOCK.getCode() : MANDATORY_CA) + suffix;
		}

		return convertersMap.get(beanName);
	}
}
