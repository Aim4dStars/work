package com.bt.nextgen.api.corporateaction.v1.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.corporateaction.CorporateActionCascadeOrder;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionCascadeOrderType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionTransactionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;


@Service
public class CorporateActionTransactionDetailsConverter {
    @Autowired
    private CorporateActionIntegrationService corporateActionService;

    public List<CorporateActionTransactionDetails> loadTransactionDetails(final CorporateActionContext context,
                                                                          final ServiceErrors serviceErrors) {
        List<String> orderNumbers = processOrderNumbers(context);

        return !orderNumbers.isEmpty() ? corporateActionService.loadCorporateActionTransactionDetails(orderNumbers, serviceErrors) : null;
    }

    public List<CorporateActionTransactionDetails> loadTransactionDetailsForIm(final CorporateActionContext context,
                                                                               final ServiceErrors serviceErrors) {
        List<String> orderNumbers = processOrderNumbers(context);

        return !orderNumbers.isEmpty() ?
               corporateActionService
                       .loadCorporateActionTransactionDetailsForIm(context.getBrokerPositionId(), orderNumbers, serviceErrors) : null;
    }

    private List<String> processOrderNumbers(CorporateActionContext context) {
        List<String> orderNumbers = new ArrayList<>();

        if (CorporateActionType.MULTI_BLOCK.equals(context.getCorporateActionDetails().getCorporateActionType()) ||
                CorporateActionType.BUY_BACK.equals(context.getCorporateActionDetails().getCorporateActionType())) {
            if (context.getCorporateActionDetails().getCascadeOrders() != null) {
                for (CorporateActionCascadeOrder order : context.getCorporateActionDetails().getCascadeOrders()) {
                    // Ignore certain ones - enum is to be revisited
                    if (CorporateActionCascadeOrderType.forId(order.getCorporateActionType()) == null) {
                        orderNumbers.add(order.getOrderNumber());
                    }
                }
            }
        } else {
            // Add primary order number
            orderNumbers.add(context.getCorporateActionDetails().getOrderNumber());
        }

        return orderNumbers;
    }
}
