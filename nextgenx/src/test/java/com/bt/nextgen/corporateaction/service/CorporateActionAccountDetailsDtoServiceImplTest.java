package com.bt.nextgen.corporateaction.service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSavedDetails;
import com.bt.nextgen.api.corporateaction.v1.permission.CorporateActionPermissionServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionAccountDetailsConverter;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionAccountDetailsDtoServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionCommonService;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionSupplementaryDetails;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionTransactionDetailsConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionAccountDetailsDtoServiceImplTest {
    @InjectMocks
    private CorporateActionAccountDetailsDtoServiceImpl corporateActionAccountDetails;

    @Mock
    private CorporateActionAccountDetailsConverter accountDetailsConverter;

    @Mock
    private CorporateActionTransactionDetailsConverter transactionDetailsConverter;

    @Mock
    private CorporateActionCommonService commonService;

    @Mock
    private CorporateActionPermissionServiceImpl corporateActionPermissionServiceImpl;

    @Mock
    private CorporateActionContext context;

    @Mock
    private CorporateActionDetails corporateActionDetails;

    @Mock
    private CorporateActionAccount corporateActionAccount;

    @Mock
    private CorporateActionAccountDetailsDto corporateActionAccountDetailsDto;

    private DateTime referenceDateTime = new DateTime();

    @Before
    public void setup() {
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.PUBLIC_OFFER);
        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.OPEN);
        when(corporateActionDetails.getExDate()).thenReturn(referenceDateTime.plusMonths(1));
        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);

        when(corporateActionAccount.getAccountId()).thenReturn("0");

        when(accountDetailsConverter.createAccountDetailsDto(any(CorporateActionContext.class), any(CorporateActionSupplementaryDetails
                .class), any(CorporateActionAccount.class), any(CorporateActionSavedDetails.class), any(ServiceErrors.class)
        )).thenReturn(corporateActionAccountDetailsDto);

        when(corporateActionPermissionServiceImpl.checkInvestorPermission(anyString())).thenReturn(Boolean.TRUE);
    }


    @Test
    public void testToCorporateActionAccountDtoList_whenThereIsNoAccount_thenReturnAnEmptyList() {
        List<CorporateActionAccountDetailsDto> list = corporateActionAccountDetails.toCorporateActionAccountDtoList(null, null, null, null);

        assertTrue(list.isEmpty());
    }

    @Test
    public void testToCorporateActionAccountDtoList_whenThereIsAnAccount_thenReturnANonEmptyList() {
        List<CorporateActionAccountDetailsDto> list = corporateActionAccountDetails
                .toCorporateActionAccountDtoList(context, Arrays.asList(corporateActionAccount), null, null);

        assertFalse(list.isEmpty());
    }

    @Test
    public void testToCorporateActionAccountDtoList_whenThereIsAnAccountButHasNoInvestorPermission_thenReturnAnEmptyList() {
        when(corporateActionPermissionServiceImpl.checkInvestorPermission(anyString())).thenReturn(Boolean.FALSE);

        List<CorporateActionAccountDetailsDto> list =
                corporateActionAccountDetails.toCorporateActionAccountDtoList(context, Arrays.asList(corporateActionAccount), null, null);

        assertTrue(list.isEmpty());
    }

    @Test
    public void testToCorporateActionAccountDtoList_whenThereIsAnAccountButDoesNotHaveAssociatedDetails_thenReturnAnEmptyList() {
        when(corporateActionPermissionServiceImpl.checkInvestorPermission(anyString())).thenReturn(Boolean.TRUE);

        when(accountDetailsConverter
                .createAccountDetailsDto(any(CorporateActionContext.class), any(CorporateActionSupplementaryDetails.class),
                        any(CorporateActionAccount.class), any(CorporateActionSavedDetails.class), any(ServiceErrors.class)))
                .thenReturn(null);

        List<CorporateActionAccountDetailsDto> list =
                corporateActionAccountDetails.toCorporateActionAccountDtoList(context, Arrays.asList(corporateActionAccount), null, null);

        assertTrue(list.isEmpty());
    }

    @Test
    public void testToCorporateActionAccountDtoList_whenThereIsAnAccountButIsMandatoryMandatory_thenReturnANonEmptyList() {
        when(corporateActionPermissionServiceImpl.checkInvestorPermission(anyString())).thenReturn(Boolean.TRUE);

        when(accountDetailsConverter.createAccountDetailsDto(any(CorporateActionContext.class), any(CorporateActionSupplementaryDetails
                .class), any(CorporateActionAccount.class), any(CorporateActionSavedDetails.class), any(ServiceErrors.class)
        )).thenReturn(corporateActionAccountDetailsDto);

        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.CLOSED);

        List<CorporateActionAccountDetailsDto> list =
                corporateActionAccountDetails.toCorporateActionAccountDtoList(context, Arrays.asList(corporateActionAccount), null, null);

        assertFalse(list.isEmpty());

        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.ASSIMILATION_FRACTION);

        list = corporateActionAccountDetails.toCorporateActionAccountDtoList(context, Arrays.asList(corporateActionAccount), null, null);

        assertFalse(list.isEmpty());

        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.PENDING);

        list = corporateActionAccountDetails.toCorporateActionAccountDtoList(context, Arrays.asList(corporateActionAccount), null, null);

        assertFalse(list.isEmpty());

        when(corporateActionDetails.getExDate()).thenReturn(referenceDateTime.minusMonths(1));

        list = corporateActionAccountDetails.toCorporateActionAccountDtoList(context, Arrays.asList(corporateActionAccount), null, null);

        assertFalse(list.isEmpty());

        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.CLOSED);

        list = corporateActionAccountDetails.toCorporateActionAccountDtoList(context, Arrays.asList(corporateActionAccount), null, null);

        assertFalse(list.isEmpty());

        when(corporateActionDetails.getExDate()).thenReturn(referenceDateTime.plusMonths(1));

        list = corporateActionAccountDetails.toCorporateActionAccountDtoList(context, Arrays.asList(corporateActionAccount), null, null);

        assertFalse(list.isEmpty());
    }

    @Test
    public void testToCorporateActionAccountDtoListAsDG_whenThereIsAMandatoryCAAccount_thenReturnNonEmptyListViaDGService() {
        when(context.isDealerGroup()).thenReturn(true);
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.ASSIMILATION_FRACTION);

        List<CorporateActionAccountDetailsDto> list = corporateActionAccountDetails.toCorporateActionAccountDtoList(context,
                Arrays.asList(corporateActionAccount), null, null);

        assertFalse(list.isEmpty());
    }

    @Test
    public void testToCorporateActionAccountDtoListAsDG_whenThereIsAVoluntaryCAAccount_ThenReturnNonEmptyListViaDGService() {
        when(context.isDealerGroup()).thenReturn(true);

        List<CorporateActionAccountDetailsDto> list = corporateActionAccountDetails.toCorporateActionAccountDtoList(context,
                Arrays.asList(corporateActionAccount), null, null);

        assertFalse(list.isEmpty());
    }
}
