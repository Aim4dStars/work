package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.hamcrest.Matchers;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;

/**
 * Corporate action dto service implementation
 */

@Service
public class CorporateActionListDtoServiceImpl extends CorporateActionListDtoServiceBaseImpl implements CorporateActionListDtoService {
	/**
	 * Main search to retrieve a list of corporate actions
	 *
	 * @param criteriaList  optional START_DATE and END_DATE.  ADVISER_ID will be required in the future.
	 * @param serviceErrors the service errors object
	 * @return a list of corporate action dto's within the date range.
	 */
	@Override
	public List<CorporateActionBaseDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
		CorporateActionListDto corporateActionListDto =
				searchCommon(new CorporateActionListDtoKey(getCriteria(Attribute.START_DATE, criteriaList),
						getCriteria(Attribute.END_DATE, criteriaList), getCriteria(Attribute.CA_TYPE, criteriaList),
						getCriteria(Attribute.ACCOUNT_ID, criteriaList), getCriteria(Attribute.PORTFOLIO_MODEL, criteriaList)),
						serviceErrors);

		return corporateActionListDto.getCorporateActions();
	}

	/**
	 * Helper method to get the value associated with the given key
	 *
	 * @param key          key of the value from Attribute object
	 * @param criteriaList the list containing criteria
	 * @return the value associated with the criteria
	 */
	private String getCriteria(String key, List<ApiSearchCriteria> criteriaList) {
		ApiSearchCriteria criteria = selectFirst(criteriaList, having(on(ApiSearchCriteria.class).getProperty(), Matchers.equalTo(key)));

		return criteria != null ? criteria.getValue() : null;
	}
}
