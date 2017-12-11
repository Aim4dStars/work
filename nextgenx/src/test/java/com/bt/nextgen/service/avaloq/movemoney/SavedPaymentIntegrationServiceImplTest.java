package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.movemoney.SavedPaymentIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.transaction.SavedPayment;
import com.bt.nextgen.service.avaloq.transaction.SavedPaymentsHolderImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by L067218 on 10/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavedPaymentIntegrationServiceImplTest {

    private static final String ACCOUNT_NUMBER = "123";
    List<String> orderTypes = new ArrayList<>();
    @Mock
    private AvaloqExecute avaloqExecute;

    @Mock
    private SavedPaymentsHolderImpl holder;

    @InjectMocks
    private SavedPaymentIntegrationServiceImpl service;

    @Mock
    private ServiceErrors serviceErrors;

    @Test
    public void getSavedPayments() {
        final List<SavedPayment> result;

        //Add codes to this list
        orderTypes.add("pay.pay#super_pens_oneoff");
        orderTypes.add("pay.pay#super_opn_lmpsm");
        orderTypes.add("pay.stord_new_super_pens");
        orderTypes.add("pay.pay.sa_stord_mdf");
        when(avaloqExecute.executeReportRequestToDomain(any(AvaloqReportRequest.class),
                eq(SavedPaymentsHolderImpl.class), any(ServiceErrors.class))).thenReturn(holder);

        result = service.loadSavedPensionPayments(ACCOUNT_NUMBER, orderTypes, serviceErrors);

        verify(avaloqExecute).executeReportRequestToDomain(any(AvaloqReportRequest.class),
                eq(SavedPaymentsHolderImpl.class), eq(serviceErrors));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSavedPaymentsWithNullAccountKey() {
        //Add codes to this list
        orderTypes.add("pay.pay#super_pens_oneoff");
        orderTypes.add("pay.pay#super_opn_lmpsm");
        orderTypes.add("pay.stord_new_super_pens");
        orderTypes.add("pay.pay.sa_stord_mdf");
        service.loadSavedPensionPayments(null, orderTypes, serviceErrors);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSavedPaymentsWithNullOrderTypes() {
        service.loadSavedPensionPayments(ACCOUNT_NUMBER, null, serviceErrors);
    }

}
