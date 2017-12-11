package com.bt.nextgen.api.inspecietransfer.v2.service;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

/**
 * @deprecated Use V3
 */
@Deprecated
public interface InspecieTransferDtoService extends FindByKeyDtoService<InspecieTransferKey, InspecieTransferDto>,
        ValidateDtoService<InspecieTransferKey, InspecieTransferDto>, SubmitDtoService<InspecieTransferKey, InspecieTransferDto> {

}
