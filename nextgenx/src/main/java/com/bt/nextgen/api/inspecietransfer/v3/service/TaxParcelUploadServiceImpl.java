package com.bt.nextgen.api.inspecietransfer.v3.service;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v3.util.FileParserUtil;
import com.bt.nextgen.api.inspecietransfer.v3.util.TaxParcelUploadUtil;
import com.bt.nextgen.api.inspecietransfer.v3.util.vetting.TaxParcelUploadFile;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.service.integration.transfer.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("TaxParcelUploadServiceV3")
public class TaxParcelUploadServiceImpl implements TaxParcelUploadService {

    private static final String ERROR_INVALID_FILETYPE = "Err.IP-0693";

    @Autowired
    private CmsService cmsService;

    @Autowired
    private FileParserUtil fileParser;

    @Autowired
    private TaxParcelUploadUtil util;

    public InspecieTransferDto validateFile(InspecieTransferDto transferDto, String pid, String sponsorName, MultipartFile file) {
        InspecieTransferDtoImpl resultDto = new InspecieTransferDtoImpl();
        if (!fileParser.isValidExcel(file)) {
            resultDto.setWarnings(Collections.singletonList(util.getError(ERROR_INVALID_FILETYPE)));
            return resultDto;
        }

        TransferType transferType = TransferType.forDisplay(transferDto.getTransferType());
        TaxParcelUploadFile parsedFile = fileParser.parse(transferDto.getIsCBO(), transferType, sponsorName, file, false);

        List<DomainApiErrorDto> errors = new ArrayList<>();
        parsedFile.validate(errors);
        resultDto.setWarnings(errors);

        if (resultDto.containsValidationWarningOnly()) {
            ((InspecieTransferDtoImpl) transferDto).setWarnings(errors);
            return util.getDtoForTransfer(transferDto, parsedFile, transferType, pid);
        }
        return resultDto;
    }
}