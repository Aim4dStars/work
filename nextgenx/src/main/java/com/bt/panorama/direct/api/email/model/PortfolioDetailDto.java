package com.bt.panorama.direct.api.email.model;

import java.util.List;


public class PortfolioDetailDto {

    private String managedPortfolioUrl;
    private String portfolioType;
    private String customerName;
    private String email;
    private List<String> friendEmails;

    public PortfolioDetailDto() {
    }

    public PortfolioDetailDto(String managedPortfolioUrl, String portfolioType, String customerName, String email, List<String> friendEmails) {
        this.managedPortfolioUrl = managedPortfolioUrl;
        this.portfolioType = portfolioType;
        this.customerName = customerName;
        this.email = email;
        this.friendEmails = friendEmails;
    }

    public String getManagedPortfolioUrl() {
        return managedPortfolioUrl;
    }

    public void setManagedPortfolioUrl(String managedPortfolioUrl) {
        this.managedPortfolioUrl = managedPortfolioUrl;
    }

    public String getPortfolioType() {
        return portfolioType;
    }

    public void setPortfolioType(String portfolioType) {
        this.portfolioType = portfolioType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFriendEmails() {
        return friendEmails;
    }

    public void setFriendEmails(List<String> friendEmails) {
        this.friendEmails = friendEmails;
    }
}
