package com.bt.nextgen.reports.investmentoptions;

/**
 * The Class InvestmentOptionsBookletAssetData represents the asset properties that are necessary to display in the Investment
 * Options Booklet.
 *
 * @author M040005
 */
@SuppressWarnings({"squid:S00107"})
public class InvestmentOptionsBookletAssetData {

    /**
     * The asset code.
     */
    private final String assetCode;

    /**
     * The asset name.
     */
    private final String assetName;

    /**
     * The fee measure.
     */
    private final String feeMeasure;

    /**
     * The weighted icr.
     */
    private final String weightedIcr;

    /**
     * The risk measure.
     */
    private final String riskMeasure;

    /**
     * The asset class.
     */
    private final String assetClass;

    /**
     * The asset sub-class.
     */
    private final String assetSubClass;

    /**
     * The fund manager.
     */
    private final String fundManager;

    /**
     * The group class.
     */
    private final String groupClass;

    /**
     * The holding limit.
     */
    private final String holdingLimit;

    /**
     * The investment buffer.
     */
    private final String investmentBuffer;

    /**
     * The investment manager name.
     */
    private final String investmentManagerName;

    /**
     * The investment manager name.
     */
    private Boolean hasWfslManager;

    /**
     * The term of the term deposit.
     */
    private final String termDepositTerm;

    /**
     * The frequency of interest.
     */
    private final String termDepositInterestFrequency;

    /**
     * Instantiates a new InvestmentOptionsBookletAssetData.
     *
     * @param assetCode             the asset code
     * @param assetName             the asset name
     * @param feeMeasure            the fee measure
     * @param weightedIcr           the weighted icr
     * @param riskMeasure           the risk measure
     * @param assetClass            the asset class
     * @param fundManager           the fund manager (MF only)
     * @param groupClass            the group class
     * @param holdingLimit          the holding limit (LS Only)
     * @param investmentBuffer      the investment buffer (LS only)
     * @param investmentManagerName the investment manager name (MP only)
     * @param assetSubClass         the asset sub class (MF only)
     */
    public InvestmentOptionsBookletAssetData(String assetCode, String assetName, String feeMeasure, String weightedIcr,
                                             String riskMeasure, String assetClass, String fundManager, String groupClass, String holdingLimit,
                                             String investmentBuffer, String investmentManagerName, Boolean hasWfslManager, String assetSubClass, String termDepositTerm, String termDepositInterestFrequency) {
        super();
        this.assetCode = assetCode;
        this.assetName = assetName;
        this.feeMeasure = feeMeasure;
        this.weightedIcr = weightedIcr;
        this.riskMeasure = riskMeasure;
        this.assetClass = assetClass;
        this.fundManager = fundManager;
        this.groupClass = groupClass;
        this.holdingLimit = holdingLimit;
        this.investmentBuffer = investmentBuffer;
        this.investmentManagerName = investmentManagerName;
        this.hasWfslManager = hasWfslManager;
        this.assetSubClass = assetSubClass;
        this.termDepositTerm = termDepositTerm;
        this.termDepositInterestFrequency = termDepositInterestFrequency;
    }

    /**
     * Gets the asset code.
     *
     * @return the asset code
     */
    public String getAssetCode() {
        return assetCode;
    }

    /**
     * Gets the asset name.
     *
     * @return the asset name
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * Gets the fee measure.
     *
     * @return the fee measure
     */
    public String getFeeMeasure() {
        return feeMeasure;
    }

    /**
     * Gets the weighted icr.
     *
     * @return the weighted icr
     */
    public String getWeightedIcr() {
        return weightedIcr;
    }

    /**
     * Gets the risk measure.
     *
     * @return the risk measure
     */
    public String getRiskMeasure() {
        return riskMeasure;
    }

    /**
     * Gets the asset class.
     *
     * @return the asset class
     */
    public String getAssetClass() {
        return assetClass;
    }

    /**
     * Gets the fund manager.
     *
     * @return the fund manager
     */
    public String getFundManager() {
        return fundManager;
    }

    /**
     * Gets the group class.
     *
     * @return the group class
     */
    public String getGroupClass() {
        return groupClass;
    }

    /**
     * Gets the holding limit.
     *
     * @return the holding limit
     */
    public String getHoldingLimit() {
        return holdingLimit;
    }

    /**
     * Gets the investment buffer.
     *
     * @return the investment buffer
     */
    public String getInvestmentBuffer() {
        return investmentBuffer;
    }

    /**
     * Gets the investment manager name.
     *
     * @return the investment manager name
     */
    public String getInvestmentManagerName() {
        return investmentManagerName;
    }

    public Boolean getHasWfslManager() {
        return hasWfslManager;
    }

    public String getAssetSubClass() {
        return assetSubClass;
    }

    public String getTermDepositTerm() {
        return termDepositTerm;
    }

    public String getTermDepositInterestFrequency() {
        return termDepositInterestFrequency;
    }
}
