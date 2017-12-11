package com.bt.nextgen.api.profile.v1.model;

public enum JobRoleConverter {

    ADVISER("Adviser"),
    ASSISTANT("Assistant"),
    PARAPLANNER("Paraplanner"),
    DEALER_GROUP_MANAGER("DG Manager"),
    PRACTICE_MANAGER("Practice Manager"),
    INVESTOR("Investor"),
    INVESTMENT_MANAGER("Investment Mgr."),
    FUND_MANAGER("Fund Manager"),
    ADVICE_AND_SCALABLE_ADVICE("Advice and Scalable Advice"),
    AUDIT("Audit"),
    DISTRIBUTION("Distribution"),
    FINANCE("Finance"),
    MARKETING("Marketing"),
    PRODUCT("Product"),
    RESEARCH("Research"),
    SERVICE_AND_OPERATION("Service and Operation"),
    TAX_AND_ACCOUNTING("Tax and Accounting"),
    IT_SUPPORT("IT Support"),
    BACK_OFFICE("Back Office"),
    TECHNICAL("Technical"),
    ACCOUNTANT("Accountant"),
    ACCOUNTANT_SUPPORT_STAFF("Accountant Assistant"),
    TRUSTEE("Trustee"),
    OTHER("Other"),
    PORTFOLIO_MANAGER("Portfolio Manager");

    private String description;

    private JobRoleConverter(String description)
    {
        this.description = description;
    }

    @Override
    public String toString()
    {
        return description;
    }

    public static JobRoleConverter forCode(String code)
    {
        for (JobRoleConverter roleType : JobRoleConverter.values())
        {
            if (roleType.description.equalsIgnoreCase(code))
            {
                return roleType;
            }
        }
        return OTHER;
    }

}
