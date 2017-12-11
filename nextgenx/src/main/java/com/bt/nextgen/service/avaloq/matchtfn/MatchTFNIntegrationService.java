package com.bt.nextgen.service.avaloq.matchtfn;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.matchtfn.model.MatchTFN;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorService;

/**
 * Created by L070354 on 13/07/2017.
 *
 * This Integration service has been created to match the TFN in Avaloq against the person id
 * which is being used in the supercheck journey for Direct Super with Existing Customers.
 */
public interface MatchTFNIntegrationService{
    /**
     * Match Super TFN in ABS
     *
     * @param personId
     *            - Avaloq Person Id
     * @param serviceErrors
     *            - Errors
     * @return  Specify whether the TFN Matched
     */
    public boolean doMatchTFN(String personId, String tfn, ServiceErrors serviceErrors);

}
