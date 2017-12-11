package com.bt.nextgen.api.transactionhistory.service;

import com.bt.nextgen.api.transactionhistory.model.TransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.model.TransactionType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transactionhistory.WrapTransactionHistoryImpl;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.transaction.DashboardTransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile({"WrapOffThreadImplementation"})
@Transactional(value = "springJpaTransactionManager")
public class WrapTransactionHistoryDtoServiceImpl implements TransactionHistoryDtoService {

    @Autowired
    @Qualifier("ThirdPartyTransactionIntegrationService")
    private DashboardTransactionIntegrationService wrapTransactionIntegrationService;

    @Autowired
    private OptionsService optionsService;

    @Override
    @Transactional(value = "springJpaTransactionManager", readOnly = true)
    public List<TransactionHistoryDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        Map<String, String> parameterValues = getParamValues(criteriaList);

        EncodedString portfolioId = new EncodedString(parameterValues.get("accountId"));
        DateTime startDate = new DateTime(parameterValues.get("startDate"));
        DateTime endDate = new DateTime(parameterValues.get("endDate"));

        List<TransactionHistoryDto> transactionDtos = getTransactionHistory(portfolioId.toString(), startDate, endDate,
                serviceErrors);

        String assetCode = parameterValues.get("assetCode");

        if (assetCode != null) {
            List<TransactionHistoryDto> filteredList = new ArrayList<TransactionHistoryDto>();
            // Filter based on securityCode or name
            for (TransactionHistoryDto dto : transactionDtos) {
                String aCode = dto.getAssetCode();
                if (aCode != null && aCode.toLowerCase().contains(assetCode.toLowerCase())) {
                    filteredList.add(dto);
                    continue;
                }

                String aName = dto.getAssetName();
                if (aName != null && aName.toLowerCase().contains(assetCode.toLowerCase())) {
                    filteredList.add(dto);
                }
            }
            transactionDtos.clear();
            transactionDtos.addAll(filteredList);
        }

        processLinkedTransactions(transactionDtos);

        return transactionDtos;
    }

    private Map<String, String> getParamValues(List<ApiSearchCriteria> criteriaList) {
        Map<String, String> parameterValues = new HashMap<String, String>();

        String accountId = null;
        String startDate = null;
        String endDate = null;
        String assetCode = null;

        for (ApiSearchCriteria parameter : criteriaList) {
            if (Attribute.ACCOUNT_ID.equals(parameter.getProperty())) {
                accountId = parameter.getValue();
            }
            else if (Attribute.START_DATE.equals(parameter.getProperty())) {
                startDate = parameter.getValue() == null ? new DateTime().toString() : parameter.getValue();
            }
            else if (Attribute.END_DATE.equals(parameter.getProperty())) {
                endDate = parameter.getValue() == null ? new DateTime().toString() : parameter.getValue();
            }
            else if (Attribute.ASSET_CODE.equals(parameter.getProperty())) {
                assetCode = parameter.getValue();
            }
        }

        parameterValues.put("accountId", accountId);
        parameterValues.put("startDate", startDate);
        parameterValues.put("endDate", endDate);
        parameterValues.put("assetCode", assetCode);

        return parameterValues;
    }

    public List<TransactionHistoryDto> getTransactionHistory(String portfolioId, DateTime startDate, DateTime endDate,
                                                             ServiceErrors serviceErrors) {
        List<TransactionHistoryDto> transactionDtos = new ArrayList<TransactionHistoryDto>();

        String pid = EncodedString.toPlainText(portfolioId);
        List<TransactionHistory> transactions = wrapTransactionIntegrationService.loadTransactionHistory(pid, endDate, startDate,
                serviceErrors);


        if (transactions != null) {
            for (TransactionHistory transaction : transactions) {
                if (transaction instanceof WrapTransactionHistoryImpl) {
                    // Wrap transaction
                    transactionDtos.add(buildWrapTransactionDto(transaction));
                }
                else {
                    //Panorama transaction
                    if (filterTransaction(transaction)) {
                        continue;
                    }
                    transactionDtos.add(buildTransactionDto(transaction));
                }
            }
        }

        return transactionDtos;
    }

    private TransactionHistoryDto buildTransactionDto(TransactionHistory transaction) {
        String description = getTransactionDescription(transaction);
        Map<String, Object> amountDetails = getAmountDetails(transaction);
        Map<String, String> assetDetails = getAssetDetails(transaction);

        return new TransactionHistoryDto(transaction, assetDetails, description, amountDetails);
    }

    private TransactionHistoryDto buildWrapTransactionDto(TransactionHistory transaction) {
        String description = transaction.getBookingText();
        Map<String, Object> amountDetails = getWrapAmountDetails(transaction);
        Map<String, String> assetDetails = getWrapAssetDetails(transaction);

        return new TransactionHistoryDto(transaction, assetDetails, description, amountDetails);
    }

    private Map<String, Object> getWrapAmountDetails(TransactionHistory transactionHistory) {
        WrapTransactionHistoryImpl wrapTransactionHistory = (WrapTransactionHistoryImpl) transactionHistory;
        Map<String, Object> amountDetails = new HashMap<String, Object>();

        BigDecimal netAmount = null;
        BigDecimal quantity = null;
        String transactionType = wrapTransactionHistory.getTransactionType();
        if (wrapTransactionHistory.getQuantity() != null) {
            quantity = wrapTransactionHistory.getQuantity();
        }
        else {
            netAmount = wrapTransactionHistory.getAmount();
        }
        amountDetails.put("transactionType", transactionType);
        amountDetails.put("netAmount", netAmount);
        amountDetails.put("quantity", quantity);
        return amountDetails;
    }

    private Map<String, String> getWrapAssetDetails(TransactionHistory transactionHistory) {
        Map<String, String> assetDetails = new HashMap<String, String>();
        WrapTransactionHistoryImpl wrapTransactionHistory = (WrapTransactionHistoryImpl) transactionHistory;
        String investmentName = null;
        String investmentType = null;
        if (wrapTransactionHistory.getAssetType() != null) {
            investmentType = wrapTransactionHistory.getAssetType().name();
            investmentName = wrapTransactionHistory.getAssetType().getDisplayName();
        }
        else {
            investmentName = "-";
        }
        String assetCode = wrapTransactionHistory.getAssetCode();
        String assetName = wrapTransactionHistory.getAssetName();

        assetDetails.put("investmentName", investmentName);
        assetDetails.put("investmentType", investmentType);
        assetDetails.put("assetCode", assetCode);
        assetDetails.put("assetName", assetName);
        return assetDetails;
    }

    private String getTransactionDescription(TransactionHistory transaction) {
        StringBuilder builder = new StringBuilder(transaction.getBookingText());

        if (transaction.getDocDescription() != null) {
            builder.append(" - ").append(transaction.getDocDescription().toLowerCase());
        }

        if (transaction.getTransactionDescription() != null) {
            builder.append(". ").append(transaction.getTransactionDescription());
        }

        return builder.toString();
    }

    private Map<String, Object> getAmountDetails(TransactionHistory transaction) {
        Map<String, Object> amountDetails = new HashMap<String, Object>();

        Asset asset = transaction.getAsset();

        BigDecimal netAmount = null;
        BigDecimal quantity = null;

        String transactionType = transaction.getTransactionType();

        if (asset != null) {
            if (AssetType.CASH.equals(asset.getAssetType()) || AssetType.TERM_DEPOSIT.equals(asset.getAssetType())) {
                netAmount = transaction.getAmount();
                transactionType = getTransactionTypeForCashAsset(transaction);
            }
            else {
                quantity = transaction.getAmount();
                transactionType = getTransactionTypeForNonCashAsset(transaction);
            }
        }
        amountDetails.put("transactionType", transactionType);
        amountDetails.put("netAmount", netAmount);
        amountDetails.put("quantity", quantity);

        return amountDetails;
    }

    private String getTransactionTypeForCashAsset(TransactionHistory transaction) {
        String transactionType = transaction.getTransactionType();
        if (transaction.getTransactionType() == null) {
            if (transaction.getAmount() != null && transaction.getAmount().signum() != -1) {
                transactionType = TransactionType.DEPOSIT.getCode();
            }
            else if (transaction.getAmount() != null) {
                transactionType = TransactionType.PAYMENT.getCode();
            }
        }
        return transactionType;
    }

    private String getTransactionTypeForNonCashAsset(TransactionHistory transaction) {
        String transactionType = transaction.getTransactionType();
        if (transaction.getTransactionType() == null) {
            if (transaction.getAmount() != null && transaction.getAmount().signum() != -1) {
                transactionType = TransactionType.BUY.getCode();
            }
            else if (transaction.getAmount() != null) {
                transactionType = TransactionType.SELL.getCode();
            }
        }
        return transactionType;
    }

    private Map<String, String> getAssetDetails(TransactionHistory transaction) {
        Map<String, String> assetDetails = new HashMap<String, String>();

        AssetType assetType = getAssetType(transaction);
        Asset contAsset = transaction.getContAsset();

        String investmentCode = null;
        String investmentName = null;
        String investmentType = null;

        if (assetType != null) {
            investmentType = assetType.name();

            if (AssetType.CASH.equals(assetType)) {
                investmentName = transaction.getAsset().getAssetName();
            }
            else if (AssetType.OPTION.equals(assetType) || AssetType.BOND.equals(assetType)) {
                investmentName = AssetType.SHARE.getDisplayName();
            }
            else if (contAsset != null
                    && (AssetType.MANAGED_PORTFOLIO.equals(assetType) || AssetType.TAILORED_PORTFOLIO.equals(assetType))) {
                investmentCode = contAsset.getAssetCode();
                investmentName = contAsset.getAssetName();
            }
            else {
                investmentName = assetType.getDisplayName();
            }
        }

        setAssetCodeAndName(assetType, transaction, assetDetails);

        assetDetails.put("investmentCode", investmentCode);
        assetDetails.put("investmentName", investmentName);
        assetDetails.put("investmentType", investmentType);

        return assetDetails;
    }

    private void setAssetCodeAndName(AssetType assetType, TransactionHistory transaction, Map<String, String> assetDetails) {
        String assetCode = null;
        String assetName = null;

        if (AssetType.CASH == assetType) {
            if (transaction.getRefAsset() != null &&
                    (transaction.getBTOrderType() == BTOrderType.CORPORATE_ACTION || transaction.getBTOrderType() == BTOrderType.INCOME)) {
                assetCode = transaction.getRefAsset().getAssetCode();
                assetName = transaction.getRefAsset().getAssetName();
            }
        }
        else {
            assetCode = transaction.getAsset() != null ? transaction.getAsset().getAssetCode() : null;
            assetName = transaction.getPosName();
        }

        assetDetails.put("assetCode", assetCode);
        assetDetails.put("assetName", assetName);
    }

    private AssetType getAssetType(TransactionHistory transaction) {
        AssetType assetType = null;

        Asset asset = transaction.getAsset();
        Asset contAsset = transaction.getContAsset();

        if (asset != null && ContainerType.DIRECT.equals(transaction.getContType())) {
            assetType = asset.getAssetType();
        }
        else if (contAsset != null && ContainerType.MANAGED_PORTFOLIO.equals(transaction.getContType())) {
            assetType = contAsset.getAssetType();
        }

        return assetType;
    }

    // TODO moved javascript logic in here to enable reports to have the same
    // behaviour. It is expected
    // that this function will no longer be required when the screen moves over
    // to TransactionDto.
    private void processLinkedTransactions(List<TransactionHistoryDto> transactionDtos) {
        Collections.sort(transactionDtos, new TransactionComparator());

        TransactionHistoryDto previousTransaction = null;
        for (TransactionHistoryDto dto : transactionDtos) {
            boolean isNotLinked = dto.getTradeDate() == null ||
                    dto.getSettlementDate() == null ||
                    dto.getOrderId() == null ||
                    previousTransaction == null;
            if (isNotLinked || !dto.getTradeDate().equals(previousTransaction.getTradeDate())
                    || !dto.getSettlementDate().equals(previousTransaction.getSettlementDate())
                    || !dto.getOrderId().equals(previousTransaction.getOrderId())) {
                dto.setIsLinked(false);
            }
            else {
                dto.setIsLinked(true);
                previousTransaction.setIsLink(true);
            }
            dto.setIsLink(false);
            previousTransaction = dto;
        }
    }

    private boolean filterTransaction(TransactionHistory transaction) {
        Asset asset = transaction.getAsset();

        // Filter transactions based on:
        // 1. The asset's money account type
        // 2. Corporate action transaction with zero unit
        if (asset != null && asset.isIncome()) {
            return true;
        }

        return transaction.getBTOrderType() == BTOrderType.CORPORATE_ACTION && transaction.getAmount() != null &&
                BigDecimal.ZERO.compareTo(transaction.getAmount()) == 0;
    }

    private static class TransactionComparator implements Comparator<TransactionHistoryDto>, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(TransactionHistoryDto o1, TransactionHistoryDto o2) {
            return new CompareToBuilder().append(o2.getTradeDate(), o1.getTradeDate())
                    .append(o2.getSettlementDate(), o1.getSettlementDate()).append(o1.getOrderId(), o2.getOrderId())
                    .append(o1.getNetAmount(), o2.getNetAmount()).append(o1.getQuantity(), o2.getQuantity()).toComparison();
        }
    }
}
