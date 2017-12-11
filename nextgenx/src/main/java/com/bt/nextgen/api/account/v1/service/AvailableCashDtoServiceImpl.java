package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.AvailableCashDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AvailableCash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("AvailableCashDtoServiceV1")
public class AvailableCashDtoServiceImpl implements AvailableCashDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Override
    public AvailableCashDto find(AccountKey key, ServiceErrors serviceErrors) {
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(key.getAccountId()));
        AvailableCash availableCash = accountService.loadAvailableCash(accountKey, serviceErrors);
        AvailableCashDto availableCashDto = convertToDto(availableCash);

        return availableCashDto;
    }

    protected AvailableCashDto convertToDto(AvailableCash availableCash) {
        if (availableCash != null) {
            BigDecimal pendingBuys = availableCash.getPendingBuys() == null ? BigDecimal.valueOf(0) : availableCash
                    .getPendingBuys().negate();
            return new AvailableCashDto(new AccountKey(EncodedString.fromPlainText(availableCash.getAccountKey().getId())
                    .toString()), availableCash.getAvailableCash(), availableCash.getPendingSells(), availableCash
                    .getQueuedBuys().negate(), pendingBuys);
        }

        return null;
    }
}
