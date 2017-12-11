package com.bt.nextgen.service.avaloq.fees;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.fees.FeesScheduleTransaction;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.fees.FeesSchedule;
import com.bt.nextgen.service.integration.fees.FeesScheduleIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AvaloqFeesScheduleIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    FeesScheduleIntegrationService feesScheduleIntegrationService;

    String accountId;
    ServiceErrors serviceErrors;

    List<FeesSchedule> interfaceList = new ArrayList<FeesSchedule>();
    DollarFeesComponent dollarComponent = new DollarFeesComponent();
    PercentageFeesComponent percentComponent = new PercentageFeesComponent();
    SlidingScaleFeesComponent slidingComponent = new SlidingScaleFeesComponent();

    @Before
    public void setup() throws Exception {
        accountId = "BB8FE88383B20FDD59ED5E2DD0231C347EED094FC7DF2098";
        FeesSchedule fees = new FeesScheduleImpl();
        List<FeesComponents> compList = new ArrayList<FeesComponents>();

        dollarComponent.setFeesComponentType(FeesComponentType.DOLLAR_FEE);
        dollarComponent.setDollar(new BigDecimal("1"));
        dollarComponent.setCpiindex(true);
        dollarComponent.setIndexation(new Date());

        percentComponent.setFeesComponentType(FeesComponentType.PERCENTAGE_FEE);
        Map<FeesMiscType, BigDecimal> percentMap = new HashMap<FeesMiscType, BigDecimal>();
        percentMap.put(FeesMiscType.PERCENT_MANAGED_PORTFOLIO, new BigDecimal("2"));
        percentMap.put(FeesMiscType.PERCENT_SHARE, new BigDecimal("2"));
        percentComponent.setPercentMap(percentMap);

        List<FeesMiscType> transactionTypes = new ArrayList<FeesMiscType>();
        transactionTypes.add(FeesMiscType.PERCENT_CASH);
        transactionTypes.add(FeesMiscType.PERCENT_MANAGED_FUND);
        transactionTypes.add(FeesMiscType.PERCENT_MANAGED_PORTFOLIO);
        transactionTypes.add(FeesMiscType.PERCENT_TERM_DEPOSIT);
        transactionTypes.add(FeesMiscType.PERCENT_SHARE);
        slidingComponent.setTransactionType(transactionTypes);

        slidingComponent.setFeesComponentType(FeesComponentType.SLIDING_SCALE_FEE);
        slidingComponent.setMinFees(new BigDecimal("1"));
        slidingComponent.setMaxFees(new BigDecimal("5"));
        List<SlidingScaleTiers> tierList = new ArrayList<SlidingScaleTiers>();
        SlidingScaleTiers tier = new SlidingScaleTiers();
        tier.setLowerBound(new BigDecimal("2"));
        tier.setUpperBound(new BigDecimal("3"));
        tier.setPercent(new BigDecimal("0.1"));
        tierList.add(tier);

        SlidingScaleTiers tier1 = new SlidingScaleTiers();
        tier1.setLowerBound(new BigDecimal("3"));
        tier1.setUpperBound(new BigDecimal("34"));
        tier1.setPercent(new BigDecimal("0.2"));
        tierList.add(tier1);

        slidingComponent.setTiers(tierList);

        compList.add(dollarComponent);
        compList.add(percentComponent);
        compList.add(slidingComponent);
        serviceErrors = new ServiceErrorsImpl();

        fees.setType(FeesType.ONGOING_FEE);
        fees.setAccountId(accountId);
        fees.setFeesComponents(compList);
        interfaceList.add(fees);

    }

    @Test
    public void testGetFeesScheduleComponents_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<FeesSchedule> interfaceList = feesScheduleIntegrationService.getFees(accountId, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(interfaceList);
        Assert.assertEquals(3, interfaceList.size());
    }

    @Test
    public void testGetFeesScheduleComponents_whenValidResponse_thenObjectCreatedAndNoLAFApplicable() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<FeesSchedule> interfaceList = feesScheduleIntegrationService.getFees(accountId, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(interfaceList);
        Assert.assertEquals(3, interfaceList.size());
    }

    @Test
    public void testValidateFeeSchedule_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        FeesScheduleTransaction transactionImpl = feesScheduleIntegrationService.validateFeeSchedule(interfaceList, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(transactionImpl);

    }

    @Test
    public void testSubmitFeeSchedule_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        FeesScheduleTransaction transactionImpl = feesScheduleIntegrationService.submitFeeSchedule(interfaceList, serviceErrors);
        Assert.assertFalse(serviceErrors.hasErrors());
        Assert.assertNotNull(transactionImpl);

    }
}
