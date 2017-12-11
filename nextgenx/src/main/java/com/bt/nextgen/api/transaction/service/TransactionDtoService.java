package com.bt.nextgen.api.transaction.service;

import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.api.transaction.model.TransactionKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;

public interface TransactionDtoService extends SearchByCriteriaDtoService<TransactionDto>,
		FindByKeyDtoService<TransactionKey, TransactionDto> {
}
