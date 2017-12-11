package com.bt.nextgen.service.wrap.integration.income;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import org.joda.time.DateTime;

import java.util.List;

/**
 * This service will be used to retrieve various investment income from wrap
 * Created by L067221 on 1/08/2017.
 */
public interface WrapIncomeIntegrationService {

     /**
      * Method to retreive investment income received from avaloq and wrap
      * @param clientId
      * @param startDate
      * @param endDate
      * @param serviceErrors
      * @return
      */
     List<SubAccountIncomeDetails> loadIncomeReceivedDetails(final String clientId, final DateTime startDate,
                                                             final DateTime endDate,
                                                             final ServiceErrors serviceErrors);
}
