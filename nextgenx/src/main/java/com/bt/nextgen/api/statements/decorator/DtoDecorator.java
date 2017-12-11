package com.bt.nextgen.api.statements.decorator;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.service.integration.financialdocument.Document;

/**
 * Decorator interface to support custom logic on dto properties.
 */
public interface DtoDecorator {

    DocumentDto decorate();
}
