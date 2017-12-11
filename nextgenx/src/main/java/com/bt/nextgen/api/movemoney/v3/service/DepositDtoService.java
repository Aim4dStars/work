package com.bt.nextgen.api.movemoney.v3.service;

import com.bt.nextgen.api.movemoney.v3.model.DepositDto;
import com.bt.nextgen.api.movemoney.v3.model.DepositKey;
import com.bt.nextgen.core.api.dto.CreateDtoService;
import com.bt.nextgen.core.api.dto.DeleteDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;

public interface DepositDtoService extends SearchByCriteriaDtoService<DepositDto>, ValidateDtoService<DepositKey, DepositDto>,
        SubmitDtoService<DepositKey, DepositDto>, CreateDtoService<DepositKey, DepositDto>,
        UpdateDtoService<DepositKey, DepositDto>, DeleteDtoService<DepositKey, DepositDto>,
        FindByKeyDtoService<DepositKey, DepositDto> {
}
