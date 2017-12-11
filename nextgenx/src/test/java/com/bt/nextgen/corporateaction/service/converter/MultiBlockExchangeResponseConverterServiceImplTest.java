package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MultiBlockExchangeResponseConverterServiceImpl;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElectionKey;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountKey;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipation;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MultiBlockExchangeResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private MultiBlockExchangeResponseConverterServiceImpl multiBlockExchangeResponseConverterServiceImpl;

    @Before
    public void setup() {
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsAFullTakeUp_thenReturnCorrectFullTakeUpId() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);
        when(context.isDealerGroupOrInvestmentManager()).thenReturn(true);

        CorporateActionOption corporateActionAccountOption = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), new
                BigDecimal("100.0"));

        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption));

        CorporateActionAccountElectionsDto accountElectionsDto = multiBlockExchangeResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(context, corporateActionAccount);

        assertNotNull(accountElectionsDto.getPrimaryAccountElection());
        assertEquals((Integer) 1, accountElectionsDto.getPrimaryAccountElection().getOptionId());
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsAPartialTakeUp_thenReturnCorrectPartialTakeUpId() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionOption corporateActionAccountOption =
                createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(1), BigDecimal.TEN);

        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption));

        CorporateActionAccountElectionsDto accountElectionsDto = multiBlockExchangeResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(context, corporateActionAccount);

        assertNotNull(accountElectionsDto.getPrimaryAccountElection());
        assertEquals((Integer) 1, accountElectionsDto.getPrimaryAccountElection().getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(accountElectionsDto.getPrimaryAccountElection().getUnits()) == 0);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsAPartialTakeUpButNoValue_thenReturnNull() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionOption corporateActionAccountOption =
                createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(1), (BigDecimal) null);

        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption));

        CorporateActionAccountElectionsDto accountElectionsDto = multiBlockExchangeResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(context, corporateActionAccount);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsNeitherPartialOrFullTakeUp_thenReturnNull() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        CorporateActionOption corporateActionAccountOption =
                createOptionMock(CorporateActionDecisionKey.MINIMUM_PRICE.getCode(1), (BigDecimal) null);

        when(corporateActionAccount.getDecisions()).thenReturn(Arrays.asList(corporateActionAccountOption));

        CorporateActionAccountElectionsDto accountElectionsDto = multiBlockExchangeResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(context, corporateActionAccount);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsNoTakeUp_thenReturnNull() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails corporateActionDetails = mock(CorporateActionDetails.class);
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);

        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);
        when(corporateActionAccount.getDecisions()).thenReturn(null);

        CorporateActionAccountElectionsDto accountElectionsDto = multiBlockExchangeResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(context, corporateActionAccount);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedDetails_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);

        when(savedDetails.getSavedParticipation()).thenReturn(null);

        assertNull(multiBlockExchangeResponseConverterServiceImpl.toSavedAccountElectionsDto(null, null, savedDetails));
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsSavedAccountWithElection_thenReturnPopulatedElectionDto() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);
        CorporateActionSavedAccountElection savedAccountElection = mock(CorporateActionSavedAccountElection.class);
        CorporateActionSavedAccountElectionKey savedAccountElectionKey = mock(CorporateActionSavedAccountElectionKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccountElectionKey.getOptionId()).thenReturn(1);
        when(savedAccountElection.getKey()).thenReturn(savedAccountElectionKey);
        when(savedAccountElection.getUnits()).thenReturn(BigDecimal.TEN);
        when(savedAccount.getAccountElections()).thenReturn(Arrays.asList(savedAccountElection));
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockExchangeResponseConverterServiceImpl.toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNotNull(electionsDto);
        assertNotNull(electionsDto.getPrimaryAccountElection());
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(electionsDto.getPrimaryAccountElection().getUnits()) == 0);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsSavedAccountWithNoElections_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getAccountElections()).thenReturn(new ArrayList<CorporateActionSavedAccountElection>());
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockExchangeResponseConverterServiceImpl.toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNull(electionsDto);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedAccount_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getAccountElections()).thenReturn(new ArrayList<CorporateActionSavedAccountElection>());
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockExchangeResponseConverterServiceImpl.toSavedAccountElectionsDto(null, "1", savedDetails);

        assertNull(electionsDto);
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenNotInvestmentManagerOrDealerGroup_thenSetPartialElectionToTrue() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        when(context.isDealerGroupOrInvestmentManager()).thenReturn(Boolean.FALSE);

        params = multiBlockExchangeResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertTrue(params.getPartialElection());
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenInvestmentManagerOrDealerGroup_thenDoNotSetPartialElection() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        when(context.isDealerGroupOrInvestmentManager()).thenReturn(Boolean.TRUE);

        params = multiBlockExchangeResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertNull(params.getPartialElection());
    }
}
