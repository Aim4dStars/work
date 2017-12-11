package com.bt.nextgen.service.avaloq.superpersonaltaxdeduction;

import java.util.List;

/**
 * Interface for Tax Deduction
 */
public interface PersonalTaxDeduction {

    /**
     * Get the list of deduction notices
     *
     * @return list PersonalTaxDeductionNotices
     */
    public List<PersonalTaxDeductionNotices> getTaxDeductionNotices();

}
