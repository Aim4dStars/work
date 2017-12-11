package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.util.rebalance.ModelAccountTypeHelper;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;
import com.bt.nextgen.service.integration.product.Product;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class ModelPortfolioRebalanceAccountDto extends BaseDto implements KeyedDto<AccountRebalanceKey> {
    private AccountRebalanceKey key;
    private OrderKey orderKey;
    private BrokerUser adviser;
    private String accountName;
    private String accountNumber;
    private String accountType;
    private String productName;
    private BigDecimal modelValue;
    private DateTime lastRebalance;
    private Integer assetClassBreach;
    private Integer toleranceBreach;
    private Integer buys;
    private Integer sells;
    private ExclusionStatus exclusionStatus;
    private String exclusionReason;

    public ModelPortfolioRebalanceAccountDto(String ipsId, RebalanceAccount rebalance, WrapAccount account, Product product,
            BrokerUser broker, ExclusionStatus exclusionStatus, String exclusionReason) {
        this.key = new AccountRebalanceKey(ipsId, EncodedString.fromPlainText(account.getAccountKey().getId()).toString());
        this.orderKey = new OrderKey(rebalance.getRebalDocId());
        this.accountName = account.getAccountName();
        this.accountNumber = account.getAccountNumber();
        this.accountType = ModelAccountTypeHelper.getAccountTypeDescription(account);
        this.productName = product == null ? "" : product.getProductName();
        this.adviser = broker;
        this.modelValue = rebalance.getValue();
        this.lastRebalance = null;
        this.assetClassBreach = rebalance.getAssetClassBreach();
        this.toleranceBreach = rebalance.getToleranceBreach();
        this.buys = rebalance.getEstimatedBuys();
        this.sells = rebalance.getEstimatedSells();
        this.exclusionStatus = exclusionStatus;
        this.exclusionReason = exclusionReason;
    }

    @Override
    public AccountRebalanceKey getKey() {
        return key;
    }

    public OrderKey getOrderKey() {
        return orderKey;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getProductName() {
        return productName;
    }

    public String getAdviserName() {
        StringBuilder builder = new StringBuilder();
        if (adviser != null) {
            if (!StringUtils.isBlank(adviser.getFirstName())) {
                builder.append(adviser.getFirstName());
            }
            if (!StringUtils.isBlank(adviser.getMiddleName())) {
                builder.append(" ");
                builder.append(adviser.getMiddleName());
            }
            if (!StringUtils.isBlank(adviser.getLastName())) {
                builder.append(" ");
                builder.append(adviser.getLastName());
            }
        }
        return builder.toString();
    }

    public String getAdviserNumber() {
        if (adviser != null) {
            return adviser.getBankReferenceKey().getId();
        }
        return null;
    }

    public BigDecimal getModelValue() {
        return modelValue;
    }

    public DateTime getLastRebalance() {
        return lastRebalance;
    }

    public Integer getAssetClassBreach() {
        return assetClassBreach;
    }

    public Integer getToleranceBreach() {
        return toleranceBreach;
    }

    public Integer getBuys() {
        return buys;
    }

    public Integer getSells() {
        return sells;
    }

    public String getExclusionReason() {
        return exclusionReason;
    }

    public ExclusionStatus getExclusionStatus() {
        return exclusionStatus;
    }
}
