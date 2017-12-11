package com.bt.nextgen.api.profile.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;

import java.util.List;

public class ProfileDetailsDto extends BaseDto {
    private List<ProfileRoles> roles;
    private List<AggregatedRoleDto> aggregatedRoles;
    private String name;
    private String id;
    private String personId;
    private String homePage;
    private String logoUrl;
    private String logoTitle;
    private boolean clientMessage;
    private boolean canNavigate;
    private boolean whatsNew;
    private boolean intermediary;
    private int intermediaryCount;
    private String accountId;
    private String referringSystem;
    private boolean isWestpacAdviser;
    private boolean isWestpacBrandedAdviserVal;
    private String dealerGroupName;
    private String positionId;
    private boolean hasMultipleAccounts;
    private String originatingSystem;
    private UserExperience userExperience;
    private boolean hasAsimAccounts;
    private boolean hasDirectAccounts;

    /** Flag indicating whether or not this user's dealer group allows offline approvals. */
    private boolean offlineApproval;

    public List<ProfileRoles> getRoles() {
        return roles;
    }

    public void setRoles(List<ProfileRoles> roles) {
        this.roles = roles;
    }

    public List<AggregatedRoleDto> getAggregatedRoles() {
        return aggregatedRoles;
    }

    public void setAggregatedRoles(List<AggregatedRoleDto> aggregatedRoles) {
        this.aggregatedRoles = aggregatedRoles;
    }

    public boolean isClientMessage() {
        return clientMessage;
    }

    public void setClientMessage(boolean clientMessage) {
        this.clientMessage = clientMessage;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getReferringSystem() {
        return referringSystem;
    }

    public void setReferringSystem(String referringSystem) {
        this.referringSystem = referringSystem;
    }

    public boolean isCanNavigate() {
        return canNavigate;
    }

    public void setCanNavigate(boolean canNavigate) {
        this.canNavigate = canNavigate;
    }

    public boolean isIntermediary() {
        return intermediary;
    }

    public void setIntermediary(boolean intermediary) {
        this.intermediary = intermediary;
    }

    public int getIntermediaryCount() {
        return intermediaryCount;
    }

    public void setIntermediaryCount(int intermediaryCount) {
        this.intermediaryCount = intermediaryCount;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDealerGroupName() {
        return dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    public boolean isWestpacAdviser() {
        return isWestpacAdviser;
    }

    public void setWestpacAdviser(boolean isWestpacAdviser) {
        this.isWestpacAdviser = isWestpacAdviser;
    }

    public boolean isWestpacBrandedAdviser() {
        return isWestpacBrandedAdviserVal;
    }

    public void setWestpacBrandedAdviser(boolean isWestpacBrandedAdviser) {
        this.isWestpacBrandedAdviserVal = isWestpacBrandedAdviser;
    }

    public boolean isWhatsNew() {
        return whatsNew;
    }

    public void setWhatsNew(boolean whatsNew) {
        this.whatsNew = whatsNew;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public void setHasMultipleAccounts(boolean hasMultipleAccounts) {
        this.hasMultipleAccounts = hasMultipleAccounts;
    }

    public boolean getHasMultipleAccounts() {
        return hasMultipleAccounts;
    }

    public String getLogoTitle() {
        return logoTitle;
    }

    public void setLogoTitle(String logoTitle) {
        this.logoTitle = logoTitle;
    }

    public String getOriginatingSystem() {
        return originatingSystem;
    }

    public void setOriginatingSystem(String originatingSystem) {
        this.originatingSystem = originatingSystem;
    }

    public String getUserExperience() {
        return userExperience != null ? userExperience.name() : null;
    }

    public void setUserExperience(UserExperience userExperience) {
        this.userExperience = userExperience;
    }

    public String getUserExperienceDisplay() {
        return userExperience != null ? userExperience.getDisplayName() : "";
    }

    public boolean isOfflineApproval() {
        return offlineApproval;
    }

    public void setOfflineApproval(boolean offlineApproval) {
        this.offlineApproval = offlineApproval;
    }

    public void setHasAsimAccounts(boolean hasAsimAccounts) {
        this.hasAsimAccounts = hasAsimAccounts;
    }

    public boolean getHasAsimAccounts() {
        return hasAsimAccounts;
    }

    public boolean getHasDirectAccounts() {
        return hasDirectAccounts;
    }

    public void setHasDirectAccounts(boolean hasDirectAccounts) {
        this.hasDirectAccounts = hasDirectAccounts;
    }
}
