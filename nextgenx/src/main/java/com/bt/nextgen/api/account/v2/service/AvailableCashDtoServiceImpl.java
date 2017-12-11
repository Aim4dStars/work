package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.AvailableCashDto;
import com.bt.nextgen.api.account.v2.model.ParameterisedAccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AvailableCash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Deprecated
@Service("AvailableCashDtoServiceV2")
public class AvailableCashDtoServiceImpl implements AvailableCashDtoService
{
    @Autowired
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Override
    public AvailableCashDto find(AccountKey key, ServiceErrors serviceErrors)
    {
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(key.getAccountId()));

        String accountServiceType = "";

        if (key instanceof ParameterisedAccountKey)
        {
            Map<String, String> parameters = ((ParameterisedAccountKey) key).getParameters();
            accountServiceType = parameters.get("serviceType");
        }

        AvailableCash availableCash = accountIntegrationServiceFactory.getInstance(accountServiceType).loadAvailableCash(accountKey, serviceErrors);
        AvailableCashDto availableCashDto = convertToDto(availableCash);

        return availableCashDto;
    }

    protected AvailableCashDto convertToDto(AvailableCash availableCash) {
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
        }

        return null;
    }
}