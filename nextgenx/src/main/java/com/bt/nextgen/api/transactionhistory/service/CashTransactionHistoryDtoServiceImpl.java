package com.bt.nextgen.api.transactionhistory.service;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck","squid:MethodCyclomaticComplexity"})
@Service
@Profile({"default","WrapOnThreadImplementation"})
@Transactional(value = "springJpaTransactionManager")
class CashTransactionHistoryDtoServiceImpl implements CashTransactionHistoryDtoService
{
    private static final String UNMARKED_CASH_CATEGORY = "unmarked";
    private static final String REVERSED_TRANSACTION_STATUS = "-36";
    private static final String ORIGINAL_TRANSACTION_STATUS = "36";
    private static final String CANNOT_BE_CATEGORISED = "cannotbemarked";
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    //private static final String PENSION_CATEGORISATION_TOGGLE = "paymentCategorisation";

    @Autowired
    @Qualifier("AvaloqTransactionIntegrationServiceImpl")
    private TransactionIntegrationService transactionIntegrationService;

    @Override
    public List <CashTransactionHistoryDto> search(List <ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
    {
        EncodedString accountId = new EncodedString(criteriaList.get(0).getValue());

        List <CashTransactionHistoryDto> transactionDtoList = new ArrayList <>();
        String dateFrom = criteriaList.get(1).getValue();
        String dateTo = criteriaList.get(2).getValue();
        List <TransactionHistory> transactions = transactionIntegrationService.loadCashTransactionHistory(accountId.plainText(),
                toDBDate(dateTo), toDBDate(dateFrom),
                serviceErrors);

        for (TransactionHistory transaction : transactions)
        {
            CashTransactionHistoryDto transactionDto = buildCashTransactionDto(transaction);

            transactionDtoList.add(transactionDto);
        }

        return transactionDtoList;
    }

    private CashTransactionHistoryDto buildCashTransactionDto(TransactionHistory transaction)
    {
        CashTransactionHistoryDto transactionDto = new CashTransactionHistoryDto(transaction,
                getTransactionCategorisationType(transaction));

        return transactionDto;
    }

    /**
     * Determine  transaction categorisation type for a {@link com.bt.nextgen.service.integration.transactionhistory.TransactionHistory}
     *
     * TransactionHistory transaction transaction to retrieve categorisation for
     * @return
     */
    private String getTransactionCategorisationType(TransactionHistory transaction)
    {
        String category = "";

        if (transaction.getCashCategorisationType() != null)
        {
            category = transaction.getCashCategorisationType().getDisplayCode();
        }
        else if (transaction.getBTOrderType() == BTOrderType.DEPOSIT)
        {
            category = UNMARKED_CASH_CATEGORY;
        }
        else if (transaction.getBTOrderType() == BTOrderType.PAYMENT)
        {
            category = UNMARKED_CASH_CATEGORY;
        }

        if(transaction.getStatus() !=null && (transaction.getStatus().equals(REVERSED_TRANSACTION_STATUS) || transaction.getStatus().equals(ORIGINAL_TRANSACTION_STATUS)))
        {
            category = CANNOT_BE_CATEGORISED;
        }

        return category;
    }

    private DateTime toDBDate(String date) {
        return DateTime.parse(date, DateTimeFormat.forPattern(DATE_FORMAT));
    }
}