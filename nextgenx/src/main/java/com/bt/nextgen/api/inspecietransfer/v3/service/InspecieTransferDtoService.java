package com.bt.nextgen.api.inspecietransfer.v3.service;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferKey;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

public interface InspecieTransferDtoService extends ValidateDtoService<InspecieTransferKey, InspecieTransferDto>,
        SubmitDtoService<InspecieTransferKey, InspecieTransferDto> {

}
