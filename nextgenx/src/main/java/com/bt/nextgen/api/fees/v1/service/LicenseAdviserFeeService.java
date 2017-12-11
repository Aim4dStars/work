package com.bt.nextgen.api.fees.v1.service;

import com.bt.nextgen.api.fees.v1.model.LicenseAdviserFeeDto;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Created by l078480 on 22/11/2016.
 */

/**
 * @deprecated Use V2
 */
@Deprecated
public interface LicenseAdviserFeeService {

    public LicenseAdviserFeeDto findLicenseAdviserFee(String adviserPositionId, String productId, ServiceErrors serviceErrors);

    public LicenseAdviserFeeDto findLicenseFeeForDealerGroup(String adviserPositionId, ServiceErrors serviceErrors);

}
