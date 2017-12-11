package com.bt.nextgen.service.avaloq.modelportfolio.orderstatus;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.ModelOrderSummaryResponse;
import com.bt.nextgen.service.integration.modelportfolio.orderstatus.OrderSummaryIntegrationService;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("avaloqOrderSummaryIntegrationService")
public class OrderSummaryIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        OrderSummaryIntegrationService {

    @Autowired
    private AvaloqReportService avaloqService;

    public ModelOrderSummaryResponse loadOrderStatusSummary(final BrokerKey brokerKey, final DateTime dateTime,
            final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<ModelOrderSummaryResponse>("loadOrderStatusSummary", serviceErrors) {

            @Override
            public ModelOrderSummaryResponse performOperation() {

                AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(OrderSummaryTemplate.ORDER_STATUS_SUMMARY)
                        .forParam(OrderSummaryParams.PARAM_BROKER_ID, brokerKey.getId())
                        .forParam(OrderSummaryParams.PARAM_VAL_DATE_FROM, dateTime)
                        .forParam(OrderSummaryParams.PARAM_VAL_DATE_TO, dateTime);

                ModelOrderSummaryResponse response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                        OrderSummaryResponseImpl.class, serviceErrors);
                return response;
            }
        }.run();
    }
}
