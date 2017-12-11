package com.bt.nextgen.api.account.v1.service.drawdown;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.account.v1.model.DrawdownDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.drawdown.AvaloqDrawdownIntegrationServiceImpl;
import com.bt.nextgen.service.integration.drawdown.Drawdown;
import com.bt.nextgen.service.integration.drawdown.DrawdownOption;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("DrawdownDtoServiceV1")
public class DrawdownDtoServiceImpl implements DrawdownDtoService {

    @Autowired
    private AvaloqDrawdownIntegrationServiceImpl drawdownService;

    @Override
    public DrawdownDto find(AccountKey key, ServiceErrors serviceErrors) {
        Drawdown drawdown = drawdownService.getDrawDownOption(
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId())),
                serviceErrors);
        return new DrawdownDto(new AccountKey(drawdown.getAccountKey().getId()), drawdown.getDrawdownOption() == null ? null
                : drawdown.getDrawdownOption().getIntlId());
    }

    @Override
    public DrawdownDto update(DrawdownDto drawdownDto, ServiceErrors serviceErrors) {
        drawdownService.updateDrawdownOption(com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString
                .toPlainText(drawdownDto.getKey().getAccountId())), DrawdownOption.forIntlId(drawdownDto.getDrawdownType()),
                serviceErrors);
        return drawdownDto;
    }
}
