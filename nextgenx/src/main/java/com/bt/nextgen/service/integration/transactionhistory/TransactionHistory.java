package com.bt.nextgen.service.integration.transactionhistory;

import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionHistory {
    Integer getEvtId();

    BigDecimal getCoprSeqNr();

    String getDocId();

    String getAccountId();

    void setAccountId(String accountId);

    String getMetaType();

    String getOrderType();

    String getTransactionType();

    void setTransactionType(String transactionType);

    BigDecimal getBalance();

    void setBalance(BigDecimal balance);

    BigDecimal getClosingBalance();

    BigDecimal getAmount();

    BigDecimal getNetAmount();

    DateTime getEffectiveDate();

    DateTime getValDate();

    DateTime getClearDate();

    String getPayerName();

    String getPayeeName();

    String getPayerBsb();

    String getPayeeBsb();

    String getPayerAccount();

    String getPayeeAccount();

    String getPayeeBillerCode();

    String getPayeeCustrRef();

    String getBookingText();

    String getTransactionDescription();

    String getDocDescription();

    String getStatus();

    void setStatus(String status);

    ContainerType getContType();

    String getContId();

    String getPosId();

    String getPosName();

    String getContAssetId();

    String getPosAssetId();

    String getRefAssetId();

    Asset getContAsset();

    void setContAsset(Asset contAsset);

    Asset getAsset();

    void setAsset(Asset asset);

    Asset getRefAsset();

    void setRefAsset(Asset asset);

    boolean isCleared();

    void setCleared(boolean isCleared);

    boolean isSystemTransaction();

    void setSystemTransaction(boolean isSystemTransaction);

    CashCategorisationType getCashCategorisationType();

    BTOrderType getBTOrderType();

    Origin getOrigin();

    List<TransactionSubType> getTransactionSubTypes();

    boolean isReversal();

    String getThirdPartySystem();
}