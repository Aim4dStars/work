package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.transaction.SavedPayment;
import com.bt.nextgen.service.avaloq.transaction.SavedPaymentsHolder;
import com.bt.nextgen.service.avaloq.transaction.SavedPaymentsHolderImpl;
import com.bt.nextgen.service.integration.movemoney.SavedPaymentIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bt.nextgen.service.avaloq.Template.SAVED_PAYMENTS;

/**
 * Created by L067218 on 27/01/2017.
 *
 *
 */
@Service
public class SavedPaymentIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements SavedPaymentIntegrationService {

    /**
     * Executor for avaloq requests.
     */
    @Autowired
    private AvaloqExecute avaloqExecute;


    @Override
    public List<SavedPayment> loadSavedPensionPayments(String accountNumber, List<String> orderTypes, ServiceErrors serviceErrors) {
        List<SavedPayment> pensionPayments = new ArrayList<>();
        if (accountNumber == null || orderTypes == null ) {
            throw new IllegalArgumentException("Account key, Order Types must be specified");
        }


        final AvaloqReportRequest avaloqReportRequest = new AvaloqReportRequest(SAVED_PAYMENTS.getName())
                .forBpListId(Collections.singletonList(accountNumber))
                .forOrderTypeList(orderTypes);

        SavedPaymentsHolder holder = avaloqExecute.executeReportRequestToDomain(avaloqReportRequest,
                SavedPaymentsHolderImpl.class, serviceErrors);

        if (holder != null && holder.getPensionPayments() != null) {
            pensionPayments =  holder.getPensionPayments();

        }

        return pensionPayments;

    }

}



