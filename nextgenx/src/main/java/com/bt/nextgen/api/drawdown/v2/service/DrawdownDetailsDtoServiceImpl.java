package com.bt.nextgen.api.drawdown.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.drawdown.v2.model.DrawdownDetailsDto;
import com.bt.nextgen.api.drawdown.v2.validation.DrawdownErrorMapper;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategy;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyDetails;
import com.bt.nextgen.service.integration.drawdownstrategy.DrawdownStrategyIntegrationService;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DrawdownDetailsDtoServiceImpl extends BaseDrawdownDetailsDtoService implements DrawdownDetailsDtoService {

    @Autowired
    private DrawdownStrategyIntegrationService drawdownService;

    @Autowired
    @Qualifier("cacheAvaloqPortfolioIntegrationService")
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private DrawdownErrorMapper errorMapper;

    @Override
    public DrawdownDetailsDto find(AccountKey key, ServiceErrors serviceErrors) {
        DrawdownStrategyDetails ddDetails = drawdownService.loadDrawdownAssetPreferences(
                com.bt.nextgen.service.integration.account.AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId())),
                serviceErrors);

        return convertToDto(ddDetails, ddDetails.getDrawdownStrategy(), serviceErrors);
    }

    @Override
    public DrawdownDetailsDto submit(DrawdownDetailsDto drawdownDto, ServiceErrors serviceErrors) {

        ServiceErrorsImpl errorList = new ServiceErrorsImpl();
        DrawdownDetailsDto resultDto = updateAssetPreferences(drawdownDto, errorList);
        if (!resultDto.hasValidationError()) {
            // Update drawdown preference
            drawdownService.submitDrawdownStrategy(convertoToStrategyModel(drawdownDto), serviceErrors);
        }

        // Create a version of drawdownDto for the front-end.
        DrawdownDetailsDto uiDto = new DrawdownDetailsDto(drawdownDto.getKey(), drawdownDto.getDrawdownType(),
                resultDto.getPriorityDrawdownList());
        // Add back the warnings, if any.
        uiDto.setWarnings(resultDto.getWarnings());

        return uiDto;
    }

    protected DrawdownDetailsDto updateAssetPreferences(DrawdownDetailsDto drawdownDto, ServiceErrors serviceErrors) {

        DrawdownStrategyDetails resultModel = drawdownService.submitDrawdownAssetPreferences(
                convertoToAssetPriorityModel(drawdownDto), serviceErrors);
        return convertToDto(resultModel, DrawdownStrategy.forIntlId(drawdownDto.getDrawdownType()), serviceErrors);
    }

    @Override
    public DrawdownDetailsDto validate(DrawdownDetailsDto drawdownDto, ServiceErrors serviceErrors) {
        DrawdownStrategyDetails resultModel = drawdownService.validateDrawdownAssetPreferences(
                convertoToAssetPriorityModel(drawdownDto), serviceErrors);
        return convertToDto(resultModel, DrawdownStrategy.forIntlId(drawdownDto.getDrawdownType()), serviceErrors);
    }

}
