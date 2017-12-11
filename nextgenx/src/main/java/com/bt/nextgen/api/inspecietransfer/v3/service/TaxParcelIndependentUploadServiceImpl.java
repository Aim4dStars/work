package com.bt.nextgen.api.inspecietransfer.v3.service;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.util.FileParserUtil;
import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelDetailedRow;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelIndependentUploadFile;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelRow;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("TaxParcelIndependentUploadServiceV3")
public class TaxParcelIndependentUploadServiceImpl {

    private static final String ERROR_INVALID_FILETYPE = "Err.IP-0693";

    @Autowired
    private CmsService cmsService;

    @Autowired
    private FileParserUtil fileParser;

    @Autowired
    private TaxParcelUploadUtil util;

    @Autowired
    @Qualifier("InspecieTransferDtoServiceV2")
    private com.bt.nextgen.api.inspecietransfer.v2.service.InspecieTransferDtoService transferMassSettleService;

    public com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto validateFile(
            com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey key,
            MultipartFile file) {

        // TODO: 30/11/2016 Avaloq TCI task required to create new service. Until then using existing but deprecated service.
        InspecieTransferDtoImpl resultDto = (InspecieTransferDtoImpl) transferMassSettleService.find(key,
                new FailFastErrorsImpl());

        if (!fileParser.isValidExcel(file)) {
            resultDto.setWarnings(Collections.singletonList(util.getError(ERROR_INVALID_FILETYPE)));
            return resultDto;
        }

        TransferType transferType = TransferType.forDisplay(resultDto.getTransferType());
        TaxParcelIndependentUploadFile parsedFile = (TaxParcelIndependentUploadFile) fileParser.parse(resultDto.getIsCBO(),
                transferType, resultDto.getSponsorDetails().getPidName(), file, true);

        List<DomainApiErrorDto> errors = new ArrayList<>();
        parsedFile.validate(resultDto, errors);
        resultDto.setWarnings(errors);

        if (resultDto.containsValidationWarningOnly()) {
            resultDto.setTaxParcels(getTaxParcelDtosForRows(parsedFile.getRows()));
        }

        return resultDto;
    }

    private List<com.bt.nextgen.api.inspecietransfer.v2.model.TaxParcelDto> getTaxParcelDtosForRows(List<TaxParcelRow> rows) {
        List<com.bt.nextgen.api.inspecietransfer.v2.model.TaxParcelDto> taxParcels = new ArrayList<>();
        for (TaxParcelRow row : rows) {
            TaxParcelDetailedRow dRow = (TaxParcelDetailedRow) row;
            BigDecimal qty = dRow.getQuantity() == null ? null : new BigDecimal(dRow.getQuantity());
            BigDecimal ocb = dRow.getOriginalCostBase() == null ? null : new BigDecimal(dRow.getOriginalCostBase());
            BigDecimal cb = dRow.getCostBase() == null ? null : new BigDecimal(dRow.getCostBase());
            BigDecimal rcb = dRow.getReducedCostBase() == null ? null : new BigDecimal(dRow.getReducedCostBase());
            BigDecimal icb = dRow.getIndexedCostBase() == null ? null : new BigDecimal(dRow.getIndexedCostBase());
            com.bt.nextgen.api.inspecietransfer.v2.model.TaxParcelDto taxParcel = new com.bt.nextgen.api.inspecietransfer.v2.model.TaxParcelDto(
                    dRow.getAssetCode(), dRow.getAcquisitionDate(), null, qty, cb, rcb, icb);
            taxParcel.setAssetId(row.getAsset().getAssetId());
            taxParcel.setOriginalCostBase(ocb);

            taxParcels.add(taxParcel);
        }
        return taxParcels;
    }
}