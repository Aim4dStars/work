package com.bt.nextgen.api.trading.v1.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.util.List;

public class AvailableAssetInfoDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey accountKey;

    // cache info
    private DateTime cacheAssetLastRefreshed;
    private DateTime cacheAssetLastUpdated;
    private long cacheAssetCount;
    private DateTime cacheTdLastRefreshed;
    private DateTime cacheTdLastUpdated;
    private long cacheTdCount;
    private DateTime cacheOldAalLastRefreshed;
    private DateTime cacheOldAalLastUpdated;
    private long cacheOldAalCount;
    private DateTime cacheBrokerProductAssetsLastRefreshed;
    private DateTime cacheBrokerProductAssetsLastUpdated;
    private long cacheBrokerProductAssetsCount;
    private DateTime cacheAalIssuersLastRefreshed;
    private DateTime cacheAalIssuersLastUpdated;
    private long cacheAalIssuersCount;
    private DateTime cacheAalIndexesLastRefreshed;
    private DateTime cacheAalIndexesLastUpdated;
    private long cacheAalIndexesCount;
    private DateTime cacheIndexAssetsLastRefreshed;
    private DateTime cacheIndexAssetsLastUpdated;
    private long cacheIndexAssetsCount;

    private DateTime bankDate;

    // account/oe info
    private String accountId;
    private String accountBpNumber;
    private String accountName;
    private String adviserId;
    private String adviserOEId;
    private String adviserName;
    private String dealerId;
    private String dealerOEId;
    private String superDealerId;

    private String directProductId;

    // aal/td service info
    private long aalServiceCount;
    private long tdRatesCount;

    // profile info
    private String activeProfileId;
    private List<String> availableProfileIds;
    private String profileUserId;
    private String userExperience;
    private String adviserUserExperience;
    private List<String> functionalRoles;

    // tradable asset info
    private long tradableAssetTypesCount;
    private List<TradeAssetTypeDto> tradableAssetTypes;
    private long tradableAssetsCount;

    public DateTime getCacheAssetLastRefreshed() {
        return cacheAssetLastRefreshed;
    }

    public void setCacheAssetLastRefreshed(DateTime cacheAssetLastRefreshed) {
        this.cacheAssetLastRefreshed = cacheAssetLastRefreshed;
    }

    public DateTime getCacheAssetLastUpdated() {
        return cacheAssetLastUpdated;
    }

    public void setCacheAssetLastUpdated(DateTime cacheAssetLastUpdated) {
        this.cacheAssetLastUpdated = cacheAssetLastUpdated;
    }

    public long getCacheAssetCount() {
        return cacheAssetCount;
    }

    public void setCacheAssetCount(long cacheAssetCount) {
        this.cacheAssetCount = cacheAssetCount;
    }

    public DateTime getCacheTdLastRefreshed() {
        return cacheTdLastRefreshed;
    }

    public void setCacheTdLastRefreshed(DateTime cacheTdLastRefreshed) {
        this.cacheTdLastRefreshed = cacheTdLastRefreshed;
    }

    public DateTime getCacheTdLastUpdated() {
        return cacheTdLastUpdated;
    }

    public void setCacheTdLastUpdated(DateTime cacheTdLastUpdated) {
        this.cacheTdLastUpdated = cacheTdLastUpdated;
    }

    public long getCacheTdCount() {
        return cacheTdCount;
    }

    public void setCacheTdCount(long cacheTdCount) {
        this.cacheTdCount = cacheTdCount;
    }

    public DateTime getCacheOldAalLastRefreshed() {
        return cacheOldAalLastRefreshed;
    }

    public void setCacheOldAalLastRefreshed(DateTime cacheOldAalLastRefreshed) {
        this.cacheOldAalLastRefreshed = cacheOldAalLastRefreshed;
    }

    public DateTime getCacheOldAalLastUpdated() {
        return cacheOldAalLastUpdated;
    }

    public void setCacheOldAalLastUpdated(DateTime cacheOldAalLastUpdated) {
        this.cacheOldAalLastUpdated = cacheOldAalLastUpdated;
    }

    public long getCacheOldAalCount() {
        return cacheOldAalCount;
    }

    public void setCacheOldAalCount(long cacheOldAalCount) {
        this.cacheOldAalCount = cacheOldAalCount;
    }

    public DateTime getCacheBrokerProductAssetsLastRefreshed() {
        return cacheBrokerProductAssetsLastRefreshed;
    }

    public void setCacheBrokerProductAssetsLastRefreshed(DateTime cacheBrokerProductAssetsLastRefreshed) {
        this.cacheBrokerProductAssetsLastRefreshed = cacheBrokerProductAssetsLastRefreshed;
    }

    public DateTime getCacheBrokerProductAssetsLastUpdated() {
        return cacheBrokerProductAssetsLastUpdated;
    }

    public void setCacheBrokerProductAssetsLastUpdated(DateTime cacheBrokerProductAssetsLastUpdated) {
        this.cacheBrokerProductAssetsLastUpdated = cacheBrokerProductAssetsLastUpdated;
    }

    public long getCacheBrokerProductAssetsCount() {
        return cacheBrokerProductAssetsCount;
    }

    public void setCacheBrokerProductAssetsCount(long cacheBrokerProductAssetsCount) {
        this.cacheBrokerProductAssetsCount = cacheBrokerProductAssetsCount;
    }

    public DateTime getCacheAalIssuersLastRefreshed() {
        return cacheAalIssuersLastRefreshed;
    }

    public void setCacheAalIssuersLastRefreshed(DateTime cacheAalIssuersLastRefreshed) {
        this.cacheAalIssuersLastRefreshed = cacheAalIssuersLastRefreshed;
    }

    public DateTime getCacheAalIssuersLastUpdated() {
        return cacheAalIssuersLastUpdated;
    }

    public void setCacheAalIssuersLastUpdated(DateTime cacheAalIssuersLastUpdated) {
        this.cacheAalIssuersLastUpdated = cacheAalIssuersLastUpdated;
    }

    public long getCacheAalIssuersCount() {
        return cacheAalIssuersCount;
    }

    public void setCacheAalIssuersCount(long cacheAalIssuersCount) {
        this.cacheAalIssuersCount = cacheAalIssuersCount;
    }

    public DateTime getCacheAalIndexesLastRefreshed() {
        return cacheAalIndexesLastRefreshed;
    }

    public void setCacheAalIndexesLastRefreshed(DateTime cacheAalIndexesLastRefreshed) {
        this.cacheAalIndexesLastRefreshed = cacheAalIndexesLastRefreshed;
    }

    public DateTime getCacheAalIndexesLastUpdated() {
        return cacheAalIndexesLastUpdated;
    }

    public void setCacheAalIndexesLastUpdated(DateTime cacheAalIndexesLastUpdated) {
        this.cacheAalIndexesLastUpdated = cacheAalIndexesLastUpdated;
    }

    public long getCacheAalIndexesCount() {
        return cacheAalIndexesCount;
    }

    public void setCacheAalIndexesCount(long cacheAalIndexesCount) {
        this.cacheAalIndexesCount = cacheAalIndexesCount;
    }

    public DateTime getCacheIndexAssetsLastRefreshed() {
        return cacheIndexAssetsLastRefreshed;
    }

    public void setCacheIndexAssetsLastRefreshed(DateTime cacheIndexAssetsLastRefreshed) {
        this.cacheIndexAssetsLastRefreshed = cacheIndexAssetsLastRefreshed;
    }

    public DateTime getCacheIndexAssetsLastUpdated() {
        return cacheIndexAssetsLastUpdated;
    }

    public void setCacheIndexAssetsLastUpdated(DateTime cacheIndexAssetsLastUpdated) {
        this.cacheIndexAssetsLastUpdated = cacheIndexAssetsLastUpdated;
    }

    public long getCacheIndexAssetsCount() {
        return cacheIndexAssetsCount;
    }

    public void setCacheIndexAssetsCount(long cacheIndexAssetsCount) {
        this.cacheIndexAssetsCount = cacheIndexAssetsCount;
    }

    public DateTime getBankDate() {
        return bankDate;
    }

    public void setBankDate(DateTime bankDate) {
        this.bankDate = bankDate;
    }

    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountBpNumber() {
        return accountBpNumber;
    }

    public void setAccountBpNumber(String accountBpNumber) {
        this.accountBpNumber = accountBpNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAdviserId() {
        return adviserId;
    }

    public void setAdviserId(String adviserId) {
        this.adviserId = adviserId;
    }

    public String getAdviserOEId() {
        return adviserOEId;
    }

    public void setAdviserOEId(String adviserOEId) {
        this.adviserOEId = adviserOEId;
    }

    public String getAdviserName() {
        return adviserName;
    }

    public void setAdviserName(String adviserName) {
        this.adviserName = adviserName;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getDealerOEId() {
        return dealerOEId;
    }

    public void setDealerOEId(String dealerOEId) {
        this.dealerOEId = dealerOEId;
    }

    public String getSuperDealerId() {
        return superDealerId;
    }

    public void setSuperDealerId(String superDealerId) {
        this.superDealerId = superDealerId;
    }

    public String getDirectProductId() {
        return directProductId;
    }

    public void setDirectProductId(String directProductId) {
        this.directProductId = directProductId;
    }

    public long getAalServiceCount() {
        return aalServiceCount;
    }

    public void setAalServiceCount(long aalServiceCount) {
        this.aalServiceCount = aalServiceCount;
    }

    public long getTdRatesCount() {
        return tdRatesCount;
    }

    public void setTdRatesCount(long tdRatesCount) {
        this.tdRatesCount = tdRatesCount;
    }

    public String getActiveProfileId() {
        return activeProfileId;
    }

    public void setActiveProfileId(String activeProfileId) {
        this.activeProfileId = activeProfileId;
    }

    public List<String> getAvailableProfileIds() {
        return availableProfileIds;
    }

    public void setAvailableProfileIds(List<String> availableProfileIds) {
        this.availableProfileIds = availableProfileIds;
    }

    public String getProfileUserId() {
        return profileUserId;
    }

    public void setProfileUserId(String profileUserId) {
        this.profileUserId = profileUserId;
    }

    public String getUserExperience() {
        return userExperience;
    }

    public void setUserExperience(String userExperience) {
        this.userExperience = userExperience;
    }

    public String getAdviserUserExperience() {
        return adviserUserExperience;
    }

    public void setAdviserUserExperience(String adviserUserExperience) {
        this.adviserUserExperience = adviserUserExperience;
    }

    public List<String> getFunctionalRoles() {
        return functionalRoles;
    }

    public void setFunctionalRoles(List<String> functionalRoles) {
        this.functionalRoles = functionalRoles;
    }

    public long getTradableAssetTypesCount() {
        return tradableAssetTypesCount;
    }

    public void setTradableAssetTypesCount(long tradableAssetTypesCount) {
        this.tradableAssetTypesCount = tradableAssetTypesCount;
    }

    public List<TradeAssetTypeDto> getTradableAssetTypes() {
        return tradableAssetTypes;
    }

    public void setTradableAssetTypes(List<TradeAssetTypeDto> tradableAssetTypes) {
        this.tradableAssetTypes = tradableAssetTypes;
    }

    public long getTradableAssetsCount() {
        return tradableAssetsCount;
    }

    public void setTradableAssetsCount(long tradableAssetsCount) {
        this.tradableAssetsCount = tradableAssetsCount;
    }

    @Override
    public AccountKey getKey() {
        return accountKey;
    }
}
