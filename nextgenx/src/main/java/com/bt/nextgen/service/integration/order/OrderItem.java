package com.bt.nextgen.service.integration.order;

import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface OrderItem {
    public String getOrderId();

    public String getOrderType();

    public BigDecimal getAmount();

    public AssetType getAssetType();

    public String getAssetId();

    public Boolean getIsFull();

    public SubAccountKey getSubAccountKey();

    public List<Pair<String, BigDecimal>> getFundsSource();

    public String getDistributionMethod();

    public BigInteger getUnits();

    public BigDecimal getPrice();

    public String getExpiry();

    public PriceType getPriceType();

    public String getBankClearNumber();

    public String getPayerAccount();

    public String getFirstNotification();

    public List<ModelPreferenceAction> getPreferences();

    public Map<FeesType, List<FeesComponents>> getFees();

    public IncomePreference getIncomePreference();
}
