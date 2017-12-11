package com.bt.nextgen.service.gesb.arrangementreporting.v2;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Created by M040398 (Florin.Adochiei@btfinancialgroup.com) on 19/05/2017.
 */
public interface RetrieveTFNService {
    String getTFN(String cisKey, ServiceErrors errors);
}
