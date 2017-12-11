package com.bt.nextgen.service.avaloq.dealergroupparams;

import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.DealerParameterKey;
import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DealerGroupParamsIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        DealerGroupParamsIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(DealerGroupParamsIntegrationServiceImpl.class);

    @Autowired
    private AvaloqReportService avaloqService;

    @Override
    public List<DealerGroupParams> loadCustomerAccountObjects(final DealerParameterKey dealerParamKey,
            final ServiceErrors serviceErrors) {

        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(DealerGroupParamsTemplate.CUSTOMER_ACCOUNTING_FOR_DEALER_GROUP)
                .forParam(DealerGroupParamsReqParams.PARAM_DEALER_GROUP_OE_ID, dealerParamKey.getBrokerKey().getId()).forParam(
                        DealerGroupParamsReqParams.PARAM_IPS_TYPE, dealerParamKey.getAccountType());
        DealerGroupParamsResponse response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                DealerGroupParamsResponseImpl.class, serviceErrors);

        if (response == null || response.getCustomerAccountObjects().isEmpty()) {
            String message = "No customer accounting defaults found for broker " + dealerParamKey.getBrokerKey().getId();
            logger.error(message);
            serviceErrors.addError(new ServiceErrorImpl(message));
            return Collections.emptyList();
        }

        return response.getCustomerAccountObjects();
    }

}