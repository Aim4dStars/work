package com.bt.nextgen.api.fees.v2.service;

import com.bt.nextgen.api.fees.v2.model.LicenseAdviserFeeDto;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Created by l078480 on 22/11/2016.
 */
public interface LicenseAdviserFeeService {

    public LicenseAdviserFeeDto findLicenseAdviserFee(String adviserPositionId, String productId, ServiceErrors serviceErrors);

    public LicenseAdviserFeeDto findLicenseFeeForDealerGroup(String adviserPositionId, ServiceErrors serviceErrors);

}
