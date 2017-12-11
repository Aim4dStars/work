package com.bt.nextgen.service.avaloq.superpersonaltaxdeduction;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Interface for tax deduction notice service.
 */
public interface PersonalTaxDeductionIntegrationService {
    /**
     * Get Personal tax Deduction notices for an account for a range of dates.
     *
     * @param accountNumber          Account Number
     * @param docId                  Id of the notice to retrieve.
     *                               If this value is {@code null}, the result will not be restricted to the notice Id.
     * @param financialYearStartDate Start of the financial year.
     * @param financialYearEndDate   End of the financial year.
     *
     * @return Tax Deduction Notices for an account for a range of dates
     */
    PersonalTaxDeduction getPersonalTaxDeductionNotices(String accountNumber, String docId,
                                                        DateTime financialYearStartDate,
                                                        DateTime financialYearEndDate, ServiceErrors serviceErrors);

    /* Create new Tax Deduction Notice.
     *
     * @param accountNumber          Account Number.
     * @param financialYearStartDate Start of the financial year.
     * @param financialYearEndDate   End of the financial year.
     * @param amount                 Variation amount.
     *
     * @return PersonalTaxDeductionNoticeTrxnDto    DTO containing success or failure status.
     */
    PersonalTaxDeductionNoticeTrxnDto createTaxDeductionNotice(String accountNumber, DateTime financialYearStartDate,
                                                               DateTime financialYearEndDate, BigDecimal amount);

    /* Vary a Tax Deduction Notice.
     *
     * @param accountNumber          Account Number.
     * @param originalDocId          Id of the original notice to vary.
     * @param financialYearStartDate Start of the financial year.
     * @param financialYearEndDate   End of the financial year.
     * @param amount                 Variation amount.
     *
     * @return PersonalTaxDeductionNoticeTrxnDto    DTO containing success or failure status.
     */
    PersonalTaxDeductionNoticeTrxnDto varyTaxDeductionNotice(String accountNumber, String originalDocId,
                                                             DateTime financialYearStartDate,
                                                             DateTime financialYearEndDate, BigDecimal amount);
}
