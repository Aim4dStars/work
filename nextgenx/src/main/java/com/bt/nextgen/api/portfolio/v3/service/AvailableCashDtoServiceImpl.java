package com.bt.nextgen.api.portfolio.v3.service;


import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.portfolio.v3.model.AvailableCashDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AvailableCash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("AvailableCashDtoServiceV3")
public class AvailableCashDtoServiceImpl implements AvailableCashDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Override
    public AvailableCashDto find(AccountKey key, ServiceErrors serviceErrors) {
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(key.getAccountId()));
        AvailableCash availableCash = accountService.loadAvailableCash(accountKey, serviceErrors);
        AvailableCashDto availableCashDto = convertToDto(key, availableCash);

        return availableCashDto;
    }

    protected AvailableCashDto convertToDto(AccountKey key, AvailableCash availableCash) {
        if (availableCash != null) {
            BigDecimal pendingBuys = availableCash.getPendingBuys() == null ? BigDecimal.valueOf(0) : availableCash
                    .getPendingBuys().negate();
            AvailableCashDto availableCashDto = new AvailableCashDto(new AccountKey(EncodedString.fromPlainText(
                    availableCash.getAccountKey().getId()).toString()), availableCash.getAvailableCash(),
                    availableCash.getPendingSells(), availableCash.getQueuedBuys().negate(), pendingBuys,
                    availableCash.getPendingSellsListedSecurities() == null ? BigDecimal.valueOf(0) : availableCash
                            .getPendingSellsListedSecurities(),
                    availableCash.getQueuedBuysListedSecurities() == null ? BigDecimal.valueOf(0) : availableCash
                            .getQueuedBuysListedSecurities().negate());
            availableCashDto.setTotalPendingSells(availableCash.getTotalPendingSells());
            return availableCashDto;
        } else {
            return new AvailableCashDto(key);
        }
    }
}
