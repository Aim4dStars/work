package com.bt.nextgen.api.drawdown.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("DrawdownDtoServiceV2")
public class DrawdownDtoServiceImpl extends BaseDrawdownDetailsDtoService implements DrawdownDtoService {

    @Autowired
    private DrawdownStrategyIntegrationService drawdownStrategyService;

    @Override
    public DrawdownDetailsDto find(AccountKey key, ServiceErrors serviceErrors) {
        String accountId = EncodedString.toPlainText(key.getAccountId());

        DrawdownStrategy strategy = drawdownStrategyService.loadDrawdownStrategy(
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);

        if (DrawdownStrategy.ASSET_PRIORITY == strategy) {
            DrawdownStrategyDetails ddModel = drawdownStrategyService.loadDrawdownAssetPreferences(
                    com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);

            return convertToDto(ddModel, strategy, serviceErrors);
        }
        return new DrawdownDetailsDto(key, strategy == null ? null : strategy.getIntlId(), null);
    }

    @Override
    public DrawdownDetailsDto update(DrawdownDetailsDto drawdownDto, ServiceErrors serviceErrors) {
        DrawdownStrategyDetails model = drawdownStrategyService.submitDrawdownStrategy(convertoToStrategyModel(drawdownDto),
                serviceErrors);

        return convertToDto(model, model.getDrawdownStrategy(), serviceErrors);
    }
}
