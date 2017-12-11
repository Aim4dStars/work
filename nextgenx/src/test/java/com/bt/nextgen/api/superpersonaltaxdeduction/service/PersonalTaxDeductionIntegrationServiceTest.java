package com.bt.nextgen.api.superpersonaltaxdeduction.service;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionIntegrationService;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by L067218 on 17/11/2016.
 */
public class PersonalTaxDeductionIntegrationServiceTest  extends BaseSecureIntegrationTest {

    private static final String ACCOUNT_NUMBER = "545409";
    private static final String DOC_ID = "4317961";
    private static final DateTime FINANCIAL_YEAR_START_DATE = new DateTime();
    private static final DateTime FINANCIAL_YEAR_END_DATE = new DateTime();
    private static final BigDecimal CLAIM_AMOUNT = new BigDecimal(2500);

    @Autowired
    PersonalTaxDeductionIntegrationService personalTaxDeductionIntegrationService;

    @SecureTestContext
    @Test
    public void saveNewTaxDeduction() throws Exception {

        PersonalTaxDeductionNoticeTrxnDto responseDto = personalTaxDeductionIntegrationService.createTaxDeductionNotice(ACCOUNT_NUMBER, FINANCIAL_YEAR_START_DATE, FINANCIAL_YEAR_END_DATE, CLAIM_AMOUNT);
        assertNotNull(responseDto);
        assertEquals("saved", responseDto.getTransactionStatus());
    }

    @SecureTestContext
    @Test
    public void varyNewTaxDeduction() throws Exception {

        PersonalTaxDeductionNoticeTrxnDto responseDto = personalTaxDeductionIntegrationService.varyTaxDeductionNotice(ACCOUNT_NUMBER, DOC_ID, FINANCIAL_YEAR_START_DATE, FINANCIAL_YEAR_END_DATE, CLAIM_AMOUNT);
        assertNotNull(responseDto);
        assertEquals("saved", responseDto.getTransactionStatus());
    }

}
