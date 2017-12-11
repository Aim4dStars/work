package com.bt.nextgen.corporateaction.service;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionClientAccountDetails;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionSupplementaryDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionStatus;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionSupplementaryDetailsTest {
    @Test
    public void test() {
        // A bit of a useless test...
        CorporateActionSupplementaryDetails supplementaryDetails = new CorporateActionSupplementaryDetails();

        supplementaryDetails.setClientAccountDetails(new CorporateActionClientAccountDetails());
        supplementaryDetails.setTransactionDetails(new ArrayList<CorporateActionTransactionDetails>());
        supplementaryDetails.setTransactionStatus(CorporateActionTransactionStatus.POST_EX_DATE);
        supplementaryDetails.setWrapAccountValuations(new ArrayList<WrapAccountValuation>());

        assertNotNull(supplementaryDetails.getClientAccountDetails());
        assertNotNull(supplementaryDetails.getTransactionDetails());
        assertNotNull(supplementaryDetails.getWrapAccountValuations());
        assertEquals(CorporateActionTransactionStatus.POST_EX_DATE, supplementaryDetails.getTransactionStatus());
    }
}
