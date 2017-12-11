package com.bt.nextgen.api.profile.v1.model;

public enum HomePageEnum
{
    INVESTOR_EXPERIENCE("investorExperience"),
    ACT_DASHBOARD("actDashboard"),
    MODEL_LIST("modelStatus"),
    CLIENT_ACCOUNT_OVERVIEW("clientOverview"),
    MONITOR_DASHBOARD("monitorDashboard"),
    PORTFOLIO_VALUATION("pv"),
    OTHER("home"),
    ERROR_PAGE("error"),
    HOLDING("holding"),
    NON_APPROVER_TNCS("nonApproverTncs"),
    ACCOUNT_APPROVAL("accountApproval"),
    CLIENT_LIST("clientList"),
    WITHDRAWN("withdrawn"),
	INTERMEDIARY_TERMS_AND_CONDITIONS("intermediaryTncs"),
    DIRECT_ONBOARDING("directOnboarding"),
    TRUSTEE_CORPORATE_ACTIONS("trusteeCorporateActions"),
    ACCOUNT_DETAILS("accountDetails");

    private final String name;

    private HomePageEnum(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }

}