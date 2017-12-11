package com.bt.nextgen.service.integration.investment;

/**
 * <p>
 * Representation of investment styles.
 * </p>
 * Static code category <code>btfg$ips_invst_style</code>
 */
@SuppressWarnings("checkstyle:com.puppycrawl.tools.checkstyle.checks.whitespace.NoLineWrapCheck")
public enum InvestmentStyle {    
    ACTIVE("Active", "activ"),
    AUSTRALIAN_GOVT_BONDS("Australian Govt Bonds", "aus_gov_bond"),
    BALANCED("Balanced","bald"),
    COMMODITIES("Commodities","comdty"),
    CONCENTRATED("Concentrated","concentrate"),
    CONSERVATIVE("Conservative","conserv"),
    CORPORATE_BONDS("Corporate Bonds","corp_bond"),
    ETHICAL("Ethical/SRI","ethical_sri"),
    GLOBAL("Global","glo"),
    GROWTH("Growth","growth"),
    HEDGED("Hedged","hdg"),
    HIGH_GROWTH("High Growth","high_growth"),
    HYBRIDS("Hybrids","hybrid"),
    INCOME("Income","income"),
    INCOME_HIGH_YIELD("Income/High Yield","income_high_yield"),
    LARGE_CAP("Large Cap","large_cap"),
    LIFE_STAGE("Life Stage","life_stage"),
    MID_CAP("Mid Cap","mid_cap"),
    MODERATE("Moderate","moderate"),
    MULTI_MANAGER("Multi-Manager","multi_mgr"),
    MULTI_MANAGER_HEDGE_FUNDS("Multi-manager Hedge Funds","multi_mgr_hdg"),
    OTHER("Other", "btfg$other"),
    PASSIVE("Passive","passiv"),
    REGIONAL("Regional","region"),
    SECTOR("Sector","sctr"),
    SMALL_CAP("Small Cap","small_cap"),
    SOVEREIGN_BONDS("Sovereign Bonds","sovereign_bond"),
    TAX_EFFICIENT("Tax Efficient","tax_efficient"),
    UNHEDGED("Un-hedged","un_hdg"),
    VALUE("Value","val"),
    DEFENSIVE("Defensive","btfg$defnv"),
    SUSTAINABLE("Sustainable","btfg$sustain"),
    SOCIALLY_RESPONSIBLE("Socially Responsible","btfg$socially_resp"),
    NEUTRAL("Neutral","btfg$neutral"),
    INDEX("Index","btfg$idx"),
    ESG("ESG","btfg$esg");

    private String description = "";
    private String code = "";

    InvestmentStyle(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public String getIntlId() {
        return code;
    }

    public static InvestmentStyle forIntlId(String intlId) {
        for (InvestmentStyle style : values()) {
            if (style.getIntlId().equals(intlId)) {
                return style;
            }
        }
        return null;
    }

    public static InvestmentStyle forDescription(String description) {
        for (InvestmentStyle style : values()) {
            if (style.getDescription().equals(description)) {
                return style;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return code;
    }
}
