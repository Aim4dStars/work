package com.bt.nextgen.reports.investmentoptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class InvestmentOptionsBookletTableData represent the data that is displayed in the investment options section of the
 * Investment Options Booklet. 
 * <p>
 * This class supports instantiation from JSON data.
 */
public class InvestmentOptionsBookletTableData {

    /** The investment type. */
    private final String investmentType;

    /** The investment measure. */
    private final String investmentMeasure;

    /** The investment measure value. */
    private final String investmentMeasureValue;

    /**
     * Instantiates a new InvestmentOptionsBookletTableData.
     *
     * @param investmentType
     *            the investment type
     * @param investmentMeasure
     *            the investment measure
     * @param investmentMeasureValue
     *            the investment measure value
     */
    @JsonCreator
    public InvestmentOptionsBookletTableData(@JsonProperty("investmentType") final String investmentType,
            @JsonProperty("investmentMeasure") final String investmentMeasure,
            @JsonProperty("investmentMeasureValue") final String investmentMeasureValue) {
        this.investmentType = investmentType;
        this.investmentMeasure = investmentMeasure;
        this.investmentMeasureValue = investmentMeasureValue;
    }

    /**
     * Gets the investment type.
     *
     * @return the investment type
     */
    public String getInvestmentType() {
        return investmentType;
    }

    /**
     * Gets the investment measure.
     *
     * @return the investment measure
     */
    public String getInvestmentMeasure() {
        return investmentMeasure;
    }

    /**
     * Gets the investment measure value.
     *
     * @return the investment measure value
     */
    public String getInvestmentMeasureValue() {
        return investmentMeasureValue;
    }
}
