package com.bt.nextgen.service.avaloq.corporateaction;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionTransactionDetailsConverter;
import com.bt.nextgen.service.integration.corporateaction.CorporateAction;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionTransactionDetailsConverterTest {
    private final DateTime currentDateTime = new DateTime();

    @InjectMocks
    private CorporateActionTransactionDetailsConverter converter;

    @Mock
    private CorporateActionIntegrationService corporateActionService;

    private List<CorporateActionTransactionDetails> transList = new ArrayList<>();

    @Mock
    private CorporateActionContext context;

    @Mock
    private CorporateActionDetails corporateActionDetails;

    @Before
    public void setup() {
        CorporateAction corporateAction = mock(CorporateAction.class);
        when(corporateAction.getOrderNumber()).thenReturn("0");
        when(corporateAction.getCorporateActionStatus()).thenReturn(CorporateActionStatus.OPEN);
        when(corporateAction.getAssetId()).thenReturn("1");
        when(corporateAction.getCloseDate()).thenReturn(currentDateTime);
        when(corporateAction.getAnnouncementDate()).thenReturn(currentDateTime);
        when(corporateAction.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(corporateAction.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.CAPITAL_CALL);
        when(corporateAction.getEligible()).thenReturn(1);
        when(corporateAction.getUnconfirmed()).thenReturn(2);

        List<CorporateActionCascadeOrder> cascadeOrders = new ArrayList<>();

        CorporateActionCascadeOrder corporateActionCascadeOrder = mock(CorporateActionCascadeOrder.class);
        when(corporateActionCascadeOrder.getCorporateActionStatus()).thenReturn(CorporateActionStatus.CLOSED);
        when(corporateActionCascadeOrder.getCorporateActionType()).thenReturn("4");
        when(corporateActionCascadeOrder.getOrderNumber()).thenReturn("1");
        when(corporateActionCascadeOrder.getCorporateActionStatus()).thenReturn(CorporateActionStatus.CLOSED);
        when(corporateActionCascadeOrder.getCorporateActionType()).thenReturn("113");
        when(corporateActionCascadeOrder.getOrderNumber()).thenReturn("2");
        cascadeOrders.add(corporateActionCascadeOrder);

        final DateTime dateTime = new DateTime();

        when(corporateActionDetails.getCorporateActionStatus()).thenReturn(CorporateActionStatus.OPEN);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.PUBLIC_OFFER);
        when(corporateActionDetails.getAssetId()).thenReturn("1");
        when(corporateActionDetails.getCloseDate()).thenReturn(dateTime);
        when(corporateActionDetails.getExDate()).thenReturn(dateTime);
        when(corporateActionDetails.getLastUpdatedDate()).thenReturn(dateTime);
        when(corporateActionDetails.getOrderNumber()).thenReturn("0");
        when(corporateActionDetails.getPayDate()).thenReturn(dateTime);
        when(corporateActionDetails.getRecordDate()).thenReturn(dateTime);
        when(corporateActionDetails.getCascadeOrders()).thenReturn(cascadeOrders);

        when(context.getCorporateActionDetails()).thenReturn(corporateActionDetails);
    }

    @Test
    public void testLoadTransactionDetails_whenHasCorporateActionAccounts_thenReturnTransactionDetailsList() {
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        transList = converter.loadTransactionDetails(context, null);
        assertNotNull(transList);

        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.SHARE_PURCHASE_PLAN);
        transList = converter.loadTransactionDetails(context, null);
        assertNotNull(transList);

        when(corporateActionDetails.getCascadeOrders()).thenReturn(null);
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.BUY_BACK);
        transList = converter.loadTransactionDetails(context, null);
        assertNull(transList);

        transList = converter.loadTransactionDetails(context, null);
        assertNull(transList);
    }

    @Test
    public void testLoadTransactionDetailsForIm_whenHasCorporateActionAccounts_thenReturnTransactionDetailsList() {
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        transList = converter.loadTransactionDetailsForIm(context, null);
        assertNotNull(transList);

        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.SHARE_PURCHASE_PLAN);
        transList = converter.loadTransactionDetailsForIm(context, null);
        assertNotNull(transList);

        when(corporateActionDetails.getCascadeOrders()).thenReturn(null);
        when(corporateActionDetails.getCorporateActionType()).thenReturn(CorporateActionType.BUY_BACK);
        transList = converter.loadTransactionDetailsForIm(context, null);
        assertNull(transList);

        transList = converter.loadTransactionDetailsForIm(context, null);
        assertNull(transList);
    }
}
