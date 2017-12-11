package com.bt.nextgen.api.transactionhistory.service;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.AvaloqFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CashTransactionHistoryReportServiceImpl implements CashTransactionHistoryReportService {

    @Autowired
    private CashTransactionHistoryDtoService pastTransactionDtoService;

    @Override
    public CashTransactionHistoryDto retrievePastTransaction(String accountId, String direction, DateTime startDate,
                                                             DateTime endDate, String receiptNo) {
        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, accountId,
                ApiSearchCriteria.OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.FROM_DATE, ApiSearchCriteria.SearchOperation.EQUALS,
                AvaloqFormatter.asAvaloqFormatDate(startDate), ApiSearchCriteria.OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.TO_DATE, ApiSearchCriteria.SearchOperation.EQUALS,
                AvaloqFormatter.asAvaloqFormatDate(endDate), ApiSearchCriteria.OperationType.STRING));

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        List<CashTransactionHistoryDto> pastTransactionDtos = pastTransactionDtoService.search(criteria, serviceErrors);

        for (CashTransactionHistoryDto pastTransaction : pastTransactionDtos) {
            if (pastTransaction.getDocId().equals(receiptNo)
                    && (direction == null || direction.equals(pastTransaction.getDebitOrCredit()))) {
                return pastTransaction;
            }
        }

        return null;
    }
}
