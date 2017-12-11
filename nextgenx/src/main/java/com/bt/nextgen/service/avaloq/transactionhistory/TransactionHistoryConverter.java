package com.bt.nextgen.service.avaloq.transactionhistory;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;
import com.bt.nextgen.core.mapping.AbstractMappingConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionOrderType;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.service.integration.transactionhistory.TransactionSubType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.google.common.collect.ComparisonChain;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Component
public class TransactionHistoryConverter extends AbstractMappingConverter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryConverter.class);

    private static final String WRAP_SEC_CLASS_UNIT_TRUEST = "Unit Trust";
    private static final String WRAP_SEC_CLASS_EQUITY = "Equity";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    public List<TransactionHistory> setExtraDetails(List<TransactionHistory> transactions, ServiceErrors serviceErrors) {

        Map<Pair<String, DateTime>, Asset> assetMap = getAssetMap(transactions, serviceErrors);

        for (TransactionHistory transaction : transactions) {

            Asset contAsset = assetMap.get(new ImmutablePair<>(transaction.getContAssetId(), transaction.getValDate()));
            Asset asset = assetMap.get(new ImmutablePair<>(transaction.getPosAssetId(), transaction.getValDate()));

            if (asset == null) {
                logger.warn("Asset cannot be retrieved for the following id: " + transaction.getPosAssetId());
            }
            else if (contAsset == null && ContainerType.MANAGED_PORTFOLIO.equals(transaction.getContType())) {
                logger.warn("Asset cannot be retrieved for the following id: " + transaction.getContAssetId());
            }
            else {
                transaction = updateTransaction(transaction, asset, contAsset);
            }

            if (transaction.getRefAssetId() != null) {
                transaction.setRefAsset(
                        assetMap.get(new ImmutablePair<>(transaction.getRefAssetId(), transaction.getValDate())));
            }
        }

        return transactions;
    }

    public List<TransactionHistory> setTransactionSubTypes(List<TransactionHistory> transactions) {
        List<TransactionHistory> transactionsWithContributions = new ArrayList<>();
        for (TransactionHistory transaction : transactions) {
            if (transaction.getTransactionSubTypes() != null && transaction.getTransactionSubTypes().size() > 0) {
                transactionsWithContributions.addAll(sortAndAddSubTypes(transaction, transaction.getTransactionSubTypes()));
            }
            else {
                transactionsWithContributions.add(transaction);
            }
        }
        return transactionsWithContributions;
    }

    private List<TransactionHistory> sortAndAddSubTypes(TransactionHistory transactionHistory,
                                                        List<TransactionSubType> transactionSubTypes) {
        List<TransactionHistory> sortedTransactions = new ArrayList<>();
        Group<TransactionSubType> groupedSubTypes = Lambda.group(transactionSubTypes,
                Lambda.by(Lambda.on(TransactionSubType.class).getTransactionSubTypeDescription()));

        List<Group<TransactionSubType>> subGroups = groupedSubTypes.subgroups();
        for (Group<TransactionSubType> subGroup : subGroups) {
            TransactionHistoryImpl transactionHistoryWithSubType = new TransactionHistoryImpl();
            BigDecimal amount = BigDecimal.ZERO;
            for (TransactionSubType subtype : subGroup.findAll()) {
                amount = amount.add(subtype.getTransactionSubTypeAmount());
            }

            String subType = subGroup.findAll().get(0).getTransactionSubType();
            String description = subGroup.findAll().get(0).getTransactionSubTypeDescription();
            String transactionType = subGroup.findAll().get(0).getTransactionType();
            if (subType != null) {
                transactionHistoryWithSubType.setDocId(transactionHistory.getDocId());
                transactionHistoryWithSubType.setTransactionType(retrieveOrderType(transactionType));
                transactionHistoryWithSubType.setContType(transactionHistory.getContType());
                transactionHistoryWithSubType.setBookingText(description);
                transactionHistoryWithSubType.setAmount(amount);
                transactionHistoryWithSubType.setValDate(transactionHistory.getValDate());
                transactionHistoryWithSubType.setEffectiveDate(transactionHistory.getEffectiveDate());
                transactionHistoryWithSubType.setAsset(transactionHistory.getAsset());
                transactionHistoryWithSubType.setContAsset(transactionHistory.getContAsset());
                sortedTransactions.add(transactionHistoryWithSubType);
            }
        }
        return sortedTransactions;
    }

    private TransactionHistory updateTransaction(TransactionHistory transaction, Asset asset, Asset contAsset) {

        String status = retrieveStatus(transaction.getStatus());
        String transactionType = retrieveOrderType(transaction.getTransactionType());

        transaction.setStatus(status);
        transaction.setTransactionType(transactionType);
        transaction.setAsset(asset);

        if (ContainerType.DIRECT.equals(transaction.getContType())) {
            transaction.setAccountId(transaction.getPosId());
        }
        else if (ContainerType.MANAGED_PORTFOLIO.equals(transaction.getContType())) {
            transaction.setAccountId(transaction.getContId());
            transaction.setContAsset(contAsset);
        }

        return transaction;
    }

    private Map<Pair<String, DateTime>, Asset> getAssetMap(List<TransactionHistory> transactions, ServiceErrors serviceErrors) {
        Map<DateTime, Set<String>> assetSetMap = new HashMap<>();

        for (TransactionHistory transaction : transactions) {
            if (transaction.getContAssetId() != null) {
                addAssetToMap(assetSetMap, transaction.getValDate(), transaction.getContAssetId());
            }

            if (transaction.getPosAssetId() != null) {
                addAssetToMap(assetSetMap, transaction.getValDate(), transaction.getPosAssetId());
            }

            if (transaction.getRefAssetId() != null) {
                addAssetToMap(assetSetMap, transaction.getValDate(), transaction.getRefAssetId());
            }
        }

        Map<Pair<String, DateTime>, Asset> result = new HashMap<>();
        for (Entry<DateTime, Set<String>> entry : assetSetMap.entrySet()) {
            Map<String, Asset> assets = assetIntegrationService.loadAssets(entry.getValue(), entry.getKey(), serviceErrors);
            for (Asset asset : assets.values()) {
                result.put(new ImmutablePair<String, DateTime>(asset.getAssetId(), entry.getKey()), asset);
            }
        }
        return result;
    }

    private void addAssetToMap(Map<DateTime, Set<String>> assetSetMap, DateTime effectiveDate, String assetId) {
        if (assetId == null) {
            return;
        }
        Set<String> assetSet = assetSetMap.get(effectiveDate);
        if (assetSet == null) {
            assetSet = new HashSet<>();
            assetSetMap.put(effectiveDate, assetSet);
        }
        assetSet.add(assetId);
    }

    /**
     * Retrieves the order type from btfg$ui_ot table based on the id
     */
    private String retrieveOrderType(String id) {

        if (StringUtils.isNotEmpty(id)) {
            Code code = staticIntegrationService.loadCode(CodeCategory.UI_ORDER_TYPE, id, new ServiceErrorsImpl());
            if (code != null) {
                return code.getName();
            }
        }
        return null;
    }

    /**
     * Retrieves the status type from btfg$evt_status table based on the id
     */
    private String retrieveStatus(String id) {

        if (StringUtils.isNotEmpty(id)) {
            Code code = staticIntegrationService.loadCode(CodeCategory.TXN_STATUS, id, new ServiceErrorsImpl());
            if (code != null) {
                return code.getName();
            }
        }
        return null;
    }

    /**
     * This method evaluates and sets the Balance for every transaction based on the total closing balance of the portfolio. It is
     * an evaluated field on the basis of the current standing of the portfolio and the amount for which the transaction has been
     * done (payment, deposit or system transaction).
     *
     * @param transactions
     *
     * @return List<PastTransaction>
     */
    public List<TransactionHistory> evaluateBalanceAndSystemTransaction(List<TransactionHistory> transactions, DateTime bankDate) {

        if (!transactions.isEmpty()) {
            List<TransactionHistory> sortedTransactions = transactions;

            sortedTransactions = sortBy(sortedTransactions);

            BigDecimal portfolioClosingBalance = sortedTransactions.get(0).getClosingBalance();

            for (int i = 0; i < sortedTransactions.size(); i++) {
                if (i == 0) {
                    sortedTransactions.get(i).setBalance(portfolioClosingBalance);
                }
                else {
                    BigDecimal balance = sortedTransactions.get(i - 1).getBalance()
                            .subtract(sortedTransactions.get(i - 1).getAmount());
                    sortedTransactions.get(i).setBalance(balance);
                }

                evaluateClearedAndSystemTransactionFlag(sortedTransactions.get(i), bankDate);
            }

            return sortedTransactions;
        }

        return transactions;
    }

    /**
     * This method evaluates the clearing status and the type of the given transaction. Clearing status is evaluated on the basis
     * of meta type and the clearance date of the transaction. System transaction is evaluated on the basis of meta type and order
     * type of the transaction.
     *
     * @param transaction
     *
     * @return
     */
    public TransactionHistory evaluateClearedAndSystemTransactionFlag(TransactionHistory transaction, DateTime bankDate) {

        if (transaction.getClearDate() != null && transaction.getMetaType().equalsIgnoreCase(TransactionType.INPAY.name())) {

            DateTime clearanceDate = transaction.getClearDate();

            if (clearanceDate.equals(bankDate) || clearanceDate.isBefore(bankDate)) {
                transaction.setCleared(true);
            }
        }
        else { // if the transaction is not INPAY then clearance flag can be ignored as specific type of INPAY transactions can
            // only be uncleared,
            // rest all types of transactions are always cleared.
            // if clearance date is not present then the transaction is marked as cleared
            transaction.setCleared(true);
        }

        if (isSystemTransaction(transaction)) {
            transaction.setSystemTransaction(true);
        }

        return transaction;
    }

    private boolean isSystemTransaction(TransactionHistory transaction) {

        boolean condition1 = transaction.getMetaType().equalsIgnoreCase(TransactionType.INPAY.name())
                && (transaction.getOrderType().equalsIgnoreCase(TransactionOrderType.BPAY_FILE.getName())
                || transaction.getOrderType().equalsIgnoreCase(TransactionOrderType.CHQ.getName()) || transaction
                .getOrderType().equalsIgnoreCase(TransactionOrderType.CHQ_FILE.getName()));

        boolean condition2 = !transaction.getMetaType().equalsIgnoreCase(TransactionType.PAY.name())
                && !transaction.getMetaType().equalsIgnoreCase(TransactionType.INPAY.name());

        return condition1 || condition2;
    }

    /**
     * Sort the transaction list on the basis of valDate and then evtId. Guava API is used to sort the chaining inputs.
     *
     * @param pastTransactions
     */
    public List<TransactionHistory> sortBy(List<TransactionHistory> pastTransactions) {

        Collections.sort(pastTransactions, new Comparator<TransactionHistory>() {
            @Override
            public int compare(TransactionHistory p1, TransactionHistory p2) {
                return ComparisonChain.start().compare(p1.getValDate(), p2.getValDate())
                        .compare(Math.abs(p1.getEvtId()), Math.abs(p2.getEvtId())).result();
            }
        });

        Collections.reverse(pastTransactions);
        sortAbsoluteEvtId(pastTransactions);

        return pastTransactions;
    }

    /**
     * Method to sort the list on the basis of absolute value of the EvtId. Positive evtId takes priority over negative evtId.
     *
     * @param pastTransactions
     *
     * @return List<PastTransaction>
     */
    public List<TransactionHistory> sortAbsoluteEvtId(List<TransactionHistory> pastTransactions) {

        for (int i = 0; i < pastTransactions.size() - 1; i++) {
            int j = i + 1;

            if (Math.abs(pastTransactions.get(i).getEvtId()) == Math.abs(pastTransactions.get(j).getEvtId())
                    && pastTransactions.get(j).getEvtId() < 0) {
                Collections.swap(pastTransactions, i, j);
            }
        }

        return pastTransactions;
    }
}
