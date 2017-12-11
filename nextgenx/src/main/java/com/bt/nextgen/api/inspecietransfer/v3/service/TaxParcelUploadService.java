package com.bt.nextgen.api.inspecietransfer.v3.service;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import org.springframework.web.multipart.MultipartFile;

public interface TaxParcelUploadService {

    public InspecieTransferDto validateFile(InspecieTransferDto transferDto, String pid, String sponsorName, MultipartFile file);
}