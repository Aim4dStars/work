package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionNonProRataPriorityOfferAccountElectionsDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionOptionDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.service.converter.NonProRataPriorityOfferResponseConverterServiceImpl;
import com.bt.nextgen.core.repository.CorporateActionSavedAccount;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElection;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountElectionKey;
import com.bt.nextgen.core.repository.CorporateActionSavedAccountKey;
import com.bt.nextgen.core.repository.CorporateActionSavedParticipation;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionNonProRataPriorityOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOptionKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NonProRataPriorityOfferResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private NonProRataPriorityOfferResponseConverterServiceImpl nonProRataPriorityOfferResponseConverterServiceImpl;

    @Before
    public void setup() {
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsSubscribedQuantity_thenReturnTakeUpCorporateActionAccountElectionsDto() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        List<CorporateActionOption> decisions = new ArrayList<>();

        decisions.add(createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_QUANTITY.getCode(), BigDecimal.TEN));
        when(account.getDecisions()).thenReturn(decisions);

        CorporateActionAccountElectionsDto accountElectionsDto = nonProRataPriorityOfferResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(null, account);

        assertNotNull(accountElectionsDto);
        assertEquals((Integer) CorporateActionNonProRataPriorityOfferType.TAKE_UP.getId(), accountElectionsDto.getPrimaryAccountElection
                ().getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(accountElectionsDto.getPrimaryAccountElection().getUnits()) == 0);
    }

    @Test
    public void
    testToSubmittedAccountElectionsDto_whenThereIsAnInvalidSubscribedQuantity_thenReturnLapsedCorporateActionAccountElectionsDto() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        List<CorporateActionOption> decisions = new ArrayList<>();

        decisions.add(createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_QUANTITY.getCode(), BigDecimal.ZERO));
        when(account.getDecisions()).thenReturn(decisions);

        CorporateActionAccountElectionsDto accountElectionsDto = nonProRataPriorityOfferResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(null, account);

        assertNotNull(accountElectionsDto);
        assertEquals((Integer) CorporateActionNonProRataPriorityOfferType.LAPSE.getId(), accountElectionsDto.getPrimaryAccountElection()
                                                                                                            .getOptionId());
        assertNull(accountElectionsDto.getPrimaryAccountElection().getUnits());

        decisions.clear();
        decisions.add(createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_QUANTITY.getCode(), (BigDecimal) null));
        when(account.getDecisions()).thenReturn(decisions);

        accountElectionsDto = nonProRataPriorityOfferResponseConverterServiceImpl.toSubmittedAccountElectionsDto(null, account);

        assertEquals((Integer) CorporateActionNonProRataPriorityOfferType.LAPSE.getId(), accountElectionsDto.getPrimaryAccountElection()
                                                                                                            .getOptionId());
        assertNull(accountElectionsDto.getPrimaryAccountElection().getUnits());
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsNoSubscribedQuantity_thenReturnLapsedCorporateActionAccountElectionsDto() {
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        when(account.getDecisions()).thenReturn(new ArrayList<CorporateActionOption>());

        CorporateActionAccountElectionsDto accountElectionsDto = nonProRataPriorityOfferResponseConverterServiceImpl
                .toSubmittedAccountElectionsDto(null, account);

        assertNotNull(accountElectionsDto);
        assertEquals((Integer) CorporateActionNonProRataPriorityOfferType.LAPSE.getId(), accountElectionsDto.getPrimaryAccountElection()
                                                                                                            .getOptionId());
        assertNull(accountElectionsDto.getPrimaryAccountElection().getUnits());

        when(account.getDecisions()).thenReturn(null);

        accountElectionsDto = nonProRataPriorityOfferResponseConverterServiceImpl.toSubmittedAccountElectionsDto(null, account);

        assertNotNull(accountElectionsDto);
        assertEquals((Integer) CorporateActionNonProRataPriorityOfferType.LAPSE.getId(), accountElectionsDto.getPrimaryAccountElection()
                                                                                                            .getOptionId());
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsNoSavedParticipation_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);

        when(savedDetails.getSavedParticipation()).thenReturn(null);

        CorporateActionAccountElectionsDto accountElectionsDto = nonProRataPriorityOfferResponseConverterServiceImpl
                .toSavedAccountElectionsDto(null, null, savedDetails);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsSavedParticipation_thenReturnPopulatedCorporateActionAccountElectionsDto() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);
        CorporateActionSavedAccountElection savedAccountElection = mock(CorporateActionSavedAccountElection.class);
        CorporateActionSavedAccountElectionKey accountElectionKey = mock(CorporateActionSavedAccountElectionKey.class);

        when(accountElectionKey.getOptionId()).thenReturn(1);
        when(savedAccountElection.getKey()).thenReturn(accountElectionKey);
        when(savedAccountElection.getUnits()).thenReturn(BigDecimal.TEN);
        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getAccountElections()).thenReturn(Arrays.asList(savedAccountElection));
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);

        CorporateActionAccountElectionsDto accountElectionsDto = nonProRataPriorityOfferResponseConverterServiceImpl
                .toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNotNull(accountElectionsDto);
        assertTrue(accountElectionsDto instanceof CorporateActionNonProRataPriorityOfferAccountElectionsDtoImpl);

        CorporateActionNonProRataPriorityOfferAccountElectionsDtoImpl nonProRataPriorityOfferAccountElectionsDto =
                (CorporateActionNonProRataPriorityOfferAccountElectionsDtoImpl) accountElectionsDto;

        assertEquals(1, nonProRataPriorityOfferAccountElectionsDto.getOptions().size());

        assertEquals((Integer) 1, nonProRataPriorityOfferAccountElectionsDto.getOptions().get(0).getOptionId());
        assertTrue(BigDecimal.TEN.compareTo(nonProRataPriorityOfferAccountElectionsDto.getOptions().get(0).getUnits()) == 0);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsSavedParticipationButNoSavedAccount_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);

        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);
        when(savedParticipation.getAccounts()).thenReturn(null);

        CorporateActionAccountElectionsDto accountElectionsDto = nonProRataPriorityOfferResponseConverterServiceImpl
                .toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testToSavedAccountElectionsDto_whenThereIsSavedParticipationButNoSavedElections_thenReturnNull() {
        CorporateActionSavedDetails savedDetails = mock(CorporateActionSavedDetails.class);
        CorporateActionSavedParticipation savedParticipation = mock(CorporateActionSavedParticipation.class);
        CorporateActionSavedAccount savedAccount = mock(CorporateActionSavedAccount.class);
        CorporateActionSavedAccountKey savedAccountKey = mock(CorporateActionSavedAccountKey.class);

        when(savedAccountKey.getAccountNumber()).thenReturn("0");
        when(savedAccount.getKey()).thenReturn(savedAccountKey);
        when(savedAccount.getAccountElections()).thenReturn(new ArrayList<CorporateActionSavedAccountElection>());
        when(savedParticipation.getAccounts()).thenReturn(Arrays.asList(savedAccount));
        when(savedDetails.getSavedParticipation()).thenReturn(savedParticipation);

        CorporateActionAccountElectionsDto accountElectionsDto = nonProRataPriorityOfferResponseConverterServiceImpl
                .toSavedAccountElectionsDto(null, "0", savedDetails);

        assertNull(accountElectionsDto);
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenThereAreMinMaxStepOptions_thenSetElectionMinMaxObject() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.MINIMUM_QUANTITY.getCode(), BigDecimal.valueOf(5.0)));
        options.add(createOptionMock(CorporateActionOptionKey.MAXIMUM_QUANTITY.getCode(), BigDecimal.valueOf(50.0)));
        options.add(createOptionMock(CorporateActionOptionKey.STEP.getCode(), BigDecimal.valueOf(5.0)));

        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        params = nonProRataPriorityOfferResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertNotNull(params.getElectionMinMax());
        assertTrue(BigDecimal.valueOf(5.0).compareTo(params.getElectionMinMax().getMinimum()) == 0);
        assertTrue(BigDecimal.valueOf(50.0).compareTo(params.getElectionMinMax().getMaximum()) == 0);
        assertTrue(BigDecimal.valueOf(5.0).compareTo(params.getElectionMinMax().getStep()) == 0);
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenThereIsASubscriptionPrice_thenSetCorporateActionPriceParam() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        List<CorporateActionOption> options = new ArrayList<>();

        options.add(createOptionMock(CorporateActionOptionKey.PRICE.getCode(), BigDecimal.valueOf(1.2)));

        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        params = nonProRataPriorityOfferResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertNotNull(params.getElectionMinMax());
        assertTrue(BigDecimal.valueOf(1.2).compareTo(params.getCorporateActionPrice()) == 0);
    }

    @Test
    public void testSetCorporateActionDetailsDtoParams_whenThereAreNoMinMaxStepOptions_thenSetElectionMinMaxObjectWithNullValues() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        List<CorporateActionOption> options = new ArrayList<>();

        when(details.getOptions()).thenReturn(options);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        params = nonProRataPriorityOfferResponseConverterServiceImpl.setCorporateActionDetailsDtoParams(context, params);

        assertNotNull(params.getElectionMinMax());
        assertNull(params.getElectionMinMax().getMinimum());
        assertNull(params.getElectionMinMax().getMaximum());
        assertNull(params.getElectionMinMax().getStep());
    }

    @Test
    public void testToElectionOptionDtos_whenThereAreElectionOptions_thenReturnListOfOptions() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_QUANTITY.getCode(), BigDecimal.TEN);

        when(details.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<CorporateActionOptionDto> optionDtos = nonProRataPriorityOfferResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertEquals(2, optionDtos.size());

        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("Take up", optionDtos.get(0).getSummary());
        assertTrue(optionDtos.get(0).getIsDefault());
        assertFalse(optionDtos.get(0).getIsNoAction());

        assertEquals("Option B", optionDtos.get(1).getTitle());
        assertEquals("Lapse", optionDtos.get(1).getSummary());
        assertFalse(optionDtos.get(1).getIsDefault());
        assertTrue(optionDtos.get(1).getIsNoAction());
    }

    @Test
    public void testToElectionOptionDtos_whenThereAreElectionOptionsButNoDecision_thenReturnListOfOptions() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        when(details.getDecisions()).thenReturn(new ArrayList<CorporateActionOption>());
        when(context.getCorporateActionDetails()).thenReturn(details);

        List<CorporateActionOptionDto> optionDtos = nonProRataPriorityOfferResponseConverterServiceImpl.toElectionOptionDtos(context, null);

        assertEquals(2, optionDtos.size());

        assertEquals("Option A", optionDtos.get(0).getTitle());
        assertEquals("Take up", optionDtos.get(0).getSummary());
        assertFalse(optionDtos.get(0).getIsDefault());
        assertFalse(optionDtos.get(0).getIsNoAction());

        assertEquals("Option B", optionDtos.get(1).getTitle());
        assertEquals("Lapse", optionDtos.get(1).getSummary());
        assertTrue(optionDtos.get(1).getIsDefault());
        assertTrue(optionDtos.get(1).getIsNoAction());
    }
}
