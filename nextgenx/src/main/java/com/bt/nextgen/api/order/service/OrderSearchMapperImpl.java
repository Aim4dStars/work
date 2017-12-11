package com.bt.nextgen.api.order.service;

import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.AvaloqFormatter;
import com.bt.nextgen.core.web.model.SearchParams;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.springframework.stereotype.Service;

@Service("OrderSearchMapperV0.1")
public class OrderSearchMapperImpl implements OrderSearchMapper {
    @Override
    @SuppressWarnings("squid:S1142")
    public String getSearchKey(ApiSearchCriteria criteria) {
        if (Attribute.ACCOUNT_ID.equals(criteria.getProperty()) && SearchOperation.EQUALS == criteria.getOperation()
                && OperationType.STRING == criteria.getOperationType()) {
            return SearchParams.PORTFOLIO_ID.name();
        }

        if (Attribute.LAST_UPDATE_DATE.equals(criteria.getProperty())
                && SearchOperation.NEG_GREATER_THAN == criteria.getOperation()
                && OperationType.DATE == criteria.getOperationType()) {
            return SearchParams.TO_DATE.name();
        }

        if (Attribute.LAST_UPDATE_DATE.equals(criteria.getProperty()) && SearchOperation.NEG_LESS_THAN == criteria.getOperation()
                && OperationType.DATE == criteria.getOperationType()) {
            return SearchParams.FROM_DATE.name();
        }

        if (Attribute.ORDER_ID.equals(criteria.getProperty()) && SearchOperation.EQUALS == criteria.getOperation()
                && OperationType.STRING == criteria.getOperationType()) {
            return SearchParams.ORDER_ID.name();
        }

        if (Attribute.ADVISER_ID.equals(criteria.getProperty()) && SearchOperation.EQUALS == criteria.getOperation()
                && OperationType.STRING == criteria.getOperationType()) {
            return SearchParams.ADVISER_ID.name();
        }

        throw new BadRequestException("Unsupported search request " + criteria.getProperty() + ":" + criteria.getOperation());
    }

    @Override
    public String getSearchValue(ApiSearchCriteria criteria) {
        if (Attribute.ACCOUNT_ID.equals(criteria.getProperty())) {
            return new EncodedString(criteria.getValue()).plainText();
        }

        if (Attribute.ADVISER_ID.equals(criteria.getProperty())) {
            return new EncodedString(criteria.getValue()).plainText();
        }

        if (OperationType.DATE.equals(criteria.getOperationType())) {
            return AvaloqFormatter.asAvaloqFormatDate(ApiFormatter.parseISODate(criteria.getValue()));
        }

        return criteria.getValue();
    }

}
