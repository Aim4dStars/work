package com.bt.nextgen.service.avaloq.contributionhistory;

/**
 * Created by M044576 on 29/06/2017.
 */
public enum WrapContributionTypeMapping {


    SPOUSE("Spouse", "spouse"),
    PERSONAL("Personal (tax deduction not claimed)", "prsnl_sav_nclaim"),
    PERSONAL_INJURY("Personal injury", "pers_injury_nclaim"),
    DIRECTED_TERMINATION_PAYMENT_TAXABLE("Directed termination payment (taxable component)",  "pers_injury_nclaim"),
    DIRECTED_TERMINATION_PAYMENT_TAX_FREE("Directed termination payment (tax free component)",  "pers_injury_nclaim"),
    FOREIGN_SUPER_TAXABLE("Foreign super (taxable amount)", "appl_fund_earning"),
    FOREIGN_SUPER_TAX_FREE("Foreign super (tax free amount)", "nassble"),
    CGT("CGT - small business 15 year exemption amount", "pers_15y_leq_cap_nclaim"),
    GOVERNMENT_CO_CONTRIBUTION("Government co-contribution","gov"),
    CGT_RETIREMENT_EXEMPTION("CGT - small business retirement exemption amount", "pers_cgt_retir_leq_cap_nclaim"),
    EMPLOYER("Employer","employer"),
    PERSONAL_TAX_DEDUCTION_CLAIMED("Personal (tax deduction claimed)","prsnl_sav_claim");

    private String contributionTypeName;
    private String intlId;

    WrapContributionTypeMapping(String contributionTypeName, String intlId) {
        this.contributionTypeName = contributionTypeName;
        this.intlId = intlId;
    }

    public String getIntlId() {
        return intlId;
    }

    public static String getIntlIdByString(String contributionTypeName) {
        for (WrapContributionTypeMapping wrapName : WrapContributionTypeMapping.values()) {
            if (contributionTypeName.equalsIgnoreCase(wrapName.contributionTypeName))
                return wrapName.getIntlId();
        }
        return null;
    }

}
