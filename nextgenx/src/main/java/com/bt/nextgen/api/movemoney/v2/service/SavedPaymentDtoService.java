package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

/**
 * Service interface for viewing saved payments
 */
public interface SavedPaymentDtoService extends SearchByCriteriaDtoService<TransactionDto> {
}
