package com.bt.nextgen.reports.corporateaaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.corporateaction.CorporateActionElectionCsvHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.DollarFeesComponent;
import com.bt.nextgen.service.avaloq.fees.FeesComponents;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.PercentageFeesComponent;
import com.bt.nextgen.service.integration.fees.FeesSchedule;
import com.bt.nextgen.service.integration.fees.FeesScheduleIntegrationService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionElectionCsvHelperTest {
    @InjectMocks
    private CorporateActionElectionCsvHelper corporateActionElectionCsvHelper;

    @Mock
    private FeesScheduleIntegrationService feesScheduleIntegrationService;

    @Test
    public void testGetOngoingAdviceFee_whenThereAreNoFees_thenReturnEmptyMap() {
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);

        when(accountDetailsDto.getAccountKey()).thenReturn(EncodedString.fromPlainText("0").toString());
        when(accountDetailsDto.getClientId()).thenReturn("0");
        when(feesScheduleIntegrationService.getFees(anyString(), any(ServiceErrors.class))).thenReturn(new ArrayList<FeesSchedule>());

        Map<String, BigDecimal> adviserFees = corporateActionElectionCsvHelper.getOngoingAdviceFee(accountDetailsDto);

        assertTrue(adviserFees.isEmpty());

        when(feesScheduleIntegrationService.getFees(anyString(), any(ServiceErrors.class))).thenReturn(null);

        adviserFees = corporateActionElectionCsvHelper.getOngoingAdviceFee(accountDetailsDto);

        assertTrue(adviserFees.isEmpty());
    }

    @Test
    public void testGetOngoingAdviceFee_whenThereAreFees_thenReturnJustOngoingFeesMap() {
        CorporateActionAccountDetailsDto accountDetailsDto = mock(CorporateActionAccountDetailsDto.class);
        FeesSchedule ongoingFeesSchedule = mock(FeesSchedule.class);
        FeesSchedule otherFeesSchedule = mock(FeesSchedule.class);
        DollarFeesComponent dollarFeesComponent = mock(DollarFeesComponent.class);
        PercentageFeesComponent percentageFeesComponent = mock(PercentageFeesComponent.class);

        List<FeesComponents> feesComponentsList = new ArrayList<>();
        feesComponentsList.add(dollarFeesComponent);
        feesComponentsList.add(percentageFeesComponent);

        when(dollarFeesComponent.getDollar()).thenReturn(BigDecimal.TEN);
        when(ongoingFeesSchedule.getFeesType()).thenReturn(FeesType.ONGOING_FEE);
        when(otherFeesSchedule.getFeesType()).thenReturn(FeesType.ADMIN_FEE);
        when(ongoingFeesSchedule.getFeesComponents()).thenReturn(feesComponentsList);
        when(accountDetailsDto.getAccountKey()).thenReturn(EncodedString.fromPlainText("0").toString());
        when(accountDetailsDto.getClientId()).thenReturn("0");
        when(feesScheduleIntegrationService.getFees(anyString(), any(ServiceErrors.class))).thenReturn(Arrays.asList(ongoingFeesSchedule, otherFeesSchedule));

        Map<String, BigDecimal> adviserFees = corporateActionElectionCsvHelper.getOngoingAdviceFee(accountDetailsDto);

        assertEquals(1, adviserFees.size());
        BigDecimal fee = adviserFees.get("0");
        assertNotNull(fee);
        assertTrue(BigDecimal.TEN.compareTo(fee) == 0);
    }

}
