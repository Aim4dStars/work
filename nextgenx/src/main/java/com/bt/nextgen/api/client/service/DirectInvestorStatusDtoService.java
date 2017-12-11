package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.GcmKey;
import com.bt.nextgen.core.api.dto.FindOneDtoService;

/**
 * Return PAN number, if it is found in the investor's SAML token
 */
public interface DirectInvestorStatusDtoService extends FindOneDtoService<GcmKey> {
}