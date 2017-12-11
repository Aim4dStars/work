package com.bt.nextgen.api.inspecietransfer.v3.util;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.model.TransferAssetDto;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelUploadFile;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.service.avaloq.transfer.transfergroup.TransferGroupDetailsImpl;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("TaxParcelUploadUtilV3")
public class TaxParcelUploadUtil {

    @Autowired
    private CmsService cmsService;

    private DomainApiErrorDto getError(String errorCode, String[] params, ErrorType errorType) {
        String errorMessage = null;
        if (params != null) {
            errorMessage = cmsService.getDynamicContent(errorCode, params);
        } else {
            errorMessage = cmsService.getContent(errorCode);
        }
        return new DomainApiErrorDto(errorCode, null, null, errorMessage, errorType);
    }

    public DomainApiErrorDto getError(String errorCode) {
        return getError(errorCode, null, ErrorType.ERROR);
    }

    public DomainApiErrorDto getError(String errorCode, String[] params) {
        return getError(errorCode, params, ErrorType.ERROR);
    }

    public DomainApiErrorDto getWarning(String errorCode) {
        return getError(errorCode, null, ErrorType.WARNING);
    }

    public DomainApiErrorDto getWarning(String errorCode, String[] params) {
        return getError(errorCode, params, ErrorType.WARNING);
    }

    public InspecieTransferDto getDtoForTransfer(InspecieTransferDto transferDto, TaxParcelUploadFile parsedFile,
            TransferType transferType, String pid) {
        String accountId = transferDto.getTargetAccountKey().getAccountId();

        TransferGroupDetailsImpl groupDetails = new TransferGroupDetailsImpl(null, transferType, transferDto.getIsCBO());
        groupDetails.setDestAssetId(transferDto.getTargetAssetId());
        groupDetails.setDestContainerId(transferDto.getTargetContainerId());
        groupDetails.setTargetAccountId(accountId);
        groupDetails.setSourceAccountId(transferDto.getSourceAccountKey().getAccountId());
        groupDetails.setExternalTransferType(transferType);
        groupDetails.setOrderType(OrderType.IN_SPECIE_TRANSFER);
        groupDetails.setTransferDate(groupDetails.getTransferDate());
        groupDetails.setIncomePreference(IncomePreference.forIntlId(transferDto.getIncomePreference()));

        List<TransferAssetDto> xferAssetDtoList = TaxParcelConverter.constructTransferAssetFromTaxParcels(parsedFile.getRows(),
                pid, transferDto);

        return new InspecieTransferDtoImpl(accountId, groupDetails, xferAssetDtoList, transferDto.getTransferPreferences(),
                transferDto.getWarnings());
    }

    public boolean containsValidationWarningOnly(List<DomainApiErrorDto> errors) {
        if (errors != null) {
            for (DomainApiErrorDto error : errors) {
                String errType = error.getErrorType();
                if (DomainApiErrorDto.ErrorType.ERROR.toString().equals(errType)) {
                    return false;
                }
            }
        }
        return true;
    }
}
