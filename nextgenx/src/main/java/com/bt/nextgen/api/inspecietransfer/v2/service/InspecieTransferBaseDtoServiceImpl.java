package com.bt.nextgen.api.inspecietransfer.v2.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.TaxParcelDto;
import com.bt.nextgen.api.inspecietransfer.v2.validation.InspecieTransferDtoErrorMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.transfer.SponsorDetailsImpl;
import com.bt.nextgen.service.avaloq.transfer.TransferDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transfer.InspecieAsset;
import com.bt.nextgen.service.integration.transfer.SponsorDetails;
import com.bt.nextgen.service.integration.transfer.TaxParcel;
import com.bt.nextgen.service.integration.transfer.TransferDetails;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @deprecated Use V3
 */
@Deprecated
public class InspecieTransferBaseDtoServiceImpl {

    @Autowired
    private InspecieTransferDtoErrorMapper inspecieTransferErrorMapper;

    @Autowired
    private TransferAssetHelper assetHelper;

    protected List<TaxParcelDto> toTaxParcelsDto(List<TaxParcel> taxParcels) {

        return Lambda.convert(taxParcels, new Converter<TaxParcel, TaxParcelDto>() {
            @Override
            public TaxParcelDto convert(TaxParcel taxParcel) {
                return TaxParcelConverter.toDto(taxParcel);
            }
        });
    }

    protected SponsorDetailsDtoImpl populateSponsorDetailsDto(SponsorDetails sponsorDetails, TransferType transferType) {
        if (sponsorDetails == null) {
            return null;
        }
        SponsorDetailsDtoImpl sponsorDetailsDto = new SponsorDetailsDtoImpl();

        switch (transferType) {
            case LS_BROKER_SPONSORED:
            case LS_OTHER:
                sponsorDetailsDto.setPid(sponsorDetails.getSponsorId());
                sponsorDetailsDto.setPidName(sponsorDetails.getSponsorName());
                sponsorDetailsDto.setHin(sponsorDetails.getInvestmentId());
                break;
            case LS_ISSUER_SPONSORED:
                sponsorDetailsDto.setSrn(sponsorDetails.getInvestmentId());
                break;
            case MANAGED_FUND:
                sponsorDetailsDto.setCustodian(sponsorDetails.getPlatformId());
                sponsorDetailsDto.setAccNumber(sponsorDetails.getInvestmentId());
                break;
            default:
                break;

        }
        return sponsorDetailsDto;
    }

    public TransferDetails toTransferDetails(InspecieTransferDto transferDto) {
        String transferId = null;
        if (transferDto.getKey() != null && transferDto.getKey().getTransferId() != null) {
            transferId = transferDto.getKey().getTransferId();
        }

        TransferDetailsImpl transferDetails = new TransferDetailsImpl(transferId, TransferType.forDisplay(transferDto
                .getTransferType()), transferDto.getIsCBO(), toTransferAssets(transferDto.getSettlementRecords()));

        if (transferDto.getDest() != null) {
            transferDetails.setDestContainerId(transferDto.getDest().getDestContainerId() != null ? EncodedString
                    .toPlainText(transferDto.getDest().getDestContainerId()) : null);
            transferDetails
                    .setDestAssetId(transferDto.getDest().getAssetId() != null ? transferDto.getDest().getAssetId() : null);
        }

        transferDetails.setAccountKey(AccountKey.valueOf(transferDto.getKey().getAccountId()));
        transferDetails.setSponsorDetails(constructSponsorDetails(transferDto));
        transferDetails.setTaxParcels(toTaxParcels(transferDto.getTaxParcels()));
        transferDetails.setValidationErrors(inspecieTransferErrorMapper.mapWarnings(transferDto.getWarnings()));
        return transferDetails;

    }

    protected List<TaxParcel> toTaxParcels(List<TaxParcelDto> taxParcelsDto) {
        return Lambda.convert(taxParcelsDto, new Converter<TaxParcelDto, TaxParcel>() {
            @Override
            public TaxParcel convert(TaxParcelDto taxParcelDto) {
                return TaxParcelConverter.fromDto(taxParcelDto);
            }
        });
    }

    protected SponsorDetailsImpl constructSponsorDetails(InspecieTransferDto transferDto) {
        SponsorDetailsImpl sponsorDetails = new SponsorDetailsImpl();
        switch (TransferType.forDisplay(transferDto.getTransferType())) {
            case LS_BROKER_SPONSORED:
            case LS_OTHER:
                sponsorDetails.setSponsorId(transferDto.getSponsorDetails().getPid());
                sponsorDetails.setInvestmentId(transferDto.getSponsorDetails().getHin());
                break;
            case LS_ISSUER_SPONSORED:
                sponsorDetails.setInvestmentId(transferDto.getSponsorDetails().getSrn());
                break;
            case MANAGED_FUND:
                sponsorDetails.setPlatformId(transferDto.getSponsorDetails().getCustodian());
                sponsorDetails.setInvestmentId(transferDto.getSponsorDetails().getAccNumber());
                break;
            default:
                break;

        }
        return sponsorDetails;
    }

    protected List<InspecieAsset> toTransferAssets(List<SettlementRecordDto> transferAssets) {

        return Lambda.convert(transferAssets, new Converter<SettlementRecordDtoImpl, InspecieAsset>() {
            @Override
            public InspecieAsset convert(SettlementRecordDtoImpl transferAsset) {
                return TransferAssetConverter.fromDto(transferAsset);
            }
        });

    }

    public InspecieTransferDto toTransferDto(TransferDetailsImpl transferDetails) {
        TransactionResponse txnResp = (TransactionResponse) transferDetails;
        InspecieTransferDtoImpl transferDto = new InspecieTransferDtoImpl(transferDetails.getTransferType().getDisplayName(),
                populateSponsorDetailsDto(transferDetails.getSponsorDetails(), transferDetails.getTransferType()),

                assetHelper.toTransferAssetsDto(transferDetails.getTransferAssets()), EncodedString.fromPlainText(
                        transferDetails.getDestContainerId()).toString(), new InspecieTransferKey(transferDetails.getAccountKey()
                        .getId(), transferDetails.getTransferId()), transferDetails.isChangeOfBeneficialOwnership(),
                inspecieTransferErrorMapper.map(txnResp.getValidationErrors()));

        if (transferDetails.getDestAssetId() != null) {
            transferDto.getDest().setAssetId(transferDetails.getDestAssetId());
        }
        if (transferDetails.getTaxParcels() != null && !transferDetails.getTaxParcels().isEmpty()) {
            transferDto.setTaxParcels(toTaxParcelsDto(transferDetails.getTaxParcels()));
        }
        if (transferDetails.getStatus() != null) {
            transferDto.setTransferStatus(transferDetails.getStatus().name());
        }
        return transferDto;
    }

}