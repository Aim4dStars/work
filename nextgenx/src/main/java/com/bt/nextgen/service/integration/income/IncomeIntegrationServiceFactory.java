package com.bt.nextgen.service.integration.income;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;

import java.util.List;

/**
 * This service will be used to retrieve various investment income from avaloq and wrap.
 * Created by L067221 on 9/08/2017.
 */
public interface IncomeIntegrationServiceFactory {

     /**
      * Method to retrieve investment income received from avaloq and wrap
      * @param accountKey
      * @param startDate
      * @param endDate
      * @param serviceErrors
      * @return
      */
     List<WrapAccountIncomeDetails> loadIncomeReceivedDetails(AccountKey accountKey, DateTime startDate, DateTime endDate,
                                                                    ServiceErrors serviceErrors);

}
