package com.bt.nextgen.api.statements.model;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.List;

public class StatementDto extends BaseDto implements KeyedDto<StatementKey> {

    private AccountKey accountKey;
    private StatementKey statementKey;
    private String statementName;
    private String statementType;
    private BigInteger size;
    private byte data[];
    private List<SupplimentaryDocument> supplimentaryDocuments;
    private String accountId;
    private String accountNumber;
    private String accountName;
    private String accountType;
    private String adviserId;
    private String adviserName;
    private String productId;
    private String productName;
    private DateTime periodStartDate;
    private DateTime periodEndDate;
    private DateTime generationDate;
    private String extensionType;

    public StatementDto(AccountKey accountKey, StatementKey statementKey, String statementName, String statementType,
            BigInteger size, byte data[], List<SupplimentaryDocument> supplimentaryDocuments, String accountId,
            String accountNumber, String accountName, String accountType, String adviserId, String adviserName, String productId,
            String productName, DateTime periodStartDate, DateTime periodEndDate, DateTime generationDate, String extensionType) {
        super();
        this.accountKey = accountKey;
        this.statementKey = statementKey;
        this.statementName = statementName;
        this.statementType = statementType;
        this.size = size;
        this.data = data;
        this.supplimentaryDocuments = supplimentaryDocuments;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.accountType = accountType;
        this.adviserId = adviserId;
        this.adviserName = adviserName;
        this.productId = productId;
        this.productName = productName;
        this.periodStartDate = periodStartDate;
        this.periodEndDate = periodEndDate;
        this.generationDate = generationDate;
        this.extensionType = extensionType;
    }

    public AccountKey getAccountKey() {
        return accountKey;
    }

    @Override
    public StatementKey getKey() {
        return statementKey;
    }

    public String getStatementName() {
        return statementName;
    }

    public String getStatementType() {
        return statementType;
    }

    public BigInteger getSize() {
        return size;
    }

    public byte[] getData() {
        return data;
    }

    public StatementKey getStatementKey() {
        return statementKey;
    }

    public List<SupplimentaryDocument> getSupplimentaryDocuments() {
        return supplimentaryDocuments;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAdviserId() {
        return adviserId;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    /**
     * @return the periodStartDate
     */
    public DateTime getPeriodStartDate() {
        return periodStartDate;
    }

    /**
     * @return the periodEndDate
     */
    public DateTime getPeriodEndDate() {
        return periodEndDate;
    }

    public String getExtensionType() {
        return extensionType;
    }

    public void setExtensionType(String extensionType) {
        this.extensionType = extensionType;
    }

    public DateTime getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(DateTime generationDate) {
        this.generationDate = generationDate;
    }
}
