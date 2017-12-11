package com.bt.nextgen.service.avaloq.superpersonaltaxdeduction;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Interface for Tax deduction notices
 */
public interface PersonalTaxDeductionNotices {

    /**
     * Get the notice amount
     *
     * @return Notice Amount
     */
    BigDecimal getNoticeAmount();

    /**
     * Get the document id.
     *
     * @return Document id.
     */
    Long getDocId();

    /**
     * Get the reference document id.
     *
     * @return Document id.
     */
    Long getRefDocId();

    /**
     * Get the notice date
     *
     * @return Notice date
     */
    DateTime getNoticeDate();

    /**
     * Get the variation notice value
     *
     * @return boolean isVarNotice
     */
    Boolean getIsVarNotice();

    /**
     * Get the Unalterable notice amount
     *
     * @return Unalterable Notice Amount
     */
    BigDecimal getUnalterableNoticeAmount();

}
