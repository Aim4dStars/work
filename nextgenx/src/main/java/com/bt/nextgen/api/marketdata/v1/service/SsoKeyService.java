package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.marketdata.v1.model.EncryptionFailedException;
import com.bt.nextgen.api.marketdata.v1.service.MarkitOnDemandKeyService.EncryptionStrength;

public interface SsoKeyService
{

	public String getEncryptedKey() throws EncryptionFailedException;

    public String getEncryptedKey(EncryptionStrength strength, AccountKey accountKey) throws EncryptionFailedException;
	public String getEncryptedKey(MarkitOnDemandKeyService.EncryptionStrength strength) throws EncryptionFailedException;

}
