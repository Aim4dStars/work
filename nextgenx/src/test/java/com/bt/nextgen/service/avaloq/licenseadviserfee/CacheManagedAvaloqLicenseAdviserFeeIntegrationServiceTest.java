package com.bt.nextgen.service.avaloq.licenseadviserfee;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by L078480 on 11/11/2016.
 */

public class CacheManagedAvaloqLicenseAdviserFeeIntegrationServiceTest extends BaseSecureIntegrationTest {


    @Autowired
    private CacheManagedAvaloqLicenseAdviserFeeIntegrationService licenseAdviserFeeIntegrationService;


    @SecureTestContext
    @Test
    public void test_loadLicenseFeesForDgAndProductId() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<FeeDgOngoingTariff> licenseAdviserFees = licenseAdviserFeeIntegrationService.loadLicenseAdviseFees("108286", "78990", "44926", serviceErrors);
        assertThat(licenseAdviserFees.size(), equalTo(1));
        assertEquals("slidingScaleFee", licenseAdviserFees.get(0).getCostMiscType().getLabel());
        assertThat(licenseAdviserFees.get(0).getFeeDgOngoingApplyDefList().size(), equalTo(2));
        assertThat(licenseAdviserFees.get(0).getFeeDgOngoingTariffBoundList().size(), equalTo(2));
    }




    @SecureTestContext
    @Test
    public void test_loadLicenseFeesForDealerAndProductIdandAdviser() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<FeeDgOngoingTariff> licenseAdviserFees = licenseAdviserFeeIntegrationService.loadLicenseAdviseFees("108286", "78990", "44926", serviceErrors);
        assertThat(licenseAdviserFees.size(), equalTo(1));
        assertEquals("slidingScaleFee", licenseAdviserFees.get(0).getCostMiscType().getLabel());
        assertThat(licenseAdviserFees.get(0).getFeeDgOngoingApplyDefList().size(), equalTo(2));
        assertThat(licenseAdviserFees.get(0).getFeeDgOngoingTariffBoundList().size(), equalTo(2));

    }

    @SecureTestContext
    @Test
    public void test_loadLicenseFeesForWhiteLabelId() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<FeeDgOngoingTariff> licenseAdviserFees = licenseAdviserFeeIntegrationService.loadLicenseAdviseFees("108286", "78990", "44926", serviceErrors);
        assertThat(licenseAdviserFees.size(), equalTo(1));
        assertEquals("slidingScaleFee", licenseAdviserFees.get(0).getCostMiscType().getLabel());
        assertThat(licenseAdviserFees.get(0).getFeeDgOngoingApplyDefList().size(), equalTo(2));
        assertThat(licenseAdviserFees.get(0).getFeeDgOngoingTariffBoundList().size(), equalTo(2));
    }


    @SecureTestContext
    @Test
    public void test_loadAllLicenseFeeEmpty() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<FeeDgOngoingTariff> licenseAdviserFees = licenseAdviserFeeIntegrationService.loadLicenseAdviseFees("102114880","102114880","102114880", serviceErrors);
        assertThat(licenseAdviserFees.size(), equalTo(0));
    }

    @SecureTestContext
    @Test
    public void test_loadAllLicenseSlidingScaleFee() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<FeeDgOngoingTariff> licenseAdviserFees = licenseAdviserFeeIntegrationService.loadLicenseAdviseFees("102114880","100182","102114880", serviceErrors);
        assertThat(licenseAdviserFees.size(), equalTo(1));
        assertEquals("slidingScaleFee", licenseAdviserFees.get(0).getCostMiscType().getLabel());
        assertThat(licenseAdviserFees.get(0).getFeeDgOngoingApplyDefList().size(), equalTo(5));
        assertThat(licenseAdviserFees.get(0).getFeeDgOngoingTariffBoundList().size(), equalTo(2));

    }
}

