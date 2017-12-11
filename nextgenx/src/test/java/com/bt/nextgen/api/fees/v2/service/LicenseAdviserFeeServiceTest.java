package com.bt.nextgen.api.fees.v2.service;

import com.bt.nextgen.api.fees.v2.model.BaseFeeType;
import com.bt.nextgen.api.fees.v2.model.DollarFeeAmount;
import com.bt.nextgen.api.fees.v2.model.LicenseAdviserFeeDto;
import com.bt.nextgen.api.fees.v2.model.PercentageFee;
import com.bt.nextgen.api.fees.v2.model.SlidingScaleFee;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerImpl;
import com.bt.nextgen.service.avaloq.fees.FeesMiscType;
import com.bt.nextgen.service.avaloq.licenseadviserfee.CacheManagedAvaloqLicenseAdviserFeeIntegrationService;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingApplyDef;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingApplyDefImpl;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingTariff;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingTariffBound;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingTariffBoundImpl;
import com.bt.nextgen.service.avaloq.licenseadviserfee.FeeDgOngoingTariffImpl;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.service.avaloq.fees.FeesMiscType.DOLLAR_FEE;
import static com.bt.nextgen.service.avaloq.fees.FeesMiscType.MIN_MAX;
import static com.bt.nextgen.service.avaloq.fees.FeesMiscType.PERCENT_CASH;
import static com.bt.nextgen.service.avaloq.fees.FeesMiscType.PERCENT_MANAGED_FUND;
import static com.bt.nextgen.service.avaloq.fees.FeesMiscType.SLIDING_SCALE_FEE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by l078480 on 23/11/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class LicenseAdviserFeeServiceTest {

    @InjectMocks
    LicenseAdviserFeeServiceImpl licenseAdviserFeeService;

    @Mock
    private CacheManagedAvaloqLicenseAdviserFeeIntegrationService licenseAdviserFeeIntegrationServiceeeService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    private List<FeeDgOngoingTariff> feeDgOngoingTariffList;
    private List<FeeDgOngoingTariff> feeDgOngoingTariffPercentageList;
    private List<FeeDgOngoingTariff> feeDgOngoingSlidingScaleList;
    private List<FeeDgOngoingTariff> allFeeDgOngoingTariffList;
    BigDecimal tarrifFactorone = new BigDecimal("2.0");
    BigDecimal tarrifFactorTwo = new BigDecimal("4.0");
    BigDecimal feeAmount = new BigDecimal(10);

    @Before
    public void setUp() {

        feeDgOngoingTariffList = new ArrayList<FeeDgOngoingTariff>();
        feeDgOngoingTariffPercentageList = new ArrayList<FeeDgOngoingTariff>();
        feeDgOngoingSlidingScaleList = new ArrayList<FeeDgOngoingTariff>();
        allFeeDgOngoingTariffList = new ArrayList<FeeDgOngoingTariff>();
        FeeDgOngoingTariffImpl dollarFeeDgOngoingTariff = new FeeDgOngoingTariffImpl();

        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        broker.setParentKey(BrokerKey.valueOf("66773"));
        broker.setDealerKey(BrokerKey.valueOf("66778"));

        // Dollar amount Fee
        dollarFeeDgOngoingTariff.setCpi(true);
        dollarFeeDgOngoingTariff.setCostMiscType(DOLLAR_FEE);
        dollarFeeDgOngoingTariff.setTariffAmnt(new BigDecimal(10));
        dollarFeeDgOngoingTariff.setCpi(true);
        feeDgOngoingTariffList.add(dollarFeeDgOngoingTariff);
        allFeeDgOngoingTariffList.add(dollarFeeDgOngoingTariff);
        // tariffFee
        FeeDgOngoingTariffImpl tarrifFeeDgOngoingTariff = new FeeDgOngoingTariffImpl();
        tarrifFeeDgOngoingTariff.setCostMiscType(FeesMiscType.PERCENT_CASH);
        tarrifFeeDgOngoingTariff.setTariffFactor(new BigDecimal(0.02));
        feeDgOngoingTariffPercentageList.add(tarrifFeeDgOngoingTariff);
        allFeeDgOngoingTariffList.add(tarrifFeeDgOngoingTariff);
        FeeDgOngoingTariffImpl tarrifFeeDgOngoingTariffMF = new FeeDgOngoingTariffImpl();
        tarrifFeeDgOngoingTariffMF.setCostMiscType(FeesMiscType.PERCENT_MANAGED_FUND);
        tarrifFeeDgOngoingTariffMF.setTariffFactor(new BigDecimal(0.04));
        feeDgOngoingTariffPercentageList.add(tarrifFeeDgOngoingTariffMF);
        allFeeDgOngoingTariffList.add(tarrifFeeDgOngoingTariffMF);

        // SlidingFee
        FeeDgOngoingTariffImpl slidingFeeDgOngoingTariffMF = new FeeDgOngoingTariffImpl();
        slidingFeeDgOngoingTariffMF.setCostMiscType(FeesMiscType.SLIDING_SCALE_FEE);
        List<FeeDgOngoingTariffBound> firstTariffbound = new ArrayList<FeeDgOngoingTariffBound>();
        List<FeeDgOngoingApplyDef> feeDgOngoingApplyDefs = new ArrayList<FeeDgOngoingApplyDef>();

        FeeDgOngoingApplyDefImpl feeDgOngoingApplyDef = new FeeDgOngoingApplyDefImpl();
        feeDgOngoingApplyDef.setfeesMiscType(FeesMiscType.PERCENT_MANAGED_FUND);
        feeDgOngoingApplyDefs.add(feeDgOngoingApplyDef);

        FeeDgOngoingApplyDefImpl feeDgOngoingApplySecond = new FeeDgOngoingApplyDefImpl();
        feeDgOngoingApplySecond.setfeesMiscType(FeesMiscType.PERCENT_SHARE);
        feeDgOngoingApplyDefs.add(feeDgOngoingApplySecond);

        FeeDgOngoingTariffBoundImpl tariffBound = new FeeDgOngoingTariffBoundImpl();
        tariffBound.setTariffFactor(new BigDecimal("2.0"));
        tariffBound.setMax(new BigDecimal(10000));
        tariffBound.setMin(new BigDecimal(0));
        tariffBound.setBoundFrom(new BigDecimal(0));
        tariffBound.setBoundTo(new BigDecimal(10000));
        firstTariffbound.add(tariffBound);
        FeeDgOngoingTariffBoundImpl tariffBoundSecond = new FeeDgOngoingTariffBoundImpl();
        tariffBoundSecond.setTariffFactor(new BigDecimal("4.0"));
        tariffBoundSecond.setMax(new BigDecimal(10000));
        tariffBoundSecond.setMin(new BigDecimal(0));
        tariffBoundSecond.setBoundFrom(new BigDecimal(0));
        tariffBoundSecond.setBoundTo(new BigDecimal(10000));
        firstTariffbound.add(tariffBoundSecond);
        slidingFeeDgOngoingTariffMF.setFeeDgOngoingTariffBoundList(firstTariffbound);
        slidingFeeDgOngoingTariffMF.setFeeDgOngoingApplyDefList(feeDgOngoingApplyDefs);
        feeDgOngoingSlidingScaleList.add(slidingFeeDgOngoingTariffMF);
        allFeeDgOngoingTariffList.add(slidingFeeDgOngoingTariffMF);

        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
    }

    @Test
    public void testFindLicenseAdviserDollarFee() {
        Mockito.when(licenseAdviserFeeIntegrationServiceeeService.loadLicenseFeesForDealer(Mockito.any(String.class),
                Mockito.any(ServiceErrors.class))).thenReturn(feeDgOngoingTariffList);
        LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseFeeForDealerGroup("test",
                new FailFastErrorsImpl());
        DollarFeeAmount dollarFee = (DollarFeeAmount) licenseAdviserFeeDto.getFeeComponentType().getFeeType()
                .get(DOLLAR_FEE.getLabel());
        assertEquals("dollarFee", dollarFee.getLabel());
        assertEquals("10", dollarFee.getFees().get(DOLLAR_FEE.getLabel()));
        assertTrue(dollarFee.isCpiIndex());
    }

    @Test
    public void testFindLicenseLicenseAdviserFee() {
        Mockito.when(licenseAdviserFeeIntegrationServiceeeService.loadLicenseAdviseFees(Mockito.any(String.class),
                Mockito.any(String.class), Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(feeDgOngoingTariffList);
        LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseAdviserFee("test", "test",
                new FailFastErrorsImpl());
        DollarFeeAmount dollarFee = (DollarFeeAmount) licenseAdviserFeeDto.getFeeComponentType().getFeeType()
                .get(DOLLAR_FEE.getLabel());
        assertEquals("licenseeFee", licenseAdviserFeeDto.getFeeComponentType().getType());
        assertEquals("dollarFee", dollarFee.getLabel());
        assertEquals("10", dollarFee.getFees().get(DOLLAR_FEE.getLabel()));
        assertTrue(dollarFee.isCpiIndex());
    }

    @Test
    public void testFindLicenseAdviserTariffFee() {

        Mockito.when(licenseAdviserFeeIntegrationServiceeeService.loadLicenseFeesForDealer(Mockito.any(String.class),
                Mockito.any(ServiceErrors.class))).thenReturn(feeDgOngoingTariffPercentageList);
        LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseFeeForDealerGroup("test",
                new FailFastErrorsImpl());
        PercentageFee percentageFee = (PercentageFee) licenseAdviserFeeDto.getFeeComponentType().getFeeType()
                .get(PERCENT_CASH.getLabel());
        assertEquals("cash", percentageFee.getLabel());
        assertEquals("4.00", percentageFee.getFees().get(PERCENT_MANAGED_FUND.getLabel()));
        assertEquals("2.00", percentageFee.getFees().get(PERCENT_CASH.getLabel()));
    }

    @Test
    public void testFindLicenseAdviserslidingFee() {

        Mockito.when(licenseAdviserFeeIntegrationServiceeeService.loadLicenseFeesForDealer(Mockito.any(String.class),
                Mockito.any(ServiceErrors.class))).thenReturn(feeDgOngoingSlidingScaleList);
        LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseFeeForDealerGroup("test",
                new FailFastErrorsImpl());
        SlidingScaleFee slidingScaleFee = (SlidingScaleFee) licenseAdviserFeeDto.getFeeComponentType().getFeeType()
                .get(SLIDING_SCALE_FEE.getLabel());
        assertEquals(slidingScaleFee.getScaleFeeTierList().size(), 2);
        assertEquals("slidingScaleFee", slidingScaleFee.getLabel());
        assertEquals("true", slidingScaleFee.getFees().get(PERCENT_MANAGED_FUND.getLabel()));
        assertEquals("0", slidingScaleFee.getScaleFeeTierList().get(0).getLowerBound());
        assertEquals("10000", slidingScaleFee.getScaleFeeTierList().get(0).getUpperBound());
        assertEquals("200.00", slidingScaleFee.getScaleFeeTierList().get(0).getPercentage());
    }

    @Test
    public void testFindLicenseAdviserslidingFeeNull() {

        Mockito.when(licenseAdviserFeeIntegrationServiceeeService.loadLicenseFeesForDealer(Mockito.any(String.class),
                Mockito.any(ServiceErrors.class))).thenReturn(null);
        LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseFeeForDealerGroup("test",
                new FailFastErrorsImpl());
        assertNull(null, licenseAdviserFeeDto);

    }

    @Test
    public void testAllLicenseAdviserFee() {
        Mockito.when(licenseAdviserFeeIntegrationServiceeeService.loadLicenseFeesForDealer(Mockito.any(String.class),
                Mockito.any(ServiceErrors.class))).thenReturn(allFeeDgOngoingTariffList);
        LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseFeeForDealerGroup("test",
                new FailFastErrorsImpl());
        DollarFeeAmount dollarFee = (DollarFeeAmount) licenseAdviserFeeDto.getFeeComponentType().getFeeType()
                .get(DOLLAR_FEE.getLabel());
        PercentageFee percentageFee = (PercentageFee) licenseAdviserFeeDto.getFeeComponentType().getFeeType()
                .get(PERCENT_CASH.getLabel());
        SlidingScaleFee slidingScaleFee = (SlidingScaleFee) licenseAdviserFeeDto.getFeeComponentType().getFeeType()
                .get(SLIDING_SCALE_FEE.getLabel());

        assertEquals("dollarFee", dollarFee.getLabel());
        assertEquals("cash", percentageFee.getLabel());
        assertEquals("slidingScaleFee", slidingScaleFee.getLabel());
    }

    @Test
    public void testFindLicenseAdviserFee_whenEmpty_returnNullObject() {
        List<FeeDgOngoingTariff> licenseAdviserFees = new ArrayList<FeeDgOngoingTariff>();
        Mockito.when(licenseAdviserFeeIntegrationServiceeeService.loadLicenseAdviseFees(Mockito.any(String.class),
                Mockito.any(String.class), Mockito.any(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(licenseAdviserFees);
        LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseAdviserFee("test", "test",
                new FailFastErrorsImpl());
        assertNull(null, licenseAdviserFeeDto);
    }

    @Test
    public void testFindLicenseAdviserTariffFee_whenNotCosttMiscType_thenEmptyMappedObject() {
        Map<String, BaseFeeType> feeTypes = new HashMap();
        List<FeeDgOngoingTariff> feeDgOngoingTariffList = new ArrayList<FeeDgOngoingTariff>();
        FeeDgOngoingTariffImpl dollarFeeDgOngoingTariff = new FeeDgOngoingTariffImpl();
        dollarFeeDgOngoingTariff.setCpi(true);
        dollarFeeDgOngoingTariff.setCostMiscType(MIN_MAX);
        dollarFeeDgOngoingTariff.setTariffAmnt(new BigDecimal(10));
        dollarFeeDgOngoingTariff.setCpi(true);
        feeDgOngoingTariffList.add(dollarFeeDgOngoingTariff);

        Mockito.when(licenseAdviserFeeIntegrationServiceeeService.loadLicenseFeesForDealer(Mockito.any(String.class),
                Mockito.any(ServiceErrors.class))).thenReturn(feeDgOngoingTariffList);
        LicenseAdviserFeeDto licenseAdviserFeeDto = licenseAdviserFeeService.findLicenseFeeForDealerGroup("test",
                new FailFastErrorsImpl());
        assertEquals("licenseeFee", licenseAdviserFeeDto.getFeeComponentType().getType());
        assertEquals(feeTypes, licenseAdviserFeeDto.getFeeComponentType().getFeeType());
    }
}
