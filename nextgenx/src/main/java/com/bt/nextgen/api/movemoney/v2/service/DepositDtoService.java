package com.bt.nextgen.api.movemoney.v2.service;

import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.core.api.dto.CreateDtoService;
import com.bt.nextgen.core.api.dto.SearchByKeyDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;

public interface DepositDtoService extends SearchByKeyDtoService<AccountKey, DepositDto>,
        ValidateDtoService<AccountKey, DepositDto>, SubmitDtoService<AccountKey, DepositDto>,
        CreateDtoService<AccountKey, DepositDto>, UpdateDtoService<AccountKey, DepositDto> {

    public List<DepositDto> loadPayeesForDeposits(AccountKey key);

    Map<String, DepositDto> loadDepositReceipts();

    public MoneyAccountIdentifier getMoneyAccountIdentifier(DepositDto keyedObject, ServiceErrors serviceErrors);
}
