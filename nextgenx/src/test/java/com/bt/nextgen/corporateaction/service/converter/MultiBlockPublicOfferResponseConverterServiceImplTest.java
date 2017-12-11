package com.bt.nextgen.corporateaction.service.converter;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountElectionsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionMultiBlockAccountElectionDtoImpl;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MultiBlockPublicOfferResponseConverterServiceImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDecisionKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MultiBlockPublicOfferResponseConverterServiceImplTest extends AbstractCorporateActionConverterTest {
    @InjectMocks
    private MultiBlockPublicOfferResponseConverterServiceImpl multiBlockPublicOfferResponseConverterService;

    @Test
    public void testToSubmittedAccountElectionsDto_whenThereIsSelectedOptionAndThereHasTakeOverLimit_thenReturnAnElectionDtoWithUnits() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "50.0", BigDecimal.valueOf(50.0));

        when(details.getTakeoverLimit()).thenReturn(BigDecimal.TEN);
        when(account.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockPublicOfferResponseConverterService.toSubmittedAccountElectionsDto(context, account);

        assertNotNull(electionsDto);
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());

        assertTrue(electionsDto.getPrimaryAccountElection() instanceof CorporateActionMultiBlockAccountElectionDtoImpl);
        CorporateActionMultiBlockAccountElectionDtoImpl multiBlockAccountElectionDto =
                (CorporateActionMultiBlockAccountElectionDtoImpl) electionsDto.getPrimaryAccountElection();
        assertTrue(BigDecimal.valueOf(50.0).compareTo(multiBlockAccountElectionDto.getPercent()) == 0);
        assertNull(multiBlockAccountElectionDto.getUnits());
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenHasTakeOverLimitWithNoQuantityButPercent_thenReturnAnElectionDtoWithUnits() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        when(account.getAvailableQuantity()).thenReturn(BigDecimal.valueOf(300));

        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "50.0", BigDecimal.valueOf(50.0));

        when(details.getTakeoverLimit()).thenReturn(BigDecimal.TEN);
        when(account.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockPublicOfferResponseConverterService.toSubmittedAccountElectionsDto(context, account);

        assertNotNull(electionsDto);
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());

        assertTrue(electionsDto.getPrimaryAccountElection() instanceof CorporateActionMultiBlockAccountElectionDtoImpl);
        CorporateActionMultiBlockAccountElectionDtoImpl multiBlockAccountElectionDto =
                (CorporateActionMultiBlockAccountElectionDtoImpl) electionsDto.getPrimaryAccountElection();
        assertTrue(BigDecimal.valueOf(50.0).compareTo(multiBlockAccountElectionDto.getPercent()) == 0);
        assertNull(multiBlockAccountElectionDto.getUnits());
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenHasTakeOverLimitWithNoQuantityOrPercent_thenReturnNullElectionDto() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);

        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.SUBSCRIBED_OPTION.getCode(1), "1");

        when(details.getTakeoverLimit()).thenReturn(BigDecimal.TEN);
        when(account.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockPublicOfferResponseConverterService.toSubmittedAccountElectionsDto(context, account);

        assertNull(electionsDto);
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenHasSelectedOptionAndNoTakeOverLimit_thenReturnAnElectionDtoWithPercent() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.PERCENT.getCode(1), "50.0", BigDecimal.valueOf(50.0));

        when(details.getTakeoverLimit()).thenReturn(null);
        when(account.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockPublicOfferResponseConverterService.toSubmittedAccountElectionsDto(context, account);

        assertNotNull(electionsDto);
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());

        assertTrue(electionsDto.getPrimaryAccountElection() instanceof CorporateActionMultiBlockAccountElectionDtoImpl);
        CorporateActionMultiBlockAccountElectionDtoImpl multiBlockAccountElectionDto =
                (CorporateActionMultiBlockAccountElectionDtoImpl) electionsDto.getPrimaryAccountElection();
        assertTrue(BigDecimal.valueOf(50.0).compareTo(multiBlockAccountElectionDto.getPercent()) == 0);
        assertNull(multiBlockAccountElectionDto.getUnits());
    }

    @Test
    public void testToSubmittedAccountElectionsDto_whenHasSelectedOptionAndNoTakeOverLimitOrPercentageButHasQty_thenReturnAnElectionDtoWithCalculatedPercent() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);
        CorporateActionAccount account = mock(CorporateActionAccount.class);
        when(account.getAvailableQuantity()).thenReturn(BigDecimal.valueOf(200.0));
        CorporateActionOption decision = createOptionMock(CorporateActionDecisionKey.QUANTITY.getCode(1), "50.0", BigDecimal.valueOf(50.0));

        when(details.getTakeoverLimit()).thenReturn(null);
        when(account.getDecisions()).thenReturn(Arrays.asList(decision));
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionAccountElectionsDto electionsDto =
                multiBlockPublicOfferResponseConverterService.toSubmittedAccountElectionsDto(context, account);

        assertNotNull(electionsDto);
        assertEquals((Integer) 1, electionsDto.getPrimaryAccountElection().getOptionId());

        CorporateActionMultiBlockAccountElectionDtoImpl multiBlockAccountElectionDto =
                (CorporateActionMultiBlockAccountElectionDtoImpl) electionsDto.getPrimaryAccountElection();
        assertTrue(BigDecimal.valueOf(25.0).compareTo(multiBlockAccountElectionDto.getPercent()) == 0);
        assertNull(multiBlockAccountElectionDto.getUnits());
    }

    @Test
    public void testSetCorporateActionDetailsDtoParam() {
        CorporateActionContext context = mock(CorporateActionContext.class);
        CorporateActionDetails details = mock(CorporateActionDetails.class);

        when(details.getTakeoverLimit()).thenReturn(BigDecimal.TEN);
        when(context.getCorporateActionDetails()).thenReturn(details);

        CorporateActionDetailsDtoParams params = new CorporateActionDetailsDtoParams();

        params = multiBlockPublicOfferResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertNull(params.getPartialElection());
        assertTrue(BigDecimal.TEN.compareTo(params.getMaxTakeUpPercent()) == 0);

        when(details.getTakeoverLimit()).thenReturn(null);
        params = new CorporateActionDetailsDtoParams();
        params = multiBlockPublicOfferResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertNull(params.getPartialElection());
        assertNull(params.getMaxTakeUpPercent());

        when(details.getTakeoverLimit()).thenReturn(BigDecimal.ZERO);
        params = new CorporateActionDetailsDtoParams();
        params = multiBlockPublicOfferResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertNull(params.getPartialElection());
        assertNull(params.getMaxTakeUpPercent());

        when(details.getTakeoverLimit()).thenReturn(BigDecimal.valueOf(100.0));
        params = new CorporateActionDetailsDtoParams();
        params = multiBlockPublicOfferResponseConverterService.setCorporateActionDetailsDtoParams(context, params);

        assertNull(params.getPartialElection());
        assertNull(params.getMaxTakeUpPercent());
    }
}