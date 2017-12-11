package com.bt.nextgen.api.regularinvestment.v2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.service.DepositDtoService;
import com.bt.nextgen.api.movemoney.v2.util.DepositUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentTransaction;

@Service("DepositDtoHelperV2")
public class DepositDtoHelper {

    @Autowired
    private DepositDtoService depositService;

    public DepositDto constructDepositDto(RegularInvestmentTransaction regularInvestment, RecurringDepositDetails directDebit,
            DepositDto depositDto) {
        DepositDto ddDto = null;
        if (directDebit != null) {
            ddDto = DepositUtil.toDepositDto(directDebit, depositDto);
            if (directDebit.getRecurringFrequency() != null)
                ddDto.setFrequency(directDebit.getRecurringFrequency().toString());

            ddDto.setKey(new com.bt.nextgen.api.account.v3.model.AccountKey(regularInvestment.getAccountKey()));

            PayeeDto fromPayDto = new PayeeDto();
            fromPayDto.setAccountId(regularInvestment.getPayerAccountId());
            fromPayDto.setAccountName(regularInvestment.getPayerAccountName());
            ddDto.setFromPayDto(fromPayDto);

            MoneyAccountIdentifier moneyAccountIdentifier = depositService.getMoneyAccountIdentifier(ddDto,
                    new ServiceErrorsImpl());

            PayeeDto toPayeeDto = new PayeeDto();
            toPayeeDto.setAccountId(moneyAccountIdentifier.getMoneyAccountId());
            ddDto.setToPayeeDto(toPayeeDto);
        }
        return ddDto;
    }
}
