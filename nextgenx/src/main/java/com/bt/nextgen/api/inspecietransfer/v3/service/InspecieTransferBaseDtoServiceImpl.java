package com.bt.nextgen.api.inspecietransfer.v3.service;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferAssetDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferPreferenceDto;
import com.bt.nextgen.api.inspecietransfer.v3.util.ModelPreferenceConverter;
import com.bt.nextgen.api.inspecietransfer.v3.util.TransferAssetConverter;
import com.bt.nextgen.api.inspecietransfer.v3.validation.InspecieTransferDtoErrorMapper;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transfer.transfergroup.TransferGroupDetailsImpl;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transfer.BeneficialOwnerChangeStatus;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class InspecieTransferBaseDtoServiceImpl {

    @Autowired
    private InspecieTransferDtoErrorMapper inspecieTransferErrorMapper;

    @Autowired
    @Qualifier("TransferAssetConverterV3")
    private TransferAssetConverter transferAssetConverter;

    private static Integer DRAW_DOWN_DELAY = 30;

    public TransferGroupDetailsImpl toTransferGroupDetails(InspecieTransferDto transferDto) {

        TransferGroupDetailsImpl transferGroupDetails = new TransferGroupDetailsImpl();
        transferGroupDetails.setTransferId(transferDto.getKey() == null ? null : transferDto.getKey().getTransferId());
        transferGroupDetails.setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus.NO);
        if (transferDto.getIsCBO() != null && Boolean.valueOf(transferDto.getIsCBO())) {
            // Set the isCBO attribute if required.
            transferGroupDetails.setChangeOfBeneficialOwnership(BeneficialOwnerChangeStatus.YES);
        }

        TransferType transferType = TransferType.forDisplay(transferDto.getTransferType());
        transferGroupDetails.setExternalTransferType(transferType);
        transferGroupDetails.setOrderType(OrderType.forDisplay(transferDto.getOrderType()));
        transferGroupDetails.setCloseAfterTransfer(transferDto.getIsFullClose());
        transferGroupDetails.setIncomePreference(IncomePreference.forIntlId(transferDto.getIncomePreference()));

        // If target container-id is provided (i.e. an existing container), the target asset-id will be ignored.
        if (transferDto.getTargetContainerId() != null) {
            transferGroupDetails.setDestContainerId(decodeString(transferDto.getTargetContainerId()));
        } else {
            transferGroupDetails.setDestAssetId(transferDto.getTargetAssetId());
        }

        // Source container and account are only required for intra-account transfer.
        if (OrderType.IN_SPECIE_TRANSFER != transferGroupDetails.getOrderType()) {
            if (transferDto.getSourceAccountKey() != null) {
                transferGroupDetails.setSourceAccountId(decodeString(transferDto.getSourceAccountKey().getAccountId()));
            }
            transferGroupDetails.setSourceContainerId(decodeString(transferDto.getSourceContainerId()));
        } else {
            transferGroupDetails.setDrawdownDelayDays(DRAW_DOWN_DELAY);
        }

        if (transferDto.getTargetAccountKey() != null) {
            transferGroupDetails.setTargetAccountId(decodeString(transferDto.getTargetAccountKey().getAccountId()));
        }

        transferGroupDetails.setTransferDate(transferDto.getTransferDate());
        transferGroupDetails.setPreferenceList(ModelPreferenceConverter.fromDtoList(transferDto.getTransferPreferences()));

        String sourceAccId = transferDto.getSourceAccountKey() == null ? null : decodeString(transferDto.getSourceAccountKey()
                .getAccountId());
        transferGroupDetails.setTransferAssets(transferAssetConverter.fromDtoList(transferDto.getTransferAssets(), sourceAccId,
                transferType));

        transferGroupDetails.setValidationErrors(inspecieTransferErrorMapper.mapWarnings(transferDto.getWarnings()));
        
        return transferGroupDetails;
    }

    public InspecieTransferDto toTransferDto(String accountId, TransferGroupDetails transferDetails, ServiceErrors serviceErrors) {
        if (transferDetails != null) {
            TransactionResponse response = (TransactionResponse) transferDetails;
            List<DomainApiErrorDto> warnings = inspecieTransferErrorMapper.map(response.getValidationErrors());
            List<TransferAssetDto> transferAssets = transferAssetConverter.toDtoList(transferDetails.getTransferAssets(),
                    transferDetails.getExternalTransferType(), serviceErrors);
            List<TransferPreferenceDto> transferPreferences = ModelPreferenceConverter.toDtoList(transferDetails
                    .getPreferenceList());
            InspecieTransferDtoImpl impl = new InspecieTransferDtoImpl(accountId, transferDetails, transferAssets,
                    transferPreferences, warnings);
            if (transferDetails.getSourceAccountKey() != null) {
                com.bt.nextgen.api.account.v3.model.AccountKey accKey = new com.bt.nextgen.api.account.v3.model.AccountKey(
                        EncodedString.fromPlainText(transferDetails.getSourceAccountKey().getId()).toString());
                impl.setSourceAccountKey(accKey);
            }
            if (transferDetails.getTargetAccountKey() != null) {
                com.bt.nextgen.api.account.v3.model.AccountKey accKey = new com.bt.nextgen.api.account.v3.model.AccountKey(
                        EncodedString.fromPlainText(transferDetails.getTargetAccountKey().getId()).toString());
                impl.setTargetAccountKey(accKey);
            }
            if (transferDetails.getDestContainerId() != null) {
                impl.setTargetContainerId(EncodedString.fromPlainText(transferDetails.getDestContainerId()).toString());
            }

            if (transferDetails.getSourceContainerId() != null) {
                impl.setSourceContainerId(EncodedString.fromPlainText(transferDetails.getSourceContainerId()).toString());
                impl.setFullClose(transferDetails.getCloseAfterTransfer());
            }
            return impl;
        }
        return null;

    }

    private String decodeString(String idString) {
        if (idString != null) {
            return EncodedString.toPlainText(idString);
        }
        return null;
    }

}