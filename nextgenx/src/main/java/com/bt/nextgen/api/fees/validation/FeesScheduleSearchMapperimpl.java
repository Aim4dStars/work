package com.bt.nextgen.api.fees.validation;

import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.AvaloqFormatter;
import org.springframework.stereotype.Service;

import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.model.SearchParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Service
public class FeesScheduleSearchMapperimpl implements FeesScheduleSearchMapper
{

	@Override
	public String getSearchKey(ApiSearchCriteria criteria)
	{
		if (Attribute.ACCOUNT_ID.equals(criteria.getProperty()) && SearchOperation.EQUALS == criteria.getOperation()
			&& OperationType.STRING == criteria.getOperationType())
		{
			return SearchParams.PORTFOLIO_ID.name();
		}
		throw new BadRequestException("Unsupported search request " + criteria.getProperty() + ":" + criteria.getOperation());
	}

	@Override
	public String getSearchValue(ApiSearchCriteria criteria)
	{
		if (Attribute.ACCOUNT_ID.equals(criteria.getProperty()))
		{
			return new EncodedString(criteria.getValue()).plainText();
		}

		if (OperationType.DATE.equals(criteria.getOperationType()))
		{
			return AvaloqFormatter.asAvaloqFormatDate(ApiFormatter.parseISODate(criteria.getValue()));
		}

		return criteria.getValue();
	}

}
