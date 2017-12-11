package com.bt.nextgen.fundpaymentnotice;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fundpaymentnotice.FundPaymentNoticeRequestImpl;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNotice;
import com.bt.nextgen.service.integration.fundpaymentnotice.FundPaymentNoticeIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class FundPaymentNoticeIntegrationTest extends BaseSecureIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(FundPaymentNoticeIntegrationTest.class);

    @Autowired
    private FundPaymentNoticeIntegrationService fundPaymentNoticeIntegrationService;

    private FundPaymentNotice fundResponse;

    /*
     * Load the Fund Payment Notice for the Managed Portfolio without passing Asset Ids in the request
     */
    @Test
    @SecureTestContext
    public void testloadFundPaymentNoticeWithoutAssetIds() throws Exception {
        logger.trace("Inside testMethod: testloadFundPaymentNoticeWithoutAssetIds()");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        FundPaymentNoticeRequestImpl request = new FundPaymentNoticeRequestImpl();
        request.setStartDate(new DateTime(2014, 9, 2, 0, 0, 0, 0));
        request.setEndDate(new DateTime(2014, 9, 2, 0, 0, 0, 0));

        final List<FundPaymentNotice> response = fundPaymentNoticeIntegrationService.getFundPaymentNoticeDetails(request, serviceErrors);

        assertThat(response.size(), is(41));

        logResponse(response.get(0));
        logResponse(response.get(4));
        logResponse(response.get(40));

        fundResponse = response.get(0);

        assertThat(fundResponse.getDistributionAmount().toString(), is("0.454919"));
        assertThat(fundResponse.getWithHoldFundPaymentAmount(), is(BigDecimal.valueOf(.02966)));
        assertThat(fundResponse.getDistributionDate(), is(new DateTime(2017, 4, 30, 0, 0, 0, 0)));
        assertThat(fundResponse.getTaxRelevanceDate(), is(new DateTime(2017, 4, 30, 0, 0, 0, 0)));
        assertThat(fundResponse.getTaxYear(), is("2016/2017"));
        assertThat(fundResponse.getAsset().getAssetId(), is("163259"));
        assertThat(fundResponse.getOrder().getOrderId(), is("8608198"));
        assertThat(fundResponse.getDistributions().size(), is(3));
        assertThat(fundResponse.getDistributions().get(0).getDistributionComponent(), is("Interest"));
        assertThat(fundResponse.getDistributions().get(0).getDistributionComponentAmount(), is(".002639"));
        assertThat(fundResponse.getDistributions().get(1).getDistributionComponent(), is("Other Income"));
        assertThat(fundResponse.getDistributions().get(1).getDistributionComponentAmount(), is(".02966"));
        assertThat(fundResponse.isAmitNotice(), is(false));

        fundResponse = response.get(4);

        assertThat(fundResponse.getDistributionAmount().toString(), is("0.300001"));
        assertThat(fundResponse.getWithHoldFundPaymentAmount(), is(BigDecimal.valueOf(0.017015)));
        assertThat(fundResponse.getDistributionDate(), is(new DateTime(2017, 4, 30, 0, 0, 0, 0)));
        assertThat(fundResponse.getTaxRelevanceDate(), is(new DateTime(2017, 4, 30, 0, 0, 0, 0)));
        assertThat(fundResponse.getTaxYear(), is("2016/2017"));
        assertThat(fundResponse.getAsset().getAssetId(), is("163256"));
        assertThat(fundResponse.getOrder().getOrderId(), is("8608206"));
        assertThat(fundResponse.getDistributions().get(2).getDistributionComponent(), is("Interest (No Non-Resident WHT)"));
        assertThat(fundResponse.getDistributions().get(2).getDistributionComponentAmount(), is(".196744"));
        assertThat(fundResponse.getDistributions().get(3).getDistributionComponent(), is("Other Income"));
        assertThat(fundResponse.getDistributions().get(3).getDistributionComponentAmount(), is(".017015"));
        assertThat(fundResponse.isAmitNotice(), is(false));

        final FundPaymentNotice amitNotice = response.get(40);

        assertEquals(amitNotice.isAmitNotice(), true);
        assertEquals(amitNotice.getAsset().getAssetId(), "477165");
        assertEquals(amitNotice.getTaxRelevanceDate(), DateTime.parse("2017-09-06"));
        assertEquals(amitNotice.getDistributionDate(), DateTime.parse("2017-09-06"));
        assertEquals(amitNotice.getTaxYear(), "2017/2018");
        assertEquals(amitNotice.getDistributions().size(), 6);
        assertEquals(amitNotice.getDistributions().get(0).getDistributionComponent(), "Franked Dividend");
        assertEquals(amitNotice.getDistributions().get(0).getDistributionComponentAmount(), "100");
        assertEquals(amitNotice.getDistributionAmount(), BigDecimal.valueOf(400));
        assertEquals(amitNotice.getWithHoldFundPaymentAmount(), BigDecimal.valueOf(170));
    }

	/*
     * Load the Fund Payment Notice for the Managed Portfolio by passing Asset Ids in the request
	 */

    @Test
    @SecureTestContext
    public void testloadFundPaymentNoticeWithAssetIds() throws Exception {
        logger.trace("Inside testMethod: testloadFundPaymentNoticeWithAssetIds()");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        FundPaymentNoticeRequestImpl request = new FundPaymentNoticeRequestImpl();
        request.setStartDate(new DateTime(2014, 9, 2, 0, 0, 0, 0));
        request.setEndDate(new DateTime(2014, 9, 2, 0, 0, 0, 0));

        List<String> assetIdList = new ArrayList<String>();
        assetIdList.add("1234");
        assetIdList.add("4567");

        request.setAssetIdList(assetIdList);

        final List<FundPaymentNotice> response = fundPaymentNoticeIntegrationService.getFundPaymentNoticeDetails(request, serviceErrors);

        assertThat(response.size(), is(41));

        logResponse(response.get(0));
        logResponse(response.get(4));
        logResponse(response.get(40));

        fundResponse = response.get(0);

        assertThat(fundResponse.getDistributionAmount().toString(), is("0.454919"));
        assertThat(fundResponse.getWithHoldFundPaymentAmount(), is(BigDecimal.valueOf(.02966)));
        assertThat(fundResponse.getDistributionDate(), is(new DateTime(2017, 4, 30, 0, 0, 0, 0)));
        assertThat(fundResponse.getTaxRelevanceDate(), is(new DateTime(2017, 4, 30, 0, 0, 0, 0)));
        assertThat(fundResponse.getTaxYear(), is("2016/2017"));
        assertThat(fundResponse.getAsset().getAssetId(), is("163259"));
        assertThat(fundResponse.getOrder().getOrderId(), is("8608198"));
        assertThat(fundResponse.getDistributions().size(), is(3));
        assertThat(fundResponse.getDistributions().get(0).getDistributionComponent(), is("Interest"));
        assertThat(fundResponse.getDistributions().get(0).getDistributionComponentAmount(), is(".002639"));
        assertThat(fundResponse.getDistributions().get(1).getDistributionComponent(), is("Other Income"));
        assertThat(fundResponse.getDistributions().get(1).getDistributionComponentAmount(), is(".02966"));
        assertThat(fundResponse.isAmitNotice(), is(false));

        fundResponse = response.get(4);

        assertThat(fundResponse.getDistributionAmount().toString(), is("0.300001"));
        assertThat(fundResponse.getWithHoldFundPaymentAmount(), is(BigDecimal.valueOf(0.017015)));
        assertThat(fundResponse.getDistributionDate(), is(new DateTime(2017, 4, 30, 0, 0, 0, 0)));
        assertThat(fundResponse.getTaxRelevanceDate(), is(new DateTime(2017, 4, 30, 0, 0, 0, 0)));
        assertThat(fundResponse.getTaxYear(), is("2016/2017"));
        assertThat(fundResponse.getAsset().getAssetId(), is("163256"));
        assertThat(fundResponse.getOrder().getOrderId(), is("8608206"));
        assertThat(fundResponse.getDistributions().get(2).getDistributionComponent(), is("Interest (No Non-Resident WHT)"));
        assertThat(fundResponse.getDistributions().get(2).getDistributionComponentAmount(), is(".196744"));
        assertThat(fundResponse.getDistributions().get(3).getDistributionComponent(), is("Other Income"));
        assertThat(fundResponse.getDistributions().get(3).getDistributionComponentAmount(), is(".017015"));
        assertThat(fundResponse.isAmitNotice(), is(false));

        final FundPaymentNotice amitNotice = response.get(40);

        assertEquals(amitNotice.isAmitNotice(), Boolean.TRUE);
        assertEquals(amitNotice.getAsset().getAssetId(), "477165");
        assertEquals(amitNotice.getTaxRelevanceDate(), DateTime.parse("2017-09-06"));
        assertEquals(amitNotice.getDistributionDate(), DateTime.parse("2017-09-06"));
        assertEquals(amitNotice.getTaxYear(), "2017/2018");
        assertEquals(amitNotice.getDistributions().size(), 6);
        assertEquals(amitNotice.getDistributions().get(0).getDistributionComponent(), "Franked Dividend");
        assertEquals(amitNotice.getDistributions().get(0).getDistributionComponentAmount(), "100");
        assertEquals(amitNotice.getDistributionAmount(), BigDecimal.valueOf(400));
        assertEquals(amitNotice.getWithHoldFundPaymentAmount(), BigDecimal.valueOf(170));
    }

    private void logResponse(FundPaymentNotice response) {
        logger.info("Distribution Amount:{}", response.getDistributionAmount());
        logger.info("WithHoldFundPayment Amount:{}", response.getWithHoldFundPaymentAmount());
        logger.info("Distribution Date:{}", response.getDistributionDate());
        logger.info("TaxRelevance Date:{}", response.getTaxRelevanceDate());
        logger.info("TaxRelevance Year:{}", response.getTaxYear());
        logger.info("AssetId:{}", response.getAsset().getAssetId());
        logger.info("OrderId:{}", response.getOrder().getOrderId());
        logger.info("Distribution Component:{}", response.getDistributions().get(0).getDistributionComponent());
        logger.info("Distribution ComponentAmount:{}", response.getDistributions().get(0).getDistributionComponentAmount());
        logger.info("Distribution Component:{}", response.getDistributions().get(1).getDistributionComponent());
        logger.info("Distribution ComponentAmount:{}", response.getDistributions().get(1).getDistributionComponentAmount());
        logger.info("Is AMIT notice :{}", response.isAmitNotice());
    }

    @Test
    @SecureTestContext(username = "explode", customerId = "201601388")
    public void testloadFundPaymentNoticeWithAssetIdsError() throws Exception {
        logger.trace("Inside testMethod: testloadFundPaymentNoticeWithAssetIds()");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        FundPaymentNoticeRequestImpl request = new FundPaymentNoticeRequestImpl();
        request.setStartDate(new DateTime(2014, 9, 2, 0, 0, 0, 0));
        request.setEndDate(new DateTime(2014, 9, 2, 0, 0, 0, 0));

        List<String> assetIdList = new ArrayList<String>();
        assetIdList.add("1234");
        assetIdList.add("4567");

        request.setAssetIdList(assetIdList);

        List<FundPaymentNotice> response = fundPaymentNoticeIntegrationService.getFundPaymentNoticeDetails(request,
                serviceErrors);
        assertThat(serviceErrors.hasErrors(), Is.is(true));
    }
}
