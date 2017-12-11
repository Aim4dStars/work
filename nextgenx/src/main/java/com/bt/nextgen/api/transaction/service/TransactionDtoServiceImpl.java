package com.bt.nextgen.api.transaction.service;

import com.bt.nextgen.api.transaction.model.InvestmentKey;
import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.api.transaction.model.TransactionKey;
import com.bt.nextgen.api.transaction.model.TransactionStatusEnum;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import com.bt.nextgen.service.avaloq.movemoney.PaymentDetailsImpl;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.avaloq.transaction.TransactionWorkflowStatus;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.movemoney.DepositIntegrationService;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PaymentIntegrationService;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.transaction.Transaction;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationServiceFactory;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsEqual.*;

@Service
@Transactional(value = "springJpaTransactionManager")
class TransactionDtoServiceImpl implements TransactionDtoService {
    private static final String UNMARKED_CASH_CATEGORY = "unmarked";
    private static final String REVERSED_TRANSACTION_STATUS = "-36";
    private static final String ORIGINAL_TRANSACTION_STATUS = "36";
    private static final String CANNOT_BE_CATEGORISED = "cannotbemarked";

    @Autowired
    private TransactionIntegrationServiceFactory transactionListServiceFactory;

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    private DepositIntegrationService depositIntegrationService;

    @Autowired
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.metrics.JavaNCSSCheck")
    private TransactionDto toTransactionDto(Transaction transaction, PayeeDetails payeeDetails) {
        TransactionKey transactionKey = new TransactionKey(
                EncodedString.fromPlainText(transaction.getTransactionId()).toString());
        InvestmentKey investmentKey = new InvestmentKey();

        TransactionDto formattedTransaction = new TransactionDto(transactionKey, investmentKey, transaction.getDescription(),
                new DateTime(transaction.getEffectiveDate()), transaction.getNetAmount(), transaction.getBalance(), null);

        /* Start : Added for CASH Refactor Implementation */
        formattedTransaction.setFirstPayment(transaction.getFirstDate());
        formattedTransaction.setLastPayment(transaction.getEndDate());
        formattedTransaction
                .setNextDueDate(null != transaction.getNextDue() ? transaction.getNextDue() : transaction.getEffectiveDate());
        formattedTransaction.setRecentTrxDate(transaction.getRecentTrxDate());
        formattedTransaction.setPayeeAccount(transaction.getPayeeAccount());
        formattedTransaction.setPayeeBsb(transaction.getPayeeBsb());
        formattedTransaction.setPayerAccount(transaction.getPayerAccount());
        formattedTransaction.setPayerBsb(transaction.getPayerBsb());
        formattedTransaction.setPaymentId(transaction.getPaymentId());
        formattedTransaction.setBillerCode(transaction.getPayeeBillerCode());
        formattedTransaction.setCustRefno(transaction.getPayeeCustrRef());
        formattedTransaction.setOrderType(transaction.getOrderType());
        formattedTransaction.setOrderTypeCode(getOrderTypeCode(transaction));
        formattedTransaction.setValDate(transaction.getValDate());
        formattedTransaction.setFrequency(
                transaction.getFrequency() != null ? transaction.getFrequency().getDescription() : Attribute.EMPTY_STRING);
        formattedTransaction.setRepeatInstr(transaction.getRepeatInstr());
        formattedTransaction.setMaxCount(transaction.getMaxPeriodCnt());
        formattedTransaction.setWorkFlowStatus(
                null != transaction.getWorkFlowStatus() ? transaction.getWorkFlowStatus().name() : Attribute.EMPTY_STRING);
        formattedTransaction.setTransactionStatus(null != transaction.getTransactionStatus()
                ? transaction.getTransactionStatus().toString() : TransactionWorkflowStatus.SCHEDULED.toString());
        formattedTransaction.setStordPosId(EncodedString.fromPlainText(transaction.getStordPos()).toString());
        formattedTransaction.setIndexationType(getIndexationType(transaction));
        formattedTransaction.setIndexationAmount(getIndexationAmount(transaction));

        if (null != transaction.getTransactionStatus()) {
            formattedTransaction.setTranStatusHolder(
                    TransactionStatusEnum.getTransactionStatusValues(transaction.getTransactionStatus().toString()));
        }

        if (null != transaction.getTransactionId()) {
            formattedTransaction.setRecieptNumber(new BigDecimal(transaction.getTransactionId()));
        }

        if (transaction.getMetaType().toString().equalsIgnoreCase(TransactionType.INPAY.getTransactionType())) {
            formattedTransaction.setMetaType(Attribute.IN_PAY_MESSAGE);
            formattedTransaction.setPayer(transaction.getPayer());
            formattedTransaction
                    .setPayee(payeeDetails != null ? payeeDetails.getCashAccount().getAccountName() : Constants.EMPTY_STRING);
        } else if (transaction.getMetaType().toString().equalsIgnoreCase(TransactionType.PAY.getTransactionType())) {
            formattedTransaction.setMetaType(Attribute.PAY_MESSAGE);
            formattedTransaction
                    .setPayer(payeeDetails != null ? payeeDetails.getCashAccount().getAccountName() : Constants.EMPTY_STRING);
            formattedTransaction.setPayee(transaction.getPayee());
        } else {
            formattedTransaction.setMetaType(Attribute.UNKNOWN);
            formattedTransaction.setPayer(transaction.getPayer());
            formattedTransaction.setPayee(transaction.getPayee());
        }

        if (transaction.getPaymentId() != null) {
            formattedTransaction
                    .setLastPaymentUpdated(formattedTransaction.getTransactionStatus()
                            .equalsIgnoreCase(TransactionWorkflowStatus.RETRYING
                                    .toString())
                    || formattedTransaction.getTransactionStatus().equalsIgnoreCase(TransactionWorkflowStatus.REJECTED.toString())
                            ? (transaction.getMetaType().toString().equalsIgnoreCase(TransactionType.INPAY.getTransactionType())
                                    ? Attribute.TXN_RETURNED_MESSAGE : Attribute.TXN_FAILURE_MESSAGE)
                            : Attribute.TXN_SUCCESS_MESSAGE);
        } else {
            formattedTransaction.setLastPaymentUpdated("-");
        }

        /* Added following code to set next payment due date in a scheduled transaction to be used while rendering in UI */
        formattedTransaction.setNextPaymentDate(transaction.getNextDue());
        formattedTransaction.setPensionPaymentType(getPensionPaymentType(transaction.getPensionPaymentType()));
        formattedTransaction.setContributionType(transaction.getContributionType());
        formattedTransaction.setMaximumAnnualPot(transaction.getPensionMaximumAnnualPot());
        formattedTransaction.setHasDrawdownInprogress(transaction.getHasDrawdownInprogress());

        return formattedTransaction;
    }

    private String getOrderTypeCode(Transaction transaction) {
        return transaction.getOrderTypeCode() != null ? transaction.getOrderTypeCode().name() : null;
    }

    private String getIndexationType(Transaction transaction) {
        return transaction.getPensionIndexationType() != null ? transaction.getPensionIndexationType().getLabel() : null;
    }

    private BigDecimal getIndexationAmount(Transaction transaction) {
        if (transaction.getPensionIndexationType() == IndexationType.DOLLAR) {
            return transaction.getPensionIndexationAmount();
        }

        if (transaction.getPensionIndexationType() == IndexationType.PERCENTAGE) {
            return transaction.getPensionIndexationPercent();
        }

        return null;
    }

    /**
     * Retrieve list of Transactions
     *
     * @param criteriaList
     * @param serviceErrors
     * @return List <TransactionDto>
     */
    @Override
    public List<TransactionDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String accountId = null;
        String transactionType = null;
        String paymentId = null;
        String transactionId = null;
        boolean isPayeeDetailRequired = true;
        String metaType = null;
        String mode = null;
        List<TransactionDto> formattedTransactions = new ArrayList<>();

        for (ApiSearchCriteria criteria : criteriaList) {
            switch (criteria.getProperty()) {
                case Attribute.PORTFOLIO_ID:
                    accountId = new EncodedString(criteria.getValue()).plainText();
                    break;
                case Attribute.TRANSACTION_TYPE:
                    transactionType = criteria.getValue();
                    break;
                case Attribute.PAYMENT_ID:
                    paymentId = criteria.getValue();
                    break;
                case Attribute.TRANSACTION_ID:
                    transactionId = criteria.getValue();
                    break;
                case Attribute.ACCOUNT_ID:
                    accountId = new EncodedString(criteria.getValue()).plainText();
                    break;
                case Constants.PAYEE_DETAIL_REQUIRED:
                    isPayeeDetailRequired = new Boolean(criteria.getValue());
                    break;
                case Constants.META_TYPE:
                    metaType = criteria.getValue();
                    break;
                case Attribute.SERVICE_TYPE:
                    mode = criteria.getValue();
                    break;
            }
        }

        WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
        identifier.setBpId(accountId);

        if (transactionType == Attribute.SCHEDULED_TRANSACTIONS) {
            List<Transaction> transactions = transactionListServiceFactory.getInstance(mode).loadScheduledTransactions(identifier,
                    serviceErrors);
            PayeeDetails payeeDetails = null;

            if (isPayeeDetailRequired) {
                payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(identifier, serviceErrors);
            }

            for (Transaction transaction : transactions) {
                formattedTransactions.add(toTransactionDto(transaction, payeeDetails));
            }

            // Sort txn in ascending order of nextduedate, if null use effectiveDate
            Collections.sort(formattedTransactions, new Comparator<TransactionDto>() {
                @Override
                public int compare(TransactionDto o3, TransactionDto o4) {
                    if (null != o3.getNextDueDate() && null != o4.getNextDueDate()) {
                        return o3.getNextDueDate().compareTo(o4.getNextDueDate());
                    } else if (null == o3.getNextDueDate() && null != o4.getNextDueDate()) {
                        return o3.getEffectiveDate().compareTo(o4.getNextDueDate());
                    } else if (null != o3.getNextDueDate() && null == o4.getNextDueDate()) {
                        return o3.getNextDueDate().compareTo(o4.getEffectiveDate());
                    } else {
                        return o3.getEffectiveDate().compareTo(o4.getEffectiveDate());
                    }

                }
            });

            // Now Sort txns by making failed and Retry txns on top
            Collections.sort(formattedTransactions, new Comparator<TransactionDto>() {
                @Override
                public int compare(TransactionDto o1, TransactionDto o2) {
                    return o1.getTranStatusHolder() < o2.getTranStatusHolder() ? -1
                            : o1.getTranStatusHolder() == o2.getTranStatusHolder() ? 0 : 1;
                }
            });
        } else if (transactionType == Attribute.STOP_SCHEDULED_TRANSACTIONS) {
            TransactionStatus transactionStatus;

            if (null != metaType && metaType.equalsIgnoreCase(Constants.DEPOSIT)) {
                PositionIdentifier positionIdentifier = new PositionIdentifierImpl();
                positionIdentifier.setPositionId(EncodedString.toPlainText(transactionId));
                transactionStatus = depositIntegrationService.stopDeposit(positionIdentifier, serviceErrors);
            } else {
                PaymentDetailsImpl paymentDetails = new PaymentDetailsImpl();
                paymentDetails.setPositionId(EncodedString.toPlainText(transactionId));
                transactionStatus = paymentIntegrationService.stopPayment(paymentDetails, serviceErrors);
            }

            TransactionKey transactionKey = new TransactionKey(transactionId);
            TransactionDto transactionDto = new TransactionDto(transactionKey, null, null, null, null, null, null);
            transactionDto.setPaymentId(paymentId);
            transactionDto.setSuccessful(transactionStatus.isSuccessful());

            // Removing successfully stopped transactions from display without having to refresh browser as per requirement
            if (!transactionStatus.isSuccessful()) {
                formattedTransactions.add(transactionDto);
            }

            return formattedTransactions;
        } else {
            formattedTransactions = toTransactionDto(
                    transactionListServiceFactory.getInstance(mode).loadRecentCashTransactions(identifier, serviceErrors));
        }

        return formattedTransactions;
    }

    private List<TransactionDto> toTransactionDto(List<TransactionHistory> pastTransactions) {
        List<TransactionDto> transactions = new ArrayList<TransactionDto>();

        for (TransactionHistory pastTransaction : pastTransactions) {
            TransactionDto transaction = new TransactionDto(null, null, null, new DateTime(pastTransaction.getEffectiveDate()),
                    pastTransaction.getAmount(), pastTransaction.getBalance(), null);

            // Including payer name to be displayed in 'Overview' -> 'Recent Transactions' page
            transaction.setDescription(pastTransaction.getBookingText()
                    + (pastTransaction.getTransactionDescription() != null ? ". " + pastTransaction.getTransactionDescription() : ""));
            transaction.setWorkFlowStatus(pastTransaction.isCleared() ? Constants.PAYMENT_STATUS_CLEARED : "");
            transaction.setValDate(new DateTime(pastTransaction.getValDate()));
            transaction.setDocDescription(pastTransaction.getDocDescription());
            transaction.setCategory(getTransactionCategorisationType(pastTransaction));

            transactions.add(transaction);
        }

        return transactions;
    }

    /**
     * Determine transaction categorisation type for a
     * {@link com.bt.nextgen.service.integration.transactionhistory.TransactionHistory}
     * <p/>
     * TransactionHistory transaction transaction to retrieve categorisation for
     *
     * @return
     */
    private String getTransactionCategorisationType(TransactionHistory transaction) {
        String category = "";

        if (transaction.getCashCategorisationType() != null) {
            category = transaction.getCashCategorisationType().getDisplayCode();
        }
        else  if (transaction.getBTOrderType() == BTOrderType.DEPOSIT) {
                category = UNMARKED_CASH_CATEGORY;
        }
        else if (transaction.getBTOrderType() == BTOrderType.PAYMENT) {
            category = UNMARKED_CASH_CATEGORY;
        }

        if (transaction.getStatus() != null && (transaction.getStatus().equals(REVERSED_TRANSACTION_STATUS)
                || transaction.getStatus().equals(ORIGINAL_TRANSACTION_STATUS))) {
            category = CANNOT_BE_CATEGORISED;
        }

        return category;
    }

    private String getPensionPaymentType(PensionPaymentType pensionPaymentType) {
        return pensionPaymentType == null ? null : pensionPaymentType.getLabel();
    }

    @Override
    public TransactionDto find(TransactionKey key, ServiceErrors serviceErrors) {
        WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
        identifier.setBpId(new EncodedString(key.getAccountId()).plainText());

        String transactionId = new EncodedString(key.getTransactionId()).plainText();

        List<Transaction> transactions = transactionListServiceFactory.getInstance(null).loadScheduledTransactions(identifier,
                serviceErrors);

        if (transactions != null && !transactions.isEmpty()) {
            Transaction transaction = selectFirst(transactions,
                    having(on(Transaction.class).getTransactionId(), equalTo(transactionId)));

            if (transaction != null) {
                PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(identifier, serviceErrors);
                return toTransactionDto(transaction, payeeDetails);
            }
        }

        return new TransactionDto();
    }
}