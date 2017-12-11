package com.bt.nextgen.service.integration.corporateaction;

/**
 * Corporate action type
 */
public enum CorporateActionType {
    // VOLUNTARY CORPORATE ACTION TYPE
    MULTI_BLOCK("multi_block", "", CorporateActionGroup.VOLUNTARY),
    EXERCISE_RIGHTS("exe_right", "Exercise Rights", CorporateActionGroup.VOLUNTARY),
    SHARE_PURCHASE_PLAN("share_purch_plan", "Share Purchase Plan", CorporateActionGroup.VOLUNTARY, false),
    PRO_RATA_PRIORITY_OFFER("pro_rata_prio_offer", "Pro Rata Priority Offer", CorporateActionGroup.VOLUNTARY),
    EXERCISE_CALL_OPTION("exeopt_call", "Exercise Call Option", CorporateActionGroup.VOLUNTARY),
    //EXERCISE_CALL_OPTION_TWO_SEC("exeopt_call_2sec", "Exercise Call Option with 2 Underlyings", CorporateActionGroup.VOLUNTARY),
    EXERCISE_RIGHTS_WITH_OPT("exe_right_with_opt", "Exercise Rights", CorporateActionGroup.VOLUNTARY),
    BUY_BACK("po_dscnt_offer", "Off Market Buy Back", CorporateActionGroup.VOLUNTARY, false),
    NON_PRO_RATA_PRIORITY_OFFER("non_pro_rata_prio_offer", "Non Pro Rata Priority Offer", CorporateActionGroup.VOLUNTARY, false),

    // MANDATORY CORPORATE ACTION TYPE
    ASSIMILATION_FRACTION("assim_frac", "Assimilation", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.DISTRIBUTION),
    //LOT_BLOCK_POSITIONS("block_lot", "Lot Block Positions", CorporateActionGroup.MANDATORY),
    BONUS_FRACTION("bonus_fract", "Bonus Issue", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.DISTRIBUTION),
    CAPITAL_CALL_SHARE("captcall_share", "Capital Call", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.CAPITAL_CALL_SHARE),
    CAPITAL_INCREASE_OVER_SUBSCRIPTION_PROCESSING("captinc_oversubs_prc", "Capital Increase Oversubscription Processing",
            CorporateActionGroup.MANDATORY),
    //DIVIDEND_CASH("div_cash", "Dividend Cash", CorporateActionGroup.MANDATORY, null),
    DIVIDEND_CASH_RIGHT("div_cash_right", "Dividend Cash Right", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.DIV_CASH_RIGHT),
    DIVIDEND_CASH_VALUE_ADJUST("div_cash_valadj", "Dividend Cash with Value Adjust", CorporateActionGroup.MANDATORY),
    //DIVIDEND_REINVESTMENT_DIRECT("div_revst_direct", "Dividend Reinvestment Direct", CorporateActionGroup.MANDATORY),
    /*DIVIDEND_REINVESTMENT_MULTI_LOT_BLOCK("drip_block_multi_lot", "Dividend Reinvestment Multi Lot Block", CorporateActionGroup.MANDATORY,
            null),*/
    EXPIRATION_OLD_SECURITY("expir", "Expiration Old Security", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.EXPIRE),
    //FEE_CHARGING("fee_chrge", "Fee Charging", CorporateActionGroup.MANDATORY),
    FINAL_LIQUIDATION_PAYMENT_SHARE("finliqpay_share", "Final Liquidation", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.FINAL_LIQUIDATION),
    //INTEREST_NOMINAL("intr_nom", "Interest Nominal", CorporateActionGroup.MANDATORY),
    //INTEREST_NOMINAL_UNIT("intr_nom_unit", "Interest Nominal Unit", CorporateActionGroup.MANDATORY),
    LIQUIDATION_PAYMENT_SHARE("liqpay_share", "Liquidation Payment Only", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.LIQUIDATION),
    CASH_MERGER_ACQUISITION("merger_cash", "Merger", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.MERGER_CASH),
    MERGER_WITH_FRACTION("merger_frac", "Merger", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.DISTRIBUTION),
    MERGER_WITH_CASH_PAYMENT_AND_FRACTION("merger_pay_frac", "Merger", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.MERGER_WITH_PAYMENT_AND_FRACTION),
    NAME_CHANGE("namechg", "Name Change With Code Change", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.NAME_CHANGE),
    NAME_CHANGE_SAME_ASSET("namechg_same_asset", "Name Change With Code Change", CorporateActionGroup.MANDATORY),
    //NO_AFT("no_aft", "No AFT", CorporateActionGroup.MANDATORY),
    PEO_FOLLOW_OFFER("peo_follow", "Public Exchange Offer", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.DISTRIBUTION),
    //PO_LOT_DEBLOCK_POSITIONS("po_deblock_lot", "PO Lot Deblock Positions", CorporateActionGroup.MANDATORY),
    PURCHASE_OFFER_EXECUTION("po_exec", "Purchase Offer Execution", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.PAYMENT),
    REDEMPTION_AT_MATURITY("rdmpt_maturity", "Redemption at Maturity", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.REDEMPTION_AT_MATURITY),
    REDEMPTION_AT_MATURITY_UNIT("rdmpt_maturity_unit", "Redemption at Maturity Unit", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.REDEMPTION),
    REDEMPTION_PRIOR_TO_MATURITY("rdmpt_prior", "Redemption Prior to Maturity", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.REDEMPTION),
    DENOMINATION_REDUCTION("rednom", "Capital Repayment", CorporateActionGroup.MANDATORY),
    DENOMINATION_REDUCTION_SAME_ASSET("rednom_same", "Capital Repayment", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.DENOMINATION),
    //REVENUE_SHARES_WITH_VALUE_ADJUST("revn_share_adjust", "Revenue Shares with Value Adjust", CorporateActionGroup.MANDATORY),
    REVERSE_SPLIT_WITH_FRACTION("revs_split_frac", "Reverse Split", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.DISTRIBUTION),
    RIGHT_WITH_FRACTION_PAYMENT("right_frac", "Rights Distribution", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.DISTRIBUTION),
    SECURITY_EXCHANGE_TWO_SECURITIES_FRACTION("secxchg_2sec_frac", "Stapled/Destapled Security", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SECURITY_EXCHANGE_TWO_SECURITIES_PAYMENT("secxchg_2sec_pay", "Stapled/Destapled Security", CorporateActionGroup.MANDATORY),
    SECURITY_EXCHANGE_THREE_SECURITIES("secxchg_3sec", "Stapled/Destapled Security", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SECURITY_EXCHANGE_FOUR_SECURITIES("secxchg_4sec", "Stapled/Destapled Security", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SECURITY_EXCHANGE_FIVE_SECURITIES("secxchg_5sec", "Stapled/Destapled Security", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SECURITY_EXCHANGE_FRACTION("secxchg_frac", "Stapled/Destapled Security", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SECURITY_EXCHANGE_CASH_PAYMENT_AND_FRACTION("secxchg_pay_frac", "Stapled/Destapled Security", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SPIN_OFF("spinoff", "Spin-off/Demerger/Distribution In Species", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.DISTRIBUTION),
    SPLIT_WITH_FRACTION("split_frac", "Split", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.SPLIT_WITH_FRACTION),
    STAPLED_SECURITY_EVENT("tax_stpldsec", "Stapled/Destapled Security", CorporateActionGroup.MANDATORY),
    BUY_BACK_MANDATORY("po_dscnt_prc", "Off Market Buy Back", CorporateActionGroup.MANDATORY, CorporateActionSummaryTemplate.BUY_BACK),

    // Below are custom events and do not present in Avaloq
    SECURITY_EXCHANGE_CONVERSION("secxchg_frac_conv", "Conversion", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SECURITY_EXCHANGE_REINVESTMENT("secxchg_frac_revst", "Reinvestment", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SECURITY_EXCHANGE_EXCHANGE("secxchg_frac_secxchg", "Exchange", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SECURITY_EXCHANGE_STAPLE("secxchg_frac_stpl", "Stapled Security", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE),
    SECURITY_EXCHANGE_DESTAPLE("secxchg_frac_destpl", "Destapled Security", CorporateActionGroup.MANDATORY,
            CorporateActionSummaryTemplate.SECURITY_EXCHANGE);


    private String id;
    private String description;
    private CorporateActionGroup group;
    private CorporateActionSummaryTemplate summaryTemplate;
    private boolean availableForIm;

    CorporateActionType(String id, String description, CorporateActionGroup group) {
        this(id, description, group, null, true);
    }

    CorporateActionType(String id, String description, CorporateActionGroup group, CorporateActionSummaryTemplate summaryTemplate) {
        this(id, description, group, summaryTemplate, true);
    }

    CorporateActionType(String id, String description, CorporateActionGroup group, boolean availableForIm) {
        this(id, description, group, null, availableForIm);
    }

    CorporateActionType(String id, String description, CorporateActionGroup group, CorporateActionSummaryTemplate summaryTemplate,
                        boolean availableForIm) {
        this.id = id;
        this.description = description;
        this.group = group;
        this.summaryTemplate = summaryTemplate;
        this.availableForIm = availableForIm;
    }

    /**
     * Converts Avaloq internal ID to CorporateActionType
     *
     * @param id Avaloq internal ID
     * @return CorporateActionType
     */
    public static CorporateActionType forId(String id) {
        for (CorporateActionType caType : CorporateActionType.values()) {
            if (caType.id.equals(id)) {
                return caType;
            }
        }

        return null;
    }

    /**
     * Converts Avaloq internal name to CorporateActionType
     *
     * @param name Avaloq internal ID
     * @return CorporateActionType
     */
    public static CorporateActionType forName(String name) {
        for (CorporateActionType caType : CorporateActionType.values()) {
            if (caType.name().equals(name)) {
                return caType;
            }
        }

        return null;
    }

    /**
     * Avaloq internal ID
     *
     * @return avalog internal ID
     */
    public String getId() {
        return id;
    }

    /**
     * Name of this enum.  Equivalent to this.name()
     *
     * @return name of this enum
     */
    public String getCode() {
        return this.name();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return corporate action group
     */
    public CorporateActionGroup getGroup() {
        return group;
    }

    /**
     * @return summary template
     */
    public CorporateActionSummaryTemplate getSummaryTemplate() {
        return summaryTemplate;
    }

    public boolean isAvailableForIm() {
        return availableForIm;
    }

    public boolean isAvailable(boolean isIm) {
        return isIm ? availableForIm : true;
    }
}


