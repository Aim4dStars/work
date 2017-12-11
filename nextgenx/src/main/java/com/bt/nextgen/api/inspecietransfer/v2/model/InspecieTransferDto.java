package com.bt.nextgen.api.inspecietransfer.v2.model;

import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @deprecated Use V3
 */
@Deprecated
public interface InspecieTransferDto extends KeyedDto<InspecieTransferKey> {

    public InspecieTransferKey getKey();

    public String getTransferType();

    public SponsorDetailsDtoImpl getSponsorDetails();

    public List<SettlementRecordDto> getSettlementRecords();

    public List<TaxParcelDto> getTaxParcels();

    public Boolean getIsCBO();

    public String getAction();

    public List<DomainApiErrorDto> getWarnings();

    public DateTime getTransferDate();

    public String getTransferStatus();

    public String getDestContainerId();

    public String getDestAssetId();

    public TransferDest getDest();

    public boolean containsValidationWarningOnly();

}
