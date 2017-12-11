package com.bt.nextgen.api.inspecietransfer.v3.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.util.List;

public interface InspecieTransferDto extends KeyedDto<InspecieTransferKey> {

    public InspecieTransferKey getKey();

    public String getOrderType();

    public String getTransferType();

    public Boolean getIsCBO();

    public DateTime getTransferDate();

    public AccountKey getSourceAccountKey();

    public AccountKey getTargetAccountKey();

    public String getSourceContainerId();

    public String getTargetContainerId();

    public String getTargetAssetId();

    public List<TransferAssetDto> getTransferAssets();

    public List<TransferPreferenceDto> getTransferPreferences();

    public String getAction();

    public List<DomainApiErrorDto> getWarnings();

    public void setKey(InspecieTransferKey key);

    public boolean getIsFullClose();

    public boolean containsValidationWarningOnly();

    public String getIncomePreference();
}
